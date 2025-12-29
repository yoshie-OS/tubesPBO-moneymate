package moneymate.util;

import java.util.Scanner;

/**
 * InputValidator - utility class untuk validasi input user
 */
public class InputValidator {
    
    /**
     * Validasi apakah string adalah angka
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validasi apakah string adalah integer
     */
    public static boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validasi jumlah uang (harus positif)
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }
    
    /**
     * Validasi string tidak kosong
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validasi email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Get valid integer input dari user
     */
    public static int getValidIntInput(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (isInteger(input)) {
                value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Input harus antara %d dan %d!\n", min, max);
                }
            } else {
                System.out.println("Input harus berupa angka!");
            }
        }
    }
    
    /**
     * Get valid double input dari user
     */
    public static double getValidDoubleInput(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (isNumeric(input)) {
                value = Double.parseDouble(input);
                if (isValidAmount(value)) {
                    return value;
                } else {
                    System.out.println("Jumlah harus lebih besar dari 0!");
                }
            } else {
                System.out.println("Input harus berupa angka!");
            }
        }
    }
    
    /**
     * Get non-empty string input
     */
    public static String getValidStringInput(Scanner scanner, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            
            if (isNotEmpty(input)) {
                return input;
            } else {
                System.out.println("Input tidak boleh kosong!");
            }
        }
    }
}
