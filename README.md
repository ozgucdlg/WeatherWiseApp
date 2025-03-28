# WeatherWise Application

A modern and user-friendly weather application built with JavaFX that provides real-time weather information for cities worldwide.

## Features

- 🔍 **City Search**: Easily search for any city worldwide
- 🌡️ **Temperature Data**: View current temperature and "feels like" temperature in Celsius
- 💧 **Humidity Information**: Check current humidity levels
- 🌪️ **Wind Speed**: Monitor wind conditions in meters per second
- ⏲️ **Atmospheric Pressure**: View pressure in hectopascals (hPa)
- 👁️ **Visibility**: Check visibility conditions in kilometers
- ☀️ **Sun Timings**: Track sunrise and sunset times
- 📱 **Modern UI**: Clean and intuitive user interface with emoji indicators
- ⚡ **Fast Response**: Quick weather data retrieval with caching system
- 🔄 **Auto-Refresh**: Weather data is cached for 5 minutes for optimal performance

## Installation

1. Ensure you have Java 11 or later installed
2. Clone the repository:
   ```bash
   git clone https://github.com/ozgucdlg/WeatherWiseApp.git
   ```
3. Navigate to the project directory:
   ```bash
   cd WeatherWiseApp
   ```
4. Build the project using Maven:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn javafx:run
   ```

## Usage

1. Launch the application
2. Enter a city name in the search field
3. Click "Show Weather" or press Enter
4. View detailed weather information including:
   - Current temperature
   - Feels like temperature
   - Humidity percentage
   - Wind speed
   - Atmospheric pressure
   - Visibility
   - Sunrise and sunset times
   - Current weather conditions

## Technology Stack

- Java 11
- JavaFX for GUI
- Maven for dependency management
- OpenWeatherMap API for weather data
- JSON for data parsing

## API Reference

The application uses the OpenWeatherMap API to fetch weather data. The following endpoints are used:

- Current Weather Data: `api.openweathermap.org/data/2.5/weather`

Parameters:
- `q`: City name
- `units`: Metric
- `lang`: en (English)
- `appid`: Your API key

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Weather data provided by [OpenWeatherMap](https://openweathermap.org/)
- Icons and emojis for weather representation
- JavaFX community for GUI components

## Screenshots

```
[Application screenshots to be added]
```

## Contact

Özgüç Dalgiç - [@ozgucdlg](https://github.com/ozgucdlg)

Project Link: [https://github.com/ozgucdlg/WeatherWiseApp](https://github.com/ozgucdlg/WeatherWiseApp)
