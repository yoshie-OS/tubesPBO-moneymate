package moneymate.interfaces;

import moneymate.exception.FileExportException;

/**
 * Interface untuk export data ke berbagai format file
 */
public interface Exportable {
    
    /**
     * Export data ke file dengan format tertentu
     * @param filePath path file tujuan
     * @throws FileExportException jika terjadi error saat export
     */
    void exportToFile(String filePath) throws FileExportException;
    
    /**
     * Mendapatkan format export yang didukung
     * @return format file (CSV, TXT, JSON, dll)
     */
    String getExportFormat();
}
