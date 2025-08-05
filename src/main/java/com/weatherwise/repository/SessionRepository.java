package com.weatherwise.repository;

import com.weatherwise.auth.model.UserSession;
import com.weatherwise.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Repository for session data access operations.
 * Follows the Repository pattern for data access abstraction.
 */
public class SessionRepository {
    private static final Logger LOGGER = Logger.getLogger(SessionRepository.class.getName());
    private final DatabaseConfig databaseConfig;
    
    public SessionRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Save or update a session
     * @param session The session to save
     * @return true if successful, false otherwise
     */
    public boolean save(UserSession session) {
        String sql = "INSERT OR REPLACE INTO sessions (session_id, user_id, username, created_at, last_activity, is_valid) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, session.getSessionId());
            pstmt.setInt(2, session.getUserId());
            pstmt.setString(3, session.getUsername());
            pstmt.setString(4, session.getCreatedAt().toString());
            pstmt.setString(5, session.getLastActivity().toString());
            pstmt.setBoolean(6, session.isValid());
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving session", e);
            return false;
        }
    }
    
    /**
     * Find session by session ID
     * @param sessionId The session ID to search for
     * @return Optional containing the session if found
     */
    public Optional<UserSession> findBySessionId(String sessionId) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sessionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createSessionFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding session by ID", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all sessions for a user
     * @param userId The user ID
     * @return List of sessions for the user
     */
    public List<UserSession> findByUserId(int userId) {
        String sql = "SELECT * FROM sessions WHERE user_id = ?";
        List<UserSession> sessions = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(createSessionFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding sessions by user ID", e);
        }
        
        return sessions;
    }
    
    /**
     * Find all valid sessions
     * @return List of valid sessions
     */
    public List<UserSession> findAllValid() {
        String sql = "SELECT * FROM sessions WHERE is_valid = 1";
        List<UserSession> sessions = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                sessions.add(createSessionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all valid sessions", e);
        }
        
        return sessions;
    }
    
    /**
     * Invalidate a session
     * @param sessionId The session ID to invalidate
     * @return true if successful, false otherwise
     */
    public boolean invalidateSession(String sessionId) {
        String sql = "UPDATE sessions SET is_valid = 0 WHERE session_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sessionId);
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error invalidating session", e);
            return false;
        }
    }
    
    /**
     * Invalidate all sessions for a user
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean invalidateAllSessionsForUser(int userId) {
        String sql = "UPDATE sessions SET is_valid = 0 WHERE user_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error invalidating all sessions for user", e);
            return false;
        }
    }
    
    /**
     * Update session's last activity
     * @param sessionId The session ID
     * @param lastActivity The new last activity time
     * @return true if successful, false otherwise
     */
    public boolean updateLastActivity(String sessionId, java.time.LocalDateTime lastActivity) {
        String sql = "UPDATE sessions SET last_activity = ? WHERE session_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lastActivity.toString());
            pstmt.setString(2, sessionId);
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating session last activity", e);
            return false;
        }
    }
    
    /**
     * Delete expired sessions
     * @param expirationTime The expiration time threshold
     * @return Number of sessions deleted
     */
    public int deleteExpiredSessions(java.time.LocalDateTime expirationTime) {
        String sql = "DELETE FROM sessions WHERE last_activity < ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, expirationTime.toString());
            
            int affected = pstmt.executeUpdate();
            LOGGER.info("Deleted " + affected + " expired sessions");
            return affected;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting expired sessions", e);
            return 0;
        }
    }
    
    /**
     * Check if session exists and is valid
     * @param sessionId The session ID to check
     * @return true if session exists and is valid, false otherwise
     */
    public boolean isValidSession(String sessionId) {
        String sql = "SELECT COUNT(*) FROM sessions WHERE session_id = ? AND is_valid = 1";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sessionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if session is valid", e);
        }
        
        return false;
    }
    
    private UserSession createSessionFromResultSet(ResultSet rs) throws SQLException {
        return new UserSession(
            rs.getString("session_id"),
            rs.getInt("user_id"),
            rs.getString("username"),
            LocalDateTime.parse(rs.getString("created_at")),
            LocalDateTime.parse(rs.getString("last_activity")),
            rs.getBoolean("is_valid")
        );
    }
} 