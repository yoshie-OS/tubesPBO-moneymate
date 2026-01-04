@echo off
echo =========================================
echo MoneyMate - Database Reset
echo =========================================
echo.
echo WARNING: This will delete all data!
echo Press Ctrl+C to cancel or
pause

echo.
echo Stopping any Java processes...
taskkill /F /IM java.exe /FI "WINDOWTITLE eq MoneyMate*" 2>nul
timeout /t 2 /nobreak >nul

echo Deleting old database...
if exist moneymate.db (
    del /F /Q moneymate.db
    echo Database deleted successfully!
) else (
    echo No database file found.
)

echo.
echo Database will be recreated on next startup.
echo.
pause
