@echo off
echo ╔════════════════════════════════════════╗
echo ║                                        ║
echo ║      MoneyMate Full Stack Setup       ║
echo ║   TypeScript + Spring Boot + SQLite    ║
echo ║                                        ║
echo ╚════════════════════════════════════════╝
echo.

REM Step 1: Build Frontend
echo [1/3] Building TypeScript Frontend...
cd frontend
call npm install
call npm run build
if %errorlevel% neq 0 (
    echo Frontend build failed!
    pause
    exit /b 1
)
cd ..

REM Step 2: Build Backend
echo.
echo [2/3] Building Spring Boot Backend...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo Backend build failed!
    pause
    exit /b 1
)

REM Step 3: Start Frontend Server
echo.
echo [3/4] Starting Frontend Server on port 3000...
cd frontend
start /B python -m http.server 3000 >nul 2>&1
cd ..
echo Frontend server started

REM Step 4: Start Spring Boot
echo.
echo [4/4] Starting Spring Boot Server...
echo.
echo ╔════════════════════════════════════════╗
echo ║            READY TO USE!               ║
echo ╠════════════════════════════════════════╣
echo ║  Backend:  http://localhost:8080/api   ║
echo ║  Frontend: http://localhost:3000       ║
echo ╚════════════════════════════════════════╝
echo.
echo Starting backend server...
java -jar target\moneymate-1.0.0.jar
