#!/bin/bash
echo "Testing if servers will start..."
echo

# Check Python
if command -v python3 &> /dev/null; then
    echo "✓ Python3 found: $(python3 --version)"
else
    echo "✗ Python3 NOT found - Frontend server won't work!"
fi

# Check Java
if command -v java &> /dev/null; then
    echo "✓ Java found: $(java -version 2>&1 | head -1)"
else
    echo "✗ Java NOT found - Backend won't work!"
fi

# Check if JAR exists
if [ -f "target/moneymate-1.0.0.jar" ]; then
    echo "✓ Backend JAR exists"
else
    echo "✗ Backend JAR missing - run mvn package first"
fi

# Check if frontend files exist
if [ -f "frontend/index.html" ]; then
    echo "✓ Frontend files exist"
else
    echo "✗ Frontend files missing"
fi
