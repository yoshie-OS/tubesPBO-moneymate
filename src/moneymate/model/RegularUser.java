package moneymate.model;

/**
 * Class RegularUser - implementasi konkret dari abstract class User
 */
public class RegularUser extends User {
    
    private String password;
    private double initialBalance;
    
    public RegularUser(String userId, String username, String email, String password) {
        super(userId, username, email);
        this.password = password;
        this.initialBalance = 0.0;
    }
    
    public RegularUser(String userId, String username, String email, String password, double initialBalance) {
        super(userId, username, email);
        this.password = password;
        this.initialBalance = initialBalance;
    }
    
    public double getInitialBalance() {
        return initialBalance;
    }
    
    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }
    
    @Override
    public void displayUserInfo() {
        System.out.println("===== INFORMASI USER =====");
        System.out.println("ID       : " + userId);
        System.out.println("Username : " + username);
        System.out.println("Email    : " + email);
        System.out.println("Terdaftar: " + createdAt);
        System.out.println("Saldo Awal: Rp " + String.format("%.2f", initialBalance));
        System.out.println("========================");
    }
    
    @Override
    public boolean validateCredentials(String password) {
        return this.password.equals(password);
    }
    
    public void changePassword(String oldPassword, String newPassword) {
        if (validateCredentials(oldPassword)) {
            this.password = newPassword;
            System.out.println("Password berhasil diubah!");
        } else {
            System.out.println("Password lama tidak sesuai!");
        }
    }
}
