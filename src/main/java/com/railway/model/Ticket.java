package com.railway.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {
    private String pnr;
    private String trainNumber;
    
    // --- Fields retrieved via JOINs (Normalization) ---
    private LocalDate date; 
    private String source; 
    private String destination; 
    private BigDecimal amount; 
    
    // --- Fields stored directly on Ticket ---
    private String status;
    private LocalDateTime bookingDate;
    private String username; // Added for secure filtering

    public Ticket() {
    }

    // Constructor simplified to reflect only necessary inputs for a new ticket
    public Ticket(String pnr, String trainNumber) { 
        this.pnr = pnr;
        this.trainNumber = trainNumber;
        this.status = "CONFIRMED";
        this.bookingDate = LocalDateTime.now();
    }
    
    // --- Getters and Setters ---
    
    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    // --- Accessor methods for Normalized Data ---
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    // --- Direct Ticket Data ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(pnr, ticket.pnr) &&
                Objects.equals(trainNumber, ticket.trainNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnr, trainNumber);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "pnr='" + pnr + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", date=" + date +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", username='" + username + '\'' +
                '}';
    }
}