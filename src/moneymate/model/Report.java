package moneymate.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class Report - untuk generate laporan keuangan
 */
public class Report {
    
    private List<Transaction> transactions;
    private YearMonth reportPeriod; // periode laporan (bulan & tahun)
    
    public Report(List<Transaction> transactions, YearMonth reportPeriod) {
        this.transactions = new ArrayList<>(transactions);
        this.reportPeriod = reportPeriod;
    }
    
    /**
     * Filter transaksi berdasarkan periode
     */
    public List<Transaction> getTransactionsInPeriod() {
        return transactions.stream()
            .filter(t -> {
                YearMonth transactionMonth = YearMonth.from(t.getDate());
                return transactionMonth.equals(reportPeriod);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Hitung total pemasukan dalam periode
     */
    public double getTotalIncome() {
        return getTransactionsInPeriod().stream()
            .filter(t -> t instanceof Income)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    /**
     * Hitung total pengeluaran dalam periode
     */
    public double getTotalExpense() {
        return getTransactionsInPeriod().stream()
            .filter(t -> t instanceof Expense)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    /**
     * Hitung saldo dalam periode
     */
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
    
    /**
     * Get pengeluaran berdasarkan kategori
     */
    public Map<String, Double> getExpenseByCategory() {
        Map<String, Double> categoryMap = new HashMap<>();

        getTransactionsInPeriod().stream()
            .filter(t -> t instanceof Expense)
            .forEach(t -> {
                String cat = t.getCategory();
                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + t.getAmount());
            });

        return categoryMap;
    }

    /**
     * Get pemasukan berdasarkan kategori
     */
    public Map<String, Double> getIncomeByCategory() {
        Map<String, Double> categoryMap = new HashMap<>();

        getTransactionsInPeriod().stream()
            .filter(t -> t instanceof Income)
            .forEach(t -> {
                String cat = t.getCategory();
                categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + t.getAmount());
            });

        return categoryMap;
    }
    
    /**
     * Generate summary report
     */
    public String generateSummary() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n========================================\n");
        sb.append("       LAPORAN KEUANGAN BULANAN\n");
        sb.append("========================================\n");
        sb.append("Periode: ").append(reportPeriod.format(formatter)).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Total Pemasukan  : Rp %,15.2f\n", getTotalIncome()));
        sb.append(String.format("Total Pengeluaran: Rp %,15.2f\n", getTotalExpense()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("SALDO            : Rp %,15.2f\n", getBalance()));
        sb.append("========================================\n");
        
        // Breakdown per kategori pengeluaran
        if (!getExpenseByCategory().isEmpty()) {
            sb.append("\nPengeluaran per Kategori:\n");
            sb.append("----------------------------------------\n");
            getExpenseByCategory().forEach((cat, amount) -> {
                double percentage = (amount / getTotalExpense()) * 100;
                sb.append(String.format("%-20s: Rp %,12.2f (%.1f%%)\n",
                                      cat, amount, percentage));
            });
        }

        // Breakdown per kategori pemasukan
        if (!getIncomeByCategory().isEmpty()) {
            sb.append("\nPemasukan per Kategori:\n");
            sb.append("----------------------------------------\n");
            getIncomeByCategory().forEach((cat, amount) -> {
                double percentage = (amount / getTotalIncome()) * 100;
                sb.append(String.format("%-20s: Rp %,12.2f (%.1f%%)\n",
                                      cat, amount, percentage));
            });
        }
        
        sb.append("========================================\n");
        
        return sb.toString();
    }
    
    public YearMonth getReportPeriod() {
        return reportPeriod;
    }
    
    public void setReportPeriod(YearMonth reportPeriod) {
        this.reportPeriod = reportPeriod;
    }
}
