@echo off
echo ==========================================
echo Copying SQL Server Authentication DLL
echo ==========================================
echo.

echo Source: C:\Users\Admin\Downloads\sqljdbc_13.2.1.0_enu\sqljdbc_13.2\enu\auth\x64
echo Destination: C:\Windows\System32\
echo.

REM Copy the DLL file to System32
copy "C:\Users\Admin\Downloads\sqljdbc_13.2.1.0_enu\sqljdbc_13.2\enu\auth\x64\mssql-jdbc_auth-13.2.1.x64.dll" "C:\Windows\System32\" /Y

if %errorlevel% == 0 (
    echo.
    echo ==========================================
    echo SUCCESS! DLL copied successfully
    echo ==========================================
    echo.
    echo The authentication DLL has been installed.
    echo.
    echo Next step: Restart the API server
    echo Run: restart_api.bat
    echo.
) else (
    echo.
    echo ==========================================
    echo ERROR: Failed to copy DLL
    echo ==========================================
    echo.
    echo You may need to run this as Administrator.
    echo Right-click this file and select "Run as administrator"
    echo.
)

pause
