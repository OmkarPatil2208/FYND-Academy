package com.bankease.dao;

import com.bankease.model.Admin;
import com.bankease.utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Admin entity
 */
public class AdminDAO {
    
    /**
     * Create a new admin
     * @param admin Admin object to create
     * @return Admin with generated ID
     * @throws SQLException if database operation fails
     */
    public Admin createAdmin(Admin admin) throws SQLException {
        String sql = "INSERT INTO admins (username, password, full_name, email, role, creation_date, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            pstmt.setString(3, admin.getFullName());
            pstmt.setString(4, admin.getEmail());
            pstmt.setString(5, admin.getRole());
            pstmt.setTimestamp(6, Timestamp.valueOf(admin.getCreationDate()));
            pstmt.setBoolean(7, admin.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating admin failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    admin.setAdminId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating admin failed, no ID obtained.");
                }
            }
        }
        
        return admin;
    }
    
    /**
     * Find admin by username
     * @param username username to search for
     * @return Optional containing Admin if found
     * @throws SQLException if database operation fails
     */
    public Optional<Admin> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM admins WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Find admin by ID
     * @param adminId admin ID to search for
     * @return Optional containing Admin if found
     * @throws SQLException if database operation fails
     */
    public Optional<Admin> findById(int adminId) throws SQLException {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all admins
     * @return List of all admins
     * @throws SQLException if database operation fails
     */
    public List<Admin> findAll() throws SQLException {
        String sql = "SELECT * FROM admins ORDER BY admin_id";
        List<Admin> admins = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        }
        
        return admins;
    }
    
    /**
     * Update admin
     * @param admin Admin object to update
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateAdmin(Admin admin) throws SQLException {
        String sql = "UPDATE admins SET username = ?, password = ?, full_name = ?, email = ?, " +
                    "role = ?, is_active = ? WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            pstmt.setString(3, admin.getFullName());
            pstmt.setString(4, admin.getEmail());
            pstmt.setString(5, admin.getRole());
            pstmt.setBoolean(6, admin.isActive());
            pstmt.setInt(7, admin.getAdminId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update admin last login
     * @param adminId admin ID
     * @param lastLogin last login timestamp
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateLastLogin(int adminId, java.time.LocalDateTime lastLogin) throws SQLException {
        String sql = "UPDATE admins SET last_login = ? WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(lastLogin));
            pstmt.setInt(2, adminId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update admin active status
     * @param adminId admin ID
     * @param isActive active status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateActiveStatus(int adminId, boolean isActive) throws SQLException {
        String sql = "UPDATE admins SET is_active = ? WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, adminId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete admin by ID
     * @param adminId admin ID to delete
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    public boolean deleteAdmin(int adminId) throws SQLException {
        String sql = "DELETE FROM admins WHERE admin_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Check if username exists
     * @param username username to check
     * @return true if username exists
     * @throws SQLException if database operation fails
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM admins WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if email exists
     * @param email email to check
     * @return true if email exists
     * @throws SQLException if database operation fails
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM admins WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get admins by role
     * @param role admin role
     * @return List of admins with the specified role
     * @throws SQLException if database operation fails
     */
    public List<Admin> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM admins WHERE role = ? ORDER BY admin_id";
        List<Admin> admins = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    admins.add(mapResultSetToAdmin(rs));
                }
            }
        }
        
        return admins;
    }
    
    /**
     * Map ResultSet to Admin object
     * @param rs ResultSet
     * @return Admin object
     * @throws SQLException if mapping fails
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setFullName(rs.getString("full_name"));
        admin.setEmail(rs.getString("email"));
        admin.setRole(rs.getString("role"));
        admin.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        admin.setActive(rs.getBoolean("is_active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            admin.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        return admin;
    }
} 