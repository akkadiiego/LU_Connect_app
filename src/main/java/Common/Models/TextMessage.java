package Common.Models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TextMessage extends Message implements Serializable{
    private String content;


    // Constructor
    public TextMessage(User sender, User receiver, String content, LocalDateTime timestamp) {
        super(sender, receiver, timestamp);
        this.content = content;
    }

    // Getters and Setters

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }



    @Override
    public String toString() {
        return "[" + getTimestamp() + "] " + getSender() + " → " + getReceiver() + ": " + content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextMessage)) return false;
        TextMessage textMessage = (TextMessage) o;
        return Objects.equals(getSender(), textMessage.getSender()) &&
                Objects.equals(getReceiver(), textMessage.getReceiver()) &&
                Objects.equals(content, textMessage.content) &&
                Objects.equals(getTimestamp(), textMessage.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender(), getReceiver(), content, getTimestamp());
    }
}

