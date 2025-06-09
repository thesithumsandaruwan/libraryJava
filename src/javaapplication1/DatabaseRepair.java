/*
 * Database Repair Utility
 * This utility fixes common database issues like missing columns
 */
package javaapplication1;

import java.sql.*;

/**
 *
 * @author Database Repair
 */
public class DatabaseRepair {
    
    public static void main(String[] args) {
        System.out.println("=== Database Repair Utility ===");
        
        try {
            // Test connection
            System.out.println("1. Testing database connection...");
            if (!DatabaseConnection.testConnection()) {
                System.out.println("✗ Database connection failed!");
                return;
            }
            System.out.println("✓ Database connection successful!");
            
            // Get connection
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("✗ Could not get database connection!");
                return;
            }
            
            // Check and repair users table
            System.out.println("\n2. Checking users table structure...");
            repairUsersTable(conn);
            
            // Check and repair borrowings table
            System.out.println("\n3. Checking borrowings table structure...");
            repairBorrowingsTable(conn);
            
            // Verify repairs
            System.out.println("\n4. Verifying database structure...");
            verifyDatabaseStructure(conn);
            
            System.out.println("\n=== Database repair completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("\n✗ Database repair failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void repairUsersTable(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        // Check if user_role column exists
        ResultSet columns = metaData.getColumns(null, null, "users", "user_role");
        if (!columns.next()) {
            System.out.println("  Adding missing user_role column...");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("ALTER TABLE users ADD COLUMN user_role ENUM('MEMBER', 'LIBRARIAN', 'ADMIN') DEFAULT 'MEMBER'");
            stmt.close();
            System.out.println("  ✓ Added user_role column");
        } else {
            System.out.println("  ✓ user_role column exists");
        }
        columns.close();
        
        // Update any NULL user_role values to MEMBER
        Statement stmt = conn.createStatement();
        int updated = stmt.executeUpdate("UPDATE users SET user_role = 'MEMBER' WHERE user_role IS NULL");
        if (updated > 0) {
            System.out.println("  ✓ Updated " + updated + " users with missing roles");
        }
        stmt.close();
    }
    
    private static void repairBorrowingsTable(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        // Check if borrowing table exists
        ResultSet tables = metaData.getTables(null, null, "borrowing", null);
        if (!tables.next()) {
            // Check for borrowings table (alternative name)
            tables.close();
            tables = metaData.getTables(null, null, "borrowings", null);
            if (!tables.next()) {
                System.out.println("  Creating missing borrowings table...");
                Statement stmt = conn.createStatement();
                String createTable = """
                    CREATE TABLE borrowings (
                        borrow_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        book_id INT NOT NULL,
                        borrow_date DATE NOT NULL,
                        due_date DATE NOT NULL,
                        return_date DATE NULL,
                        fine_amount DECIMAL(10,2) DEFAULT 0.00,
                        is_returned BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(user_id),
                        FOREIGN KEY (book_id) REFERENCES books(book_id)
                    )
                """;
                stmt.executeUpdate(createTable);
                stmt.close();
                System.out.println("  ✓ Created borrowings table");
            } else {
                System.out.println("  ✓ borrowings table exists");
            }
        } else {
            System.out.println("  ✓ borrowing table exists");
        }
        tables.close();
    }
    
    private static void verifyDatabaseStructure(Connection conn) throws SQLException {
        // Verify users table
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("DESCRIBE users");
        System.out.println("  Users table structure:");
        while (rs.next()) {
            System.out.println("    " + rs.getString("Field") + " - " + rs.getString("Type"));
        }
        rs.close();
        
        // Verify books table
        rs = stmt.executeQuery("DESCRIBE books");
        System.out.println("  Books table structure:");
        while (rs.next()) {
            System.out.println("    " + rs.getString("Field") + " - " + rs.getString("Type"));
        }
        rs.close();
        
        // Check for borrowings/borrowing table
        try {
            rs = stmt.executeQuery("DESCRIBE borrowings");
            System.out.println("  Borrowings table structure:");
            while (rs.next()) {
                System.out.println("    " + rs.getString("Field") + " - " + rs.getString("Type"));
            }
            rs.close();
        } catch (SQLException e) {
            try {
                rs = stmt.executeQuery("DESCRIBE borrowing");
                System.out.println("  Borrowing table structure:");
                while (rs.next()) {
                    System.out.println("    " + rs.getString("Field") + " - " + rs.getString("Type"));
                }
                rs.close();
            } catch (SQLException e2) {
                System.out.println("  ✗ No borrowing/borrowings table found");
            }
        }
        
        stmt.close();
    }
}
