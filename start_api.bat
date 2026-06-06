@echo off
echo ===============================================
echo  RAILWAY BOOKING SYSTEM - START API SERVER
echo ===============================================
echo.

cd /d "d:\TrainSystem\RailwaySystem"

echo Starting Spring Boot API Server...
echo.
echo The API will be available at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

"C:\Users\Admin\Downloads\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd" spring-boot:run

pause
