package ma.emsi.foodallergyapp.model;

public class ChatRequest {
    private String userId;
    private String message;
    private String conversationId;

    public ChatRequest() {}

    public ChatRequest(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public ChatRequest(String userId, String message, String conversationId) {
        this.userId = userId;
        this.message = message;
        this.conversationId = conversationId;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
}