/*
 * Session Management Class
 * This class manages user sessions and current logged-in user information
 */
package javaapplication1;

/**
 *
 * @author Library Management System
 */
public class SessionManager {
    
    private static UserDAO.User currentUser = null;
    
    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(UserDAO.User user) {
        currentUser = user;
    }
    
    /**
     * Get the current logged-in user
     */
    public static UserDAO.User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get current user's ID
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }
    
    /**
     * Get current user's name
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "";
    }
    
    /**
     * Get current user's email
     */
    public static String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }
    
    /**
     * Logout current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Check if current user is admin (for this demo, admin email)
     */
    public static boolean isCurrentUserAdmin() {
        return currentUser != null && 
               ("admin@library.com".equals(currentUser.getEmail()) || 
                currentUser.getEmail().toLowerCase().contains("admin"));
    }
}
