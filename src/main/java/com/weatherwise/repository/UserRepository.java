package com.weatherwise.repository;

import com.weatherwise.auth.model.User;
import com.weatherwise.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Repository for user data access operations.
 * Follows the Repository pattern for data access abstraction.
 */
public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private final DatabaseConfig databaseConfig;
    
    public UserRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Save a new user to the database
     * @param user The user to save
     * @return true if successful, false otherwise
     */
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, created_at, last_login_at, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getCreatedAt().toString());
            pstmt.setString(5, user.getLastLoginAt().toString());
            pstmt.setBoolean(6, user.isActive());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user", e);
            return false;
        }
    }
    
    /**
     * Find user by username or email
     * @param usernameOrEmail The username or email to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createUserFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username or email", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Find user by ID
     * @param userId The user ID to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createUserFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Check if username exists
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if username exists", e);
        }
        
        return false;
    }
    
    /**
     * Check if email exists
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email exists", e);
        }
        
        return false;
    }
    
    /**
     * Update user's last login time
     * @param userId The user ID
     * @param lastLoginAt The new last login time
     * @return true if successful, false otherwise
     */
    public boolean updateLastLogin(int userId, LocalDateTime lastLoginAt) {
        String sql = "UPDATE users SET last_login_at = ? WHERE id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lastLoginAt.toString());
            pstmt.setInt(2, userId);
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user last login", e);
            return false;
        }
    }
    
    /**
     * Find all active users
     * @return List of active users
     */
    public List<User> findAllActive() {
        String sql = "SELECT * FROM users WHERE is_active = true";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all active users", e);
        }
        
        return users;
    }
    
    /**
     * Update user's active status
     * @param userId The user ID
     * @param isActive The new active status
     * @return true if successful, false otherwise
     */
    public boolean updateActiveStatus(int userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, userId);
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user active status", e);
            return false;
        }
    }
    
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password_hash"),
            LocalDateTime.parse(rs.getString("created_at")),
            LocalDateTime.parse(rs.getString("last_login_at")),
            rs.getBoolean("is_active")
        );
    }
} 