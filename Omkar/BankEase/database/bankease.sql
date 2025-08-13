-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 12, 2025 at 07:13 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bankease`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `account_id` int(11) NOT NULL,
  `account_number` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `account_type` enum('SAVINGS','CHECKING','FIXED_DEPOSIT') NOT NULL,
  `balance` decimal(15,2) DEFAULT 0.00,
  `creation_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1,
  `is_frozen` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`account_id`, `account_number`, `user_id`, `account_type`, `balance`, `creation_date`, `is_active`, `is_frozen`) VALUES
(1, 'ACC1754478385204', 1, 'SAVINGS', 250.00, '2025-08-06 05:36:19', 1, 0);

--
-- Triggers `accounts`
--
DELIMITER $$
CREATE TRIGGER `after_account_balance_update` AFTER UPDATE ON `accounts` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `admin_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` enum('SUPER_ADMIN','ADMIN','SUPPORT') DEFAULT 'ADMIN',
  `creation_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`admin_id`, `username`, `password`, `full_name`, `email`, `role`, `creation_date`, `last_login`, `is_active`) VALUES
(1, 'omkar', 'omkar123', 'System Administrator', 'admin@bankease.com', 'SUPER_ADMIN', '2025-08-06 10:47:55', '2025-08-10 23:40:51', 1);

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `transaction_id` int(11) NOT NULL,
  `transaction_type` enum('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
  `account_id` int(11) NOT NULL,
  `from_account_number` varchar(20) DEFAULT NULL,
  `to_account_number` varchar(20) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `description` text DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('PENDING','COMPLETED','FAILED','CANCELLED') DEFAULT 'PENDING'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transaction_id`, `transaction_type`, `account_id`, `from_account_number`, `to_account_number`, `amount`, `description`, `timestamp`, `status`) VALUES
(1, 'DEPOSIT', 1, NULL, NULL, 100.00, 'Initial deposit', '2025-08-06 05:36:19', 'COMPLETED'),
(2, 'DEPOSIT', 1, NULL, NULL, 150.00, 'Balance update: 100.00 -> 250.00', '2025-08-07 09:35:35', 'COMPLETED'),
(3, 'DEPOSIT', 1, NULL, NULL, 150.00, 'Testing again', '2025-08-07 04:05:35', 'COMPLETED');

--
-- Triggers `transactions`
--
DELIMITER $$
CREATE TRIGGER `before_transaction_insert` BEFORE INSERT ON `transactions` FOR EACH ROW BEGIN
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
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Stand-in structure for view `transaction_summary`
-- (See below for the actual view)
--
CREATE TABLE `transaction_summary` (
`transaction_type` enum('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT')
,`transaction_count` bigint(21)
,`total_amount` decimal(37,2)
,`average_amount` decimal(19,6)
,`first_transaction` timestamp
,`last_transaction` timestamp
);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `registration_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `full_name`, `phone_number`, `registration_date`, `is_active`) VALUES
(1, 'Omkar', '1234', 'omkar@gmail.com', 'Omkar Patil', '9876543210', '2025-08-06 05:32:28', 1),
(2, 'atharva', 'at123', 'Atharva@gmail.com', 'Atharva Gawdey\r\n', '3353465', '2025-08-11 05:23:06', 1);

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_account_summary`
-- (See below for the actual view)
--
CREATE TABLE `user_account_summary` (
`user_id` int(11)
,`username` varchar(50)
,`full_name` varchar(100)
,`email` varchar(100)
,`total_accounts` bigint(21)
,`total_balance` decimal(37,2)
,`frozen_accounts` decimal(22,0)
);

-- --------------------------------------------------------

--
-- Structure for view `transaction_summary`
--
DROP TABLE IF EXISTS `transaction_summary`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `transaction_summary`  AS SELECT `t`.`transaction_type` AS `transaction_type`, count(0) AS `transaction_count`, sum(`t`.`amount`) AS `total_amount`, avg(`t`.`amount`) AS `average_amount`, min(`t`.`timestamp`) AS `first_transaction`, max(`t`.`timestamp`) AS `last_transaction` FROM `transactions` AS `t` WHERE `t`.`status` = 'COMPLETED' GROUP BY `t`.`transaction_type` ;

-- --------------------------------------------------------

--
-- Structure for view `user_account_summary`
--
DROP TABLE IF EXISTS `user_account_summary`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_account_summary`  AS SELECT `u`.`user_id` AS `user_id`, `u`.`username` AS `username`, `u`.`full_name` AS `full_name`, `u`.`email` AS `email`, count(`a`.`account_id`) AS `total_accounts`, sum(case when `a`.`is_active` = 1 then `a`.`balance` else 0 end) AS `total_balance`, sum(case when `a`.`is_frozen` = 1 then 1 else 0 end) AS `frozen_accounts` FROM (`users` `u` left join `accounts` `a` on(`u`.`user_id` = `a`.`user_id`)) GROUP BY `u`.`user_id`, `u`.`username`, `u`.`full_name`, `u`.`email` ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_id`),
  ADD UNIQUE KEY `account_number` (`account_number`),
  ADD KEY `idx_account_number` (`account_number`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_account_type` (`account_type`),
  ADD KEY `idx_accounts_creation_date` (`creation_date`);

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_role` (`role`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `idx_account_id` (`account_id`),
  ADD KEY `idx_transaction_type` (`transaction_type`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_timestamp` (`timestamp`),
  ADD KEY `idx_transactions_timestamp` (`timestamp`),
  ADD KEY `idx_transactions_type_status` (`transaction_type`,`status`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_users_registration_date` (`registration_date`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `account_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accounts`
--
ALTER TABLE `accounts`
  ADD CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
