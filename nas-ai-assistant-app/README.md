# NAS AI Assistant Backend

Spring Boot backend application for NAS AI Assistant.

## Requirements

- Java 21
- Maven 3.6+

## Quick Start

1. Run the application:
   ```bash
   mvn spring-boot:run
   ```

2. Access the API:
   - Health check: http://localhost:8080/api/health
   - Hello endpoint: http://localhost:8080/api/hello

## Project Structure

```
src/main/java/com/gaojiayi/nasaiassistantapp/
├── NasAiAssistantAppApplication.java  # Main application class
├── controller/
│   └── HelloController.java          # Sample REST controller
└── resources/
    └── application.yml                # Application configuration
```

## Build

```bash
mvn clean package
```

## Test

```bash
mvn test
```
