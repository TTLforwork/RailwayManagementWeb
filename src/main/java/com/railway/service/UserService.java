package com.railway.service;

import com.railway.dao.UserDAO;
import com.railway.model.User;

import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service; 

@Service
public class UserService {
    private final UserDAO userDAO;

    // FIX: Reverted to simple no-argument constructor for CLI/Console compatibility
    public UserService() { 
        this.userDAO = new UserDAO(); // Manual instantiation for CLI
    }

    /**
     * Registers a new user.
     */
    public boolean registerUser(User user, String password) throws SQLException {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required!");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters!");
        }

        User existing = userDAO.getUserByUsername(user.getUsername());
        if (existing != null) {
            throw new IllegalArgumentException("Username already exists!");
        }

        user.setRole("passenger"); 
        
        return userDAO.registerUser(user, password);
    }

    /**
     * Authenticates the user.
     */
    public User login(String username, String password) throws SQLException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required!");
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            throw new SecurityException("Invalid username or password!");
        }

        return user;
    }

    public User getUserProfile(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    public boolean updateUserProfile(User user) throws SQLException {
        User existing = userDAO.getUserByUsername(user.getUsername());
        if (existing == null) {
            throw new IllegalArgumentException("User not found!");
        }

        return userDAO.updateUser(user);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }
}