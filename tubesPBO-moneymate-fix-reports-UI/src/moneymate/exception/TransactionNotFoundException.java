package moneymate.exception;

/**
 * Exception yang dilempar ketika transaksi yang dicari tidak ditemukan
 */
public class TransactionNotFoundException extends Exception {
    
    private String transactionId;
    
    public TransactionNotFoundException(String transactionId) {
        super("Transaksi dengan ID '" + transactionId + "' tidak ditemukan!");
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
}
