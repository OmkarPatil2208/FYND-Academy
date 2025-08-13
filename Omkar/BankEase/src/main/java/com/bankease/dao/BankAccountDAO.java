package com.bankease.dao;

import com.bankease.model.BankAccount;
import com.bankease.utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for BankAccount entity
 */
public class BankAccountDAO {
    
    /**
     * Create a new bank account
     * @param account BankAccount object to create
     * @return BankAccount with generated ID
     * @throws SQLException if database operation fails
     */
    public BankAccount createAccount(BankAccount account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, user_id, account_type, balance, creation_date, is_active, is_frozen) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setInt(2, account.getUserId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setTimestamp(5, Timestamp.valueOf(account.getCreationDate()));
            pstmt.setBoolean(6, account.isActive());
            pstmt.setBoolean(7, account.isFrozen());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setAccountId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        }
        
        return account;
    }
    
    /**
     * Find account by account number
     * @param accountNumber account number to search for
     * @return Optional containing BankAccount if found
     * @throws SQLException if database operation fails
     */
    public Optional<BankAccount> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Find account by ID
     * @param accountId account ID to search for
     * @return Optional containing BankAccount if found
     * @throws SQLException if database operation fails
     */
    public Optional<BankAccount> findById(int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all accounts for a user
     * @param userId user ID
     * @return List of accounts for the user
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY account_id";
        List<BankAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }
        
        return accounts;
    }
    
    /**
     * Get all accounts
     * @return List of all accounts
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY account_id";
        List<BankAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        
        return accounts;
    }
    
    /**
     * Update account balance
     * @param accountId account ID
     * @param newBalance new balance
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateBalance(int accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accountId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update account frozen status
     * @param accountId account ID
     * @param isFrozen frozen status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateFrozenStatus(int accountId, boolean isFrozen) throws SQLException {
        String sql = "UPDATE accounts SET is_frozen = ? WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isFrozen);
            pstmt.setInt(2, accountId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update account active status
     * @param accountId account ID
     * @param isActive active status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateActiveStatus(int accountId, boolean isActive) throws SQLException {
        String sql = "UPDATE accounts SET is_active = ? WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, accountId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete account by ID
     * @param accountId account ID to delete
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    public boolean deleteAccount(int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Check if account number exists
     * @param accountNumber account number to check
     * @return true if account number exists
     * @throws SQLException if database operation fails
     */
    public boolean accountNumberExists(String accountNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get total balance for a user
     * @param userId user ID
     * @return total balance
     * @throws SQLException if database operation fails
     */
    public double getTotalBalanceForUser(int userId) throws SQLException {
        String sql = "SELECT SUM(balance) FROM accounts WHERE user_id = ? AND is_active = true";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        
        return 0.0;
    }
    
    /**
     * Update account frozen status by account number
     * @param accountNumber account number
     * @param isFrozen frozen status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateFrozenStatusByAccountNumber(String accountNumber, boolean isFrozen) throws SQLException {
        String sql = "UPDATE accounts SET is_frozen = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isFrozen);
            pstmt.setString(2, accountNumber);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get total number of accounts
     * @return total number of accounts
     * @throws SQLException if database operation fails
     */
    public int getTotalAccounts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }

    /**
     * Get total balance across all accounts
     * @return total balance
     * @throws SQLException if database operation fails
     */
    public double getTotalBalance() throws SQLException {
        String sql = "SELECT SUM(balance) FROM accounts WHERE is_active = true";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        
        return 0.0;
    }

    /**
     * Search accounts by account number
     * @param accountNumber account number to search for
     * @return List of matching accounts
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> searchByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number LIKE ? ORDER BY account_id";
        List<BankAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + accountNumber + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }
        
        return accounts;
    }

    /**
     * Search accounts by account type
     * @param accountType account type to search for
     * @return List of matching accounts
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> searchByAccountType(String accountType) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_type = ? ORDER BY account_id";
        List<BankAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }
        
        return accounts;
    }

    /**
     * Map ResultSet to BankAccount object
     * @param rs ResultSet
     * @return BankAccount object
     * @throws SQLException if mapping fails
     */
    private BankAccount mapResultSetToAccount(ResultSet rs) throws SQLException {
        BankAccount account = new BankAccount();
        account.setAccountId(rs.getInt("account_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setUserId(rs.getInt("user_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getDouble("balance"));
        account.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        account.setActive(rs.getBoolean("is_active"));
        account.setFrozen(rs.getBoolean("is_frozen"));
        return account;
    }
} 