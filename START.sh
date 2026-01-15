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

# Step 3: Start Frontend (static server) and Backend
echo
echo "[3/3] Starting Frontend (http://localhost:3000)"
pushd frontend >/dev/null
python3 -m http.server 3000 >/tmp/moneymate-frontend.log 2>&1 &
FRONT_PID=$!
popd >/dev/null

echo
echo "Starting Spring Boot Backend (http://localhost:8080/api)..."
echo
echo "╔════════════════════════════════════════╗"
echo "║            READY TO USE!               ║"
echo "╠════════════════════════════════════════╣"
echo "║  Backend:  http://localhost:8080/api  ║"
echo "║  Frontend: http://localhost:3000       ║"
echo "╚════════════════════════════════════════╝"
echo

java -jar target/moneymate-1.0.0.jar

echo "Stopping frontend server (PID: $FRONT_PID)"
kill $FRONT_PID 2>/dev/null
