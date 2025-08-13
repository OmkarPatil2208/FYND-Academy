package com.bankease.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration utility class
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "config/database.properties";
    private static final String DEFAULT_CONFIG_FILE = "config/database.properties";
    
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    
    static {
        loadDatabaseConfig();
    }
    
    /**
     * Load database configuration from properties file
     */
    private static void loadDatabaseConfig() {
        Properties props = new Properties();
        
        try {
            // Try to load from config file
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (IOException e) {
                // If config file doesn't exist, use default values
                System.out.println("Database config file not found, using default values.");
                setDefaultConfig();
                return;
            }
            
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            
        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            setDefaultConfig();
        }
    }
    
    /**
     * Set default database configuration
     */
    private static void setDefaultConfig() {
        url = "jdbc:mysql://localhost:3306/bankease?useSSL=false&serverTimezone=UTC";
        username = "root";
        password = "password";
        driver = "com.mysql.cj.jdbc.Driver";
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + e.getMessage());
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get database URL
     * @return database URL
     */
    public static String getUrl() {
        return url;
    }
    
    /**
     * Get database username
     * @return database username
     */
    public static String getUsername() {
        return username;
    }
    
    /**
     * Get database password
     * @return database password
     */
    public static String getPassword() {
        return password;
    }
    
    /**
     * Get database driver
     * @return database driver
     */
    public static String getDriver() {
        return driver;
    }
} 