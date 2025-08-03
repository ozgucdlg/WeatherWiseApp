package com.weatherwise.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSession {
    private String sessionId;
    private int userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private boolean isValid;

    public UserSession(int userId, String username) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.username = username;
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.isValid = true;
    }

    // Constructor for database retrieval
    public UserSession(String sessionId, int userId, String username, 
                      LocalDateTime createdAt, LocalDateTime lastActivity, boolean isValid) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
        this.lastActivity = lastActivity;
        this.isValid = isValid;
    }

    // Getters
    public String getSessionId() { return sessionId; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastActivity() { return lastActivity; }
    public boolean isValid() { return isValid; }

    // Setters
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    public void setValid(boolean valid) { isValid = valid; }

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    public boolean isExpired(long maxSessionDurationMinutes) {
        return LocalDateTime.now().isAfter(lastActivity.plusMinutes(maxSessionDurationMinutes));
    }

    @Override
    public String toString() {
        return String.format("UserSession{sessionId='%s', userId=%d, username='%s', valid=%s}", 
                           sessionId, userId, username, isValid);
    }
} 