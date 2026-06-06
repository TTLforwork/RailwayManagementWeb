# Railway Booking System

A comprehensive Java-based Railway Ticket Booking and Management System with passenger and admin functionalities.

## Features

### For Passengers
- **User Registration & Login**: Secure user authentication system
- **Train Search**: Search trains by source, destination, and date
- **Online Ticket Booking**: Book train tickets with real-time seat availability
- **Booking Management**: View and manage all bookings
- **Ticket Cancellation**: Cancel tickets with automated refund processing
- **Seat Availability**: Check real-time seat availability for trains
- **Feedback System**: Submit ratings and feedback for train services
- **View Feedback History**: Track submitted feedback and admin responses

### For Administration
- **Train Management**: Add, update, and delete train information
- **Route Management**: Manage train routes and schedules
- **Booking Overview**: View all passenger bookings
- **Feedback Management**: View and respond to passenger feedback
- **Train Ratings**: View average ratings for all trains
- **Comprehensive Dashboard**: Monitor system operations

### Document Management
- **Document Upload**: Upload and manage travel documents
- **Passenger Documents**: Link documents to passenger profiles
- **Booking Documents**: Automatic ticket and receipt generation
- **Document Categories**: Support for tickets, receipts, and invoices

## Technology Stack

- **Language**: Java 11+
- **Database**: Microsoft SQL Server / MySQL
- **JDBC**: Database connectivity
- **Build Tool**: Maven
- **Architecture**: Layered (Model, DAO, Service, UI)

## Project Structure

```
Source Packages
│
└── com.railway
    │
    ├── com.railway.controller                    # Handles HTTP requests/responses
    │   ├── BookingController.java                # Manages ticket booking endpoints
    │   ├── FeedbackController.java               # Handles feedback submission
    │   ├── TrainController.java                  # Manages train operations (add/edit/delete)
    │   └── UserController.java                   # User authentication & profile management
    │
    ├── com.railway.dao                           # Database access layer
    │   ├── BookingDAO.java                       # Booking database operations
    │   ├── DocumentDAO.java                      # Document storage/retrieval
    │   ├── FeedbackDAO.java                      # Feedback database operations
    │   ├── TrainDAO.java                         # Train CRUD operations
    │   └── UserDAO.java                          # User database operations
    │
    ├── com.railway.model                         # Entity/Domain classes
    │   ├── Document.java                         # Document entity (ID proofs, etc.)
    │   ├── Feedback.java                         # Feedback entity
    │   ├── Passenger.java                        # Passenger details entity
    │   ├── Schedule.java                         # Train schedule entity
    │   ├── Station.java                          # Station information entity
    │   ├── Ticket.java                           # Ticket/booking entity
    │   ├── Train.java                            # Train details entity
    │   └── User.java                             # User account entity
    │
    ├── com.railway.service                       # Business logic layer
    │   ├── BookingService.java                   # Booking business logic
    │   ├── DocumentService.java                  # Document processing logic
    │   ├── FeedbackService.java                  # Feedback processing logic
    │   ├── TrainService.java                     # Train management logic
    │   └── UserService.java                      # User management logic
    │
    ├── com.railway.test                          # Test files
    │   └── DatabaseTest.java                     # Database connection tests
    │
    ├── com.railway.ui                            # Console/Menu interfaces
    │   ├── AdminMenu.java                        # Admin console menu
    │   └── PassengerMenu.java                    # Passenger console menu
    │
    └── com.railway.util                          # Utility classes
        ├── DatabaseConnection.java               # Database connection manager
        └── PNRGenerator.java                     # Generates unique PNR numbers

Test Packages
└── <default package>

Other Sources
│
└── src/main/resources
    ├── <default package>
    │   ├── application.properties                # Spring Boot configuration
    │   └── database.properties                   # Database credentials & settings
    │
    ├── static                                    # Frontend HTML pages
    │   ├── add_train.html                        # Admin: Add new train form
    │   ├── admin.html                            # Admin login page
    │   ├── admin_feedback.html                   # Admin: View all feedback
    │   ├── admin_hub.html                        # Admin dashboard
    │   ├── bookings.html                         # View booking history
    │   ├── edit_train.html                       # Admin: Edit train details
    │   ├── feedback_submit.html                  # Submit feedback form
    │   ├── index.html                            # Home/landing page
    │   ├── login.html                            # User login page
    │   ├── passenger_dashboard.html              # Passenger main dashboard
    │   ├── profile.html                          # User profile page
    │   └── register.html                         # New user registration
    │
    ├── static.css
    │   └── style.css                             # Global styles for all pages
    │
    └── static.js
        └── app.js                                # Frontend JavaScript logic

Dependencies
Test Dependencies
Java Dependencies
└── JDK 24 (Default)

Project Files
├── pom.xml                                       # Maven dependencies & build config
└── nbactions.xml                                 # NetBeans IDE actions
```

## Database Schema

The system uses the following tables:
- **User**: User account information
- **Login**: Authentication credentials
- **Passenger**: Passenger details for bookings
- **Train**: Train information and routes
- **Ticket**: Booking tickets
- **Station**: Railway station details
- **Schedule**: Train schedules at stations
- **Feedback**: Passenger feedback and ratings
- **Document**: Document metadata
- **PassengerDocument**: Links documents to passengers
- **BookingDocument**: Links documents to bookings
- **Admin**: Admin user information
- **Admin_Station**: Admin-station relationships
- **Admin_Train**: Admin-train relationships

## Setup Instructions

### Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java -version`

2. **Apache Maven**
   - Download from [Maven](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **Database (Choose one)**
   - **Microsoft SQL Server** (Recommended)
     - Download [SQL Server Express](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
     - Install [SQL Server Management Studio (SSMS)](https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms)

   - **OR MySQL**
     - Download [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
     - Install [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)

### Database Setup

#### For SQL Server:

1. Open SQL Server Management Studio (SSMS)
2. Connect to your SQL Server instance
3. Open and execute the **`database_setup.sql`** script
   - This script will automatically:
     - Create the database `RailwaySystem`
     - Create all tables
     - Insert seed data

#### For MySQL:

1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open and execute the **`database_setup_mysql.sql`** script
   - This script will automatically:
     - Create the database `railway_system`
     - Create all tables
     - Insert seed data

### Application Configuration

1. Navigate to `src/main/resources/database.properties`

2. **For SQL Server**, ensure these settings:
   ```properties
   db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
   db.url=jdbc:sqlserver://localhost:1433;databaseName=RailwaySystem;encrypt=true;trustServerCertificate=true
   db.username=sa
   db.password=YourPassword123
   ```

3. **For MySQL**, comment out SQL Server settings and uncomment MySQL settings:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/railway_system?useSSL=false&serverTimezone=UTC
   db.username=root
   db.password=root
   ```

4. Update the username and password to match your database credentials

### Build and Run

1. **Open Terminal/Command Prompt** in the project directory

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.railway.RailwayBookingSystem"
   ```

   Or using Java directly:
   ```bash
   mvn package
   java -cp target/classes;target/dependency/* com.railway.RailwayBookingSystem
   ```

## Usage Guide

### Default Login Credentials

**Admin Login:**
- Username: `admin`
- Password: `admin123`

**Sample Passenger Login:**
- Username: `john_doe`
- Password: `password123`

(Or register a new passenger account)

### Passenger Workflow

1. **Register/Login** to the system
2. **Search Trains** by source, destination, and date
3. **Check Seat Availability** for desired train
4. **Book Ticket** by providing passenger details
5. **View Bookings** to see all your reservations
6. **Cancel Ticket** if needed (using PNR)
7. **Submit Feedback** to rate your experience

### Admin Workflow

1. **Login** with admin credentials
2. **Add/Update/Delete Trains** to manage train information
3. **View All Bookings** to monitor reservations
4. **Handle Feedback** by viewing and responding to passenger feedback
5. **View Train Ratings** to analyze service quality

## Key Features Implementation

### 1. Booking System
- Real-time seat availability checking
- Automatic PNR generation
- Transaction-based booking (rollback on failure)

### 2. Feedback System
- 5-star rating system
- Comment submission
- Admin response capability
- Average rating calculation per train

### 3. Document Management
- Document upload and storage
- Link documents to passengers and bookings
- Automatic ticket/receipt generation
- Support for multiple document types

### 4. Security
- Password-based authentication
- User isolation (users can only cancel their own tickets)
- Admin-only access to management features

## Database Connection Pooling

The application uses basic JDBC connections. For production use, consider implementing connection pooling with:
- HikariCP
- Apache DBCP
- C3P0

## Future Enhancements

- Password encryption (BCrypt/SHA-256)
- Email notifications for bookings
- Payment gateway integration
- PDF ticket generation
- Train schedule visualization
- Mobile responsive web interface
- RESTful API development
- Multi-language support

## Troubleshooting

### Database Connection Issues

**Error: "Unable to connect to database"**
- Verify database server is running
- Check database credentials in `database.properties`
- Ensure firewall allows database connections
- For SQL Server, verify TCP/IP is enabled in SQL Server Configuration Manager

### Compilation Issues

**Error: "Package does not exist"**
- Run `mvn clean install` to download dependencies
- Verify Maven is properly installed

### Runtime Issues

**Error: "ClassNotFoundException"**
- Ensure JDBC driver is in classpath
- Run `mvn package` to include all dependencies

## Contributing

This is a educational/demonstration project. Feel free to fork and enhance!

## License

This project is created for educational purposes.

## Contact

For questions or issues, please create an issue in the project repository.

---

**Version**: 1.0
**Last Updated**: 2025-11-22
**Author**: Railway Booking System Development Team
