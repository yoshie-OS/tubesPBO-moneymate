# Category Issue - "Lain-Lain" Problem Diagnosis Guide

## Problem
When generating reports ("Generate Laporan"), all expenses in the **"Rincian Pengeluaran per kategori"** (Expense Breakdown by Category) section are being grouped under the **"Lain-Lain"** (Other/Miscellaneous) category instead of their proper categories.

## Root Cause
The issue is likely one of these:

1. **Category is being sent as NULL from frontend** to backend
2. **Category is not being saved to the database** properly
3. **Category is being saved as NULL** in the database
4. **Category is NULL when retrieved** from the database

## Diagnostic Steps

I've added **detailed logging** to the backend to trace where the category is being lost.

### Step 1: Run the Application

```batch
START.bat
```

### Step 2: Register and Add Test Transactions

1. **Register** with initial balance: `Rp 2,000,000`

2. **Add multiple expenses with DIFFERENT categories:**
   - Makanan (Food): `Rp 100,000`
   - Transportasi (Transport): `Rp 150,000`
   - Belanja (Shopping): `Rp 200,000`
   - Hiburan (Entertainment): `Rp 75,000`

### Step 3: Monitor Backend Console

When each transaction is added, you should see logs like:

```
Creating transaction from request:
  Type: PENGELUARAN
  Category from request: Makanan
  Parsed to: Makanan
  Category being saved: Makanan
```

### Step 4: Check for Issues

#### PROBLEM 1: Category is NULL from frontend
If you see:
```
Creating transaction from request:
  Type: PENGELUARAN
  Category from request: NULL
  Parsed to: Lain-lain
```

**Issue**: Frontend is not sending category
**Solution**: Check that the category select dropdown is properly populated

#### PROBLEM 2: Category is NULL in database
If you see:
```
  Category being saved: NULL
```

**Issue**: Category field is empty or not being retrieved correctly from category select
**Solution**: Verify category dropdown is selected before adding transaction

#### PROBLEM 3: Proper category being saved but retrieves as NULL
If you see correct logs when saving but "Lain-lain" in report:
```
Category being saved: Makanan
...
[on reload]
Category from DB: NULL → Parsed as: Lain-lain
```

**Issue**: Database corruption or category column issue
**Solution**: Delete database and start fresh

### Step 5: Generate Report

1. Go to "Laporan" (Reports) section
2. Click "Generate laporan"
3. Check the **"Rincian Pengeluaran per Kategori"** section
4. Should show breakdown by categories like:
   - Makanan: Rp 100,000
   - Transportasi: Rp 150,000
   - Belanja: Rp 200,000
   - Hiburan: Rp 75,000

NOT all under "Lain-lain"

## Expected Console Output

### When registering user:
```
✓ User initialized
✓ User saved to database: testuser
  Password saved: YES
```

### When adding each transaction:
```
Creating transaction from request:
  Type: PENGELUARAN
  Category from request: Makanan
  Parsed to: Makanan
  Category being saved: Makanan
✓ Transaksi berhasil ditambahkan: TRANS_...
✓ User balance updated in database: Rp ...
```

### When loading transactions:
```
✓ Loaded 4 transactions from database for user: USER_...
  Category from DB: Makanan → Parsed as: Makanan
  Category from DB: Transportasi → Parsed as: Transportasi
  Category from DB: Belanja → Parsed as: Belanja
  Category from DB: Hiburan → Parsed as: Hiburan
```

## Frontend Category Selection

The frontend needs to properly send category. Verify:

1. **Category dropdown is populated** when changing transaction type
2. **A category is SELECTED** before clicking "Tambah" (Add)
3. **Category value is sent** in the POST request to `/api/transactions`

### To check in browser:
1. Open F12 (Developer Tools)
2. Go to Network tab
3. Add a transaction
4. Click on the POST request to `transactions`
5. Check Request Body - should include:
```json
{
  "type": "expense",
  "category": "Makanan",
  "amount": 100000,
  ...
}
```

If category is missing or empty, the problem is in the frontend category dropdown.

## Database Check

To verify categories are in the database:

1. Stop the application
2. Use SQLite browser to open `moneymate.db`
3. Query: `SELECT * FROM transactions`
4. Check the `category` column - should show actual category names, NOT NULL

## Common Issues and Solutions

### Issue 1: All categories show as "Lain-lain"
**Possible causes:**
- Frontend category dropdown not working
- Category field not being sent to backend
- Database categories are NULL

**Verify:**
1. Check console logs for "Category from request: NULL"
2. Check browser Network tab for missing category field
3. Check database for NULL values in category column

### Issue 2: Categories work but report shows "Lain-lain"
**Possible causes:**
- Report calculation filtering wrong category
- Category name mismatch in report grouping

**Verify:**
- Check that categories are correctly retrieved when loading (see console logs)
- Check that report is grouping by the right field

### Issue 3: Some categories work, some show as "Lain-lain"
**Possible causes:**
- Specific category names are typed wrong
- Category name has extra spaces or special characters

**Verify:**
1. Check exact category names in Category.java enum
2. Check that dropdown shows correct display names
3. Verify no extra spaces in category values

## Files with Logging

Logging has been added to:

1. **TransactionController.java**
   - Logs when category is received from frontend
   - Logs category parsing result
   - Shows what's being sent to database

2. **TransactionDAOImpl.java**
   - Logs when category is being saved to database
   - Logs when category is retrieved from database
   - Shows what category was parsed to

## Next Steps

1. **Delete database** (done: moneymate.db deleted)
2. **Run START.bat**
3. **Add test transactions** with different categories
4. **Monitor console** for the logging above
5. **Identify** which step is failing:
   - Frontend not sending category?
   - Backend not parsing?
   - Database not storing?
   - Database not retrieving?
6. **Report back** with the specific console output and the step that's failing
7. **Fix** will be targeted based on where the issue is

## Example Good Session

```
[Register]
✓ User initialized
✓ User saved to database: testuser
  Password saved: YES

[Add Expense: Makanan]
Creating transaction from request:
  Type: PENGELUARAN
  Category from request: Makanan
  Parsed to: Makanan
  Category being saved: Makanan
✓ Transaksi berhasil ditambahkan: TRANS_20260106_001

[Add Expense: Transportasi]
Creating transaction from request:
  Type: PENGELUARAN
  Category from request: Transportasi
  Parsed to: Transportasi
  Category being saved: Transportasi
✓ Transaksi berhasil ditambahkan: TRANS_20260106_002

[Generate Report]
Should show:
- Makanan: Rp 100,000
- Transportasi: Rp 150,000
✓ Report correct
```

## Build Info
- Backend: ✅ Rebuilt with category logging
- Database: ✅ Deleted - fresh test ready
- Ready: ✅ Test with your transactions and report back console logs
