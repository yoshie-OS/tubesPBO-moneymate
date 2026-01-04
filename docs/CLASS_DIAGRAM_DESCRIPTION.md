# MoneyMate - Class Diagram Description

## Diagram Overview

MoneyMate menggunakan arsitektur berbasis OOP dengan package structure yang jelas:

```
┌─────────────────────────────────────────────────────────┐
│                    Package Structure                    │
├─────────────────────────────────────────────────────────┤
│  moneymate.model       - Domain models & entities       │
│  moneymate.controller  - Business logic                 │
│  moneymate.view        - User interface                 │
│  moneymate.exception   - Custom exceptions              │
│  moneymate.interfaces  - Contracts/Interfaces           │
│  moneymate.util        - Utility classes                │
└─────────────────────────────────────────────────────────┘
```

## Class Relationships

### 1. INHERITANCE HIERARCHY

```
Transaction (Abstract)
    |
    ├── Income
    |     - source: String
    |     - Additional fields untuk pemasukan
    |
    └── Expense
          - paymentMethod: String
          - isRecurring: boolean
          - Additional fields untuk pengeluaran

User (Abstract)
    |
    └── RegularUser
          - password: String
          - initialBalance: double
          - Concrete implementation
```

### 2. INTERFACE IMPLEMENTATION

```
<<interface>> Calculable
    |
    └── TransactionManager implements Calculable
          - Menghitung total income, expense, balance
          - Manage list of transactions

<<interface>> Exportable
    |
    └── FileExporter implements Exportable
          - Export to CSV
          - Export to TXT
```

### 3. ASSOCIATIONS

```
MoneyMateApp
    |
    ├── uses → TransactionManager
    |            |
    |            └── manages → List<Transaction>
    |                              |
    |                              ├── Income instances
    |                              └── Expense instances
    |
    └── uses → RegularUser (currentUser)

TransactionManager
    |
    └── creates → Report
                    |
                    └── analyzes → List<Transaction>

Transaction
    |
    └── has → Category (enum)
                |
                ├── Income categories (GAJI, BONUS, ...)
                └── Expense categories (MAKANAN, TRANSPORTASI, ...)
```

### 4. UTILITY CLASSES

```
FileExporter
    └── uses → TransactionManager (untuk get data)

DateUtil
    └── provides static methods untuk date operations

InputValidator
    └── provides static methods untuk input validation
```

## Key Design Patterns

### 1. Template Method Pattern
- Abstract class `Transaction` mendefinisikan template
- Subclass `Income` dan `Expense` implement specific behavior

### 2. Strategy Pattern  
- Interface `Calculable` mendefinisikan calculation strategy
- `TransactionManager` implement strategy tersebut

### 3. Facade Pattern
- `MoneyMateApp` sebagai facade untuk user interaction
- Menyederhanakan kompleksitas dari berbagai components

## UML Notation

```
┌─────────────────────┐
│    ClassName        │  - Class box
├─────────────────────┤
│ - attribute: type   │  - Private attribute
│ # attribute: type   │  # Protected attribute  
│ + attribute: type   │  + Public attribute
├─────────────────────┤
│ + method(): type    │  - Methods
└─────────────────────┘

△       - Inheritance (extends)
◁- - -  - Interface implementation
◆       - Composition
◇       - Aggregation
───>    - Association/Dependency
```

## Exception Hierarchy

```
Exception
    |
    ├── InvalidTransactionException
    |     - When transaction data is invalid
    |
    ├── InsufficientBalanceException
    |     - When balance < expense amount
    |     - Stores: currentBalance, requestedAmount
    |
    ├── TransactionNotFoundException
    |     - When transaction ID not found
    |     - Stores: transactionId
    |
    └── FileExportException
          - When file export fails
```

## Data Flow

```
User Input
    ↓
MoneyMateApp (View Layer)
    ↓
TransactionManager (Controller Layer)
    ↓
Transaction/Income/Expense (Model Layer)
    ↓
Validation (InputValidator, Transaction.isValid())
    ↓
Storage (List<Transaction>)
    ↓
Processing (Calculable methods)
    ↓
Output (Display, Report, FileExport)
```

## Key Methods by Class

### Transaction (Abstract)
- `+ getTransactionType(): String` {abstract}
- `+ isValid(): boolean` {abstract}
- `+ toString(): String`

### Income extends Transaction
- `+ getTransactionType(): String` → returns "PEMASUKAN"
- `+ isValid(): boolean` → validates income data
- `- source: String`

### Expense extends Transaction
- `+ getTransactionType(): String` → returns "PENGELUARAN"
- `+ isValid(): boolean` → validates expense data
- `- paymentMethod: String`
- `- isRecurring: boolean`

### TransactionManager implements Calculable
- `+ addTransaction(Transaction): void`
- `+ deleteTransaction(String): void`
- `+ updateTransaction(String, Transaction): void`
- `+ findTransactionById(String): Transaction`
- `+ calculateTotalBalance(): double`
- `+ calculateTotalIncome(): double`
- `+ calculateTotalExpense(): double`
- `+ generateMonthlyReport(YearMonth): Report`

### Report
- `+ getTotalIncome(): double`
- `+ getTotalExpense(): double`
- `+ getBalance(): double`
- `+ getExpenseByCategory(): Map<Category, Double>`
- `+ getIncomeByCategory(): Map<Category, Double>`
- `+ generateSummary(): String`

### FileExporter implements Exportable
- `+ exportToFile(String): void`
- `- exportToCSV(String): void`
- `- exportToTXT(String): void`
- `+ getExportFormat(): String`

## Notes

1. **Encapsulation**: Semua attributes menggunakan private/protected dengan getter/setter
2. **Polymorphism**: Method overriding di Income/Expense, interface implementation
3. **Abstraction**: Abstract classes (Transaction, User) dan Interfaces (Calculable, Exportable)
4. **Inheritance**: Clear parent-child relationships
5. **Exception Handling**: Custom exceptions untuk specific error cases

---

Untuk visualisasi grafis yang lebih detail, gunakan tools seperti:
- Visual Paradigm
- draw.io
- PlantUML
- Lucidchart

---
