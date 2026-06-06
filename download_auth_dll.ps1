# Download SQL Server authentication DLL
$url = "https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/12.4.1.jre11/mssql-jdbc-12.4.1.jre11.jar"
$jarFile = "d:\TrainSystem\RailwaySystem\mssql-jdbc-12.4.1.jre11.jar"
$outputDir = "d:\TrainSystem\RailwaySystem\auth"

Write-Host "Downloading JDBC JAR file..."
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri $url -OutFile $jarFile -UseBasicParsing

Write-Host "Extracting DLL from JAR..."
New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory($jarFile, $outputDir)

Write-Host "Locating auth DLL..."
Get-ChildItem -Path $outputDir -Filter "mssql-jdbc_auth*.dll" -Recurse | ForEach-Object {
    Write-Host "Found: $($_.FullName)"
    Copy-Item $_.FullName -Destination "d:\TrainSystem\RailwaySystem\" -Force
    Write-Host "Copied to d:\TrainSystem\RailwaySystem\$($_.Name)"
}

Write-Host "Cleaning up..."
Remove-Item $jarFile -Force
Remove-Item $outputDir -Recurse -Force

Write-Host "Done!"
