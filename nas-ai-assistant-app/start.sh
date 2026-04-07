#!/bin/bash

# NAS AI Assistant Backend Startup Script

echo "Starting NAS AI Assistant Backend..."
echo "Java version:"
java -version

echo ""
echo "Compiling project..."
if command -v mvn &> /dev/null; then
    mvn clean compile
    echo ""
    echo "Starting application..."
    mvn spring-boot:run
else
    echo "Maven not found. Please install Maven or use the Maven Wrapper:"
    echo "  ./mvnw spring-boot:run"
fi
