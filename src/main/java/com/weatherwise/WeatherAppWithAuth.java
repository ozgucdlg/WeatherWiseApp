package com.weatherwise;

import com.weatherwise.auth.model.UserSession;
import com.weatherwise.auth.service.AuthService;
import com.weatherwise.model.WeatherData;
import com.weatherwise.service.WeatherService;
import com.weatherwise.ui.auth.LoginScreen;
import com.weatherwise.ui.auth.SignupScreen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class WeatherAppWithAuth extends Application {
    private final WeatherService weatherService = new WeatherService();
    private final AuthService authService = AuthService.getInstance();
    
    private TextArea weatherDisplay;
    private TextField cityInput;
    private Label statusLabel;
    private Label userLabel;
    private Button logoutButton;
    private UserSession currentSession;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("WeatherWise Application");
        
        // Check if user is already authenticated
        if (currentSession == null) {
            showAuthenticationScreen();
        } else {
            showMainApplication();
        }
    }

    private void showAuthenticationScreen() {
        LoginScreen loginScreen = new LoginScreen(new LoginScreen.LoginCallback() {
            @Override
            public void onLoginSuccess(UserSession session) {
                currentSession = session;
                showMainApplication();
            }

            @Override
            public void onSignupRequested() {
                showSignupScreen();
            }
        });
        
        loginScreen.show();
    }

    private void showSignupScreen() {
        SignupScreen signupScreen = new SignupScreen(new SignupScreen.SignupCallback() {
            @Override
            public void onSignupSuccess() {
                // Show login screen after successful signup
                showAuthenticationScreen();
            }

            @Override
            public void onLoginRequested() {
                showAuthenticationScreen();
            }
        });
        
        signupScreen.show();
    }

    private void showMainApplication() {
        // Create the main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // Create header with user info and logout
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        // User info
        userLabel = new Label("Welcome, " + currentSession.getUsername() + "!");
        userLabel.getStyleClass().add("user-label");

        // Logout button
        logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> handleLogout());

        headerBox.getChildren().addAll(userLabel, logoutButton);

        // Create title
        Label titleLabel = new Label("WeatherWise - Weather Application");
        titleLabel.getStyleClass().add("title-label");

        // Create the search section
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 20, 0));
        
        cityInput = new TextField();
        cityInput.setPromptText("Enter city name...");
        cityInput.setPrefWidth(250);
        
        Button searchButton = new Button("Show Weather");
        searchButton.setOnAction(e -> searchWeather());
        
        searchBox.getChildren().addAll(cityInput, searchButton);

        // Create the display area
        weatherDisplay = new TextArea();
        weatherDisplay.setEditable(false);
        weatherDisplay.setPrefRowCount(12);
        weatherDisplay.setPrefColumnCount(40);
        weatherDisplay.setWrapText(true);

        // Create status label
        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        // Add all components to the main layout
        mainLayout.getChildren().addAll(
            headerBox,
            titleLabel,
            searchBox,
            weatherDisplay,
            statusLabel
        );

        // Create the scene and apply CSS
        Scene scene = new Scene(mainLayout, 600, 550);
        scene.getStylesheets().add(getClass().getResource("/styles/weather-app.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(400);
        primaryStage.show();

        // Add enter key handler
        cityInput.setOnAction(e -> searchWeather());

        // Display welcome message
        weatherDisplay.setText("Welcome to WeatherWise!\n\n" +
                             "Enter a city name above to get current weather information.\n\n" +
                             "Features:\n" +
                             "• Current temperature and feels like\n" +
                             "• Humidity and wind speed\n" +
                             "• Atmospheric pressure and visibility\n" +
                             "• Sunrise and sunset times\n" +
                             "• Weather conditions description");
    }

    private void searchWeather() {
        // Validate session before proceeding
        if (currentSession == null || authService.validateSession(currentSession.getSessionId()) == null) {
            handleSessionExpired();
            return;
        }

        String city = cityInput.getText().trim();
        if (city.isEmpty()) {
            statusLabel.setText("Please enter a city name!");
            return;
        }

        try {
            WeatherData weatherData = weatherService.getWeatherData(city);
            weatherDisplay.setText(weatherData.toString());
            statusLabel.setText("");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            weatherDisplay.setText("");
        }
    }

    private void handleLogout() {
        if (currentSession != null) {
            authService.logout(currentSession.getSessionId());
            currentSession = null;
        }
        
        // Clear the main window
        primaryStage.close();
        
        // Show authentication screen again
        showAuthenticationScreen();
    }

    private void handleSessionExpired() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Session Expired");
        alert.setHeaderText("Your session has expired");
        alert.setContentText("Please log in again to continue using the application.");
        alert.showAndWait();
        
        handleLogout();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 