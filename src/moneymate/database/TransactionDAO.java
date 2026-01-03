package moneymate.database;

import moneymate.model.Transaction;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * TransactionDAO - Data Access Object Interface
 *
 * OOP Concepts:
 * - Interface: Abstraksi operasi database
 * - Separation of Concerns: Memisahkan business logic dari data access
 * - Polymorphism: Implementasi berbeda bisa digunakan (SQLite, MySQL, dll)
 */
public interface TransactionDAO {

    /**
     * Save transaction ke database
     */
    void save(Transaction transaction) throws SQLException;

    /**
     * Update transaction di database
     */
    void update(Transaction transaction) throws SQLException;

    /**
     * Delete transaction berdasarkan ID
     */
    void delete(String transactionId) throws SQLException;

    /**
     * Find transaction by ID
     */
    Transaction findById(String transactionId) throws SQLException;

    /**
     * Get semua transactions
     */
    List<Transaction> findAll() throws SQLException;

    /**
     * Get transactions berdasarkan tipe
     */
    List<Transaction> findByType(String type) throws SQLException;

    /**
     * Get transactions berdasarkan tanggal
     */
    List<Transaction> findByDate(LocalDate date) throws SQLException;

    /**
     * Get transactions berdasarkan bulan
     */
    List<Transaction> findByMonth(YearMonth month) throws SQLException;

    /**
     * Delete semua transactions
     */
    void deleteAll() throws SQLException;
}
