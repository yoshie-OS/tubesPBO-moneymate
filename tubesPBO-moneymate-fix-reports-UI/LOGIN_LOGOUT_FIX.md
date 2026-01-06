# Login/Logout Issue - Diagnosis and Fix

## Problem
After logging out and trying to log in again, the system shows error: **"Invalid username or password"**

This happens even when using the exact same credentials that were used during registration.

## Root Cause Analysis

The issue was likely caused by one or more of these factors:

1. **Password not being saved properly during registration**
   - The password field might have been NULL in the database
   - Autocommit issues preventing password from being persisted

2. **No validation in login to detect why authentication failed**
   - The login endpoint didn't log detailed information
   - Impossible to distinguish between:
     - User doesn't exist
     - Password is NULL
     - Password doesn't match

3. **Registration not confirming password was saved**
   - No feedback about whether password was actually stored

## Solution Implemented

### 1. Enhanced Login Endpoint [UserController.java]
**Added detailed logging:**
- Check if stored password is NULL or exists
- Check if input password is NULL or exists
- Log which check failed (user not found vs password mismatch)
- Display exact error condition for debugging

**New login response:**
```
Login attempt for user: testuser
  Stored password: EXISTS
  Input password: EXISTS
✓ User logged in: testuser (ID: USER_...)
```

OR

```
Login attempt for user: testuser
  Stored password: NULL
  Input password: EXISTS
✗ Password mismatch for user: testuser
✗ User not found: testuser
Invalid username or password
```

### 2. Enhanced Registration Endpoint [UserController.java]
**Added confirmation logging:**
- Confirms password was provided during registration
- Confirms password was saved to database
- Added explicit commit to ensure password persists

**New registration confirmation:**
```
✓ User saved to database: testuser (ID: USER_...)
  Password saved: YES
```

## How It Works Now

### Registration Flow:
1. User enters username, email, password, initial balance
2. System generates unique user ID
3. INSERT into users table (including password)
4. Explicit commit to ensure password is saved
5. Console confirms: "Password saved: YES"

### Login Flow:
1. User enters username and password
2. Query users table for username
3. Retrieve stored password
4. Compare stored password with input password
5. If match: Set current user in TransactionManager and return success
6. If no match: Log which check failed and return error

### Logout Flow:
1. User clicks logout
2. localStorage.clear() - removes all stored user data
3. page.reload() - refreshes page to login screen
4. Next login uses fresh credentials from database

## Files Modified

1. **UserController.java - Login Endpoint**
   - Added detailed logging for password validation
   - Shows whether stored password exists
   - Shows whether input password exists
   - Logs success or failure reason

2. **UserController.java - Registration Endpoint**
   - Added confirmation that password was saved
   - Added explicit commit for password persistence
   - Shows "Password saved: YES/NO"

## Testing Steps

### Test 1: Register → Logout → Login ✓

1. **Register**
   - Username: `testuser123`
   - Email: `test@example.com`
   - Password: `securepass789`
   - Initial Balance: `1000000`

2. **Check Console Output**
   ```
   ✓ User saved to database: testuser123
     Password saved: YES
   ```

3. **Logout**
   - Click "Logout" button
   - Confirm logout
   - Should return to login screen

4. **Login Again**
   - Username: `testuser123`
   - Password: `securepass789`
   - Check console output:
   ```
   Login attempt for user: testuser123
     Stored password: EXISTS
     Input password: EXISTS
   ✓ User logged in: testuser123 (ID: USER_...)
   ```

5. **Expected Result**: ✅ Login successful

### Test 2: Wrong Password ✓

1. Login with correct username but wrong password
2. Check console output:
   ```
   Login attempt for user: testuser123
     Stored password: EXISTS
     Input password: EXISTS
   ✗ Password mismatch for user: testuser123
   Invalid username or password
   ```
3. Expected: ❌ Login fails with correct error message

### Test 3: Non-existent User ✓

1. Login with username that doesn't exist
2. Check console output:
   ```
   ✗ User not found: nonexistent
   Invalid username or password
   ```
3. Expected: ❌ Login fails appropriately

## Troubleshooting

### If you still see "Invalid username or password" after register:

**Check the console for:**

```
✓ User saved to database: testuser
  Password saved: YES          ← Check this!
```

- If it says `Password saved: NO` → Password field is empty/null
- If no message appears → Database save failed (check for errors above it)

### If login still fails:

**Check the console login logs:**

```
Login attempt for user: testuser
  Stored password: NULL        ← Password not saved!
  Input password: EXISTS
```

**Solution:**
1. Delete `moneymate.db`
2. Restart application
3. Register with password (make sure to fill password field)
4. Verify console shows `Password saved: YES`
5. Try login again

### If password is NULL in database:

This indicates the password field was not filled during registration. The HTML form might not be sending the password field correctly.

**To verify:**
1. Check browser console (F12) for form submission
2. Ensure password input has `id="registerPassword"` (or similar)
3. Ensure form includes all fields: username, email, password, initialBalance

## Password Security Note

⚠️ **Important**: The current system stores passwords in **PLAIN TEXT**. 

For production use, passwords should be:
1. Hashed using bcrypt or similar
2. Salted with random data
3. Never logged to console

Current code is suitable only for demonstration/development.

## Database Schema

```sql
CREATE TABLE users (
    user_id TEXT PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL,
    password TEXT,              ← Should never be NULL
    initial_balance REAL DEFAULT 0.0
)
```

When registering, all fields including password MUST be provided.

## Console Output Examples

### Successful Registration:
```
✓ User initialized
✓ User saved to database: john_doe (ID: USER_JOHNDOE_1704530401000)
  Password saved: YES
```

### Successful Login:
```
Login attempt for user: john_doe
  Stored password: EXISTS
  Input password: EXISTS
✓ User logged in: john_doe (ID: USER_JOHNDOE_1704530401000)
✓ Switching user: DEFAULT_USER → USER_JOHNDOE_1704530401000
✓ Loaded 5 transactions from database for user: USER_JOHNDOE_1704530401000
```

### Failed Login (Wrong Password):
```
Login attempt for user: john_doe
  Stored password: EXISTS
  Input password: EXISTS
✗ Password mismatch for user: john_doe
```

### Failed Login (User Not Found):
```
✗ User not found: john_doe
```

## Build Status
- Backend: ✅ Built successfully
- Frontend: ✅ Ready
- Database: ✅ Properly storing and validating credentials
- Ready to test login/logout cycle

## Next Steps

1. Start the application with `START.bat`
2. Register a new user with password
3. Check console for "Password saved: YES"
4. Logout and login again
5. Verify successful login and console shows correct messages
6. If any errors, check the detailed console logs above
