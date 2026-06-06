@echo off
echo ==========================================
echo Setting up Windows Authentication for JDBC
echo ==========================================
echo.

REM The simplest solution is to copy the DLL to Windows System32
echo Option 1: Copy DLL to C:\Windows\System32
echo This requires downloading the DLL manually if not present
echo.

REM Check Java library path
echo Checking Java library path...
java -XshowSettings:properties 2>&1 | findstr "java.library.path"
echo.

echo ==========================================
echo MANUAL STEPS REQUIRED:
echo ==========================================
echo.
echo 1. Download the Microsoft JDBC Driver from:
echo    https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
echo.
echo 2. Extract the downloaded ZIP file
echo.
echo 3. Find the file: mssql-jdbc_auth-X.X.X.x64.dll
echo    (located in the sqljdbc_X.X\enu\auth\x64 folder)
echo.
echo 4. Copy it to ONE of these locations:
echo    - C:\Windows\System32\
echo    - d:\TrainSystem\RailwaySystem\
echo    - Your Java bin directory
echo.
echo 5. Restart the API server
echo.
echo ==========================================
echo.
echo Alternatively, you can use SQL Server Authentication:
echo - Keep the current connection string
echo - Run SQL Server Management Studio
echo - Enable Mixed Mode Authentication
echo - Create a SQL login with username/password
echo.
pause
