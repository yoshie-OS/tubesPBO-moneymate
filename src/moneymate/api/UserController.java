package moneymate.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import moneymate.controller.TransactionManager;
import moneymate.database.DatabaseManager;

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
     * POST /api/init - Initialize user and balance (register)
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initializeUser(@RequestBody InitRequest request) {
        // Generate unique user ID
        String userId = generateUserId(request.getUsername());
        
        // Check if username already exists
        if (userExists(request.getUsername())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Save user to database
        boolean saved = saveUserToDatabase(userId, request);
        
        if (!saved) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Failed to save user");
            return ResponseEntity.internalServerError().body(response);
        }
        
        // Set current user in transaction manager
        transactionManager.setCurrentUserId(userId);
        transactionManager.setInitialBalance(request.getInitialBalance());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User initialized");
        response.put("userId", userId);
        response.put("username", request.getUsername());
        response.put("email", request.getEmail());
        response.put("initialBalance", request.getInitialBalance());

        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/login - Login user
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        String sql = """
            SELECT user_id, username, email, initial_balance, password 
            FROM users 
            WHERE username = ?
        """;

        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getUsername());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    
                    // Simple password check (in production, use proper hashing!)
                    if (storedPassword != null && storedPassword.equals(request.getPassword())) {
                        String userId = rs.getString("user_id");
                        double initialBalance = rs.getDouble("initial_balance");
                        
                        // Set current user in transaction manager
                        transactionManager.setCurrentUserId(userId);
                        transactionManager.setInitialBalance(initialBalance);
                        
                        response.put("success", true);
                        response.put("userId", userId);
                        response.put("username", rs.getString("username"));
                        response.put("email", rs.getString("email"));
                        response.put("initialBalance", initialBalance);
                        
                        System.out.println("✓ User logged in: " + request.getUsername() + " (ID: " + userId + ")");
                        return ResponseEntity.ok(response);
                    }
                }
            }
            
            response.put("success", false);
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
            
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
            response.put("success", false);
            response.put("error", "Login failed");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Generate unique user ID based on username
     */
    private String generateUserId(String username) {
        return "USER_" + username.toUpperCase().replaceAll("[^A-Z0-9]", "") + "_" + System.currentTimeMillis();
    }
    
    /**
     * Check if username already exists
     */
    private boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to check user existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Save user data to database
     */
    private boolean saveUserToDatabase(String userId, InitRequest request) {
        String sql = """
            INSERT INTO users (user_id, username, email, password, initial_balance)
            VALUES (?, ?, ?, ?, ?)
        """;

        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, request.getUsername());
            pstmt.setString(3, request.getEmail());
            pstmt.setString(4, request.getPassword());
            pstmt.setDouble(5, request.getInitialBalance());

            pstmt.executeUpdate();
            System.out.println("✓ User saved to database: " + request.getUsername() + " (ID: " + userId + ")");
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to save user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static class InitRequest {
        private String username;
        private String email;
        private String password;
        private double initialBalance;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public double getInitialBalance() { return initialBalance; }
        public void setInitialBalance(double initialBalance) { this.initialBalance = initialBalance; }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
