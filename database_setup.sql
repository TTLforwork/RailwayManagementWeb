USE master;
GO

-- 1. Force-Close connections and DROP the database
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'RailwaySystem')
BEGIN
    -- Force roll back of all open transactions to allow drop
    ALTER DATABASE RailwaySystem SET SINGLE_USER WITH ROLLBACK IMMEDIATE; 
    DROP DATABASE RailwaySystem;
END
GO

-- 2. Create a Fresh Database
CREATE DATABASE RailwaySystem;
GO
USE RailwaySystem;
GO

-- ========================================
-- DROP EXISTING TABLES (ENSURE CLEAN SLATE)
-- ========================================
-- Drop tables in reverse dependency order (This is crucial for idempotency)

IF OBJECT_ID('Feedback') IS NOT NULL DROP TABLE Feedback;
IF OBJECT_ID('Ticket') IS NOT NULL DROP TABLE Ticket;
IF OBJECT_ID('Admin_Train') IS NOT NULL DROP TABLE Admin_Train;
IF OBJECT_ID('Schedule') IS NOT NULL DROP TABLE Schedule;
IF OBJECT_ID('Admin_Station') IS NOT NULL DROP TABLE Admin_Station;
IF OBJECT_ID('Train') IS NOT NULL DROP TABLE Train;
IF OBJECT_ID('Station') IS NOT NULL DROP TABLE Station;
IF OBJECT_ID('Passenger') IS NOT NULL DROP TABLE Passenger;
IF OBJECT_ID('Login') IS NOT NULL DROP TABLE Login;
IF OBJECT_ID('Admin') IS NOT NULL DROP TABLE Admin;
IF OBJECT_ID('[User]') IS NOT NULL DROP TABLE [User];
GO

-- ========================================
-- TABLE CREATION (100% Normalized)
-- ========================================

-- Create User table (SECURE: WITH Role Column)
CREATE TABLE [User] (
    Username VARCHAR(50) PRIMARY KEY NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Address VARCHAR(255),
    City VARCHAR(80),
    Age INT,
    Contact VARCHAR(15),
    Gender CHAR(1),
    Role VARCHAR(20) NOT NULL DEFAULT 'passenger' CHECK (Role IN ('passenger', 'admin')) 
);

-- Create Login table
CREATE TABLE Login (
    Username VARCHAR(50) PRIMARY KEY NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FOREIGN KEY (Username) REFERENCES [User](Username) ON DELETE CASCADE
);

-- Create Passenger table (NORMALIZED: Removed Source/Dest/Date. PNR widened for resilience.)
CREATE TABLE Passenger (
    PNR VARCHAR(50) PRIMARY KEY NOT NULL, 
    PassengerName VARCHAR(100) NOT NULL,
    Age INT,
    Gender CHAR(1),
    Username VARCHAR(50) NOT NULL,
    FOREIGN KEY (Username) REFERENCES [User](Username) ON DELETE CASCADE
);

-- Create Train table
CREATE TABLE Train (
    TrainNumber VARCHAR(10) PRIMARY KEY NOT NULL,
    TrainName VARCHAR(100) NOT NULL,
    Source VARCHAR(100),
    Destination VARCHAR(100),
    Date DATE,
    Cost DECIMAL(10,2),
    Seats INT DEFAULT 100 
);

-- Create Ticket table (NORMALIZED: Removed Source/Dest/Date/Amount, now only stores PNR and Train reference)
CREATE TABLE Ticket (
    PNR VARCHAR(50) NOT NULL, 
    TrainNumber VARCHAR(10) NOT NULL,
    Status VARCHAR(20) DEFAULT 'CONFIRMED',
    BookingDate DATETIME DEFAULT GETDATE(),
    BoardingScheduleID INT,
    DeboardingScheduleID INT,
    PRIMARY KEY (PNR, TrainNumber),
    FOREIGN KEY (PNR) REFERENCES Passenger(PNR) ON DELETE CASCADE,
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber) ON DELETE CASCADE
);

-- Create Admin table
CREATE TABLE Admin (
    Username VARCHAR(50) PRIMARY KEY NOT NULL,
    AdminID INT UNIQUE NOT NULL,
    FOREIGN KEY (Username) REFERENCES [User](Username) ON DELETE CASCADE
);

-- Create Station table
CREATE TABLE Station (
    -- FIX: Corrected syntax from 'PRIMARY IS NULL' to 'PRIMARY KEY'
    StationName VARCHAR(100) PRIMARY KEY NOT NULL,
    Address VARCHAR(255)
);

-- Create Admin_Station junction table
CREATE TABLE Admin_Station (
    Username VARCHAR(50) NOT NULL,
    StationName VARCHAR(100) NOT NULL,
    PRIMARY KEY (Username, StationName),
    FOREIGN KEY (Username) REFERENCES Admin(Username) ON DELETE CASCADE,
    FOREIGN KEY (StationName) REFERENCES Station(StationName) ON DELETE CASCADE
);

-- Create Schedule table
CREATE TABLE Schedule (
    TrainNumber VARCHAR(10) NOT NULL,
    StationName VARCHAR(100) NOT NULL,
    ScheduleID VARCHAR(10),
    arrd_arrival_time TIME,
    dept_departure_time TIME,
    PRIMARY KEY (TrainNumber, StationName),
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber) ON DELETE CASCADE,
    FOREIGN KEY (StationName) REFERENCES Station(StationName) ON DELETE CASCADE
);

-- Create Admin_Train junction table
CREATE TABLE Admin_Train (
    Username VARCHAR(50) NOT NULL,
    TrainNumber VARCHAR(10) NOT NULL,
    PRIMARY KEY (Username, TrainNumber),
    FOREIGN KEY (Username) REFERENCES Admin(Username) ON DELETE CASCADE,
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber) ON DELETE CASCADE
);

-- Create Feedback table
CREATE TABLE Feedback (
    FeedbackID INT PRIMARY KEY IDENTITY(1,1),
    Username VARCHAR(50) NOT NULL,
    TrainNumber VARCHAR(10) NOT NULL,
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment VARCHAR(500),
    AdminResponse VARCHAR(500),
    DateSubmitted DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (Username) REFERENCES [User](Username),
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber)
);

GO

-- ========================================
-- SEED DATA (Extended with MORE realistic data)
-- ========================================

-- Insert ALL Users (PASSENGERS & ADMINS)
INSERT INTO [User] (Username, Name, Address, City, Age, Contact, Gender, Role) VALUES
-- Passengers
('john_doe', 'John Doe', '123 Main St', 'New York', 30, '555-0101', 'M', 'passenger'),
('jane_smith', 'Jane Smith', '456 Oak Ave', 'Los Angeles', 28, '555-0102', 'F', 'passenger'),
('mike_johnson', 'Mike Johnson', '789 Pine Rd', 'Chicago', 35, '555-0103', 'M', 'passenger'),
('sarah_williams', 'Sarah Williams', '321 Elm St', 'Houston', 26, '555-0104', 'F', 'passenger'),
('david_brown', 'David Brown', '654 Maple Dr', 'Phoenix', 42, '555-0105', 'M', 'passenger'),
('emily_davis', 'Emily Davis', '987 Cedar Ln', 'Philadelphia', 31, '555-0106', 'F', 'passenger'),
('robert_miller', 'Robert Miller', '147 Birch Ave', 'San Antonio', 38, '555-0107', 'M', 'passenger'),
('lisa_wilson', 'Lisa Wilson', '258 Spruce St', 'San Diego', 29, '555-0108', 'F', 'passenger'),
('james_moore', 'James Moore', '369 Willow Way', 'Dallas', 33, '555-0109', 'M', 'passenger'),
('maria_taylor', 'Maria Taylor', '741 Ash Ct', 'San Jose', 27, '555-0110', 'F', 'passenger'),
('william_anderson', 'William Anderson', '852 Poplar Pl', 'Austin', 36, '555-0111', 'M', 'passenger'),
('jennifer_thomas', 'Jennifer Thomas', '963 Hickory Blvd', 'Jacksonville', 32, '555-0112', 'F', 'passenger'),
('charles_jackson', 'Charles Jackson', '159 Sycamore Dr', 'Fort Worth', 40, '555-0113', 'M', 'passenger'),
('patricia_white', 'Patricia White', '357 Magnolia Ave', 'Columbus', 34, '555-0114', 'F', 'passenger'),
('christopher_harris', 'Christopher Harris', '753 Dogwood Ln', 'Charlotte', 37, '555-0115', 'M', 'passenger'),
-- Admins
('admin_ny', 'Admin New York', '100 Admin Plaza', 'New York', 40, '555-1001', 'M', 'admin'),
('admin_la', 'Admin Los Angeles', '200 Admin Tower', 'Los Angeles', 45, '555-1002', 'F', 'admin'),
('admin_chi', 'Admin Chicago', '300 Admin Center', 'Chicago', 38, '555-1003', 'M', 'admin'),
('admin_hou', 'Admin Houston', '400 Admin Building', 'Houston', 42, '555-1004', 'F', 'admin'),
('admin_phx', 'Admin Phoenix', '500 Admin Suite', 'Phoenix', 39, '555-1005', 'M', 'admin');

-- Insert Login credentials 
INSERT INTO Login (Username, Password) VALUES
-- Passengers
('john_doe', 'password123'),
('jane_smith', 'securepass456'),
('mike_johnson', 'mike2025'),
('sarah_williams', 'sarah@pass'),
('david_brown', 'david123'),
('emily_davis', 'emily456'),
('robert_miller', 'robert789'),
('lisa_wilson', 'lisa2025'),
('james_moore', 'james123'),
('maria_taylor', 'maria456'),
('william_anderson', 'william789'),
('jennifer_thomas', 'jennifer2025'),
('charles_jackson', 'charles123'),
('patricia_white', 'patricia456'),
('christopher_harris', 'christopher789'),
-- Admins
('admin_ny', 'admin123'),
('admin_la', 'admin456'),
('admin_chi', 'admin789'),
('admin_hou', 'admin2025'),
('admin_phx', 'admin321');

-- Insert sample Trains (More realistic routes)
INSERT INTO Train (TrainNumber, TrainName, Source, Destination, Date, Cost, Seats) VALUES
('T001', 'Express East', 'New York', 'Chicago', '2025-11-25', 89.99, 100),
('T002', 'Western Flyer', 'Los Angeles', 'Phoenix', '2025-11-26', 65.50, 120),
('T003', 'Southern Star', 'Houston', 'San Antonio', '2025-11-27', 45.00, 80),
('T004', 'Coastal Express', 'San Diego', 'Los Angeles', '2025-11-28', 35.00, 150),
('T005', 'Northeast Corridor', 'New York', 'Philadelphia', '2025-11-29', 55.00, 200),
('T006', 'Midwest Runner', 'Chicago', 'Dallas', '2025-11-30', 95.00, 110),
('T007', 'Pacific Coast', 'San Francisco', 'San Diego', '2025-12-01', 120.00, 90),
('T008', 'Texas Express', 'Dallas', 'Houston', '2025-12-02', 68.00, 130),
('T009', 'Mountain View', 'Phoenix', 'Denver', '2025-12-03', 105.00, 100),
('T010', 'Atlantic Express', 'Philadelphia', 'Jacksonville', '2025-12-04', 125.00, 95),
('T011', 'Capital Link', 'New York', 'Washington DC', '2025-12-05', 75.00, 180),
('T012', 'Sunshine Special', 'Miami', 'Orlando', '2025-12-06', 50.00, 140),
('T013', 'Desert Runner', 'Phoenix', 'Las Vegas', '2025-12-07', 85.00, 110),
('T014', 'Lake Shore Limited', 'Chicago', 'Cleveland', '2025-12-08', 70.00, 125),
('T015', 'Gulf Coast Express', 'Houston', 'New Orleans', '2025-12-09', 80.00, 115);

-- Insert sample Passengers (more realistic data)
INSERT INTO Passenger (PNR, PassengerName, Age, Gender, Username) VALUES
('PNR001', 'John Doe', 30, 'M', 'john_doe'),
('PNR002', 'Jane Smith', 28, 'F', 'jane_smith'),
('PNR003', 'Mike Johnson', 35, 'M', 'mike_johnson'),
('PNR004', 'Sarah Williams', 26, 'F', 'sarah_williams'),
('PNR005', 'David Brown', 42, 'M', 'david_brown'),
('PNR006', 'Emily Davis', 31, 'F', 'emily_davis'),
('PNR007', 'Robert Miller', 38, 'M', 'robert_miller'),
('PNR008', 'Lisa Wilson', 29, 'F', 'lisa_wilson'),
('PNR009', 'James Moore', 33, 'M', 'james_moore'),
('PNR010', 'Maria Taylor', 27, 'F', 'maria_taylor'),
('PNR011', 'William Anderson', 36, 'M', 'william_anderson'),
('PNR012', 'Jennifer Thomas', 32, 'F', 'jennifer_thomas'),
('PNR013', 'Charles Jackson', 40, 'M', 'charles_jackson'),
('PNR014', 'Patricia White', 34, 'F', 'patricia_white'),
('PNR015', 'Christopher Harris', 37, 'M', 'christopher_harris');

-- Insert sample Tickets (connecting passengers to trains)
INSERT INTO Ticket (PNR, TrainNumber, Status, BookingDate) VALUES
('PNR001', 'T001', 'CONFIRMED', '2025-11-20 10:30:00'),
('PNR002', 'T002', 'CONFIRMED', '2025-11-21 14:15:00'),
('PNR003', 'T003', 'CONFIRMED', '2025-11-22 09:00:00'),
('PNR004', 'T004', 'CONFIRMED', '2025-11-23 16:45:00'),
('PNR005', 'T005', 'CANCELLED', '2025-11-24 11:20:00'),
('PNR006', 'T006', 'CONFIRMED', '2025-11-25 08:30:00'),
('PNR007', 'T007', 'CONFIRMED', '2025-11-26 13:00:00'),
('PNR008', 'T008', 'CONFIRMED', '2025-11-27 15:30:00'),
('PNR009', 'T009', 'CONFIRMED', '2025-11-28 10:00:00'),
('PNR010', 'T010', 'CONFIRMED', '2025-11-29 12:45:00'),
('PNR011', 'T011', 'CONFIRMED', '2025-11-30 09:15:00'),
('PNR012', 'T012', 'CONFIRMED', '2025-12-01 14:00:00'),
('PNR013', 'T013', 'CONFIRMED', '2025-12-02 11:30:00'),
('PNR014', 'T014', 'CONFIRMED', '2025-12-03 16:00:00'),
('PNR015', 'T015', 'CONFIRMED', '2025-12-04 10:45:00');

-- Insert sample Admins
INSERT INTO Admin (Username, AdminID) VALUES 
('admin_ny', 1),
('admin_la', 2),
('admin_chi', 3),
('admin_hou', 4),
('admin_phx', 5);

-- Insert sample Stations
INSERT INTO Station (StationName, Address) VALUES
('New York Penn Station', '8th Ave & 33rd St, New York, NY 10001'),
('Chicago Union Station', '225 S Canal St, Chicago, IL 60606'),
('Los Angeles Union Station', '800 N Alameda St, Los Angeles, CA 90012'),
('Phoenix Sky Harbor', '3400 E Sky Harbor Blvd, Phoenix, AZ 85034'),
('Houston Central', '902 Washington Ave, Houston, TX 77002'),
('Philadelphia 30th Street', '2955 Market St, Philadelphia, PA 19104'),
('San Antonio Station', '350 Hoefgen Ave, San Antonio, TX 78205'),
('San Diego Santa Fe', '1050 Kettner Blvd, San Diego, CA 92101'),
('Dallas Union Station', '400 S Houston St, Dallas, TX 75202'),
('San Jose Diridon', '65 Cahill St, San Jose, CA 95110'),
('Austin Station', '250 N Lamar Blvd, Austin, TX 78703'),
('Jacksonville Station', '3570 Clifford Ln, Jacksonville, FL 32209'),
('Fort Worth Central', '1001 Jones St, Fort Worth, TX 76102'),
('Columbus Union', '4300 International Gateway, Columbus, OH 43219'),
('Charlotte Gateway', '1914 N Tryon St, Charlotte, NC 28206');

-- Insert Admin-Station relationships
INSERT INTO Admin_Station (Username, StationName) VALUES
('admin_ny', 'New York Penn Station'),
('admin_ny', 'Philadelphia 30th Street'),
('admin_la', 'Los Angeles Union Station'),
('admin_la', 'San Diego Santa Fe'),
('admin_chi', 'Chicago Union Station'),
('admin_hou', 'Houston Central'),
('admin_hou', 'San Antonio Station'),
('admin_phx', 'Phoenix Sky Harbor');

-- Insert sample Schedules (train stops with arrival/departure times)
INSERT INTO Schedule (TrainNumber, StationName, ScheduleID, arrd_arrival_time, dept_departure_time) VALUES
-- T001: New York to Chicago
('T001', 'New York Penn Station', 'S001', '08:00:00', '08:30:00'),
('T001', 'Philadelphia 30th Street', 'S002', '10:00:00', '10:15:00'),
('T001', 'Chicago Union Station', 'S003', '18:00:00', '18:30:00'),
-- T002: Los Angeles to Phoenix
('T002', 'Los Angeles Union Station', 'S004', '09:00:00', '09:30:00'),
('T002', 'Phoenix Sky Harbor', 'S005', '15:00:00', '15:30:00'),
-- T003: Houston to San Antonio
('T003', 'Houston Central', 'S006', '07:00:00', '07:30:00'),
('T003', 'San Antonio Station', 'S007', '11:00:00', '11:30:00'),
-- T005: New York to Philadelphia
('T005', 'New York Penn Station', 'S008', '12:00:00', '12:30:00'),
('T005', 'Philadelphia 30th Street', 'S009', '14:00:00', '14:30:00'),
-- T006: Chicago to Dallas
('T006', 'Chicago Union Station', 'S010', '10:00:00', '10:30:00'),
('T006', 'Dallas Union Station', 'S011', '20:00:00', '20:30:00');

-- Insert Admin-Train relationships
INSERT INTO Admin_Train (Username, TrainNumber) VALUES
('admin_ny', 'T001'),
('admin_ny', 'T005'),
('admin_ny', 'T011'),
('admin_la', 'T002'),
('admin_la', 'T004'),
('admin_la', 'T007'),
('admin_chi', 'T001'),
('admin_chi', 'T006'),
('admin_chi', 'T014'),
('admin_hou', 'T003'),
('admin_hou', 'T008'),
('admin_hou', 'T015'),
('admin_phx', 'T002'),
('admin_phx', 'T009'),
('admin_phx', 'T013');

-- Insert sample Feedback
INSERT INTO Feedback (Username, TrainNumber, Rating, Comment, AdminResponse, DateSubmitted) VALUES
('john_doe', 'T001', 5, 'Excellent service! Very comfortable journey.', 'Thank thank you for your positive feedback!', '2025-11-21 10:00:00'),
('jane_smith', 'T002', 4, 'Good experience overall, but seats could be more comfortable.', 'We appreciate your feedback and will work on improvements.', '2025-11-22 11:30:00'),
('mike_johnson', 'T003', 3, 'Average service. Train was delayed by 30 minutes.', 'We apologize for the delay. We are working on better scheduling.', '2025-11-23 14:00:00'),
('sarah_williams', 'T004', 5, 'Perfect! Clean trains and friendly staff.', 'Thank you! We are glad you enjoyed your trip.', '2025-11-24 09:15:00'),
('emily_davis', 'T006', 4, 'Nice journey but food options were limited.', 'Thanks for the suggestion. We will expand our menu.', '2025-11-26 16:00:00'),
('robert_miller', 'T007', 5, 'Amazing scenic route and great service!', 'We are thrilled you enjoyed the journey!', '2025-11-27 12:00:00'),
('lisa_wilson', 'T008', 2, 'Train was overcrowded and uncomfortable.', 'We apologize for the inconvenience. We are adding more seats.', '2025-11-28 15:30:00'),
('james_moore', 'T009', 4, 'Good service but Wi-Fi was not working.', 'Thank you for reporting. Our tech team is addressing this.', '2025-11-29 10:45:00'),
('maria_taylor', 'T010', 5, 'Wonderful experience! Will definitely travel again.', 'Thank you! We look forward to serving you again.', '2025-11-30 13:20:00'),
('william_anderson', 'T011', 3, 'Decent service but expensive for the distance.', 'We appreciate your feedback on pricing.', '2025-12-01 11:00:00');

GO

PRINT '========================================';
PRINT 'SUCCESS: Database created and populated!';
PRINT '========================================';
PRINT 'Database Statistics:';
PRINT '- Users (Passengers): 15';
PRINT '- Users (Admins): 5';
PRINT '- Trains: 15';
PRINT '- Passengers: 15';
PRINT '- Tickets: 15';
PRINT '- Stations: 15';
PRINT '- Feedback Entries: 10';
PRINT '========================================';
GO