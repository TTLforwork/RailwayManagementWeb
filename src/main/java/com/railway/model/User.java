package com.railway.model;

import java.util.Objects;

public class User {
    private String username;
    private String name;
    private String address;
    private String city;
    private Integer age;
    private String contact;
    private String gender;
    private String role; // CRITICAL: Added for security and display

    public User() {
    }

    // Original 7-argument constructor (sets role to 'passenger')
    public User(String username, String name, String address, String city,
                Integer age, String contact, String gender) {
        this.username = username;
        this.name = name;
        this.address = address;
        this.city = city;
        this.age = age;
        this.contact = contact;
        this.gender = gender;
        this.role = "passenger"; // Default role
    }

    // FIX: New 8-argument constructor to match the call in UserController.registerUser
    public User(String username, String name, String address, String city,
                Integer age, String contact, String gender, String role) {
        this.username = username;
        this.name = name;
        this.address = address;
        this.city = city;
        this.age = age;
        this.contact = contact;
        this.gender = gender;
        this.role = role; 
    }

    // --- Getters and Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", age=" + age +
                ", contact='" + contact + '\'' +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}