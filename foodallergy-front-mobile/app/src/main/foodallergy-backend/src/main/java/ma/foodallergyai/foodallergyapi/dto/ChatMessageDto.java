package ma.foodallergyai.foodallergyapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessageDto {
    private UUID id;
    private UUID conversationId;
    private String messageText;
    private Boolean isUserMessage;
    private String messageType;
    private LocalDateTime createdAt;

    // Constructors
    public ChatMessageDto() {}

    public ChatMessageDto(UUID id, UUID conversationId, String messageText, Boolean isUserMessage, String messageType, LocalDateTime createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.messageText = messageText;
        this.isUserMessage = isUserMessage;
        this.messageType = messageType;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public Boolean getIsUserMessage() { return isUserMessage; }
    public void setIsUserMessage(Boolean isUserMessage) { this.isUserMessage = isUserMessage; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
