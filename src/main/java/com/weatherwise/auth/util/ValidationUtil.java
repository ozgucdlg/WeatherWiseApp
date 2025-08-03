package com.weatherwise.auth.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * Validate email format
     * @param email The email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate username format
     * @param username The username to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validate password strength
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        String trimmedPassword = password.trim();
        
        // Check length
        if (trimmedPassword.length() < MIN_PASSWORD_LENGTH || 
            trimmedPassword.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }
        
        // Check for at least one letter and one number
        boolean hasLetter = false;
        boolean hasNumber = false;
        
        for (char c : trimmedPassword.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }
        
        return hasLetter && hasNumber;
    }

    /**
     * Validate that passwords match
     * @param password The password
     * @param confirmPassword The confirmation password
     * @return true if passwords match, false otherwise
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Sanitize input string to prevent injection attacks
     * @param input The input string to sanitize
     * @return The sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'&]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    /**
     * Get password requirements message
     * @return A string describing password requirements
     */
    public static String getPasswordRequirements() {
        return String.format("Password must be %d-%d characters long and contain at least one letter and one number.", 
                           MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
    }

    /**
     * Get username requirements message
     * @return A string describing username requirements
     */
    public static String getUsernameRequirements() {
        return "Username must be 3-20 characters long and contain only letters, numbers, and underscores.";
    }
} 