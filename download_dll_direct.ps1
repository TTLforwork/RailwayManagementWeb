# Download SQL Server authentication DLL directly
$url = "https://go.microsoft.com/fwlink/?linkid=2249926"  # Microsoft JDBC Driver download
$zipFile = "d:\TrainSystem\RailwaySystem\sqljdbc.zip"
$extractDir = "d:\TrainSystem\RailwaySystem\sqljdbc_extracted"
$targetDir = "d:\TrainSystem\RailwaySystem"

Write-Host "Downloading Microsoft JDBC Driver package..."
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
try {
    Invoke-WebRequest -Uri $url -OutFile $zipFile -UseBasicParsing -TimeoutSec 60

    Write-Host "Extracting package..."
    Expand-Archive -Path $zipFile -DestinationPath $extractDir -Force

    Write-Host "Searching for auth DLL..."
    $dllFiles = Get-ChildItem -Path $extractDir -Filter "mssql-jdbc_auth-*.x64.dll" -Recurse

    if ($dllFiles.Count -gt 0) {
        foreach ($dll in $dllFiles) {
            Write-Host "Found: $($dll.FullName)"
            Copy-Item $dll.FullName -Destination $targetDir -Force
            Write-Host "Copied to: $targetDir\$($dll.Name)"
        }
    } else {
        Write-Host "No DLL files found in the package"
        Write-Host "Contents of extracted directory:"
        Get-ChildItem -Path $extractDir -Recurse -File | Select-Object -First 20 | ForEach-Object { Write-Host $_.FullName }
    }

    Write-Host "Cleaning up..."
    Remove-Item $zipFile -Force -ErrorAction SilentlyContinue
    Remove-Item $extractDir -Recurse -Force -ErrorAction SilentlyContinue

    Write-Host "Done!"
} catch {
    Write-Host "Error: $_"
}
