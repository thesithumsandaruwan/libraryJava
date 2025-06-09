/*
 * Book Data Access Object (DAO) Class
 * This class handles all book-related database operations
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
public class BookDAO {
    
    /**
     * Book model class
     */
    public static class Book {
        private int bookId;
        private String bookNo;
        private String bookName;
        private String author;
        private int publishYear;
        private boolean isAvailable;
        private Timestamp createdAt;
        
        // Constructors
        public Book() {}
        
        public Book(String bookNo, String bookName, String author, int publishYear) {
            this.bookNo = bookNo;
            this.bookName = bookName;
            this.author = author;
            this.publishYear = publishYear;
            this.isAvailable = true;
        }
        
        // Getters and Setters
        public int getBookId() { return bookId; }
        public void setBookId(int bookId) { this.bookId = bookId; }
        
        public String getBookNo() { return bookNo; }
        public void setBookNo(String bookNo) { this.bookNo = bookNo; }
        
        public String getBookName() { return bookName; }
        public void setBookName(String bookName) { this.bookName = bookName; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public int getPublishYear() { return publishYear; }
        public void setPublishYear(int publishYear) { this.publishYear = publishYear; }
        
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
    
    /**
     * Add a new book
     */
    public static boolean addBook(Book book) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            // Check if book number already exists
            if (bookNoExists(book.getBookNo())) {
                showError("Book number already exists! Please use a different book number.");
                return false;
            }
            
            String sql = "INSERT INTO books (book_no, book_name, author, publish_year) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, book.getBookNo());
            stmt.setString(2, book.getBookName());
            stmt.setString(3, book.getAuthor());
            stmt.setInt(4, book.getPublishYear());
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("Book added successfully!");
                return true;
            } else {
                showError("Failed to add book!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all books
     */
    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return books;
            }
            
            String sql = "SELECT * FROM books ORDER BY book_name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setBookNo(rs.getString("book_no"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setAvailable(rs.getBoolean("is_available"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
                books.add(book);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * Get available books only
     */
    public static List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return books;
            }
            
            String sql = "SELECT * FROM books WHERE is_available = TRUE ORDER BY book_name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setBookNo(rs.getString("book_no"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setAvailable(rs.getBoolean("is_available"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
                books.add(book);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * Update book information
     */
    public static boolean updateBook(Book book) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            String sql = "UPDATE books SET book_no = ?, book_name = ?, author = ?, publish_year = ? WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, book.getBookNo());
            stmt.setString(2, book.getBookName());
            stmt.setString(3, book.getAuthor());
            stmt.setInt(4, book.getPublishYear());
            stmt.setInt(5, book.getBookId());
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("Book updated successfully!");
                return true;
            } else {
                showError("Failed to update book!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete book
     */
    public static boolean deleteBook(int bookId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            // Check if book is currently borrowed
            String checkBorrowings = "SELECT COUNT(*) FROM borrowing WHERE book_id = ? AND is_returned = FALSE";
            PreparedStatement checkStmt = conn.prepareStatement(checkBorrowings);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int activeBorrowings = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            if (activeBorrowings > 0) {
                showError("Cannot delete book that is currently borrowed!");
                return false;
            }
            
            String sql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                showMessage("Book deleted successfully!");
                return true;
            } else {
                showError("Failed to delete book!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Search books by various criteria
     */
    public static List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return books;
            }
            
            String sql = """
                SELECT * FROM books 
                WHERE book_no LIKE ? OR book_name LIKE ? OR author LIKE ? 
                ORDER BY book_name
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setBookNo(rs.getString("book_no"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setAvailable(rs.getBoolean("is_available"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
                books.add(book);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return books;
    }
    
    /**
     * Get book by book number
     */
    public static Book getBookByBookNo(String bookNo) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT * FROM books WHERE book_no = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookNo);
            
            ResultSet rs = stmt.executeQuery();
            
            Book book = null;
            if (rs.next()) {
                book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setBookNo(rs.getString("book_no"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setAvailable(rs.getBoolean("is_available"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
            }
            
            rs.close();
            stmt.close();
            
            return book;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Update book availability status
     */
    public static boolean updateBookAvailability(int bookId, boolean isAvailable) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            
            String sql = "UPDATE books SET is_available = ? WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if book number already exists
     */
    private static boolean bookNoExists(String bookNo) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            
            String sql = "SELECT COUNT(*) FROM books WHERE book_no = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookNo);
            
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
