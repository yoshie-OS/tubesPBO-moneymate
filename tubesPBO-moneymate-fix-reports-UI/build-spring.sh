#!/bin/bash

echo "========================================"
echo "   Building Spring Boot Backend"
echo "========================================"
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven first."
    echo
    echo "Install command:"
    echo "  Fedora/RHEL: sudo dnf install maven"
    echo "  Ubuntu:      sudo apt install maven"
    echo "  Windows:     Download from https://maven.apache.org/download.cgi"
    exit 1
fi

# Build with Maven
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo
    echo "========================================"
    echo "✓ Spring Boot Build Successful!"
    echo "========================================"
    echo "JAR location: target/moneymate-1.0.0.jar"
else
    echo
    echo "❌ Build failed!"
    exit 1
fi
