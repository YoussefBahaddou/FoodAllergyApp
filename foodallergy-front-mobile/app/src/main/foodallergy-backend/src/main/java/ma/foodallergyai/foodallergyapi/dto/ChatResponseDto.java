package ma.foodallergyai.foodallergyapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatResponseDto {
    private UUID conversationId;
    private String botResponse;
    private String messageType;
    private LocalDateTime timestamp;
    private boolean success;
    private String error;

    // Constructors
    public ChatResponseDto() {}

    public ChatResponseDto(UUID conversationId, String botResponse) {
        this.conversationId = conversationId;
        this.botResponse = botResponse;
        this.messageType = "text";
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }

    // Getters and Setters
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }

    public String getBotResponse() { return botResponse; }
    public void setBotResponse(String botResponse) { this.botResponse = botResponse; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
