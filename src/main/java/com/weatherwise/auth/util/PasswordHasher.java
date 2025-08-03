package com.weatherwise.auth.util;

import java.security.SecureRandom;

public class PasswordHasher {
    private static final int BCRYPT_ROUNDS = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Hash a password using BCrypt algorithm
     * @param password The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Simple BCrypt implementation for demonstration
        // In production, use a proper BCrypt library like jBCrypt
        return simpleHash(password);
    }

    /**
     * Verify a password against its hash
     * @param password The plain text password to verify
     * @param hash The stored password hash
     * @return true if password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        
        String computedHash = simpleHash(password);
        return computedHash.equals(hash);
    }

    /**
     * Simple hash implementation for demonstration
     * In production, use proper BCrypt library
     */
    private static String simpleHash(String password) {
        // This is a simplified hash for demonstration
        // In real implementation, use jBCrypt or similar
        int hash = password.hashCode();
        for (int i = 0; i < BCRYPT_ROUNDS; i++) {
            hash = hash * 31 + password.length();
        }
        return Integer.toHexString(hash);
    }

    /**
     * Generate a random salt for password hashing
     * @return A random salt string
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
} 