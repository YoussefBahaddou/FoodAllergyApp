package ma.emsi.foodallergyapp.model;

import java.util.UUID;

public class ChatRequest {
    private UUID userId;
    private String message;
    private UUID conversationId;

    public ChatRequest(UUID userId, String message, UUID conversationId) {
        this.userId = userId;
        this.message = message;
        this.conversationId = conversationId;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
}