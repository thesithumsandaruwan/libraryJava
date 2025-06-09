/*
 * Borrowing Management Form
 * This form handles book borrowing and returning operations
 */
package javaapplication1;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Library Management System
 */
public class BorrowingManagement extends javax.swing.JFrame {

    private DefaultTableModel borrowingTableModel;
    private DefaultTableModel availableBooksModel;
    private DefaultTableModel usersModel;

    /**
     * Creates new form BorrowingManagement
     */
    public BorrowingManagement() {
        initComponents();
        initializeCustomComponents();
        loadData();
    }
    
    /**
     * Initialize custom components
     */
    private void initializeCustomComponents() {
        setTitle("Borrowing Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Current Borrowings Tab
        JPanel borrowingsPanel = createBorrowingsPanel();
        tabbedPane.addTab("Current Borrowings", borrowingsPanel);
        
        // Borrow Book Tab
        JPanel borrowPanel = createBorrowPanel();
        tabbedPane.addTab("Borrow Book", borrowPanel);
        
        // Return Book Tab
        JPanel returnPanel = createReturnPanel();
        tabbedPane.addTab("Return Book", returnPanel);
        
        // Overdue Books Tab
        JPanel overduePanel = createOverduePanel();
        tabbedPane.addTab("Overdue Books", overduePanel);
        
        // Add navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            SecondPage sp = new SecondPage();
            sp.setVisible(true);
            dispose();
        });
        buttonPanel.add(backButton);
        
        // Layout
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create current borrowings panel
     */
    private JPanel createBorrowingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Current Borrowings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Borrowing ID", "User", "Book No", "Book Name", "Borrow Date", "Due Date", "Days Overdue"};
        borrowingTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable borrowingTable = new JTable(borrowingTableModel);
        borrowingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(borrowingTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCurrentBorrowings());
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create borrow book panel
     */
    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Borrow Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Available books panel
        JPanel booksPanel = new JPanel(new BorderLayout());
        booksPanel.setBorder(BorderFactory.createTitledBorder("Available Books"));
        
        String[] bookColumns = {"Book ID", "Book No", "Book Name", "Author", "Year"};
        availableBooksModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable booksTable = new JTable(availableBooksModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        
        // Users panel
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBorder(BorderFactory.createTitledBorder("Users"));
        
        String[] userColumns = {"User ID", "Name", "Email", "Role"};
        usersModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable usersTable = new JTable(usersModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        
        contentPanel.add(booksPanel);
        contentPanel.add(usersPanel);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Borrow button
        JPanel borrowButtonPanel = new JPanel(new FlowLayout());
        JButton borrowButton = new JButton("Borrow Selected Book");
        borrowButton.addActionListener(e -> {
            int selectedBookRow = booksTable.getSelectedRow();
            int selectedUserRow = usersTable.getSelectedRow();
            
            if (selectedBookRow == -1 || selectedUserRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select both a book and a user!", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int bookId = (Integer) availableBooksModel.getValueAt(selectedBookRow, 0);
            int userId = (Integer) usersModel.getValueAt(selectedUserRow, 0);
            String bookName = (String) availableBooksModel.getValueAt(selectedBookRow, 2);
            String userName = (String) usersModel.getValueAt(selectedUserRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Borrow book \"" + bookName + "\" to user \"" + userName + "\"?", 
                "Confirm Borrowing", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (BorrowingDAO.borrowBook(userId, bookId)) {
                    loadData(); // Refresh all data
                }
            }
        });
        borrowButtonPanel.add(borrowButton);
        panel.add(borrowButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create return book panel
     */
    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Return Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Instructions
        JLabel instructionLabel = new JLabel("Select a borrowing from the Current Borrowings tab and use the Return button below.");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(instructionLabel, BorderLayout.CENTER);
        
        // Return button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton returnButton = new JButton("Return Selected Book");
        returnButton.addActionListener(e -> returnSelectedBook());
        buttonPanel.add(returnButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create overdue books panel
     */
    private JPanel createOverduePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Overdue Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.RED);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table for overdue books
        String[] columnNames = {"Borrowing ID", "User", "Book No", "Book Name", "Due Date", "Days Overdue", "Fine Amount"};
        DefaultTableModel overdueModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable overdueTable = new JTable(overdueModel);
        overdueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(overdueTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load overdue data
        loadOverdueBooks(overdueModel);
        
        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Overdue");
        refreshButton.addActionListener(e -> loadOverdueBooks(overdueModel));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Load all data
     */
    private void loadData() {
        loadCurrentBorrowings();
        loadAvailableBooks();
        loadUsers();
    }
    
    /**
     * Load current borrowings
     */
    private void loadCurrentBorrowings() {
        borrowingTableModel.setRowCount(0);
        List<BorrowingDAO.Borrowing> borrowings = BorrowingDAO.getCurrentBorrowings();
        
        for (BorrowingDAO.Borrowing borrowing : borrowings) {
            // Calculate days overdue
            long currentTime = System.currentTimeMillis();
            long dueTime = borrowing.getDueDate().getTime();
            long daysOverdue = (currentTime - dueTime) / (24 * 60 * 60 * 1000);
            String overdueText = daysOverdue > 0 ? String.valueOf(daysOverdue) : "0";
            
            Object[] row = {
                borrowing.getBorrowingId(),
                borrowing.getUserName(),
                borrowing.getBookNo(),
                borrowing.getBookName(),
                borrowing.getBorrowDate(),
                borrowing.getDueDate(),
                overdueText
            };
            borrowingTableModel.addRow(row);
        }
    }
    
    /**
     * Load available books
     */
    private void loadAvailableBooks() {
        availableBooksModel.setRowCount(0);
        List<BookDAO.Book> books = BookDAO.getAvailableBooks();
        
        for (BookDAO.Book book : books) {
            Object[] row = {
                book.getBookId(),
                book.getBookNo(),
                book.getBookName(),
                book.getAuthor(),
                book.getPublishYear()
            };
            availableBooksModel.addRow(row);
        }
    }
    
    /**
     * Load users
     */
    private void loadUsers() {
        usersModel.setRowCount(0);
        List<UserDAO.User> users = UserDAO.getAllUsers();
        
        for (UserDAO.User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getUserRole()
            };
            usersModel.addRow(row);
        }
    }
    
    /**
     * Load overdue books
     */
    private void loadOverdueBooks(DefaultTableModel model) {
        model.setRowCount(0);
        List<BorrowingDAO.Borrowing> overdueBooks = BorrowingDAO.getOverdueBorrowings();
        
        for (BorrowingDAO.Borrowing borrowing : overdueBooks) {
            long currentTime = System.currentTimeMillis();
            long dueTime = borrowing.getDueDate().getTime();
            long daysOverdue = (currentTime - dueTime) / (24 * 60 * 60 * 1000);
            double fineAmount = daysOverdue * 1.0; // $1 per day
            
            Object[] row = {
                borrowing.getBorrowingId(),
                borrowing.getUserName(),
                borrowing.getBookNo(),
                borrowing.getBookName(),
                borrowing.getDueDate(),
                daysOverdue,
                String.format("$%.2f", fineAmount)
            };
            model.addRow(row);
        }
    }
    
    /**
     * Return selected book
     */
    private void returnSelectedBook() {
        // Get selected borrowing from current borrowings table
        Component[] components = getAllComponents(this);
        JTable borrowingTable = null;
        
        for (Component comp : components) {
            if (comp instanceof JTable && ((JTable) comp).getModel() == borrowingTableModel) {
                borrowingTable = (JTable) comp;
                break;
            }
        }
        
        if (borrowingTable == null) {
            JOptionPane.showMessageDialog(this, 
                "Could not find borrowing table!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = borrowingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a borrowing from the Current Borrowings tab!", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int borrowingId = (Integer) borrowingTableModel.getValueAt(selectedRow, 0);
        String userName = (String) borrowingTableModel.getValueAt(selectedRow, 1);
        String bookName = (String) borrowingTableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Return book \"" + bookName + "\" from user \"" + userName + "\"?", 
            "Confirm Return", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (BorrowingDAO.returnBook(borrowingId)) {
                loadData(); // Refresh all data
            }
        }
    }
    
    /**
     * Get all components recursively
     */
    private Component[] getAllComponents(Container container) {
        java.util.List<Component> components = new java.util.ArrayList<>();
        Component[] comps = container.getComponents();
        
        for (Component comp : comps) {
            components.add(comp);
            if (comp instanceof Container) {
                Component[] subComps = getAllComponents((Container) comp);
                for (Component subComp : subComps) {
                    components.add(subComp);
                }
            }
        }
        
        return components.toArray(new Component[0]);
    }
    
    /**
     * NetBeans generated code placeholder
     */
    private void initComponents() {
        // This method would be generated by NetBeans
        // For now, we'll handle UI creation in initializeCustomComponents()
    }
    
    /**
     * Main method for testing
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new BorrowingManagement().setVisible(true);
        });
    }
}
