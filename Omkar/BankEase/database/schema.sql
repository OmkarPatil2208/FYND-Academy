-- BankEase Database Schema
-- MySQL Database Schema for BankEase Banking System

-- Create database
CREATE DATABASE IF NOT EXISTS bankease;
USE bankease;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Admins table
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('SUPER_ADMIN', 'ADMIN', 'SUPPORT') DEFAULT 'ADMIN',
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Bank accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    account_type ENUM('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT') NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_frozen BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_account_number (account_number),
    INDEX idx_user_id (user_id),
    INDEX idx_account_type (account_type)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT') NOT NULL,
    account_id INT NOT NULL,
    from_account_number VARCHAR(20) NULL,
    to_account_number VARCHAR(20) NULL,
    amount DECIMAL(15,2) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    INDEX idx_account_id (account_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_status (status),
    INDEX idx_timestamp (timestamp)
);

-- Insert default admin user
INSERT INTO admins (username, password, full_name, email, role) 
VALUES ('admin', 'admin123', 'System Administrator', 'admin@bankease.com', 'SUPER_ADMIN')
ON DUPLICATE KEY UPDATE admin_id = admin_id;

-- Create indexes for better performance
CREATE INDEX idx_users_registration_date ON users(registration_date);
CREATE INDEX idx_accounts_creation_date ON accounts(creation_date);
CREATE INDEX idx_transactions_timestamp ON transactions(timestamp);
CREATE INDEX idx_transactions_type_status ON transactions(transaction_type, status);

-- Create view for user account summary
CREATE OR REPLACE VIEW user_account_summary AS
SELECT 
    u.user_id,
    u.username,
    u.full_name,
    u.email,
    COUNT(a.account_id) as total_accounts,
    SUM(CASE WHEN a.is_active = TRUE THEN a.balance ELSE 0 END) as total_balance,
    SUM(CASE WHEN a.is_frozen = TRUE THEN 1 ELSE 0 END) as frozen_accounts
FROM users u
LEFT JOIN accounts a ON u.user_id = a.user_id
GROUP BY u.user_id, u.username, u.full_name, u.email;

-- Create view for transaction summary
CREATE OR REPLACE VIEW transaction_summary AS
SELECT 
    t.transaction_type,
    COUNT(*) as transaction_count,
    SUM(t.amount) as total_amount,
    AVG(t.amount) as average_amount,
    MIN(t.timestamp) as first_transaction,
    MAX(t.timestamp) as last_transaction
FROM transactions t
WHERE t.status = 'COMPLETED'
GROUP BY t.transaction_type;

-- Create stored procedure for account balance update
DELIMITER //
CREATE PROCEDURE UpdateAccountBalance(
    IN p_account_id INT,
    IN p_amount DECIMAL(15,2),
    IN p_operation ENUM('ADD', 'SUBTRACT')
)
BEGIN
    DECLARE current_balance DECIMAL(15,2);
    DECLARE new_balance DECIMAL(15,2);
    
    -- Get current balance
    SELECT balance INTO current_balance 
    FROM accounts 
    WHERE account_id = p_account_id;
    
    -- Calculate new balance
    IF p_operation = 'ADD' THEN
        SET new_balance = current_balance + p_amount;
    ELSE
        SET new_balance = current_balance - p_amount;
    END IF;
    
    -- Update balance
    UPDATE accounts 
    SET balance = new_balance 
    WHERE account_id = p_account_id;
    
    -- Return the new balance
    SELECT new_balance as new_balance;
END //
DELIMITER ;

-- Create stored procedure for transfer between accounts
DELIMITER //
CREATE PROCEDURE TransferBetweenAccounts(
    IN p_from_account_id INT,
    IN p_to_account_id INT,
    IN p_amount DECIMAL(15,2),
    IN p_description TEXT
)
BEGIN
    DECLARE from_balance DECIMAL(15,2);
    DECLARE to_balance DECIMAL(15,2);
    DECLARE from_account_number VARCHAR(20);
    DECLARE to_account_number VARCHAR(20);
    DECLARE transfer_id INT;
    
    -- Start transaction
    START TRANSACTION;
    
    -- Get account details
    SELECT balance, account_number INTO from_balance, from_account_number
    FROM accounts WHERE account_id = p_from_account_id;
    
    SELECT balance, account_number INTO to_balance, to_account_number
    FROM accounts WHERE account_id = p_to_account_id;
    
    -- Check if sufficient funds
    IF from_balance < p_amount THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient funds';
    END IF;
    
    -- Update balances
    UPDATE accounts SET balance = balance - p_amount WHERE account_id = p_from_account_id;
    UPDATE accounts SET balance = balance + p_amount WHERE account_id = p_to_account_id;
    
    -- Create transfer out transaction
    INSERT INTO transactions (transaction_type, account_id, from_account_number, to_account_number, amount, description, status)
    VALUES ('TRANSFER_OUT', p_from_account_id, from_account_number, to_account_number, p_amount, p_description, 'COMPLETED');
    
    -- Create transfer in transaction
    INSERT INTO transactions (transaction_type, account_id, from_account_number, to_account_number, amount, description, status)
    VALUES ('TRANSFER_IN', p_to_account_id, from_account_number, to_account_number, p_amount, p_description, 'COMPLETED');
    
    -- Commit transaction
    COMMIT;
    
    SELECT 'Transfer completed successfully' as result;
END //
DELIMITER ;

-- Create trigger to log account balance changes
DELIMITER //
CREATE TRIGGER after_account_balance_update
AFTER UPDATE ON accounts
FOR EACH ROW
BEGIN
    IF OLD.balance != NEW.balance THEN
        INSERT INTO transactions (transaction_type, account_id, amount, description, status)
        VALUES (
            CASE 
                WHEN NEW.balance > OLD.balance THEN 'DEPOSIT'
                ELSE 'WITHDRAWAL'
            END,
            NEW.account_id,
            ABS(NEW.balance - OLD.balance),
            CONCAT('Balance update: ', OLD.balance, ' -> ', NEW.balance),
            'COMPLETED'
        );
    END IF;
END //
DELIMITER ;

-- Create trigger to validate account status before transactions
DELIMITER //
CREATE TRIGGER before_transaction_insert
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
    DECLARE account_frozen BOOLEAN;
    DECLARE account_active BOOLEAN;
    
    -- Check account status
    SELECT is_frozen, is_active INTO account_frozen, account_active
    FROM accounts WHERE account_id = NEW.account_id;
    
    IF account_frozen = TRUE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Account is frozen';
    END IF;
    
    IF account_active = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Account is inactive';
    END IF;
END //
DELIMITER ;

-- Grant permissions (adjust as needed for your MySQL setup)
-- GRANT ALL PRIVILEGES ON bankease.* TO 'bankease_user'@'localhost';
-- FLUSH PRIVILEGES; 