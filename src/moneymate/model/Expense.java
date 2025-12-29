package moneymate.model;

import java.time.LocalDate;

/**
 * Class Expense - merepresentasikan transaksi pengeluaran
 * Inheritance dari Transaction
 */
public class Expense extends Transaction {
    
    private String paymentMethod; // metode pembayaran (Cash, Debit, Credit, E-Wallet)
    private boolean isRecurring; // apakah pengeluaran berulang (tagihan bulanan, dll)

    public Expense(double amount, String description, LocalDate date, String category,
                   String paymentMethod, boolean isRecurring) {
        super(amount, description, date, category);
        this.paymentMethod = paymentMethod;
        this.isRecurring = isRecurring;
    }

    public Expense(double amount, String description, LocalDate date, String category) {
        this(amount, description, date, category, "Cash", false);
    }

    // Legacy constructor for compatibility with old Category enum
    @Deprecated
    public Expense(double amount, String description, LocalDate date, Category category,
                   String paymentMethod, boolean isRecurring) {
        this(amount, description, date, category.getDisplayName(), paymentMethod, isRecurring);
    }

    @Deprecated
    public Expense(double amount, String description, LocalDate date, Category category) {
        this(amount, description, date, category.getDisplayName(), "Cash", false);
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public boolean isRecurring() {
        return isRecurring;
    }
    
    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
    
    @Override
    public String getTransactionType() {
        return "PENGELUARAN";
    }
    
    @Override
    public boolean isValid() {
        return amount > 0 && description != null && !description.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        String recurring = isRecurring ? " [BERULANG]" : "";
        return super.toString() + " | Pembayaran: " + paymentMethod + recurring;
    }
}
