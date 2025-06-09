# Library Management System

A comprehensive Java Swing application for managing library operations with MySQL database integration and role-based access control.

## Features

### 🎯 **Role-Based System**
- **Members**: Access to home page, borrowing books, viewing personal borrowing history
- **Librarians**: Access to book management, user management, borrowing operations
- **Admins**: Full system access including librarian management and system statistics

### 📚 **Core Functionality**
- **User Management**: Registration, authentication, role management
- **Book Management**: Add, delete, search, and manage book inventory
- **Borrowing System**: Book borrowing/returning with due date tracking and fine calculation
- **Admin Panel**: System statistics, user role management, librarian promotion/demotion

### 🛠 **Technical Features**
- MySQL database integration with automatic database/table creation
- Comprehensive error handling and validation
- Session management for user authentication
- Modern Java Swing GUI with role-based navigation

## Database Schema

### Tables
- **users**: User information with roles (MEMBER, LIBRARIAN, ADMIN)
- **books**: Book catalog with availability tracking
- **borrowings**: Borrowing records with due dates and return status

### Sample Data
The system automatically creates sample users for each role:
- **Member**: member@lib.com / password123
- **Librarian**: librarian@lib.com / password123  
- **Admin**: admin@lib.com / password123

## Setup Instructions

### Prerequisites
- Java 8 or higher
- MySQL Server 8.0+
- NetBeans IDE (optional, for GUI editing)

### Installation

1. **Clone/Download the project**
   ```bash
   git clone <repository-url>
   cd library-management-system
   ```

2. **Database Setup**
   - Start MySQL server
   - Update database credentials in `DatabaseConnection.java`:
     ```java
     private static final String USERNAME = "root"; // Your MySQL username
     private static final String PASSWORD = "";     // Your MySQL password
     ```

3. **Compile the Application**
   ```bash
   javac -cp "lib\*" -d "build\classes" src\javaapplication1\*.java
   ```

4. **Run the Application**
   ```bash
   java -cp "lib\*;build\classes" javaapplication1.FirstPage
   ```

## Usage Guide

### First Time Setup
1. Run the application - database and tables will be created automatically
2. Use sample login credentials or register a new account
3. Login with appropriate role to access different features

### Member Workflow
1. Login → Redirected to HomePage
2. View available books and borrowing history
3. Borrow books through the system

### Librarian Workflow  
1. Login → Access to SecondPage (main menu)
2. Manage Books: Add/Delete books in inventory
3. Manage Users: View user details, add new members
4. Handle Borrowing: Process book borrowing and returns

### Admin Workflow
1. Login → Full system access
2. All Librarian functions plus:
3. Admin Panel: Manage librarians, view system statistics
4. User Role Management: Promote/demote users between roles

## Project Structure

```
src/javaapplication1/
├── FirstPage.java          # Login form
├── Signup.java            # User registration  
├── HomePage.java          # Member dashboard
├── SecondPage.java        # Main menu (Librarian/Admin)
├── Book_Details.java     # Book management
├── User_Details.java     # User management
├── BorrowingManagement.java  # Borrowing operations
├── AdminPanel.java       # Admin-only features
├── DatabaseConnection.java  # Database utility
├── UserDAO.java          # User data access
├── BookDAO.java          # Book data access  
├── BorrowingDAO.java     # Borrowing data access
├── SessionManager.java   # Session management
└── DatabaseTest.java     # System testing
```

## Key Classes

- **DatabaseConnection**: Handles MySQL connectivity and table creation
- **UserDAO**: Complete user management with role-based authentication
- **BookDAO**: Book inventory management and CRUD operations
- **BorrowingDAO**: Borrowing system with fine calculation
- **SessionManager**: User session tracking across the application

## Testing

Run the database integration test:
```bash
java -cp "lib\*;build\classes" javaapplication1.DatabaseTest
```

This will verify:
- Database connectivity
- User registration and authentication
- Book management operations
- Data retrieval functionality

## Technologies Used

- **Java Swing**: GUI framework
- **MySQL**: Database management
- **JDBC**: Database connectivity
- **NetBeans**: IDE for GUI design (optional)

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support or questions, please open an issue in the repository or contact the development team.

---

**Status**: ✅ **Production Ready**  
**Last Updated**: June 2025  
**Version**: 1.0.0
