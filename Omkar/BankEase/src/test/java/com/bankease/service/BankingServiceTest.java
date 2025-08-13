package com.bankease.service;

import com.bankease.exceptions.AccountFrozenException;
import com.bankease.exceptions.InsufficientFundsException;
import com.bankease.exceptions.InvalidAccountException;
import com.bankease.model.BankAccount;
import com.bankease.model.Transaction;
import com.bankease.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for BankingService
 */
@DisplayName("BankingService Tests")
public class BankingServiceTest {
    
    private BankingService bankingService;
    
    @BeforeEach
    void setUp() {
        bankingService = new BankingService();
    }
    
    @Test
    @DisplayName("Should register a new user successfully")
    void testRegisterUser() throws SQLException {
        // Given
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        String fullName = "Test User";
        String phoneNumber = "1234567890";
        
        // When
        User user = bankingService.registerUser(username, password, email, fullName, phoneNumber);
        
        // Then
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertTrue(user.isActive());
    }
    
    @Test
    @DisplayName("Should throw exception when registering user with existing username")
    void testRegisterUserWithExistingUsername() throws SQLException {
        // Given
        String username = "existinguser";
        String password = "password123";
        String email = "existing@example.com";
        String fullName = "Existing User";
        String phoneNumber = "1234567890";
        
        // Register first user
        bankingService.registerUser(username, password, email, fullName, phoneNumber);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bankingService.registerUser(username, "newpassword", "new@example.com", "New User", "0987654321");
        });
    }
    
    @Test
    @DisplayName("Should login user with correct credentials")
    void testLoginUserSuccess() throws SQLException {
        // Given
        String username = "logintest";
        String password = "password123";
        String email = "login@example.com";
        String fullName = "Login Test User";
        String phoneNumber = "1234567890";
        
        User registeredUser = bankingService.registerUser(username, password, email, fullName, phoneNumber);
        
        // When
        Optional<User> loggedInUser = bankingService.loginUser(username, password);
        
        // Then
        assertTrue(loggedInUser.isPresent());
        assertEquals(registeredUser.getUserId(), loggedInUser.get().getUserId());
        assertEquals(username, loggedInUser.get().getUsername());
    }
    
    @Test
    @DisplayName("Should return empty when login with wrong password")
    void testLoginUserWrongPassword() throws SQLException {
        // Given
        String username = "wrongpassuser";
        String password = "password123";
        String email = "wrongpass@example.com";
        String fullName = "Wrong Pass User";
        String phoneNumber = "1234567890";
        
        bankingService.registerUser(username, password, email, fullName, phoneNumber);
        
        // When
        Optional<User> loggedInUser = bankingService.loginUser(username, "wrongpassword");
        
        // Then
        assertFalse(loggedInUser.isPresent());
    }
    
    @Test
    @DisplayName("Should create a new bank account successfully")
    void testCreateAccount() throws SQLException {
        // Given
        User user = bankingService.registerUser("accountuser", "password123", "account@example.com", 
                                              "Account User", "1234567890");
        String accountType = "SAVINGS";
        double initialBalance = 1000.0;
        
        // When
        BankAccount account = bankingService.createAccount(user.getUserId(), accountType, initialBalance);
        
        // Then
        assertNotNull(account);
        assertEquals(user.getUserId(), account.getUserId());
        assertEquals(accountType, account.getAccountType());
        assertEquals(initialBalance, account.getBalance());
        assertTrue(account.isActive());
        assertFalse(account.isFrozen());
    }
    
    @Test
    @DisplayName("Should deposit money successfully")
    void testDeposit() throws SQLException, InvalidAccountException, AccountFrozenException {
        // Given
        User user = bankingService.registerUser("deposituser", "password123", "deposit@example.com", 
                                              "Deposit User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        double depositAmount = 500.0;
        
        // When
        Transaction transaction = bankingService.deposit(account.getAccountNumber(), depositAmount, "Test deposit");
        
        // Then
        assertNotNull(transaction);
        assertEquals("DEPOSIT", transaction.getTransactionType());
        assertEquals(depositAmount, transaction.getAmount());
        assertEquals("COMPLETED", transaction.getStatus());
        
        // Verify balance was updated
        double newBalance = bankingService.getAccountBalance(account.getAccountNumber());
        assertEquals(1500.0, newBalance);
    }
    
    @Test
    @DisplayName("Should withdraw money successfully")
    void testWithdraw() throws SQLException, InvalidAccountException, AccountFrozenException, InsufficientFundsException {
        // Given
        User user = bankingService.registerUser("withdrawuser", "password123", "withdraw@example.com", 
                                              "Withdraw User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        double withdrawAmount = 300.0;
        
        // When
        Transaction transaction = bankingService.withdraw(account.getAccountNumber(), withdrawAmount, "Test withdrawal");
        
        // Then
        assertNotNull(transaction);
        assertEquals("WITHDRAWAL", transaction.getTransactionType());
        assertEquals(withdrawAmount, transaction.getAmount());
        assertEquals("COMPLETED", transaction.getStatus());
        
        // Verify balance was updated
        double newBalance = bankingService.getAccountBalance(account.getAccountNumber());
        assertEquals(700.0, newBalance);
    }
    
    @Test
    @DisplayName("Should throw InsufficientFundsException when withdrawing more than balance")
    void testWithdrawInsufficientFunds() throws SQLException {
        // Given
        User user = bankingService.registerUser("insufficientuser", "password123", "insufficient@example.com", 
                                              "Insufficient User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 100.0);
        double withdrawAmount = 500.0;
        
        // When & Then
        assertThrows(InsufficientFundsException.class, () -> {
            bankingService.withdraw(account.getAccountNumber(), withdrawAmount, "Test withdrawal");
        });
    }
    
    @Test
    @DisplayName("Should transfer money between accounts successfully")
    void testTransfer() throws SQLException, InvalidAccountException, AccountFrozenException, InsufficientFundsException {
        // Given
        User user = bankingService.registerUser("transferuser", "password123", "transfer@example.com", 
                                              "Transfer User", "1234567890");
        BankAccount fromAccount = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        BankAccount toAccount = bankingService.createAccount(user.getUserId(), "CHECKING", 500.0);
        double transferAmount = 300.0;
        
        // When
        List<Transaction> transactions = bankingService.transfer(fromAccount.getAccountNumber(), 
                                                               toAccount.getAccountNumber(), 
                                                               transferAmount, "Test transfer");
        
        // Then
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        
        Transaction withdrawalTransaction = transactions.get(0);
        Transaction depositTransaction = transactions.get(1);
        
        assertEquals("TRANSFER_OUT", withdrawalTransaction.getTransactionType());
        assertEquals("TRANSFER_IN", depositTransaction.getTransactionType());
        assertEquals(transferAmount, withdrawalTransaction.getAmount());
        assertEquals(transferAmount, depositTransaction.getAmount());
        
        // Verify balances were updated
        double fromBalance = bankingService.getAccountBalance(fromAccount.getAccountNumber());
        double toBalance = bankingService.getAccountBalance(toAccount.getAccountNumber());
        assertEquals(700.0, fromBalance);
        assertEquals(800.0, toBalance);
    }
    
    @Test
    @DisplayName("Should throw exception when transferring to same account")
    void testTransferToSameAccount() throws SQLException {
        // Given
        User user = bankingService.registerUser("sametransferuser", "password123", "sametransfer@example.com", 
                                              "Same Transfer User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bankingService.transfer(account.getAccountNumber(), account.getAccountNumber(), 100.0, "Test transfer");
        });
    }
    
    @Test
    @DisplayName("Should get transaction history for user")
    void testGetUserTransactionHistory() throws SQLException, InvalidAccountException, AccountFrozenException, InsufficientFundsException {
        // Given
        User user = bankingService.registerUser("historyuser", "password123", "history@example.com", 
                                              "History User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        
        // Perform some transactions
        bankingService.deposit(account.getAccountNumber(), 500.0, "Deposit 1");
        bankingService.withdraw(account.getAccountNumber(), 200.0, "Withdrawal 1");
        bankingService.deposit(account.getAccountNumber(), 300.0, "Deposit 2");
        
        // When
        List<Transaction> transactions = bankingService.getUserTransactionHistory(user.getUserId());
        
        // Then
        assertNotNull(transactions);
        assertTrue(transactions.size() >= 3); // At least 3 transactions (initial deposit + 2 manual transactions)
    }
    
    @Test
    @DisplayName("Should get user accounts")
    void testGetUserAccounts() throws SQLException {
        // Given
        User user = bankingService.registerUser("accountsuser", "password123", "accounts@example.com", 
                                              "Accounts User", "1234567890");
        BankAccount account1 = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        BankAccount account2 = bankingService.createAccount(user.getUserId(), "CHECKING", 500.0);
        
        // When
        List<BankAccount> accounts = bankingService.getUserAccounts(user.getUserId());
        
        // Then
        assertNotNull(accounts);
        assertEquals(2, accounts.size());
    }
    
    @Test
    @DisplayName("Should throw InvalidAccountException for non-existent account")
    void testInvalidAccountException() {
        // When & Then
        assertThrows(InvalidAccountException.class, () -> {
            bankingService.getAccountBalance("NONEXISTENT");
        });
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for negative deposit amount")
    void testNegativeDepositAmount() throws SQLException {
        // Given
        User user = bankingService.registerUser("neguser", "password123", "neg@example.com", 
                                              "Negative User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bankingService.deposit(account.getAccountNumber(), -100.0, "Negative deposit");
        });
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for negative withdrawal amount")
    void testNegativeWithdrawalAmount() throws SQLException {
        // Given
        User user = bankingService.registerUser("negwithdrawuser", "password123", "negwithdraw@example.com", 
                                              "Negative Withdraw User", "1234567890");
        BankAccount account = bankingService.createAccount(user.getUserId(), "SAVINGS", 1000.0);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bankingService.withdraw(account.getAccountNumber(), -100.0, "Negative withdrawal");
        });
    }
} 