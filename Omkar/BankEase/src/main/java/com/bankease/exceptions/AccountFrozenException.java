package com.bankease.exceptions;

/**
 * Custom exception thrown when trying to perform operations on a frozen account
 */
public class AccountFrozenException extends Exception {
    
    public AccountFrozenException() {
        super("Account is frozen and cannot perform transactions");
    }
    
    public AccountFrozenException(String message) {
        super(message);
    }
    
    public AccountFrozenException(String message, Throwable cause) {
        super(message, cause);
    }
} 