package com.weatherwise;

import com.weatherwise.model.WeatherData;
import com.weatherwise.service.WeatherService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class WeatherApp extends Application {
    private final WeatherService weatherService = new WeatherService();
    private TextArea weatherDisplay;
    private TextField cityInput;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("WeatherWise Application");

        // Create the main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

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
            titleLabel,
            searchBox,
            weatherDisplay,
            statusLabel
        );

        // Create the scene and apply CSS
        Scene scene = new Scene(mainLayout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/weather-app.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(400);
        primaryStage.show();

        // Add enter key handler
        cityInput.setOnAction(e -> searchWeather());
    }

    private void searchWeather() {
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

    public static void main(String[] args) {
        launch(args);
    }
} 