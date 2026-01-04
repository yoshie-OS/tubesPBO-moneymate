# 💰 MoneyMate - Personal Finance Manager

**Aplikasi manajemen keuangan pribadi berbasis Java dengan TypeScript frontend, Spring Boot backend, dan SQLite database.**

---

## 📦 Prerequisites (Install Dulu!)

### **Windows:**
Install dependencies berikut:
1. **Java 17+** - [Download JDK](https://www.oracle.com/java/technologies/downloads/)
2. **Maven** - [Download Maven](https://maven.apache.org/download.cgi)
3. **Node.js + npm** - [Download Node.js](https://nodejs.org/)
4. **Python 3** (for the lightweight static server on port 3000)

Verify installation:
```bash
java -version    # Should show version 17+
mvn -version     # Should show Maven version
node -version    # Should show Node version
python --version # Should show Python 3.x
```

### **Linux (Fedora/RHEL):**
```bash
sudo dnf install java-17-openjdk-devel maven nodejs npm
```

### **Linux (Ubuntu/Debian):**
```bash
sudo apt install openjdk-17-jdk maven nodejs npm
```

---

## 🚀 Cara Menjalankan

### **Windows:**
1. Double-click `START.bat`
2. Script will: install/build frontend, build backend, start Python static server on http://localhost:3000, then start Spring Boot backend on http://localhost:8080
3. Buka browser: http://localhost:3000 (frontend) — API tersedia di http://localhost:8080/api

### **Linux/Mac:**
```bash
./START.sh
```
3. Script akan menjalankan python3 -m http.server 3000 (frontend) lalu backend Spring Boot di 8080.
4. Buka browser: http://localhost:3000 (frontend) — API: http://localhost:8080/api

> **First run will take 1-2 minutes** (downloading dependencies)

---

## 🗂️ Struktur Database

Database SQLite (`moneymate.db`) akan otomatis dibuat saat pertama kali dijalankan dengan struktur:

**Tabel `users`:**
- `user_id` (TEXT, PRIMARY KEY)
- `username` (TEXT)
- `email` (TEXT)
- `initial_balance` (REAL)

**Tabel `transactions`:**
- `transaction_id` (TEXT, PRIMARY KEY)
- `transaction_type` (TEXT: PEMASUKAN/PENGELUARAN)
- `amount` (REAL)
- `description` (TEXT)
- `date` (TEXT)
- `category` (TEXT) - **Free-text input, bukan enum**
- `source` (TEXT) - Untuk income
- `payment_method` (TEXT) - Untuk expense
- `is_recurring` (INTEGER) - 0/1 untuk expense berulang

### Melihat Isi Database
```bash
./show-database.sh
```

### Database untuk Group Project
Database `moneymate.db` **DISERTAKAN** dalam repository dengan data awal kosong. Saat clone pertama kali, database sudah siap digunakan. Data yang ditambahkan akan tersimpan secara lokal di komputer masing-masing anggota.

---

## 🎯 Konsep OOP yang Diimplementasikan

### 1. **Inheritance** (Pewarisan)
- `Transaction` (abstract) → `Income` & `Expense`
  - [Transaction.java](src/moneymate/model/Transaction.java)
  - [Income.java](src/moneymate/model/Income.java)
  - [Expense.java](src/moneymate/model/Expense.java)

- `User` (abstract) → `RegularUser`
  - [User.java](src/moneymate/model/User.java)
  - [RegularUser.java](src/moneymate/model/RegularUser.java)

**Contoh di kode:**
```java
public abstract class Transaction {
    private double amount;
    private String description;
    // ...
}

public class Income extends Transaction {
    private String source; // Atribut spesifik Income
}

public class Expense extends Transaction {
    private String paymentMethod; // Atribut spesifik Expense
}
```

---

### 2. **Abstract Class**
- `Transaction` dengan abstract methods: `getTransactionType()`, `isValid()`
- `User` dengan abstract method: `getUserType()`

**Contoh:**
```java
public abstract class Transaction {
    public abstract String getTransactionType();
    public abstract boolean isValid();
}
```

---

### 3. **Interface**
- `Calculable` → implemented by `TransactionManager`
  - [Calculable.java](src/moneymate/interfaces/Calculable.java)

- `Exportable` → implemented by `FileExporter`
  - [Exportable.java](src/moneymate/interfaces/Exportable.java)

- `TransactionDAO` → implemented by `TransactionDAOImpl`
  - [TransactionDAO.java](src/moneymate/database/TransactionDAO.java)
  - [TransactionDAOImpl.java](src/moneymate/database/TransactionDAOImpl.java)

**Contoh:**
```java
public interface Calculable {
    double calculateTotalBalance();
    double calculateTotalIncome();
    double calculateTotalExpense();
}

public class TransactionManager implements Calculable {
    @Override
    public double calculateTotalBalance() { /* ... */ }
}
```

---

### 4. **Polymorphism**
**Runtime Polymorphism** di `TransactionManager`:
```java
// Satu method untuk handle Income dan Expense berbeda
public void addTransaction(Transaction transaction) {
    if (transaction.getTransactionType().equals("PEMASUKAN")) {
        // Logic untuk Income
    } else {
        // Logic untuk Expense
    }
}
```

**Method Overloading** di `Income` dan `Expense`:
```java
// Constructor overloading
public Income(double amount, String description, LocalDate date, String category) { }
public Income(double amount, String description, LocalDate date, String category, String source) { }
```

---

### 5. **Encapsulation**
Semua atribut `private` dengan getter/setter:
```java
public class Transaction {
    private double amount;          // Private
    private String description;     // Private

    public double getAmount() { return amount; }         // Public getter
    public void setAmount(double amount) { this.amount = amount; } // Public setter
}
```

---

### 6. **Design Patterns**

#### **Singleton Pattern**
`DatabaseManager` - Hanya satu instance koneksi database
- [DatabaseManager.java:41](src/moneymate/database/DatabaseManager.java#L41)

```java
public class DatabaseManager {
    private static DatabaseManager instance;

    private DatabaseManager() { /* private constructor */ }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
```

#### **DAO Pattern**
`TransactionDAO` interface + `TransactionDAOImpl`
- Abstraksi database operations
- Mudah switch ke database lain (MySQL, PostgreSQL)

```java
public interface TransactionDAO {
    void save(Transaction t);
    List<Transaction> findAll();
}

public class TransactionDAOImpl implements TransactionDAO {
    @Override
    public void save(Transaction t) {
        // SQLite implementation
    }
}
```

#### **Factory Pattern**
`TransactionDAOImpl.createTransactionFromResultSet()` - [Line 194](src/moneymate/database/TransactionDAOImpl.java#L194)

```java
private Transaction createTransactionFromResultSet(ResultSet rs) {
    if (type.equals("PEMASUKAN")) {
        return new Income(...);  // Factory creates Income
    } else {
        return new Expense(...); // Factory creates Expense
    }
}
```

---

### 7. **Exception Handling (Custom Exception)**
- `InvalidTransactionException` - [InvalidTransactionException.java](src/moneymate/exception/InvalidTransactionException.java)
- `InsufficientBalanceException` - [InsufficientBalanceException.java](src/moneymate/exception/InsufficientBalanceException.java)
- `TransactionNotFoundException` - [TransactionNotFoundException.java](src/moneymate/exception/TransactionNotFoundException.java)

```java
public class InvalidTransactionException extends Exception {
    public InvalidTransactionException(String message) {
        super(message);
    }
}

// Usage:
if (amount <= 0) {
    throw new InvalidTransactionException("Amount must be positive!");
}
```

---

## 🏗️ Arsitektur Aplikasi

```
MoneyMate/
├── Frontend (TypeScript)
│   ├── src/app.ts          - Main TypeScript logic
│   ├── index.html          - UI
│   └── styles.css          - Styling
│
├── Backend (Spring Boot)
│   ├── api/                - REST Controllers
│   │   ├── TransactionController.java
│   │   └── UserController.java
│   │
│   ├── model/              - Domain Models
│   │   ├── Transaction.java (abstract)
│   │   ├── Income.java
│   │   ├── Expense.java
│   │   └── Report.java
│   │
│   ├── controller/         - Business Logic
│   │   └── TransactionManager.java
│   │
│   ├── database/           - Data Access Layer
│   │   ├── DatabaseManager.java (Singleton)
│   │   ├── TransactionDAO.java (Interface)
│   │   └── TransactionDAOImpl.java
│   │
│   ├── exception/          - Custom Exceptions
│   └── util/               - Utilities
│
└── moneymate.db           - SQLite Database
```

---

## 🔌 REST API Endpoints

### Transactions
- `GET /api/transactions` - Get all transactions
- `POST /api/transactions` - Add new transaction
- `DELETE /api/transactions/{id}` - Delete transaction
- `GET /api/transactions/type/{type}` - Filter by type (income/expense)

### Balance
- `GET /api/balance` - Get balance summary

### Reports
- `GET /api/report/{month}` - Get monthly report (format: YYYY-MM)

### User
- `POST /api/init` - Initialize user & initial balance

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "type": "income",
    "amount": 5000000,
    "description": "Gaji Bulanan",
    "date": "2025-12-29",
    "category": "Gaji",
    "source": "PT. ABC"
  }'
```

---

## 📁 File Structure

```
Tubes_PBO/
├── src/moneymate/              # Source code Java
│   ├── model/                  # Domain models
│   ├── controller/             # Business logic
│   ├── database/               # Database layer
│   ├── api/                    # REST controllers
│   ├── exception/              # Custom exceptions
│   └── util/                   # Utilities
│
├── frontend/                   # TypeScript frontend
│   ├── src/app.ts
│   ├── index.html
│   └── styles.css
│
├── lib/                        # External JARs (SQLite JDBC)
├── exports/                    # Export folder untuk CSV
├── moneymate.db               # SQLite database
├── START.sh / START.bat       # Main entry point
├── show-database.sh           # View database tool
└── README.md                  # This file
```

---

## 🛠️ Technologies Used

- **Backend:** Java 17+, Spring Boot 3.2.0
- **Frontend:** TypeScript, HTML5, CSS3
- **Database:** SQLite 3.x
- **Build Tool:** Maven
- **Design Patterns:** Singleton, DAO, Factory

---

## 👥 Group Members

*(Isi nama anggota kelompok di sini)*

---

## 📝 Notes untuk Presentasi ke Dosen

### Poin-poin yang harus dijelaskan:

1. **Inheritance**: Tunjukkan `Transaction` → `Income`/`Expense`
2. **Abstract Class**: Jelaskan abstract methods di `Transaction`
3. **Interface**: Demo `Calculable`, `TransactionDAO`
4. **Polymorphism**: Tunjukkan method overloading & runtime polymorphism
5. **Encapsulation**: Semua atribut private dengan getter/setter
6. **Singleton**: `DatabaseManager` pattern
7. **DAO Pattern**: Pemisahan database logic
8. **Exception Handling**: Custom exceptions

### Demo Flow:
1. Jalankan aplikasi dengan `START.sh` / `START.bat`
2. Setup user (username, email, initial balance)
3. Tambah Income dengan kategori bebas (e.g., "Gaji Freelance")
4. Tambah Expense dengan kategori bebas (e.g., "Beli Laptop")
5. Tunjukkan balance update otomatis
6. Export laporan bulanan
7. Show database dengan `./show-database.sh`

---

## 🐛 Troubleshooting

**Port 8080 sudah digunakan:**
```bash
# Linux/Mac
fuser -k 8080/tcp

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Database error:**
- Delete `moneymate.db` dan restart aplikasi
- Database akan dibuat ulang otomatis

**Maven build failed:**
```bash
mvn clean install
```

---

## 📄 License

Educational project for Object-Oriented Programming course.
