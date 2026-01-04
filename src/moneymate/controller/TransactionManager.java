package moneymate.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import moneymate.database.TransactionDAO;
import moneymate.database.TransactionDAOImpl;
import moneymate.exception.InsufficientBalanceException;
import moneymate.exception.InvalidTransactionException;
import moneymate.exception.TransactionNotFoundException;
import moneymate.interfaces.Calculable;
import moneymate.model.Category;
import moneymate.model.Expense;
import moneymate.model.Income;
import moneymate.model.Report;
import moneymate.model.Transaction;

/**
 * TransactionManager - mengelola semua transaksi
 * Implements Calculable untuk perhitungan saldo
 *
 * OOP Concepts:
 * - Dependency Injection: Uses TransactionDAO interface
 * - Encapsulation: Private DAO field
 * - Interface Implementation: Implements Calculable
 */
public class TransactionManager implements Calculable {

    private List<Transaction> transactions;
    private double initialBalance;
    private TransactionDAO transactionDAO;
    private String currentUserId; // Current logged-in user

    public TransactionManager() {
        this.transactions = new ArrayList<>();
        this.initialBalance = 0.0;
        this.transactionDAO = new TransactionDAOImpl();
        this.currentUserId = "DEFAULT_USER"; // Default for backward compatibility
        loadTransactionsFromDatabase();
    }

    public TransactionManager(double initialBalance) {
        this.transactions = new ArrayList<>();
        this.initialBalance = initialBalance;
        this.transactionDAO = new TransactionDAOImpl();
        this.currentUserId = "DEFAULT_USER";
        loadTransactionsFromDatabase();
    }
    
    /**
     * Set current user ID - used for multi-user support
     */
    public void setCurrentUserId(String userId) {
        if (userId != null && !userId.equals(this.currentUserId)) {
            this.currentUserId = userId;
            // Update DAO with current user
            if (transactionDAO instanceof TransactionDAOImpl) {
                ((TransactionDAOImpl) transactionDAO).setCurrentUserId(userId);
            }
            loadTransactionsFromDatabase(); // Reload transactions for this user
        }
    }
    
    /**
     * Get current user ID
     */
    public String getCurrentUserId() {
        return this.currentUserId;
    }

    /**
     * Load transactions dari database ke memory (filtered by user)
     */
    private void loadTransactionsFromDatabase() {
        try {
            List<Transaction> allTransactions = transactionDAO.findAll();
            // Filter by current user
            this.transactions = allTransactions.stream()
                .filter(t -> {
                    // Check if transaction belongs to current user
                    // This requires Transaction to have a userId field
                    return true; // For now, load all - will be filtered by DAO
                })
                .collect(Collectors.toList());
            System.out.println("✓ Loaded " + transactions.size() + " transactions from database for user: " + currentUserId);
        } catch (SQLException e) {
            System.err.println("Failed to load transactions: " + e.getMessage());
            this.transactions = new ArrayList<>();
        }
    }
    
    /**
     * Tambah transaksi baru (save to database)
     */
    public void addTransaction(Transaction transaction) throws InvalidTransactionException, InsufficientBalanceException {
        if (transaction == null) {
            throw new InvalidTransactionException("Transaksi tidak boleh null!");
        }

        if (!transaction.isValid()) {
            throw new InvalidTransactionException("Data transaksi tidak valid!");
        }

        // Validasi saldo untuk expense
        if (transaction instanceof Expense) {
            double currentBalance = calculateTotalBalance();
            if (currentBalance < transaction.getAmount()) {
                throw new InsufficientBalanceException(currentBalance, transaction.getAmount());
            }
        }

        try {
            transactionDAO.save(transaction);
            transactions.add(transaction);
            System.out.println("✓ Transaksi berhasil ditambahkan: " + transaction.getTransactionId());
        } catch (SQLException e) {
            throw new InvalidTransactionException("Gagal menyimpan ke database: " + e.getMessage());
        }
    }
    
    /**
     * Hapus transaksi berdasarkan ID (delete from database)
     */
    public void deleteTransaction(String transactionId) throws TransactionNotFoundException {
        Transaction toRemove = findTransactionById(transactionId);
        try {
            transactionDAO.delete(transactionId);
            transactions.remove(toRemove);
            System.out.println("✓ Transaksi berhasil dihapus: " + transactionId);
        } catch (SQLException e) {
            throw new TransactionNotFoundException("Gagal menghapus dari database: " + e.getMessage());
        }
    }
    
    /**
     * Update transaksi (update in database)
     */
    public void updateTransaction(String transactionId, Transaction updatedTransaction)
            throws TransactionNotFoundException, InvalidTransactionException {

        if (!updatedTransaction.isValid()) {
            throw new InvalidTransactionException("Data transaksi tidak valid!");
        }

        Transaction oldTransaction = findTransactionById(transactionId);
        int index = transactions.indexOf(oldTransaction);

        try {
            // Set transaction ID to match the old one
            java.lang.reflect.Field idField = Transaction.class.getDeclaredField("transactionId");
            idField.setAccessible(true);
            idField.set(updatedTransaction, transactionId);

            transactionDAO.update(updatedTransaction);
            transactions.set(index, updatedTransaction);
            System.out.println("✓ Transaksi berhasil diupdate: " + transactionId);
        } catch (SQLException e) {
            throw new InvalidTransactionException("Gagal update database: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidTransactionException("Gagal set transaction ID: " + e.getMessage());
        }
    }
    
    /**
     * Cari transaksi berdasarkan ID
     */
    public Transaction findTransactionById(String transactionId) throws TransactionNotFoundException {
        return transactions.stream()
            .filter(t -> t.getTransactionId().equals(transactionId))
            .findFirst()
            .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }
    
    /**
     * Get semua transaksi
     */
    @Override
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Get transaksi berdasarkan tipe
     */
    public List<Transaction> getTransactionsByType(Class<? extends Transaction> type) {
        return transactions.stream()
            .filter(type::isInstance)
            .collect(Collectors.toList());
    }
    
    /**
     * Get transaksi berdasarkan kategori
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        return transactions.stream()
            .filter(t -> t.getCategory() != null &&
                         t.getCategory().getDisplayName().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    /**
     * Get transaksi berdasarkan kategori (legacy - enum version)
     */
    @Deprecated
    public List<Transaction> getTransactionsByCategory(Category category) {
        return getTransactionsByCategory(category.getDisplayName());
    }
    
    /**
     * Get transaksi berdasarkan tanggal
     */
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactions.stream()
            .filter(t -> t.getDate().equals(date))
            .collect(Collectors.toList());
    }
    
    /**
     * Get transaksi dalam periode bulan tertentu
     */
    public List<Transaction> getTransactionsByMonth(YearMonth month) {
        return transactions.stream()
            .filter(t -> YearMonth.from(t.getDate()).equals(month))
            .collect(Collectors.toList());
    }
    
    // Implementation of Calculable interface
    
    @Override
    public double calculateTotalBalance() {
        return initialBalance + calculateTotalIncome() - calculateTotalExpense();
    }
    
    @Override
    public double calculateTotalIncome() {
        return transactions.stream()
            .filter(t -> t instanceof Income)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    @Override
    public double calculateTotalExpense() {
        return transactions.stream()
            .filter(t -> t instanceof Expense)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    /**
     * Generate laporan bulanan
     */
    public Report generateMonthlyReport(YearMonth month) {
        return new Report(transactions, month);
    }
    
    /**
     * Display semua transaksi
     */
    public void displayAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        
        System.out.println("\n========== DAFTAR TRANSAKSI ==========");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println("======================================");
        System.out.printf("Total: %d transaksi\n", transactions.size());
    }
    
    /**
     * Display summary saldo
     */
    public void displayBalanceSummary() {
        System.out.println("\n========== RINGKASAN SALDO ==========");
        System.out.printf("Saldo Awal       : Rp %,15.2f\n", initialBalance);
        System.out.printf("Total Pemasukan  : Rp %,15.2f\n", calculateTotalIncome());
        System.out.printf("Total Pengeluaran: Rp %,15.2f\n", calculateTotalExpense());
        System.out.println("-------------------------------------");
        System.out.printf("SALDO AKHIR      : Rp %,15.2f\n", calculateTotalBalance());
        System.out.println("=====================================\n");
    }
    
    public double getInitialBalance() {
        return initialBalance;
    }
    
    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }
}
