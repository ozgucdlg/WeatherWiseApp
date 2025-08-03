package com.weatherwise.ui.auth;

import com.weatherwise.auth.model.UserSession;
import com.weatherwise.auth.service.AuthService;
import com.weatherwise.auth.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginScreen {
    private final Stage stage;
    private final AuthService authService;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label statusLabel;
    private LoginCallback callback;

    public interface LoginCallback {
        void onLoginSuccess(UserSession session);
        void onSignupRequested();
    }

    public LoginScreen(LoginCallback callback) {
        this.callback = callback;
        this.authService = AuthService.getInstance();
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI() {
        stage.setTitle("WeatherWise - Login");
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        // Main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPrefWidth(400);
        mainLayout.setPrefHeight(500);

        // Title
        Label titleLabel = new Label("Welcome to WeatherWise");
        titleLabel.getStyleClass().add("title-label");

        // Subtitle
        Label subtitleLabel = new Label("Please sign in to continue");
        subtitleLabel.getStyleClass().add("subtitle-label");

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);

        // Username field
        Label usernameLabel = new Label("Username or Email:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username or email");
        usernameField.setPrefWidth(300);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(300);

        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(300);
        loginButton.setOnAction(e -> handleLogin());

        // Status label
        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        // Add components to form
        formContainer.getChildren().addAll(
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            loginButton, statusLabel
        );

        // Separator
        Separator separator = new Separator();

        // Sign up section
        VBox signupContainer = new VBox(10);
        signupContainer.setAlignment(Pos.CENTER);

        Label signupLabel = new Label("Don't have an account?");
        Button signupButton = new Button("Create Account");
        signupButton.setOnAction(e -> handleSignupRequest());

        signupContainer.getChildren().addAll(signupLabel, signupButton);

        // Add all components to main layout
        mainLayout.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            formContainer,
            separator,
            signupContainer
        );

        // Create scene and apply CSS
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/styles/weather-app.css").toExternalForm());

        stage.setScene(scene);

        // Add enter key handler
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Clear previous status
        statusLabel.setText("");

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        // Sanitize input
        username = ValidationUtil.sanitizeInput(username);

        try {
            // Attempt login
            UserSession session = authService.login(username, password);
            
            if (session != null) {
                statusLabel.setText("Login successful!");
                statusLabel.getStyleClass().remove("error-label");
                statusLabel.getStyleClass().add("success-label");
                
                // Close login window and notify callback
                stage.close();
                if (callback != null) {
                    callback.onLoginSuccess(session);
                }
            } else {
                statusLabel.setText("Invalid username or password");
                statusLabel.getStyleClass().remove("success-label");
                statusLabel.getStyleClass().add("error-label");
                passwordField.clear();
            }
        } catch (Exception e) {
            statusLabel.setText("Login failed. Please try again.");
            statusLabel.getStyleClass().remove("success-label");
            statusLabel.getStyleClass().add("error-label");
        }
    }

    private void handleSignupRequest() {
        stage.close();
        if (callback != null) {
            callback.onSignupRequested();
        }
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
} 