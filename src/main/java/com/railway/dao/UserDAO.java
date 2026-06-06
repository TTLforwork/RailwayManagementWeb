package com.railway.dao;

import com.railway.model.User;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository; 

@Repository // FIX: ADDED @Repository
public class UserDAO {

    // --- Helper to map result set to a User object (Including the Role field) ---
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString("Username"));
        user.setName(rs.getString("Name"));
        user.setAddress(rs.getString("Address"));
        user.setCity(rs.getString("City"));
        user.setAge(rs.getObject("Age", Integer.class));
        user.setContact(rs.getString("Contact"));
        user.setGender(rs.getString("Gender"));
        
        // CRITICAL FIX: Retrieve the Role from the User table
        user.setRole(rs.getString("Role")); 
        
        return user;
    }

    // --- registerUser (Handles multi-table transaction) ---
    public boolean registerUser(User user, String password) throws SQLException {
        // Step 1: Insert into User profile table
        String userSql = "INSERT INTO [User] (Username, Name, Address, City, Age, Contact, Gender, Role) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // Step 2: Insert into Login credentials table
        String loginSql = "INSERT INTO Login (Username, Password) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement userStmt = conn.prepareStatement(userSql);
                 PreparedStatement loginStmt = conn.prepareStatement(loginSql)) {

                // Execute User Insert
                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getName());
                userStmt.setString(3, user.getAddress());
                userStmt.setString(4, user.getCity());
                userStmt.setObject(5, user.getAge());
                userStmt.setString(6, user.getContact());
                userStmt.setString(7, user.getGender());
                userStmt.setString(8, user.getRole()); // Insert the role
                userStmt.executeUpdate();

                // Execute Login Insert
                loginStmt.setString(1, user.getUsername());
                loginStmt.setString(2, password);
                loginStmt.executeUpdate();

                conn.commit(); // Commit transaction
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    // --- login (Verifies credentials and returns full User object with Role) ---
    public User login(String username, String password) throws SQLException {
        // Query joins User and Login tables to verify password and retrieve profile data + Role
        String sql = "SELECT u.* FROM [User] u " +
                     "INNER JOIN Login l ON u.Username = l.Username " +
                     "WHERE u.Username = ? AND l.Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    // --- getUserByUsername (Retrieves full profile for display) ---
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM [User] WHERE Username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    // --- updateUser (Updates only the User profile data) ---
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE [User] SET Name = ?, Address = ?, City = ?, Age = ?, Contact = ?, Gender = ? " +
                     "WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getCity());
            stmt.setObject(4, user.getAge());
            stmt.setString(5, user.getContact());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getUsername());

            return stmt.executeUpdate() > 0;
        }
    }

    // --- getAllUsers (Optional: for potential Admin view) ---
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [User] ORDER BY Username";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
}
