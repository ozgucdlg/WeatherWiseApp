package com.weatherwise.auth.service;

import com.weatherwise.auth.model.User;
import com.weatherwise.auth.model.UserSession;
import com.weatherwise.auth.util.PasswordHasher;
import com.weatherwise.auth.util.ValidationUtil;
import com.weatherwise.repository.UserRepository;
import com.weatherwise.repository.SessionRepository;
import com.weatherwise.service.DatabaseInitializationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Authentication service that handles user authentication and session management.
 * Follows the Service pattern and uses Repository pattern for data access.
 */
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private static final long SESSION_TIMEOUT_MINUTES = 30;
    
    private final Map<String, UserSession> activeSessions = new HashMap<>();
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final DatabaseInitializationService databaseInitService;
    private static AuthService instance;
    
    private AuthService() {
        this.userRepository = new UserRepository();
        this.sessionRepository = new SessionRepository();
        this.databaseInitService = new DatabaseInitializationService();
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
        if (!databaseInitService.initializeDatabase()) {
            LOGGER.severe("Failed to initialize database");
            throw new RuntimeException("Database initialization failed");
        }
    }

    private boolean saveUser(User user) {
        return userRepository.save(user);
    }

    private User findUser(String usernameOrEmail) {
        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail);
        return user.orElse(null);
    }

    private User findUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    private boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private void updateUserLastLogin(User user) {
        userRepository.updateLastLogin(user.getId(), user.getLastLoginAt());
    }

    private void saveSession(UserSession session) {
        sessionRepository.save(session);
    }

    private void invalidateSession(String sessionId) {
        sessionRepository.invalidateSession(sessionId);
    }
} 