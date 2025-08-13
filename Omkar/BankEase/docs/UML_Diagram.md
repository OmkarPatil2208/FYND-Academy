# BankEase UML Class Diagram

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        BankEase System                        │
├─────────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │    User     │    │ BankAccount │    │ Transaction │      │
│  │             │    │             │    │             │      │
│  │ -userId     │    │ -accountId  │    │ -transactionId│     │
│  │ -username   │    │ -accountNumber│   │ -transactionType│   │
│  │ -password   │    │ -userId     │    │ -accountId  │      │
│  │ -email      │    │ -accountType│    │ -amount     │      │
│  │ -fullName   │    │ -balance    │    │ -description│      │
│  │ -phoneNumber│    │ -creationDate│   │ -timestamp  │      │
│  │ -registrationDate│ -isActive   │    │ -status     │      │
│  │ -isActive   │    │ -isFrozen   │    │             │      │
│  │ -accounts   │    │ -transactions│    │             │      │
│  │             │    │             │    │             │      │
│  │ +addAccount()│   │ +addTransaction()│ │ +isCompleted()│   │
│  │ +getAccountByNumber()│ +getTransactionsByType()│ +isFailed()│ │
│  │ +getTotalBalance()│ +getTotalDeposits()│ +isPending()│    │
│  └─────────────┘    │ +getTotalWithdrawals()│ +isTransfer()│ │
│                     │ +getTotalTransfersIn()│ +isDeposit()│  │
│                     │ +getTotalTransfersOut()│ +isWithdrawal()││
│                     └─────────────┘    │ +getFormattedAmount()││
│                                       │ +getFormattedTimestamp()││
│                                       └─────────────┘      │
│                                                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │    Admin    │    │ BankingService│   │ AdminService│      │
│  │             │    │             │    │             │      │
│  │ -adminId    │    │ -userDAO    │    │ -adminDAO   │      │
│  │ -username   │    │ -accountDAO │    │ -userDAO    │      │
│  │ -password   │    │ -transactionDAO│  │ -accountDAO │      │
│  │ -fullName   │    │ -transferLock│   │ -transactionDAO│    │
│  │ -email      │    │             │    │             │      │
│  │ -role       │    │ +registerUser()│  │ +loginAdmin()│     │
│  │ -creationDate│   │ +loginUser()│    │ +createAdmin()│     │
│  │ -isActive   │    │ +createAccount()│ │ +getAllUsers()│     │
│  │ -lastLogin  │    │ +deposit()  │    │ +getAllAccounts()│   │
│  │             │    │ +withdraw() │    │ +getAllTransactions()││
│  │ +isSuperAdmin()│ │ +transfer() │    │ +updateAccountFrozenStatus()││
│  │ +isAdmin()  │    │ +getAccountBalance()│ +getSystemStatistics()││
│  │ +isSupport()│    │ +getTransactionHistory()│ +searchUsers()││
│  │ +updateLastLogin()│ +getUserAccounts()│ +searchAccounts()││
│  │ +canFreezeAccounts()│ +updateAccountFrozenStatus()│ +getTransactionsByFilter()││
│  │ +canViewAllTransactions()│ +getAccountStatistics()│       │
│  │ +canManageUsers()│ +generateAccountNumber()│              │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│                                                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   UserDAO   │    │ BankAccountDAO│   │ TransactionDAO│    │
│  │             │    │             │    │             │      │
│  │ +createUser()│   │ +createAccount()│ │ +createTransaction()││
│  │ +findByUsername()│ +findByAccountNumber()│ +findById()│   │
│  │ +findById() │    │ +findById() │    │ +findByAccountId()│ │
│  │ +findAll()  │    │ +findByUserId()│  │ +findByUserId()│  │
│  │ +updateUser()│   │ +findAll()  │    │ +findAll()  │      │
│  │ +deleteUser()│   │ +updateBalance()│ │ +findByType()│     │
│  │ +updateUserStatus()│ +updateFrozenStatus()│ +findByDateRange()││
│  │ +usernameExists()│ +updateActiveStatus()│ +findByStatus()│ │
│  │ +emailExists()│   │ +deleteAccount()│  │ +updateStatus()│  │
│  │ +mapResultSetToUser()│ +accountNumberExists()│ +getTransactionStatistics()││
│  └─────────────┘    │ +getTotalBalanceForUser()│ +mapResultSetToTransaction()││
│                     │ +mapResultSetToAccount()│               │
│                     └─────────────┘    └─────────────┘      │
│                                                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   AdminDAO  │    │ DatabaseConfig│   │ BankEaseApplication││
│  │             │    │             │    │             │      │
│  │ +createAdmin()│  │ -url        │    │ -bankingService│   │
│  │ +findByUsername()│ -username   │    │ -adminService│     │
│  │ +findById() │    │ -password   │    │ -scanner    │      │
│  │ +findAll()  │    │ -driver     │    │ -currentUser│      │
│  │ +updateAdmin()│  │             │    │ -currentAdmin│     │
│  │ +updateLastLogin()│ +getConnection()│ │             │      │
│  │ +updateActiveStatus()│ +testConnection()│ +main()   │     │
│  │ +deleteAdmin()│   │ +getUrl()   │    │ +showMainMenu()│   │
│  │ +usernameExists()│ +getUsername()│   │ +userLogin()│      │
│  │ +emailExists()│   │ +getPassword()│  │ +userRegistration()││
│  │ +findByRole()│    │ +getDriver()│    │ +adminLogin()│     │
│  │ +mapResultSetToAdmin()│ +loadDatabaseConfig()│ +showUserMenu()││
│  └─────────────┘    │ +setDefaultConfig()│ +showAdminMenu()│ │
│                     └─────────────┘    └─────────────┘      │
│                                                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │ InsufficientFundsException│ AccountFrozenException│ InvalidAccountException││
│  │             │    │             │    │             │      │
│  │ +InsufficientFundsException()│ +AccountFrozenException()│ +InvalidAccountException()││
│  │ +InsufficientFundsException(String)│ +AccountFrozenException(String)│ +InvalidAccountException(String)││
│  │ +InsufficientFundsException(String, Throwable)│ +AccountFrozenException(String, Throwable)│ +InvalidAccountException(String, Throwable)││
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│                                                               │
└─────────────────────────────────────────────────────────────────┘
```

## Class Relationships

### Inheritance Hierarchy
- All custom exceptions extend `Exception`
- All DAO classes follow the same pattern for database operations

### Associations
- **User** ↔ **BankAccount** (1:N): A user can have multiple bank accounts
- **BankAccount** ↔ **Transaction** (1:N): An account can have multiple transactions
- **User** ↔ **Transaction** (1:N): A user can have multiple transactions across accounts
- **Admin** ↔ **User** (1:N): An admin can manage multiple users
- **Admin** ↔ **BankAccount** (1:N): An admin can manage multiple accounts
- **Admin** ↔ **Transaction** (1:N): An admin can view all transactions

### Dependencies
- **BankingService** depends on **UserDAO**, **BankAccountDAO**, **TransactionDAO**
- **AdminService** depends on **AdminDAO**, **UserDAO**, **BankAccountDAO**, **TransactionDAO**
- **BankEaseApplication** depends on **BankingService**, **AdminService**
- All DAO classes depend on **DatabaseConfig**

## Package Structure

```
com.bankease/
├── model/
│   ├── User.java
│   ├── BankAccount.java
│   ├── Transaction.java
│   └── Admin.java
├── dao/
│   ├── UserDAO.java
│   ├── BankAccountDAO.java
│   ├── TransactionDAO.java
│   └── AdminDAO.java
├── service/
│   ├── BankingService.java
│   └── AdminService.java
├── exceptions/
│   ├── InsufficientFundsException.java
│   ├── AccountFrozenException.java
│   └── InvalidAccountException.java
├── utils/
│   └── DatabaseConfig.java
└── main/
    └── BankEaseApplication.java
```

## Design Patterns Used

### 1. DAO Pattern
- **Purpose**: Separate data access logic from business logic
- **Implementation**: Each entity has its corresponding DAO class
- **Benefits**: Easy to switch database implementations, testable code

### 2. Service Layer Pattern
- **Purpose**: Encapsulate business logic and coordinate between DAOs
- **Implementation**: BankingService and AdminService classes
- **Benefits**: Centralized business logic, transaction management

### 3. Singleton Pattern (DatabaseConfig)
- **Purpose**: Ensure single database configuration instance
- **Implementation**: Static methods and configuration loading
- **Benefits**: Consistent database connection management

### 4. Factory Pattern (Account Generation)
- **Purpose**: Generate unique account numbers
- **Implementation**: generateAccountNumber() method in BankingService
- **Benefits**: Centralized account number generation logic

## Thread Safety

### ReentrantLock Usage
```java
private final ReentrantLock transferLock = new ReentrantLock();

public List<Transaction> transfer(...) {
    transferLock.lock();
    try {
        // Transfer logic
    } finally {
        transferLock.unlock();
    }
}
```

### Thread-Safe Operations
- Account balance updates
- Transaction creation
- Transfer operations
- Database connections

## Database Relationships

### ERD (Entity Relationship Diagram)
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    users    │    │   accounts  │    │ transactions│
│             │    │             │    │             │
│ user_id (PK)│◄───┤ user_id (FK)│◄───┤ account_id (FK)│
│ username    │    │ account_id (PK)│  │ transaction_id (PK)│
│ password    │    │ account_number│   │ transaction_type│
│ email       │    │ account_type│    │ amount      │
│ full_name   │    │ balance     │    │ description │
│ phone_number│    │ creation_date│   │ timestamp   │
│ registration_date│ is_active   │    │ status      │
│ is_active   │    │ is_frozen   │    │ from_account_number│
└─────────────┘    └─────────────┘    │ to_account_number│
                                       └─────────────┘
┌─────────────┐
│   admins    │
│             │
│ admin_id (PK)│
│ username    │
│ password    │
│ full_name   │
│ email       │
│ role        │
│ creation_date│
│ last_login  │
│ is_active   │
└─────────────┘
```

## Security Considerations

### Authentication
- Username/password validation
- Session management
- Role-based access control

### Data Protection
- Prepared statements for SQL injection prevention
- Input validation
- Account freezing capabilities

### Transaction Security
- Atomic operations
- Rollback mechanisms
- Audit trails

## Performance Optimizations

### Database Indexes
- Primary keys on all tables
- Foreign key indexes
- Composite indexes for common queries
- Timestamp indexes for transaction history

### Connection Pooling
- Database connection reuse
- Connection timeout handling
- Connection validation

### Caching Strategy
- In-memory transaction lists
- Account balance caching
- User session caching

## Testing Strategy

### Unit Tests
- Service layer testing
- DAO layer testing
- Exception handling testing

### Integration Tests
- Database operations
- End-to-end workflows
- Concurrent operations

### Test Data Management
- Test database setup
- Data cleanup procedures
- Mock data generation

---

This UML diagram provides a comprehensive overview of the BankEase system architecture, showing the relationships between classes, packages, and the overall system design. 