package com.railway.dao;

import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository; 

@Repository
public class BookingDAO {

    // --- Helper: Map ResultSet to Ticket object ---
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setPnr(rs.getString("PNR"));
        ticket.setTrainNumber(rs.getString("TrainNumber"));

        // Map Train details
        Date date = rs.getDate("TravelDate");
        if (date != null) ticket.setDate(date.toLocalDate());

        ticket.setSource(rs.getString("Source"));
        ticket.setDestination(rs.getString("Destination"));
        ticket.setAmount(rs.getBigDecimal("Cost"));

        // Ticket status and username
        ticket.setStatus(rs.getString("Status"));
        try {
            ticket.setUsername(rs.getString("Username"));
        } catch (SQLException ignored) {
            // Username may not exist in some queries
        }

        return ticket;
    }

    // --- Book a ticket (atomic transaction with seat check) ---
    public boolean bookTicket(Passenger passenger, Ticket ticket) throws SQLException {
        String passengerSql = "INSERT INTO Passenger (PNR, PassengerName, Age, Gender, Username) VALUES (?, ?, ?, ?, ?)";
        String ticketSql = "INSERT INTO Ticket (PNR, TrainNumber) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check seat availability ATOMICALLY (using SELECT to ensure the count is up-to-date)
            String seatCheckSql = "SELECT t.Seats - " +
                                  "(SELECT COUNT(tkt.PNR) FROM Ticket tkt WHERE tkt.TrainNumber = ? AND tkt.Status = 'CONFIRMED') AS AvailableSeats " +
                                  "FROM Train t WHERE t.TrainNumber = ?";
            
            int availableSeats;
            try (PreparedStatement seatStmt = conn.prepareStatement(seatCheckSql)) {
                seatStmt.setString(1, ticket.getTrainNumber());
                seatStmt.setString(2, ticket.getTrainNumber());
                try (ResultSet rs = seatStmt.executeQuery()) {
                    if (rs.next()) {
                        availableSeats = rs.getInt("AvailableSeats");
                    } else {
                        throw new SQLException("Train not found during booking check!");
                    }
                }
            }

            if (availableSeats <= 0) {
                // Check fails inside the transaction, no seats available
                throw new IllegalStateException("No seats available for this train!");
            }

            // 2. Insert Passenger
            try (PreparedStatement ps = conn.prepareStatement(passengerSql)) {
                ps.setString(1, passenger.getPnr());
                ps.setString(2, passenger.getPassengerName());
                ps.setObject(3, passenger.getAge());
                ps.setString(4, passenger.getGender());
                ps.setString(5, passenger.getUsername());
                ps.executeUpdate();
            }

            // 3. Insert Ticket
            try (PreparedStatement ps = conn.prepareStatement(ticketSql)) {
                ps.setString(1, ticket.getPnr());
                ps.setString(2, ticket.getTrainNumber());
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException | IllegalStateException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    // --- Cancel a ticket (atomic) ---
    public boolean cancelTicket(String pnr) throws SQLException {
        String deleteTicketSql = "DELETE FROM Ticket WHERE PNR = ?";
        String deletePassengerSql = "DELETE FROM Passenger WHERE PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ticketStmt = conn.prepareStatement(deleteTicketSql);
                 PreparedStatement passengerStmt = conn.prepareStatement(deletePassengerSql)) {

                ticketStmt.setString(1, pnr);
                ticketStmt.executeUpdate();

                passengerStmt.setString(1, pnr);
                passengerStmt.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // --- Retrieve ticket details by PNR ---
    public Ticket getTicketDetails(String pnr) throws SQLException {
        String sql = "SELECT t.PNR, t.TrainNumber, t.Status, tr.Source, tr.Destination, " +
                "tr.Date as TravelDate, tr.Cost, p.Username " +
                "FROM Ticket t " +
                "INNER JOIN Passenger p ON t.PNR = p.PNR " +
                "INNER JOIN Train tr ON t.TrainNumber = tr.TrainNumber " +
                "WHERE t.PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToTicket(rs);
            }
        }
        return null;
    }

    // --- Retrieve all bookings ---
    public List<Ticket> getAllBookings() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.PNR, t.TrainNumber, t.Status, tr.Source, tr.Destination, " +
                "tr.Date as TravelDate, tr.Cost, p.Username " +
                "FROM Ticket t " +
                "INNER JOIN Passenger p ON t.PNR = p.PNR " +
                "INNER JOIN Train tr ON t.TrainNumber = tr.TrainNumber " +
                "ORDER BY tr.Date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) tickets.add(mapResultSetToTicket(rs));
        }
        return tickets;
    }

    // --- Retrieve tickets by username ---
    public List<Ticket> getTicketsByUsername(String username) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.PNR, t.TrainNumber, t.Status, tr.Source, tr.Destination, " +
                "tr.Date as TravelDate, tr.Cost, p.Username " +
                "FROM Ticket t " +
                "INNER JOIN Passenger p ON t.PNR = p.PNR " +
                "INNER JOIN Train tr ON t.TrainNumber = tr.TrainNumber " +
                "WHERE p.Username = ? ORDER BY tr.Date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) tickets.add(mapResultSetToTicket(rs));
            }
        }
        return tickets;
    }

    // --- Retrieve passenger details by PNR ---
    public Passenger getPassengerByPNR(String pnr) throws SQLException {
        String sql = "SELECT * FROM Passenger WHERE PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Passenger passenger = new Passenger();
                    passenger.setPnr(rs.getString("PNR"));
                    passenger.setPassengerName(rs.getString("PassengerName"));
                    passenger.setAge(rs.getObject("Age", Integer.class));
                    passenger.setGender(rs.getString("Gender"));
                    passenger.setUsername(rs.getString("Username"));
                    return passenger;
                }
            }
        }
        return null;
    }
}