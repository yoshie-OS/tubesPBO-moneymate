# Balance Persistence Fix

## Problem
**Each time the application was restarted, the balance was reset to zero** instead of retaining the updated balance from previous transactions.

### Root Cause
The balance was only being stored in **memory** (`TransactionManager.initialBalance`) and not being **saved back to the database** after transactions. While transactions were persisted to the `transactions` table, the updated balance in the `users` table was never updated.

### How it Failed:
1. User registers with initial balance (e.g., Rp 1,000,000)
   - ✅ Initial balance saved to `users` table
2. User adds income/expenses
   - ✅ Transactions saved to `transactions` table
   - ❌ Updated balance NOT saved to `users` table
3. Application restarts
   - ❌ `initial_balance` column still has original value
   - Balance = initial_balance + transactions (incorrect if initial_balance wasn't updated)

## Solution Implemented

### Changes Made to `TransactionManager.java`:

1. **Added new method `updateUserBalance()`**
   - Updates the `initial_balance` column in the `users` table with the current calculated balance
   - Called after each transaction operation

2. **Modified transaction methods to persist balance:**
   - `addTransaction()` - now calls `updateUserBalance()` after saving
   - `deleteTransaction()` - now calls `updateUserBalance()` after deleting
   - `updateTransaction()` - now calls `updateUserBalance()` after updating

3. **Added database imports:**
   - `java.sql.Connection`
   - `java.sql.PreparedStatement`
   - `moneymate.database.DatabaseManager`

### How it Works Now:
1. Transaction is saved to database
2. Immediately after, the user's current balance is calculated
3. The `users.initial_balance` column is updated with this new balance
4. On restart, the balance is loaded correctly from the database

## Testing Instructions

1. **Start the application** (run `START.bat`)
2. **Register a user** with initial balance (e.g., Rp 1,000,000)
3. **Add transactions** (income/expenses)
4. **Verify balance** in the UI
5. **Close and restart** the application
6. **Login again** - balance should be preserved ✅

## Database Schema Note

The existing `users` table already had the `initial_balance` column, so no schema changes were needed. We simply repurposed this column to store the **current** balance instead of just the initial balance, which is semantically more accurate.

For future improvements, consider:
- Renaming the column to `current_balance` for clarity
- Adding a separate `initial_balance` column to track the original starting amount
- Adding timestamp columns to track when balance was last updated
