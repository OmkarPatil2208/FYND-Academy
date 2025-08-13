package com.bankease.model;

import java.time.LocalDateTime;

/**
 * Admin model class representing a bank administrator
 */
public class Admin {
    private int adminId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role; // SUPER_ADMIN, ADMIN, SUPPORT
    private LocalDateTime creationDate;
    private boolean isActive;
    private LocalDateTime lastLogin;

    // Constructors
    public Admin() {
        this.creationDate = LocalDateTime.now();
        this.isActive = true;
    }

    public Admin(String username, String password, String fullName, String email, String role) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Business methods
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isSupport() {
        return "SUPPORT".equals(role);
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public boolean canFreezeAccounts() {
        return isSuperAdmin() || isAdmin();
    }

    public boolean canViewAllTransactions() {
        return isSuperAdmin() || isAdmin();
    }

    public boolean canManageUsers() {
        return isSuperAdmin();
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", creationDate=" + creationDate +
                ", isActive=" + isActive +
                ", lastLogin=" + lastLogin +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return adminId == admin.adminId && username.equals(admin.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(adminId, username);
    }
} 