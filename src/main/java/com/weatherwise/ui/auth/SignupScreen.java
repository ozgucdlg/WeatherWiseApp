package com.weatherwise.ui.auth;

import com.weatherwise.auth.service.AuthService;
import com.weatherwise.auth.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SignupScreen {
    private final Stage stage;
    private final AuthService authService;
    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label statusLabel;
    private SignupCallback callback;

    public interface SignupCallback {
        void onSignupSuccess();
        void onLoginRequested();
    }

    public SignupScreen(SignupCallback callback) {
        this.callback = callback;
        this.authService = AuthService.getInstance();
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI() {
        stage.setTitle("WeatherWise - Create Account");
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        // Main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPrefWidth(450);
        mainLayout.setPrefHeight(600);

        // Title
        Label titleLabel = new Label("Create Your Account");
        titleLabel.getStyleClass().add("title-label");

        // Subtitle
        Label subtitleLabel = new Label("Join WeatherWise to get started");
        subtitleLabel.getStyleClass().add("subtitle-label");

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);

        // Username field
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefWidth(350);
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateUsername());

        // Username requirements
        Label usernameRequirements = new Label(ValidationUtil.getUsernameRequirements());
        usernameRequirements.getStyleClass().add("requirements-label");

        // Email field
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setPrefWidth(350);
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(350);
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());

        // Password requirements
        Label passwordRequirements = new Label(ValidationUtil.getPasswordRequirements());
        passwordRequirements.getStyleClass().add("requirements-label");

        // Confirm password field
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setPrefWidth(350);
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateConfirmPassword());

        // Sign up button
        Button signupButton = new Button("Create Account");
        signupButton.setPrefWidth(350);
        signupButton.setOnAction(e -> handleSignup());

        // Status label
        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        // Add components to form
        formContainer.getChildren().addAll(
            usernameLabel, usernameField, usernameRequirements,
            emailLabel, emailField,
            passwordLabel, passwordField, passwordRequirements,
            confirmPasswordLabel, confirmPasswordField,
            signupButton, statusLabel
        );

        // Separator
        Separator separator = new Separator();

        // Login section
        VBox loginContainer = new VBox(10);
        loginContainer.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Already have an account?");
        Button loginButton = new Button("Sign In");
        loginButton.setOnAction(e -> handleLoginRequest());

        loginContainer.getChildren().addAll(loginLabel, loginButton);

        // Add all components to main layout
        mainLayout.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            formContainer,
            separator,
            loginContainer
        );

        // Create scene and apply CSS
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/styles/weather-app.css").toExternalForm());

        stage.setScene(scene);

        // Add enter key handler
        usernameField.setOnAction(e -> handleSignup());
        emailField.setOnAction(e -> handleSignup());
        passwordField.setOnAction(e -> handleSignup());
        confirmPasswordField.setOnAction(e -> handleSignup());
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty() && !ValidationUtil.isValidUsername(username)) {
            usernameField.getStyleClass().add("error-field");
        } else {
            usernameField.getStyleClass().remove("error-field");
        }
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
            emailField.getStyleClass().add("error-field");
        } else {
            emailField.getStyleClass().remove("error-field");
        }
    }

    private void validatePassword() {
        String password = passwordField.getText();
        if (!password.isEmpty() && !ValidationUtil.isValidPassword(password)) {
            passwordField.getStyleClass().add("error-field");
        } else {
            passwordField.getStyleClass().remove("error-field");
        }
        validateConfirmPassword();
    }

    private void validateConfirmPassword() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (!confirmPassword.isEmpty() && !ValidationUtil.doPasswordsMatch(password, confirmPassword)) {
            confirmPasswordField.getStyleClass().add("error-field");
        } else {
            confirmPasswordField.getStyleClass().remove("error-field");
        }
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Clear previous status
        statusLabel.setText("");

        // Validate all fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
            return;
        }

        // Validate username
        if (!ValidationUtil.isValidUsername(username)) {
            statusLabel.setText("Invalid username format");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
            return;
        }

        // Validate email
        if (!ValidationUtil.isValidEmail(email)) {
            statusLabel.setText("Invalid email format");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
            return;
        }

        // Validate password
        if (!ValidationUtil.isValidPassword(password)) {
            statusLabel.setText("Password does not meet requirements");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
            return;
        }

        // Validate password confirmation
        if (!ValidationUtil.doPasswordsMatch(password, confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
            return;
        }

        // Sanitize inputs
        username = ValidationUtil.sanitizeInput(username);
        email = ValidationUtil.sanitizeInput(email);

        try {
            // Attempt registration
            boolean success = authService.registerUser(username, email, password);
            
            if (success) {
                statusLabel.setText("Account created successfully!");
                statusLabel.getStyleClass().remove("error-label");
                statusLabel.getStyleClass().add("success-label");
                
                // Clear form
                clearForm();
                
                // Close signup window and notify callback
                stage.close();
                if (callback != null) {
                    callback.onSignupSuccess();
                }
            } else {
                statusLabel.setText("Registration failed. Username or email may already exist.");
                statusLabel.getStyleClass().remove("success-label");
                statusLabel.getStyleClass().add("error-label");
            }
        } catch (Exception e) {
            statusLabel.setText("Registration failed. Please try again.");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
        }
    }

    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        usernameField.getStyleClass().remove("error-field");
        emailField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
        confirmPasswordField.getStyleClass().remove("error-field");
    }

    private void handleLoginRequest() {
        stage.close();
        if (callback != null) {
            callback.onLoginRequested();
        }
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
} 