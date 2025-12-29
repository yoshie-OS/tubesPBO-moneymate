package moneymate.exception;

/**
 * Exception yang dilempar ketika transaksi tidak valid
 * Contoh: jumlah negatif, data kosong, dll
 */
public class InvalidTransactionException extends Exception {
    
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
