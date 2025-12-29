package moneymate.exception;

/**
 * Exception yang dilempar ketika terjadi kesalahan saat export file
 */
public class FileExportException extends Exception {
    
    public FileExportException(String message) {
        super(message);
    }
    
    public FileExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
