package javaapplication1;

import java.sql.*;

/**
 * Simple test to list all users in database
 */
public class ListUsers {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("Database connection failed!");
                return;
            }
            
            System.out.println("=== All Users in Database ===");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Password: " + rs.getString("password"));
                System.out.println("Role: " + rs.getString("user_role"));
                System.out.println("---");
            }
            
            rs.close();
            stmt.close();
            
            // Now test authentication with known user
            System.out.println("\n=== Testing Authentication ===");
            
            // Get first user to test with
            PreparedStatement testStmt = conn.prepareStatement("SELECT * FROM users LIMIT 1");
            ResultSet testRs = testStmt.executeQuery();
            
            if (testRs.next()) {
                String testEmail = testRs.getString("email");
                String testPassword = testRs.getString("password");
                System.out.println("Testing with: " + testEmail + " / " + testPassword);
                
                UserDAO.User user = UserDAO.authenticateUser(testEmail, testPassword);
                if (user != null) {
                    System.out.println("✓ Authentication successful: " + user.getName() + " (" + user.getUserRole() + ")");
                } else {
                    System.out.println("✗ Authentication failed!");
                }
            }
            
            testRs.close();
            testStmt.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
