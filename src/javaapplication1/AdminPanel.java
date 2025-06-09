/*
 * Admin Panel Form
 * This form provides admin-only functionality for managing librarians and system settings
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
public class AdminPanel extends javax.swing.JFrame {

    private DefaultTableModel librariansTableModel;
    private DefaultTableModel allUsersTableModel;

    /**
     * Creates new form AdminPanel
     */
    public AdminPanel() {
        // Check if current user is admin
        UserDAO.User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null || currentUser.getUserRole() != UserDAO.UserRole.ADMIN) {
            JOptionPane.showMessageDialog(null, 
                "Access Denied! Admin privileges required.", 
                "Unauthorized Access", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initComponents();
        initializeCustomComponents();
        loadData();
    }
    
    /**
     * Initialize custom components
     */
    private void initializeCustomComponents() {
        setTitle("Library Management System - Admin Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Librarian Management Tab
        JPanel librariansPanel = createLibrariansPanel();
        tabbedPane.addTab("Manage Librarians", librariansPanel);
        
        // All Users Tab
        JPanel usersPanel = createAllUsersPanel();
        tabbedPane.addTab("All Users", usersPanel);
        
        // System Statistics Tab
        JPanel statsPanel = createStatsPanel();
        tabbedPane.addTab("System Statistics", statsPanel);
        
        // Add navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            SecondPage sp = new SecondPage();
            sp.setVisible(true);
            dispose();
        });
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            SessionManager.logout();
            FirstPage fp = new FirstPage();
            fp.setVisible(true);
            dispose();
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(logoutButton);
        
        // Layout
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create librarians management panel
     */
    private JPanel createLibrariansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Librarian Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"User ID", "Name", "Email", "Mobile", "Address", "Created Date"};
        librariansTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable librariansTable = new JTable(librariansTableModel);
        librariansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(librariansTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addLibrarianButton = new JButton("Add New Librarian");
        addLibrarianButton.addActionListener(e -> addNewLibrarian());
        
        JButton promoteToLibrarianButton = new JButton("Promote Member to Librarian");
        promoteToLibrarianButton.addActionListener(e -> promoteToLibrarian());
        
        JButton demoteToMemberButton = new JButton("Demote to Member");
        demoteToMemberButton.addActionListener(e -> demoteToMember(librariansTable));
        
        JButton deleteLibrarianButton = new JButton("Delete Librarian");
        deleteLibrarianButton.addActionListener(e -> deleteLibrarian(librariansTable));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadLibrarians());
        
        buttonPanel.add(addLibrarianButton);
        buttonPanel.add(promoteToLibrarianButton);
        buttonPanel.add(demoteToMemberButton);
        buttonPanel.add(deleteLibrarianButton);
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create all users panel
     */
    private JPanel createAllUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("All Users Overview", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"User ID", "Name", "Email", "Role", "Mobile", "Address", "Created Date"};
        allUsersTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable allUsersTable = new JTable(allUsersTableModel);
        allUsersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(allUsersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAllUsers());
        
        JButton changeRoleButton = new JButton("Change User Role");
        changeRoleButton.addActionListener(e -> changeUserRole(allUsersTable));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(changeRoleButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create system statistics panel
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = new JLabel("System Statistics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(titleLabel, gbc);
        
        // Statistics labels
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Get statistics
        int totalBooks = BookDAO.getAllBooks().size();
        int availableBooks = BookDAO.getAvailableBooks().size();
        int totalUsers = UserDAO.getAllUsers().size();
        int currentBorrowings = BorrowingDAO.getCurrentBorrowings().size();
        int overdueBorrowings = BorrowingDAO.getOverdueBorrowings().size();
        
        // Count users by role
        int adminCount = 0, librarianCount = 0, memberCount = 0;
        List<UserDAO.User> allUsers = UserDAO.getAllUsers();
        for (UserDAO.User user : allUsers) {
            switch (user.getUserRole()) {
                case ADMIN: adminCount++; break;
                case LIBRARIAN: librarianCount++; break;
                case MEMBER: memberCount++; break;
            }
        }
        
        // Add stats to panel
        addStatRow(panel, gbc, 1, "Total Books:", String.valueOf(totalBooks));
        addStatRow(panel, gbc, 2, "Available Books:", String.valueOf(availableBooks));
        addStatRow(panel, gbc, 3, "Books Currently Borrowed:", String.valueOf(totalBooks - availableBooks));
        addStatRow(panel, gbc, 4, "Total Users:", String.valueOf(totalUsers));
        addStatRow(panel, gbc, 5, "Administrators:", String.valueOf(adminCount));
        addStatRow(panel, gbc, 6, "Librarians:", String.valueOf(librarianCount));
        addStatRow(panel, gbc, 7, "Members:", String.valueOf(memberCount));
        addStatRow(panel, gbc, 8, "Current Borrowings:", String.valueOf(currentBorrowings));
        addStatRow(panel, gbc, 9, "Overdue Books:", String.valueOf(overdueBorrowings));
        
        // Refresh button
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton refreshStatsButton = new JButton("Refresh Statistics");
        refreshStatsButton.addActionListener(e -> {
            // Recreate the stats panel
            Container parent = panel.getParent();
            parent.remove(panel);
            parent.add(createStatsPanel(), BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        });
        panel.add(refreshStatsButton, gbc);
        
        return panel;
    }
    
    /**
     * Helper method to add statistics row
     */
    private void addStatRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(valueComp, gbc);
    }
    
    /**
     * Load all data
     */
    private void loadData() {
        loadLibrarians();
        loadAllUsers();
    }
    
    /**
     * Load librarians data
     */
    private void loadLibrarians() {
        librariansTableModel.setRowCount(0);
        List<UserDAO.User> users = UserDAO.getAllUsers();
        
        for (UserDAO.User user : users) {
            if (user.getUserRole() == UserDAO.UserRole.LIBRARIAN) {
                Object[] row = {
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getMobileNo(),
                    user.getAddress(),
                    user.getCreatedAt()
                };
                librariansTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Load all users data
     */
    private void loadAllUsers() {
        allUsersTableModel.setRowCount(0);
        List<UserDAO.User> users = UserDAO.getAllUsers();
        
        for (UserDAO.User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getUserRole(),
                user.getMobileNo(),
                user.getAddress(),
                user.getCreatedAt()
            };
            allUsersTableModel.addRow(row);
        }
    }
    
    /**
     * Add new librarian
     */
    private void addNewLibrarian() {
        // Open signup form with librarian role pre-selected
        SwingUtilities.invokeLater(() -> {
            Signup signup = new Signup();
            signup.setVisible(true);
            // Note: You would need to modify Signup form to accept a default role parameter
        });
    }
    
    /**
     * Promote member to librarian
     */
    private void promoteToLibrarian() {
        // Show dialog to select member
        List<UserDAO.User> members = UserDAO.getAllUsers();
        java.util.List<UserDAO.User> membersList = new java.util.ArrayList<>();
        
        for (UserDAO.User user : members) {
            if (user.getUserRole() == UserDAO.UserRole.MEMBER) {
                membersList.add(user);
            }
        }
        
        if (membersList.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No members found to promote!", 
                "No Members", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] memberNames = new String[membersList.size()];
        for (int i = 0; i < membersList.size(); i++) {
            memberNames[i] = membersList.get(i).getName() + " (" + membersList.get(i).getEmail() + ")";
        }
        
        String selectedMember = (String) JOptionPane.showInputDialog(this, 
            "Select member to promote to librarian:", 
            "Promote to Librarian", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            memberNames, 
            memberNames[0]);
        
        if (selectedMember != null) {
            int selectedIndex = java.util.Arrays.asList(memberNames).indexOf(selectedMember);
            UserDAO.User user = membersList.get(selectedIndex);
            
            if (changeUserRole(user, UserDAO.UserRole.LIBRARIAN)) {
                loadData();
            }
        }
    }
    
    /**
     * Demote librarian to member
     */
    private void demoteToMember(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a librarian to demote!", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) librariansTableModel.getValueAt(selectedRow, 0);
        String userName = (String) librariansTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Demote librarian \"" + userName + "\" to member?", 
            "Confirm Demotion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO.User user = new UserDAO.User();
            user.setUserId(userId);
            if (changeUserRole(user, UserDAO.UserRole.MEMBER)) {
                loadData();
            }
        }
    }
    
    /**
     * Delete librarian
     */
    private void deleteLibrarian(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a librarian to delete!", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) librariansTableModel.getValueAt(selectedRow, 0);
        String userName = (String) librariansTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete librarian \"" + userName + "\"?\nThis action cannot be undone!", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (UserDAO.deleteUser(userId)) {
                loadData();
            }
        }
    }
    
    /**
     * Change user role
     */
    private void changeUserRole(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user!", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) allUsersTableModel.getValueAt(selectedRow, 0);
        String userName = (String) allUsersTableModel.getValueAt(selectedRow, 1);
        UserDAO.UserRole currentRole = (UserDAO.UserRole) allUsersTableModel.getValueAt(selectedRow, 3);
        
        UserDAO.UserRole[] roles = UserDAO.UserRole.values();
        UserDAO.UserRole selectedRole = (UserDAO.UserRole) JOptionPane.showInputDialog(this, 
            "Select new role for user \"" + userName + "\":", 
            "Change User Role", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            roles, 
            currentRole);
        
        if (selectedRole != null && selectedRole != currentRole) {
            UserDAO.User user = new UserDAO.User();
            user.setUserId(userId);
            if (changeUserRole(user, selectedRole)) {
                loadData();
            }
        }
    }
    
    /**
     * Helper method to change user role
     */
    private boolean changeUserRole(UserDAO.User user, UserDAO.UserRole newRole) {
        try {
            return UserDAO.updateUserRole(user.getUserId(), newRole);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error changing user role: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
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
            new AdminPanel().setVisible(true);
        });
    }
}
