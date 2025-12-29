package moneymate.interfaces;

import java.util.List;
import moneymate.model.Transaction;

/**
 * Interface untuk menghitung total saldo dan statistik keuangan
 */
public interface Calculable {
    
    /**
     * Menghitung total saldo dari semua transaksi
     * @return total saldo (pemasukan - pengeluaran)
     */
    double calculateTotalBalance();
    
    /**
     * Menghitung total pemasukan
     * @return total pemasukan
     */
    double calculateTotalIncome();
    
    /**
     * Menghitung total pengeluaran
     * @return total pengeluaran
     */
    double calculateTotalExpense();
    
    /**
     * Mendapatkan daftar transaksi untuk perhitungan
     * @return list of transactions
     */
    List<Transaction> getTransactions();
}
