package moneymate.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Singleton pattern untuk koneksi database SQLite
 *
 * OOP Concepts:
 * - Singleton Pattern: Hanya satu instance DatabaseManager
 * - Encapsulation: Private constructor, controlled access
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:sqlite:moneymate.db";
    private Connection connection;

    /**
     * Private constructor - mencegah instantiasi langsung (Singleton)
     */
    private DatabaseManager() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
            System.out.println("✓ Database connection established");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    /**
     * Get singleton instance (Singleton Pattern)
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            // Reopen connection if closed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get connection: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Initialize database tables
     */
    private void initializeDatabase() {
        String createTransactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id TEXT PRIMARY KEY,
                transaction_type TEXT NOT NULL,
                amount REAL NOT NULL,
                description TEXT,
                date TEXT NOT NULL,
                category TEXT NOT NULL,
                source TEXT,
                payment_method TEXT,
                is_recurring INTEGER DEFAULT 0
            )
        """;

        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                email TEXT NOT NULL,
                initial_balance REAL DEFAULT 0.0
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTransactionsTable);
            stmt.execute(createUsersTable);
            System.out.println("✓ Database tables initialized");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}
