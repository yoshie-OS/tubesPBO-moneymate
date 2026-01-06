# Balance Persistence - Quick Test Checklist

## ✅ Pre-Test Verification
- [ ] Backend JAR rebuilt: `target\moneymate-1.0.0.jar` (33.9MB)
- [ ] Frontend built: `frontend\dist\app.js` exists
- [ ] No moneymate.db file (will be created fresh on first run)

## 🧪 Test Scenario 1: Basic Persistence (5 minutes)

### Setup
1. [ ] Delete `moneymate.db` if it exists
2. [ ] Run `START.bat`
3. [ ] Frontend loads at http://localhost:3000
4. [ ] Backend running at http://localhost:8080/api

### Register User
- [ ] Click "Register / Daftar"
- [ ] Enter:
  - Username: `testuser`
  - Email: `test@example.com`
  - Password: `password123`
  - Initial Balance: **1000000** (Rp 1,000,000)
- [ ] Click "Register"
- [ ] Verify balance shows **Rp 1,000,000**

### Add Transaction
- [ ] Go to "Tambah Transaksi" (Add Transaction)
- [ ] Add Income:
  - Type: Income
  - Amount: 500000
  - Description: Salary
  - Category: Salary
  - Date: Today
- [ ] Click "Tambah" (Add)
- [ ] Verify balance updates to **Rp 1,500,000**

### First Restart Test
- [ ] **Close the application completely** (Ctrl+C on console)
- [ ] **Wait 2 seconds**
- [ ] **Run `START.bat` again**
- [ ] Frontend loads
- [ ] Login with username: `testuser`, password: `password123`
- [ ] **CRITICAL CHECK**: Balance should show **Rp 1,500,000** ✅ or ❌

### Result
- **If balance is Rp 1,500,000**: ✅ **TEST PASSED**
- **If balance is Rp 1,000,000**: ❌ Initial balance only (transactions not loaded)
- **If balance is 0**: ❌ Data not persisting (database issue)

---

## 🧪 Test Scenario 2: Multiple Transactions (10 minutes)

### Setup
- [ ] Continue from Test 1 or register new user
- [ ] Starting balance: **Rp 2,000,000**

### Add Multiple Transactions
1. [ ] **Income**: +Rp 500,000 (Bonus)
   - Expected balance: **Rp 2,500,000**
   - Verify: ✅ Balance shows Rp 2,500,000

2. [ ] **Expense**: -Rp 300,000 (Food)
   - Expected balance: **Rp 2,200,000**
   - Verify: ✅ Balance shows Rp 2,200,000

3. [ ] **Expense**: -Rp 150,000 (Transport)
   - Expected balance: **Rp 2,050,000**
   - Verify: ✅ Balance shows Rp 2,050,000

### Restart Test
- [ ] **Close application**
- [ ] **Run `START.bat` again**
- [ ] Login
- [ ] Check balance: Should show **Rp 2,050,000** ✅

### Delete Transaction Test
- [ ] Go to "Lihat Transaksi" (View Transactions)
- [ ] Delete one expense (-Rp 150,000)
- [ ] Balance should update to **Rp 2,200,000**
- [ ] **Close and restart**
- [ ] Login and verify balance is **Rp 2,200,000** ✅

---

## 🧪 Test Scenario 3: Report Balance (5 minutes)

### Setup
- [ ] Have at least 2-3 transactions from current month

### Generate Report
- [ ] Go to "Laporan" (Reports)
- [ ] Click "Generate laporan"
- [ ] Verify report shows:
  - [ ] **Saldo Awal** (Starting balance)
  - [ ] **Total Pemasukan** (Total income)
  - [ ] **Total Pengeluaran** (Total expenses)
  - [ ] **Saldo Akhir** (Final balance)
  - [ ] Formula check: Saldo Akhir = Saldo Awal + Pemasukan - Pengeluaran ✅

### Restart Test
- [ ] **Close and restart** application
- [ ] Login
- [ ] Generate report again
- [ ] Verify **Saldo Akhir is the same** ✅

---

## 🔍 Console Log Verification

### Expected Messages (when balance is working):
```
✓ Database connection established
✓ Database tables initialized
✓ Switching user: DEFAULT_USER → USER_TESTUSER_...
✓ Loaded N transactions from database for user: USER_TESTUSER_...
✓ Transaksi berhasil ditambahkan: TRANS_...
✓ User balance updated in database: Rp 1,500,000.00 (User ID: USER_TESTUSER_...)
```

### Warning Signs (something wrong):
- `⚠ Warning: No rows updated for user ID: ...` → User not in database
- `⚠ Warning: Attempted to update balance for DEFAULT_USER` → Not logged in properly
- **No update message after transaction** → Balance not being saved

---

## ❌ Troubleshooting

### Balance goes to zero after restart:
1. Check console for warning messages ↑
2. Verify `moneymate.db` file exists in project root
3. Confirm user logs in (not DEFAULT_USER)
4. Check that transactions are showing in "Lihat Transaksi"

### Balance shows old value (not updated after transaction):
1. Wait 1-2 seconds for database to save
2. Refresh the page (Ctrl+R)
3. Check console for errors

### Report balance wrong:
1. Verify "Saldo Awal" matches initial balance
2. Check that transactions are included
3. Manually calculate: Saldo Awal + Pemasukan - Pengeluaran

---

## 📋 Final Checklist

- [ ] **Scenario 1 PASSED**: Balance persists after restart
- [ ] **Scenario 2 PASSED**: Multiple transactions persist correctly  
- [ ] **Scenario 3 PASSED**: Report calculations are accurate
- [ ] **Console logs**: Show update messages, no warnings
- [ ] **Database file**: `moneymate.db` exists and growing
- [ ] **Ready for production**: ✅ YES / ❌ NO

---

## 📝 Notes
- Database persists in: `c:\Users\asus\tubesPBO-moneymate\tubesPBO-moneymate-fix-reports-UI\moneymate.db`
- To reset: Delete `moneymate.db` and run START.bat
- Each user has separate transaction list (multi-user support)
- Balance calculation: `initial_balance + income - expense`
