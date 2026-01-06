# MoneyMate - OOP Concepts Deep Dive & Implementation Guide

## Table of Contents
1. [Object-Oriented Programming Overview](#overview)
2. [Detailed OOP Concepts](#concepts)
3. [Design Patterns](#patterns)
4. [Code Examples](#examples)
5. [SOLID Principles](#solid)
6. [Best Practices](#practices)

---

## OVERVIEW

MoneyMate demonstrates advanced Object-Oriented Programming (OOP) through its architecture:

- **7 Core OOP Concepts** implemented
- **4 Major Design Patterns** utilized
- **5 SOLID Principles** followed
- **4-Layer Architecture** with clear separation of concerns

---

## CONCEPTS

### 1. ENCAPSULATION

**Definition:** Bundling data (attributes) and methods that operate on that data into a single unit (class), and hiding internal details from the outside world.

**Why:** 
- Protects data integrity
- Reduces coupling
- Allows controlled access to internal state

**Implementation in MoneyMate:**

#### TransactionManager Example:
```java
public class TransactionManager implements Calculable {
    // PRIVATE - Not accessible from outside
    private List<Transaction> transactions;
    private double initialBalance;
    private TransactionDAO transactionDAO;
    private String currentUserId;
    
    // PUBLIC - Controlled access points
    public void addTransaction(Transaction transaction) 
            throws InvalidTransactionException {
        // Validation logic
        if (!transaction.isValid()) {
            throw new InvalidTransactionException("Invalid!");
        }
        
        // Only add if valid
        transactionDAO.save(transaction);
        transactions.add(transaction);
    }
    
    // GETTER - Read-only access
    public double calculateTotalBalance() {
        return initialBalance + calculateTotalIncome() - calculateTotalExpense();
    }
    
    // SETTER - Controlled write with validation
    public void setInitialBalance(double balance) {
        if (balance >= 0) {
            this.initialBalance = balance;
        } else {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
    }
}
```

**Benefits in MoneyMate:**
- Cannot directly modify `transactions` list from outside
- Cannot set negative `initialBalance`
- DAO layer is hidden from API consumers
- All modifications go through validated methods

---

### 2. INHERITANCE

**Definition:** Creating a new class based on an existing class, inheriting all its attributes and methods. The new class (child) extends the existing class (parent).

**Why:**
- Code reuse
- Establish relationships
- Polymorphic behavior

**Inheritance Hierarchies in MoneyMate:**

#### Transaction Hierarchy:
```java
// PARENT CLASS
public abstract class Transaction {
    protected String transactionId;
    protected double amount;
    protected String description;
    protected LocalDate date;
    protected Category category;
    
    // Shared implementation
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    
    // Abstract - must be implemented by children
    public abstract String getTransactionType();
    public abstract boolean isValid();
    
    protected String generateTransactionId() {
        return "TRX-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

// CHILD CLASS #1
public class Income extends Transaction {
    private String source;  // Additional attribute
    
    public Income(double amount, String description, LocalDate date, 
                  Category category, String source) {
        super(amount, description, date, category);  // Call parent constructor
        this.source = source;
    }
    
    @Override  // Override parent abstract method
    public String getTransactionType() {
        return "PEMASUKAN";
    }
    
    @Override
    public boolean isValid() {
        return amount > 0 && description != null && !description.isEmpty();
    }
    
    // Additional method specific to Income
    public String getSource() { return source; }
}

// CHILD CLASS #2
public class Expense extends Transaction {
    private String paymentMethod;  // Additional attribute
    private boolean isRecurring;   // Additional attribute
    
    public Expense(double amount, String description, LocalDate date,
                   Category category, String paymentMethod, boolean isRecurring) {
        super(amount, description, date, category);  // Call parent constructor
        this.paymentMethod = paymentMethod;
        this.isRecurring = isRecurring;
    }
    
    @Override  // Override parent abstract method
    public String getTransactionType() {
        return "PENGELUARAN";
    }
    
    @Override
    public boolean isValid() {
        return amount > 0 && description != null && !description.isEmpty() 
            && paymentMethod != null;
    }
    
    // Additional methods specific to Expense
    public String getPaymentMethod() { return paymentMethod; }
    public boolean isRecurring() { return isRecurring; }
}
```

#### User Hierarchy:
```java
// PARENT CLASS
public abstract class User {
    protected String userId;
    protected String username;
    protected String email;
    protected LocalDateTime createdAt;
    
    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
    
    // Abstract method - subclasses must implement
    public abstract void displayUserInfo();
}

// CHILD CLASS
public class RegularUser extends User {
    private String password;
    private double initialBalance;
    
    public RegularUser(String userId, String username, String email,
                      String password, double initialBalance) {
        super(userId, username, email);  // Call parent constructor
        this.password = password;
        this.initialBalance = initialBalance;
    }
    
    @Override  // Implement parent abstract method
    public void displayUserInfo() {
        System.out.println("ID: " + userId);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Balance: " + initialBalance);
    }
}
```

**Benefits:**
- Income and Expense reuse Transaction logic (transactionId, amount, validation)
- Both can be treated as Transaction objects
- Shared functionality in one place
- Easy to add new transaction types (Transfer, Investment)

---

### 3. POLYMORPHISM

**Definition:** The ability of objects to take multiple forms. Same interface, different implementations.

**Types in MoneyMate:**

#### A. Method Overriding (Runtime Polymorphism)
```java
// Same method name, different implementation
List<Transaction> transactions = new ArrayList<>();
transactions.add(new Income(100000, "Salary", date, Category.SALARY, "Employer"));
transactions.add(new Expense(50000, "Food", date, Category.MAKANAN, "Cash", false));
transactions.add(new Income(500000, "Bonus", date, Category.BONUS, "Employer"));

// Polymorphic loop - same code, different behavior
for (Transaction t : transactions) {
    // Which getTransactionType() gets called depends on RUNTIME type!
    System.out.println(t.getTransactionType());
}

Output:
PEMASUKAN      // Income.getTransactionType()
PENGELUARAN    // Expense.getTransactionType()
PEMASUKAN      // Income.getTransactionType()
```

#### B. Interface Polymorphism
```java
// FileExporter and future PDF/Excel exporters all implement Exportable
Exportable exporter1 = new FileExporter(manager, "CSV");
// Exportable exporter2 = new PDFExporter(manager);  [Future]
// Exportable exporter3 = new ExcelExporter(manager);  [Future]

// Same interface, can swap implementations without changing code!
exporter1.exportToFile("report.csv");
// exporter2.exportToFile("report.pdf");
// exporter3.exportToFile("report.xlsx");
```

#### C. Collection Polymorphism
```java
// The magic of polymorphism: mixed types in single list
List<Transaction> transactions = transactionManager.getTransactions();

// Contains both Income and Expense objects
double totalBalance = 0;
for (Transaction t : transactions) {
    // Even though they're stored as Transaction, 
    // correct behavior happens based on actual type
    if (t instanceof Income) {
        totalBalance += t.getAmount();  // Income adds
    } else if (t instanceof Expense) {
        totalBalance -= t.getAmount();  // Expense subtracts
    }
}

// Better approach - let polymorphism do the work:
public double calculateBalance() {
    return initialBalance + 
           calculateTotalIncome() - 
           calculateTotalExpense();
}

// Inside these methods:
private double calculateTotalIncome() {
    return transactions.stream()
        .filter(t -> t instanceof Income)
        .mapToDouble(Transaction::getAmount)
        .sum();
}
```

**Benefits:**
- Write code once, work with all transaction types
- New transaction types work without code changes (Income/Expense/Transfer)
- Different exporters can be swapped seamlessly
- Reduces code duplication

---

### 4. ABSTRACTION

**Definition:** Showing only essential features and hiding implementation details. Use abstract classes and interfaces to define contracts.

**Why:**
- Separates interface from implementation
- Allows focus on "what" not "how"
- Makes code more maintainable

**Abstract Classes in MoneyMate:**

```java
// ABSTRACTION: Define contract without implementation
public abstract class Transaction {
    // Common data and implementation
    protected String transactionId;
    protected double amount;
    protected String description;
    protected LocalDate date;
    protected Category category;
    
    // Concrete methods - has implementation
    public String getTransactionId() {
        return transactionId;
    }
    
    // Abstract methods - subclasses must implement
    public abstract String getTransactionType();
    public abstract boolean isValid();
}

// User doesn't care HOW getId works, just that it returns String
String id = transaction.getTransactionId();

// User also doesn't care about implementation details
// Just knows that every transaction type has getTransactionType()
String type = transaction.getTransactionType();
// Income returns "PEMASUKAN"
// Expense returns "PENGELUARAN"
```

**Interfaces in MoneyMate:**

```java
// ABSTRACTION: Define what operations are possible
public interface Calculable {
    double calculateTotalBalance();
    double calculateTotalIncome();
    double calculateTotalExpense();
    List<Transaction> getTransactions();
}

// TransactionManager implements this contract
public class TransactionManager implements Calculable {
    // Specific implementation details
    private List<Transaction> transactions;
    private double initialBalance;
    
    @Override
    public double calculateTotalBalance() {
        return initialBalance + 
               calculateTotalIncome() - 
               calculateTotalExpense();
    }
    
    @Override
    public double calculateTotalIncome() {
        // Specific implementation
    }
    
    @Override
    public double calculateTotalExpense() {
        // Specific implementation
    }
    
    @Override
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}

// External code uses abstraction, not concrete class
Calculable calculator = new TransactionManager();
double balance = calculator.calculateTotalBalance();
// Doesn't care about internal implementation details
```

**Benefits:**
- Controllers don't need to know TransactionManager details
- Can swap implementations (e.g., different calculation strategies)
- Clear contracts for what each class should do
- Easier to test (mock interfaces)

---

### 5. COMPOSITION

**Definition:** "Has-a" relationship where one class owns and is responsible for another class. Strong ownership relationship.

**Lifetime:** Owner and owned object have linked lifetimes - if owner is destroyed, owned object may be destroyed too.

**Examples in MoneyMate:**

#### TransactionManager Composes Transaction List
```java
public class TransactionManager implements Calculable {
    // Composition: TransactionManager OWNS the list
    private List<Transaction> transactions;  // ◇ Composition
    
    public TransactionManager() {
        this.transactions = new ArrayList<>();  // Created here
    }
    
    public void addTransaction(Transaction t) {
        transactions.add(t);  // Added to owned list
    }
    
    // When TransactionManager is destroyed, 
    // the list is also destroyed
}
```

#### Transaction Composes Category
```java
public abstract class Transaction {
    // Composition: Transaction OWNS the Category
    protected Category category;  // ◇ Composition
    
    public Transaction(double amount, String description, 
                      LocalDate date, Category category) {
        this.category = category;  // Takes ownership
    }
    
    public Category getCategory() {
        return category;
    }
}

// When Transaction is destroyed, Category is too
// (or garbage collected if no other references)
```

---

### 6. AGGREGATION

**Definition:** "Has-a" relationship where one class uses/references another, but they can exist independently. Weak ownership relationship.

**Lifetime:** Objects have independent lifetimes - owned object can exist without owner.

**Examples in MoneyMate:**

#### Report Aggregates Transactions
```java
public class Report {
    // Aggregation: Report USES transactions but doesn't create them
    private List<Transaction> transactions;  // ◇ Aggregation (not ownership)
    
    public Report(List<Transaction> transactions, YearMonth period) {
        // References existing transactions
        this.transactions = new ArrayList<>(transactions);
    }
    
    // Transactions exist in TransactionManager
    // Report just references them for analysis
    
    public double getTotalIncome() {
        return transactions.stream()
            .filter(t -> t instanceof Income)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}

// Multiple reports can share same transactions
Report report1 = new Report(transactions, YearMonth.of(2025, 12));
Report report2 = new Report(transactions, YearMonth.of(2026, 1));

// Transactions still exist even if reports are deleted
// They're in TransactionManager
```

#### FileExporter Aggregates TransactionManager
```java
public class FileExporter implements Exportable {
    // Aggregation: FileExporter USES manager but doesn't own it
    private TransactionManager transactionManager;  // ◇ Aggregation
    
    public FileExporter(TransactionManager manager, String format) {
        this.transactionManager = manager;  // References existing manager
    }
    
    public void exportToCSV(String filePath) throws FileExportException {
        // Uses manager to get transactions
        List<Transaction> transactions = 
            transactionManager.getTransactions();
        
        // Export them
        // ...
    }
}

// TransactionManager exists independently
TransactionManager manager = new TransactionManager();

// Multiple exporters can use same manager
FileExporter csvExporter = new FileExporter(manager, "CSV");
FileExporter txtExporter = new FileExporter(manager, "TXT");

// Both work with same manager
csvExporter.exportToCSV("report.csv");
txtExporter.exportToTXT("report.txt");

// Manager exists independently
// Exporters come and go
```

**Composition vs Aggregation:**
| Aspect | Composition | Aggregation |
|--------|-----------|-------------|
| **Ownership** | Owner owns the object | Owner just uses object |
| **Creation** | Owner creates object | Object exists independently |
| **Deletion** | Object deleted when owner deleted | Object survives owner deletion |
| **Example in MoneyMate** | TransactionManager owns transactions | Report uses transactions |
| **Notation** | ◆ (filled diamond) | ◇ (empty diamond) |
| **Relationship** | Strong "has-a" | Weak "has-a" or "uses" |

---

### 7. MULTIPLE INHERITANCE (via Interfaces)

**Definition:** Java doesn't support multiple inheritance from classes, but implements multiple interfaces to get similar benefits.

**Why:**
- Avoid complexity of multiple class inheritance
- Clear contracts through interfaces
- Flexible design

**Example:**

```java
// Interface 1: Calculations
public interface Calculable {
    double calculateTotalBalance();
    double calculateTotalIncome();
    double calculateTotalExpense();
}

// Interface 2: Export capability
public interface Exportable {
    void exportToFile(String filePath);
    String getExportFormat();
}

// Class implementing multiple interfaces
// (Concept of multiple inheritance through interfaces)
public class ReportWithExport implements Calculable, Exportable {
    private List<Transaction> transactions;
    
    @Override
    public double calculateTotalBalance() {
        // Implementation
    }
    
    @Override
    public void exportToFile(String filePath) {
        // Implementation
    }
}

// Can be used as either interface type
Calculable calc = new ReportWithExport();
Exportable exp = new ReportWithExport();

// Get benefits of both behaviors
double balance = calc.calculateTotalBalance();
exp.exportToFile("report.csv");
```

---

## PATTERNS

### 1. SINGLETON PATTERN

**Intent:** Ensure a class has only one instance and provide a global point of access to it.

**Use Case:** Database connections should be single throughout the application.

**Implementation in MoneyMate:**

```java
public class DatabaseManager {
    // Static instance - shared across entire application
    private static DatabaseManager instance;
    
    // Private constructor - cannot instantiate directly
    private DatabaseManager() {
        initializeConnection();
    }
    
    // Synchronized method ensures thread safety
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private Connection connection;
    
    public Connection getConnection() {
        return connection;
    }
}

// Usage:
// Get the singleton instance
DatabaseManager dbManager = DatabaseManager.getInstance();
Connection conn = dbManager.getConnection();

// Next call gets same instance
DatabaseManager dbManager2 = DatabaseManager.getInstance();
assert dbManager == dbManager2;  // Same object!
```

**Benefits:**
- Only one database connection exists
- Global access point
- Thread-safe initialization
- Lazy initialization (created when first needed)

---

### 2. DATA ACCESS OBJECT (DAO) PATTERN

**Intent:** Separate data access logic from business logic by creating an abstraction layer.

**Structure:**

```
┌─────────────────────────┐
│   Business Logic        │
│  (TransactionManager)   │
└────────┬────────────────┘
         │ uses
         ▼
┌─────────────────────────┐
│   Data Access Object    │
│     (Interface)         │
└────────┬────────────────┘
         │ implemented by
         ▼
┌─────────────────────────┐
│   DAO Implementation    │
│  (TransactionDAOImpl)    │
└────────┬────────────────┘
         │ uses
         ▼
┌─────────────────────────┐
│   Database Layer        │
│ (DatabaseManager)       │
└─────────────────────────┘
```

**Implementation:**

```java
// ABSTRACTION LAYER - Interface
public interface TransactionDAO {
    void save(Transaction transaction) throws SQLException;
    void update(Transaction transaction) throws SQLException;
    void delete(String transactionId) throws SQLException;
    Transaction findById(String transactionId) throws SQLException;
    List<Transaction> findAll(String userId) throws SQLException;
}

// CONCRETE IMPLEMENTATION
public class TransactionDAOImpl implements TransactionDAO {
    
    @Override
    public void save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (" +
                    "transaction_id, user_id, transaction_type, amount, " +
                    "description, date, category, source, payment_method, " +
                    "is_recurring) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transaction.getTransactionId());
            // ... set other parameters
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<Transaction> findAll(String userId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction t = createTransactionFromResultSet(rs);
                    transactions.add(t);
                }
            }
        }
        return transactions;
    }
}

// USAGE IN BUSINESS LOGIC
public class TransactionManager implements Calculable {
    private TransactionDAO transactionDAO;  // Depends on interface, not implementation
    
    public TransactionManager() {
        this.transactionDAO = new TransactionDAOImpl();  // Could swap for different implementation
    }
    
    public void addTransaction(Transaction transaction) throws InvalidTransactionException {
        try {
            transactionDAO.save(transaction);  // Works with interface
            transactions.add(transaction);
        } catch (SQLException e) {
            throw new InvalidTransactionException("Failed to save: " + e.getMessage());
        }
    }
    
    public List<Transaction> loadTransactions(String userId) throws SQLException {
        return transactionDAO.findAll(userId);  // Works with interface
    }
}
```

**Benefits:**
- Business logic doesn't care about SQL details
- Easy to test (mock the DAO)
- Easy to switch database (create new DAO implementation)
- Follows separation of concerns

---

### 3. STRATEGY PATTERN

**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

**Use Case:** Different export formats (CSV, TXT, future: PDF, Excel)

**Implementation:**

```java
// CONTEXT - Uses different strategies
public class FileExporter implements Exportable {
    private TransactionManager manager;
    private String format;
    
    public FileExporter(TransactionManager manager, String format) {
        this.manager = manager;
        this.format = format.toUpperCase();
    }
    
    @Override
    public void exportToFile(String filePath) throws FileExportException {
        // Strategy pattern: Choose algorithm based on format
        switch (format) {
            case "CSV":
                exportToCSV(filePath);
                break;
            case "TXT":
                exportToTXT(filePath);
                break;
            case "JSON":
                exportToJSON(filePath);  // Future
                break;
            case "PDF":
                exportToPDF(filePath);   // Future
                break;
            default:
                throw new FileExportException("Unknown format: " + format);
        }
    }
    
    private void exportToCSV(String filePath) throws FileExportException {
        // CSV-specific algorithm
        try (FileWriter fw = new FileWriter(filePath);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // CSV header
            pw.println("TransactionID,Type,Amount,Description,Date,Category");
            
            // CSV rows
            for (Transaction t : manager.getTransactions()) {
                pw.printf("%s,%s,%.2f,%s,%s,%s%n",
                    t.getTransactionId(),
                    t.getTransactionType(),
                    t.getAmount(),
                    t.getDescription(),
                    t.getDate(),
                    t.getCategory());
            }
        } catch (IOException e) {
            throw new FileExportException("Failed to export CSV: " + e.getMessage());
        }
    }
    
    private void exportToTXT(String filePath) throws FileExportException {
        // TXT-specific algorithm
        try (FileWriter fw = new FileWriter(filePath);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("===== TRANSACTION REPORT =====");
            pw.printf("Generated: %s%n%n", LocalDateTime.now());
            
            // Formatted text output
            for (Transaction t : manager.getTransactions()) {
                pw.println("───────────────────────────");
                pw.printf("ID:       %s%n", t.getTransactionId());
                pw.printf("Type:     %s%n", t.getTransactionType());
                pw.printf("Amount:   Rp %.2f%n", t.getAmount());
                pw.printf("Date:     %s%n", t.getDate());
                pw.println();
            }
        } catch (IOException e) {
            throw new FileExportException("Failed to export TXT: " + e.getMessage());
        }
    }
    
    // Future strategies can be added easily
    private void exportToJSON(String filePath) throws FileExportException {
        // JSON export logic
    }
    
    private void exportToPDF(String filePath) throws FileExportException {
        // PDF export logic
    }
}

// USAGE
// Strategy can be chosen at runtime
FileExporter csvExporter = new FileExporter(manager, "CSV");
csvExporter.exportToFile("transactions.csv");  // Uses CSV strategy

FileExporter txtExporter = new FileExporter(manager, "TXT");
txtExporter.exportToFile("transactions.txt");  // Uses TXT strategy

// Future: New strategies don't require changing existing code!
// FileExporter pdfExporter = new FileExporter(manager, "PDF");
// pdfExporter.exportToFile("transactions.pdf");
```

**Benefits:**
- Algorithms can be chosen at runtime
- Easy to add new export formats
- Each format logic is isolated
- Client code remains unchanged when new formats added

---

### 4. FACTORY PATTERN

**Intent:** Create objects without specifying the exact classes to create.

**Use Case:** Creating Transaction objects from API requests or JSON

**Implementation:**

```java
// Factory method in Transaction class
public class Transaction {
    // ...
    
    public static Transaction fromString(String type, double amount, 
                                        String description, LocalDate date,
                                        String categoryName, String extra) {
        if (type.equalsIgnoreCase("income")) {
            return new Income(amount, description, date, categoryName, extra);
        } else if (type.equalsIgnoreCase("expense")) {
            boolean recurring = extra.contains("recurring");
            return new Expense(amount, description, date, categoryName, extra, recurring);
        } else {
            throw new IllegalArgumentException("Unknown transaction type: " + type);
        }
    }
}

// Use case in API Controller
@PostMapping("/transactions")
public ResponseEntity<?> addTransaction(@RequestBody Map<String, Object> request) {
    String type = (String) request.get("transactionType");
    double amount = ((Number) request.get("amount")).doubleValue();
    String description = (String) request.get("description");
    LocalDate date = LocalDate.parse((String) request.get("date"));
    String category = (String) request.get("category");
    String extra = (String) request.get("source");
    
    // Factory creates appropriate type
    Transaction transaction = Transaction.fromString(
        type, amount, description, date, category, extra
    );
    
    // Works with the transaction
    transactionManager.addTransaction(transaction);
    
    return ResponseEntity.ok("Transaction added");
}
```

**Benefits:**
- Encapsulates object creation logic
- Client doesn't know about subclasses
- Easy to add new types
- Centralized creation logic

---

## EXAMPLES

### Complete Transaction Flow

```java
// 1. CREATE Transaction (Factory Pattern)
Transaction income = new Income(
    500000.0, 
    "Monthly Salary", 
    LocalDate.of(2026, 1, 6),
    Category.GAJI, 
    "Employer"
);

// 2. VALIDATE (Encapsulation)
if (!income.isValid()) {
    throw new InvalidTransactionException("Invalid income!");
}

// 3. PERSIST (DAO Pattern + Singleton)
transactionDAO.save(income);
// DAO internally uses DatabaseManager.getInstance().getConnection()

// 4. ADD TO MANAGER (Composition)
transactionManager.addTransaction(income);
// TransactionManager adds to its owned transactions list

// 5. CALCULATE (Interface Implementation)
double newBalance = transactionManager.calculateTotalBalance();
// Implements Calculable interface

// 6. EXPORT (Strategy Pattern)
FileExporter exporter = new FileExporter(transactionManager, "CSV");
exporter.exportToFile("report.csv");
// Uses CSV strategy

// 7. GENERATE REPORT (Aggregation)
List<Transaction> monthTransactions = transactions.stream()
    .filter(t -> t.getDate().getMonth() == Month.JANUARY)
    .collect(Collectors.toList());

Report report = new Report(monthTransactions, YearMonth.now());
// Report aggregates (doesn't own) transactions

// 8. USE POLYMORPHISM
for (Transaction t : transactions) {
    System.out.println(t.getTransactionType());  // Polymorphic!
}
```

---

## SOLID

### Single Responsibility Principle
Each class has one reason to change:
- `TransactionManager`: manages transactions
- `FileExporter`: exports data
- `DatabaseManager`: manages DB connection

### Open/Closed Principle
Open for extension, closed for modification:
```java
// Can add new transaction types without changing Transaction class
public class Transfer extends Transaction {
    // New type, no changes to existing code
}

// Can add new export formats without changing FileExporter logic
case "PDF":
    exportToPDF(filePath);
```

### Liskov Substitution Principle
Subtypes can be used instead of parent:
```java
Transaction t = new Income(...);  // Income IS-A Transaction
t = new Expense(...);             // Expense IS-A Transaction
// Both work the same way

User u = new RegularUser(...);    // RegularUser IS-A User
// Works wherever User expected
```

### Interface Segregation Principle
Clients depend on specific interfaces:
```java
// Instead of one big interface, have specific ones
Calculable calc = transactionManager;
Exportable exp = fileExporter;
// Each uses only what they need
```

### Dependency Inversion Principle
Depend on abstractions, not concrete classes:
```java
// Good: Depends on interface
private TransactionDAO transactionDAO;  // Interface

// Bad: Depends on concrete class
// private TransactionDAOImpl transactionDAO;  // Concrete
```

---

## PRACTICES

### 1. Use Interfaces for Extensibility
```java
// Good - Can swap implementations
Calculable calc = new TransactionManager();
Exportable exp = new FileExporter(...);

// Bad - Tightly coupled to concrete classes
// TransactionManager calc = new TransactionManager();
```

### 2. Prefer Composition over Inheritance
```java
// Good - Composition
public class TransactionManager {
    private TransactionDAO transactionDAO;  // Composed
}

// Can be problematic if overused
// public class ExtendedTransactionManager extends TransactionManager { }
```

### 3. Keep Classes Focused
```java
// Good - Single responsibility
public class InputValidator {
    public static boolean validateAmount(double amount) { ... }
}

public class TransactionManager {
    // Only manages transactions, delegates validation
}

// Bad - Too many responsibilities
// public class TransactionManager {
//     public void validate(...) { ... }
//     public void export(...) { ... }
//     public void calculateTax(...) { ... }
// }
```

### 4. Use Meaningful Names
```java
// Good
List<Transaction> calculateTotalBalance() { ... }
Transaction findById(String id) { ... }

// Bad
// List<Transaction> calc() { ... }
// Transaction find(String id) { ... }
```

### 5. Encapsulation - Use Getters/Setters
```java
// Good - Controlled access
public double getBalance() {
    return calculateTotalBalance();  // Calculated, not stored
}

// Bad - Direct access
// public double balance;
```

---

## Summary Table

| Concept | Purpose | Implementation | Benefit |
|---------|---------|-----------------|---------|
| **Encapsulation** | Hide details | private/public access | Data integrity |
| **Inheritance** | Code reuse | extends keyword | DRY principle |
| **Polymorphism** | Multiple forms | override methods | Flexible code |
| **Abstraction** | Simplify interface | abstract/interface | Focus on what not how |
| **Composition** | Strong "has-a" | private owned object | Clear ownership |
| **Aggregation** | Weak "has-a" | reference shared object | Loose coupling |
| **Singleton** | Single instance | getInstance() | Global access |
| **DAO** | Separate concerns | Interface + Implementation | Testability |
| **Strategy** | Switchable algorithms | switch/if logic | Runtime flexibility |
| **Factory** | Create objects | static factory methods | Encapsulation |

---

This comprehensive guide shows how MoneyMate implements enterprise-level OOP principles and patterns! 🎯

