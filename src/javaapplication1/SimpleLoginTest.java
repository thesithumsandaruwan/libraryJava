package javaapplication1;

/**
 * Simple login test
 */
public class SimpleLoginTest {
    public static void main(String[] args) {
        System.out.println("=== Simple Login Test ===");
        
        // Test with known user credentials
        String[] testCredentials = {
            "admin@library.com:admin123",
            "kasun@gmail.com:kasun", 
            "adheesha@gmail.com:adheesha",
            "testlib@library.com:testlib123"
        };
        
        for (String credential : testCredentials) {
            String[] parts = credential.split(":");
            String email = parts[0];
            String password = parts[1];
            
            System.out.println("\nTesting: " + email + " / " + password);
            
            UserDAO.User user = UserDAO.authenticateUser(email, password);
            if (user != null) {
                System.out.println("✓ SUCCESS: " + user.getName() + " (" + user.getUserRole() + ")");
            } else {
                System.out.println("✗ FAILED");
            }
        }
    }
}
