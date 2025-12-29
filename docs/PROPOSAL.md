# PROPOSAL PROYEK APLIKASI MONEYMATE

## 📱 Judul Aplikasi
**MoneyMate - Personal Finance Manager**

Aplikasi Manajer Keuangan Pribadi Berbasis Desktop

---

## 👥 Tim Pengembang

| No | Nama | NIM | Peran |
|:--:|:-----|:----|:------|
| 1  | [Nama Anggota 1] | [NIM] | Project Leader & Backend Developer |
| 2  | [Nama Anggota 2] | [NIM] | UI/UX Designer & Frontend Developer |
| 3  | [Nama Anggota 3] | [NIM] | Backend Developer & Database Handler |
| 4  | [Nama Anggota 4] | [NIM] | Quality Assurance & Tester |
| 5  | [Nama Anggota 5] | [NIM] | Documentation & Report Generator |

---

## 📋 Deskripsi Aplikasi

MoneyMate adalah aplikasi manajer keuangan pribadi berbasis desktop yang dirancang untuk membantu pengguna melacak, mengelola, dan menganalisis arus kas (pemasukan dan pengeluaran) mereka. Aplikasi ini menerapkan prinsip-prinsip **Object-Oriented Programming (OOP)** untuk menciptakan sistem yang modular, mudah dikelola, dan dapat diperluas.

### Tujuan Aplikasi:
- Membantu pengguna mencatat transaksi keuangan harian
- Memberikan visualisasi dan analisis pengeluaran
- Menghasilkan laporan keuangan bulanan
- Memudahkan pengelolaan kategori transaksi

---

## ✨ Daftar Fitur Utama

### 1. Manajemen Transaksi
- ✅ **Tambah Transaksi**: Mencatat pemasukan dan pengeluaran dengan detail lengkap
- ✅ **Ubah Transaksi**: Memodifikasi data transaksi yang sudah ada
- ✅ **Hapus Transaksi**: Menghapus transaksi dengan konfirmasi
- ✅ **Lihat Semua Transaksi**: Menampilkan daftar lengkap transaksi

### 2. Klasifikasi Transaksi
**Kategori Pemasukan:**
- Gaji
- Bonus
- Investasi
- Lain-lain

**Kategori Pengeluaran:**
- Makanan
- Transportasi
- Hiburan
- Belanja
- Tagihan
- Kesehatan
- Pendidikan
- Lain-lain

### 3. Perhitungan Keuangan
- 💰 Hitung total saldo (pemasukan - pengeluaran)
- 📊 Hitung total pemasukan
- 📉 Hitung total pengeluaran
- 🔍 Analisis per kategori

### 4. Laporan Bulanan
- 📅 Generate laporan per bulan
- 📈 Breakdown pengeluaran dan pemasukan per kategori
- 💯 Persentase pengeluaran per kategori
- 📝 Summary lengkap transaksi bulanan

### 5. Export Laporan
- 📄 Export ke format **CSV** (untuk Excel)
- 📝 Export ke format **TXT** (untuk dokumentasi)
- 💾 Save otomatis dengan timestamp

### 6. Filter & Pencarian
- 🔎 Filter by tipe transaksi (Income/Expense)
- 🏷️ Filter by kategori
- 📆 Filter by tanggal tertentu
- 📅 Filter by bulan tertentu

### 7. Validasi Data
- ⚠️ **Custom Exception Handling** untuk error management
- ✔️ Validasi input (jumlah, tanggal, kategori)
- 🛡️ Proteksi saldo (mencegah pengeluaran melebihi saldo)
- 📧 Validasi format email

---

## 🏗️ Penerapan Konsep OOP

### 1. **Inheritance** (Pewarisan)
```
Transaction (Abstract Class)
    ├── Income (extends Transaction)
    └── Expense (extends Transaction)

User (Abstract Class)
    └── RegularUser (extends User)
```

### 2. **Interface**
- **Calculable**: Interface untuk menghitung total saldo, income, dan expense
  - Diimplementasikan oleh: `TransactionManager`
  
- **Exportable**: Interface untuk export data ke berbagai format
  - Diimplementasikan oleh: `FileExporter`

### 3. **Abstract Class**
- **Transaction**: Parent class untuk semua jenis transaksi
  - Abstract methods: `getTransactionType()`, `isValid()`
  
- **User**: Base class untuk sistem user (untuk pengembangan multi-user)
  - Abstract methods: `displayUserInfo()`, `validateCredentials()`

### 4. **Encapsulation**
- Semua atribut menggunakan modifier `private` atau `protected`
- Akses data melalui getter/setter
- Validasi di dalam setter methods

### 5. **Polymorphism**
- Method overriding di class `Income` dan `Expense`
- Method `toString()` di-override untuk custom display
- Interface implementation dengan behavior berbeda

### 6. **Custom Exception**
- `InvalidTransactionException`: Validasi transaksi tidak valid
- `InsufficientBalanceException`: Saldo tidak mencukupi
- `TransactionNotFoundException`: Transaksi tidak ditemukan
- `FileExportException`: Error saat export file

### 7. **Enum**
- `Category`: Enum untuk kategori transaksi dengan tipe (Income/Expense)

---

## 📊 Class Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        <<interface>>                            │
│                         Calculable                              │
├─────────────────────────────────────────────────────────────────┤
│ + calculateTotalBalance(): double                               │
│ + calculateTotalIncome(): double                                │
│ + calculateTotalExpense(): double                               │
│ + getTransactions(): List<Transaction>                          │
└─────────────────────────────────────────────────────────────────┘
                              △
                              │
                              │ implements
                              │
┌─────────────────────────────────────────────────────────────────┐
│                     TransactionManager                          │
├─────────────────────────────────────────────────────────────────┤
│ - transactions: List<Transaction>                               │
│ - initialBalance: double                                        │
├─────────────────────────────────────────────────────────────────┤
│ + addTransaction(Transaction): void                             │
│ + deleteTransaction(String): void                               │
│ + updateTransaction(String, Transaction): void                  │
│ + findTransactionById(String): Transaction                      │
│ + generateMonthlyReport(YearMonth): Report                      │
│ + displayAllTransactions(): void                                │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                        <<abstract>>                             │
│                         Transaction                             │
├─────────────────────────────────────────────────────────────────┤
│ # transactionId: String                                         │
│ # amount: double                                                │
│ # description: String                                           │
│ # date: LocalDate                                               │
│ # category: Category                                            │
├─────────────────────────────────────────────────────────────────┤
│ + getTransactionType(): String {abstract}                       │
│ + isValid(): boolean {abstract}                                 │
│ + toString(): String                                            │
└─────────────────────────────────────────────────────────────────┘
                              △
                              │
                 ┌────────────┴────────────┐
                 │                         │
┌────────────────────────────┐  ┌────────────────────────────┐
│         Income             │  │         Expense            │
├────────────────────────────┤  ├────────────────────────────┤
│ - source: String           │  │ - paymentMethod: String    │
├────────────────────────────┤  │ - isRecurring: boolean     │
│ + getTransactionType()     │  ├────────────────────────────┤
│ + isValid()                │  │ + getTransactionType()     │
│ + toString()               │  │ + isValid()                │
└────────────────────────────┘  │ + toString()               │
                                └────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                        <<enumeration>>                          │
│                          Category                               │
├─────────────────────────────────────────────────────────────────┤
│ GAJI, BONUS, INVESTASI, LAIN_LAIN_PEMASUKAN                    │
│ MAKANAN, TRANSPORTASI, HIBURAN, BELANJA                        │
│ TAGIHAN, KESEHATAN, PENDIDIKAN, LAIN_LAIN_PENGELUARAN         │
├─────────────────────────────────────────────────────────────────┤
│ - displayName: String                                           │
│ - type: String                                                  │
├─────────────────────────────────────────────────────────────────┤
│ + isIncomeCategory(): boolean                                   │
│ + isExpenseCategory(): boolean                                  │
│ + getIncomeCategories(): Category[]                             │
│ + getExpenseCategories(): Category[]                            │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                        <<abstract>>                             │
│                            User                                 │
├─────────────────────────────────────────────────────────────────┤
│ # userId: String                                                │
│ # username: String                                              │
│ # email: String                                                 │
│ # createdAt: LocalDateTime                                      │
├─────────────────────────────────────────────────────────────────┤
│ + displayUserInfo(): void {abstract}                            │
│ + validateCredentials(String): boolean {abstract}               │
└─────────────────────────────────────────────────────────────────┘
                              △
                              │
┌─────────────────────────────────────────────────────────────────┐
│                        RegularUser                              │
├─────────────────────────────────────────────────────────────────┤
│ - password: String                                              │
│ - initialBalance: double                                        │
├─────────────────────────────────────────────────────────────────┤
│ + displayUserInfo(): void                                       │
│ + validateCredentials(String): boolean                          │
│ + changePassword(String, String): void                          │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                           Report                                │
├─────────────────────────────────────────────────────────────────┤
│ - transactions: List<Transaction>                               │
│ - reportPeriod: YearMonth                                       │
├─────────────────────────────────────────────────────────────────┤
│ + getTransactionsInPeriod(): List<Transaction>                  │
│ + getTotalIncome(): double                                      │
│ + getTotalExpense(): double                                     │
│ + getBalance(): double                                          │
│ + getExpenseByCategory(): Map<Category, Double>                 │
│ + getIncomeByCategory(): Map<Category, Double>                  │
│ + generateSummary(): String                                     │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                    <<Exception Classes>>                        │
├─────────────────────────────────────────────────────────────────┤
│ InvalidTransactionException                                     │
│ InsufficientBalanceException                                    │
│ TransactionNotFoundException                                    │
│ FileExportException                                             │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                        <<interface>>                            │
│                         Exportable                              │
├─────────────────────────────────────────────────────────────────┤
│ + exportToFile(String): void                                    │
│ + getExportFormat(): String                                     │
└─────────────────────────────────────────────────────────────────┘
                              △
                              │ implements
                              │
┌─────────────────────────────────────────────────────────────────┐
│                        FileExporter                             │
├─────────────────────────────────────────────────────────────────┤
│ - transactionManager: TransactionManager                        │
│ - format: String                                                │
├─────────────────────────────────────────────────────────────────┤
│ + exportToFile(String): void                                    │
│ + exportToCSV(String): void                                     │
│ + exportToTXT(String): void                                     │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                        MoneyMateApp                             │
├─────────────────────────────────────────────────────────────────┤
│ - transactionManager: TransactionManager                        │
│ - currentUser: RegularUser                                      │
│ - scanner: Scanner                                              │
├─────────────────────────────────────────────────────────────────┤
│ + start(): void                                                 │
│ + mainMenu(): void                                              │
│ + addTransactionMenu(): void                                    │
│ + updateTransactionMenu(): void                                 │
│ + deleteTransactionMenu(): void                                 │
│ + viewBalance(): void                                           │
│ + monthlyReportMenu(): void                                     │
│ + exportReportMenu(): void                                      │
│ + filterTransactionsMenu(): void                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📋 Penjelasan Rancangan Fitur dan Pembagian Tugas Anggota

### Anggota 1: [Nama] - Project Leader & Backend Developer
**Tugas:**
- Merancang arsitektur aplikasi secara keseluruhan
- Mengimplementasikan package `model`:
  - Abstract class `Transaction`
  - Class `Income` dan `Expense` (Inheritance)
  - Enum `Category`
- Membuat Custom Exceptions
- Koordinasi tim dan code review

**Deliverables:**
- `Transaction.java`, `Income.java`, `Expense.java`
- `Category.java`
- Package `exception` (semua exception classes)

---

### Anggota 2: [Nama] - UI/UX Designer & Frontend Developer
**Tugas:**
- Merancang User Interface (Console-based)
- Mengimplementasikan package `view`:
  - Class `MoneyMateApp` (Main Application)
  - Menu system dan user interaction
- Membuat desain flow diagram aplikasi
- Testing user experience

**Deliverables:**
- `MoneyMateApp.java`
- Flow diagram
- User manual/guide

---

### Anggota 3: [Nama] - Backend Developer & Database Handler
**Tugas:**
- Mengimplementasikan package `controller`:
  - Class `TransactionManager` (implements Calculable)
- Mengimplementasikan Abstract class `User` dan `RegularUser`
- Membuat logika bisnis untuk manajemen transaksi
- Handle data persistence (future: database integration)

**Deliverables:**
- `TransactionManager.java`
- `User.java`, `RegularUser.java`
- Business logic documentation

---

### Anggota 4: [Nama] - Quality Assurance & Tester
**Tugas:**
- Mengimplementasikan package `util`:
  - Class `InputValidator` (validasi input)
  - Class `DateUtil` (utility tanggal)
- Membuat test cases untuk semua fitur
- Melakukan testing dan bug reporting
- Membuat exception handling strategy

**Deliverables:**
- `InputValidator.java`, `DateUtil.java`
- Test cases document
- Bug report & fixes

---

### Anggota 5: [Nama] - Documentation & Report Generator
**Tugas:**
- Mengimplementasikan:
  - Class `Report` (generate laporan bulanan)
  - Class `FileExporter` (implements Exportable)
  - Interface `Calculable` dan `Exportable`
- Membuat dokumentasi kode (Javadoc)
- Membuat proposal dan laporan akhir
- Presentasi

**Deliverables:**
- `Report.java`, `FileExporter.java`
- `Calculable.java`, `Exportable.java`
- Proposal, dokumentasi lengkap
- Slide presentasi

---

## 💻 Teknologi yang Digunakan

### 1. Bahasa Pemrograman
- **Java SE 11 atau lebih tinggi**
  - Object-Oriented Programming
  - Java Collections Framework
  - Java Time API (LocalDate, YearMonth)
  - Exception Handling

### 2. Development Tools
- **IDE**: 
  - IntelliJ IDEA / Eclipse / NetBeans / VS Code
- **Build Tool**: 
  - Manual compilation atau Maven/Gradle (opsional)
- **Version Control**: 
  - Git & GitHub untuk kolaborasi tim

### 3. Libraries & APIs
- **Java Standard Library**:
  - `java.util` - Collections, Scanner
  - `java.time` - Date/Time handling
  - `java.io` - File I/O operations
  - `java.util.stream` - Stream API untuk filtering

### 4. File Formats
- **Export Formats**:
  - CSV (Comma-Separated Values) - untuk Excel
  - TXT (Plain Text) - untuk dokumentasi

### 5. Design Tools
- **Class Diagram**: 
  - Draw.io / Lucidchart / Visual Paradigm
- **Documentation**:
  - Markdown untuk README
  - Javadoc untuk code documentation
  - Microsoft Word/Google Docs untuk proposal

---

## 📁 Struktur Project

```
Tubes_PBO/
│
├── src/
│   └── moneymate/
│       ├── model/
│       │   ├── Transaction.java (abstract)
│       │   ├── Income.java
│       │   ├── Expense.java
│       │   ├── Category.java (enum)
│       │   ├── User.java (abstract)
│       │   ├── RegularUser.java
│       │   └── Report.java
│       │
│       ├── controller/
│       │   └── TransactionManager.java
│       │
│       ├── view/
│       │   └── MoneyMateApp.java (Main)
│       │
│       ├── exception/
│       │   ├── InvalidTransactionException.java
│       │   ├── InsufficientBalanceException.java
│       │   ├── TransactionNotFoundException.java
│       │   └── FileExportException.java
│       │
│       ├── interfaces/
│       │   ├── Calculable.java
│       │   └── Exportable.java
│       │
│       └── util/
│           ├── FileExporter.java
│           ├── DateUtil.java
│           └── InputValidator.java
│
├── docs/
│   ├── PROPOSAL.md (this file)
│   ├── CLASS_DIAGRAM.png
│   └── USER_MANUAL.md
│
├── exports/
│   └── (exported reports akan tersimpan disini)
│
└── README.md
```

---

## 🎯 Timeline Pengembangan

| Minggu | Kegiatan | PIC |
|:------:|:---------|:----|
| 1 | Analisis kebutuhan & perancangan class diagram | Semua anggota |
| 2 | Implementasi model classes & exceptions | Anggota 1, 5 |
| 3 | Implementasi controller & business logic | Anggota 3 |
| 4 | Implementasi view & utility classes | Anggota 2, 4 |
| 5 | Integration & testing | Anggota 4 |
| 6 | Bug fixing & optimization | Semua anggota |
| 7 | Dokumentasi & laporan | Anggota 5 |
| 8 | Presentasi & demo | Semua anggota |

---

## 🎓 Konsep OOP yang Diterapkan - Summary

### ✅ 1. Inheritance (Pewarisan)
- `Transaction` → `Income`, `Expense`
- `User` → `RegularUser`

### ✅ 2. Interface
- `Calculable` - untuk perhitungan keuangan
- `Exportable` - untuk export file

### ✅ 3. Abstract Class
- `Transaction` - base class transaksi
- `User` - base class user

### ✅ 4. Encapsulation
- Private/protected fields dengan getter/setter
- Validasi di setter methods

### ✅ 5. Polymorphism
- Method overriding: `getTransactionType()`, `isValid()`, `toString()`
- Interface implementation dengan behavior berbeda

### ✅ 6. Exception Handling
- Custom exceptions untuk error handling yang spesifik
- Thrown dan caught di berbagai layer aplikasi

### ✅ 7. Enum
- `Category` dengan methods tambahan

---

## 🚀 Cara Menjalankan Aplikasi

### Compile:
```bash
# Dari folder Tubes_PBO
javac -d bin src/moneymate/**/*.java
```

### Run:
```bash
# Dari folder Tubes_PBO
java -cp bin moneymate.view.MoneyMateApp
```

Atau menggunakan IDE favorit Anda dengan menjalankan method `main()` di class `MoneyMateApp`.

---

## 📝 Catatan Pengembangan Future

1. **GUI Version**: Implementasi JavaFX/Swing untuk versi GUI
2. **Database Integration**: Tambahkan persistence dengan SQLite/MySQL
3. **Multi-user Support**: Full implementation user authentication
4. **Budget Planning**: Fitur perencanaan budget bulanan
5. **Charts & Graphs**: Visualisasi data dengan JFreeChart
6. **Mobile App**: Port ke Android/iOS

---

## 📞 Kontak Tim

Untuk pertanyaan atau informasi lebih lanjut, silakan hubungi:

**Email**: [email kelompok]@university.edu
**Repository**: https://github.com/[username]/MoneyMate

---

## 📄 Lisensi

© 2025 MoneyMate Development Team. All Rights Reserved.

Aplikasi ini dibuat untuk tujuan pembelajaran dalam mata kuliah Pemrograman Berorientasi Objek.

---

**Disetujui oleh:**

Tim MoneyMate Development
Tanggal: [Tanggal Submit]

---

