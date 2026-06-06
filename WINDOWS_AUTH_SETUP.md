# Windows Authentication Setup Guide

## Current Status

The Railway Booking System API is configured to use Windows Authentication for SQL Server, but requires the Microsoft JDBC authentication DLL to be installed.

## Error Message

```
Error creating Feedback table: Integrated authentication failed
```

## Solution Options

### Option 1: Install Authentication DLL (Recommended for Windows Auth)

1. **Download the Microsoft JDBC Driver package**:
   - Visit: https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
   - Download the latest version compatible with your JRE

2. **Extract and locate the DLL**:
   - Extract the downloaded ZIP file
   - Navigate to the `auth\x64` folder inside the extracted directory
   - Find the file: `mssql-jdbc_auth-X.X.X.x64.dll`

3. **Copy the DLL to one of these locations**:
   - **C:\Windows\System32\** (Recommended - system-wide)
   - **d:\TrainSystem\RailwaySystem\** (Project directory)
   - **Your Java bin directory** (e.g., C:\Program Files\Java\jdk-XX\bin)

4. **Restart the API server**:
   ```powershell
   cd d:\TrainSystem\RailwaySystem
   .\start_api.bat
   ```

### Option 2: Use SQL Server Authentication (Simpler)

If you prefer to use SQL Server authentication instead:

1. **Enable Mixed Mode Authentication in SQL Server**:
   ```powershell
   # Run in PowerShell as Administrator
   sqlcmd -S localhost\SQLEXPRESS -Q "EXEC xp_instance_regwrite N'HKEY_LOCAL_MACHINE', N'Software\Microsoft\MSSQLServer\MSSQLServer', N'LoginMode', REG_DWORD, 2"

   # Restart SQL Server
   Restart-Service 'MSSQL$SQLEXPRESS'
   ```

2. **Create a SQL Server login**:
   ```powershell
   sqlcmd -S localhost\SQLEXPRESS -Q "CREATE LOGIN railwayuser WITH PASSWORD = 'Railway@123'"
   sqlcmd -S localhost\SQLEXPRESS -Q "USE railway_system; CREATE USER railwayuser FOR LOGIN railwayuser"
   sqlcmd -S localhost\SQLEXPRESS -Q "USE railway_system; ALTER ROLE db_owner ADD MEMBER railwayuser"
   ```

3. **Update database.properties**:
   Edit `src\main\resources\database.properties`:
   ```properties
   db.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=railway_system;encrypt=true;trustServerCertificate=true;loginTimeout=5
   db.username=railwayuser
   db.password=Railway@123
   ```
   (Remove `integratedSecurity=true` and `authenticationScheme=JavaKerberos`)

4. **Restart the API server**

## Current Configuration

**File**: `src\main\resources\database.properties`

```properties
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
db.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=railway_system;integratedSecurity=true;authenticationScheme=JavaKerberos;encrypt=true;trustServerCertificate=true;loginTimeout=5
db.username=
db.password=
```

## Verify Connection

After completing either option, test the API:

```bash
curl http://localhost:8080/api/trains
```

You should see a JSON response instead of an error.

## Notes

- **Java Version**: You're running Java 25.0.1 (64-bit)
- **SQL Server**: SQLEXPRESS on localhost
- **Database**: railway_system
- **API Port**: 8080
