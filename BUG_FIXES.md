# Bug Fixes - MoneyMate Multi-User Support

## Bug yang Diperbaiki

### 🐛 Bug: Initial Balance Akun Tercampur Antar User
**Deskripsi:** Ketika membuat 2 akun berbeda dengan initial balance berbeda, setelah logout dan login kembali ke akun pertama, initial balance akun pertama berubah menjadi sama dengan akun kedua.

**Penyebab:**
1. **UserController.java** - Menggunakan fixed `user_id = "USER_001"` untuk semua user
2. **UserController.java** - Menggunakan `INSERT OR REPLACE` yang menimpa data user sebelumnya
3. **Database Schema** - Tidak ada constraint UNIQUE pada username
4. **TransactionManager** - Tidak ada filtering berdasarkan user_id
5. **TransactionDAO** - Tidak menyimpan dan memfilter transaksi per user
6. **Frontend** - Tidak menyimpan userId dengan proper dan tidak mengirim userId saat transaksi

## Perbaikan yang Dilakukan

### 1. Backend Changes

#### DatabaseManager.java
- ✅ Menambahkan kolom `password` dan constraint `UNIQUE` pada username di tabel users
- ✅ Menambahkan kolom `user_id` dengan FOREIGN KEY di tabel transactions

```java
// Sebelum
CREATE TABLE users (
    user_id TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    ...
)

// Sesudah
CREATE TABLE users (
    user_id TEXT PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT,
    ...
)

CREATE TABLE transactions (
    ...
    user_id TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
)
```

#### UserController.java
- ✅ Menambahkan fungsi `generateUserId()` untuk generate unique user ID
- ✅ Menambahkan fungsi `userExists()` untuk cek username sudah ada atau belum
- ✅ Mengubah `INSERT OR REPLACE` menjadi `INSERT` biasa
- ✅ Menambahkan endpoint `/api/login` untuk autentikasi user
- ✅ Set user session di TransactionManager saat login/register
- ✅ Menambahkan password field di InitRequest dan LoginRequest

#### TransactionManager.java
- ✅ Menambahkan field `currentUserId` untuk tracking user yang sedang login
- ✅ Menambahkan method `setCurrentUserId()` untuk set user session
- ✅ Update DAO dengan userId saat user berubah
- ✅ Reload transactions saat user berubah

#### TransactionDAOImpl.java
- ✅ Menambahkan field `currentUserId` untuk filtering
- ✅ Menambahkan method `setCurrentUserId()`
- ✅ Update semua query SQL untuk include `user_id` filter:
  - `save()` - Insert dengan user_id
  - `update()` - Update hanya transaksi milik user
  - `delete()` - Delete hanya transaksi milik user
  - `findById()` - Find dengan filter user_id
  - `findAll()` - Load hanya transaksi user
  - `findByType()`, `findByDate()`, `findByMonth()` - Semua dengan filter user_id
  - `deleteAll()` - Delete hanya transaksi user

#### TransactionController.java
- ✅ Menambahkan ThreadLocal `currentUserId` untuk session management
- ✅ Menambahkan method `setCurrentUser()` dan `getCurrentUserId()`
- ✅ Update `addTransaction()` untuk set userId dari request
- ✅ Menambahkan field `userId` di TransactionRequest class

### 2. Frontend Changes

#### app.ts
- ✅ Update `login()` method untuk call backend `/api/login` endpoint
- ✅ Store user data (termasuk userId) di `localStorage.currentUser`
- ✅ Update `register()` untuk store userId dari response
- ✅ Update `addTransaction()` untuk include userId dari localStorage
- ✅ Update `logout()` untuk clear semua localStorage
- ✅ Check `currentUser` instead of `userSetup` pada init

## Testing

### Cara Test Bug Fix:

1. **Build Backend:**
   ```bash
   mvn clean package
   ```

2. **Build Frontend:**
   ```bash
   cd frontend
   npm run build
   ```

3. **Hapus Database Lama (Penting!):**
   ```bash
   # Di Windows
   del moneymate.db
   
   # Di Linux/Mac
   rm moneymate.db
   ```
   Database harus dihapus karena schema berubah (ada kolom baru)

4. **Start Application:**
   ```bash
   # Windows
   START.bat
   
   # Linux/Mac
   ./START.sh
   ```

5. **Test Scenario:**
   - Buka http://localhost:8080
   - Buat akun 1: username="user1", password="pass1", initial balance=200000000
   - Logout
   - Buat akun 2: username="user2", password="pass2", initial balance=35000
   - Logout
   - Login ke akun 1 dengan username="user1", password="pass1"
   - ✅ Initial balance harus tetap 200000000
   - Buat transaksi di akun 1
   - Logout dan login ke akun 2
   - ✅ Transaksi akun 1 tidak muncul di akun 2
   - ✅ Initial balance akun 2 tetap 35000

## Breaking Changes

⚠️ **PENTING:** Database schema berubah, perlu hapus file `moneymate.db` sebelum menjalankan aplikasi yang sudah di-fix.

## Catatan Keamanan

⚠️ **Password Storage:** Saat ini password disimpan dalam plain text. Untuk production, harus menggunakan password hashing (BCrypt, Argon2, dll).

## File yang Diubah

1. `src/moneymate/database/DatabaseManager.java`
2. `src/moneymate/api/UserController.java`
3. `src/moneymate/api/TransactionController.java`
4. `src/moneymate/controller/TransactionManager.java`
5. `src/moneymate/database/TransactionDAOImpl.java`
6. `frontend/src/app.ts`

## Summary

Bug telah diperbaiki dengan implementasi proper multi-user support:
- ✅ Setiap user memiliki unique userId
- ✅ Username harus unique (tidak boleh duplikat)
- ✅ Transaksi di-filter per user
- ✅ Initial balance disimpan per user
- ✅ Session management untuk track user yang sedang login
- ✅ Login/Logout functionality dengan password
