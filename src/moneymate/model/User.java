package moneymate.model;

import java.time.LocalDateTime;

/**
 * Abstract class untuk User - dapat dikembangkan untuk multi-user version
 */
public abstract class User {
    
    protected String userId;
    protected String username;
    protected String email;
    protected LocalDateTime createdAt;
    
    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // Setters
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Abstract methods - untuk dikembangkan di future version
    public abstract void displayUserInfo();
    
    public abstract boolean validateCredentials(String password);
    
    @Override
    public String toString() {
        return String.format("User[ID=%s, Username=%s, Email=%s]", 
                           userId, username, email);
    }
}
