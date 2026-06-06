# Railway Booking System - API Testing Guide

## Server Information
- **Base URL**: http://localhost:8080
- **Port**: 8080
- **Status**: ✅ Running

## Available API Endpoints

### 1. Train Management APIs

#### Get All Trains
```bash
curl http://localhost:8080/api/trains
```

#### Search Trains
```bash
curl "http://localhost:8080/api/trains/search?source=New%20York&destination=Chicago&date=2025-11-25"
```

#### Get Train by Number
```bash
curl http://localhost:8080/api/trains/T001
```

#### Check Train Availability
```bash
curl http://localhost:8080/api/trains/T001/availability
```

#### Add New Train (Admin)
```bash
curl -X POST http://localhost:8080/api/trains \
  -H "Content-Type: application/json" \
  -d "{\"trainNumber\":\"T006\",\"trainName\":\"Express Special\",\"source\":\"Boston\",\"destination\":\"Miami\",\"date\":\"2025-12-01\",\"cost\":199.99}"
```

#### Delete Train (Admin)
```bash
curl -X DELETE http://localhost:8080/api/trains/T006
```

---

### 2. User Management APIs

#### Register New User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser\",\"password\":\"password123\",\"name\":\"Test User\",\"address\":\"123 Test St\",\"city\":\"Test City\",\"age\":25,\"contact\":\"555-0199\",\"gender\":\"M\"}"
```

#### User Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john_doe\",\"password\":\"password123\"}"
```

#### Get User Profile
```bash
curl http://localhost:8080/api/users/john_doe
```

#### Update User Profile
```bash
curl -X PUT http://localhost:8080/api/users/john_doe \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"John Doe Updated\",\"address\":\"456 New Address\",\"city\":\"New York\",\"age\":31,\"contact\":\"555-0101\",\"gender\":\"M\"}"
```

---

### 3. Booking Management APIs

#### Book a Ticket
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john_doe\",\"passengerName\":\"John Doe\",\"age\":30,\"gender\":\"M\",\"trainNumber\":\"T001\",\"source\":\"New York\",\"destination\":\"Chicago\"}"
```

#### Get All Bookings (Admin)
```bash
curl http://localhost:8080/api/bookings
```

#### Get User Bookings
```bash
curl http://localhost:8080/api/bookings/user/john_doe
```

#### Cancel Booking
```bash
curl -X DELETE "http://localhost:8080/api/bookings/PNR001?username=john_doe"
```

---

### 4. Feedback Management APIs

#### Submit Feedback
```bash
curl -X POST http://localhost:8080/api/feedback \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john_doe\",\"trainNumber\":\"T001\",\"rating\":5,\"comments\":\"Excellent service!\"}"
```

#### Get All Feedback (Admin)
```bash
curl http://localhost:8080/api/feedback
```

#### Get Feedback for Specific Train
```bash
curl http://localhost:8080/api/feedback/train/T001
```

#### Get Pending Feedback (Admin)
```bash
curl http://localhost:8080/api/feedback/pending
```

#### Respond to Feedback (Admin)
```bash
curl -X PUT http://localhost:8080/api/feedback/1/respond \
  -H "Content-Type: application/json" \
  -d "{\"adminResponse\":\"Thank you for your feedback!\"}"
```

---

## Testing with PowerShell (Windows)

If curl doesn't work, use PowerShell's Invoke-WebRequest:

### GET Request Example:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/trains" -Method GET | Select-Object -ExpandProperty Content
```

### POST Request Example:
```powershell
$body = @{
    username = "testuser"
    password = "password123"
    name = "Test User"
    address = "123 Test St"
    city = "Test City"
    age = 25
    contact = "555-0199"
    gender = "M"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/users/register" -Method POST -Body $body -ContentType "application/json" | Select-Object -ExpandProperty Content
```

---

## Response Format

All API responses follow this format:

### Success Response:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful"
}
```

### Error Response:
```json
{
  "success": false,
  "error": "Error message here"
}
```

---

## Testing in Browser

You can test GET endpoints directly in your browser:

- All Trains: http://localhost:8080/api/trains
- Search Trains: http://localhost:8080/api/trains/search?source=New%20York&destination=Chicago
- Train Details: http://localhost:8080/api/trains/T001
- All Bookings: http://localhost:8080/api/bookings
- All Feedback: http://localhost:8080/api/feedback

---

## API Testing Tools

For better testing experience, use these tools:

1. **Postman** - https://www.postman.com/downloads/
2. **Insomnia** - https://insomnia.rest/download
3. **Thunder Client** (VS Code Extension)
4. **Browser Extensions** - RESTClient, Talend API Tester

---

## Database Connection Note

⚠️ **Important**: The API server is running but database connection may fail if:
- SQL Server is not running on port 1433
- SQL Server is not configured to accept TCP/IP connections
- Windows Firewall is blocking port 1433

To fix database connection issues:
1. Run `fix_sql_browser.bat` to start SQL Browser service
2. Check SQL Server Configuration Manager - enable TCP/IP
3. Restart SQL Server service
4. Make sure database `railway_system` exists

---

## Quick Test Sequence

Try these commands in order to test the full flow:

1. **Get all trains**: `curl http://localhost:8080/api/trains`
2. **Login**: `curl -X POST http://localhost:8080/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"john_doe\",\"password\":\"password123\"}"`
3. **Get user bookings**: `curl http://localhost:8080/api/bookings/user/john_doe`
4. **Get feedback**: `curl http://localhost:8080/api/feedback`
