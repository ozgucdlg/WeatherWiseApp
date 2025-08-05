package com.weatherwise.service;

import com.weatherwise.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for database initialization and schema management.
 * Follows the Service pattern for business logic separation.
 */
public class DatabaseInitializationService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializationService.class.getName());
    private final DatabaseConfig databaseConfig;
    
    public DatabaseInitializationService() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Initialize the database schema
     * @return true if successful, false otherwise
     */
    public boolean initializeDatabase() {
        try (Connection conn = databaseConfig.getConnection()) {
            createUsersTable(conn);
            createSessionsTable(conn);
            createIndexes(conn);
            
            LOGGER.info("Database schema initialized successfully");
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database schema", e);
            return false;
        }
    }
    
    private void createUsersTable(Connection conn) throws Exception {
        String createUsersTable = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT UNIQUE NOT NULL," +
            "email TEXT UNIQUE NOT NULL," +
            "password_hash TEXT NOT NULL," +
            "created_at TEXT NOT NULL," +
            "last_login_at TEXT NOT NULL," +
            "is_active INTEGER DEFAULT 1" +
            ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            LOGGER.info("Users table created/verified successfully");
        }
    }
    
    private void createSessionsTable(Connection conn) throws Exception {
        String createSessionsTable = 
            "CREATE TABLE IF NOT EXISTS sessions (" +
            "session_id TEXT PRIMARY KEY," +
            "user_id INTEGER NOT NULL," +
            "username TEXT NOT NULL," +
            "created_at TEXT NOT NULL," +
            "last_activity TEXT NOT NULL," +
            "is_valid INTEGER DEFAULT 1," +
            "FOREIGN KEY (user_id) REFERENCES users (id)" +
            ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSessionsTable);
            LOGGER.info("Sessions table created/verified successfully");
        }
    }
    
    private void createIndexes(Connection conn) throws Exception {
        String[] indexQueries = {
            "CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)",
            "CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)",
            "CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id)",
            "CREATE INDEX IF NOT EXISTS idx_sessions_valid ON sessions(is_valid)",
            "CREATE INDEX IF NOT EXISTS idx_sessions_last_activity ON sessions(last_activity)"
        };
        
        try (Statement stmt = conn.createStatement()) {
            for (String query : indexQueries) {
                stmt.execute(query);
            }
            LOGGER.info("Database indexes created/verified successfully");
        }
    }
    
    /**
     * Check if database is properly initialized
     * @return true if initialized, false otherwise
     */
    public boolean isDatabaseInitialized() {
        try (Connection conn = databaseConfig.getConnection()) {
            // Check if users table exists
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT 1 FROM users LIMIT 1");
                stmt.executeQuery("SELECT 1 FROM sessions LIMIT 1");
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database not properly initialized", e);
            return false;
        }
    }
    
    /**
     * Get database health status
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return databaseConfig.isHealthy() && isDatabaseInitialized();
    }
} 