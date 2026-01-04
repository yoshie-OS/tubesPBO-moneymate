package moneymate.model;

import java.time.LocalDate;

/**
 * Class Income - merepresentasikan transaksi pemasukan
 * Inheritance dari Transaction
 */
public class Income extends Transaction {
    
    private String source; // sumber pemasukan

    public Income(double amount, String description, LocalDate date, Category category, String source) {
        super(amount, description, date, category);
        this.source = source;
    }

    public Income(double amount, String description, LocalDate date, String categoryName, String source) {
        this(amount, description, date, Category.fromString(categoryName, true), source);
    }

    public Income(double amount, String description, LocalDate date, Category category) {
        this(amount, description, date, category, "Tidak disebutkan");
    }

    public Income(double amount, String description, LocalDate date, String categoryName) {
        this(amount, description, date, Category.fromString(categoryName, true));
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    @Override
    public String getTransactionType() {
        return "PEMASUKAN";
    }
    
    @Override
    public boolean isValid() {
        return amount > 0 && description != null && !description.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return super.toString() + " | Sumber: " + source;
    }
}
