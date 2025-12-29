package moneymate.view;

import moneymate.controller.TransactionManager;
import moneymate.model.*;
import moneymate.exception.*;
import moneymate.util.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;

/**
 * MoneyMateApp - Main application class dengan console-based UI
 */
public class MoneyMateApp {
    
    private TransactionManager transactionManager;
    private RegularUser currentUser;
    private Scanner scanner;
    
    public MoneyMateApp() {
        this.scanner = new Scanner(System.in);
        this.transactionManager = new TransactionManager();
    }
    
    public void start() {
        displayWelcome();
        setupUser();
        mainMenu();
    }
    
    private void displayWelcome() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║                                        ║");
        System.out.println("║          MONEYMATE v1.0                ║");
        System.out.println("║   Manajer Keuangan Pribadi Anda        ║");
        System.out.println("║                                        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    private void setupUser() {
        System.out.println("=== SETUP USER ===");
        String username = InputValidator.getValidStringInput(scanner, "Masukkan username: ");
        String email = scanner.nextLine();
        
        // Simple setup tanpa password untuk versi console
        currentUser = new RegularUser("USR001", username, email, "default");
        
        System.out.print("Masukkan saldo awal (Rp): ");
        String balanceInput = scanner.nextLine().trim();
        
        if (InputValidator.isNumeric(balanceInput)) {
            double initialBalance = Double.parseDouble(balanceInput);
            if (initialBalance >= 0) {
                currentUser.setInitialBalance(initialBalance);
                transactionManager.setInitialBalance(initialBalance);
            }
        }
        
        System.out.println("\n✓ User berhasil dibuat!");
        currentUser.displayUserInfo();
    }
    
    private void mainMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║            MENU UTAMA                  ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Tambah Transaksi                    ║");
            System.out.println("║ 2. Lihat Semua Transaksi               ║");
            System.out.println("║ 3. Ubah Transaksi                      ║");
            System.out.println("║ 4. Hapus Transaksi                     ║");
            System.out.println("║ 5. Lihat Saldo                         ║");
            System.out.println("║ 6. Laporan Bulanan                     ║");
            System.out.println("║ 7. Export Laporan                      ║");
            System.out.println("║ 8. Filter Transaksi                    ║");
            System.out.println("║ 0. Keluar                              ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            int choice = InputValidator.getValidIntInput(scanner, "Pilih menu (0-8): ", 0, 8);
            
            try {
                switch (choice) {
                    case 1:
                        addTransactionMenu();
                        break;
                    case 2:
                        viewAllTransactions();
                        break;
                    case 3:
                        updateTransactionMenu();
                        break;
                    case 4:
                        deleteTransactionMenu();
                        break;
                    case 5:
                        viewBalance();
                        break;
                    case 6:
                        monthlyReportMenu();
                        break;
                    case 7:
                        exportReportMenu();
                        break;
                    case 8:
                        filterTransactionsMenu();
                        break;
                    case 0:
                        running = false;
                        displayGoodbye();
                        break;
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
    
    private void addTransactionMenu() throws InvalidTransactionException, InsufficientBalanceException {
        System.out.println("\n=== TAMBAH TRANSAKSI ===");
        System.out.println("1. Pemasukan");
        System.out.println("2. Pengeluaran");
        
        int type = InputValidator.getValidIntInput(scanner, "Pilih tipe (1-2): ", 1, 2);
        
        double amount = InputValidator.getValidDoubleInput(scanner, "Masukkan jumlah (Rp): ");
        String description = InputValidator.getValidStringInput(scanner, "Deskripsi: ");
        
        System.out.print("Tanggal (dd/MM/yyyy) [Enter untuk hari ini]: ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date = dateInput.isEmpty() ? DateUtil.getCurrentDate() : DateUtil.parseDate(dateInput);

        System.out.print("Kategori: ");
        String category = scanner.nextLine().trim();

        Transaction transaction;

        if (type == 1) {
            // Income
            System.out.print("Sumber pemasukan: ");
            String source = scanner.nextLine().trim();
            transaction = new Income(amount, description, date, category, source);
        } else {
            // Expense
            System.out.print("Metode pembayaran (Cash/Debit/Credit/E-Wallet): ");
            String paymentMethod = scanner.nextLine().trim();
            System.out.print("Pengeluaran berulang? (y/n): ");
            boolean isRecurring = scanner.nextLine().trim().equalsIgnoreCase("y");
            transaction = new Expense(amount, description, date, category, paymentMethod, isRecurring);
        }
        
        transactionManager.addTransaction(transaction);
    }
    
    private Category selectCategory(Category[] categories) {
        System.out.println("\nPilih Kategori:");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("%d. %s\n", i + 1, categories[i].getDisplayName());
        }
        
        int choice = InputValidator.getValidIntInput(scanner, "Pilihan: ", 1, categories.length);
        return categories[choice - 1];
    }
    
    private void viewAllTransactions() {
        transactionManager.displayAllTransactions();
    }
    
    private void updateTransactionMenu() {
        try {
            viewAllTransactions();
            if (transactionManager.getTransactions().isEmpty()) {
                return;
            }
            
            String id = InputValidator.getValidStringInput(scanner, "\nMasukkan ID transaksi yang akan diubah: ");
            Transaction oldTransaction = transactionManager.findTransactionById(id);
            
            System.out.println("\nTransaksi saat ini:");
            System.out.println(oldTransaction);
            
            System.out.println("\nMasukkan data baru:");
            
            double amount = InputValidator.getValidDoubleInput(scanner, "Jumlah baru (Rp): ");
            String description = InputValidator.getValidStringInput(scanner, "Deskripsi baru: ");

            System.out.print("Tanggal baru (dd/MM/yyyy): ");
            LocalDate date = DateUtil.parseDate(scanner.nextLine().trim());

            System.out.print("Kategori: ");
            String category = scanner.nextLine().trim();

            Transaction newTransaction;
            if (oldTransaction instanceof Income) {
                System.out.print("Sumber pemasukan: ");
                String source = scanner.nextLine().trim();
                newTransaction = new Income(amount, description, date, category, source);
            } else {
                System.out.print("Metode pembayaran: ");
                String paymentMethod = scanner.nextLine().trim();
                System.out.print("Pengeluaran berulang? (y/n): ");
                boolean isRecurring = scanner.nextLine().trim().equalsIgnoreCase("y");
                newTransaction = new Expense(amount, description, date, category, paymentMethod, isRecurring);
            }
            
            transactionManager.updateTransaction(id, newTransaction);
            
        } catch (TransactionNotFoundException | InvalidTransactionException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private void deleteTransactionMenu() {
        try {
            viewAllTransactions();
            if (transactionManager.getTransactions().isEmpty()) {
                return;
            }
            
            String id = InputValidator.getValidStringInput(scanner, "\nMasukkan ID transaksi yang akan dihapus: ");
            
            Transaction transaction = transactionManager.findTransactionById(id);
            System.out.println("\nTransaksi yang akan dihapus:");
            System.out.println(transaction);
            
            System.out.print("Yakin ingin menghapus? (y/n): ");
            String confirm = scanner.nextLine().trim();
            
            if (confirm.equalsIgnoreCase("y")) {
                transactionManager.deleteTransaction(id);
            } else {
                System.out.println("Penghapusan dibatalkan.");
            }
            
        } catch (TransactionNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private void viewBalance() {
        transactionManager.displayBalanceSummary();
    }
    
    private void monthlyReportMenu() {
        System.out.println("\n=== LAPORAN BULANAN ===");
        System.out.print("Masukkan bulan (MM/yyyy) [Enter untuk bulan ini]: ");
        String monthInput = scanner.nextLine().trim();
        
        YearMonth month = monthInput.isEmpty() ? DateUtil.getCurrentMonth() : DateUtil.parseYearMonth(monthInput);
        
        Report report = transactionManager.generateMonthlyReport(month);
        System.out.println(report.generateSummary());
    }
    
    private void exportReportMenu() {
        try {
            System.out.println("\n=== EXPORT LAPORAN ===");
            System.out.println("1. CSV");
            System.out.println("2. TXT");
            
            int choice = InputValidator.getValidIntInput(scanner, "Pilih format (1-2): ", 1, 2);
            String format = (choice == 1) ? "CSV" : "TXT";
            
            String fileName = "MoneyMate_Report_" + DateUtil.getCurrentDate() + "." + format.toLowerCase();
            String filePath = "exports\\" + fileName;
            
            FileExporter exporter = new FileExporter(transactionManager, format);
            exporter.exportToFile(filePath);
            
        } catch (FileExportException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private void filterTransactionsMenu() {
        System.out.println("\n=== FILTER TRANSAKSI ===");
        System.out.println("1. Filter by Tipe (Income/Expense)");
        System.out.println("2. Filter by Kategori");
        System.out.println("3. Filter by Tanggal");
        System.out.println("4. Filter by Bulan");
        
        int choice = InputValidator.getValidIntInput(scanner, "Pilih filter (1-4): ", 1, 4);
        List<Transaction> filtered = null;
        
        switch (choice) {
            case 1:
                System.out.println("1. Pemasukan");
                System.out.println("2. Pengeluaran");
                int type = InputValidator.getValidIntInput(scanner, "Pilih tipe: ", 1, 2);
                filtered = transactionManager.getTransactionsByType(
                    type == 1 ? Income.class : Expense.class);
                break;
                
            case 2:
                System.out.println("Pilih tipe kategori:");
                System.out.println("1. Kategori Pemasukan");
                System.out.println("2. Kategori Pengeluaran");
                int catType = InputValidator.getValidIntInput(scanner, "Pilih: ", 1, 2);
                Category cat = selectCategory(catType == 1 ? 
                    Category.getIncomeCategories() : Category.getExpenseCategories());
                filtered = transactionManager.getTransactionsByCategory(cat);
                break;
                
            case 3:
                System.out.print("Masukkan tanggal (dd/MM/yyyy): ");
                LocalDate date = DateUtil.parseDate(scanner.nextLine().trim());
                filtered = transactionManager.getTransactionsByDate(date);
                break;
                
            case 4:
                System.out.print("Masukkan bulan (MM/yyyy): ");
                YearMonth month = DateUtil.parseYearMonth(scanner.nextLine().trim());
                filtered = transactionManager.getTransactionsByMonth(month);
                break;
        }
        
        if (filtered != null && !filtered.isEmpty()) {
            System.out.println("\n=== HASIL FILTER ===");
            for (Transaction t : filtered) {
                System.out.println(t);
            }
            System.out.printf("\nTotal: %d transaksi\n", filtered.size());
        } else {
            System.out.println("Tidak ada transaksi yang sesuai filter.");
        }
    }
    
    private void displayGoodbye() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║                                        ║");
        System.out.println("║   Terima kasih telah menggunakan       ║");
        System.out.println("║           MONEYMATE!                   ║");
        System.out.println("║                                        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    public static void main(String[] args) {
        MoneyMateApp app = new MoneyMateApp();
        app.start();
    }
}
