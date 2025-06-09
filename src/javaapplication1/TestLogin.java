package javaapplication1;

/**
 * Test class to isolate the login issue
 */
public class TestLogin {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            
            // Test database connection
            if (!DatabaseConnection.testConnection()) {
                System.out.println("Database connection failed!");
                return;
            }
            System.out.println("Database connection successful!");
            
            // Test user authentication  
            System.out.println("Testing librarian login...");
            
            // Debug: Check what we're passing
            String testEmail = "adheesha@gmail.com";
            String testPassword = "adheesha";
            System.out.println("Attempting login with: '" + testEmail + "' / '" + testPassword + "'");
            
            UserDAO.User user = UserDAO.authenticateUser(testEmail, testPassword);
            
            if (user == null) {
                System.out.println("Authentication returned null. Let's check if user exists...");
                
                // Direct database check
                try {
                    java.sql.Connection conn = DatabaseConnection.getConnection();
                    java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
                    stmt.setString(1, testEmail);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        System.out.println("User found in database:");
                        System.out.println("  Email: '" + rs.getString("email") + "'");
                        System.out.println("  Password: '" + rs.getString("password") + "'");
                        System.out.println("  Role: '" + rs.getString("user_role") + "'");
                        
                        // Check if password matches
                        String dbPassword = rs.getString("password");
                        if (dbPassword.equals(testPassword)) {
                            System.out.println("  ✓ Password matches!");
                        } else {
                            System.out.println("  ✗ Password mismatch: expected '" + testPassword + "', got '" + dbPassword + "'");
                        }
                    } else {
                        System.out.println("User not found in database!");
                    }
                    
                    rs.close();
                    stmt.close();
                } catch (Exception e) {
                    System.out.println("Error checking database: " + e.getMessage());
                }
            }
            
            if (user != null) {
                System.out.println("User authenticated: " + user.getName() + " (" + user.getUserRole() + ")");
                
                // Set session
                SessionManager.setCurrentUser(user);
                
                // Try to create SecondPage
                System.out.println("Creating SecondPage...");
                SecondPage sp = new SecondPage();
                System.out.println("SecondPage created successfully!");
                sp.setVisible(true);
                
            } else {
                System.out.println("Authentication failed!");
            }
            
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
