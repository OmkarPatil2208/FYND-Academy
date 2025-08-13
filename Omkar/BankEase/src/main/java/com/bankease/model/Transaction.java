package com.bankease.model;

import java.time.LocalDateTime;

/**
 * Transaction model class representing a bank transaction
 */
public class Transaction {
    private int transactionId;
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    private int accountId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;
    private LocalDateTime timestamp;
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED

    // Constructors
    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Transaction(String transactionType, int accountId, double amount, String description) {
        this();
        this.transactionType = transactionType;
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }

    public Transaction(String transactionType, String fromAccountNumber, String toAccountNumber, 
                     double amount, String description) {
        this();
        this.transactionType = transactionType;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Business methods
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isTransfer() {
        return "TRANSFER_IN".equals(transactionType) || "TRANSFER_OUT".equals(transactionType);
    }

    public boolean isDeposit() {
        return "DEPOSIT".equals(transactionType);
    }

    public boolean isWithdrawal() {
        return "WITHDRAWAL".equals(transactionType);
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    public String getFormattedTimestamp() {
        return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", transactionType='" + transactionType + '\'' +
                ", accountId=" + accountId +
                ", fromAccountNumber='" + fromAccountNumber + '\'' +
                ", toAccountNumber='" + toAccountNumber + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(transactionId);
    }
} 