# Database Setup Guide

## Quick Reference

| Database Type | Script to Use | Database Name |
|--------------|---------------|---------------|
| **SQL Server** | `database_setup.sql` | `RailwaySystem` |
| **MySQL** | `database_setup_mysql.sql` | `railway_system` |

---

## SQL Server Setup

### Method 1: Automatic (Recommended)

**Step 1:** Open SQL Server Management Studio (SSMS)

**Step 2:** Connect to your SQL Server instance (e.g., `localhost`)

**Step 3:** Open the file `database_setup.sql`
- File → Open → File → Select `database_setup.sql`

**Step 4:** Execute the script
- Click "Execute" button or press F5

**What the script does:**
```sql
-- Creates database if it doesn't exist
CREATE DATABASE RailwaySystem;

-- Uses the database
USE RailwaySystem;

-- Creates all 10 tables
-- Inserts sample data
```

**Expected Output:**
```
(5 rows affected)  -- Users inserted
(5 rows affected)  -- Trains inserted
(5 rows affected)  -- Stations inserted
...
Database setup completed successfully!
```

### Method 2: Manual Steps

If you want to do it manually:

```sql
-- Step 1: Create database
CREATE DATABASE RailwaySystem;
GO

-- Step 2: Use the database
USE RailwaySystem;
GO

-- Step 3: Execute the rest of database_setup.sql
-- (Copy and paste the table creation and insert statements)
```

---

## MySQL Setup

### Method 1: Automatic (Recommended)

**Step 1:** Open MySQL Workbench

**Step 2:** Connect to your MySQL server (e.g., `localhost:3306`)

**Step 3:** Open the file `database_setup_mysql.sql`
- File → Open SQL Script → Select `database_setup_mysql.sql`

**Step 4:** Execute the script
- Click the lightning bolt icon or press Ctrl+Shift+Enter

**What the script does:**
```sql
-- Creates database if it doesn't exist
CREATE DATABASE IF NOT EXISTS railway_system;

-- Uses the database
USE railway_system;

-- Creates all 10 tables
-- Inserts sample data
```

**Expected Output:**
```
5 row(s) affected    -- Users inserted
5 row(s) affected    -- Trains inserted
5 row(s) affected    -- Stations inserted
...
'Database setup completed successfully!'
```

### Method 2: Command Line

```bash
# Login to MySQL
mysql -u root -p

# Execute the script
source /path/to/database_setup_mysql.sql

# Or in one command:
mysql -u root -p < database_setup_mysql.sql
```

---

## Verify Installation

After running the script, verify the setup:

### For SQL Server:

```sql
USE RailwaySystem;

-- Check all tables exist
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- Expected: 10 tables
-- Admin, Admin_Station, Admin_Train, Login, Passenger,
-- Schedule, Station, Ticket, Train, User

-- Check sample data
SELECT COUNT(*) AS UserCount FROM [User];      -- Should be 5
SELECT COUNT(*) AS TrainCount FROM Train;      -- Should be 5
SELECT COUNT(*) AS TicketCount FROM Ticket;    -- Should be 5
```

### For MySQL:

```sql
USE railway_system;

-- Check all tables exist
SHOW TABLES;

-- Expected: 10 tables
-- Admin, Admin_Station, Admin_Train, Login, Passenger,
-- Schedule, Station, Ticket, Train, User

-- Check sample data
SELECT COUNT(*) AS UserCount FROM User;        -- Should be 5
SELECT COUNT(*) AS TrainCount FROM Train;      -- Should be 5
SELECT COUNT(*) AS TicketCount FROM Ticket;    -- Should be 5
```

---

## Sample Data Overview

The script inserts the following seed data:

### Users (5 records)
| Username | Password | Name | City |
|----------|----------|------|------|
| john_doe | password123 | John Doe | New York |
| jane_smith | securepass456 | Jane Smith | Los Angeles |
| bob_wilson | bobpass789 | Bob Wilson | Chicago |
| alice_brown | alicepass012 | Alice Brown | Houston |
| charlie_davis | charliepass345 | Charlie Davis | Phoenix |

### Trains (5 records)
| Train No | Train Name | Route | Date | Cost |
|----------|------------|-------|------|------|
| T001 | Express East | New York → Chicago | 2025-11-25 | $89.99 |
| T002 | Western Flyer | Los Angeles → Phoenix | 2025-11-26 | $65.50 |
| T003 | Southern Cross | Chicago → Houston | 2025-11-27 | $112.75 |
| T004 | Coast to Coast | New York → Los Angeles | 2025-11-28 | $245.00 |
| T005 | Midwest Express | Chicago → Phoenix | 2025-11-29 | $135.25 |

### Bookings (5 records)
Each user has one booking linked to the corresponding train.

### Stations (5 records)
- Grand Central Station (New York)
- Union Station LA (Los Angeles)
- Chicago Union Station (Chicago)
- Houston Central (Houston)
- Phoenix Station (Phoenix)

---

## Database Schema

```
User (PK: Username)
  ├── Login (FK: Username)
  └── Passenger (FK: Username)
        └── Ticket (FK: PNR, TrainNumber)
              └── Train (PK: TrainNumber)
                    └── Schedule (FK: TrainNumber, StationName)
                          └── Station (PK: StationName)

Admin (PK: AdminID)
  ├── Admin_Station (FK: AdminID, StationName)
  └── Admin_Train (FK: AdminID, TrainNumber)
```

---

## Troubleshooting

### Error: "Database already exists"

**Solution:** The script handles this. It uses:
- SQL Server: `IF NOT EXISTS` check
- MySQL: `CREATE DATABASE IF NOT EXISTS`

If you want to start fresh:

**SQL Server:**
```sql
DROP DATABASE RailwaySystem;
-- Then re-run database_setup.sql
```

**MySQL:**
```sql
DROP DATABASE railway_system;
-- Then re-run database_setup_mysql.sql
```

### Error: "Cannot drop database because it is currently in use"

**Solution:**

**SQL Server:**
```sql
USE master;
ALTER DATABASE RailwaySystem SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE RailwaySystem;
```

**MySQL:**
```sql
-- Close all connections to the database first
DROP DATABASE railway_system;
```

### Error: Foreign key constraint fails

**Cause:** Tables are being dropped in wrong order

**Solution:** The script already handles this by dropping in reverse dependency order. Make sure you run the entire script, not individual statements.

### Error: Syntax error near 'GO'

**Cause:**
- In SQL Server: Normal, `GO` is a batch separator
- In MySQL: `GO` is not valid

**Solution:** Use the correct script for your database:
- SQL Server → `database_setup.sql`
- MySQL → `database_setup_mysql.sql`

### Error: Invalid object name '[User]'

**Cause:** Table doesn't exist yet

**Solution:** Make sure the CREATE TABLE statements executed successfully before INSERT statements.

---

## Key Differences: SQL Server vs MySQL

| Feature | SQL Server | MySQL |
|---------|-----------|-------|
| **Database Name** | `RailwaySystem` | `railway_system` |
| **Script File** | `database_setup.sql` | `database_setup_mysql.sql` |
| **Batch Separator** | `GO` | Not needed |
| **User Table** | `[User]` (brackets needed) | `User` (no brackets) |
| **Auto-increment** | `IDENTITY(1,1)` | `AUTO_INCREMENT` |
| **Date Functions** | `GETDATE()` | `NOW()` |
| **Print Message** | `PRINT` | `SELECT ... AS Message` |

---

## Connection Strings

After database setup, update `src/main/resources/database.properties`:

### SQL Server:
```properties
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
db.url=jdbc:sqlserver://localhost:1433;databaseName=RailwaySystem;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=YOUR_PASSWORD
```

### MySQL:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/railway_system?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD
```

---

## Next Steps

After database setup:

1. ✅ Verify all tables exist (see "Verify Installation" above)
2. ✅ Check sample data is inserted
3. ✅ Configure `database.properties` with your credentials
4. ✅ Test connection: Run `DatabaseTest.java`
5. ✅ Start the application

---

**Quick Commands:**

**SQL Server:**
```bash
# Open SSMS → Connect → Open database_setup.sql → Execute (F5)
```

**MySQL:**
```bash
mysql -u root -p < database_setup_mysql.sql
```

**Verify:**
```sql
-- SQL Server
USE RailwaySystem;
SELECT COUNT(*) FROM [User];

-- MySQL
USE railway_system;
SELECT COUNT(*) FROM User;
```

---

**Status:** Database setup scripts ready
**Files:**
- `database_setup.sql` (SQL Server)
- `database_setup_mysql.sql` (MySQL)
