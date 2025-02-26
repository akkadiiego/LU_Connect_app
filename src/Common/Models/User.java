package Common.Models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String username;
    private String password;
    private boolean isOnline;

    // Constructor
    public User(String username, String password, boolean isOnline) {
        this.username = username;
        this.password = password;
        this.isOnline = isOnline;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return password; }
    public void setPasswordHash(String password) { this.password = password; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }


    @Override
    public String toString() {
        return username + " (" + (isOnline ? "Online" : "Offline") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return isOnline == user.isOnline &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, isOnline);
    }
}

