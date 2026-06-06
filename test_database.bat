@echo off
echo ===============================================
echo  RAILWAY BOOKING SYSTEM - DATABASE TEST
echo ===============================================
echo.

cd /d "d:\TrainSystem\RailwaySystem"

echo Running database connection test...
echo.

"C:\Users\Admin\Downloads\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd" exec:java -Dexec.mainClass=com.railway.test.DatabaseTest

echo.
echo ===============================================
echo  TEST COMPLETED
echo ===============================================
echo.
pause
