# Install SQL Server Authentication DLL for Windows Authentication
# This script must be run as Administrator

Write-Host "=========================================="
Write-Host "SQL Server Auth DLL Installation"
Write-Host "=========================================="
Write-Host ""

# Check if running as administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "ERROR: This script must be run as Administrator!" -ForegroundColor Red
    Write-Host ""
    Write-Host "To run as Administrator:"
    Write-Host "1. Right-click on PowerShell"
    Write-Host "2. Select 'Run as Administrator'"
    Write-Host "3. Run this command:"
    Write-Host "   cd d:\TrainSystem\RailwaySystem"
    Write-Host "   .\install_auth_dll.ps1"
    Write-Host ""
    pause
    exit 1
}

Write-Host "Downloading Microsoft JDBC Driver..." -ForegroundColor Yellow

# Download URL for JDBC Driver 12.4
$url = "https://download.microsoft.com/download/d/b/1/db19b5e4-52fc-4f21-bec0-c1cb8b58e4b4/sqljdbc_12.4.2.0_enu.tar.gz"
$downloadPath = "$env:TEMP\sqljdbc.tar.gz"
$extractPath = "$env:TEMP\sqljdbc_extracted"

try {
    # Download the tar.gz file
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $url -OutFile $downloadPath -UseBasicParsing

    Write-Host "Download complete!" -ForegroundColor Green
    Write-Host "Extracting files..." -ForegroundColor Yellow

    # Create extraction directory
    New-Item -ItemType Directory -Force -Path $extractPath | Out-Null

    # Extract tar.gz (requires tar command available in Windows 10+)
    tar -xzf $downloadPath -C $extractPath

    Write-Host "Searching for authentication DLL..." -ForegroundColor Yellow

    # Find the DLL file
    $dllFile = Get-ChildItem -Path $extractPath -Filter "mssql-jdbc_auth-*.x64.dll" -Recurse | Select-Object -First 1

    if ($dllFile) {
        Write-Host "Found: $($dllFile.Name)" -ForegroundColor Green

        # Copy to System32
        $destination = "C:\Windows\System32\$($dllFile.Name)"
        Copy-Item $dllFile.FullName -Destination $destination -Force

        Write-Host "Successfully copied to: $destination" -ForegroundColor Green

        # Also copy to project directory as backup
        Copy-Item $dllFile.FullName -Destination "d:\TrainSystem\RailwaySystem\" -Force
        Write-Host "Also copied to project directory" -ForegroundColor Green

        # Cleanup
        Write-Host "Cleaning up temporary files..." -ForegroundColor Yellow
        Remove-Item $downloadPath -Force -ErrorAction SilentlyContinue
        Remove-Item $extractPath -Recurse -Force -ErrorAction SilentlyContinue

        Write-Host ""
        Write-Host "=========================================="
        Write-Host "Installation Complete!" -ForegroundColor Green
        Write-Host "=========================================="
        Write-Host ""
        Write-Host "Next steps:"
        Write-Host "1. Close this window"
        Write-Host "2. Run: .\start_api.bat"
        Write-Host ""

    } else {
        Write-Host "ERROR: Could not find authentication DLL in the package" -ForegroundColor Red
        Write-Host "You may need to download manually from:"
        Write-Host "https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server"
    }

} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Manual download instructions:"
    Write-Host "1. Visit: https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server"
    Write-Host "2. Download and extract the driver"
    Write-Host "3. Find mssql-jdbc_auth-*.x64.dll in auth\x64 folder"
    Write-Host "4. Copy it to C:\Windows\System32\"
}

pause
