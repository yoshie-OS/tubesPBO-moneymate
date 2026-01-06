# Report Balance Fix Documentation

## Problem
When generating reports in the "Generate laporan" section, the **saldo (balance) was showing as zero** because the `Report` class was only calculating balance from:
- `Total Income - Total Expense`

It was **not including the initial balance**, which is the starting amount when the user first registered.

### Example of the Bug:
- Initial Balance: Rp 1,000,000
- Income during month: Rp 500,000
- Expenses during month: Rp 200,000
- **Incorrect Report Balance**: Rp 300,000 (only income - expenses)
- **Correct Report Balance**: Rp 1,300,000 (initial + income - expenses)

## Solution Implemented

### 1. Updated `Report.java` Model
**Changes:**
- Added `initialBalance` field to store the starting balance
- Updated constructor with optional parameter for `initialBalance`
- Modified `getBalance()` method to include initial balance:
  ```java
  public double getBalance() {
      return initialBalance + getTotalIncome() - getTotalExpense();
  }
  ```
- Updated `generateSummary()` to display:
  - Saldo Awal (Initial Balance)
  - Total Pemasukan (Total Income)
  - Total Pengeluaran (Total Expense)
  - Saldo Akhir (Final Balance) = Initial + Income - Expense
- Added getter and setter for `initialBalance`

### 2. Updated `TransactionManager.java`
**Changes:**
- Modified `generateMonthlyReport()` to pass the current `initialBalance`:
  ```java
  public Report generateMonthlyReport(YearMonth month) {
      return new Report(transactions, month, initialBalance);
  }
  ```

### 3. Updated `TransactionController.java` API
**Changes:**
- Added `initialBalance` field to the API response:
  ```json
  {
    "month": "2026-01",
    "initialBalance": 1000000.0,
    "totalIncome": 500000.0,
    "totalExpense": 200000.0,
    "balance": 1300000.0,
    "expenseByCategory": {...},
    "incomeByCategory": {...},
    "summary": "..."
  }
  ```

### 4. Updated Frontend `app.ts` UI
**Changes:**
- Enhanced the report display to show 4 summary cards instead of 3:
  1. **Saldo Awal** (Initial Balance) - Purple color
  2. **Total Pemasukan** (Total Income) - Green color
  3. **Total Pengeluaran** (Total Expense) - Red color
  4. **Saldo Akhir** (Final Balance) - Primary color

This makes the balance calculation transparent and easy to verify.

## How It Works Now

### Report Calculation Formula:
```
Final Balance = Initial Balance + Total Income - Total Expenses
```

### Step by Step:
1. User registers with initial balance
2. User adds transactions (income/expenses)
3. Each transaction is saved to database
4. User generates monthly report
5. Report retrieves:
   - User's current balance (stored in users table)
   - All transactions for the month
6. Report calculates:
   - Sum of all income transactions
   - Sum of all expenses transactions
   - Final balance = Initial + Income - Expenses
7. Report displays all values with breakdown by category

## Files Modified
1. [src/moneymate/model/Report.java](src/moneymate/model/Report.java)
   - Added initialBalance field and constructors
   - Updated getBalance() method
   - Updated generateSummary() method
   - Added getInitialBalance() and setInitialBalance() methods

2. [src/moneymate/controller/TransactionManager.java](src/moneymate/controller/TransactionManager.java)
   - Updated generateMonthlyReport() to pass initialBalance

3. [src/moneymate/api/TransactionController.java](src/moneymate/api/TransactionController.java)
   - Updated /api/report/{month} endpoint to include initialBalance

4. [frontend/src/app.ts](frontend/src/app.ts)
   - Updated report UI to display 4 summary cards
   - Changed "Saldo" to "Saldo Awal" and "Saldo Akhir"

## Testing
1. Start the application
2. Login or register with initial balance (e.g., Rp 1,000,000)
3. Add several income and expense transactions
4. Go to "Generate laporan" section
5. Select a month and click generate
6. Verify the balance calculation:
   - Saldo Awal = Your starting balance
   - Total Pemasukan = Sum of all income
   - Total Pengeluaran = Sum of all expenses
   - Saldo Akhir = Saldo Awal + Total Pemasukan - Total Pengeluaran

## Related Fixes
This fix works in conjunction with the previous balance persistence fix. Now:
- ✅ Balance is saved to database after each transaction
- ✅ Balance is loaded correctly on login
- ✅ Balance is correctly displayed in reports

## Build Status
- Backend: ✅ Built successfully (moneymate-1.0.0.jar)
- Frontend: ✅ Compiled successfully
- Ready to deploy and test
