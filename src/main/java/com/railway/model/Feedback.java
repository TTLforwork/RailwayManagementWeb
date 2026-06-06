package com.railway.model;

import java.time.LocalDateTime;

public class Feedback {
    private int feedbackId;
    private String username;
    private String trainNumber;
    private int rating;
    private String comment;
    private String adminResponse;
    private LocalDateTime dateSubmitted;

    public Feedback() {}

    // Constructor for submission
    public Feedback(String username, String trainNumber, int rating, String comment) {
        this.username = username;
        this.trainNumber = trainNumber;
        this.rating = rating;
        this.comment = comment;
    }

    // --- Getters and Setters ---
    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }

    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(LocalDateTime dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", username='" + username + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", adminResponse='" + adminResponse + '\'' +
                '}';
    }
}