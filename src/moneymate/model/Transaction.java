package moneymate.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract class Transaction - parent class untuk Income dan Expense
 */
public abstract class Transaction {
    
    protected String transactionId;
    protected double amount;
    protected String description;
    protected LocalDate date;
    protected String category;

    protected static int transactionCounter = 0;

    public Transaction(double amount, String description, LocalDate date, String category) {
        this.transactionId = generateTransactionId();
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        transactionCounter++;
        return String.format("TRX%05d", transactionCounter);
    }
    
    // Getters
    public String getTransactionId() {
        return transactionId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getCategory() {
        return category;
    }

    // Setters
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * Abstract method - setiap jenis transaksi punya tipe berbeda
     */
    public abstract String getTransactionType();
    
    /**
     * Abstract method - untuk validasi transaksi
     */
    public abstract boolean isValid();
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[%s] %s - %s: Rp %.2f (%s) - %s", 
                           transactionId,
                           getTransactionType(),
                           date.format(formatter),
                           amount,
                           category,
                           description);
    }
}
