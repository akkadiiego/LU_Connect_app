package Common.Models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private LocalDateTime timestamp;

    public Message(String sender, String receiver, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
