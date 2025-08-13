package com.bankease.dao;

import com.bankease.model.Transaction;
import com.bankease.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transaction entity
 */
public class TransactionDAO {
    
    /**
     * Create a new transaction
     * @param transaction Transaction object to create
     * @return Transaction with generated ID
     * @throws SQLException if database operation fails
     */
    public Transaction createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_type, account_id, from_account_number, to_account_number, " +
                    "amount, description, timestamp, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, transaction.getTransactionType());
            pstmt.setInt(2, transaction.getAccountId());
            pstmt.setString(3, transaction.getFromAccountNumber());
            pstmt.setString(4, transaction.getToAccountNumber());
            pstmt.setDouble(5, transaction.getAmount());
            pstmt.setString(6, transaction.getDescription());
            pstmt.setTimestamp(7, Timestamp.valueOf(transaction.getTimestamp()));
            pstmt.setString(8, transaction.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        }
        
        return transaction;
    }
    
    /**
     * Find transaction by ID
     * @param transactionId transaction ID to search for
     * @return Optional containing Transaction if found
     * @throws SQLException if database operation fails
     */
    public Optional<Transaction> findById(int transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all transactions for an account
     * @param accountId account ID
     * @return List of transactions for the account
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Get all transactions for a user (across all accounts)
     * @param userId user ID
     * @return List of transactions for the user
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByUserId(int userId) throws SQLException {
        String sql = "SELECT t.* FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.account_id " +
                    "WHERE a.user_id = ? ORDER BY t.timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Get all transactions
     * @return List of all transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    
    /**
     * Get transactions by type
     * @param transactionType transaction type
     * @return List of transactions of the specified type
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByType(String transactionType) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_type = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transactionType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Get transactions by date range
     * @param startDate start date
     * @param endDate end date
     * @return List of transactions in the date range
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Get transactions by status
     * @param status transaction status
     * @return List of transactions with the specified status
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE status = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Update transaction status
     * @param transactionId transaction ID
     * @param status new status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateStatus(int transactionId, String status) throws SQLException {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, transactionId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get transaction statistics for an account
     * @param accountId account ID
     * @return array with [totalDeposits, totalWithdrawals, totalTransfersIn, totalTransfersOut]
     * @throws SQLException if database operation fails
     */
    public double[] getTransactionStatistics(int accountId) throws SQLException {
        String sql = "SELECT transaction_type, SUM(amount) as total FROM transactions " +
                    "WHERE account_id = ? AND status = 'COMPLETED' GROUP BY transaction_type";
        double[] stats = new double[4]; // [deposits, withdrawals, transfers_in, transfers_out]
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("transaction_type");
                    double total = rs.getDouble("total");
                    
                    switch (type) {
                        case "DEPOSIT":
                            stats[0] = total;
                            break;
                        case "WITHDRAWAL":
                            stats[1] = total;
                            break;
                        case "TRANSFER_IN":
                            stats[2] = total;
                            break;
                        case "TRANSFER_OUT":
                            stats[3] = total;
                            break;
                    }
                }
            }
        }
        
        return stats;
    }
    
    /**
     * Get total number of transactions
     * @return total number of transactions
     * @throws SQLException if database operation fails
     */
    public int getTotalTransactions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions";
        
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
     * Get transactions by account number
     * @param accountNumber account number
     * @return List of transactions for the account
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT t.* FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.account_id " +
                    "WHERE a.account_number = ? ORDER BY t.timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }

    /**
     * Map ResultSet to Transaction object
     * @param rs ResultSet
     * @return Transaction object
     * @throws SQLException if mapping fails
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setFromAccountNumber(rs.getString("from_account_number"));
        transaction.setToAccountNumber(rs.getString("to_account_number"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        transaction.setStatus(rs.getString("status"));
        return transaction;
    }
} 