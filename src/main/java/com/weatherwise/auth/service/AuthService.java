package com.weatherwise.auth.service;

import com.weatherwise.auth.model.User;
import com.weatherwise.auth.model.UserSession;
import com.weatherwise.auth.util.PasswordHasher;
import com.weatherwise.auth.util.ValidationUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private static final String DB_URL = "jdbc:sqlite:weatherwise_auth.db";
    private static final long SESSION_TIMEOUT_MINUTES = 30;
    
    private final Map<String, UserSession> activeSessions = new HashMap<>();
    private static AuthService instance;
    
    private AuthService() {
        initializeDatabase();
    }
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Register a new user
     * @param username The username
     * @param email The email address
     * @param password The password
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(String username, String email, String password) {
        try {
            // Validate inputs
            if (!ValidationUtil.isValidUsername(username) || 
                !ValidationUtil.isValidEmail(email) || 
                !ValidationUtil.isValidPassword(password)) {
                LOGGER.warning("Invalid input data for user registration");
                return false;
            }
            
            // Sanitize inputs
            username = ValidationUtil.sanitizeInput(username);
            email = ValidationUtil.sanitizeInput(email);
            
            // Check if user already exists
            if (userExists(username) || emailExists(email)) {
                LOGGER.warning("User or email already exists: " + username);
                return false;
            }
            
            // Hash password
            String passwordHash = PasswordHasher.hashPassword(password);
            
            // Create user
            User user = new User(username, email, passwordHash);
            
            // Save to database
            return saveUser(user);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during user registration", e);
            return false;
        }
    }

    /**
     * Authenticate a user
     * @param username The username or email
     * @param password The password
     * @return UserSession if authentication successful, null otherwise
     */
    public UserSession login(String username, String password) {
        try {
            // Sanitize input
            username = ValidationUtil.sanitizeInput(username);
            
            // Find user by username or email
            User user = findUser(username);
            if (user == null) {
                LOGGER.warning("User not found: " + username);
                return null;
            }
            
            // Verify password
            if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                LOGGER.warning("Invalid password for user: " + username);
                return null;
            }
            
            // Check if user is active
            if (!user.isActive()) {
                LOGGER.warning("Inactive user attempted login: " + username);
                return null;
            }
            
            // Create session
            UserSession session = new UserSession(user.getId(), user.getUsername());
            
            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            updateUserLastLogin(user);
            
            // Store session
            activeSessions.put(session.getSessionId(), session);
            saveSession(session);
            
            LOGGER.info("User logged in successfully: " + username);
            return session;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            return null;
        }
    }

    /**
     * Logout a user
     * @param sessionId The session ID
     * @return true if logout successful, false otherwise
     */
    public boolean logout(String sessionId) {
        try {
            UserSession session = activeSessions.get(sessionId);
            if (session != null) {
                session.setValid(false);
                activeSessions.remove(sessionId);
                invalidateSession(sessionId);
                LOGGER.info("User logged out: " + session.getUsername());
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
            return false;
        }
    }

    /**
     * Validate a session
     * @param sessionId The session ID
     * @return UserSession if valid, null otherwise
     */
    public UserSession validateSession(String sessionId) {
        try {
            UserSession session = activeSessions.get(sessionId);
            if (session == null || !session.isValid()) {
                return null;
            }
            
            // Check if session is expired
            if (session.isExpired(SESSION_TIMEOUT_MINUTES)) {
                logout(sessionId);
                return null;
            }
            
            // Update last activity
            session.updateActivity();
            return session;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating session", e);
            return null;
        }
    }

    /**
     * Get current user from session
     * @param sessionId The session ID
     * @return User if session valid, null otherwise
     */
    public User getCurrentUser(String sessionId) {
        UserSession session = validateSession(sessionId);
        if (session != null) {
            return findUserById(session.getUserId());
        }
        return null;
    }

    // Database operations
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Create users table
            String createUsersTable = 
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "created_at TEXT NOT NULL," +
                "last_login_at TEXT NOT NULL," +
                "is_active BOOLEAN DEFAULT 1" +
                ")";
            
            // Create sessions table
            String createSessionsTable = 
                "CREATE TABLE IF NOT EXISTS sessions (" +
                "session_id TEXT PRIMARY KEY," +
                "user_id INTEGER NOT NULL," +
                "username TEXT NOT NULL," +
                "created_at TEXT NOT NULL," +
                "last_activity TEXT NOT NULL," +
                "is_valid BOOLEAN DEFAULT 1," +
                "FOREIGN KEY (user_id) REFERENCES users (id)" +
                ")";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createUsersTable);
                stmt.execute(createSessionsTable);
            }
            
            LOGGER.info("Database initialized successfully");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
        }
    }

    private boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, created_at, last_login_at, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    private User findUser(String usernameOrEmail) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user", e);
        }
        
        return null;
    }

    private User findUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID", e);
        }
        
        return null;
    }

    private boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists", e);
        }
        
        return false;
    }

    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    private void updateUserLastLogin(User user) {
        String sql = "UPDATE users SET last_login_at = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getLastLoginAt().toString());
            pstmt.setInt(2, user.getId());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user last login", e);
        }
    }

    private void saveSession(UserSession session) {
        String sql = "INSERT OR REPLACE INTO sessions (session_id, user_id, username, created_at, last_activity, is_valid) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, session.getSessionId());
            pstmt.setInt(2, session.getUserId());
            pstmt.setString(3, session.getUsername());
            pstmt.setString(4, session.getCreatedAt().toString());
            pstmt.setString(5, session.getLastActivity().toString());
            pstmt.setBoolean(6, session.isValid());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving session", e);
        }
    }

    private void invalidateSession(String sessionId) {
        String sql = "UPDATE sessions SET is_valid = 0 WHERE session_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sessionId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error invalidating session", e);
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