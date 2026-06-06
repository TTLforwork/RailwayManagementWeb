@echo off
echo ===============================================
echo  FIX SQL SERVER BROWSER SERVICE
echo ===============================================
echo.
echo Starting SQL Server Browser Service...
echo.

net start "SQLBrowser"

echo.
echo ===============================================
echo  DONE!
echo ===============================================
echo.
echo Now run: test_database.bat
echo.
pause
