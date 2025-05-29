package ma.emsi.foodallergyapp.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import ma.emsi.foodallergyapp.config.GeminiConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeminiChatService {
    private static final String TAG = "GeminiChatService";
    // Updated model name - Google changed from "gemini-pro" to "gemini-1.5-flash"
    private static final String MODEL_NAME = "gemini-1.5-flash";

    private GenerativeModelFutures model;
    private ExecutorService executor;
    private Handler mainHandler;

    public GeminiChatService() {
        try {
            // Check if API key is valid
            if (GeminiConfig.GEMINI_API_KEY == null ||
                GeminiConfig.GEMINI_API_KEY.equals("YOUR_ACTUAL_API_KEY_HERE") ||
                GeminiConfig.GEMINI_API_KEY.equals("AIzaSyC_YOUR_ACTUAL_API_KEY_HERE") ||
                GeminiConfig.GEMINI_API_KEY.length() < 30) {
                Log.e(TAG, "Invalid Gemini API key. Please set a valid API key in GeminiConfig.java");
                return;
            }

            GenerativeModel gm = new GenerativeModel(MODEL_NAME, GeminiConfig.GEMINI_API_KEY);
            model = GenerativeModelFutures.from(gm);
            executor = Executors.newSingleThreadExecutor();
            mainHandler = new Handler(Looper.getMainLooper());

            Log.d(TAG, "Gemini service initialized successfully with model: " + MODEL_NAME);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Gemini service", e);
        }
    }

    public interface ChatCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void sendMessage(String message, ChatCallback callback) {
        if (model == null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError("❌ Gemini service not initialized.\n\nPlease check:\n• Your API key is valid\n• You have internet connection\n• The API key is correctly set in GeminiConfig.java");
                }
            });
            return;
        }

        // Run in background thread
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Sending message to Gemini: " + message);

                    // Create the prompt with food allergy context
                    String prompt = createFoodAllergyPrompt(message);

                    Content content = new Content.Builder()
                            .addText(prompt)
                            .build();

                    // Make synchronous call in background thread
                    GenerateContentResponse response = model.generateContent(content).get();

                    String responseText = response.getText();
                    Log.d(TAG, "Received response from Gemini: " + responseText);

                    // Post result to main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (responseText != null && !responseText.trim().isEmpty()) {
                                callback.onSuccess(responseText.trim());
                            } else {
                                callback.onError("Empty response from Gemini AI");
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error calling Gemini API", e);

                    // Post error to main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String errorMessage = getDetailedErrorMessage(e);
                            callback.onError(errorMessage);
                        }
                    });
                }
            }
        });
    }

    private String getDetailedErrorMessage(Exception e) {
        String message = e.getMessage();

        if (message == null) {
            return "❌ Unknown error occurred while contacting Gemini AI";
        }

        if (message.contains("not found") || message.contains("404")) {
            return "❌ Model Not Found\n\nThe Gemini model is not available. This might be due to:\n• API version changes\n• Regional restrictions\n• Service updates\n\nTrying fallback response...";
        } else if (message.contains("API_KEY") || message.contains("401") || message.contains("Unauthorized")) {
            return "❌ Invalid API Key\n\nPlease:\n1. Get a valid API key from Google AI Studio\n2. Update GeminiConfig.java with your key\n3. Rebuild the app";
        } else if (message.contains("403") || message.contains("Forbidden")) {
            return "❌ API Access Denied\n\nYour API key might be:\n• Expired\n• Over quota\n• Restricted";
        } else if (message.contains("429") || message.contains("quota")) {
            return "❌ Rate Limit Exceeded\n\nToo many requests. Please wait a moment and try again.";
        } else if (message.contains("network") || message.contains("timeout") || message.contains("connection")) {
            return "❌ Network Error\n\nPlease check your internet connection and try again.";
        } else if (message.contains("SAFETY")) {
            return "❌ Content Safety Filter\n\nYour message was blocked by safety filters. Please rephrase your question.";
        } else {
            return "❌ Gemini AI Error\n\n" + message + "\n\nPlease try again or check your API configuration.";
        }
    }

    private String createFoodAllergyPrompt(String userMessage) {
        return "You are a helpful Food Allergy Assistant. Your role is to provide accurate, safe, and helpful information about food allergies, ingredients, and food safety. " +
                "Always prioritize safety and recommend consulting healthcare professionals for serious medical concerns. " +
                "Keep responses concise but informative (under 300 words). " +
                "If asked about non-food-allergy topics, politely redirect to food allergy related questions. " +
                "Focus on the 8 major allergens: milk, eggs, peanuts, tree nuts, fish, shellfish, wheat, and soy.\n\n" +
                "User question: " + userMessage + "\n\n" +
                "Please provide a helpful response about food allergies, ingredients, or food safety:";
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
