# WeatherWise Application

## Overview
WeatherWise is a Java-based weather information application that provides real-time weather data using the OpenWeatherMap API. The application delivers accurate weather information including temperature, humidity, and weather conditions for any city worldwide.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Reference](#api-reference)
- [Contributing](#contributing)
- [License](#license)

## Features
- Real-time weather data retrieval
- Temperature display in Celsius
- Humidity percentage information
- Current weather conditions description
- Turkish language support for weather descriptions
- UTF-8 character encoding support
- Error handling for API requests

## Technologies Used
- Java 11
- Maven
- OpenWeatherMap API
- org.json Library

## Prerequisites
Before running the application, ensure you have the following installed:
- Java Development Kit (JDK) 11 or higher
- Maven 3.6.x or higher
- Git (optional, for version control)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/ozgucdlg/WeatherWiseApp.git
```

2. Navigate to the project directory:
```bash
cd WeatherWiseApp
```

3. Build the project:
```bash
mvn clean install
```

## Configuration
The application uses the OpenWeatherMap API. The API key is already configured in the application. However, if you need to use your own API key:

1. Sign up at [OpenWeatherMap](https://openweathermap.org/api)
2. Get your API key
3. Replace the API key in `src/main/java/com/weatherwise/WeatherApi.java`

## Usage
To run the application:

```bash
mvn exec:java
```

When prompted:
1. Enter the name of the city you want to check the weather for
2. The application will display:
   - City name
   - Current temperature in Celsius
   - Humidity percentage
   - Weather description

Example output:
```
Lütfen şehir ismi giriniz:
Istanbul
Şehir: Istanbul
Sıcaklık: 18.5°C
Nem: 65%
Hava Durumu: parçalı bulutlu
```

## API Reference
This application uses the OpenWeatherMap API v2.5. For more information about the API:
- [OpenWeatherMap API Documentation](https://openweathermap.org/current)
- Base URL: `api.openweathermap.org/data/2.5/weather`
- Supported Parameters:
  - `q`: City name
  - `appid`: API key
  - `units`: Metric
  - `lang`: Language (tr for Turkish)

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request. For major changes:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
Developed by [ozgucdlg](https://github.com/ozgucdlg)
