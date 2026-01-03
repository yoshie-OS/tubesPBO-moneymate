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

# Step 3: Start Frontend Server
echo
echo "[3/4] Starting Frontend Server on port 3000..."
cd frontend
python3 -m http.server 3000 > /dev/null 2>&1 &
FRONTEND_PID=$!
cd ..
echo "Frontend server started (PID: $FRONTEND_PID)"

# Step 4: Start Spring Boot
echo
echo "[4/4] Starting Spring Boot Server..."
echo
echo "╔════════════════════════════════════════╗"
echo "║            READY TO USE!               ║"
echo "╠════════════════════════════════════════╣"
echo "║  Backend:  http://localhost:8080/api   ║"
echo "║  Frontend: http://localhost:3000       ║"
echo "╚════════════════════════════════════════╝"
echo
echo "Starting backend server..."
java -jar target/moneymate-1.0.0.jar
