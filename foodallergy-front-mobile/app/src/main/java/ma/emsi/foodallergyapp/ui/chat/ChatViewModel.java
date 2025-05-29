package ma.emsi.foodallergyapp.ui.chat;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ma.emsi.foodallergyapp.api.GeminiChatService;
import ma.emsi.foodallergyapp.api.SupabaseChatService;
import ma.emsi.foodallergyapp.model.ChatMessage;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    private List<ChatMessage> messageList = new ArrayList<>();
    private GeminiChatService geminiService;
    private Handler mainHandler;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        geminiService = new GeminiChatService();
        mainHandler = new Handler(Looper.getMainLooper());
        messages.setValue(messageList);
        isLoading.setValue(false);

        // Add welcome message
        addWelcomeMessage();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private void addWelcomeMessage() {
        ChatMessage welcomeMessage = new ChatMessage(
            "Hello! I'm your Food Allergy Assistant powered by Google Gemini AI. I can help you with:\n\n" +
            "• Information about food allergens\n" +
            "• Reading ingredient lists\n" +
            "• Safe food alternatives\n" +
            "• Allergy management tips\n" +
            "• Cross-contamination prevention\n\n" +
            "How can I help you today?",
            ChatMessage.TYPE_BOT
        );
        messageList.add(welcomeMessage);
        messages.setValue(new ArrayList<>(messageList));
    }

    public void sendMessage(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return;
        }

        // Add user message
        ChatMessage userMessage = new ChatMessage(messageText.trim(), ChatMessage.TYPE_USER);
        messageList.add(userMessage);
        messages.setValue(new ArrayList<>(messageList));

        // Show loading
        isLoading.setValue(true);

        // Add typing indicator
        ChatMessage typingMessage = new ChatMessage("Thinking...", ChatMessage.TYPE_BOT);
        typingMessage.setTyping(true);
        messageList.add(typingMessage);
        messages.setValue(new ArrayList<>(messageList));

        // Send to Gemini AI
        geminiService.sendMessage(messageText.trim(), new GeminiChatService.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                // Ensure we're on the main thread
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Remove typing indicator
                        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).isTyping()) {
                            messageList.remove(messageList.size() - 1);
                        }

                        // Add bot response
                        ChatMessage botMessage = new ChatMessage(response, ChatMessage.TYPE_BOT);
                        messageList.add(botMessage);

                        isLoading.setValue(false);
                        messages.setValue(new ArrayList<>(messageList));
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Ensure we're on the main thread
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Remove typing indicator
                        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).isTyping()) {
                            messageList.remove(messageList.size() - 1);
                        }

                        // Add fallback response
                        String fallbackResponse = getFallbackResponse(messageText);
                        ChatMessage botMessage = new ChatMessage(fallbackResponse, ChatMessage.TYPE_BOT);
                        messageList.add(botMessage);

                        isLoading.setValue(false);
                        error.setValue(errorMessage);
                        messages.setValue(new ArrayList<>(messageList));
                    }
                });
            }
        });
    }

    private String getFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("allergen") || lowerMessage.contains("allergy")) {
            return "I'm here to help with food allergies! The 8 major allergens are:\n\n" +
                   "1. Milk\n2. Eggs\n3. Peanuts\n4. Tree nuts\n5. Fish\n6. Shellfish\n7. Wheat\n8. Soy\n\n" +
                   "What specific allergen would you like to know about?";
        } else if (lowerMessage.contains("ingredient")) {
            return "When reading ingredient lists:\n\n" +
                   "• Look for allergen warnings like 'Contains:' or 'May contain:'\n" +
                   "• Check for alternative names (e.g., casein for milk)\n" +
                   "• Be aware of cross-contamination warnings\n" +
                   "• When in doubt, contact the manufacturer\n\n" +
                   "What ingredient are you concerned about?";
        } else if (lowerMessage.contains("safe") || lowerMessage.contains("eat")) {
            return "Food safety tips for allergies:\n\n" +
                   "• Always read labels carefully\n" +
                   "• Check for cross-contamination warnings\n" +
                   "• Ask about ingredients when dining out\n" +
                   "• Carry emergency medication if prescribed\n" +
                   "• When in doubt, don't eat it\n\n" +
                   "What food are you checking?";
        } else if (lowerMessage.contains("peanut") || lowerMessage.contains("nut")) {
            return "Peanut and tree nut allergies:\n\n" +
                   "• Peanuts are legumes, not tree nuts\n" +
                   "• Tree nuts include almonds, walnuts, cashews, etc.\n" +
                   "• Watch for cross-contamination in facilities\n" +
                   "• Check labels for 'may contain nuts'\n" +
                   "• Be cautious with ethnic cuisines that commonly use nuts";
        } else if (lowerMessage.contains("milk") || lowerMessage.contains("dairy")) {
            return "Milk allergy information:\n\n" +
                   "• Avoid all dairy products\n" +
                   "• Watch for hidden dairy: casein, whey, lactose\n" +
                   "• Check medications and supplements\n" +
                   "• Look for dairy-free alternatives\n" +
                   "• Be aware that 'non-dairy' doesn't mean milk-free";
        } else if (lowerMessage.contains("gluten") || lowerMessage.contains("wheat")) {
            return "Gluten and wheat information:\n\n" +
                   "• Gluten is in wheat, barley, rye, and some oats\n" +
                   "• Look for certified gluten-free labels\n" +
                   "• Be aware of cross-contamination\n" +
                   "• Check sauces, seasonings, and processed foods\n" +
                   "• Wheat allergy is different from celiac disease";
        } else {
            return "I'm sorry, I'm having trouble connecting to my AI service right now. I'm here to help with food allergy questions, ingredient information, and safe eating tips.\n\n" +
                   "Try asking about:\n" +
                   "• Specific allergens (milk, eggs, nuts, etc.)\n" +
                   "• Reading ingredient labels\n" +
                   "• Safe food alternatives\n" +
                   "• Cross-contamination prevention";
        }
    }

    public void clearMessages() {
        messageList.clear();
        addWelcomeMessage();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (geminiService != null) {
            geminiService.shutdown();
        }
    }
}