package ma.emsi.foodallergyapp.model;

import java.util.Date;

public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_BOT = 2;

    private String id;
    private String message;
    private int type;
    private Date timestamp;
    private boolean isTyping;

    public ChatMessage() {
        this.timestamp = new Date();
    }

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
        this.timestamp = new Date();
        this.isTyping = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isTyping() { return isTyping; }
    public void setTyping(boolean typing) { isTyping = typing; }

    // Legacy methods for compatibility
    public String getMessageText() { return message; }
    public void setMessageText(String messageText) { this.message = messageText; }

    public boolean isUserMessage() { return type == TYPE_USER; }
    public void setUserMessage(boolean userMessage) {
        this.type = userMessage ? TYPE_USER : TYPE_BOT;
    }
}