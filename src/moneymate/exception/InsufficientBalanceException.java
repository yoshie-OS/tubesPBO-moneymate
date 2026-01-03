package moneymate.exception;

/**
 * Exception yang dilempar ketika saldo tidak mencukupi
 * untuk melakukan transaksi pengeluaran
 */
public class InsufficientBalanceException extends Exception {
    
    private double currentBalance;
    private double requestedAmount;
    
    public InsufficientBalanceException(double currentBalance, double requestedAmount) {
        super(String.format("Saldo tidak mencukupi! Saldo saat ini: Rp %.2f, Jumlah yang diminta: Rp %.2f", 
                          currentBalance, requestedAmount));
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }
    
    public double getCurrentBalance() {
        return currentBalance;
    }
    
    public double getRequestedAmount() {
        return requestedAmount;
    }
}
