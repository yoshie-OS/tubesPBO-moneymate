# MoneyMate - Pembagian Tugas Tim

## 👥 Detail Pembagian Tugas Kelompok

### 📋 Template - Silakan Isi dengan Data Anggota Anda

---

## Anggota 1: [Nama Lengkap] - [NIM]
### Role: Project Leader & Backend Developer

#### ✅ Tanggung Jawab:
- Koordinasi keseluruhan project
- Merancang arsitektur aplikasi
- Code review dan integration
- Repository management (Git)

#### 📝 Implementasi:

**1. Package: moneymate.model**
- [x] `Transaction.java` - Abstract class untuk transaksi
  - Abstract methods: getTransactionType(), isValid()
  - Fields: transactionId, amount, description, date, category
  - Methods: getters, setters, toString()

- [x] `Income.java` - Class untuk pemasukan (extends Transaction)
  - Additional field: source
  - Override: getTransactionType(), isValid()
  
- [x] `Expense.java` - Class untuk pengeluaran (extends Transaction)
  - Additional fields: paymentMethod, isRecurring
  - Override: getTransactionType(), isValid()

- [x] `Category.java` - Enum untuk kategori
  - Income categories: GAJI, BONUS, INVESTASI, LAIN_LAIN_PEMASUKAN
  - Expense categories: MAKANAN, TRANSPORTASI, HIBURAN, dll
  - Methods: isIncomeCategory(), isExpenseCategory()

**2. Package: moneymate.exception**
- [x] `InvalidTransactionException.java`
- [x] `InsufficientBalanceException.java`
- [x] `TransactionNotFoundException.java`
- [x] `FileExportException.java`

#### 📊 Deliverables:
- [ ] Source code (Transaction.java, Income.java, Expense.java, Category.java)
- [ ] Exception classes (4 files)
- [ ] UML Class Diagram
- [ ] Git repository setup

#### ⏰ Timeline:
- Minggu 1-2: Design & arsitektur
- Minggu 3-4: Implementasi model classes
- Minggu 5-8: Code review & integration

---

## Anggota 2: [Nama Lengkap] - [NIM]
### Role: UI/UX Designer & Frontend Developer

#### ✅ Tanggung Jawab:
- Merancang user interface (console-based)
- Implementasi view layer
- User experience & interaction
- Testing usability

#### 📝 Implementasi:

**1. Package: moneymate.view**
- [x] `MoneyMateApp.java` - Main application class
  - Method start() - Initialize aplikasi
  - Method mainMenu() - Display dan handle main menu
  - Method addTransactionMenu() - UI untuk tambah transaksi
  - Method updateTransactionMenu() - UI untuk update transaksi
  - Method deleteTransactionMenu() - UI untuk delete transaksi
  - Method viewBalance() - Display saldo
  - Method monthlyReportMenu() - UI laporan bulanan
  - Method exportReportMenu() - UI export
  - Method filterTransactionsMenu() - UI filter
  - Method displayWelcome() - Welcome screen
  - Method displayGoodbye() - Exit screen

**2. Design Elements**
- Menu structure design
- User flow diagram
- Error message design
- Output formatting

#### 📊 Deliverables:
- [ ] MoneyMateApp.java (complete with all menus)
- [ ] User flow diagram
- [ ] User Manual (docs/USER_MANUAL.md)
- [ ] Screenshot/demo aplikasi

#### ⏰ Timeline:
- Minggu 1-2: UI/UX design & planning
- Minggu 3-5: Implementasi view layer
- Minggu 6-7: User testing & refinement
- Minggu 8: User manual & documentation

---

## Anggota 3: [Nama Lengkap] - [NIM]
### Role: Backend Developer & Data Handler

#### ✅ Tanggung Jawab:
- Implementasi business logic
- Transaction management
- Data processing & validation
- User management system

#### 📝 Implementasi:

**1. Package: moneymate.controller**
- [x] `TransactionManager.java` - Core business logic
  - implements Calculable interface
  - Field: List<Transaction>, initialBalance
  - Method addTransaction() - dengan validasi & exception handling
  - Method deleteTransaction() - remove transaksi
  - Method updateTransaction() - update transaksi
  - Method findTransactionById() - search transaksi
  - Method getTransactionsByType() - filter by type
  - Method getTransactionsByCategory() - filter by category
  - Method getTransactionsByDate() - filter by date
  - Method getTransactionsByMonth() - filter by month
  - Method calculateTotalBalance() - dari Calculable
  - Method calculateTotalIncome() - dari Calculable
  - Method calculateTotalExpense() - dari Calculable
  - Method generateMonthlyReport() - create Report object
  - Method displayAllTransactions() - show all
  - Method displayBalanceSummary() - show summary

**2. Package: moneymate.model**
- [x] `User.java` - Abstract class
  - Fields: userId, username, email, createdAt
  - Abstract methods: displayUserInfo(), validateCredentials()

- [x] `RegularUser.java` - extends User
  - Additional fields: password, initialBalance
  - Override abstract methods
  - Method changePassword()

#### 📊 Deliverables:
- [ ] TransactionManager.java (complete implementation)
- [ ] User.java & RegularUser.java
- [ ] Business logic documentation
- [ ] Integration testing

#### ⏰ Timeline:
- Minggu 2-3: Design controller layer
- Minggu 4-5: Implementasi TransactionManager
- Minggu 5-6: Implementasi User classes
- Minggu 7: Testing & bug fixing

---

## Anggota 4: [Nama Lengkap] - [NIM]
### Role: Quality Assurance & Tester

#### ✅ Tanggung Jawab:
- Input validation utilities
- Date/time utilities
- Testing all features
- Bug reporting & fixing

#### 📝 Implementasi:

**1. Package: moneymate.util**
- [x] `InputValidator.java` - Validation utilities
  - Static method isNumeric() - validate number
  - Static method isInteger() - validate integer
  - Static method isValidAmount() - validate positive amount
  - Static method isNotEmpty() - validate non-empty string
  - Static method isValidEmail() - validate email format
  - Static method getValidIntInput() - get validated int from user
  - Static method getValidDoubleInput() - get validated double
  - Static method getValidStringInput() - get non-empty string

- [x] `DateUtil.java` - Date utilities
  - Static method parseDate() - parse string to LocalDate
  - Static method parseYearMonth() - parse string to YearMonth
  - Static method formatDate() - format LocalDate to string
  - Static method formatYearMonth() - format YearMonth to string
  - Static method getCurrentDate() - get today
  - Static method getCurrentMonth() - get current month
  - Static method isValidDate() - validate date string
  - Static method isValidYearMonth() - validate month string

**2. Testing**
- Create test cases untuk semua fitur
- Manual testing setiap feature
- Bug tracking & reporting
- Performance testing

#### 📊 Deliverables:
- [ ] InputValidator.java (complete)
- [ ] DateUtil.java (complete)
- [ ] Test cases document (docs/TEST_CASES.md)
- [ ] Bug report document (docs/BUG_REPORT.md)
- [ ] Testing summary

#### ⏰ Timeline:
- Minggu 2-3: Implementasi utility classes
- Minggu 4-5: Create test cases
- Minggu 6-7: Manual testing & bug reporting
- Minggu 8: Final testing & validation

---

## Anggota 5: [Nama Lengkap] - [NIM]
### Role: Documentation & Report Generator Specialist

#### ✅ Tanggung Jawab:
- Implementasi report generation
- File export functionality
- Interface design
- Complete documentation

#### 📝 Implementasi:

**1. Package: moneymate.interfaces**
- [x] `Calculable.java` - Interface untuk calculation
  - Method calculateTotalBalance()
  - Method calculateTotalIncome()
  - Method calculateTotalExpense()
  - Method getTransactions()

- [x] `Exportable.java` - Interface untuk export
  - Method exportToFile(String filePath)
  - Method getExportFormat()

**2. Package: moneymate.model**
- [x] `Report.java` - Report generation class
  - Field: transactions, reportPeriod
  - Method getTransactionsInPeriod() - filter by period
  - Method getTotalIncome() - income dalam period
  - Method getTotalExpense() - expense dalam period
  - Method getBalance() - balance dalam period
  - Method getExpenseByCategory() - breakdown expense
  - Method getIncomeByCategory() - breakdown income
  - Method generateSummary() - create formatted report

**3. Package: moneymate.util**
- [x] `FileExporter.java` - implements Exportable
  - Field: transactionManager, format
  - Method exportToFile() - main export method
  - Private method exportToCSV() - export to CSV format
  - Private method exportToTXT() - export to TXT format

**4. Documentation**
- [x] README.md - Project overview
- [x] docs/PROPOSAL.md - Proposal lengkap
- [x] docs/USER_MANUAL.md - User guide
- [x] docs/CLASS_DIAGRAM_DESCRIPTION.md - Class diagram explanation
- [x] COMPILE_RUN_GUIDE.md - How to compile & run
- [ ] Javadoc untuk semua classes
- [ ] Presentation slides

#### 📊 Deliverables:
- [ ] Calculable.java & Exportable.java
- [ ] Report.java (complete)
- [ ] FileExporter.java (complete)
- [ ] Complete documentation (5+ documents)
- [ ] Javadoc HTML
- [ ] Presentation materials

#### ⏰ Timeline:
- Minggu 2-3: Implementasi interfaces & Report class
- Minggu 4: Implementasi FileExporter
- Minggu 5-6: Write documentation
- Minggu 7: Generate Javadoc & create presentation
- Minggu 8: Final review & presentation preparation

---

## 📊 Ringkasan Distribusi Kode

| Anggota | Package | Files | Lines (est.) |
|:-------:|:--------|:------|:------------:|
| 1 | model, exception | 7 files | ~400 lines |
| 2 | view | 1 file | ~350 lines |
| 3 | controller, model (User) | 3 files | ~350 lines |
| 4 | util | 2 files | ~200 lines |
| 5 | interfaces, model (Report), util | 4 files | ~300 lines |

**Total**: ~17 Java files, ~1600 lines of code

---

## 📅 Timeline Overview

| Week | Activities | All Members |
|:----:|:-----------|:------------|
| 1 | Requirements analysis, Class diagram design | Collaboration |
| 2-3 | Core implementation (model, exception, interfaces) | Anggota 1, 5 |
| 4-5 | Controller & View implementation | Anggota 2, 3 |
| 5-6 | Util & testing implementation | Anggota 4 |
| 6-7 | Integration, testing, bug fixing | All |
| 7 | Documentation & Javadoc | Anggota 5 |
| 8 | Final review, presentation preparation | All |

---

## 🤝 Collaboration Guidelines

### Git Workflow
1. Branch naming: `feature/nama-fitur` atau `fix/bug-description`
2. Commit message: Clear & descriptive
3. Pull Request: Review by team leader before merge
4. Daily sync: Update team on progress

### Communication
- Daily standup (online/offline)
- Weekly review meeting
- Slack/WhatsApp group untuk quick questions
- Google Drive untuk shared documents

### Code Standards
- Follow Java naming conventions
- Write Javadoc untuk public methods
- Handle exceptions properly
- Write clean, readable code

---

## ✅ Checklist Progress

### Implementation Progress
- [ ] Exception classes (Anggota 1)
- [ ] Model classes - Transaction hierarchy (Anggota 1)
- [ ] Model classes - User hierarchy (Anggota 3)
- [ ] Category enum (Anggota 1)
- [ ] Interfaces (Anggota 5)
- [ ] TransactionManager (Anggota 3)
- [ ] Report class (Anggota 5)
- [ ] FileExporter (Anggota 5)
- [ ] Utility classes (Anggota 4)
- [ ] MoneyMateApp - View layer (Anggota 2)

### Documentation Progress
- [x] README.md (Anggota 5)
- [x] PROPOSAL.md (Anggota 5)
- [x] USER_MANUAL.md (Anggota 5)
- [x] CLASS_DIAGRAM_DESCRIPTION.md (Anggota 5)
- [ ] Javadoc (Anggota 5)
- [ ] TEST_CASES.md (Anggota 4)
- [ ] Presentation slides (Anggota 5)

### Testing Progress
- [ ] Unit testing (Anggota 4)
- [ ] Integration testing (All)
- [ ] User acceptance testing (Anggota 2, 4)
- [ ] Bug fixing (All)

---

## 📝 Notes

1. **Fleksibilitas**: Jika ada anggota yang selesai lebih cepat, bantu anggota lain
2. **Code Review**: Setiap code harus di-review minimal oleh 1 anggota lain
3. **Testing**: Setiap fitur harus ditest sebelum merge ke main branch
4. **Documentation**: Update documentation seiring development
5. **Backup**: Regular commit ke repository untuk avoid data loss

---

**Last Updated**: [Tanggal]
**Project Status**: [In Progress / Ready for Review / Completed]

---
