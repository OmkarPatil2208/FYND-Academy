package com.bankease.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User model class representing a bank customer
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDateTime registrationDate;
    private boolean isActive;
    private List<BankAccount> accounts;

    // Constructors
    public User() {
        this.accounts = new ArrayList<>();
        this.registrationDate = LocalDateTime.now();
        this.isActive = true;
    }

    public User(String username, String password, String email, String fullName, String phoneNumber) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    // Business methods
    public void addAccount(BankAccount account) {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        accounts.add(account);
    }

    public BankAccount getAccountByNumber(String accountNumber) {
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public double getTotalBalance() {
        return accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                ", accountsCount=" + (accounts != null ? accounts.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, username);
    }
} 