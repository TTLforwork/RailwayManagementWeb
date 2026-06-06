@echo off
echo ==========================================
echo Restarting Railway Booking System API
echo ==========================================
echo.

echo Stopping any running Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Starting API server...
echo.

start "Railway API Server" cmd /k "cd /d d:\TrainSystem\RailwaySystem && C:\Users\Admin\Downloads\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd spring-boot:run"

echo.
echo API server is starting in a new window...
echo Wait about 30 seconds for it to fully start.
echo.
echo Then test it with: curl http://localhost:8080/api/trains
echo.
pause
