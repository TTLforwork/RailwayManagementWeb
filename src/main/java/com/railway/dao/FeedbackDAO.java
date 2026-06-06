package com.railway.dao;

import com.railway.model.Feedback;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository; 

@Repository
public class FeedbackDAO {

    // Helper method to map ResultSet to Feedback object
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(rs.getInt("FeedbackID"));
        feedback.setUsername(rs.getString("Username"));
        feedback.setTrainNumber(rs.getString("TrainNumber"));
        feedback.setRating(rs.getInt("Rating"));
        feedback.setComment(rs.getString("Comment"));
        feedback.setAdminResponse(rs.getString("AdminResponse"));
        
        // Handle DateTime conversion
        Timestamp ts = rs.getTimestamp("DateSubmitted");
        if (ts != null) {
            feedback.setDateSubmitted(ts.toLocalDateTime());
        }
        return feedback;
    }

    // --- Passenger Action: Submit Feedback ---
    public boolean submitFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO Feedback (Username, TrainNumber, Rating, Comment, DateSubmitted) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, feedback.getUsername());
            stmt.setString(2, feedback.getTrainNumber());
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComment());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            return stmt.executeUpdate() > 0;
        }
    }
    
    // --- Admin Action: View All Feedback ---
    public List<Feedback> getAllFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.* FROM Feedback f ORDER BY f.DateSubmitted DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        }
        return feedbackList;
    }

    // --- NEW FIX: Retrieve only feedback with no Admin response ---
    public List<Feedback> getPendingFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        // SQL: Where AdminResponse is NULL or an empty string
        String sql = "SELECT f.* FROM Feedback f WHERE f.AdminResponse IS NULL OR f.AdminResponse = '' ORDER BY f.DateSubmitted DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        }
        return feedbackList;
    }

    // --- NEW FIX: Retrieve Feedback by ID (Needed for AdminMenu) ---
    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        String sql = "SELECT f.* FROM Feedback f WHERE f.FeedbackID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, feedbackId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeedback(rs);
                }
            }
        }
        return null;
    }
    
    // --- NEW FIX: Get Average Rating for a Train (Needed for AdminMenu) ---
    public double getTrainAverageRating(String trainNumber) throws SQLException {
        // FIX: Use CAST for explicit double calculation, then only select the result
        String sql = "SELECT AVG(CAST(Rating AS DECIMAL(3, 2))) AS AvgRating FROM Feedback WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Returns 0.0 if AVG is NULL (no feedback)
                    return rs.getDouble("AvgRating");
                }
            }
        }
        return 0.0;
    }
    
    // --- Admin Action: Update Admin Response ---
    public boolean updateAdminResponse(int feedbackId, String response) throws SQLException {
        String sql = "UPDATE Feedback SET AdminResponse = ? WHERE FeedbackID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, response);
            stmt.setInt(2, feedbackId);

            return stmt.executeUpdate() > 0;
        }
    }
}