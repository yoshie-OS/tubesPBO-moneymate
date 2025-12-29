package moneymate.util;

import moneymate.model.*;
import moneymate.controller.TransactionManager;
import moneymate.exception.FileExportException;
import moneymate.interfaces.Exportable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * FileExporter - untuk export data transaksi ke file
 * Implements Exportable interface
 */
public class FileExporter implements Exportable {
    
    private TransactionManager transactionManager;
    private String format;
    
    public FileExporter(TransactionManager transactionManager, String format) {
        this.transactionManager = transactionManager;
        this.format = format.toUpperCase();
    }
    
    @Override
    public void exportToFile(String filePath) throws FileExportException {
        try {
            switch (format) {
                case "CSV":
                    exportToCSV(filePath);
                    break;
                case "TXT":
                    exportToTXT(filePath);
                    break;
                default:
                    throw new FileExportException("Format tidak didukung: " + format);
            }
            System.out.println("✓ File berhasil di-export ke: " + filePath);
        } catch (IOException e) {
            throw new FileExportException("Gagal menulis file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Export ke format CSV
     */
    private void exportToCSV(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("ID,Tipe,Tanggal,Kategori,Deskripsi,Jumlah,Detail");
            
            // Data
            List<Transaction> transactions = transactionManager.getTransactions();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Transaction t : transactions) {
                String detail = "";
                if (t instanceof Income) {
                    detail = "Sumber: " + ((Income) t).getSource();
                } else if (t instanceof Expense) {
                    Expense exp = (Expense) t;
                    detail = "Pembayaran: " + exp.getPaymentMethod() + 
                            (exp.isRecurring() ? " [BERULANG]" : "");
                }
                
                writer.printf("%s,%s,%s,%s,\"%s\",%.2f,\"%s\"\n",
                    t.getTransactionId(),
                    t.getTransactionType(),
                    t.getDate().format(formatter),
                    t.getCategory(),
                    t.getDescription(),
                    t.getAmount(),
                    detail
                );
            }
            
            // Summary
            writer.println();
            writer.printf("TOTAL PEMASUKAN,,,,,%,.2f,\n", transactionManager.calculateTotalIncome());
            writer.printf("TOTAL PENGELUARAN,,,,,%,.2f,\n", transactionManager.calculateTotalExpense());
            writer.printf("SALDO AKHIR,,,,,%,.2f,\n", transactionManager.calculateTotalBalance());
        }
    }
    
    /**
     * Export ke format TXT
     */
    private void exportToTXT(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("========================================");
            writer.println("       LAPORAN TRANSAKSI MONEYMATE");
            writer.println("========================================");
            writer.println("Tanggal Export: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")));
            writer.println("========================================\n");
            
            List<Transaction> transactions = transactionManager.getTransactions();
            
            if (transactions.isEmpty()) {
                writer.println("Tidak ada transaksi.");
            } else {
                writer.println("DAFTAR TRANSAKSI:\n");
                for (Transaction t : transactions) {
                    writer.println(t);
                    writer.println();
                }
            }
            
            writer.println("========================================");
            writer.println("RINGKASAN KEUANGAN");
            writer.println("========================================");
            writer.printf("Saldo Awal       : Rp %,15.2f\n", transactionManager.getInitialBalance());
            writer.printf("Total Pemasukan  : Rp %,15.2f\n", transactionManager.calculateTotalIncome());
            writer.printf("Total Pengeluaran: Rp %,15.2f\n", transactionManager.calculateTotalExpense());
            writer.println("----------------------------------------");
            writer.printf("SALDO AKHIR      : Rp %,15.2f\n", transactionManager.calculateTotalBalance());
            writer.println("========================================");
        }
    }
    
    @Override
    public String getExportFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format.toUpperCase();
    }
}
