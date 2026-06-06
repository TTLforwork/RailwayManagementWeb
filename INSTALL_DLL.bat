@echo off
echo ==========================================
echo SQL Server Authentication DLL Installer
echo ==========================================
echo.
echo This will install the required DLL for Windows Authentication
echo.
echo IMPORTANT: You will be prompted for Administrator privileges
echo.
pause

PowerShell -Command "Start-Process PowerShell -ArgumentList '-ExecutionPolicy Bypass -File ""d:\TrainSystem\RailwaySystem\install_auth_dll.ps1""' -Verb RunAs"

echo.
echo The installation script has been launched in an elevated window.
echo Please check that window for progress.
echo.
pause
