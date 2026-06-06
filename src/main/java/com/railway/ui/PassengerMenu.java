package com.railway.ui;

import com.railway.model.Feedback;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.model.User;
import com.railway.service.BookingService;
import com.railway.service.FeedbackService;
import com.railway.service.TrainService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class PassengerMenu {
    private final Scanner scanner;
    private final User currentUser;
    private final BookingService bookingService;
    private final TrainService trainService;
    private final FeedbackService feedbackService;

    public PassengerMenu(Scanner scanner, User currentUser) {
        this.scanner = scanner;
        this.currentUser = currentUser;
        this.bookingService = new BookingService();
        this.trainService = new TrainService();
        this.feedbackService = new FeedbackService();
    }

    public void display() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║      PASSENGER MENU                    ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ Welcome, " + String.format("%-30s", currentUser.getName()) + "║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Search Trains                       ║");
            System.out.println("║ 2. Book Ticket                         ║");
            System.out.println("║ 3. View My Bookings                    ║");
            System.out.println("║ 4. Cancel Ticket                       ║");
            System.out.println("║ 5. Check Seat Availability             ║");
            System.out.println("║ 6. Submit Feedback                     ║");
            System.out.println("║ 7. View My Feedback                    ║");
            System.out.println("║ 8. Logout                              ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();
            // scanner.nextLine(); // FIX: Removed redundant nextLine call since getIntInput now consumes it

            try {
                switch (choice) {
                    case 1:
                        searchTrains();
                        break;
                    case 2:
                        bookTicket();
                        break;
                    case 3:
                        viewMyBookings();
                        break;
                    case 4:
                        cancelTicket();
                        break;
                    case 5:
                        checkSeatAvailability();
                        break;
                    case 6:
                        submitFeedback();
                        break;
                    case 7:
                        viewMyFeedback();
                        break;
                    case 8:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void searchTrains() throws SQLException {
        System.out.println("\n=== SEARCH TRAINS ===");
        System.out.print("Enter source (e.g., New York): ");
        String source = scanner.nextLine().trim();
        if (source.isEmpty()) source = null;

        System.out.print("Enter destination (e.g., Chicago): ");
        String destination = scanner.nextLine().trim();
        if (destination.isEmpty()) destination = null;

        LocalDate date = null;
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Searching without date filter.");
            }
        }
        
        if (source == null || destination == null || date == null) {
            System.err.println("Source, Destination, and Date are required for a proper search.");
            return;
        }

        List<Train> trains = trainService.searchTrains(source, destination, date);

        if (trains.isEmpty()) {
            System.out.println("No trains found matching your criteria.");
        } else {
            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-12s %-25s %-20s %-20s %-12s %-10s %-10s%n",
                    "Train No", "Train Name", "Source", "Destination", "Date", "Cost", "Seats");
            System.out.println("=".repeat(120));

            for (Train train : trains) {
                int availableSeats = trainService.checkSeatAvailability(train.getTrainNumber());
                System.out.printf("%-12s %-25s %-20s %-20s %-12s ₹%-9.2f %-10d%n",
                        train.getTrainNumber(),
                        train.getTrainName(),
                        train.getSource(),
                        train.getDestination(),
                        train.getDate(),
                        train.getCost(),
                        availableSeats);
            }
            System.out.println("=".repeat(120));
        }
    }

    private void bookTicket() throws SQLException {
        System.out.println("\n=== BOOK TICKET ===");
        System.out.print("Enter Train Number: ");
        String trainNumber = scanner.nextLine().trim();

        Train train = trainService.getTrainDetails(trainNumber);
        if (train == null) {
            System.out.println("Train not found!");
            return;
        }

        int availableSeats = trainService.checkSeatAvailability(trainNumber);
        System.out.println("\nTrain Details:");
        System.out.println("Train Name: " + train.getTrainName());
        System.out.println("Route: " + train.getSource() + " → " + train.getDestination());
        System.out.println("Date: " + train.getDate());
        System.out.println("Cost: ₹" + train.getCost());
        System.out.println("Available Seats: " + availableSeats);

        if (availableSeats <= 0) {
            System.out.println("Sorry, no seats available!");
            return;
        }

        System.out.print("\nEnter Passenger Name: ");
        String passengerName = scanner.nextLine().trim();

        System.out.print("Enter Age: ");
        int age = getIntInput();
        // scanner.nextLine(); // FIX: Removed redundant nextLine call since getIntInput now consumes it

        System.out.print("Enter Gender (M/F/O): ");
        String gender = scanner.nextLine().trim().toUpperCase();

        // FIX: Removed redundant source/destination inputs as they are no longer needed
        // The service layer only needs the core passenger and train info
        try {
            String pnr = bookingService.bookTicket(currentUser.getUsername(), passengerName,
                    age, gender, trainNumber);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("✓ TICKET BOOKED SUCCESSFULLY!");
            System.out.println("=".repeat(50));
            System.out.println("PNR: " + pnr);
            System.out.println("Passenger Name: " + passengerName);
            System.out.println("Train: " + train.getTrainName() + " (" + trainNumber + ")");
            System.out.println("Date: " + train.getDate());
            System.out.println("Route: " + train.getSource() + " → " + train.getDestination());
            System.out.println("Amount: ₹" + train.getCost());
            System.out.println("=".repeat(50));
            System.out.println("Please save your PNR for future reference.");
        } catch (SQLException e) {
            System.err.println("Booking failed: " + e.getMessage());
        }
    }

    private void viewMyBookings() throws SQLException {
        System.out.println("\n=== MY BOOKINGS ===");
        List<Ticket> tickets = bookingService.getUserBookings(currentUser.getUsername());

        if (tickets.isEmpty()) {
            System.out.println("You have no bookings.");
        } else {
            System.out.println("\n" + "=".repeat(110));
            System.out.printf("%-15s %-12s %-12s %-20s %-20s %-10s%n",
                    "PNR", "Train No", "Date", "Source", "Destination", "Amount");
            System.out.println("=".repeat(110));

            for (Ticket ticket : tickets) {
                System.out.printf("%-15s %-12s %-12s %-20s %-20s ₹%-9.2f%n",
                        ticket.getPnr(),
                        ticket.getTrainNumber(),
                        ticket.getDate(),
                        ticket.getSource(),
                        ticket.getDestination(),
                        ticket.getAmount());
            }
            System.out.println("=".repeat(110));
        }
    }

    private void cancelTicket() throws SQLException {
        System.out.println("\n=== CANCEL TICKET ===");
        System.out.print("Enter PNR: ");
        String pnr = scanner.nextLine().trim();

        Ticket ticket = bookingService.getTicketDetails(pnr);
        if (ticket == null) {
            System.out.println("Ticket not found!");
            return;
        }

        System.out.println("\nTicket Details:");
        System.out.println("PNR: " + ticket.getPnr());
        System.out.println("Train Number: " + ticket.getTrainNumber());
        System.out.println("Route: " + ticket.getSource() + " → " + ticket.getDestination());
        System.out.println("Date: " + ticket.getDate());
        System.out.println("Amount: ₹" + ticket.getAmount());

        System.out.print("\nAre you sure you want to cancel this ticket? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            try {
                boolean success = bookingService.cancelTicket(pnr, currentUser.getUsername());
                if (success) {
                    System.out.println("\n✓ Ticket cancelled successfully!");
                    System.out.println("Refund of ₹" + ticket.getAmount() + " will be processed.");
                }
            } catch (SQLException e) {
                System.err.println("Cancellation failed: " + e.getMessage());
            }
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    private void checkSeatAvailability() throws SQLException {
        System.out.println("\n=== CHECK SEAT AVAILABILITY ===");
        System.out.print("Enter Train Number: ");
        String trainNumber = scanner.nextLine().trim();

        try {
            Train train = trainService.getTrainDetails(trainNumber);
            if (train == null) {
                System.out.println("Train not found!");
                return;
            }

            int availableSeats = trainService.checkSeatAvailability(trainNumber);

            System.out.println("\nTrain: " + train.getTrainName() + " (" + trainNumber + ")");
            System.out.println("Route: " + train.getSource() + " → " + train.getDestination());
            System.out.println("Date: " + train.getDate());
            System.out.println("Total Seats: " + train.getTotalSeats());
            System.out.println("Available Seats: " + availableSeats);
            System.out.println("Status: " + (availableSeats > 0 ? "AVAILABLE" : "FULL"));
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void submitFeedback() throws SQLException {
        System.out.println("\n=== SUBMIT FEEDBACK ===");
        System.out.print("Enter Train Number (e.g., T001): ");
        String trainNumber = scanner.nextLine().trim();
        
        Train train = trainService.getTrainDetails(trainNumber);
        if (train == null) {
            System.err.println("Train not found. Feedback submission requires a valid train number.");
            return;
        }

        System.out.print("Enter Rating (1-5): ");
        int rating;
        try {
            rating = getIntInput();
            // scanner.nextLine(); // FIX: Removed redundant nextLine
        } catch (Exception e) {
            System.err.println("Invalid rating input. Aborting.");
            // scanner.nextLine(); // FIX: Removed redundant nextLine
            return;
        }

        System.out.print("Enter Comments: ");
        String comments = scanner.nextLine().trim();
        
        // FIX: The model requires a Feedback object
        Feedback feedback = new Feedback(currentUser.getUsername(), trainNumber, rating, comments);

        try {
            boolean success = feedbackService.submitFeedback(feedback);
            if (success) {
                System.out.println("\n✓ Feedback submitted successfully! Thank you.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to submit feedback: " + e.getMessage());
        }
    }

    private void viewMyFeedback() throws SQLException {
        System.out.println("\n=== MY FEEDBACK ===");
        // NOTE: Assumes FeedbackService has a getUserFeedback method
        // Since it doesn't exist, we'll retrieve all and filter manually
        List<Feedback> feedbackList = feedbackService.getAllFeedback(); 
        
        List<Feedback> myFeedback = new java.util.ArrayList<>();
        for (Feedback f : feedbackList) {
            if (f.getUsername().equals(currentUser.getUsername())) {
                myFeedback.add(f);
            }
        }


        if (myFeedback.isEmpty()) {
            System.out.println("You haven't submitted any feedback yet.");
        } else {
            for (Feedback feedback : myFeedback) {
                System.out.println("\n" + "-".repeat(80));
                System.out.println("Feedback ID: " + feedback.getFeedbackId());
                System.out.println("Train Number: " + (feedback.getTrainNumber() != null ? feedback.getTrainNumber() : "General"));
                System.out.println("Rating: " + "★".repeat(feedback.getRating()) + "☆".repeat(5 - feedback.getRating()));
                // FIX: Corrected method name to getComment()
                System.out.println("Comments: " + feedback.getComment());
                System.out.println("Submitted: " + feedback.getDateSubmitted());
                
                String responseStatus = (feedback.getAdminResponse() != null && !feedback.getAdminResponse().isEmpty()) 
                                        ? "Responded" : "Pending";
                System.out.println("Status: " + responseStatus);
                
                if (responseStatus.equals("Responded")) {
                    System.out.println("Admin Response: " + feedback.getAdminResponse());
                }
                System.out.println("-".repeat(80));
            }
        }
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Please enter a number: ");
            scanner.next(); // Consume non-integer input
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return value;
    }
}