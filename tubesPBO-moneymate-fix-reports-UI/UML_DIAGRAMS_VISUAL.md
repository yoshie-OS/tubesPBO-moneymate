# MoneyMate - Visual UML Diagrams (ASCII Art)

## 1. COMPLETE CLASS DIAGRAM

```
╔════════════════════════════════════════════════════════════════════════════╗
║                      MONEYMATE OOP CLASS DIAGRAM                          ║
╚════════════════════════════════════════════════════════════════════════════╝


                         ┏━━━━━━━━━━━━━━━━━━━━━━━┓
                         ┃  <<interface>>        ┃
                         ┃    Calculable         ┃
                         ┣━━━━━━━━━━━━━━━━━━━━━━━┫
                         ┃ +calculateTotalBalance│
                         ┃ +calculateTotalIncome │
                         ┃ +calculateTotalExpense
                         ┗━━━━━━━━━━━━━━━━━━━━━━━┛
                                  △
                                  │
                                  │ implements
                                  │
              ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
              ┃   TransactionManager (CORE)    ┃
              ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
              ┃ - transactions: List            ┃
              ┃ - initialBalance: double        ┃
              ┃ - transactionDAO: DAO           ┃
              ┃ - currentUserId: String         ┃
              ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
              ┃ + add/delete/updateTransaction  ┃
              ┃ + calculate*()                  ┃
              ┃ + generateReport()              ┃
              ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
                    △                    △
                    │                    │
              creates & uses        uses & creates
                    │                    │
        ┌───────────┼───────────┐        │
        │           │           │        │
    ┌───▼───┐   ┌───▼────┐  ┌──▼─────┐
    │Report │   │DAO Impl│  │Category │
    └───────┘   └────────┘  └────────┘
        △           △
        │           │ uses
    uses &      ┌───▼──────────┐
  aggregates    │ DatabaseMgr  │
        │       │ (Singleton)  │
        │       └──────┬───────┘
        │              │
        │              ▼
        │         ┌─────────┐
        │         │ SQLite  │
        │         │ Database│
        │         └─────────┘
        │
    ┌───▼──────────────────────────────────────┐
    │         <<abstract>>                      │
    │         Transaction                      │
    ├──────────────────────────────────────────┤
    │ # transactionId: String                  │
    │ # amount: double                         │
    │ # description: String                    │
    │ # date: LocalDate                        │
    │ # category: Category                     │
    ├──────────────────────────────────────────┤
    │ + getTransactionId(): String             │
    │ + getAmount(): double                    │
    │ + getTransactionType(): String*          │
    │ + isValid(): boolean                     │
    │ - generateTransactionId(): String        │
    └───▲──────────────────────────────────────┘
        │
        │ extends (Inheritance)
        │
    ┌───┴────────────────────┬──────────────────┐
    │                        │                  │
┌───▼────────┐        ┌─────▼──────┐          │
│   Income   │        │   Expense  │      (Future: Transfer)
├────────────┤        ├────────────┤
│ - source   │        │ - payment  │
│            │        │   Method   │
├────────────┤        │ - recurring│
│ + getSource│        ├────────────┤
│            │        │ + getPayment
└────────────┘        │ + isRecurr │
                      └────────────┘


                    ┏━━━━━━━━━━━━━━━━┓
                    ┃ <<interface>>  ┃
                    ┃   Exportable   ┃
                    ┣━━━━━━━━━━━━━━━━┫
                    ┃ + exportToFile ┃
                    ┃ + getFormat    ┃
                    ┗━━━━━━━━━━━━━━━━┛
                          △
                          │
                          │ implements
                          │
        ┏━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃   FileExporter        ┃
        ┣━━━━━━━━━━━━━━━━━━━━━━━┫
        ┃ - format: String      ┃
        ┃ - manager: TxnManager ┃
        ┣━━━━━━━━━━━━━━━━━━━━━━━┫
        ┃ + exportToCSV()       ┃
        ┃ + exportToTXT()       ┃
        ┗━━━━━━━━━━━━━━━━━━━━━━━┛


                  ┌─────────────────────────┐
                  │ <<abstract>>            │
                  │ User                    │
                  ├─────────────────────────┤
                  │ # userId: String        │
                  │ # username: String      │
                  │ # email: String         │
                  │ # createdAt: DateTime   │
                  ├─────────────────────────┤
                  │ + displayUserInfo()* (abstract)
                  └──────────────┬──────────┘
                                 │
                                 │ extends
                                 │
                           ┌─────▼──────────┐
                           │ RegularUser    │
                           ├────────────────┤
                           │ - password     │
                           │ - initBalance  │
                           ├────────────────┤
                           │ + displayInfo()│
                           └────────────────┘
```

---

## 2. SEQUENCE DIAGRAM - ADDING A TRANSACTION

```
┌──────────┐         ┌──────────────┐       ┌─────────────────┐       ┌────────────────┐       ┌────────┐
│Frontend  │         │  API Layer   │       │TransactionManager│       │  TransactionDAO│       │Database│
└──────────┘         └──────────────┘       └─────────────────┘       └────────────────┘       └────────┘
     │                     │                       │                          │                      │
     │  POST /transactions │                       │                          │                      │
     ├────────────────────>│                       │                          │                      │
     │                     │                       │                          │                      │
     │                     │  validate input       │                          │                      │
     │                     │ (InputValidator)      │                          │                      │
     │                     ├─────────────┐         │                          │                      │
     │                     │<────────────┤         │                          │                      │
     │                     │             │         │                          │                      │
     │                     │  addTransaction()     │                          │                      │
     │                     ├────────────────────>│                          │                      │
     │                     │                     │  validate transaction     │                      │
     │                     │                     ├──────┐                    │                      │
     │                     │                     │<─────┤                    │                      │
     │                     │                     │      │                    │                      │
     │                     │                     │  transactionDAO.save()    │                      │
     │                     │                     ├───────────────────────>│                      │
     │                     │                     │                          │  INSERT INTO DB      │
     │                     │                     │                          ├──────────────────>│
     │                     │                     │                          │<──────────────────┤
     │                     │                     │                          │  SUCCESS           │
     │                     │                     │<───────────────────────┤                      │
     │                     │                     │                          │                      │
     │                     │                     ├─ calculate balance       │                      │
     │                     │                     ├─ update in-memory list   │                      │
     │                     │                     │                          │                      │
     │                     │  ✓ Success (JSON)   │                          │                      │
     │                     │<────────────────────┤                          │                      │
     │                     │                     │                          │                      │
     │  ✓ Display updated  │                     │                          │                      │
     │<────────────────────┤                     │                          │                      │
     │   balance & tables  │                     │                          │                      │
     │                     │                     │                          │                      │
```

---

## 3. DEPENDENCY INJECTION FLOW

```
                    ┌─────────────────────────┐
                    │ DatabaseManager         │
                    │ (Singleton)             │
                    └────────────┬────────────┘
                                 │
                                 │ provides
                                 │ Connection
                                 │
                    ┌────────────▼────────────┐
                    │ TransactionDAOImpl       │
                    ├─────────────────────────┤
                    │ Uses Connection from DM │
                    └────────────┬────────────┘
                                 │
                                 │ injected into
                                 │
        ┌────────────────────────▼────────────────────────┐
        │ TransactionManager                             │
        ├────────────────────────────────────────────────┤
        │ Contains: TransactionDAO dao                   │
        │ Interacts with: Database through DAO           │
        │ Doesn't know concrete implementation details   │
        └────────────────────────────────────────────────┘
                                 │
                                 │ uses
                                 │
                    ┌────────────▼────────────┐
                    │ REST Controller         │
                    ├─────────────────────────┤
                    │ Delegates to Manager    │
                    └─────────────────────────┘
```

---

## 4. OBJECT COMPOSITION TREE

```
┌─────────────────────────────┐
│   TransactionManager        │
├─────────────────────────────┤
│                             │
│  ┌───────────────────────┐  │
│  │ List<Transaction>     │  │ ◇─── Composition (owns)
│  │                       │  │
│  │  ┌──────────────────┐ │  │
│  │  │ Transaction #1   │ │  │
│  │  ├──────────────────┤ │  │
│  │  │ - amount: 500000 │ │  │
│  │  │ - category: ────┼┼─┼──┼──────► Category (Enum)
│  │  │ - date: 6/1/26  │ │  │
│  │  └──────────────────┘ │  │
│  │                       │  │
│  │  ┌──────────────────┐ │  │
│  │  │ Transaction #2   │ │  │
│  │  ├──────────────────┤ │  │
│  │  │ - amount: 100000 │ │  │
│  │  │ - category: ────┼┼─┼──┼──────► Category (Enum)
│  │  │ - date: 5/1/26  │ │  │
│  │  └──────────────────┘ │  │
│  │                       │  │
│  │  ... (more)           │  │
│  └───────────────────────┘  │
│                             │
│  ┌───────────────────────┐  │
│  │ TransactionDAO        │  │ ◇─── Composition (owns interface)
│  │ (Concrete: DAOImpl)    │  │
│  │  - connection ref     │  │
│  └───────────────────────┘  │
│                             │
│  ┌───────────────────────┐  │
│  │ initialBalance: 1M    │  │ (primitive)
│  └───────────────────────┘  │
│                             │
│  ┌───────────────────────┐  │
│  │ currentUserId: String │  │ (primitive)
│  └───────────────────────┘  │
│                             │
└─────────────────────────────┘


┌─────────────────────────────┐
│   Report                    │
├─────────────────────────────┤
│                             │
│  ┌───────────────────────┐  │
│  │ List<Transaction>     │  │ ◇─── Aggregation (references)
│  │ (Filtered from above) │  │
│  └───────────────────────┘  │
│                             │
│  ┌───────────────────────┐  │
│  │ reportPeriod: 2026-01 │  │ (primitive)
│  └───────────────────────┘  │
│                             │
│  ┌───────────────────────┐  │
│  │ initialBalance: 1M    │  │ (primitive)
│  └───────────────────────┘  │
│                             │
└─────────────────────────────┘
```

---

## 5. INHERITANCE HIERARCHY COMPLETE TREE

```
java.lang.Object
    │
    ├─► User (abstract)
    │       │
    │       └─► RegularUser (concrete)
    │           ├─ userId: String
    │           ├─ username: String
    │           ├─ email: String
    │           ├─ password: String
    │           └─ initialBalance: double
    │
    ├─► Transaction (abstract)
    │   │   ├─ transactionId: String
    │   │   ├─ amount: double
    │   │   ├─ description: String
    │   │   ├─ date: LocalDate
    │   │   └─ category: Category
    │   │
    │   ├─► Income (concrete)
    │   │   └─ source: String
    │   │
    │   ├─► Expense (concrete)
    │   │   ├─ paymentMethod: String
    │   │   └─ isRecurring: boolean
    │   │
    │   └─► (Future Types)
    │       ├─ Transfer
    │       └─ Investment
    │
    ├─► Report (concrete)
    │   ├─ transactions: List
    │   ├─ reportPeriod: YearMonth
    │   └─ initialBalance: double
    │
    ├─► Category (Enum)
    │   ├─ SALARY, BONUS, ... (Income)
    │   └─ FOOD, TRANSPORT, ... (Expense)
    │
    ├─► Exceptions (all extending Exception)
    │   ├─ FileExportException
    │   ├─ InsufficientBalanceException
    │   ├─ InvalidTransactionException
    │   └─ TransactionNotFoundException
    │
    └─► Other Classes
        ├─ TransactionManager
        ├─ FileExporter
        ├─ DatabaseManager (Singleton)
        ├─ TransactionDAOImpl
        ├─ DateUtil
        ├─ InputValidator
        └─ REST Controllers
```

---

## 6. POLYMORPHISM IN ACTION

```
┌─────────────────────────────────────────────────────────────┐
│  Polymorphic Behavior Example                              │
└─────────────────────────────────────────────────────────────┘

    List<Transaction> transactions = new ArrayList<>();
    
    transactions.add(new Income(100000, "Gaji", date, "Gaji"));
    transactions.add(new Expense(50000, "Makan", date, "Makanan", "Cash", false));
    transactions.add(new Income(500000, "Bonus", date, "Bonus"));
    transactions.add(new Expense(1000000, "Sewa", date, "Sewa", "Transfer", true));

    ↓ Same code, different behavior

    for (Transaction t : transactions) {
        
        ┌─ Polymorphic Method Call
        │
        System.out.println(t.getTransactionType());
        │
        ├─► Income #1: returns "PEMASUKAN"
        ├─► Expense #1: returns "PENGELUARAN"
        ├─► Income #2: returns "PEMASUKAN"
        └─► Expense #2: returns "PENGELUARAN"

        ┌─ Same for getAmount()
        │
        │  t.getAmount()
        │
        ├─► Income #1: returns 100000.0
        ├─► Expense #1: returns 50000.0
        ├─► Income #2: returns 500000.0
        └─► Expense #2: returns 1000000.0
    }


┌─ RESULT: Polymorphism allows treating different types uniformly!
└─► No need for instanceof checks in most cases
    Can iterate through mixed collections easily
    New transaction types can be added without changing loop code
```

---

## 7. LAYER ARCHITECTURE DIAGRAM

```
╔════════════════════════════════════════════════════════════════════════════╗
║                           PRESENTATION LAYER                              ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  ┌──────────────────────────────────────────────────────────────────────┐ ║
║  │ Frontend (TypeScript/HTML/CSS)                                      │ ║
║  │ - MoneyMateApp class                                                │ ║
║  │ - HTML templates (dashboard, transactions, reports)                │ ║
║  │ - User interface components                                         │ ║
║  └────────────────────┬─────────────────────────────────────────────────┘ ║
║                       │                                                    ║
║                       │ HTTP/REST (JSON)                                  ║
║                       │                                                    ║
║  ┌────────────────────▼─────────────────────────────────────────────────┐ ║
║  │ REST Controllers (Spring Boot)                                       │ ║
║  │ ├─ TransactionController                                            │ ║
║  │ │  ├─ @PostMapping /transactions                                   │ ║
║  │ │  ├─ @GetMapping /transactions                                    │ ║
║  │ │  ├─ @DeleteMapping /transactions/{id}                          │ ║
║  │ │  └─ @GetMapping /reports                                        │ ║
║  │ │                                                                  │ ║
║  │ └─ UserController                                                  │ ║
║  │    ├─ @PostMapping /register                                      │ ║
║  │    └─ @PostMapping /login                                         │ ║
║  │                                                                    │ ║
║  └────────────────────┬────────────────────────────────────────────────┘ ║
║                       │                                                    ║
╚═══════════════════════╪════════════════════════════════════════════════════╝
                        │
╔═══════════════════════╪════════════════════════════════════════════════════╗
║                       │         BUSINESS LOGIC LAYER                       ║
╠═══════════════════════╪════════════════════════════════════════════════════╣
║                       │                                                    ║
║                       ▼                                                    ║
║  ┌──────────────────────────────────────────────────────────────────────┐ ║
║  │ TransactionManager (implements Calculable)                          │ ║
║  │ - Core business logic                                              │ ║
║  │ - Transaction management (add/update/delete)                       │ ║
║  │ - Balance calculations                                             │ ║
║  │ - Report generation                                                │ ║
║  │                                                                    │ ║
║  │ Methods:                                                           │ ║
║  │ ├─ addTransaction()        ┐                                      │ ║
║  │ ├─ deleteTransaction()     ├─ Calculable Interface               │ ║
║  │ ├─ updateTransaction()     │                                      │ ║
║  │ ├─ calculateTotalBalance() ┘                                      │ ║
║  │ ├─ calculateTotalIncome()                                         │ ║
║  │ ├─ calculateTotalExpense()                                        │ ║
║  │ └─ generateReport()                                               │ ║
║  │                                                                    │ ║
║  └────┬──────────────────────────────────────────────────────────┬───┘ ║
║       │                                                          │      ║
║       ▼                                                          ▼      ║
║  ┌─────────────────┐  ┌──────────────┐  ┌──────────────────────────┐  ║
║  │ FileExporter    │  │ Report       │  │ Utility Classes          │  ║
║  │ (Exportable)    │  │ Generator    │  │ ├─ InputValidator        │  ║
║  │                 │  │              │  │ ├─ DateUtil              │  ║
║  │ ├─ exportToCSV()│  │ ├─ generate  │  │ └─ Category (Enum)       │  ║
║  │ ├─ exportToTXT()│  │ │   Summary()│  │                         │  ║
║  │ └─ exportToFile│  │ ├─ get       │  │                         │  ║
║  │                 │  │ │   Breakdown│  │                         │  ║
║  │                 │  │ └─ getBalance  │                         │  ║
║  └─────────────────┘  └──────────────┘  └──────────────────────────┘  ║
║                                                                        ║
║  ┌──────────────────────────────────────────────────────────────────┐ ║
║  │ Data Models                                                      │ ║
║  │ ├─ Transaction (abstract) → Income, Expense                     │ ║
║  │ ├─ User (abstract) → RegularUser                                │ ║
║  │ └─ Report, Category, etc.                                       │ ║
║  │                                                                  │ ║
║  └──────────────────┬─────────────────────────────────────────────┘ ║
║                     │                                                  ║
╚═════════════════════╪══════════════════════════════════════════════════╝
                      │
╔═════════════════════╪══════════════════════════════════════════════════╗
║                     │      DATA ACCESS LAYER (DAO Pattern)             ║
╠═════════════════════╪══════════════════════════════════════════════════╣
║                     │                                                  ║
║                     ▼                                                  ║
║  ┌──────────────────────────────────────────────────────────────────┐ ║
║  │ TransactionDAO (Interface)                                       │ ║
║  │ +save(t: Transaction): void                                      │ ║
║  │ +update(t: Transaction): void                                    │ ║
║  │ +delete(id: String): void                                        │ ║
║  │ +findById(id: String): Transaction                               │ ║
║  │ +findAll(userId: String): List<Transaction>                      │ ║
║  │                                                                  │ ║
║  └────────────────────┬─────────────────────────────────────────────┘ ║
║                       │                                                ║
║                       ▼                                                ║
║  ┌──────────────────────────────────────────────────────────────────┐ ║
║  │ TransactionDAOImpl (Concrete Implementation)                      │ ║
║  │ - Maps ResultSet to Transaction objects                          │ ║
║  │ - Executes SQL queries                                           │ ║
║  │ - Handles data transformation                                    │ ║
║  │                                                                  │ ║
║  └────────────────────┬─────────────────────────────────────────────┘ ║
║                       │                                                ║
║                       ▼                                                ║
║  ┌──────────────────────────────────────────────────────────────────┐ ║
║  │ DatabaseManager (Singleton)                                      │ ║
║  │ - getInstance(): DatabaseManager (static)                        │ ║
║  │ - getConnection(): Connection                                    │ ║
║  │ - initializeTables(): void                                       │ ║
║  │ - Manages single SQLite connection                               │ ║
║  │ - Thread-safe connection access                                  │ ║
║  │                                                                  │ ║
║  └────────────────────┬─────────────────────────────────────────────┘ ║
║                       │                                                ║
╚═══════════════════════╪════════════════════════════════════════════════╝
                        │
╔═══════════════════════╪════════════════════════════════════════════════╗
║                       │           DATABASE LAYER                        ║
╠═══════════════════════╪════════════════════════════════════════════════╣
║                       │                                                ║
║                       ▼                                                ║
║  ┌──────────────────────────────────────────────────────────────────┐ ║
║  │ SQLite Database (moneymate.db)                                   │ ║
║  │                                                                  │ ║
║  │ Tables:                                                          │ ║
║  │  users                    transactions                           │ ║
║  │  ┌──────────────────┐    ┌──────────────────────────────┐      │ ║
║  │  │ user_id (PK)     │    │ transaction_id (PK)          │      │ ║
║  │  │ username         │    │ user_id (FK)                 │      │ ║
║  │  │ email            │    │ transaction_type             │      │ ║
║  │  │ password         │    │ amount                       │      │ ║
║  │  │ initial_balance  │    │ description                  │      │ ║
║  │  │ created_at       │    │ date                         │      │ ║
║  │  └──────────────────┘    │ category                     │      │ ║
║  │                         │ source/payment_method        │      │ ║
║  │                         │ is_recurring                 │      │ ║
║  │                         │ created_at                   │      │ ║
║  │                         └──────────────────────────────┘      │ ║
║  │                                                                  │ ║
║  │ Indices:                                                         │ ║
║  │ - user_id (for fast user queries)                               │ ║
║  │ - date (for range queries)                                      │ ║
║  │                                                                  │ ║
║  └──────────────────────────────────────────────────────────────────┘ ║
║                                                                        ║
╚════════════════════════════════════════════════════════════════════════╝
```

---

## 8. INTERFACE REALIZATION DIAGRAM

```
┌────────────────────────────────────────────────────────────┐
│          Interface: Calculable                             │
├────────────────────────────────────────────────────────────┤
│ calculateTotalBalance(): double                            │
│ calculateTotalIncome(): double                             │
│ calculateTotalExpense(): double                            │
│ getTransactions(): List<Transaction>                       │
└──────┬─────────────────────────────────────────────────────┘
       │
       │ <<realizes>>
       │
┌──────▼──────────────────────────────────────────────────────┐
│       TransactionManager                                    │
├───────────────────────────────────────────────────────────┬┤
│ Realizes Calculable by implementing all methods:          ││
│                                                            ││
│ public double calculateTotalBalance() {                   ││
│     return initialBalance +                               ││
│            calculateTotalIncome() -                        ││
│            calculateTotalExpense();                        ││
│ }                                                          ││
│                                                            ││
│ public double calculateTotalIncome() {                    ││
│     return transactions.stream()                          ││
│         .filter(t -> t instanceof Income)                 ││
│         .mapToDouble(Transaction::getAmount)              ││
│         .sum();                                            ││
│ }                                                          ││
│                                                            ││
│ public double calculateTotalExpense() {                   ││
│     return transactions.stream()                          ││
│         .filter(t -> t instanceof Expense)                ││
│         .mapToDouble(Transaction::getAmount)              ││
│         .sum();                                            ││
│ }                                                          ││
└────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────┐
│          Interface: Exportable                             │
├────────────────────────────────────────────────────────────┤
│ exportToFile(filePath: String): void                       │
│ getExportFormat(): String                                  │
└──────┬─────────────────────────────────────────────────────┘
       │
       │ <<realizes>>
       │
┌──────▼──────────────────────────────────────────────────────┐
│       FileExporter                                          │
├───────────────────────────────────────────────────────────┬┤
│ Realizes Exportable by implementing:                      ││
│                                                            ││
│ public void exportToFile(String filePath) {              ││
│     switch(format) {                                      ││
│         case "CSV":                                       ││
│             exportToCSV(filePath);                        ││
│             break;                                        ││
│         case "TXT":                                       ││
│             exportToTXT(filePath);                        ││
│             break;                                        ││
│     }                                                      ││
│ }                                                          ││
│                                                            ││
│ public String getExportFormat() {                        ││
│     return format;                                        ││
│ }                                                          ││
└────────────────────────────────────────────────────────────┘
```

---

## 9. STEREOTYPE DIAGRAM

```
┌─────────────────────────┐
│ <<application>>         │
│ MoneyMateApplication    │
│ (Spring Boot App)       │
└────────────┬────────────┘
             │
             ├─────────────────┬──────────────────────┐
             │                 │                      │
             ▼                 ▼                      ▼
    ┌────────────────┐  ┌──────────────┐  ┌────────────────┐
    │<<controller>>  │  │<<service>>   │  │<<component>>   │
    │TransactionCtrl │  │TransactionMgr│  │FileExporter    │
    └────────────────┘  └──────────────┘  └────────────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
                ▼              ▼              ▼
         ┌──────────────┐ ┌────────────┐ ┌──────────────┐
         │<<entity>>    │ │<<interface│ │<<dataaccess>>│
         │Transaction  │ │Calculable │ │TransactionDAO│
         └──────────────┘ └────────────┘ └──────────────┘
```

---

## 10. MULTIPLICITY & CARDINALITY

```
TransactionManager ──── 1..1
                            │
                         has-many
                            │
                         0..*
                    ┌───────────────────┐
                    │ Transaction       │
                    └───────────────────┘
                            △
                            │ is-a
                         1..1
                        ┌──┴──┐
                        │     │
                    ┌───▼──┐ ┌─▼────┐
                    │Income│ │Expns │
                    └──────┘ └──────┘


TransactionManager ──── 1..1
                            │
                      manages
                            │
                         1..1
                    ┌──────────────┐
                    │TransactionDAO│
                    └──────────────┘


FileExporter ────────── 1..1
                            │
                      uses/aggregates
                            │
                         1..1
                    ┌──────────────────────┐
                    │TransactionManager    │
                    └──────────────────────┘
                            │
                         owns-many
                            │
                         0..*
                    ┌──────────────┐
                    │Transaction   │
                    └──────────────┘


Report ──────────────── 1..1
                            │
                      aggregates
                            │
                         0..*
                    ┌──────────────┐
                    │Transaction   │
                    └──────────────┘
```

---

## 11. STATE DIAGRAM - TRANSACTION LIFECYCLE

```
                    ┌──────────────┐
                    │  Creation    │
                    │              │
                    └──────┬───────┘
                           │
                    new Income() or
                    new Expense()
                           │
                           ▼
                    ┌──────────────┐
                    │  In Memory   │ ◄─┐
                    │ (Valid)      │  │
                    │              │  │
                    └──────┬───────┘  │
                           │         Update
                    add to            Transaction
                    manager.    │  │
                    add()       └──┘
                           │
                           ▼
                    ┌──────────────┐
                    │  Persisted   │ ◄─┐
                    │  in Database │  │
                    │              │  │ Can be
                    └──────┬───────┘  │ retrieved
                           │         from DB
                           │         │
        ┌──────────────────┼─────────┘
        │                  │
        │            Retrieved on
        │            next login
        │                  │
        │                  ▼
        │           ┌──────────────┐
        │           │ In Memory    │
        │           │ (Reloaded)   │
        │           │              │
        │           └──────┬───────┘
        │                  │
        │           On delete() or
        │           manager.delete()
        │                  │
        ▼                  ▼
┌──────────────┐    ┌──────────────┐
│   Deleted    │    │  Deleted     │
│  from DB     │◄───│  from Memory │
│              │    │              │
└──────────────┘    └──────────────┘
```

---

This comprehensive UML documentation provides complete visual representation of the MoneyMate project's architecture!

