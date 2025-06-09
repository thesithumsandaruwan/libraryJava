/*
 * Borrowing Data Access Object (DAO) Class
 * This class handles all borrowing-related database operations
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
public class BorrowingDAO {
    
    /**
     * Borrowing model class
     */
    public static class Borrowing {
        private int borrowingId;
        private int userId;
        private int bookId;
        private String userName;
        private String bookName;
        private String bookNo;
        private Date borrowDate;
        private Date dueDate;
        private Date returnDate;
        private boolean isReturned;
        private double fineAmount;
        private Timestamp createdAt;
        
        // Constructors
        public Borrowing() {}
        
        public Borrowing(int userId, int bookId) {
            this.userId = userId;
            this.bookId = bookId;
            this.isReturned = false;
            this.fineAmount = 0.0;
        }
        
        // Getters and Setters
        public int getBorrowingId() { return borrowingId; }
        public void setBorrowingId(int borrowingId) { this.borrowingId = borrowingId; }
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public int getBookId() { return bookId; }
        public void setBookId(int bookId) { this.bookId = bookId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getBookName() { return bookName; }
        public void setBookName(String bookName) { this.bookName = bookName; }
        
        public String getBookNo() { return bookNo; }
        public void setBookNo(String bookNo) { this.bookNo = bookNo; }
        
        public Date getBorrowDate() { return borrowDate; }
        public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
        
        public Date getDueDate() { return dueDate; }
        public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
        
        public Date getReturnDate() { return returnDate; }
        public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
        
        public boolean isReturned() { return isReturned; }
        public void setReturned(boolean returned) { isReturned = returned; }
        
        public double getFineAmount() { return fineAmount; }
        public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
    
    /**
     * Borrow a book
     */
    public static boolean borrowBook(int userId, int bookId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            // Check if book is available
            String checkAvailability = "SELECT is_available FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkAvailability);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next() || !rs.getBoolean("is_available")) {
                showError("Book is not available for borrowing!");
                rs.close();
                checkStmt.close();
                return false;
            }
            rs.close();
            checkStmt.close();
            
            // Check if user already has this book borrowed
            String checkExisting = "SELECT COUNT(*) FROM borrowing WHERE user_id = ? AND book_id = ? AND is_returned = FALSE";
            PreparedStatement existingStmt = conn.prepareStatement(checkExisting);
            existingStmt.setInt(1, userId);
            existingStmt.setInt(2, bookId);
            ResultSet existingRs = existingStmt.executeQuery();
            existingRs.next();
            int existingBorrowings = existingRs.getInt(1);
            existingRs.close();
            existingStmt.close();
            
            if (existingBorrowings > 0) {
                showError("You have already borrowed this book!");
                return false;
            }
            
            // Calculate due date (14 days from now)
            long currentTime = System.currentTimeMillis();
            long dueTime = currentTime + (14 * 24 * 60 * 60 * 1000L); // 14 days
            Date borrowDate = new Date(currentTime);
            Date dueDate = new Date(dueTime);
            
            // Insert borrowing record
            String sql = "INSERT INTO borrowing (user_id, book_id, borrow_date, due_date, is_returned, fine_amount) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, borrowDate);
            stmt.setDate(4, dueDate);
            stmt.setBoolean(5, false);
            stmt.setDouble(6, 0.0);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                // Update book availability
                BookDAO.updateBookAvailability(bookId, false);
                showMessage("Book borrowed successfully! Due date: " + dueDate);
                return true;
            } else {
                showError("Failed to borrow book!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Return a book
     */
    public static boolean returnBook(int borrowingId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return false;
            }
            
            // Get borrowing details
            String getBorrowing = "SELECT book_id, due_date FROM borrowing WHERE borrowing_id = ? AND is_returned = FALSE";
            PreparedStatement getStmt = conn.prepareStatement(getBorrowing);
            getStmt.setInt(1, borrowingId);
            ResultSet rs = getStmt.executeQuery();
            
            if (!rs.next()) {
                showError("Borrowing record not found or book already returned!");
                rs.close();
                getStmt.close();
                return false;
            }
            
            int bookId = rs.getInt("book_id");
            Date dueDate = rs.getDate("due_date");
            rs.close();
            getStmt.close();
            
            // Calculate fine if overdue
            Date returnDate = new Date(System.currentTimeMillis());
            double fineAmount = 0.0;
            
            if (returnDate.after(dueDate)) {
                long overdueDays = (returnDate.getTime() - dueDate.getTime()) / (24 * 60 * 60 * 1000);
                fineAmount = overdueDays * 1.0; // $1 per day fine
            }
            
            // Update borrowing record
            String sql = "UPDATE borrowing SET return_date = ?, is_returned = TRUE, fine_amount = ? WHERE borrowing_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setDate(1, returnDate);
            stmt.setDouble(2, fineAmount);
            stmt.setInt(3, borrowingId);
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                // Update book availability
                BookDAO.updateBookAvailability(bookId, true);
                
                if (fineAmount > 0) {
                    showMessage("Book returned successfully! Fine amount: $" + String.format("%.2f", fineAmount));
                } else {
                    showMessage("Book returned successfully!");
                }
                return true;
            } else {
                showError("Failed to return book!");
                return false;
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all current borrowings (not returned)
     */
    public static List<Borrowing> getCurrentBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return borrowings;
            }
            
            String sql = "SELECT b.*, u.name as user_name, bk.book_name, bk.book_no " +
                        "FROM borrowing b " +
                        "JOIN users u ON b.user_id = u.user_id " +
                        "JOIN books bk ON b.book_id = bk.book_id " +
                        "WHERE b.is_returned = FALSE " +
                        "ORDER BY b.due_date";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Borrowing borrowing = new Borrowing();
                borrowing.setBorrowingId(rs.getInt("borrowing_id"));
                borrowing.setUserId(rs.getInt("user_id"));
                borrowing.setBookId(rs.getInt("book_id"));
                borrowing.setUserName(rs.getString("user_name"));
                borrowing.setBookName(rs.getString("book_name"));
                borrowing.setBookNo(rs.getString("book_no"));
                borrowing.setBorrowDate(rs.getDate("borrow_date"));
                borrowing.setDueDate(rs.getDate("due_date"));
                borrowing.setReturnDate(rs.getDate("return_date"));
                borrowing.setReturned(rs.getBoolean("is_returned"));
                borrowing.setFineAmount(rs.getDouble("fine_amount"));
                borrowing.setCreatedAt(rs.getTimestamp("created_at"));
                borrowings.add(borrowing);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return borrowings;
    }
    
    /**
     * Get borrowing history for a user
     */
    public static List<Borrowing> getUserBorrowingHistory(int userId) {
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return borrowings;
            }
            
            String sql = "SELECT b.*, u.name as user_name, bk.book_name, bk.book_no " +
                        "FROM borrowing b " +
                        "JOIN users u ON b.user_id = u.user_id " +
                        "JOIN books bk ON b.book_id = bk.book_id " +
                        "WHERE b.user_id = ? " +
                        "ORDER BY b.borrow_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrowing borrowing = new Borrowing();
                borrowing.setBorrowingId(rs.getInt("borrowing_id"));
                borrowing.setUserId(rs.getInt("user_id"));
                borrowing.setBookId(rs.getInt("book_id"));
                borrowing.setUserName(rs.getString("user_name"));
                borrowing.setBookName(rs.getString("book_name"));
                borrowing.setBookNo(rs.getString("book_no"));
                borrowing.setBorrowDate(rs.getDate("borrow_date"));
                borrowing.setDueDate(rs.getDate("due_date"));
                borrowing.setReturnDate(rs.getDate("return_date"));
                borrowing.setReturned(rs.getBoolean("is_returned"));
                borrowing.setFineAmount(rs.getDouble("fine_amount"));
                borrowing.setCreatedAt(rs.getTimestamp("created_at"));
                borrowings.add(borrowing);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return borrowings;
    }
    
    /**
     * Get overdue borrowings
     */
    public static List<Borrowing> getOverdueBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showError("Database connection failed!");
                return borrowings;
            }
            
            String sql = "SELECT b.*, u.name as user_name, bk.book_name, bk.book_no " +
                        "FROM borrowing b " +
                        "JOIN users u ON b.user_id = u.user_id " +
                        "JOIN books bk ON b.book_id = bk.book_id " +
                        "WHERE b.is_returned = FALSE AND b.due_date < CURDATE() " +
                        "ORDER BY b.due_date";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Borrowing borrowing = new Borrowing();
                borrowing.setBorrowingId(rs.getInt("borrowing_id"));
                borrowing.setUserId(rs.getInt("user_id"));
                borrowing.setBookId(rs.getInt("book_id"));
                borrowing.setUserName(rs.getString("user_name"));
                borrowing.setBookName(rs.getString("book_name"));
                borrowing.setBookNo(rs.getString("book_no"));
                borrowing.setBorrowDate(rs.getDate("borrow_date"));
                borrowing.setDueDate(rs.getDate("due_date"));
                borrowing.setReturnDate(rs.getDate("return_date"));
                borrowing.setReturned(rs.getBoolean("is_returned"));
                borrowing.setFineAmount(rs.getDouble("fine_amount"));
                borrowing.setCreatedAt(rs.getTimestamp("created_at"));
                borrowings.add(borrowing);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return borrowings;
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
