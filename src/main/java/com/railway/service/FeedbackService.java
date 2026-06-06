package com.railway.service;

import com.railway.dao.FeedbackDAO;
import com.railway.dao.TrainDAO;
import com.railway.model.Feedback;
import com.railway.model.Train;

import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service; 

@Service
public class FeedbackService {
    private final FeedbackDAO feedbackDAO;
    private final TrainDAO trainDAO;

    // FIX: Reverted to simple no-argument constructor
    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
        this.trainDAO = new TrainDAO();
    }

    /**
     * Handles the business logic for a passenger submitting new feedback.
     */
    public boolean submitFeedback(Feedback feedback) throws SQLException {
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (feedback.getComment() == null || feedback.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }

        Train train = trainDAO.getTrainByNumber(feedback.getTrainNumber());
        if (train == null) {
            throw new IllegalArgumentException("Train not found for this feedback.");
        }

        return feedbackDAO.submitFeedback(feedback);
    }

    /**
     * Retrieves all submitted feedback for the administrator view.
     */
    public List<Feedback> getAllFeedback() throws SQLException {
        return feedbackDAO.getAllFeedback();
    }
    
    /**
     * Retrieves only feedback where AdminResponse is NULL or empty.
     */
    public List<Feedback> getPendingFeedback() throws SQLException {
        return feedbackDAO.getPendingFeedback();
    }

    /**
     * Retrieves feedback by ID (needed for AdminMenu response function).
     */
    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        return feedbackDAO.getFeedbackById(feedbackId);
    }

    /**
     * Calculates the average rating for a given train number (needed for AdminMenu).
     */
    public double getTrainAverageRating(String trainNumber) throws SQLException {
        return feedbackDAO.getTrainAverageRating(trainNumber);
    }
    
    /**
     * Allows an administrator to respond to a specific feedback entry.
     */
    public boolean updateAdminResponse(int feedbackId, String response) throws SQLException {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin response cannot be empty.");
        }
        return feedbackDAO.updateAdminResponse(feedbackId, response);
    }
}