#!/bin/bash

echo "========================================"
echo "   Building TypeScript Frontend"
echo "========================================"
echo

cd frontend

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "npm not found. Please install Node.js first."
    echo "Run: sudo dnf install nodejs npm"
    exit 1
fi

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

# Compile TypeScript
echo "Compiling TypeScript..."
npm run build

if [ $? -eq 0 ]; then
    echo
    echo "========================================"
    echo "    TypeScript Build Successful!"
    echo "========================================"
    echo
    echo "Compiled JavaScript: frontend/dist/app.js"
    echo
else
    echo
    echo "========================================"
    echo "    TypeScript Build Failed!"
    echo "========================================"
    exit 1
fi
