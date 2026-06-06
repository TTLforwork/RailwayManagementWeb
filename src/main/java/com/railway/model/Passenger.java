package com.railway.model;

import java.util.Objects;

public class Passenger {
    private String pnr;
    private String passengerName;
    private Integer age;
    private String gender;
    // Removed Source and Destination (Normalized)
    private String username;

    public Passenger() {
    }

    // Constructor updated for normalized fields (Source/Destination removed)
    public Passenger(String pnr, String passengerName, Integer age, String gender, String username) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.age = age;
        this.gender = gender;
        this.username = username;
    }

    // --- Getters and Setters ---
    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    // NOTE: Removed getSource(), getDestination(), setSource(), setDestination()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(pnr, passenger.pnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnr);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "pnr='" + pnr + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", username='" + username + '\'' +
                '}';
    }
}