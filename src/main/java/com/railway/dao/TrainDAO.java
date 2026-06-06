package com.railway.dao;

import com.railway.model.Train;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository; 

@Repository
public class TrainDAO {

    // --- Helper to convert database result into a Train object ---
    private Train extractTrainFromResultSet(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setTrainNumber(rs.getString("TrainNumber"));
        train.setTrainName(rs.getString("TrainName"));
        train.setSource(rs.getString("Source"));
        train.setDestination(rs.getString("Destination"));

        Date date = rs.getDate("Date");
        if (date != null) {
            train.setDate(date.toLocalDate());
        }

        train.setCost(rs.getBigDecimal("Cost"));
        
        // FIX: Retrieve Total Seats from the DB
        try {
            train.setTotalSeats(rs.getInt("Seats"));
        } catch (SQLException ignored) {
            // Field might not be in the result set for some queries, use default 100
            train.setTotalSeats(100); 
        }

        // AvailableSeats is calculated in the Service layer
        return train;
    }

    // --- CRUD Operations ---

    public boolean addTrain(Train train) throws SQLException {
        // FIX: Added Seats column to the insert statement
        String sql = "INSERT INTO Train (TrainNumber, TrainName, Source, Destination, Date, Cost, Seats) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, train.getTrainNumber());
            stmt.setString(2, train.getTrainName());
            stmt.setString(3, train.getSource());
            stmt.setString(4, train.getDestination());
            stmt.setDate(5, Date.valueOf(train.getDate()));
            stmt.setBigDecimal(6, train.getCost());
            stmt.setInt(7, train.getTotalSeats()); // FIX: Added TotalSeats

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateTrain(Train train) throws SQLException {
        // FIX: Added Seats column to the update statement
        String sql = "UPDATE Train SET TrainName = ?, Source = ?, Destination = ?, " +
                "Date = ?, Cost = ?, Seats = ? WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, train.getTrainName());
            stmt.setString(2, train.getSource());
            stmt.setString(3, train.getDestination());
            stmt.setDate(4, Date.valueOf(train.getDate()));
            stmt.setBigDecimal(5, train.getCost());
            stmt.setInt(6, train.getTotalSeats()); // FIX: Added TotalSeats
            stmt.setString(7, train.getTrainNumber());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTrain(String trainNumber) throws SQLException {
        String sql = "DELETE FROM Train WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);
            return stmt.executeUpdate() > 0;
        }
    }

    // --- Retrieval Operations ---

    public Train getTrainByNumber(String trainNumber) throws SQLException {
        // FIX: Included Seats column in selection
        String sql = "SELECT * FROM Train WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTrainFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Train> searchTrains(String source, String destination, LocalDate date) throws SQLException {
        List<Train> trains = new ArrayList<>();
        // FIX: Included Seats column in selection
        String sql = "SELECT * FROM Train WHERE Source = ? AND Destination = ? AND Date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, source);
            stmt.setString(2, destination);
            stmt.setDate(3, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(extractTrainFromResultSet(rs));
                }
            }
        }
        return trains;
    }

    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();
        // FIX: Included Seats column in selection
        String sql = "SELECT * FROM Train ORDER BY Date, TrainNumber";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trains.add(extractTrainFromResultSet(rs));
            }
        }
        return trains;
    }

    // --- Availability Calculation ---

    public int getAvailableSeats(String trainNumber) throws SQLException {
        // FIX: Query the actual 'Seats' column from the Train table and calculate dynamically
        String sql = "SELECT t.Seats - " +
                     "(SELECT COUNT(tkt.PNR) FROM Ticket tkt WHERE tkt.TrainNumber = ? AND tkt.Status = 'CONFIRMED') AS AvailableSeats " +
                     "FROM Train t WHERE t.TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);
            stmt.setString(2, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Return the calculated result, ensuring it's not negative
                    return Math.max(0, rs.getInt("AvailableSeats"));
                }
            }
        }
        // Return 0 if the train is not found, preventing overbooking
        return 0;
    }
}