package com.railway.controller;

import com.railway.model.User;
import com.railway.service.UserService;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // FIX: Use Constructor Injection. Spring will automatically provide the UserService bean.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // FIX: The User model now has the required 8-argument constructor
            User user = new User(
                request.getUsername(),
                request.getName(),
                request.getAddress(),
                request.getCity(),
                request.getAge(),
                request.getContact(),
                request.getGender(),
                request.getRole() != null ? request.getRole() : "passenger"
            );

            boolean success = userService.registerUser(user, request.getPassword());
            if (success) {
                response.put("success", true);
                response.put("message", "User registered successfully");
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Registration failed. Username may already exist.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            if (user != null) {
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserProfile(username);
            if (user != null) {
                response.put("success", true);
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable String username,
            @RequestBody User updatedUser) {
        Map<String, Object> response = new HashMap<>();
        try {
            updatedUser.setUsername(username);
            boolean success = userService.updateUserProfile(updatedUser);
            if (success) {
                response.put("success", true);
                response.put("message", "Profile updated successfully");
                response.put("data", updatedUser);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to update profile");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Inner classes for request bodies
    public static class RegisterRequest {
        private String username;
        private String password;
        private String name;
        private String address;
        private String city;
        private int age;
        private String contact;
        private String gender;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}