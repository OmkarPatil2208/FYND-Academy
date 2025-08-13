package com.bankease.main;

import com.bankease.model.Admin;
import com.bankease.model.BankAccount;
import com.bankease.model.Transaction;
import com.bankease.model.User;
import com.bankease.service.AdminService;
import com.bankease.service.BankingService;
import com.bankease.utils.DatabaseConfig;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main application class for BankEase console application
 */
public class BankEaseApplication {
    private static final BankingService bankingService = new BankingService();
    private static final AdminService adminService = new AdminService();
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static Admin currentAdmin = null;

    public static void main(String[] args) {
        System.out.println("=== Welcome to BankEase - Banking Management System ===");
        
        // Test database connection
        if (!DatabaseConfig.testConnection()) {
            System.out.println("ERROR: Cannot connect to database. Please check your database configuration.");
            System.out.println("Database URL: " + DatabaseConfig.getUrl());
            System.out.println("Database Username: " + DatabaseConfig.getUsername());
            return;
        }
        
        System.out.println("Database connection successful!");
        
        // Initialize default admin if needed
        initializeDefaultAdmin();
        
        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1:
                        userLogin();
                        break;
                    case 2:
                        userRegistration();
                        break;
                    case 3:
                        adminLogin();
                        break;
                    case 4:
                        System.out.println("Thank you for using BankEase!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== BankEase Main Menu ===");
        System.out.println("1. User Login");
        System.out.println("2. User Registration");
        System.out.println("3. Admin Login");
        System.out.println("4. Exit");
        System.out.println("==========================");
    }

    private static void userLogin() {
        System.out.println("\n=== User Login ===");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        
        try {
            Optional<User> userOpt = bankingService.loginUser(username, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("Login successful! Welcome, " + currentUser.getFullName());
                showUserMenu();
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void userRegistration() {
        System.out.println("\n=== User Registration ===");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        String email = getStringInput("Email: ");
        String fullName = getStringInput("Full Name: ");
        String phoneNumber = getStringInput("Phone Number: ");
        
        try {
            User user = bankingService.registerUser(username, password, email, fullName, phoneNumber);
            System.out.println("Registration successful! User ID: " + user.getUserId());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    private static void adminLogin() {
        System.out.println("\n=== Admin Login ===");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        
        try {
            Optional<Admin> adminOpt = adminService.loginAdmin(username, password);
            if (adminOpt.isPresent()) {
                currentAdmin = adminOpt.get();
                System.out.println("Admin login successful! Welcome, " + currentAdmin.getFullName());
                showAdminMenu();
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void showUserMenu() {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Accounts");
            System.out.println("2. Create New Account");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer");
            System.out.println("6. View Transaction History");
            System.out.println("7. View Account Statistics");
            System.out.println("8. Logout");
            System.out.println("==================");
            
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        viewUserAccounts();
                        break;
                    case 2:
                        createNewAccount();
                        break;
                    case 3:
                        performDeposit();
                        break;
                    case 4:
                        performWithdrawal();
                        break;
                    case 5:
                        performTransfer();
                        break;
                    case 6:
                        viewTransactionHistory();
                        break;
                    case 7:
                        viewAccountStatistics();
                        break;
                    case 8:
                        currentUser = null;
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. View All Users");
            System.out.println("2. View All Accounts");
            System.out.println("3. View All Transactions");
            System.out.println("4. Freeze/Unfreeze Account");
            System.out.println("5. View System Statistics");
            System.out.println("6. Search Users");
            System.out.println("7. Search Accounts");
            System.out.println("8. View Transactions by Filter");
            System.out.println("9. Logout");
            System.out.println("===================");
            
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        viewAllUsers();
                        break;
                    case 2:
                        viewAllAccounts();
                        break;
                    case 3:
                        viewAllTransactions();
                        break;
                    case 4:
                        freezeUnfreezeAccount();
                        break;
                    case 5:
                        viewSystemStatistics();
                        break;
                    case 6:
                        searchUsers();
                        break;
                    case 7:
                        searchAccounts();
                        break;
                    case 8:
                        viewTransactionsByFilter();
                        break;
                    case 9:
                        currentAdmin = null;
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void viewUserAccounts() throws SQLException {
        List<BankAccount> accounts = bankingService.getUserAccounts(currentUser.getUserId());
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\n=== Your Accounts ===");
        System.out.printf("%-15s %-12s %-15s %-10s %-10s%n", 
                         "Account Number", "Type", "Balance", "Status", "Frozen");
        System.out.println("------------------------------------------------------------");
        
        for (BankAccount account : accounts) {
            System.out.printf("%-15s %-12s $%-14.2f %-10s %-10s%n",
                             account.getAccountNumber(),
                             account.getAccountType(),
                             account.getBalance(),
                             account.isActive() ? "Active" : "Inactive",
                             account.isFrozen() ? "Yes" : "No");
        }
    }

    private static void createNewAccount() throws SQLException {
        System.out.println("\n=== Create New Account ===");
        System.out.println("Account Types: SAVINGS, CHECKING, FIXED_DEPOSIT");
        String accountType = getStringInput("Account Type: ").toUpperCase();
        double initialBalance = getDoubleInput("Initial Balance: ");
        
        BankAccount account = bankingService.createAccount(currentUser.getUserId(), accountType, initialBalance);
        System.out.println("Account created successfully!");
        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Initial Balance: $" + account.getBalance());
    }

    private static void performDeposit() throws SQLException {
        System.out.println("\n=== Deposit ===");
        String accountNumber = getStringInput("Account Number: ");
        double amount = getDoubleInput("Amount: ");
        String description = getStringInput("Description: ");
        
        try {
            Transaction transaction = bankingService.deposit(accountNumber, amount, description);
            System.out.println("Deposit successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("New Balance: $" + bankingService.getAccountBalance(accountNumber));
        } catch (Exception e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void performWithdrawal() throws SQLException {
        System.out.println("\n=== Withdraw ===");
        String accountNumber = getStringInput("Account Number: ");
        double amount = getDoubleInput("Amount: ");
        String description = getStringInput("Description: ");
        
        try {
            Transaction transaction = bankingService.withdraw(accountNumber, amount, description);
            System.out.println("Withdrawal successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("New Balance: $" + bankingService.getAccountBalance(accountNumber));
        } catch (Exception e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private static void performTransfer() throws SQLException {
        System.out.println("\n=== Transfer ===");
        String fromAccount = getStringInput("From Account Number: ");
        String toAccount = getStringInput("To Account Number: ");
        double amount = getDoubleInput("Amount: ");
        String description = getStringInput("Description: ");
        
        try {
            List<Transaction> transactions = bankingService.transfer(fromAccount, toAccount, amount, description);
            System.out.println("Transfer successful!");
            System.out.println("Transaction IDs: " + transactions.get(0).getTransactionId() + 
                             ", " + transactions.get(1).getTransactionId());
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void viewTransactionHistory() throws SQLException {
        System.out.println("\n=== Transaction History ===");
        List<Transaction> transactions = bankingService.getUserTransactionHistory(currentUser.getUserId());
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.printf("%-12s %-15s %-12s %-15s %-20s%n", 
                         "ID", "Type", "Amount", "Status", "Timestamp");
        System.out.println("------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-12d %-15s $%-11.2f %-15s %-20s%n",
                             transaction.getTransactionId(),
                             transaction.getTransactionType(),
                             transaction.getAmount(),
                             transaction.getStatus(),
                             transaction.getFormattedTimestamp());
        }
    }

    private static void viewAccountStatistics() throws SQLException {
        System.out.println("\n=== Account Statistics ===");
        String accountNumber = getStringInput("Account Number: ");
        
        try {
            double[] stats = bankingService.getAccountStatistics(accountNumber);
            System.out.println("Total Deposits: $" + stats[0]);
            System.out.println("Total Withdrawals: $" + stats[1]);
            System.out.println("Total Transfers In: $" + stats[2]);
            System.out.println("Total Transfers Out: $" + stats[3]);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllUsers() throws SQLException {
        List<User> users = adminService.getAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        System.out.println("\n=== All Users ===");
        System.out.printf("%-8s %-15s %-25s %-20s %-10s%n", 
                         "ID", "Username", "Full Name", "Email", "Status");
        System.out.println("------------------------------------------------------------");
        
        for (User user : users) {
            System.out.printf("%-8d %-15s %-25s %-20s %-10s%n",
                             user.getUserId(),
                             user.getUsername(),
                             user.getFullName(),
                             user.getEmail(),
                             user.isActive() ? "Active" : "Inactive");
        }
    }

    private static void viewAllAccounts() throws SQLException {
        List<BankAccount> accounts = adminService.getAllAccounts();
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\n=== All Accounts ===");
        System.out.printf("%-8s %-15s %-8s %-12s %-15s %-10s %-10s%n", 
                         "ID", "Account Number", "User ID", "Type", "Balance", "Status", "Frozen");
        System.out.println("------------------------------------------------------------");
        
        for (BankAccount account : accounts) {
            System.out.printf("%-8d %-15s %-8d %-12s $%-14.2f %-10s %-10s%n",
                             account.getAccountId(),
                             account.getAccountNumber(),
                             account.getUserId(),
                             account.getAccountType(),
                             account.getBalance(),
                             account.isActive() ? "Active" : "Inactive",
                             account.isFrozen() ? "Yes" : "No");
        }
    }

    private static void viewAllTransactions() throws SQLException {
        List<Transaction> transactions = adminService.getAllTransactions();
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("\n=== All Transactions ===");
        System.out.printf("%-8s %-15s %-8s %-12s %-15s %-20s%n", 
                         "ID", "Type", "Account ID", "Amount", "Status", "Timestamp");
        System.out.println("------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-8d %-15s %-8d $%-11.2f %-15s %-20s%n",
                             transaction.getTransactionId(),
                             transaction.getTransactionType(),
                             transaction.getAccountId(),
                             transaction.getAmount(),
                             transaction.getStatus(),
                             transaction.getFormattedTimestamp());
        }
    }

    private static void freezeUnfreezeAccount() throws SQLException {
        System.out.println("\n=== Freeze/Unfreeze Account ===");
        String accountNumber = getStringInput("Account Number: ");
        String action = getStringInput("Action (freeze/unfreeze): ").toLowerCase();
        
        boolean isFrozen = action.equals("freeze");
        
        try {
            boolean success = adminService.updateAccountFrozenStatus(accountNumber, isFrozen);
            if (success) {
                System.out.println("Account " + (isFrozen ? "frozen" : "unfrozen") + " successfully.");
            } else {
                System.out.println("Failed to update account status.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewSystemStatistics() throws SQLException {
        double[] stats = adminService.getSystemStatistics();
        
        System.out.println("\n=== System Statistics ===");
        System.out.println("Total Users: " + (int) stats[0]);
        System.out.println("Total Accounts: " + (int) stats[1]);
        System.out.println("Total Transactions: " + (int) stats[2]);
        System.out.println("Total Balance: $" + stats[3]);
    }

    private static void searchUsers() throws SQLException {
        System.out.println("\n=== Search Users ===");
        System.out.println("Search by: username, email, fullname");
        String criteria = getStringInput("Search criteria: ").toLowerCase();
        String value = getStringInput("Search value: ");
        
        List<User> users = adminService.searchUsers(criteria, value);
        
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        System.out.println("\n=== Search Results ===");
        System.out.printf("%-8s %-15s %-25s %-20s%n", 
                         "ID", "Username", "Full Name", "Email");
        System.out.println("------------------------------------------------------------");
        
        for (User user : users) {
            System.out.printf("%-8d %-15s %-25s %-20s%n",
                             user.getUserId(),
                             user.getUsername(),
                             user.getFullName(),
                             user.getEmail());
        }
    }

    private static void searchAccounts() throws SQLException {
        System.out.println("\n=== Search Accounts ===");
        System.out.println("Search by: account_number, account_type, user_id");
        String criteria = getStringInput("Search criteria: ").toLowerCase();
        String value = getStringInput("Search value: ");
        
        List<BankAccount> accounts = adminService.searchAccounts(criteria, value);
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\n=== Search Results ===");
        System.out.printf("%-8s %-15s %-8s %-12s %-15s%n", 
                         "ID", "Account Number", "User ID", "Type", "Balance");
        System.out.println("------------------------------------------------------------");
        
        for (BankAccount account : accounts) {
            System.out.printf("%-8d %-15s %-8d %-12s $%-14.2f%n",
                             account.getAccountId(),
                             account.getAccountNumber(),
                             account.getUserId(),
                             account.getAccountType(),
                             account.getBalance());
        }
    }

    private static void viewTransactionsByFilter() throws SQLException {
        System.out.println("\n=== View Transactions by Filter ===");
        System.out.println("1. By Type");
        System.out.println("2. By Status");
        System.out.println("3. By Date Range");
        System.out.println("4. By User ID");
        System.out.println("5. By Account Number");
        
        int choice = getIntInput("Enter your choice: ");
        List<Transaction> transactions = null;
        
        switch (choice) {
            case 1:
                String type = getStringInput("Transaction Type: ");
                transactions = adminService.getTransactionsByType(type);
                break;
            case 2:
                String status = getStringInput("Transaction Status: ");
                transactions = adminService.getTransactionsByStatus(status);
                break;
            case 3:
                System.out.println("Enter start date (YYYY-MM-DD): ");
                String startDateStr = getStringInput("Start Date: ");
                System.out.println("Enter end date (YYYY-MM-DD): ");
                String endDateStr = getStringInput("End Date: ");
                LocalDateTime startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
                LocalDateTime endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
                transactions = adminService.getTransactionsByDateRange(startDate, endDate);
                break;
            case 4:
                int userId = getIntInput("User ID: ");
                transactions = adminService.getTransactionsByUserId(userId);
                break;
            case 5:
                String accountNumber = getStringInput("Account Number: ");
                transactions = adminService.getTransactionsByAccount(accountNumber);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (transactions == null || transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("\n=== Filtered Transactions ===");
        System.out.printf("%-8s %-15s %-8s %-12s %-15s %-20s%n", 
                         "ID", "Type", "Account ID", "Amount", "Status", "Timestamp");
        System.out.println("------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-8d %-15s %-8d $%-11.2f %-15s %-20s%n",
                             transaction.getTransactionId(),
                             transaction.getTransactionType(),
                             transaction.getAccountId(),
                             transaction.getAmount(),
                             transaction.getStatus(),
                             transaction.getFormattedTimestamp());
        }
    }

    private static void initializeDefaultAdmin() {
        try {
            // Check if any admin exists
            List<Admin> admins = adminService.getAllAdmins();
            if (admins.isEmpty()) {
                // Create default admin only if no admins exist
                Admin defaultAdmin = adminService.createAdmin("admin", "admin123", 
                                                           "System Administrator", "admin@bankease.com", "SUPER_ADMIN");
                System.out.println("Default admin created: admin/admin123");
            } else {
                System.out.println("Admin accounts already exist. Skipping default admin creation.");
            }
        } catch (SQLException e) {
            System.out.println("Warning: Could not initialize default admin: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handle case where admin already exists
            System.out.println("Admin already exists. Skipping default admin creation.");
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
} 