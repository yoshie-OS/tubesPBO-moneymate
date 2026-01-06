# MoneyMate - Comprehensive UML Diagram & OOP Analysis

## Project Overview
MoneyMate is a personal finance management application built with Spring Boot backend and TypeScript frontend, demonstrating advanced OOP concepts through its architecture.

---

## 1. CLASS HIERARCHY DIAGRAM

### 1.1 Inheritance Hierarchy - User Classes

```
┌─────────────────────────────────────┐
│     <<abstract>> User               │
├─────────────────────────────────────┤
│ #userId: String                     │
│ #username: String                   │
│ #email: String                      │
│ #createdAt: LocalDateTime           │
├─────────────────────────────────────┤
│ +getUsername(): String              │
│ +getEmail(): String                 │
│ +displayUserInfo(): void (abstract) │
└─────────────────────────────────────┘
           ▲
           │ inherits
           │
┌─────────────────────────────────────┐
│    RegularUser                      │
├─────────────────────────────────────┤
│ -password: String                   │
│ -initialBalance: double             │
├─────────────────────────────────────┤
│ +getInitialBalance(): double        │
│ +setInitialBalance(double): void    │
│ +displayUserInfo(): void            │
└─────────────────────────────────────┘
```

### 1.2 Inheritance Hierarchy - Transaction Classes

```
┌─────────────────────────────────────────┐
│   <<abstract>> Transaction              │
├─────────────────────────────────────────┤
│ #transactionId: String                  │
│ #amount: double                         │
│ #description: String                    │
│ #date: LocalDate                        │
│ #category: Category                     │
├─────────────────────────────────────────┤
│ +getTransactionId(): String             │
│ +getAmount(): double                    │
│ +getTransactionType(): String (abstract)│
│ +isValid(): boolean                     │
│ -generateTransactionId(): String        │
└─────────────────────────────────────────┘
           ▲
           │ inherits
           ├──────────────────┬──────────────────┐
           │                  │                  │
    ┌──────────────┐  ┌──────────────┐         (Future: Transfer)
    │    Income    │  │   Expense    │
    ├──────────────┤  ├──────────────┤
    │-source:String
│ -paymentMethod:String│
    │              │  │-isRecurring: │
    ├──────────────┤  │  boolean     │
    │+getSource(): │  ├──────────────┤
    │String        │  │+getPayment   │
    │+setSource(): │  │Method():     │
    │void          │  │String        │
    └──────────────┘  │+isRecurring()│
                      │:boolean      │
                      └──────────────┘
```

---

## 2. INTERFACE IMPLEMENTATIONS

### 2.1 Calculable Interface (Abstraction)

```
┌──────────────────────────────────────┐
│   <<interface>> Calculable           │
├──────────────────────────────────────┤
│ +calculateTotalBalance(): double     │
│ +calculateTotalIncome(): double      │
│ +calculateTotalExpense(): double     │
│ +getTransactions(): List<Transaction>│
└──────────────────────────────────────┘
           ▲
           │ implements
           │
┌──────────────────────────────────────┐
│   TransactionManager                 │
│   (Singleton-like Controller)        │
├──────────────────────────────────────┤
│ -transactions: List<Transaction>     │
│ -initialBalance: double              │
│ -transactionDAO: TransactionDAO      │
│ -currentUserId: String               │
├──────────────────────────────────────┤
│ +addTransaction(Transaction): void   │
│ +deleteTransaction(id: String): void │
│ +calculateTotalBalance(): double     │
│ +calculateTotalIncome(): double      │
│ +calculateTotalExpense(): double     │
│ +generateReport(): Report            │
└──────────────────────────────────────┘
```

### 2.2 Exportable Interface (Abstraction)

```
┌──────────────────────────────────────┐
│   <<interface>> Exportable           │
├──────────────────────────────────────┤
│ +exportToFile(filePath: String):void │
│ +getExportFormat(): String           │
└──────────────────────────────────────┘
           ▲
           │ implements
           │
┌──────────────────────────────────────┐
│   FileExporter                       │
├──────────────────────────────────────┤
│ -transactionManager:                 │
│  TransactionManager                  │
│ -format: String                      │
├──────────────────────────────────────┤
│ +exportToFile(filePath:String):void  │
│ +exportToCSV(path:String):void       │
│ +exportToTXT(path:String):void       │
│ +getExportFormat():String            │
└──────────────────────────────────────┘
```

---

## 3. COMPOSITION & AGGREGATION DIAGRAM

```
┌────────────────────────────────────┐
│      TransactionManager            │
├────────────────────────────────────┤
│ -transactions: List<Transaction>   │ ◇───────────► Transaction
│ -transactionDAO: TransactionDAO    │ │ 0..*         (Composition)
│                                    │ │
│                                    │ ◇───────────► Report
│                                    │   (Aggregation)
└────────────────────────────────────┘


┌────────────────────────────────────┐
│      Report                        │
├────────────────────────────────────┤
│ -transactions: List<Transaction>   │ ◇───────────► Transaction
│ -reportPeriod: YearMonth           │   0..*        (Aggregation)
│ -initialBalance: double            │
└────────────────────────────────────┘


┌────────────────────────────────────┐
│      Transaction                   │
├────────────────────────────────────┤
│ -category: Category                │ ◇───────────► Category
│                                    │   (Composition)
└────────────────────────────────────┘
```

---

## 4. COMPLETE ARCHITECTURE DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                             │
│                                                                         │
│  ┌────────────────────────────┐      ┌────────────────────────────┐    │
│  │   Frontend (TypeScript)    │      │   REST API Controller      │    │
│  ├────────────────────────────┤      ├────────────────────────────┤    │
│  │ MoneyMateApp (Class)       │◄────►│ TransactionController      │    │
│  │ - loadTransactions()       │      │ UserController             │    │
│  │ - addTransaction()         │      │ - POST /transactions       │    │
│  │ - deleteTransaction()      │      │ - GET /transactions        │    │
│  │ - generateReport()         │      │ - DELETE /transactions/{id}│    │
│  └────────────────────────────┘      └────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │ HTTP/JSON
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        BUSINESS LOGIC LAYER                             │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │           TransactionManager (Implements Calculable)            │  │
│  ├──────────────────────────────────────────────────────────────────┤  │
│  │ Responsibilities:                                                │  │
│  │ - Manage all transactions (add, update, delete)                │  │
│  │ - Calculate balance, income, expense                           │  │
│  │ - Generate reports                                             │  │
│  │ - Multi-user transaction management                            │  │
│  │                                                                  │  │
│  │ Interfaces Implemented:                                         │  │
│  │ - Calculable (for balance calculations)                        │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                      ▲              ▲              ▲                    │
│                      │              │              │                    │
│           ┌──────────┴────┐    ┌────┴──────────┐   │                   │
│           │                │    │               │   │                   │
│      Creates          Uses      Uses        Aggregates                  │
│           │                │    │               │   │                   │
│           ▼                ▼    ▼               ▼   ▼                   │
│     ┌──────────────┐  ┌─────────────┐  ┌──────────────────┐            │
│     │ Transaction  │  │ FileExporter│  │ Report Generator │            │
│     │ (Abstract)   │  │             │  │                  │            │
│     └──────────────┘  └─────────────┘  └──────────────────┘            │
│           ▲                  │                                          │
│           │                  │                                          │
│      ┌────┴────┐      Implements Exportable                            │
│      │    │                                                             │
│   Income Expense                                                        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ Uses
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          DATA ACCESS LAYER                              │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │         TransactionDAO (Interface)                              │  │
│  ├──────────────────────────────────────────────────────────────────┤  │
│  │ +save(Transaction): void                                        │  │
│  │ +update(Transaction): void                                      │  │
│  │ +delete(id: String): void                                       │  │
│  │ +findById(id: String): Transaction                              │  │
│  │ +findAll(userId: String): List<Transaction>                     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│           ▲                                                             │
│           │ Implements                                                  │
│           │                                                             │
│  ┌────────┴───────────────────────────────────────────────────────┐   │
│  │    TransactionDAOImpl (Concrete Implementation)               │   │
│  ├────────────────────────────────────────────────────────────────┤   │
│  │ Responsibilities:                                              │   │
│  │ - Execute SQL queries                                         │   │
│  │ - Map ResultSet to Transaction objects                        │   │
│  │ - Handle database operations                                  │   │
│  └────────────────────────────────────────────────────────────────┘   │
│           │                                                             │
│           │ Uses                                                        │
│           ▼                                                             │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │         DatabaseManager (Singleton)                            │  │
│  ├──────────────────────────────────────────────────────────────────┤  │
│  │ -instance: DatabaseManager (static)                            │  │
│  │ -connection: Connection                                        │  │
│  ├──────────────────────────────────────────────────────────────────┤  │
│  │ +getInstance(): DatabaseManager (static)                       │  │
│  │ +getConnection(): Connection                                   │  │
│  │ +initializeTables(): void                                      │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ JDBC
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          DATABASE LAYER                                 │
│                                                                         │
│           SQLite Database (moneymate.db)                               │
│           ├── users table                                              │
│           ├── transactions table                                       │
│           └── indices for optimization                                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 5. OOP CONCEPTS IMPLEMENTED

### 5.1 ENCAPSULATION
**Definition:** Bundling data and methods that operate on that data, hiding internal details.

**Implementation:**
```
TransactionManager:
- Private fields: transactions, initialBalance, transactionDAO, currentUserId
- Public methods for controlled access to data
- Getter/setter methods with validation

FileExporter:
- Private transactionManager field
- Private format field
- Public methods for exporting

Transaction (Abstract):
- Protected fields (accessible to subclasses)
- Private helper methods (generateTransactionId)
```

### 5.2 INHERITANCE
**Definition:** Creating new classes based on existing ones, promoting code reuse.

**Implementation:**
```
Transaction Hierarchy:
Transaction (Parent)
├── Income (Child)
└── Expense (Child)

User Hierarchy:
User (Abstract Parent)
└── RegularUser (Concrete Child)

Benefits:
- Code reuse: Income and Expense reuse Transaction logic
- RegularUser specializes User with password and balance
- Polymorphic behavior through overridden methods
```

### 5.3 POLYMORPHISM
**Definition:** Objects of different types can be treated through the same interface.

**Implementation:**
```
1. Method Overriding:
   - Transaction.getTransactionType() - abstract in parent
   - Income overrides: returns "PEMASUKAN"
   - Expense overrides: returns "PENGELUARAN"

2. Interface-based Polymorphism:
   - List<Transaction> can contain Income and Expense objects
   - FileExporter implements Exportable interface
   - Calculated as Transaction regardless of subtype
   
3. Example Usage:
   List<Transaction> transactions = new ArrayList<>();
   transactions.add(new Income(...));      // Income is-a Transaction
   transactions.add(new Expense(...));     // Expense is-a Transaction
   
   for (Transaction t : transactions) {
       System.out.println(t.getTransactionType());  // Polymorphic call
   }
```

### 5.4 ABSTRACTION
**Definition:** Hiding implementation details and showing only essential features.

**Implementation:**
```
1. Abstract Classes:
   - Transaction (abstract parent)
   - User (abstract parent)
   - Force subclasses to implement abstract methods

2. Interfaces:
   - Calculable: Abstract calculation operations
     ├── calculateTotalBalance()
     ├── calculateTotalIncome()
     └── calculateTotalExpense()
   
   - Exportable: Abstract export operations
     ├── exportToFile()
     └── getExportFormat()

3. Benefits:
   - Clients use high-level interfaces, not implementation details
   - Easy to swap implementations (e.g., different exporters)
   - Clear contracts for what each class should do
```

### 5.5 COMPOSITION
**Definition:** "Has-a" relationship where a class contains instances of other classes.

**Implementation:**
```
TransactionManager HAS-A:
- List<Transaction> (owns the transactions)
- TransactionDAO (owns the data access layer)

Transaction HAS-A:
- Category (owns category information)

Report HAS-A:
- List<Transaction> (owns filtered transactions)

Benefits:
- Strong ownership relationship
- Lifecycle management of contained objects
- Flexibility to change implementations
```

### 5.6 AGGREGATION
**Definition:** "Has-a" relationship where classes can exist independently.

**Implementation:**
```
Report AGGREGATES:
- List<Transaction> (transactions can exist independently)
- Don't own transactions, just reference them

FileExporter AGGREGATES:
- TransactionManager (can exist without FileExporter)
- Don't own manager, just use it

Benefits:
- Loose coupling between classes
- Can use same TransactionManager with different exporters
- Objects can be shared across multiple containers
```

---

## 6. DESIGN PATTERNS USED

### 6.1 Singleton Pattern
```java
public class DatabaseManager {
    private static DatabaseManager instance;
    
    private DatabaseManager() { }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
// Ensures only one database connection throughout application
```

### 6.2 Data Access Object (DAO) Pattern
```java
TransactionDAO (Interface)
    ↓
TransactionDAOImpl (Implementation)
    ↓
DatabaseManager (Connection)
    ↓
SQLite Database

// Separates data access logic from business logic
```

### 6.3 Strategy Pattern
```java
FileExporter implements Exportable {
    public void exportToFile(String filePath) {
        switch(format) {
            case "CSV": exportToCSV(filePath); break;
            case "TXT": exportToTXT(filePath); break;
        }
    }
}
// Different export strategies for different formats
```

### 6.4 Factory Pattern
```java
Transaction.fromString(type, categoryName)
Income.fromJSON(jsonObject)
Expense.fromJSON(jsonObject)

// Creates appropriate object type based on input
```

---

## 7. PACKAGE STRUCTURE & RESPONSIBILITIES

```
moneymate/
├── api/
│   ├── TransactionController       [REST endpoint for transactions]
│   └── UserController              [REST endpoint for users]
│
├── config/
│   └── AppConfig                   [Spring configuration]
│
├── controller/
│   └── TransactionManager          [Business logic - Implements Calculable]
│
├── database/
│   ├── DatabaseManager             [Singleton - connection management]
│   ├── TransactionDAO              [Interface]
│   └── TransactionDAOImpl           [Concrete implementation]
│
├── exception/
│   ├── FileExportException         [Custom exception]
│   ├── InsufficientBalanceException
│   ├── InvalidTransactionException
│   └── TransactionNotFoundException
│
├── interfaces/
│   ├── Calculable                  [Interface for calculations]
│   └── Exportable                  [Interface for export operations]
│
├── model/
│   ├── Category                    [Enum for categories]
│   ├── User                        [Abstract class]
│   ├── RegularUser                 [Concrete user implementation]
│   ├── Transaction                 [Abstract class]
│   ├── Income                      [Concrete transaction type]
│   ├── Expense                     [Concrete transaction type]
│   └── Report                      [Financial reports]
│
└── util/
    ├── FileExporter                [Implements Exportable]
    ├── DateUtil                    [Date utilities]
    └── InputValidator              [Input validation]
```

---

## 8. METHOD COLLABORATION DIAGRAM

### Adding a Transaction Flow

```
Frontend (HTML/TypeScript)
    │
    │ POST /api/transactions
    │ { amount, description, type, category, ... }
    │
    ▼
TransactionController.addTransaction()
    │
    ├─► InputValidator.validate()         [Validate input]
    │
    ├─► TransactionManager.addTransaction()
    │   │
    │   ├─► Transaction.isValid()         [Check validity]
    │   │
    │   ├─► transactionDAO.save()         [Persist to DB]
    │   │   │
    │   │   └─► TransactionDAOImpl.save()
    │   │       │
    │   │       └─► DatabaseManager.getConnection()
    │   │           │
    │   │           └─► SQLite Database [Insert]
    │   │
    │   ├─► calculateTotalBalance()       [Update calculations]
    │   │
    │   └─► transactions.add()            [Update in-memory list]
    │
    ├─► Report.generateSummary()          [Generate report]
    │
    └─► API Response (JSON)
            │
            ▼
        Frontend updates UI
```

---

## 9. Exception Handling Architecture

```
┌─────────────────────────────────────┐
│      <<exception>>                  │
│      Exception (Java)               │
└─────────────────────────────────────┘
           ▲
           │ extends
           │
┌─────────────────────────────────────┐
│  Custom Exception Hierarchy         │
├─────────────────────────────────────┤
│ • FileExportException               │
│   - Thrown when export fails        │
│                                     │
│ • InsufficientBalanceException      │
│   - Thrown when balance too low     │
│                                     │
│ • InvalidTransactionException       │
│   - Thrown when transaction invalid │
│                                     │
│ • TransactionNotFoundException      │
│   - Thrown when transaction not found│
└─────────────────────────────────────┘
```

---

## 10. DEPENDENCIES & RELATIONSHIPS SUMMARY

| Class | Depends On | Relationship | Purpose |
|-------|-----------|--------------|---------|
| TransactionManager | TransactionDAO | Uses | Data persistence |
| TransactionManager | Transaction | Creates | Transaction management |
| TransactionController | TransactionManager | Uses | Business logic |
| FileExporter | Exportable | Implements | File operations |
| FileExporter | TransactionManager | Uses | Access transactions |
| Report | Transaction | Aggregates | Financial analysis |
| RegularUser | User | Extends | Specialization |
| Income | Transaction | Extends | Specialization |
| Expense | Transaction | Extends | Specialization |
| TransactionDAOImpl | DatabaseManager | Uses | DB connection |
| Frontend | TransactionController | Calls via REST | API communication |

---

## 11. OOP PRINCIPLES APPLIED

### Single Responsibility Principle (SRP)
- **TransactionManager**: Only manages transactions
- **FileExporter**: Only exports data
- **DatabaseManager**: Only manages connections
- **TransactionDAOImpl**: Only handles transaction persistence

### Open/Closed Principle (OCP)
- **Interfaces (Calculable, Exportable)**: Open for extension, closed for modification
- **Abstract classes**: Define contracts, allow subclasses to implement

### Liskov Substitution Principle (LSP)
- **Income and Expense**: Can be used wherever Transaction is expected
- **RegularUser**: Can be used wherever User is expected

### Interface Segregation Principle (ISP)
- **Calculable**: Only calculation methods
- **Exportable**: Only export methods
- No unnecessary methods in interfaces

### Dependency Inversion Principle (DIP)
- **TransactionManager depends on TransactionDAO interface**, not concrete implementation
- **FileExporter depends on TransactionManager interface**, not concrete class

---

## 12. KEY METRICS

- **Total Classes**: 24+
- **Abstract Classes**: 2 (Transaction, User)
- **Interfaces**: 2 (Calculable, Exportable)
- **Concrete Implementations**: 20+
- **Custom Exceptions**: 4
- **Design Patterns**: 4 (Singleton, DAO, Strategy, Factory)
- **Layers**: 4 (Presentation, Business Logic, Data Access, Database)

---

## 13. EXTENSIBILITY & FUTURE ENHANCEMENTS

```
Possible Extensions:

1. New Transaction Types:
   Transaction (abstract)
   ├── Income
   ├── Expense
   ├── Transfer          [NEW]
   └── Investment        [NEW]

2. New Export Formats:
   Exportable (interface)
   ├── FileExporter.exportToCSV()
   ├── FileExporter.exportToTXT()
   ├── ExcelExporter.exportToXLSX()  [NEW]
   └── PDFExporter.exportToPDF()      [NEW]

3. New User Types:
   User (abstract)
   ├── RegularUser
   ├── BusinessUser      [NEW]
   └── PremiumUser       [NEW]

4. New Calculations:
   Calculable (interface)
   ├── calculateTotalBalance()
   ├── calculateTotalIncome()
   ├── calculateTotalExpense()
   ├── calculateMonthlyTrend() [NEW]
   └── calculateCategoryBreakdown() [NEW]

All can be added without modifying existing code!
```

---

## CONCLUSION

The MoneyMate project demonstrates **strong OOP principles** through:
- Clear inheritance hierarchies (Transaction, User)
- Well-defined interfaces (Calculable, Exportable)
- Proper encapsulation of responsibilities
- Loose coupling between layers via interfaces
- Multiple design patterns for flexibility
- Scalable and maintainable architecture

This architecture supports **SOLID principles** and allows for **easy extension** without modifying existing code.

