package com.bankease.exceptions;

/**
 * Custom exception thrown when there are insufficient funds for a transaction
 */
public class InsufficientFundsException extends Exception {
    
    public InsufficientFundsException() {
        super("Insufficient funds for this transaction");
    }
    
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
} 