# BankEase Setup Guide

## Prerequisites

1. **Java 11 or higher** - Make sure you have Java 11+ installed
2. **Maven** - For building the project
3. **XAMPP** - For MySQL database
4. **IntelliJ IDEA** - For development

## Database Setup

### 1. Start XAMPP
1. Open XAMPP Control Panel
2. Start Apache and MySQL services
3. Make sure MySQL is running on port 3306

### 2. Create Database
1. Open phpMyAdmin (http://localhost/phpmyadmin)
2. Create a new database named `bankease`
3. Import the database schema from `database/bankease.sql`

### 3. Configure Database Connection
1. Open `config/database.properties`
2. Update the password field with your MySQL root password:
   ```properties
   db.password=your_mysql_password
   ```
3. If you don't have a password set for root, leave it empty:
   ```properties
   db.password=
   ```

## Project Setup

### 1. Import Project in IntelliJ
1. Open IntelliJ IDEA
2. Select "Open" and choose the BankEase project folder
3. Wait for Maven to download dependencies

### 2. Build Project
1. Open Maven tab in IntelliJ
2. Run `clean` and `compile` goals
3. Or use command line: `mvn clean compile`

### 3. Run the Application
1. Run `BankEaseApplication.java` as the main class
2. Or use Maven: `mvn exec:java -Dexec.mainClass="com.bankease.main.BankEaseApplication"`

## Default Credentials

### Admin Login
- Username: `admin`
- Password: `admin123`

### Test User (if exists)
- Username: `Omkar`
- Password: `1234`

## Troubleshooting

### Common Issues

1. **"Cannot access com.bankease.service.AdminService"**
   - Solution: The AdminService class has been created. Make sure to rebuild the project.

2. **Database Connection Failed**
   - Check if XAMPP MySQL is running
   - Verify database credentials in `config/database.properties`
   - Make sure the `bankease` database exists

3. **Compilation Errors**
   - Run `mvn clean compile` to rebuild
   - Check Java version (should be 11+)
   - Make sure all dependencies are downloaded

4. **Missing Dependencies**
   - Run `mvn dependency:resolve` to download missing dependencies
   - Check internet connection for Maven repository access

### Database Connection Test
The application will automatically test the database connection on startup. If it fails, check:
- MySQL service is running
- Database credentials are correct
- Database `bankease` exists
- Network connectivity to localhost:3306

## Project Structure

```
BankEase/
├── src/main/java/com/bankease/
│   ├── dao/           # Data Access Objects
│   ├── exceptions/    # Custom exceptions
│   ├── main/          # Main application class
│   ├── model/         # Entity classes
│   ├── service/       # Business logic services
│   └── utils/         # Utility classes
├── config/            # Configuration files
├── database/          # Database schema and data
└── pom.xml           # Maven configuration
```

## Features

- User registration and login
- Bank account management
- Deposit, withdrawal, and transfer operations
- Transaction history
- Admin panel with system management
- Account freezing/unfreezing
- Search and filtering capabilities

## Development

To add new features:
1. Create model classes in `model/` package
2. Create DAO classes in `dao/` package
3. Create service classes in `service/` package
4. Update the main application as needed
5. Add corresponding database tables if required
