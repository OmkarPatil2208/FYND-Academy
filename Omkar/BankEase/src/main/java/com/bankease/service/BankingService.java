package com.bankease.service;

import com.bankease.dao.BankAccountDAO;
import com.bankease.dao.TransactionDAO;
import com.bankease.dao.UserDAO;
import com.bankease.exceptions.AccountFrozenException;
import com.bankease.exceptions.InsufficientFundsException;
import com.bankease.exceptions.InvalidAccountException;
import com.bankease.model.BankAccount;
import com.bankease.model.Transaction;
import com.bankease.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Service class for banking operations
 */
public class BankingService {
    private final UserDAO userDAO;
    private final BankAccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final ReentrantLock transferLock = new ReentrantLock();

    public BankingService() {
        this.userDAO = new UserDAO();
        this.accountDAO = new BankAccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Register a new user
     * @param username username
     * @param password password
     * @param email email
     * @param fullName full name
     * @param phoneNumber phone number
     * @return User object
     * @throws SQLException if database operation fails
     */
    public User registerUser(String username, String password, String email, String fullName, String phoneNumber) 
            throws SQLException {
        
        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = new User(username, password, email, fullName, phoneNumber);
        return userDAO.createUser(user);
    }

    /**
     * Authenticate user login
     * @param username username
     * @param password password
     * @return User object if authentication successful
     * @throws SQLException if database operation fails
     */
    public Optional<User> loginUser(String username, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password) && user.isActive()) {
                // Load user's accounts
                List<BankAccount> accounts = accountDAO.findByUserId(user.getUserId());
                user.setAccounts(accounts);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Create a new bank account
     * @param userId user ID
     * @param accountType account type (SAVINGS, CHECKING, FIXED_DEPOSIT)
     * @param initialBalance initial balance
     * @return BankAccount object
     * @throws SQLException if database operation fails
     */
    public BankAccount createAccount(int userId, String accountType, double initialBalance) throws SQLException {
        // Generate unique account number
        String accountNumber = generateAccountNumber();
        
        BankAccount account = new BankAccount(accountNumber, userId, accountType, initialBalance);
        account = accountDAO.createAccount(account);
        
        // Create initial deposit transaction if balance > 0
        if (initialBalance > 0) {
            Transaction transaction = new Transaction("DEPOSIT", account.getAccountId(), initialBalance, "Initial deposit");
            transaction.setStatus("COMPLETED");
            transactionDAO.createTransaction(transaction);
        }
        
        return account;
    }

    /**
     * Deposit money into an account
     * @param accountNumber account number
     * @param amount amount to deposit
     * @param description transaction description
     * @return Transaction object
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     * @throws AccountFrozenException if account is frozen
     */
    public Transaction deposit(String accountNumber, double amount, String description) 
            throws SQLException, InvalidAccountException, AccountFrozenException {
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Optional<BankAccount> accountOpt = accountDAO.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new InvalidAccountException("Account not found: " + accountNumber);
        }
        
        BankAccount account = accountOpt.get();
        
        if (account.isFrozen()) {
            throw new AccountFrozenException("Account is frozen: " + accountNumber);
        }
        
        if (!account.isActive()) {
            throw new InvalidAccountException("Account is inactive: " + accountNumber);
        }
        
        // Update balance
        double newBalance = account.getBalance() + amount;
        accountDAO.updateBalance(account.getAccountId(), newBalance);
        account.setBalance(newBalance);
        
        // Create transaction
        Transaction transaction = new Transaction("DEPOSIT", account.getAccountId(), amount, description);
        transaction.setStatus("COMPLETED");
        return transactionDAO.createTransaction(transaction);
    }

    /**
     * Withdraw money from an account
     * @param accountNumber account number
     * @param amount amount to withdraw
     * @param description transaction description
     * @return Transaction object
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     * @throws AccountFrozenException if account is frozen
     * @throws InsufficientFundsException if insufficient funds
     */
    public Transaction withdraw(String accountNumber, double amount, String description) 
            throws SQLException, InvalidAccountException, AccountFrozenException, InsufficientFundsException {
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Optional<BankAccount> accountOpt = accountDAO.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new InvalidAccountException("Account not found: " + accountNumber);
        }
        
        BankAccount account = accountOpt.get();
        
        if (account.isFrozen()) {
            throw new AccountFrozenException("Account is frozen: " + accountNumber);
        }
        
        if (!account.isActive()) {
            throw new InvalidAccountException("Account is inactive: " + accountNumber);
        }
        
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds. Balance: $" + account.getBalance() + 
                                               ", Required: $" + amount);
        }
        
        // Update balance
        double newBalance = account.getBalance() - amount;
        accountDAO.updateBalance(account.getAccountId(), newBalance);
        account.setBalance(newBalance);
        
        // Create transaction
        Transaction transaction = new Transaction("WITHDRAWAL", account.getAccountId(), amount, description);
        transaction.setStatus("COMPLETED");
        return transactionDAO.createTransaction(transaction);
    }

    /**
     * Transfer money between accounts
     * @param fromAccountNumber source account number
     * @param toAccountNumber destination account number
     * @param amount amount to transfer
     * @param description transaction description
     * @return List of Transaction objects (withdrawal and deposit)
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     * @throws AccountFrozenException if account is frozen
     * @throws InsufficientFundsException if insufficient funds
     */
    public List<Transaction> transfer(String fromAccountNumber, String toAccountNumber, double amount, String description) 
            throws SQLException, InvalidAccountException, AccountFrozenException, InsufficientFundsException {
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Use lock to ensure thread safety for transfers
        transferLock.lock();
        try {
            // Get source account
            Optional<BankAccount> fromAccountOpt = accountDAO.findByAccountNumber(fromAccountNumber);
            if (fromAccountOpt.isEmpty()) {
                throw new InvalidAccountException("Source account not found: " + fromAccountNumber);
            }
            
            // Get destination account
            Optional<BankAccount> toAccountOpt = accountDAO.findByAccountNumber(toAccountNumber);
            if (toAccountOpt.isEmpty()) {
                throw new InvalidAccountException("Destination account not found: " + toAccountNumber);
            }
            
            BankAccount fromAccount = fromAccountOpt.get();
            BankAccount toAccount = toAccountOpt.get();
            
            // Validate accounts
            if (fromAccount.isFrozen()) {
                throw new AccountFrozenException("Source account is frozen: " + fromAccountNumber);
            }
            
            if (toAccount.isFrozen()) {
                throw new AccountFrozenException("Destination account is frozen: " + toAccountNumber);
            }
            
            if (!fromAccount.isActive()) {
                throw new InvalidAccountException("Source account is inactive: " + fromAccountNumber);
            }
            
            if (!toAccount.isActive()) {
                throw new InvalidAccountException("Destination account is inactive: " + toAccountNumber);
            }
            
            if (fromAccount.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient funds in source account. Balance: $" + 
                                                   fromAccount.getBalance() + ", Required: $" + amount);
            }
            
            // Update balances
            double fromNewBalance = fromAccount.getBalance() - amount;
            double toNewBalance = toAccount.getBalance() + amount;
            
            accountDAO.updateBalance(fromAccount.getAccountId(), fromNewBalance);
            accountDAO.updateBalance(toAccount.getAccountId(), toNewBalance);
            
            // Create transactions
            Transaction withdrawalTransaction = new Transaction("TRANSFER_OUT", fromAccount.getAccountId(), 
                                                             amount, description + " (Transfer to " + toAccountNumber + ")");
            withdrawalTransaction.setStatus("COMPLETED");
            withdrawalTransaction.setToAccountNumber(toAccountNumber);
            
            Transaction depositTransaction = new Transaction("TRANSFER_IN", toAccount.getAccountId(), 
                                                          amount, description + " (Transfer from " + fromAccountNumber + ")");
            depositTransaction.setStatus("COMPLETED");
            depositTransaction.setFromAccountNumber(fromAccountNumber);
            
            // Save transactions
            withdrawalTransaction = transactionDAO.createTransaction(withdrawalTransaction);
            depositTransaction = transactionDAO.createTransaction(depositTransaction);
            
            return List.of(withdrawalTransaction, depositTransaction);
            
        } finally {
            transferLock.unlock();
        }
    }

    /**
     * Get account balance
     * @param accountNumber account number
     * @return account balance
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     */
    public double getAccountBalance(String accountNumber) throws SQLException, InvalidAccountException {
        Optional<BankAccount> accountOpt = accountDAO.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new InvalidAccountException("Account not found: " + accountNumber);
        }
        
        return accountOpt.get().getBalance();
    }

    /**
     * Get transaction history for an account
     * @param accountNumber account number
     * @return List of transactions
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     */
    public List<Transaction> getTransactionHistory(String accountNumber) throws SQLException, InvalidAccountException {
        Optional<BankAccount> accountOpt = accountDAO.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new InvalidAccountException("Account not found: " + accountNumber);
        }
        
        return transactionDAO.findByAccountId(accountOpt.get().getAccountId());
    }

    /**
     * Get transaction history for a user
     * @param userId user ID
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> getUserTransactionHistory(int userId) throws SQLException {
        return transactionDAO.findByUserId(userId);
    }

    /**
     * Get all accounts for a user
     * @param userId user ID
     * @return List of accounts
     * @throws SQLException if database operation fails
     */
    public List<BankAccount> getUserAccounts(int userId) throws SQLException {
        return accountDAO.findByUserId(userId);
    }

    /**
     * Freeze/unfreeze an account
     * @param accountNumber account number
     * @param isFrozen freeze status
     * @return true if update successful
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     */
    public boolean updateAccountFrozenStatus(String accountNumber, boolean isFrozen) 
            throws SQLException, InvalidAccountException {
        
        Optional<BankAccount> accountOpt = accountDAO.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new InvalidAccountException("Account not found: " + accountNumber);
        }
        
        return accountDAO.updateFrozenStatus(accountOpt.get().getAccountId(), isFrozen);
    }

    /**
     * Get transaction statistics for an account
     * @param accountNumber account number
     * @return array with [totalDeposits, totalWithdrawals, totalTransfersIn, totalTransfersOut]
     * @throws SQLException if database operation fails
     * @throws InvalidAccountException if account not found
     */
    public double[] getAccountStatistics(String accountNumber) throws SQLException, InvalidAccountException {
        Optional<BankAccount> accountOpt = accountDAO.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new InvalidAccountException("Account not found: " + accountNumber);
        }
        
        return transactionDAO.getTransactionStatistics(accountOpt.get().getAccountId());
    }

    /**
     * Generate unique account number
     * @return unique account number
     */
    private String generateAccountNumber() {
        // Simple implementation - in production, use a more sophisticated approach
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return String.format("ACC%013d", timestamp + random);
    }
} 