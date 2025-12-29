# MoneyMate - User Manual

## 📘 Panduan Pengguna MoneyMate

### Daftar Isi
1. [Memulai Aplikasi](#memulai-aplikasi)
2. [Setup User](#setup-user)
3. [Menu Utama](#menu-utama)
4. [Fitur-Fitur](#fitur-fitur)
5. [Tips & Trik](#tips--trik)
6. [Troubleshooting](#troubleshooting)

---

## Memulai Aplikasi

### Menjalankan MoneyMate

**Menggunakan Command Line:**
```bash
# Compile terlebih dahulu
javac -d bin src/moneymate/**/*.java

# Jalankan aplikasi
java -cp bin moneymate.view.MoneyMateApp
```

**Menggunakan IDE:**
1. Buka project di IDE (IntelliJ IDEA/Eclipse/VS Code)
2. Cari file `MoneyMateApp.java` di folder `src/moneymate/view/`
3. Klik kanan → Run

---

## Setup User

Saat pertama kali menjalankan aplikasi, Anda akan diminta untuk setup user:

```
=== SETUP USER ===
Masukkan username: [Nama Anda]
Masukkan saldo awal (Rp): [Jumlah saldo awal Anda]
```

**Catatan:**
- Username wajib diisi
- Saldo awal bisa 0 atau lebih
- Data ini akan digunakan untuk perhitungan saldo

---

## Menu Utama

```
╔════════════════════════════════════════╗
║            MENU UTAMA                  ║
╠════════════════════════════════════════╣
║ 1. Tambah Transaksi                    ║
║ 2. Lihat Semua Transaksi               ║
║ 3. Ubah Transaksi                      ║
║ 4. Hapus Transaksi                     ║
║ 5. Lihat Saldo                         ║
║ 6. Laporan Bulanan                     ║
║ 7. Export Laporan                      ║
║ 8. Filter Transaksi                    ║
║ 0. Keluar                              ║
╚════════════════════════════════════════╝
```

---

## Fitur-Fitur

### 1. Tambah Transaksi

#### Menambah Pemasukan
1. Pilih menu **1. Tambah Transaksi**
2. Pilih **1. Pemasukan**
3. Masukkan jumlah (contoh: 5000000)
4. Masukkan deskripsi (contoh: "Gaji bulan Januari")
5. Masukkan tanggal (format: dd/MM/yyyy) atau tekan Enter untuk hari ini
6. Pilih kategori:
   - 1. Gaji
   - 2. Bonus
   - 3. Investasi
   - 4. Lain-lain
7. Masukkan sumber pemasukan (contoh: "PT ABC")

**Contoh:**
```
=== TAMBAH TRANSAKSI ===
1. Pemasukan
2. Pengeluaran
Pilih tipe (1-2): 1
Masukkan jumlah (Rp): 5000000
Deskripsi: Gaji bulan Januari
Tanggal (dd/MM/yyyy) [Enter untuk hari ini]: 
Pilih Kategori:
1. Gaji
2. Bonus
3. Investasi
4. Lain-lain
Pilihan: 1
Sumber pemasukan: PT ABC
✓ Transaksi berhasil ditambahkan: TRX00001
```

#### Menambah Pengeluaran
1. Pilih menu **1. Tambah Transaksi**
2. Pilih **2. Pengeluaran**
3. Masukkan jumlah
4. Masukkan deskripsi
5. Masukkan tanggal atau tekan Enter
6. Pilih kategori pengeluaran
7. Pilih metode pembayaran (Cash/Debit/Credit/E-Wallet)
8. Apakah pengeluaran berulang? (y/n)

**Catatan:**
- Sistem akan mengecek saldo sebelum menambah pengeluaran
- Jika saldo tidak mencukupi, transaksi akan ditolak

---

### 2. Lihat Semua Transaksi

Menampilkan semua transaksi yang pernah dicatat.

**Output:**
```
========== DAFTAR TRANSAKSI ==========
[TRX00001] PEMASUKAN - 10/11/2025: Rp 5,000,000.00 (Gaji) - Gaji bulan Januari | Sumber: PT ABC
[TRX00002] PENGELUARAN - 10/11/2025: Rp 50,000.00 (Makanan) - Makan siang | Pembayaran: Cash
======================================
Total: 2 transaksi
```

---

### 3. Ubah Transaksi

1. Pilih menu **3. Ubah Transaksi**
2. Lihat daftar transaksi
3. Masukkan ID transaksi yang ingin diubah
4. Masukkan data baru (jumlah, deskripsi, tanggal, kategori)

**Catatan:**
- Tipe transaksi (Income/Expense) tidak bisa diubah
- Untuk mengubah tipe, hapus dan buat transaksi baru

---

### 4. Hapus Transaksi

1. Pilih menu **4. Hapus Transaksi**
2. Lihat daftar transaksi
3. Masukkan ID transaksi yang ingin dihapus
4. Konfirmasi dengan 'y' atau batalkan dengan 'n'

**Peringatan:**
- Penghapusan tidak dapat dibatalkan
- Pastikan ID transaksi benar sebelum konfirmasi

---

### 5. Lihat Saldo

Menampilkan ringkasan keuangan:

```
========== RINGKASAN SALDO ==========
Saldo Awal       : Rp      10,000,000.00
Total Pemasukan  : Rp       5,000,000.00
Total Pengeluaran: Rp       2,500,000.00
-------------------------------------
SALDO AKHIR      : Rp      12,500,000.00
=====================================
```

---

### 6. Laporan Bulanan

Generate laporan keuangan untuk bulan tertentu.

**Cara Penggunaan:**
1. Pilih menu **6. Laporan Bulanan**
2. Masukkan bulan (format: MM/yyyy) atau Enter untuk bulan ini
3. Lihat laporan lengkap dengan breakdown per kategori

**Output:**
```
========================================
       LAPORAN KEUANGAN BULANAN
========================================
Periode: November 2025
----------------------------------------
Total Pemasukan  : Rp   5,000,000.00
Total Pengeluaran: Rp   2,500,000.00
----------------------------------------
SALDO            : Rp   2,500,000.00
========================================

Pengeluaran per Kategori:
----------------------------------------
Makanan             : Rp   1,000,000.00 (40.0%)
Transportasi        : Rp     500,000.00 (20.0%)
Hiburan             : Rp   1,000,000.00 (40.0%)
========================================
```

---

### 7. Export Laporan

Export semua transaksi ke file.

**Format yang Tersedia:**
1. **CSV** - untuk dibuka di Excel/Spreadsheet
2. **TXT** - untuk dokumentasi text

**Cara Penggunaan:**
1. Pilih menu **7. Export Laporan**
2. Pilih format (1: CSV atau 2: TXT)
3. File akan disimpan di folder `exports/` dengan nama:
   - `MoneyMate_Report_[tanggal].csv`
   - `MoneyMate_Report_[tanggal].txt`

**Lokasi File:**
```
Tubes_PBO/
└── exports/
    ├── MoneyMate_Report_2025-11-10.csv
    └── MoneyMate_Report_2025-11-10.txt
```

---

### 8. Filter Transaksi

Filter dan cari transaksi berdasarkan kriteria tertentu.

**Pilihan Filter:**
1. **Filter by Tipe**: Lihat hanya Pemasukan atau Pengeluaran
2. **Filter by Kategori**: Lihat transaksi kategori tertentu
3. **Filter by Tanggal**: Lihat transaksi di tanggal tertentu
4. **Filter by Bulan**: Lihat transaksi dalam bulan tertentu

**Contoh - Filter by Kategori:**
```
=== FILTER TRANSAKSI ===
1. Filter by Tipe (Income/Expense)
2. Filter by Kategori
3. Filter by Tanggal
4. Filter by Bulan
Pilih filter (1-4): 2

Pilih tipe kategori:
1. Kategori Pemasukan
2. Kategori Pengeluaran
Pilih: 2

Pilih Kategori:
1. Makanan
2. Transportasi
3. Hiburan
...
Pilihan: 1

=== HASIL FILTER ===
[TRX00002] PENGELUARAN - 10/11/2025: Rp 50,000.00 (Makanan) - Makan siang
[TRX00005] PENGELUARAN - 10/11/2025: Rp 100,000.00 (Makanan) - Groceries

Total: 2 transaksi
```

---

## Tips & Trik

### 💡 Tips Pencatatan yang Baik

1. **Catat Segera**
   - Catat transaksi segera setelah terjadi
   - Jangan menunda-nunda pencatatan

2. **Deskripsi Jelas**
   - Gunakan deskripsi yang detail
   - Contoh: "Makan siang di Restoran X" lebih baik dari "Makan"

3. **Kategori Konsisten**
   - Gunakan kategori yang sama untuk jenis pengeluaran yang sama
   - Memudahkan analisis di laporan bulanan

4. **Review Rutin**
   - Lihat laporan bulanan setiap akhir bulan
   - Identifikasi pos pengeluaran terbesar

5. **Backup Data**
   - Export laporan secara berkala
   - Simpan file CSV/TXT sebagai backup

### 🎯 Shortcuts & Quick Actions

- **Tanggal**: Tekan Enter untuk menggunakan tanggal hari ini
- **Bulan**: Tekan Enter untuk menggunakan bulan berjalan
- **Menu 0**: Langsung keluar dari aplikasi

---

## Troubleshooting

### ❌ Error: "Saldo tidak mencukupi"

**Penyebab:** Jumlah pengeluaran melebihi saldo yang tersedia

**Solusi:**
- Periksa saldo Anda (Menu 5)
- Kurangi jumlah pengeluaran
- Atau tambah pemasukan terlebih dahulu

---

### ❌ Error: "Transaksi tidak ditemukan"

**Penyebab:** ID transaksi yang dimasukkan salah atau tidak ada

**Solusi:**
- Lihat semua transaksi (Menu 2)
- Copy ID transaksi dengan benar
- ID berbentuk: TRX00001, TRX00002, dst.

---

### ❌ Error: "Data transaksi tidak valid"

**Penyebab:** Input data tidak sesuai format

**Solusi:**
- Jumlah harus angka positif (> 0)
- Deskripsi tidak boleh kosong
- Tanggal harus format dd/MM/yyyy (contoh: 10/11/2025)
- Bulan harus format MM/yyyy (contoh: 11/2025)

---

### ❌ Error: "Gagal menulis file"

**Penyebab:** Tidak ada izin menulis atau folder tidak ada

**Solusi:**
- Pastikan folder `exports/` ada
- Pastikan aplikasi punya izin write
- Tutup file jika sedang dibuka

---

### ⚠️ Input Tidak Diterima

**Penyebab:** Format input salah

**Solusi:**
- **Angka**: Hanya masukkan angka tanpa titik/koma (gunakan 5000000, bukan 5.000.000)
- **Tanggal**: Gunakan format dd/MM/yyyy dengan slash (/)
- **Pilihan Menu**: Hanya masukkan angka sesuai pilihan yang tersedia

---

## Format Data

### Format Tanggal
```
Format: dd/MM/yyyy
Contoh: 10/11/2025 (10 November 2025)
```

### Format Bulan
```
Format: MM/yyyy
Contoh: 11/2025 (November 2025)
```

### Format Jumlah
```
Input: angka tanpa separator
Contoh: 5000000 (untuk Rp 5.000.000)

Output: akan ditampilkan dengan separator
Tampilan: Rp 5,000,000.00
```

---

## Kategori Transaksi

### Kategori Pemasukan
- **Gaji**: Pendapatan tetap bulanan
- **Bonus**: Bonus, THR, incentive
- **Investasi**: Return investasi, dividen
- **Lain-lain**: Pemasukan lainnya

### Kategori Pengeluaran
- **Makanan**: Makan, groceries, snack
- **Transportasi**: Bensin, parkir, tol, transport umum
- **Hiburan**: Nonton, jalan-jalan, hobi
- **Belanja**: Shopping pakaian, elektronik
- **Tagihan**: Listrik, air, internet, pulsa
- **Kesehatan**: Dokter, obat, vitamin
- **Pendidikan**: Kursus, buku, sekolah
- **Lain-lain**: Pengeluaran lainnya

---

## FAQ (Frequently Asked Questions)

### Q: Apakah data tersimpan permanen?
**A:** Untuk versi console ini, data hanya tersimpan selama aplikasi berjalan. Gunakan fitur export untuk backup data Anda.

### Q: Bisa multi-user?
**A:** Versi saat ini untuk single user. Multi-user sudah dirancang di abstract class User untuk pengembangan future.

### Q: Bisa edit kategori?
**A:** Kategori sudah ditentukan di enum Category. Untuk custom kategori perlu modifikasi code.

### Q: Maksimal berapa transaksi?
**A:** Tidak ada batasan, tergantung memory komputer Anda.

### Q: Format export bisa PDF?
**A:** Saat ini hanya CSV dan TXT. PDF adalah future development.

---

## Kontak & Support

Jika menemui masalah atau punya pertanyaan:

📧 Email: [email-kelompok]@university.edu
🐛 Bug Report: [GitHub Issues]
📚 Dokumentasi: Lihat PROPOSAL.md dan README.md

---

**MoneyMate User Manual v1.0**  
© 2025 MoneyMate Development Team

---

Selamat menggunakan MoneyMate! 💰✨
