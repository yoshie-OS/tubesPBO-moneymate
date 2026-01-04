package moneymate.api;

import moneymate.controller.TransactionManager;
import moneymate.model.*;
import moneymate.exception.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST API Controller for Transaction operations
 *
 * OOP Concepts:
 * - RESTful API design
 * - Dependency Injection (TransactionManager)
 * - Exception Handling
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionManager transactionManager;
    
    // Thread-local storage for current user ID (simple session management)
    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    // Spring Dependency Injection - shares singleton instance
    public TransactionController(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    /**
     * Set current user for the session
     */
    public void setCurrentUser(String userId) {
        currentUserId.set(userId);
        transactionManager.setCurrentUserId(userId);
    }
    
    /**
     * Get current user ID
     */
    private String getCurrentUserId() {
        String userId = currentUserId.get();
        if (userId == null) {
            userId = "DEFAULT_USER"; // Fallback for backward compatibility
        }
        return userId;
    }

    /**
     * GET /api/transactions - Get all transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionManager.getTransactions());
    }

    /**
     * POST /api/transactions - Add new transaction
     */
    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest request) {
        try {
            // Ensure user ID is set
            if (request.getUserId() != null) {
                setCurrentUser(request.getUserId());
            }
            
            Transaction transaction = createTransactionFromRequest(request);
            transactionManager.addTransaction(transaction);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transaction added successfully");
            response.put("transaction", transaction);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTransactionException | InsufficientBalanceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * DELETE /api/transactions/{id} - Delete transaction
     */
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String id) {
        try {
            transactionManager.deleteTransaction(id);
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Transaction deleted successfully");
            return ResponseEntity.ok(response);
        } catch (TransactionNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * GET /api/balance - Get balance summary
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Double>> getBalance() {
        Map<String, Double> balance = new HashMap<>();
        balance.put("totalBalance", transactionManager.calculateTotalBalance());
        balance.put("totalIncome", transactionManager.calculateTotalIncome());
        balance.put("totalExpense", transactionManager.calculateTotalExpense());
        balance.put("initialBalance", transactionManager.getInitialBalance());
        return ResponseEntity.ok(balance);
    }

    /**
     * GET /api/report/{month} - Get monthly report
     */
    @GetMapping("/report/{month}")
    public ResponseEntity<?> getMonthlyReport(@PathVariable String month) {
        try {
            YearMonth yearMonth = YearMonth.parse(month);
            Report report = transactionManager.generateMonthlyReport(yearMonth);

            Map<String, Object> response = new HashMap<>();
            response.put("month", month);
            response.put("totalIncome", report.getTotalIncome());
            response.put("totalExpense", report.getTotalExpense());
            response.put("balance", report.getBalance());
            response.put("expenseByCategory", report.getExpenseByCategory());
            response.put("incomeByCategory", report.getIncomeByCategory());
            response.put("summary", report.generateSummary());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid month format. Use YYYY-MM");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET /api/transactions/type/{type} - Filter by type
     */
    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@PathVariable String type) {
        if (type.equalsIgnoreCase("income")) {
            return ResponseEntity.ok(transactionManager.getTransactionsByType(Income.class));
        } else if (type.equalsIgnoreCase("expense")) {
            return ResponseEntity.ok(transactionManager.getTransactionsByType(Expense.class));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/categories - Get all categories (deprecated - now using free text)
     */
    @GetMapping("/categories")
    @Deprecated
    public ResponseEntity<Map<String, Category[]>> getCategories() {
        Map<String, Category[]> categories = new HashMap<>();
        categories.put("income", Category.getIncomeCategories());
        categories.put("expense", Category.getExpenseCategories());
        return ResponseEntity.ok(categories);
    }

    /**
     * Helper method to create Transaction from request
     * OOP Concept: Factory pattern
     */
    private Transaction createTransactionFromRequest(TransactionRequest request) {
        double amount = request.getAmount();
        String description = request.getDescription();
        LocalDate date = LocalDate.parse(request.getDate());
        boolean isIncome = request.getType().equalsIgnoreCase("income");
        String categoryStr = request.getCategory();
        
        // Convert String to Category enum
        Category category = parseCategory(categoryStr, isIncome);

        if (isIncome) {
            return new Income(amount, description, date, category, request.getSource());
        } else {
            return new Expense(amount, description, date, category,
                request.getPaymentMethod(), request.isRecurring());
        }
    }
    
    /**
     * Helper method to parse category string to Category enum
     */
    private Category parseCategory(String categoryStr, boolean isIncome) {
        if (categoryStr == null || categoryStr.trim().isEmpty()) {
            return isIncome ? Category.LAIN_LAIN_PEMASUKAN : Category.LAIN_LAIN_PENGELUARAN;
        }
        
        // Try to find by enum name first
        try {
            return Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to find by display name
            Category[] categories = isIncome ? Category.getIncomeCategories() : Category.getExpenseCategories();
            for (Category cat : categories) {
                if (cat.getDisplayName().equalsIgnoreCase(categoryStr)) {
                    return cat;
                }
            }
            // Default fallback
            return isIncome ? Category.LAIN_LAIN_PEMASUKAN : Category.LAIN_LAIN_PENGELUARAN;
        }
    }

    /**
     * Inner class for Transaction request body
     * OOP Concept: Encapsulation
     */
    public static class TransactionRequest {
        private String type;
        private double amount;
        private String description;
        private String date;
        private String category;
        private String source;
        private String paymentMethod;
        private boolean recurring;
        private String userId;

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public boolean isRecurring() { return recurring; }
        public void setRecurring(boolean recurring) { this.recurring = recurring; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}
