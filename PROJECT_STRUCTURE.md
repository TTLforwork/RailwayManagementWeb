# Railway Booking System - Project Structure

## Complete File Structure

```
RailwaySystem/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── railway/
│       │           │
│       │           ├── RailwayBookingSystem.java     # Main application entry point
│       │           │
│       │           ├── model/                        # Domain Models (Entities)
│       │           │   ├── User.java                 # User entity
│       │           │   ├── Train.java                # Train entity
│       │           │   ├── Passenger.java            # Passenger entity
│       │           │   ├── Ticket.java               # Ticket/Booking entity
│       │           │   ├── Feedback.java             # Feedback entity
│       │           │   ├── Document.java             # Document entity
│       │           │   ├── Station.java              # Station entity
│       │           │   └── Schedule.java             # Schedule entity
│       │           │
│       │           ├── dao/                          # Data Access Layer
│       │           │   ├── UserDAO.java              # User database operations
│       │           │   ├── TrainDAO.java             # Train database operations
│       │           │   ├── BookingDAO.java           # Booking database operations
│       │           │   ├── FeedbackDAO.java          # Feedback database operations
│       │           │   └── DocumentDAO.java          # Document database operations
│       │           │
│       │           ├── service/                      # Business Logic Layer
│       │           │   ├── UserService.java          # User business logic
│       │           │   ├── TrainService.java         # Train business logic
│       │           │   ├── BookingService.java       # Booking business logic
│       │           │   ├── FeedbackService.java      # Feedback business logic
│       │           │   └── DocumentService.java      # Document business logic
│       │           │
│       │           ├── ui/                           # Presentation Layer
│       │           │   ├── PassengerMenu.java        # Passenger UI menu
│       │           │   └── AdminMenu.java            # Admin UI menu
│       │           │
│       │           └── util/                         # Utility Classes
│       │               ├── DatabaseConnection.java   # Database connection manager
│       │               └── PNRGenerator.java         # PNR generation utility
│       │
│       └── resources/
│           └── database.properties                   # Database configuration
│
├── database_setup.sql                                # Database schema & seed data
├── pom.xml                                          # Maven project configuration
├── README.md                                        # Comprehensive documentation
├── QUICK_START.md                                   # Quick start guide
├── PROJECT_STRUCTURE.md                             # This file
└── .gitignore                                       # Git ignore rules
```

## Layer Architecture

### 1. Presentation Layer (UI)
**Location**: `src/main/java/com/railway/ui/`

- **PassengerMenu.java**: Console-based interface for passengers
  - Search trains
  - Book tickets
  - View bookings
  - Cancel tickets
  - Submit feedback

- **AdminMenu.java**: Console-based interface for administrators
  - Manage trains
  - View bookings
  - Handle feedback
  - View ratings

### 2. Service Layer (Business Logic)
**Location**: `src/main/java/com/railway/service/`

- **UserService.java**
  - User registration
  - User authentication
  - Profile management

- **TrainService.java**
  - Train CRUD operations
  - Train search
  - Seat availability

- **BookingService.java**
  - Ticket booking
  - Ticket cancellation
  - Booking retrieval

- **FeedbackService.java**
  - Feedback submission
  - Feedback response
  - Rating calculation

- **DocumentService.java**
  - Document upload
  - Document linking
  - Document management

### 3. Data Access Layer (DAO)
**Location**: `src/main/java/com/railway/dao/`

- **UserDAO.java**
  - Database operations for User and Login tables
  - CRUD operations
  - Authentication queries

- **TrainDAO.java**
  - Database operations for Train table
  - Search queries
  - Seat availability queries

- **BookingDAO.java**
  - Database operations for Passenger and Ticket tables
  - Transaction management
  - Booking queries

- **FeedbackDAO.java**
  - Database operations for Feedback table
  - Rating calculations
  - Feedback status management

- **DocumentDAO.java**
  - Database operations for Document tables
  - Document linking operations

### 4. Model Layer (Entities)
**Location**: `src/main/java/com/railway/model/`

Each model class represents a database table:

| Model Class | Database Table | Primary Key |
|------------|----------------|-------------|
| User | User | Username |
| Train | Train | TrainNumber |
| Passenger | Passenger | PNR |
| Ticket | Ticket | PNR + TrainNumber |
| Feedback | Feedback | FeedbackID |
| Document | Document | DocumentID |
| Station | Station | StationName |
| Schedule | Schedule | TrainNumber + StationName |

### 5. Utility Layer
**Location**: `src/main/java/com/railway/util/`

- **DatabaseConnection.java**
  - Manages database connections
  - Connection pooling (basic)
  - Configuration loading

- **PNRGenerator.java**
  - Generates unique PNR numbers
  - Timestamp-based generation

## Database Schema

### Core Tables

1. **User** - User account information
   - Username (PK)
   - Name, Address, City, Age, Contact, Gender

2. **Login** - Authentication
   - Username (PK, FK → User)
   - Password

3. **Train** - Train information
   - TrainNumber (PK)
   - TrainName, Source, Destination, Date, Cost

4. **Passenger** - Passenger details
   - PNR (PK)
   - PassengerName, Age, Gender, Source, Destination
   - Username (FK → User)

5. **Ticket** - Bookings
   - PNR (PK, FK → Passenger)
   - TrainNumber (PK, FK → Train)
   - Date, Source, Destination

6. **Station** - Railway stations
   - StationName (PK)
   - Address

7. **Schedule** - Train schedules
   - TrainNumber (PK, FK → Train)
   - StationName (PK, FK → Station)
   - ScheduleID, ArrivalTime, DepartureTime

8. **Feedback** - User feedback
   - FeedbackID (PK)
   - Username (FK → User)
   - TrainNumber, Rating, Comments, Status, AdminResponse

### Document Management Tables

9. **Document** - Document metadata
   - DocumentID (PK)
   - DocumentType, FileName, FilePath, FileSize
   - UploadedBy, UploadDate, Status

10. **PassengerDocument** - Links documents to passengers
    - PNR (PK, FK → Passenger)
    - DocumentID (PK, FK → Document)
    - Purpose

11. **BookingDocument** - Links documents to bookings
    - PNR (PK, FK → Passenger)
    - DocumentID (PK, FK → Document)
    - DocumentCategory (TICKET/RECEIPT/INVOICE)

### Admin Tables

12. **Admin** - Admin information
    - AdminID (PK)
    - Address

13. **Admin_Station** - Admin-Station relationship
    - AdminID (PK, FK → Admin)
    - StationName (PK, FK → Station)

14. **Admin_Train** - Admin-Train relationship
    - AdminID (PK, FK → Admin)
    - TrainNumber (PK, FK → Train)

## Design Patterns Used

### 1. **Layered Architecture**
- Clear separation of concerns
- Model → DAO → Service → UI

### 2. **Data Access Object (DAO) Pattern**
- Abstracts database operations
- Encapsulates SQL queries

### 3. **Service Layer Pattern**
- Business logic separation
- Transaction management

### 4. **Singleton Pattern**
- DatabaseConnection utility
- Single point of database access

### 5. **Factory Pattern**
- PNRGenerator for unique ID generation

## Key Features Implementation

### Feature 1: Ticket Booking
**Flow**: UI → BookingService → BookingDAO + TrainDAO → Database

1. User searches trains (TrainService)
2. User selects train and provides details
3. System checks seat availability (TrainDAO)
4. System generates PNR (PNRGenerator)
5. System creates Passenger and Ticket records (BookingDAO)
6. Transaction committed or rolled back

### Feature 2: Feedback System
**Flow**: UI → FeedbackService → FeedbackDAO → Database

1. User submits rating and comments
2. FeedbackService validates rating (1-5)
3. FeedbackDAO stores feedback
4. Admin views pending feedback
5. Admin responds to feedback
6. System updates feedback status

### Feature 3: Document Management
**Flow**: UI → DocumentService → DocumentDAO → Database + FileSystem

1. System generates document (ticket/receipt)
2. DocumentService creates metadata
3. DocumentDAO stores document info
4. System links document to passenger/booking
5. Document available for retrieval

## Configuration Files

### 1. pom.xml
Maven project configuration:
- Project metadata
- Dependencies (JDBC drivers, utilities)
- Build plugins
- Compiler settings

### 2. database.properties
Database configuration:
- Driver class
- Connection URL
- Credentials
- Connection pool settings

## Data Flow Example: Book Ticket

```
User Input (PassengerMenu)
    ↓
BookingService.bookTicket()
    ↓
├─→ TrainService.getTrainDetails() ──→ TrainDAO.getTrainByNumber()
│                                           ↓
│                                      Database Query
│                                           ↓
│   ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← Train Object
│
├─→ TrainService.checkSeatAvailability() ─→ TrainDAO.getAvailableSeats()
│                                                ↓
│                                           Database Query
│                                                ↓
│   ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← Available Seats
│
├─→ PNRGenerator.generatePNR()
│        ↓
│   Unique PNR
│
└─→ BookingDAO.bookTicket(passenger, ticket)
         ↓
    Database Transaction
         ↓
    ├─→ INSERT Passenger
    └─→ INSERT Ticket
         ↓
    COMMIT / ROLLBACK
         ↓
    Success / Failure
         ↓
    User Confirmation
```

## Class Relationships

### User → Passenger → Ticket
- One User can make multiple bookings (1:N)
- Each booking creates a Passenger record (1:1)
- Each Passenger has one Ticket (1:1)

### Train → Ticket
- One Train can have multiple Tickets (1:N)
- Each Ticket belongs to one Train (N:1)

### User → Feedback
- One User can submit multiple Feedback entries (1:N)
- Each Feedback belongs to one User (N:1)

### Passenger → Document
- One Passenger can have multiple Documents (M:N via PassengerDocument)
- One Document can relate to multiple Passengers (M:N)

## Security Considerations

1. **Authentication**: Username/password validation
2. **Authorization**: User can only access their own data
3. **Input Validation**: Service layer validates all inputs
4. **SQL Injection Prevention**: Prepared statements used throughout
5. **Transaction Management**: ACID properties maintained

## Performance Optimizations

1. **Connection Management**: Reusable database connections
2. **Prepared Statements**: SQL query compilation optimization
3. **Indexing**: Primary and foreign keys indexed
4. **Transaction Scope**: Minimal transaction boundaries

## Error Handling

1. **SQLException**: Database errors caught and logged
2. **IllegalArgumentException**: Invalid input data
3. **SecurityException**: Unauthorized access attempts
4. **Transaction Rollback**: On any failure during booking

## Testing Strategy

### Unit Testing
- Test each DAO method independently
- Test service layer business logic
- Mock database connections

### Integration Testing
- Test complete workflows
- Test database transactions
- Test error scenarios

### User Acceptance Testing
- Test passenger workflows
- Test admin workflows
- Test edge cases

## Future Enhancements

1. **Web Interface**: Convert to Spring Boot REST API
2. **Security**: Add password encryption, JWT authentication
3. **Payment Integration**: Add payment gateway
4. **Email Notifications**: Send booking confirmations
5. **PDF Generation**: Generate printable tickets
6. **Caching**: Add Redis for frequently accessed data
7. **Microservices**: Split into independent services
8. **Mobile App**: Native Android/iOS apps

---

**Project Version**: 1.0
**Last Updated**: 2025-11-22
**Architecture**: Layered Architecture with DAO Pattern
