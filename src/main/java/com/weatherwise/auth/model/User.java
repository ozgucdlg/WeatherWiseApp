package com.weatherwise.auth.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean isActive;

    // Constructor
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Constructor for database retrieval
    public User(int id, String username, String email, String passwordHash, 
                LocalDateTime createdAt, LocalDateTime lastLoginAt, boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.isActive = isActive;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setActive(boolean active) { isActive = active; }
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', active=%s}", 
                           id, username, email, isActive);
    }
} 