package javaapplication1;

/**
 * Add test librarian user
 */
public class AddTestLibrarian {
    public static void main(String[] args) {
        try {
            System.out.println("Adding test librarian user...");
            
            UserDAO.User testLibrarian = new UserDAO.User(
                "Test Librarian", 
                "123 Test Library St", 
                "1234567890", 
                "testlib@library.com", 
                "testlib123",
                UserDAO.UserRole.LIBRARIAN
            );
            
            boolean success = UserDAO.registerUser(testLibrarian);
            if (success) {
                System.out.println("✓ Test librarian created successfully!");
                System.out.println("  Email: testlib@library.com");
                System.out.println("  Password: testlib123");
                
                // Test authentication
                UserDAO.User authUser = UserDAO.authenticateUser("testlib@library.com", "testlib123");
                if (authUser != null) {
                    System.out.println("✓ Authentication test successful: " + authUser.getName() + " (" + authUser.getUserRole() + ")");
                } else {
                    System.out.println("✗ Authentication test failed!");
                }
            } else {
                System.out.println("✗ Failed to create test librarian (may already exist)");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
