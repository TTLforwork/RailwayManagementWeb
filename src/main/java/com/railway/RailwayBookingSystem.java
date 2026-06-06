package com.railway;

import com.railway.model.User;
import com.railway.service.UserService;
import com.railway.ui.AdminMenu;
import com.railway.ui.PassengerMenu;
import com.railway.util.DatabaseConnection;

import java.sql.SQLException;
import java.util.Scanner;

public class RailwayBookingSystem {
    private static final Scanner scanner = new Scanner(System.in);
    // FIX: userService now uses the simple no-argument constructor without issue.
    private static final UserService userService = new UserService(); 

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║         RAILWAY TICKET BOOKING SYSTEM                      ║");
        System.out.println("║         Version 1.0                                        ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n⚠ ERROR: Unable to connect to database!");
            System.err.println("Please check your database configuration in database.properties");
            System.err.println("Make sure the database server is running and credentials are correct.");
            return;
        }

        System.out.println("\n✓ Database connection successful!");

        while (true) {
            displayMainMenu();
            int choice = getIntInput();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        passengerLogin();
                        break;
                    case 2:
                        passengerRegistration();
                        break;
                    case 3:
                        adminLogin();
                        break;
                    case 4:
                        System.out.println("\n════════════════════════════════════════");
                        System.out.println("Thank you for using Railway Booking System!");
                        System.out.println("════════════════════════════════════════\n");
                        scanner.close();
                        System.exit(0);
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

    private static void displayMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         MAIN MENU                      ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Passenger Login                     ║");
        System.out.println("║ 2. Passenger Registration              ║");
        System.out.println("║ 3. Admin Login                         ║");
        System.out.println("║ 4. Exit                                ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Enter your choice: ");
    }

    private static void passengerLogin() throws SQLException {
        System.out.println("\n=== PASSENGER LOGIN ===");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = userService.login(username, password);
            System.out.println("\n✓ Login successful! Welcome, " + user.getName() + "!");

            PassengerMenu passengerMenu = new PassengerMenu(scanner, user);
            passengerMenu.display();
        } catch (SecurityException e) {
            System.err.println("\n✗ Login failed: " + e.getMessage());
        }
    }

    private static void passengerRegistration() throws SQLException {
        System.out.println("\n=== PASSENGER REGISTRATION ===");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Password (minimum 6 characters): ");
        String password = scanner.nextLine().trim();

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.err.println("\n✗ Passwords do not match!");
            return;
        }

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter City: ");
        String city = scanner.nextLine().trim();

        System.out.print("Enter Age: ");
        int age = getIntInput();
        scanner.nextLine();

        System.out.print("Enter Contact Number: ");
        String contact = scanner.nextLine().trim();

        System.out.print("Enter Gender (M/F): ");
        String gender = scanner.nextLine().trim().toUpperCase();
        
        User user = new User(username, name, address, city, age, contact, gender);

        try {
            boolean success = userService.registerUser(user, password);
            if (success) {
                System.out.println("\n✓ Registration successful!");
                System.out.println("You can now login with your credentials.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("\n✗ Registration failed: " + e.getMessage());
        }
    }

    private static void adminLogin() {
        System.out.println("\n=== ADMIN LOGIN ===");
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine().trim();

        // NOTE: Uses hardcoded credentials for console admin login
        if (username.equals("admin") && password.equals("admin123")) {
            System.out.println("\n✓ Admin login successful!");

            AdminMenu adminMenu = new AdminMenu(scanner);
            adminMenu.display();
        } else {
            System.err.println("\n✗ Invalid admin credentials!");
        }
    }

    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}