package moneymate.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * DateUtil - utility class untuk operasi tanggal
 */
public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    
    /**
     * Parse string ke LocalDate
     */
    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    /**
     * Parse string ke YearMonth
     */
    public static YearMonth parseYearMonth(String monthString) throws DateTimeParseException {
        return YearMonth.parse(monthString, MONTH_FORMATTER);
    }
    
    /**
     * Format LocalDate ke string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format YearMonth ke string
     */
    public static String formatYearMonth(YearMonth yearMonth) {
        return yearMonth.format(MONTH_FORMATTER);
    }
    
    /**
     * Get current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * Get current month
     */
    public static YearMonth getCurrentMonth() {
        return YearMonth.now();
    }
    
    /**
     * Cek apakah tanggal valid
     */
    public static boolean isValidDate(String dateString) {
        try {
            parseDate(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Cek apakah bulan valid
     */
    public static boolean isValidYearMonth(String monthString) {
        try {
            parseYearMonth(monthString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Get date formatter
     */
    public static DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }
    
    /**
     * Get month formatter
     */
    public static DateTimeFormatter getMonthFormatter() {
        return MONTH_FORMATTER;
    }
}
