package Common.Models;

import java.time.LocalDateTime;

public class Message {
    private User sender;
    private User receiver;
    private LocalDateTime timestamp;
    protected String filename;
    protected int fileSize;
    protected byte[] data;
    protected String content;

    public Message(User sender, User receiver,LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getContent() { return content; }
    public void setContent(String content) { content = content; }
}
