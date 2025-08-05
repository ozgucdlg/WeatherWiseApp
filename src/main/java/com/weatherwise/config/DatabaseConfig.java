package com.weatherwise.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Database configuration and connection management.
 * Follows the Singleton pattern for connection management.
 */
public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static DatabaseConfig instance;
    
    private DatabaseConfig() {
        initializeDataSource();
    }
    
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private void initializeDataSource() {
        try {
            // Load SQLite driver
            Class.forName(AppConfig.DATABASE_DRIVER);
            LOGGER.info("SQLite database connection initialized successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database connection", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DATABASE_URL);
    }
    
    public void close() {
        LOGGER.info("Database connection closed");
    }
    
    public boolean isHealthy() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
} 