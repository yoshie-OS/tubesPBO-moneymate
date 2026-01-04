package moneymate.model;

/**
 * Enum untuk kategori transaksi
 */
public enum Category {
    // Kategori Pemasukan
    GAJI("Gaji", "INCOME"),
    BONUS("Bonus", "INCOME"),
    INVESTASI("Investasi", "INCOME"),
    LAIN_LAIN_PEMASUKAN("Lain-lain", "INCOME"),
    
    // Kategori Pengeluaran
    MAKANAN("Makanan", "EXPENSE"),
    TRANSPORTASI("Transportasi", "EXPENSE"),
    HIBURAN("Hiburan", "EXPENSE"),
    BELANJA("Belanja", "EXPENSE"),
    TAGIHAN("Tagihan", "EXPENSE"),
    KESEHATAN("Kesehatan", "EXPENSE"),
    PENDIDIKAN("Pendidikan", "EXPENSE"),
    LAIN_LAIN_PENGELUARAN("Lain-lain", "EXPENSE");
    
    private final String displayName;
    private final String type;
    
    Category(String displayName, String type) {
        this.displayName = displayName;
        this.type = type;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isIncomeCategory() {
        return type.equals("INCOME");
    }
    
    public boolean isExpenseCategory() {
        return type.equals("EXPENSE");
    }
    
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Parse string to Category using either enum name or display name (case-insensitive).
     * When no match is found, it falls back to a sensible default based on preferred type.
     */
    public static Category fromString(String value) {
        return fromString(value, false);
    }

    public static Category fromString(String value, boolean preferIncome) {
        if (value == null || value.trim().isEmpty()) {
            return preferIncome ? LAIN_LAIN_PEMASUKAN : LAIN_LAIN_PENGELUARAN;
        }

        String normalized = value.trim();
        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(normalized) ||
                category.getDisplayName().equalsIgnoreCase(normalized)) {
                return category;
            }
        }

        // Default to miscellaneous based on preferred type
        return preferIncome ? LAIN_LAIN_PEMASUKAN : LAIN_LAIN_PENGELUARAN;
    }
    
    /**
     * Get all income categories
     */
    public static Category[] getIncomeCategories() {
        return new Category[]{GAJI, BONUS, INVESTASI, LAIN_LAIN_PEMASUKAN};
    }
    
    /**
     * Get all expense categories
     */
    public static Category[] getExpenseCategories() {
        return new Category[]{MAKANAN, TRANSPORTASI, HIBURAN, BELANJA, 
                             TAGIHAN, KESEHATAN, PENDIDIKAN, LAIN_LAIN_PENGELUARAN};
    }
}
