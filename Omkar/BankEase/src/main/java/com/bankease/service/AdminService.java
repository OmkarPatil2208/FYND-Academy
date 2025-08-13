package com.bankease.service;

import com.bankease.dao.AdminDAO;
import com.bankease.dao.BankAccountDAO;
import com.bankease.dao.TransactionDAO;
import com.bankease.dao.UserDAO;
import com.bankease.model.Admin;
import com.bankease.model.BankAccount;
import com.bankease.model.Transaction;
import com.bankease.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for admin operations
 */
public class AdminService {
    private final AdminDAO adminDAO;
    private final UserDAO userDAO;
    private final BankAccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AdminService() {
        this.adminDAO = new AdminDAO();
        this.userDAO = new UserDAO();
        this.accountDAO = new BankAccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Authenticate admin login
     * @param username username
     * @param password password
     * @return Admin object if authentication successful
     * @throws SQLException if database operation fails
     */
    public Optional<Admin> loginAdmin(String username, String password) throws SQLException {
        Optional<Admin> adminOpt = adminDAO.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPassword().equals(password) && admin.isActive()) {
                // Update last login
                admin.updateLastLogin();
                adminDAO.updateLastLogin(admin.getAdminId(), admin.getLastLogin());
                return Optional.of(admin);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Create a new admin
     * @param username username
     * @param password password
     * @param fullName full name
     * @param email email
     * @param role admin role
     * @return Admin object
     * @throws SQLException if database operation fails
     */
    public Admin createAdmin(String username, String password, String fullName, String email, String role) 
            throws SQLException {
        
        // Check if username already exists
        if (adminDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (adminDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        Admin admin = new Admin(username, password, fullName, email, role);
        return adminDAO.createAdmin(admin);
    }

    /**
     * Get all users
     * @return List of all users
     * @throws SQLException if database operation fails
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    /**
     * Get all accounts
     * @return List of all accounts
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> getAllAccounts() throws SQLException {
        return accountDAO.findAll();
    }

    /**
     * Get all transactions
     * @return List of all transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getAllTransactions() throws SQLException {
        return transactionDAO.findAll();
    }

    /**
     * Update account frozen status
     * @param accountNumber account number
     * @param isFrozen freeze status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateAccountFrozenStatus(String accountNumber, boolean isFrozen) throws SQLException {
        return accountDAO.updateFrozenStatusByAccountNumber(accountNumber, isFrozen);
    }

    /**
     * Get system statistics
     * @return array with [totalUsers, totalAccounts, totalTransactions, totalBalance]
     * @throws SQLException if database operation fails
     */
    public double[] getSystemStatistics() throws SQLException {
        int totalUsers = userDAO.getTotalUsers();
        int totalAccounts = accountDAO.getTotalAccounts();
        int totalTransactions = transactionDAO.getTotalTransactions();
        double totalBalance = accountDAO.getTotalBalance();
        
        return new double[]{totalUsers, totalAccounts, totalTransactions, totalBalance};
    }

    /**
     * Search users by criteria
     * @param criteria search criteria (username, email, fullname)
     * @param value search value
     * @return List of matching users
     * @throws SQLException if database operation fails
     */
    public List<User> searchUsers(String criteria, String value) throws SQLException {
        switch (criteria.toLowerCase()) {
            case "username":
                return userDAO.searchByUsername(value);
            case "email":
                return userDAO.searchByEmail(value);
            case "fullname":
                return userDAO.searchByFullName(value);
            default:
                throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }
    }

    /**
     * Search accounts by criteria
     * @param criteria search criteria (account_number, account_type, user_id)
     * @param value search value
     * @return List of matching accounts
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> searchAccounts(String criteria, String value) throws SQLException {
        switch (criteria.toLowerCase()) {
            case "account_number":
                return accountDAO.searchByAccountNumber(value);
            case "account_type":
                return accountDAO.searchByAccountType(value);
            case "user_id":
                try {
                    int userId = Integer.parseInt(value);
                    return accountDAO.findByUserId(userId);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid user ID: " + value);
                }
            default:
                throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }
    }

    /**
     * Get transactions by type
     * @param type transaction type
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getTransactionsByType(String type) throws SQLException {
        return transactionDAO.findByType(type);
    }

    /**
     * Get transactions by status
     * @param status transaction status
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getTransactionsByStatus(String status) throws SQLException {
        return transactionDAO.findByStatus(status);
    }

    /**
     * Get transactions by date range
     * @param startDate start date
     * @param endDate end date
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) 
            throws SQLException {
        return transactionDAO.findByDateRange(startDate, endDate);
    }

    /**
     * Get transactions by user ID
     * @param userId user ID
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        return transactionDAO.findByUserId(userId);
    }

    /**
     * Get transactions by account number
     * @param accountNumber account number
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getTransactionsByAccount(String accountNumber) throws SQLException {
        return transactionDAO.findByAccountNumber(accountNumber);
    }

    /**
     * Update admin
     * @param admin Admin object to update
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateAdmin(Admin admin) throws SQLException {
        return adminDAO.updateAdmin(admin);
    }

    /**
     * Delete admin
     * @param adminId admin ID to delete
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    public boolean deleteAdmin(int adminId) throws SQLException {
        return adminDAO.deleteAdmin(adminId);
    }

    /**
     * Get admin by ID
     * @param adminId admin ID
     * @return Optional containing Admin if found
     * @throws SQLException if database operation fails
     */
    public Optional<Admin> getAdminById(int adminId) throws SQLException {
        return adminDAO.findById(adminId);
    }

    /**
     * Get admins by role
     * @param role admin role
     * @return List of admins with the specified role
     * @throws SQLException if database operation fails
     */
    public List<Admin> getAdminsByRole(String role) throws SQLException {
        return adminDAO.findByRole(role);
    }

    /**
     * Get all admins
     * @return List of all admins
     * @throws SQLException if database operation fails
     */
    public List<Admin> getAllAdmins() throws SQLException {
        return adminDAO.findAll();
    }
} 