/*
 * User Data Access Object (DAO) Class
 * This class handles all user-related database operations
 */
package javaapplication1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Library Management System
 */
public class UserDAO {
    
    /**
     * User Role Enum
     */
    public enum UserRole {
        MEMBER, LIBRARIAN, ADMIN
    }
    
    /**
     * User model class
     */
    public static class User {
        private int userId;
        private String name;
        private String address;
        private String mobileNo;
        private String email;
        private String password;
        private UserRole userRole;
        private Timestamp createdAt;
        
        // Constructors
        public User() {
            this.userRole = UserRole.MEMBER; // Default role
        }
        
        public User(String name, String address, String mobileNo, String email, String password) {
            this.name = name;
            this.address = address;
            this.mobileNo = mobileNo;
            this.email = email;
            this.password = password;
            this.userRole = UserRole.MEMBER; // Default role
        }
        
        public User(String name, String address, String mobileNo, String email, String password, UserRole userRole) {
            this.name = name;
            this.address = address;
            this.mobileNo = mobileNo;
            this.email = email;
            this.password = password;
            this.userRole = userRole;
        }
        
        // Getters and Setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public UserRole getUserRole() { return userRole; }
        public void setUserRole(UserRole userRole) { this.userRole = userRole; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
    
    /**
     * Register a new user
     */
    public static boolean registerUser(User user) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            // Check if email already exists
            if (emailExists(user.getEmail())) {
                showError("Email already exists! Please use a different email.");
                return false;
            }
            
            String sql = "INSERT INTO users (name, address, mobile_no, email, password, user_role) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getMobileNo());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword()); // In production, hash the password
            stmt.setString(6, user.getUserRole().toString());
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("User registered successfully!");
                return true;
            } else {
                showError("Failed to register user!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Authenticate user login
     */
    public static User authenticateUser(String email, String password) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return null;
            }
            
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, email);
            stmt.setString(2, password); // In production, compare with hashed password
            
            ResultSet rs = stmt.executeQuery();
            
            User user = null;
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setAddress(rs.getString("address"));
                user.setMobileNo(rs.getString("mobile_no"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setUserRole(UserRole.valueOf(rs.getString("user_role")));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
            
            rs.close();
            stmt.close();
            
            return user;
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all users
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return users;
            }
            
            String sql = "SELECT * FROM users ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setAddress(rs.getString("address"));
                user.setMobileNo(rs.getString("mobile_no"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setUserRole(UserRole.valueOf(rs.getString("user_role")));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Update user information
     */
    public static boolean updateUser(User user) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            String sql = "UPDATE users SET name = ?, address = ?, mobile_no = ?, email = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getMobileNo());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("User updated successfully!");
                return true;
            } else {
                showError("Failed to update user!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user role
     */
    public static boolean updateUserRole(int userId, UserRole newRole) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            String sql = "UPDATE users SET user_role = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, newRole.toString());
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("User role updated successfully!");
                return true;
            } else {
                showError("Failed to update user role!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete user
     */
    public static boolean deleteUser(int userId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            // Check if user has any active borrowings
            String checkBorrowings = "SELECT COUNT(*) FROM borrowing WHERE user_id = ? AND is_returned = FALSE";
            PreparedStatement checkStmt = conn.prepareStatement(checkBorrowings);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int activeBorrowings = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            if (activeBorrowings > 0) {
                showError("Cannot delete user with active book borrowings!");
                return false;
            }
            
            String sql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("User deleted successfully!");
                return true;
            } else {
                showError("Failed to delete user!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if email already exists
     */
    private static boolean emailExists(String email) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            rs.close();
            stmt.close();
            
            return count > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user by email
     */
    public static User getUserByEmail(String email) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            
            User user = null;
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setAddress(rs.getString("address"));
                user.setMobileNo(rs.getString("mobile_no"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
            
            rs.close();
            stmt.close();
            
            return user;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Show error message
     */
    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show success message
     */
    private static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
