package com.railway.test;

import com.railway.model.*;
import com.railway.service.*;
import com.railway.util.DatabaseConnection;

import java.sql.SQLException;
import java.util.List;

public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  DATABASE CONNECTION & API TEST");
        System.out.println("═══════════════════════════════════════════════\n");

        // Test 1: Database Connection
        testDatabaseConnection();

        // Test 2: Train Service
        testTrainService();

        // Test 3: User Service
        testUserService();

        // Test 4: Booking Service
        testBookingService();

        // Test 5: Feedback Service
        testFeedbackService();

        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("  TEST COMPLETED");
        System.out.println("═══════════════════════════════════════════════");
    }

    private static void testDatabaseConnection() {
        System.out.println("TEST 1: Database Connection");
        System.out.println("─────────────────────────────────────────────");
        try {
            boolean connected = DatabaseConnection.testConnection();
            if (connected) {
                System.out.println("✓ Database connection successful!");
            } else {
                System.out.println("✗ Database connection failed!");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testTrainService() {
        System.out.println("TEST 2: Train Service - Get All Trains");
        System.out.println("─────────────────────────────────────────────");
        try {
            TrainService trainService = new TrainService();
            List<Train> trains = trainService.getAllTrains();

            if (trains != null) {
                System.out.println("✓ Successfully retrieved " + trains.size() + " trains from database");

                if (!trains.isEmpty()) {
                    System.out.println("\nSample Train Data:");
                    Train firstTrain = trains.get(0);
                    System.out.println("  Train Number: " + firstTrain.getTrainNumber());
                    System.out.println("  Train Name: " + firstTrain.getTrainName());
                    System.out.println("  Source: " + firstTrain.getSource());
                    System.out.println("  Destination: " + firstTrain.getDestination());
                    System.out.println("  Date: " + firstTrain.getDate());
                    System.out.println("  Cost: ₹" + firstTrain.getCost());

                    // Test seat availability
                    int seats = trainService.checkSeatAvailability(firstTrain.getTrainNumber());
                    System.out.println("  Available Seats: " + seats);
                } else {
                    System.out.println("⚠ No trains found in database. Run database_setup.sql to insert seed data.");
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testUserService() {
        System.out.println("TEST 3: User Service - Get All Users");
        System.out.println("─────────────────────────────────────────────");
        try {
            UserService userService = new UserService();
            List<User> users = userService.getAllUsers();

            if (users != null) {
                System.out.println("✓ Successfully retrieved " + users.size() + " users from database");

                if (!users.isEmpty()) {
                    System.out.println("\nSample User Data:");
                    User firstUser = users.get(0);
                    System.out.println("  Username: " + firstUser.getUsername());
                    System.out.println("  Name: " + firstUser.getName());
                    System.out.println("  City: " + firstUser.getCity());
                    System.out.println("  Age: " + firstUser.getAge());
                    System.out.println("  Contact: " + firstUser.getContact());
                } else {
                    System.out.println("⚠ No users found in database. Run database_setup.sql to insert seed data.");
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testBookingService() {
        System.out.println("TEST 4: Booking Service - Get All Bookings");
        System.out.println("─────────────────────────────────────────────");
        try {
            BookingService bookingService = new BookingService();
            List<Ticket> tickets = bookingService.getAllBookings();

            if (tickets != null) {
                System.out.println("✓ Successfully retrieved " + tickets.size() + " bookings from database");

                if (!tickets.isEmpty()) {
                    System.out.println("\nSample Booking Data:");
                    Ticket firstTicket = tickets.get(0);
                    System.out.println("  PNR: " + firstTicket.getPnr());
                    System.out.println("  Train Number: " + firstTicket.getTrainNumber());
                    System.out.println("  Date: " + firstTicket.getDate());
                    System.out.println("  Source: " + firstTicket.getSource());
                    System.out.println("  Destination: " + firstTicket.getDestination());
                    System.out.println("  Amount: ₹" + firstTicket.getAmount());

                    // Test getting passenger details
                    Passenger passenger = bookingService.getPassengerDetails(firstTicket.getPnr());
                    if (passenger != null) {
                        System.out.println("  Passenger Name: " + passenger.getPassengerName());
                    }
                } else {
                    System.out.println("⚠ No bookings found in database. Run database_setup.sql to insert seed data.");
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testFeedbackService() {
        System.out.println("TEST 5: Feedback Service - Get All Feedback");
        System.out.println("─────────────────────────────────────────────");
        try {
            FeedbackService feedbackService = new FeedbackService();
            List<Feedback> feedbackList = feedbackService.getAllFeedback();

            if (feedbackList != null) {
                System.out.println("✓ Successfully retrieved " + feedbackList.size() + " feedback entries from database");

                if (!feedbackList.isEmpty()) {
                    System.out.println("\nSample Feedback Data:");
                    Feedback firstFeedback = feedbackList.get(0);
                    System.out.println("  Feedback ID: " + firstFeedback.getFeedbackId());
                    System.out.println("  Username: " + firstFeedback.getUsername());
                    System.out.println("  Train Number: " + firstFeedback.getTrainNumber());
                    System.out.println("  Rating: " + firstFeedback.getRating() + "/5");
                    
                    // FIX: Changed getComments() to getComment()
                    System.out.println("  Comments: " + firstFeedback.getComment());
                    
                    // FIX: Replaced getStatus() with AdminResponse check
                    String responseStatus = firstFeedback.getAdminResponse() != null && !firstFeedback.getAdminResponse().isEmpty() 
                                            ? "Responded" : "Pending";
                    System.out.println("  Status: " + responseStatus);
                    
                } else {
                    System.out.println("⚠ No feedback found in database. This is normal for a new installation.");
                    System.out.println("  Feedback table is created automatically but empty.");
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        System.out.println();
    }
}
