# BankEase - Java Console Banking System

A complete Java console-based banking management system built with OOP principles, JDBC, multithreading, and comprehensive exception handling.

## 🏦 Features

### User Features
- **User Registration & Login**: Secure user authentication system
- **Account Management**: Create multiple bank accounts (Savings, Checking, Fixed Deposit)
- **Transaction Operations**: 
  - Deposit funds
  - Withdraw funds
  - Transfer between accounts
- **Real-time Balance**: View current account balances
- **Transaction History**: Complete transaction history with timestamps
- **Account Statistics**: View transaction summaries and statistics

### Admin Features
- **Admin Authentication**: Secure admin login system
- **User Management**: View all users and their account statuses
- **Account Management**: Freeze/unfreeze user accounts
- **Transaction Auditing**: Comprehensive transaction filtering and reporting
- **System Statistics**: View overall system metrics

## 🛠️ Technical Stack

- **Java 11+**
- **MySQL 8.0+**
- **Maven** for dependency management
- **JUnit 5** for testing
- **JDBC** for database connectivity
- **Multithreading** for concurrent operations
- **Functional Programming** with Java 8 Streams API

## 📋 Prerequisites

- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd BankEase
```

### 2. Database Setup

#### Option A: Local MySQL Setup
1. Install MySQL Server
2. Create a new database:
```sql
CREATE DATABASE bankease;
```

3. Run the schema script:
```bash
mysql -u root -p bankease < database/schema.sql
```

#### Option B: Remote Database (Railway, PlanetScale, etc.)
1. Create a MySQL database on your preferred cloud provider
2. Update the database configuration in `config/database.properties`:
```properties
db.url=jdbc:mysql://your-host:3306/bankease?useSSL=false&serverTimezone=UTC
db.username=your_username
db.password=your_password
```

### 3. Build the Project
```bash
mvn clean compile
```

### 4. Run Tests
```bash
mvn test
```

### 5. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.bankease.main.BankEaseApplication"
```

Or build and run the JAR:
```bash
mvn clean package
java -jar target/bankease-1.0.0.jar
```

## 🏗️ Project Structure

```
BankEase/
├── src/
│   ├── main/java/com/bankease/
│   │   ├── model/           # POJOs (User, BankAccount, Transaction, Admin)
│   │   ├── dao/             # Data Access Objects
│   │   ├── service/         # Business Logic Layer
│   │   ├── exceptions/      # Custom Exceptions
│   │   ├── utils/           # Utility Classes
│   │   └── main/            # Main Application Class
│   └── test/java/com/bankease/
│       └── service/         # JUnit Test Cases
├── config/                  # Configuration Files
├── database/                # Database Schema
├── pom.xml                  # Maven Configuration
└── README.md               # This File
```

## 🎯 Usage Guide

### Starting the Application
1. Run the main application
2. Choose from the main menu:
   - User Login
   - User Registration
   - Admin Login
   - Exit

### User Operations
1. **Register**: Create a new user account
2. **Login**: Access your banking dashboard
3. **Create Account**: Open new bank accounts
4. **Deposit/Withdraw**: Perform transactions
5. **Transfer**: Move money between accounts
6. **View History**: Check transaction records

### Admin Operations
1. **Login**: Use admin credentials (default: admin/admin123)
2. **View Users**: See all registered users
3. **Manage Accounts**: Freeze/unfreeze accounts
4. **Audit Transactions**: Filter and view transaction data
5. **System Statistics**: Monitor overall system health

## 🔧 Configuration

### Database Configuration
Edit `config/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/bankease?useSSL=false&serverTimezone=UTC
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Default Admin Credentials
- **Username**: admin
- **Password**: admin123
- **Role**: SUPER_ADMIN

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BankingServiceTest

# Run with detailed output
mvn test -Dtest=BankingServiceTest -Dsurefire.useFile=false
```

### Test Coverage
The project includes comprehensive JUnit tests covering:
- User registration and authentication
- Account creation and management
- Transaction operations (deposit, withdrawal, transfer)
- Exception handling
- Business logic validation

## 🔒 Security Features

- **Password Protection**: Secure user authentication
- **Account Freezing**: Admin can freeze suspicious accounts
- **Transaction Validation**: Comprehensive input validation
- **SQL Injection Prevention**: Prepared statements usage
- **Thread Safety**: Concurrent transfer operations

## 🚀 Advanced Features

### Multithreading
- Thread-safe transfer operations using `ReentrantLock`
- Concurrent transaction processing
- Deadlock prevention mechanisms

### Functional Programming
- Java 8 Streams API for data processing
- Lambda expressions for filtering and mapping
- Functional interfaces for business logic

### Exception Handling
- Custom exceptions for business logic
- Comprehensive error messages
- Graceful error recovery

### Database Features
- Stored procedures for complex operations
- Database triggers for audit trails
- Views for reporting and analytics
- Indexes for performance optimization

## 📊 Database Schema

### Tables
1. **users**: User information and authentication
2. **admins**: Administrator accounts and roles
3. **accounts**: Bank account details and balances
4. **transactions**: Complete transaction history

### Views
- **user_account_summary**: User account statistics
- **transaction_summary**: Transaction analytics

### Stored Procedures
- **UpdateAccountBalance**: Safe balance updates
- **TransferBetweenAccounts**: Atomic transfer operations

## 🛠️ Development

### Adding New Features
1. Create model classes in `model/` package
2. Implement DAO layer in `dao/` package
3. Add business logic in `service/` package
4. Write tests in `test/` package
5. Update main application if needed

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Implement proper exception handling

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Error
```
Error: Cannot connect to database
```
**Solution**: Check database configuration in `config/database.properties`

#### Compilation Errors
```
BankAccount cannot be resolved to a type
```
**Solution**: Ensure all model classes are properly imported

#### Test Failures
```
SQLException: Table doesn't exist
```
**Solution**: Run the database schema script first

### Logs
- Application logs are displayed in the console
- Database errors are logged with detailed messages
- Test results show pass/fail status

## 📈 Performance Considerations

- Database indexes for fast queries
- Connection pooling for database efficiency
- Thread-safe operations for concurrent access
- Optimized queries with proper joins

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Java 8 Streams API for functional programming
- MySQL for robust database management
- JUnit 5 for comprehensive testing
- Maven for dependency management

## 📞 Support

For issues and questions:
1. Check the troubleshooting section
2. Review the test cases for examples
3. Examine the database schema
4. Contact the development team

---

**BankEase** - Making banking simple and secure! 🏦✨ 