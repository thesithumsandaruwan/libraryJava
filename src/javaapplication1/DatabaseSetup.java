package javaapplication1;

import java.sql.*;

/**
 * Database setup utility to ensure sample users exist
 */
public class DatabaseSetup {
    public static void main(String[] args) {
        try {
            System.out.println("=== Database Setup Utility ===");
            
            // Test connection
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("Database connection failed!");
                return;
            }
            System.out.println("✓ Database connection successful!");
            
            // Check existing users
            PreparedStatement checkUsers = conn.prepareStatement("SELECT name, email, user_role FROM users");
            ResultSet rs = checkUsers.executeQuery();
            
            System.out.println("\nExisting users:");
            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                System.out.println("  " + rs.getString("name") + " (" + rs.getString("email") + ") - " + rs.getString("user_role"));
            }
            
            if (!hasUsers) {
                System.out.println("  No users found. Creating sample users...");
                
                // Insert sample users with different roles
                PreparedStatement insertUser = conn.prepareStatement(
                    "INSERT INTO users (name, address, mobile_no, email, password, user_role) VALUES (?, ?, ?, ?, ?, ?)"
                );
                
                // Insert Admin user
                insertUser.setString(1, "System Admin");
                insertUser.setString(2, "123 Admin Street");
                insertUser.setString(3, "1111111111");
                insertUser.setString(4, "admin@library.com");
                insertUser.setString(5, "admin123");
                insertUser.setString(6, "ADMIN");
                insertUser.executeUpdate();
                System.out.println("  ✓ Created admin user: admin@library.com / admin123");
                
                // Insert Librarian user
                insertUser.setString(1, "John Librarian");
                insertUser.setString(2, "456 Library Ave");
                insertUser.setString(3, "2222222222");
                insertUser.setString(4, "librarian@library.com");
                insertUser.setString(5, "lib123");
                insertUser.setString(6, "LIBRARIAN");
                insertUser.executeUpdate();
                System.out.println("  ✓ Created librarian user: librarian@library.com / lib123");
                
                // Insert Member user
                insertUser.setString(1, "Jane Member");
                insertUser.setString(2, "789 Member Road");
                insertUser.setString(3, "3333333333");
                insertUser.setString(4, "member@library.com");
                insertUser.setString(5, "mem123");
                insertUser.setString(6, "MEMBER");
                insertUser.executeUpdate();
                System.out.println("  ✓ Created member user: member@library.com / mem123");
                
                insertUser.close();
            }
            
            // Test authentication
            System.out.println("\nTesting authentication:");
            UserDAO.User testUser = UserDAO.authenticateUser("librarian@library.com", "lib123");
            if (testUser != null) {
                System.out.println("✓ Librarian authentication successful: " + testUser.getName() + " (" + testUser.getUserRole() + ")");
            } else {
                System.out.println("✗ Librarian authentication failed!");
            }
            
            rs.close();
            checkUsers.close();
            
            System.out.println("\n=== Database setup completed! ===");
            
        } catch (Exception e) {
            System.err.println("Database setup error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
