package com.bankease.exceptions;

/**
 * Custom exception thrown when an account is invalid or not found
 */
public class InvalidAccountException extends Exception {
    
    public InvalidAccountException() {
        super("Invalid account or account not found");
    }
    
    public InvalidAccountException(String message) {
        super(message);
    }
    
    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }
} 