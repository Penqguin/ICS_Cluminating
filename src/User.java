package src;
// import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class User {
    private String username;
    private String password;
    private double balance;
    private boolean twoFactorEnabled;
    private String contactDestination;
    private String verificationCode;
    private List<String> costStreams;
    private List<String> revenueStreams;
    // Predefined advice messages for users!
    private final List<String> advice = List.of(
            "Use a strong password and change it regularly.",
            "Enable two-factor authentication for added security.",
            "Review your cost and revenue streams monthly to identify trends and opportunities.",
            "Make sure to spend less then you earn to maintain a positive balance!",
            "Consider trying Minecraft",
            "Money isn't everything! If you're down, it's not the end of the world!",
            "Remember to take breaks!",
            "When things aren't going right, try something new!"
    );

    public User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.twoFactorEnabled = false;
        this.costStreams = new ArrayList<>();
        this.revenueStreams = new ArrayList<>();
    }
    
    public User(String username, String password, double balance, boolean twoFactorEnabled, ArrayList<> costStreams, ArrayList<> revenueStreams) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.twoFactorEnabled = false;
        this.costStreams = new ArrayList<>();
        this.revenueStreams = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean setPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        this.password = password;
        return true;
    }

    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void enableTwoFactor(String contactDestination) {
        this.twoFactorEnabled = true;
        this.contactDestination = contactDestination;
        this.verificationCode = generateVerificationCode();
    }

    public String getContactDestination() {
        return contactDestination;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public boolean verifyCode(String code) {
        return verificationCode != null && verificationCode.equals(code);
    }

    public void addCostStream(String cost) {
        if (cost != null && !cost.isBlank()) {
            costStreams.add(cost);
        }
    }

    public void addRevenueStream(String revenue) {
        if (revenue != null && !revenue.isBlank()) {
            revenueStreams.add(revenue);
        }
    }

    public List<String> getCostStreams() {
        return Collections.unmodifiableList(costStreams);
    }

    public List<String> getRevenueStreams() {
        return Collections.unmodifiableList(revenueStreams);
    }

    public String getLoadingMessage() {
        return "Loading account for " + username + "...";
    }

    public String getAdvice() {
        return advice.get(new Random().nextInt(advice.size()));
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (100000 + Math.random() * 900000));
    }
}
