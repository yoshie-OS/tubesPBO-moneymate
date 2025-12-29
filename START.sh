#!/bin/bash

echo "╔════════════════════════════════════════╗"
echo "║                                        ║"
echo "║      MoneyMate Full Stack Setup       ║"
echo "║   TypeScript + Spring Boot + SQLite    ║"
echo "║                                        ║"
echo "╚════════════════════════════════════════╝"
echo

# Step 1: Build Frontend
echo "[1/3] Building TypeScript Frontend..."
bash frontend/build.sh
if [ $? -ne 0 ]; then
    echo "Frontend build failed!"
    exit 1
fi

# Step 2: Build Backend
echo
echo "[2/3] Building Spring Boot Backend..."
bash build-spring.sh
if [ $? -ne 0 ]; then
    echo "Backend build failed!"
    exit 1
fi

# Step 3: Start Spring Boot
echo
echo "[3/3] Starting Spring Boot Server..."
echo
echo "╔════════════════════════════════════════╗"
echo "║            READY TO USE!               ║"
echo "╠════════════════════════════════════════╣"
echo "║  Backend:  http://localhost:8080/api  ║"
echo "║  Frontend: Open frontend/index.html    ║"
echo "╚════════════════════════════════════════╝"
echo
echo "Starting backend server..."
java -jar target/moneymate-1.0.0.jar
