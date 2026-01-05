package moneymate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import moneymate.model.Category;
import moneymate.model.Expense;
import moneymate.model.Income;
import moneymate.model.Transaction;

/**
 * TransactionDAOImpl - Implementasi TransactionDAO untuk SQLite
 *
 * OOP Concepts:
 * - Interface Implementation: Implements TransactionDAO
 * - Encapsulation: Private helper methods
 * - Polymorphism: Factory pattern untuk create Income/Expense dari ResultSet
 */
public class TransactionDAOImpl implements TransactionDAO {

    private final Connection connection;
    private String currentUserId = "DEFAULT_USER"; // For backward compatibility

    public TransactionDAOImpl() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }
    
    /**
     * Set current user ID for filtering transactions
     */
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId != null ? userId : "DEFAULT_USER";
    }

    @Override
    public void save(Transaction transaction) throws SQLException {
        String sql = """
            INSERT INTO transactions (
                transaction_id, user_id, transaction_type, amount, description,
                date, category, source, payment_method, is_recurring
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, currentUserId);
            pstmt.setString(3, transaction.getTransactionType());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setString(6, transaction.getDate().toString());
            pstmt.setString(7, transaction.getCategory() != null ? transaction.getCategory().getDisplayName() : null);

            // Polymorphism: Handle Income vs Expense specific fields
            if (transaction instanceof Income) {
                Income income = (Income) transaction;
                pstmt.setString(8, income.getSource());
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setInt(10, 0);
            } else if (transaction instanceof Expense) {
                Expense expense = (Expense) transaction;
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setString(9, expense.getPaymentMethod());
                pstmt.setInt(10, expense.isRecurring() ? 1 : 0);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Transaction transaction) throws SQLException {
        String sql = """
            UPDATE transactions SET
                transaction_type = ?, amount = ?, description = ?,
                date = ?, category = ?, source = ?,
                payment_method = ?, is_recurring = ?
            WHERE transaction_id = ? AND user_id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getTransactionType());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getDescription());
            pstmt.setString(4, transaction.getDate().toString());
            pstmt.setString(5, transaction.getCategory() != null ? transaction.getCategory().getDisplayName() : null);

            if (transaction instanceof Income) {
                Income income = (Income) transaction;
                pstmt.setString(6, income.getSource());
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setInt(8, 0);
            } else if (transaction instanceof Expense) {
                Expense expense = (Expense) transaction;
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setString(7, expense.getPaymentMethod());
                pstmt.setInt(8, expense.isRecurring() ? 1 : 0);
            }

            pstmt.setString(9, transaction.getTransactionId());
            pstmt.setString(10, currentUserId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            pstmt.setString(2, currentUserId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Transaction findById(String transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            pstmt.setString(2, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return createTransactionFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByType(String type) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_type = ? AND user_id = ? ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setString(2, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE date = ? AND user_id = ?";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, date.toString());
            pstmt.setString(2, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByMonth(YearMonth month) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE date LIKE ? AND user_id = ? ORDER BY date DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, month.toString() + "%");
            pstmt.setString(2, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
        }
        return transactions;
    }

    @Override
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM transactions WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, currentUserId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Factory Method Pattern: Create Transaction object dari ResultSet
     * OOP Concept: Polymorphism - Returns Income atau Expense based on type
     */
    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("transaction_type");
        double amount = rs.getDouble("amount");
        String description = rs.getString("description");
        LocalDate date = LocalDate.parse(rs.getString("date"));
        String category = rs.getString("category");
        boolean isIncome = type.equals("PEMASUKAN");
        Category parsedCategory = Category.fromString(category, isIncome);

        Transaction transaction;

        if (type.equals("PEMASUKAN")) {
            String source = rs.getString("source");
            transaction = new Income(amount, description, date, parsedCategory, source);
        } else {
            String paymentMethod = rs.getString("payment_method");
            boolean isRecurring = rs.getInt("is_recurring") == 1;
            transaction = new Expense(amount, description, date, parsedCategory, paymentMethod, isRecurring);
        }

        // Set the original transaction ID
        try {
            java.lang.reflect.Field idField = Transaction.class.getDeclaredField("transactionId");
            idField.setAccessible(true);
            idField.set(transaction, rs.getString("transaction_id"));
        } catch (Exception e) {
            // If reflection fails, keep the auto-generated ID
        }

        return transaction;
    }
}
