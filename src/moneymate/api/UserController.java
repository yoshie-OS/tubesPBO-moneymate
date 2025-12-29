package moneymate.api;

import moneymate.controller.TransactionManager;
import moneymate.database.DatabaseManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * REST API Controller for User operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final TransactionManager transactionManager;

    // Spring Dependency Injection - shares singleton instance
    public UserController(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * POST /api/init - Initialize user and balance
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initializeUser(@RequestBody InitRequest request) {
        transactionManager.setInitialBalance(request.getInitialBalance());

        // Save user to database
        saveUserToDatabase(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User initialized");
        response.put("initialBalance", request.getInitialBalance());

        return ResponseEntity.ok(response);
    }

    /**
     * Save user data to database
     */
    private void saveUserToDatabase(InitRequest request) {
        String sql = """
            INSERT OR REPLACE INTO users (user_id, username, email, initial_balance)
            VALUES (?, ?, ?, ?)
        """;

        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Use a fixed user_id (since we only support single user for now)
            pstmt.setString(1, "USER_001");
            pstmt.setString(2, request.getUsername());
            pstmt.setString(3, request.getEmail());
            pstmt.setDouble(4, request.getInitialBalance());

            pstmt.executeUpdate();
            System.out.println("✓ User saved to database: " + request.getUsername());

        } catch (SQLException e) {
            System.err.println("Failed to save user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class InitRequest {
        private String username;
        private String email;
        private double initialBalance;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public double getInitialBalance() { return initialBalance; }
        public void setInitialBalance(double initialBalance) { this.initialBalance = initialBalance; }
    }
}
