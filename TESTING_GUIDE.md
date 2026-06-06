# Testing Guide - Railway Booking System

## Important: Testing Before Deployment

⚠️ **WARNING**: The code has been created but **NOT TESTED** against a live database yet.

You **MUST** perform the following tests before using this system in production.

## Prerequisites for Testing

1. **Java JDK 11+** installed
2. **Maven** installed
3. **SQL Server** or **MySQL** installed and running
4. **Database created** and **schema loaded**

## Test Setup Steps

### Step 1: Database Setup

#### For SQL Server:
```sql
-- Open SQL Server Management Studio (SSMS)
-- Connect to localhost

-- Create database
CREATE DATABASE RailwaySystem;
GO

-- Use the database
USE RailwaySystem;
GO

-- Execute the database_setup.sql file
-- This will create all tables and insert seed data
```

#### For MySQL:
```sql
-- Open MySQL Workbench
-- Connect to localhost

-- Create database
CREATE DATABASE railway_system;

-- Use the database
USE railway_system;

-- Execute the database_setup.sql file
-- Note: You may need to adjust some SQL Server-specific syntax
```

### Step 2: Configure Database Connection

Edit `src/main/resources/database.properties` with YOUR actual database credentials:

**For SQL Server:**
```properties
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
db.url=jdbc:sqlserver://localhost:1433;databaseName=RailwaySystem;encrypt=true;trustServerCertificate=true
db.username=YOUR_ACTUAL_USERNAME
db.password=YOUR_ACTUAL_PASSWORD
```

**For MySQL:**
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/railway_system?useSSL=false&serverTimezone=UTC
db.username=YOUR_ACTUAL_USERNAME
db.password=YOUR_ACTUAL_PASSWORD
```

### Step 3: Compile the Project

```bash
cd d:\TrainSystem\RailwaySystem
mvn clean compile
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

If you see errors, check:
- Java is installed: `java -version`
- Maven is installed: `mvn -version`
- All dependencies are downloaded

### Step 4: Run Database Test

```bash
mvn exec:java -Dexec.mainClass="com.railway.test.DatabaseTest"
```

**Expected Output:**
```
═══════════════════════════════════════════════
  DATABASE CONNECTION & API TEST
═══════════════════════════════════════════════

TEST 1: Database Connection
─────────────────────────────────────────────
✓ Database connection successful!

TEST 2: Train Service - Get All Trains
─────────────────────────────────────────────
✓ Successfully retrieved 5 trains from database

Sample Train Data:
  Train Number: T001
  Train Name: Express East
  Source: New York
  Destination: Chicago
  Date: 2025-11-25
  Cost: ₹89.99
  Available Seats: 99

TEST 3: User Service - Get All Users
─────────────────────────────────────────────
✓ Successfully retrieved 5 users from database

Sample User Data:
  Username: john_doe
  Name: John Doe
  City: New York
  Age: 30
  Contact: 555-0101

TEST 4: Booking Service - Get All Bookings
─────────────────────────────────────────────
✓ Successfully retrieved 5 bookings from database

Sample Booking Data:
  PNR: PNR001
  Train Number: T001
  Date: 2025-11-25
  Source: New York
  Destination: Chicago
  Amount: ₹89.99
  Passenger Name: John Doe

TEST 5: Feedback Service - Get All Feedback
─────────────────────────────────────────────
✓ Successfully retrieved 0 feedback entries from database
⚠ No feedback found in database. This is normal for a new installation.
  Feedback table is created automatically but empty.

═══════════════════════════════════════════════
  TEST COMPLETED
═══════════════════════════════════════════════
```

## Manual Testing Checklist

### ✅ Test 1: Database Connection

**Test Command:**
```bash
mvn exec:java -Dexec.mainClass="com.railway.test.DatabaseTest"
```

**What to Check:**
- [ ] Database connection succeeds
- [ ] No connection errors
- [ ] Correct database is accessed

**Common Issues:**
| Issue | Solution |
|-------|----------|
| Connection refused | Start database server |
| Invalid credentials | Update database.properties |
| Database not found | Create database first |
| Driver not found | Run `mvn clean install` |

---

### ✅ Test 2: Train APIs

**Manual Test via Main Application:**
```bash
mvn exec:java -Dexec.mainClass="com.railway.RailwayBookingSystem"
# Choose: 3. Admin Login
# Username: admin
# Password: admin123
# Choose: 4. View All Trains
```

**Expected Result:**
- Should display 5 trains from seed data
- Should show train details correctly
- Available seats should be calculated

**What to Verify:**
- [ ] All 5 trains are displayed
- [ ] Train numbers: T001, T002, T003, T004, T005
- [ ] Source/Destination are correct
- [ ] Costs are displayed
- [ ] Available seats shown (should be 99 for each)

---

### ✅ Test 3: User Registration & Login

**Test Registration:**
```bash
# Run application
# Choose: 2. Passenger Registration
# Enter test data:
Username: testuser
Password: test123
Confirm Password: test123
Full Name: Test User
Address: 123 Test St
City: Test City
Age: 25
Contact: 555-9999
Gender: M
```

**Expected Result:**
- [ ] Registration succeeds
- [ ] User added to database
- [ ] Can login with new credentials

**Test Login:**
```bash
# Choose: 1. Passenger Login
# Username: testuser
# Password: test123
```

**Expected Result:**
- [ ] Login succeeds
- [ ] Welcome message with user name
- [ ] Passenger menu appears

---

### ✅ Test 4: Train Search

**Test Search:**
```bash
# Login as passenger
# Choose: 1. Search Trains
# Enter: Source: New York
# Enter: Destination: Chicago
# Enter: Date: 2025-11-25
```

**Expected Result:**
- [ ] Shows train T001 (Express East)
- [ ] Displays correct route
- [ ] Shows available seats
- [ ] Cost displayed correctly

**Edge Cases to Test:**
- [ ] Search with only source
- [ ] Search with only destination
- [ ] Search with only date
- [ ] Search with no results

---

### ✅ Test 5: Ticket Booking

**Test Booking:**
```bash
# Login as passenger (testuser)
# Choose: 2. Book Ticket
# Enter: Train Number: T001
# Enter: Passenger Name: Test User
# Enter: Age: 25
# Enter: Gender: M
# Enter: Boarding: New York
# Enter: Destination: Chicago
```

**Expected Result:**
- [ ] Booking succeeds
- [ ] PNR generated (format: PNR + timestamp)
- [ ] Ticket details displayed
- [ ] Amount shows ₹89.99
- [ ] Available seats decreased by 1

**Database Verification:**
```sql
-- Check Passenger table
SELECT * FROM Passenger WHERE Username = 'testuser';

-- Check Ticket table
SELECT * FROM Ticket WHERE PNR = 'YOUR_PNR';

-- Verify seat count
SELECT COUNT(*) as BookedSeats FROM Ticket WHERE TrainNumber = 'T001';
```

---

### ✅ Test 6: View Bookings

**Test:**
```bash
# Login as passenger (testuser)
# Choose: 3. View My Bookings
```

**Expected Result:**
- [ ] Shows all bookings for logged-in user
- [ ] Displays PNR, train number, date
- [ ] Shows source and destination
- [ ] Amount displayed correctly

---

### ✅ Test 7: Cancel Ticket

**Test:**
```bash
# Login as passenger (testuser)
# Choose: 4. Cancel Ticket
# Enter: PNR: YOUR_PNR
# Confirm: yes
```

**Expected Result:**
- [ ] Shows ticket details before cancellation
- [ ] Cancellation succeeds
- [ ] Refund amount displayed
- [ ] Ticket removed from database
- [ ] Passenger record removed

**Database Verification:**
```sql
-- Should return 0 rows
SELECT * FROM Ticket WHERE PNR = 'YOUR_PNR';
SELECT * FROM Passenger WHERE PNR = 'YOUR_PNR';
```

---

### ✅ Test 8: Seat Availability

**Test:**
```bash
# Login as passenger
# Choose: 5. Check Seat Availability
# Enter: Train Number: T001
```

**Expected Result:**
- [ ] Shows train details
- [ ] Displays available seats (should be less than 100 if bookings exist)
- [ ] Status shows AVAILABLE or FULL

**Calculation Verification:**
- Total seats: 100
- Booked seats: Query count from Ticket table
- Available = 100 - Booked

---

### ✅ Test 9: Submit Feedback

**Test:**
```bash
# Login as passenger (testuser)
# Choose: 6. Submit Feedback
# Enter: Train Number: T001
# Enter: Rating: 5
# Enter: Comments: Excellent service!
```

**Expected Result:**
- [ ] Feedback submitted successfully
- [ ] Feedback ID generated
- [ ] Status set to PENDING

**Database Verification:**
```sql
SELECT * FROM Feedback WHERE Username = 'testuser';
```

---

### ✅ Test 10: View Feedback (Passenger)

**Test:**
```bash
# Login as passenger (testuser)
# Choose: 7. View My Feedback
```

**Expected Result:**
- [ ] Shows all feedback submitted by user
- [ ] Displays rating with stars
- [ ] Shows comments
- [ ] Shows status (PENDING/RESPONDED)
- [ ] Shows admin response if available

---

### ✅ Test 11: Admin - Add Train

**Test:**
```bash
# Login as admin
# Choose: 1. Add Train
# Enter details:
Train Number: T999
Train Name: Test Express
Source: Test City A
Destination: Test City B
Date: 2025-12-01
Cost: 150.00
```

**Expected Result:**
- [ ] Train added successfully
- [ ] Appears in train list
- [ ] Can be searched by passengers

**Database Verification:**
```sql
SELECT * FROM Train WHERE TrainNumber = 'T999';
```

---

### ✅ Test 12: Admin - Update Train

**Test:**
```bash
# Login as admin
# Choose: 2. Update Train
# Enter: Train Number: T999
# Update fields as needed
```

**Expected Result:**
- [ ] Shows current train details
- [ ] Updates succeed
- [ ] Changes reflected in database

---

### ✅ Test 13: Admin - Delete Train

**Test:**
```bash
# Login as admin
# Choose: 3. Delete Train
# Enter: Train Number: T999
# Confirm: yes
```

**Expected Result:**
- [ ] Shows train details before deletion
- [ ] Deletion succeeds
- [ ] Train removed from database
- [ ] Related bookings also removed (CASCADE)

---

### ✅ Test 14: Admin - View All Bookings

**Test:**
```bash
# Login as admin
# Choose: 5. View All Bookings
```

**Expected Result:**
- [ ] Shows all bookings from all users
- [ ] Displays PNR, train, date, route
- [ ] Shows amounts

---

### ✅ Test 15: Admin - View Pending Feedback

**Test:**
```bash
# Login as admin
# Choose: 6. View Pending Feedback
```

**Expected Result:**
- [ ] Shows only PENDING feedback
- [ ] Displays feedback ID, user, rating, comments
- [ ] No RESPONDED feedback shown

---

### ✅ Test 16: Admin - Respond to Feedback

**Test:**
```bash
# Login as admin
# Choose: 8. Respond to Feedback
# Enter: Feedback ID: (from pending list)
# Enter: Response: Thank you for your feedback!
```

**Expected Result:**
- [ ] Shows feedback details
- [ ] Response submitted successfully
- [ ] Status changes to RESPONDED
- [ ] Response visible to passenger

**Passenger Verification:**
```bash
# Login as passenger (who submitted feedback)
# Choose: 7. View My Feedback
# Should see admin response
```

---

### ✅ Test 17: Admin - View Train Ratings

**Test:**
```bash
# Login as admin
# Choose: 9. View Train Ratings
```

**Expected Result:**
- [ ] Shows all trains with average ratings
- [ ] Rating calculated from all feedback
- [ ] Shows "No ratings" for trains without feedback

**Calculation Verification:**
```sql
SELECT TrainNumber, AVG(CAST(Rating AS FLOAT)) as AvgRating
FROM Feedback
GROUP BY TrainNumber;
```

---

## Integration Tests

### Test Scenario 1: Complete Booking Flow
1. [ ] Register new user
2. [ ] Login
3. [ ] Search trains
4. [ ] Check seat availability
5. [ ] Book ticket
6. [ ] View booking
7. [ ] Submit feedback
8. [ ] Logout

### Test Scenario 2: Admin Management Flow
1. [ ] Login as admin
2. [ ] Add new train
3. [ ] View all trains
4. [ ] View all bookings
5. [ ] Check pending feedback
6. [ ] Respond to feedback
7. [ ] View train ratings
8. [ ] Update train
9. [ ] Logout

### Test Scenario 3: Cancellation Flow
1. [ ] Login as passenger
2. [ ] Book ticket
3. [ ] View booking (verify it exists)
4. [ ] Cancel ticket
5. [ ] View bookings (should not show cancelled ticket)
6. [ ] Check seat availability (should increase by 1)

---

## Error Handling Tests

### Test Invalid Login
```bash
# Try: Invalid username/password
Expected: Error message, no access
```

### Test Duplicate Username Registration
```bash
# Try: Register with existing username
Expected: Error about username already exists
```

### Test Book Non-existent Train
```bash
# Try: Book ticket for train number that doesn't exist
Expected: Error "Train not found"
```

### Test Book Full Train
```bash
# Try: Book ticket when all 100 seats are taken
Expected: Error "No seats available"
```

### Test Cancel Other User's Ticket
```bash
# Try: User A tries to cancel User B's ticket
Expected: Error "You can only cancel your own tickets"
```

### Test Invalid Feedback Rating
```bash
# Try: Submit rating of 0 or 6
Expected: Error "Rating must be between 1 and 5"
```

---

## Performance Tests

### Test Large Data Volume
1. Insert 100+ trains
2. Insert 1000+ bookings
3. Test search performance
4. Test view all bookings performance

### Test Concurrent Bookings
1. Multiple users book same train simultaneously
2. Verify seat count accuracy
3. Check for race conditions

---

## Database Integrity Tests

### Test CASCADE Deletes
```sql
-- Delete a user
DELETE FROM [User] WHERE Username = 'testuser';

-- Verify cascades:
-- Login record should be deleted
-- Passenger records should be deleted
-- Ticket records should be deleted
-- Feedback records should be deleted
```

### Test Referential Integrity
```sql
-- Try to insert Ticket without Passenger (should fail)
INSERT INTO Ticket (PNR, TrainNumber, Date, Source, Destination)
VALUES ('INVALID', 'T001', '2025-12-01', 'A', 'B');
-- Expected: Foreign key constraint error
```

---

## Known Limitations to Test

1. **Password Security**: Passwords stored in plain text (needs encryption)
2. **Session Management**: No session timeout
3. **Concurrent Booking**: No locking mechanism for seat booking
4. **File Upload**: Document upload not fully implemented
5. **Email**: No email notifications

---

## Test Results Template

Create a file `TEST_RESULTS.txt`:

```
===========================================
RAILWAY BOOKING SYSTEM - TEST RESULTS
===========================================
Date: ___________
Tester: ___________
Database: SQL Server / MySQL (circle one)

DATABASE CONNECTION
[ ] PASS  [ ] FAIL  Connection established
Notes: _________________________________

TRAIN APIS
[ ] PASS  [ ] FAIL  Get all trains
[ ] PASS  [ ] FAIL  Search trains
[ ] PASS  [ ] FAIL  Add train (admin)
[ ] PASS  [ ] FAIL  Update train (admin)
[ ] PASS  [ ] FAIL  Delete train (admin)
Notes: _________________________________

USER APIS
[ ] PASS  [ ] FAIL  User registration
[ ] PASS  [ ] FAIL  User login
[ ] PASS  [ ] FAIL  Get user profile
Notes: _________________________________

BOOKING APIS
[ ] PASS  [ ] FAIL  Book ticket
[ ] PASS  [ ] FAIL  Cancel ticket
[ ] PASS  [ ] FAIL  View my bookings
[ ] PASS  [ ] FAIL  View all bookings (admin)
Notes: _________________________________

FEEDBACK APIS
[ ] PASS  [ ] FAIL  Submit feedback
[ ] PASS  [ ] FAIL  View my feedback
[ ] PASS  [ ] FAIL  View all feedback (admin)
[ ] PASS  [ ] FAIL  Respond to feedback (admin)
Notes: _________________________________

OVERALL RESULT
[ ] ALL TESTS PASSED
[ ] SOME TESTS FAILED (see notes)

===========================================
```

---

## Next Steps After Testing

1. If all tests pass: ✅ System is ready for use
2. If tests fail: 🔧 Debug and fix issues
3. Document any bugs found
4. Implement missing features (password encryption, etc.)
5. Consider adding JUnit automated tests
6. Set up CI/CD pipeline

---

**IMPORTANT**: Do not skip testing! This ensures data integrity and proper functionality.
