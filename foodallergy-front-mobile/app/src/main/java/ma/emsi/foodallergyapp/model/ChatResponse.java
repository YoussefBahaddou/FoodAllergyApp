package ma.emsi.foodallergyapp.model;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("response")
    private String response;

    @SerializedName("message")
    private String message;

    @SerializedName("conversation_id")
    private String conversationId;

    @SerializedName("success")
    private boolean success;

    @SerializedName("error")
    private String error;

    public ChatResponse() {}

    // Getters and Setters
    public String getResponse() {
        // Try both 'response' and 'message' fields
        return response != null ? response : message;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}