package moneymate.view;

import moneymate.controller.TransactionManager;
import moneymate.model.*;
import moneymate.exception.*;
import moneymate.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MoneyMateGUI - Java Swing GUI Application
 *
 * OOP Concepts:
 * - Event-Driven Programming
 * - Inheritance: extends JFrame
 * - Encapsulation: private fields and methods
 */
public class MoneyMateGUI extends JFrame {

    private TransactionManager transactionManager;
    private RegularUser currentUser;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;

    public MoneyMateGUI() {
        this.transactionManager = new TransactionManager();
        setupUser();
        initializeGUI();
        refreshTransactionTable();
        updateBalanceDisplay();
    }

    private void setupUser() {
        String username = JOptionPane.showInputDialog(null, "Masukkan username:", "Setup User", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = "User";
        }

        String email = JOptionPane.showInputDialog(null, "Masukkan email:", "Setup User", JOptionPane.QUESTION_MESSAGE);
        if (email == null || email.trim().isEmpty()) {
            email = "user@email.com";
        }

        String balanceStr = JOptionPane.showInputDialog(null, "Masukkan saldo awal (Rp):", "Setup User", JOptionPane.QUESTION_MESSAGE);
        double initialBalance = 0.0;
        try {
            if (balanceStr != null && !balanceStr.trim().isEmpty()) {
                initialBalance = Double.parseDouble(balanceStr);
            }
        } catch (NumberFormatException e) {
            initialBalance = 0.0;
        }

        currentUser = new RegularUser("USR001", username, email, "default");
        currentUser.setInitialBalance(initialBalance);
        transactionManager.setInitialBalance(initialBalance);
    }

    private void initializeGUI() {
        setTitle("MoneyMate - Personal Finance Manager");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel - User Info and Balance
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Transaction Table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Action Buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Menu Bar
        setJMenuBar(createMenuBar());

        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(52, 73, 94));

        // User Info
        JLabel userLabel = new JLabel("👤 " + currentUser.getUsername() + " (" + currentUser.getEmail() + ")");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);

        // Balance Panel
        JPanel balancePanel = new JPanel(new GridLayout(1, 3, 10, 0));
        balancePanel.setOpaque(false);

        balanceLabel = new JLabel("Saldo: Rp 0", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(46, 204, 113));

        incomeLabel = new JLabel("Pemasukan: Rp 0", SwingConstants.CENTER);
        incomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        incomeLabel.setForeground(new Color(52, 152, 219));

        expenseLabel = new JLabel("Pengeluaran: Rp 0", SwingConstants.CENTER);
        expenseLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        expenseLabel.setForeground(new Color(231, 76, 60));

        balancePanel.add(incomeLabel);
        balancePanel.add(balanceLabel);
        balancePanel.add(expenseLabel);

        panel.add(userLabel);
        panel.add(balancePanel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Tipe", "Tanggal", "Kategori", "Deskripsi", "Jumlah", "Detail"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton addButton = new JButton("➕ Tambah Transaksi");
        JButton deleteButton = new JButton("🗑️ Hapus Transaksi");
        JButton refreshButton = new JButton("🔄 Refresh");
        JButton reportButton = new JButton("📊 Laporan Bulanan");

        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        reportButton.setFont(new Font("Arial", Font.BOLD, 12));

        addButton.addActionListener(e -> showAddTransactionDialog());
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        refreshButton.addActionListener(e -> {
            refreshTransactionTable();
            updateBalanceDisplay();
        });
        reportButton.addActionListener(e -> showMonthlyReport());

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        panel.add(reportButton);

        return panel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportCSV = new JMenuItem("Export CSV");
        JMenuItem exportTXT = new JMenuItem("Export TXT");
        JMenuItem exit = new JMenuItem("Exit");

        exportCSV.addActionListener(e -> exportReport("CSV"));
        exportTXT.addActionListener(e -> exportReport("TXT"));
        exit.addActionListener(e -> System.exit(0));

        fileMenu.add(exportCSV);
        fileMenu.add(exportTXT);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem viewAll = new JMenuItem("Semua Transaksi");
        JMenuItem viewIncome = new JMenuItem("Pemasukan Saja");
        JMenuItem viewExpense = new JMenuItem("Pengeluaran Saja");

        viewAll.addActionListener(e -> filterTransactions("ALL"));
        viewIncome.addActionListener(e -> filterTransactions("INCOME"));
        viewExpense.addActionListener(e -> filterTransactions("EXPENSE"));

        viewMenu.add(viewAll);
        viewMenu.add(viewIncome);
        viewMenu.add(viewExpense);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> showAboutDialog());
        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(this, "Tambah Transaksi", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Type
        JLabel typeLabel = new JLabel("Tipe:");
        String[] types = {"Pemasukan", "Pengeluaran"};
        JComboBox<String> typeCombo = new JComboBox<>(types);

        // Amount
        JLabel amountLabel = new JLabel("Jumlah (Rp):");
        JTextField amountField = new JTextField();

        // Description
        JLabel descLabel = new JLabel("Deskripsi:");
        JTextField descField = new JTextField();

        // Date
        JLabel dateLabel = new JLabel("Tanggal (dd/MM/yyyy):");
        JTextField dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Category - now free text input
        JLabel categoryLabel = new JLabel("Kategori:");
        JTextField categoryField = new JTextField();
        categoryField.setToolTipText("Contoh: Makanan, Gaji, Transport, dll");

        // Extra field (Source/Payment Method)
        JLabel extraLabel = new JLabel("Sumber:");
        JTextField extraField = new JTextField();

        // Recurring checkbox (for Expense)
        JCheckBox recurringCheck = new JCheckBox("Pengeluaran Berulang");
        recurringCheck.setVisible(false);

        typeCombo.addActionListener(e -> {
            boolean isIncome = typeCombo.getSelectedIndex() == 0;
            if (isIncome) {
                extraLabel.setText("Sumber:");
                recurringCheck.setVisible(false);
                categoryField.setToolTipText("Contoh: Gaji, Bonus, Investasi, dll");
            } else {
                extraLabel.setText("Metode Pembayaran:");
                recurringCheck.setVisible(true);
                categoryField.setToolTipText("Contoh: Makanan, Transport, Hiburan, dll");
            }
        });

        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryField);
        formPanel.add(extraLabel);
        formPanel.add(extraField);
        formPanel.add(new JLabel());
        formPanel.add(recurringCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("💾 Simpan");
        JButton cancelButton = new JButton("❌ Batal");

        saveButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String description = descField.getText();
                LocalDate date = DateUtil.parseDate(dateField.getText());
                String category = categoryField.getText().trim();
                String extra = extraField.getText();

                if (category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Kategori tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transaction transaction;
                if (typeCombo.getSelectedIndex() == 0) {
                    transaction = new Income(amount, description, date, category, extra);
                } else {
                    boolean isRecurring = recurringCheck.isSelected();
                    transaction = new Expense(amount, description, date, category, extra, isRecurring);
                }

                transactionManager.addTransaction(transaction);
                refreshTransactionTable();
                updateBalanceDisplay();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidTransactionException | InsufficientBalanceException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshTransactionTable() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionManager.getTransactions();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Transaction t : transactions) {
            String detail = "";
            if (t instanceof Income) {
                detail = "Sumber: " + ((Income) t).getSource();
            } else if (t instanceof Expense) {
                Expense exp = (Expense) t;
                detail = exp.getPaymentMethod() + (exp.isRecurring() ? " [BERULANG]" : "");
            }

            Object[] row = {
                t.getTransactionId(),
                t.getTransactionType(),
                t.getDate().format(formatter),
                t.getCategory(),
                t.getDescription(),
                String.format("Rp %,.2f", t.getAmount()),
                detail
            };
            tableModel.addRow(row);
        }
    }

    private void updateBalanceDisplay() {
        double balance = transactionManager.calculateTotalBalance();
        double income = transactionManager.calculateTotalIncome();
        double expense = transactionManager.calculateTotalExpense();

        balanceLabel.setText(String.format("Saldo: Rp %,.2f", balance));
        incomeLabel.setText(String.format("Pemasukan: Rp %,.2f", income));
        expenseLabel.setText(String.format("Pengeluaran: Rp %,.2f", expense));
    }

    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                transactionManager.deleteTransaction(transactionId);
                refreshTransactionTable();
                updateBalanceDisplay();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (TransactionNotFoundException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterTransactions(String filterType) {
        tableModel.setRowCount(0);
        List<Transaction> transactions;

        switch (filterType) {
            case "INCOME":
                transactions = transactionManager.getTransactionsByType(Income.class);
                break;
            case "EXPENSE":
                transactions = transactionManager.getTransactionsByType(Expense.class);
                break;
            default:
                transactions = transactionManager.getTransactions();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Transaction t : transactions) {
            String detail = "";
            if (t instanceof Income) {
                detail = "Sumber: " + ((Income) t).getSource();
            } else if (t instanceof Expense) {
                Expense exp = (Expense) t;
                detail = exp.getPaymentMethod() + (exp.isRecurring() ? " [BERULANG]" : "");
            }

            Object[] row = {
                t.getTransactionId(),
                t.getTransactionType(),
                t.getDate().format(formatter),
                t.getCategory(),
                t.getDescription(),
                String.format("Rp %,.2f", t.getAmount()),
                detail
            };
            tableModel.addRow(row);
        }
    }

    private void showMonthlyReport() {
        String monthInput = JOptionPane.showInputDialog(this, "Masukkan bulan (MM/yyyy):",
            YearMonth.now().format(DateTimeFormatter.ofPattern("MM/yyyy")));

        if (monthInput != null && !monthInput.trim().isEmpty()) {
            try {
                YearMonth month = DateUtil.parseYearMonth(monthInput);
                Report report = transactionManager.generateMonthlyReport(month);

                JTextArea textArea = new JTextArea(report.generateSummary());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Laporan Bulanan - " + month, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Format bulan salah! Gunakan MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportReport(String format) {
        try {
            String fileName = "MoneyMate_Report_" + LocalDate.now() + "." + format.toLowerCase();
            String filePath = "exports/" + fileName;

            FileExporter exporter = new FileExporter(transactionManager, format);
            exporter.exportToFile(filePath);

            JOptionPane.showMessageDialog(this, "Laporan berhasil di-export ke:\n" + filePath, "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileExportException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        String message = """
            MoneyMate v1.0
            Personal Finance Manager

            Developed by: [Your Team Name]

            Features:
            • Transaction Management
            • Category Classification
            • Monthly Reports
            • Export to CSV/TXT
            • SQLite Database

            © 2025 MoneyMate Team
            """;

        JOptionPane.showMessageDialog(this, message, "About MoneyMate", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MoneyMateGUI app = new MoneyMateGUI();
            app.setVisible(true);
        });
    }
}
