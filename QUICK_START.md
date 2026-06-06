# Quick Start Guide

## Railway Booking System - Get Started in 5 Minutes

### Step 1: Database Setup (2 minutes)

**For SQL Server:**
```sql
-- Open SQL Server Management Studio (SSMS)
-- Connect to your server
-- Run these commands:

CREATE DATABASE RailwaySystem;
GO

USE RailwaySystem;
GO

-- Then execute the database_setup.sql file
```

**For MySQL:**
```sql
-- Open MySQL Workbench
-- Connect to your server
-- Run these commands:

CREATE DATABASE railway_system;
USE railway_system;

-- Then execute the database_setup.sql file
```

### Step 2: Configure Database Connection (1 minute)

Edit `src/main/resources/database.properties`:

**For SQL Server:**
```properties
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
db.url=jdbc:sqlserver://localhost:1433;databaseName=RailwaySystem;encrypt=true;trustServerCertificate=true
db.username=YOUR_USERNAME
db.password=YOUR_PASSWORD
```

**For MySQL:**
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/railway_system?useSSL=false&serverTimezone=UTC
db.username=YOUR_USERNAME
db.password=YOUR_PASSWORD
```

### Step 3: Build the Project (1 minute)

Open terminal/command prompt in the project directory:

```bash
mvn clean compile
```

### Step 4: Run the Application (1 minute)

```bash
mvn exec:java -Dexec.mainClass="com.railway.RailwayBookingSystem"
```

### Step 5: Login and Explore

**Try Admin Features:**
- Username: `admin`
- Password: `admin123`

**Try Passenger Features:**
- Register a new account, OR
- Use sample account:
  - Username: `john_doe`
  - Password: `password123`

## Quick Feature Test

### Test Passenger Features:
1. Login as passenger
2. Search trains: Enter "New York" as source, "Chicago" as destination
3. Book a ticket for train T001
4. View your bookings
5. Submit feedback with 5-star rating

### Test Admin Features:
1. Login as admin
2. View all trains
3. Add a new train
4. View all bookings
5. Respond to pending feedback

## Troubleshooting

**Can't connect to database?**
- Make sure SQL Server/MySQL is running
- Check your credentials in database.properties
- For SQL Server: Enable TCP/IP in SQL Server Configuration Manager

**Maven not found?**
- Download from https://maven.apache.org/download.cgi
- Add to PATH environment variable

**Java version error?**
- This project requires Java 11 or higher
- Download from https://www.oracle.com/java/technologies/downloads/

## Project Structure Overview

```
RailwaySystem/
├── src/main/java/com/railway/
│   ├── RailwayBookingSystem.java  ← Main application (START HERE)
│   ├── model/                      ← Data models
│   ├── dao/                        ← Database operations
│   ├── service/                    ← Business logic
│   ├── ui/                         ← User interface menus
│   └── util/                       ← Utilities
├── database_setup.sql              ← Database schema
└── pom.xml                         ← Maven configuration
```

## Key Classes to Explore

1. **[RailwayBookingSystem.java](src/main/java/com/railway/RailwayBookingSystem.java)** - Main entry point
2. **[PassengerMenu.java](src/main/java/com/railway/ui/PassengerMenu.java)** - Passenger features
3. **[AdminMenu.java](src/main/java/com/railway/ui/AdminMenu.java)** - Admin features
4. **[BookingService.java](src/main/java/com/railway/service/BookingService.java)** - Booking logic
5. **[TrainService.java](src/main/java/com/railway/service/TrainService.java)** - Train management

## Common Commands

**Build project:**
```bash
mvn clean compile
```

**Run application:**
```bash
mvn exec:java -Dexec.mainClass="com.railway.RailwayBookingSystem"
```

**Package to JAR:**
```bash
mvn clean package
```

**Run JAR:**
```bash
java -jar target/RailwayBookingSystem-1.0-SNAPSHOT.jar
```

**Clean build artifacts:**
```bash
mvn clean
```

## Sample Data

The `database_setup.sql` includes:
- 5 sample users
- 5 sample trains
- 5 sample stations
- 5 sample bookings
- Train schedules

You can login with any sample user or create your own account.

## Next Steps

1. ✅ Complete the setup steps above
2. ✅ Test both passenger and admin features
3. ✅ Explore the code structure
4. ✅ Read the full [README.md](README.md) for detailed documentation
5. ✅ Customize and extend the features

## Support

For detailed documentation, see [README.md](README.md)

For issues, check the Troubleshooting section in README.md

---

**Happy Coding! 🚂**
