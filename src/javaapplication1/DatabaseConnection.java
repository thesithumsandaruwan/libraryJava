/*
 * Database Connection Utility Class for Library Management System
 * This class handles all database connections and provides utility methods
 * for database operations.
 */
package javaapplication1;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Library Management System
 */
public class DatabaseConnection {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "library_management";
    private static final String FULL_DB_URL = DB_URL + DB_NAME;
    private static final String USERNAME = "root"; // Change this to your MySQL username
    private static final String PASSWORD = ""; // Change this to your MySQL password
    
    private static Connection connection = null;
    
    /**
     * Get database connection
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Create database if it doesn't exist
                createDatabaseIfNotExists();
                
                // Connect to the database
                connection = DriverManager.getConnection(FULL_DB_URL, USERNAME, PASSWORD);
                
                // Create tables if they don't exist
                createTablesIfNotExist();
            }
        } catch (ClassNotFoundException e) {
            showError("MySQL JDBC Driver not found. Please add mysql-connector-java to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            showError("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Create database if it doesn't exist
     */
    private static void createDatabaseIfNotExists() {
        try {
            Connection tempConnection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = tempConnection.createStatement();
            
            String createDB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            statement.executeUpdate(createDB);
            
            statement.close();
            tempConnection.close();
        } catch (SQLException e) {
            showError("Failed to create database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create tables if they don't exist
     */
    private static void createTablesIfNotExist() {
        try {
            Statement statement = connection.createStatement();
            
            // Create Users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    address VARCHAR(255) NOT NULL,
                    mobile_no VARCHAR(15) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    user_role ENUM('MEMBER', 'LIBRARIAN', 'ADMIN') DEFAULT 'MEMBER',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            statement.executeUpdate(createUsersTable);
            
            // Check and add user_role column if it doesn't exist (for existing databases)
            ensureUserRoleColumnExists();
            
            // Create Books table
            String createBooksTable = """
                CREATE TABLE IF NOT EXISTS books (
                    book_id INT AUTO_INCREMENT PRIMARY KEY,
                    book_no VARCHAR(50) UNIQUE NOT NULL,
                    book_name VARCHAR(200) NOT NULL,
                    author VARCHAR(100) NOT NULL,
                    publish_year INT NOT NULL,
                    is_available BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            statement.executeUpdate(createBooksTable);
            
            // Create Borrowing table for tracking book loans
            String createBorrowingTable = """
                CREATE TABLE IF NOT EXISTS borrowing (
                    borrow_id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    book_id INT NOT NULL,
                    borrow_date DATE NOT NULL,
                    return_date DATE,
                    is_returned BOOLEAN DEFAULT FALSE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )
            """;
            statement.executeUpdate(createBorrowingTable);
            
            // Insert some sample data if tables are empty
            insertSampleData();
            
            statement.close();
        } catch (SQLException e) {
            showError("Failed to create tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ensure user_role column exists in users table (for database migration)
     */
    private static void ensureUserRoleColumnExists() {
        try {
            // Check if user_role column exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(DB_NAME, null, "users", "user_role");
            
            if (!columns.next()) {
                // Column doesn't exist, add it
                Statement statement = connection.createStatement();
                String addColumnSQL = "ALTER TABLE users ADD COLUMN user_role ENUM('MEMBER', 'LIBRARIAN', 'ADMIN') DEFAULT 'MEMBER'";
                statement.executeUpdate(addColumnSQL);
                statement.close();
                System.out.println("Added user_role column to existing users table");
            }
            columns.close();
        } catch (SQLException e) {
            System.err.println("Error checking/adding user_role column: " + e.getMessage());
        }
    }
    
    /**
     * Insert sample data for testing
     */
    private static void insertSampleData() {
        try {
            // Check if users table is empty
            PreparedStatement checkUsers = connection.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet rs = checkUsers.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                // Insert sample users with different roles
                PreparedStatement insertUser = connection.prepareStatement(
                    "INSERT INTO users (name, address, mobile_no, email, password, user_role) VALUES (?, ?, ?, ?, ?, ?)"
                );
                
                // Insert Admin user
                insertUser.setString(1, "System Admin");
                insertUser.setString(2, "123 Admin Street");
                insertUser.setString(3, "1111111111");
                insertUser.setString(4, "admin@library.com");
                insertUser.setString(5, "admin123"); // In real application, this should be hashed
                insertUser.setString(6, "ADMIN");
                insertUser.executeUpdate();
                
                // Insert Librarian user
                insertUser.setString(1, "John Librarian");
                insertUser.setString(2, "456 Library Ave");
                insertUser.setString(3, "2222222222");
                insertUser.setString(4, "librarian@library.com");
                insertUser.setString(5, "lib123");
                insertUser.setString(6, "LIBRARIAN");
                insertUser.executeUpdate();
                
                // Insert Member user
                insertUser.setString(1, "Jane Member");
                insertUser.setString(2, "789 Member Road");
                insertUser.setString(3, "3333333333");
                insertUser.setString(4, "member@library.com");
                insertUser.setString(5, "mem123");
                insertUser.setString(6, "MEMBER");
                insertUser.executeUpdate();
                
                insertUser.close();
            }
            
            // Check if books table is empty
            PreparedStatement checkBooks = connection.prepareStatement("SELECT COUNT(*) FROM books");
            rs = checkBooks.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                // Insert sample books
                String[][] sampleBooks = {
                    {"B001", "Java Programming", "James Gosling", "1995"},
                    {"B002", "Python for Beginners", "Guido van Rossum", "1991"},
                    {"B003", "Database Design", "Edgar Codd", "1970"},
                    {"B004", "Web Development", "Tim Berners-Lee", "1989"},
                    {"B005", "Software Engineering", "Ian Sommerville", "2000"}
                };
                
                PreparedStatement insertBook = connection.prepareStatement(
                    "INSERT INTO books (book_no, book_name, author, publish_year) VALUES (?, ?, ?, ?)"
                );
                
                for (String[] book : sampleBooks) {
                    insertBook.setString(1, book[0]);
                    insertBook.setString(2, book[1]);
                    insertBook.setString(3, book[2]);
                    insertBook.setInt(4, Integer.parseInt(book[3]));
                    insertBook.executeUpdate();
                }
                insertBook.close();
            }
            
            checkUsers.close();
            checkBooks.close();
        } catch (SQLException e) {
            System.err.println("Failed to insert sample data: " + e.getMessage());
        }
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show error message dialog
     */
    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
