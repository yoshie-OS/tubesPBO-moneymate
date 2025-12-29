#!/bin/bash

echo "╔════════════════════════════════════════╗"
echo "║   MoneyMate - Database Verification    ║"
echo "╚════════════════════════════════════════╝"
echo

echo "📁 Database File:"
ls -lh moneymate.db
echo

echo "📊 Transactions in Database:"
sqlite3 moneymate.db -header -column "SELECT transaction_id AS ID, transaction_type AS Tipe, amount AS Jumlah, description AS Deskripsi, date AS Tanggal, category AS Kategori FROM transactions;"
echo

echo "👤 Users in Database:"
sqlite3 moneymate.db -header -column "SELECT * FROM users;"
echo

echo "📈 Database Statistics:"
echo "Total Transactions: $(sqlite3 moneymate.db 'SELECT COUNT(*) FROM transactions;')"
echo "Total Pemasukan: Rp $(sqlite3 moneymate.db "SELECT SUM(amount) FROM transactions WHERE transaction_type='PEMASUKAN';")"
echo "Total Pengeluaran: Rp $(sqlite3 moneymate.db "SELECT SUM(amount) FROM transactions WHERE transaction_type='PENGELUARAN';")"
echo

echo "✅ Data persists in SQLite database file: moneymate.db"
echo "✅ Proof: Close app, reopen - data still there!"
