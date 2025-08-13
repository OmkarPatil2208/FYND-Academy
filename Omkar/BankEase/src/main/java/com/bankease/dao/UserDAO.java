package com.bankease.dao;

import com.bankease.model.User;
import com.bankease.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entity
 */
public class UserDAO {
    
    /**
     * Create a new user
     * @param user User object to create
     * @return User with generated ID
     * @throws SQLException if database operation fails
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, full_name, phone_number, registration_date, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setTimestamp(6, Timestamp.valueOf(user.getRegistrationDate()));
            pstmt.setBoolean(7, user.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        
        return user;
    }
    
    /**
     * Find user by username
     * @param username username to search for
     * @return Optional containing User if found
     * @throws SQLException if database operation fails
     */
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Find user by ID
     * @param userId user ID to search for
     * @return Optional containing User if found
     * @throws SQLException if database operation fails
     */
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all users
     * @return List of all users
     * @throws SQLException if database operation fails
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Update user
     * @param user User object to update
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ?, " +
                    "phone_number = ?, is_active = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setBoolean(6, user.isActive());
            pstmt.setInt(7, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete user by ID
     * @param userId user ID to delete
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update user active status
     * @param userId user ID
     * @param isActive active status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateUserStatus(int userId, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, userId);
            
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
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
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
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
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
     * Get total number of users
     * @return total number of users
     * @throws SQLException if database operation fails
     */
    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }

    /**
     * Search users by username
     * @param username username to search for
     * @return List of matching users
     * @throws SQLException if database operation fails
     */
    public List<User> searchByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username LIKE ? ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + username + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }

    /**
     * Search users by email
     * @param email email to search for
     * @return List of matching users
     * @throws SQLException if database operation fails
     */
    public List<User> searchByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email LIKE ? ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + email + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }

    /**
     * Search users by full name
     * @param fullName full name to search for
     * @return List of matching users
     * @throws SQLException if database operation fails
     */
    public List<User> searchByFullName(String fullName) throws SQLException {
        String sql = "SELECT * FROM users WHERE full_name LIKE ? ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + fullName + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }

    /**
     * Map ResultSet to User object
     * @param rs ResultSet
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
} 