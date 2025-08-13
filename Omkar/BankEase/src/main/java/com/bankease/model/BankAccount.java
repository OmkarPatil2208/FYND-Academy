package com.bankease.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BankAccount model class representing a bank account
 */
public class BankAccount {
    private int accountId;
    private String accountNumber;
    private int userId;
    private String accountType; // SAVINGS, CHECKING, FIXED_DEPOSIT
    private double balance;
    private LocalDateTime creationDate;
    private boolean isActive;
    private boolean isFrozen;
    private List<Transaction> transactions;

    // Constructors
    public BankAccount() {
        this.transactions = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
        this.isActive = true;
        this.isFrozen = false;
    }

    public BankAccount(String accountNumber, int userId, String accountType, double initialBalance) {
        this();
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = initialBalance;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Business methods
    public void addTransaction(Transaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(transaction);
    }

    public List<Transaction> getTransactionsByType(String type) {
        return transactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals(type))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactions.stream()
                .filter(transaction -> !transaction.getTimestamp().isBefore(startDate) && 
                                    !transaction.getTimestamp().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public double getTotalDeposits() {
        return transactions.stream()
                .filter(transaction -> "DEPOSIT".equals(transaction.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalWithdrawals() {
        return transactions.stream()
                .filter(transaction -> "WITHDRAWAL".equals(transaction.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalTransfersIn() {
        return transactions.stream()
                .filter(transaction -> "TRANSFER_IN".equals(transaction.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalTransfersOut() {
        return transactions.stream()
                .filter(transaction -> "TRANSFER_OUT".equals(transaction.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountId=" + accountId +
                ", accountNumber='" + accountNumber + '\'' +
                ", userId=" + userId +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", creationDate=" + creationDate +
                ", isActive=" + isActive +
                ", isFrozen=" + isFrozen +
                ", transactionsCount=" + (transactions != null ? transactions.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BankAccount that = (BankAccount) obj;
        return accountId == that.accountId && accountNumber.equals(that.accountNumber);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(accountId, accountNumber);
    }
} 