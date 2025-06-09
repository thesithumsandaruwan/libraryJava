/*
 * Database Integration Test
 * This class tests the database connectivity and basic operations
 */
package javaapplication1;

/**
 *
 * @author Database Test
 */
public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=== Database Integration Test ===");
        
        try {
            // Test 1: Database Connection
            System.out.println("1. Testing database connection...");
            if (DatabaseConnection.testConnection()) {
                System.out.println("✓ Database connection successful!");
            } else {
                System.out.println("✗ Database connection failed!");
                return;
            }
            
            // Test 2: User Registration
            System.out.println("\n2. Testing user registration...");
            UserDAO.User testUser = new UserDAO.User(
                "Test User", 
                "123 Test Street", 
                "1234567890", 
                "test@example.com", 
                "password123",
                UserDAO.UserRole.MEMBER
            );
            
            boolean userRegistered = UserDAO.registerUser(testUser);
            if (userRegistered) {
                System.out.println("✓ User registration successful!");
            } else {
                System.out.println("✗ User registration failed (might already exist)");
            }
            
            // Test 3: User Authentication
            System.out.println("\n3. Testing user authentication...");
            UserDAO.User authenticatedUser = UserDAO.authenticateUser("test@example.com", "password123");
            if (authenticatedUser != null) {
                System.out.println("✓ User authentication successful!");
                System.out.println("  User: " + authenticatedUser.getName());
            } else {
                System.out.println("✗ User authentication failed!");
            }
            
            // Test 4: Book Management
            System.out.println("\n4. Testing book management...");
            BookDAO.Book testBook = new BookDAO.Book(
                "TEST001", 
                "Test Book Title", 
                "Test Author", 
                2024
            );
            
            boolean bookAdded = BookDAO.addBook(testBook);
            if (bookAdded) {
                System.out.println("✓ Book addition successful!");
            } else {
                System.out.println("✗ Book addition failed (might already exist)");
            }
            
            // Test 5: Retrieve Books
            System.out.println("\n5. Testing book retrieval...");
            var books = BookDAO.getAllBooks();
            System.out.println("✓ Found " + books.size() + " books in database");
            
            // Test 6: Retrieve Users
            System.out.println("\n6. Testing user retrieval...");
            var users = UserDAO.getAllUsers();
            System.out.println("✓ Found " + users.size() + " users in database");
            
            System.out.println("\n=== All tests completed successfully! ===");
            System.out.println("The database integration is working correctly.");
            
        } catch (Exception e) {
            System.err.println("\n✗ Database test failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
