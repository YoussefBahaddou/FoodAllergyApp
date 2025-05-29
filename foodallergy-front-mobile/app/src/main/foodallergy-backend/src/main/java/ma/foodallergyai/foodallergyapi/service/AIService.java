package ma.foodallergyai.foodallergyapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    @Value("${huggingface.api.token:}")
    private String huggingFaceToken;

    @Value("${huggingface.api.url:https://api-inference.huggingface.co/models/microsoft/DialoGPT-medium}")
    private String huggingFaceUrl;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String userMessage, String context) {
        try {
            // If no Hugging Face token, use fallback responses
            if (huggingFaceToken == null || huggingFaceToken.isEmpty()) {
                return generateFallbackResponse(userMessage, context);
            }

            String enhancedPrompt = buildPrompt(userMessage, context);

            // Create request body
            String jsonBody = String.format(
                "{\"inputs\": \"%s\", \"parameters\": {\"max_length\": 100, \"temperature\": 0.7}}",
                enhancedPrompt.replace("\"", "\\\"")
            );

            RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(huggingFaceUrl)
                    .addHeader("Authorization", "Bearer " + huggingFaceToken)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseHuggingFaceResponse(responseBody);
                } else {
                    return generateFallbackResponse(userMessage, context);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return generateFallbackResponse(userMessage, context);
        }
    }

    private String buildPrompt(String userMessage, String context) {
        return String.format(
            "You are a helpful food allergy assistant. %s\n\nUser: %s\nAssistant:",
            context, userMessage
        );
    }

    private String parseHuggingFaceResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            if (jsonNode.isArray() && jsonNode.size() > 0) {
                JsonNode firstResult = jsonNode.get(0);
                if (firstResult.has("generated_text")) {
                    String generatedText = firstResult.get("generated_text").asText();
                    // Extract only the assistant's response
                    String[] parts = generatedText.split("Assistant:");
                    if (parts.length > 1) {
                        return parts[parts.length - 1].trim();
                    }
                    return generatedText.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "I'm sorry, I couldn't process your request right now. Please try again.";
    }

    private String generateFallbackResponse(String userMessage, String context) {
        String lowerMessage = userMessage.toLowerCase();

        // Food safety questions
        if (lowerMessage.contains("safe") || lowerMessage.contains("eat") || lowerMessage.contains("consume")) {
            if (context.contains("allergic to")) {
                return "Based on your allergy profile, I'd recommend checking the ingredient list carefully. Look for any allergens you're sensitive to. Would you like me to help you identify specific ingredients to avoid?";
            }
            return "To determine if a food is safe for you, I need to know about your specific allergies. Have you set up your allergy profile in the app?";
        }

        // Ingredient questions
        if (lowerMessage.contains("ingredient") || lowerMessage.contains("contain")) {
            return "I can help you understand ingredients! You can scan a product barcode or tell me about specific ingredients you're concerned about. What would you like to know?";
        }

        // Allergy management
        if (lowerMessage.contains("allergy") || lowerMessage.contains("allergic")) {
            return "I'm here to help with your food allergies! You can ask me about specific foods, ingredients to avoid, or how to read food labels. What specific allergy concerns do you have?";
        }

        // Scanning help
        if (lowerMessage.contains("scan") || lowerMessage.contains("barcode")) {
            return "You can use the scanner feature to check if products are safe for your allergies. Just tap the scanner tab and point your camera at a barcode, or enter the barcode manually.";
        }

        // General greeting
        if (lowerMessage.contains("hello") || lowerMessage.contains("hi") || lowerMessage.contains("help")) {
            return "Hello! I'm your food allergy assistant. I can help you with:\n\n• Checking if foods are safe for your allergies\n• Understanding ingredient labels\n• Managing your allergy profile\n• Scanning products for allergens\n\nWhat would you like to know?";
        }

        // Default response
        return "I'm here to help with food allergy questions! You can ask me about specific foods, ingredients, or how to use the app's features. What would you like to know?";
    }
}