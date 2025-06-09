package javaapplication1;

import javax.swing.SwingUtilities;

/**
 * Complete application test
 */
public class CompleteTest {
    public static void main(String[] args) {
        System.out.println("=== Complete Application Test ===");
        
        // Test 1: Database connectivity
        System.out.println("1. Testing database connection...");
        if (!DatabaseConnection.testConnection()) {
            System.out.println("✗ Database connection failed!");
            return;
        }
        System.out.println("✓ Database connection successful!");
        
        // Test 2: Authentication
        System.out.println("\n2. Testing user authentication...");
        UserDAO.User testUser = UserDAO.authenticateUser("adheesha@gmail.com", "adheesha");
        if (testUser == null) {
            System.out.println("✗ Authentication failed!");
            return;
        }
        System.out.println("✓ Authentication successful: " + testUser.getName() + " (" + testUser.getUserRole() + ")");
        
        // Test 3: Session management
        System.out.println("\n3. Testing session management...");
        SessionManager.setCurrentUser(testUser);
        UserDAO.User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null || !currentUser.getEmail().equals(testUser.getEmail())) {
            System.out.println("✗ Session management failed!");
            return;
        }
        System.out.println("✓ Session management successful!");
        
        // Test 4: UI Creation
        System.out.println("\n4. Testing UI creation...");
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("Creating FirstPage...");
                    FirstPage fp = new FirstPage();
                    System.out.println("✓ FirstPage created successfully!");
                    
                    System.out.println("Creating SecondPage...");
                    SecondPage sp = new SecondPage();
                    System.out.println("✓ SecondPage created successfully!");
                    
                    // Show the login page
                    fp.setVisible(true);
                    
                    System.out.println("\n=== Test completed successfully! ===");
                    System.out.println("You can now:");
                    System.out.println("1. Login with: adheesha@gmail.com / adheesha (Librarian)");
                    System.out.println("2. Login with: kasun@gmail.com / kasun (Admin)");
                    System.out.println("3. Login with: admin@library.com / admin123 (Member)");
                    
                } catch (Exception e) {
                    System.err.println("✗ UI creation failed: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("✗ UI test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
