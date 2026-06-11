package src;
// import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import src.ui.Utils;

public class User {
    private String username;
    private String password;
    private double balance;
    private boolean twoFactorEnabled;
    private String contactDestination;
    private String verificationCode;
    private ArrayList<FinancialStream> costStreams;
    private ArrayList<FinancialStream> revenueStreams;
    private double savingsRate;

    public static class FinancialStream {
        private String name;
        private double amount;
        private String frequency; // Weekly, Monthly, Yearly

        public FinancialStream(String name, double amount, String frequency) {
            this.name = name;
            this.amount = amount;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return name + ";" + amount + ";" + frequency;
        }

        public static FinancialStream fromString(String s) {
            String[] parts = s.split(";");
            if (parts.length == 3) {
                try {
                    return new FinancialStream(parts[0], Double.parseDouble(parts[1]), parts[2]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            // Fallback for old format (just name)
            return new FinancialStream(s, 0.0, "Monthly");
        }

        public String getName() { return name; }
        public double getAmount() { return amount; }
        public String getFrequency() { return frequency; }
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

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
    
    public User(String username, String password, double balance, boolean twoFactorEnabled, ArrayList<FinancialStream> costStreams, ArrayList<FinancialStream> revenueStreams) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.twoFactorEnabled = twoFactorEnabled;
        this.costStreams = costStreams;
        this.revenueStreams = revenueStreams;
    }

    // Static User Management Functions
    public static int login(String username, String password) {
        if (DatabaseManager.verifyPassword(username, password)) {
            return DatabaseManager.getUserIdByUsername(username);
        }
        return -1;
    }

    public static int createAccount(String username, String password, String emailOrPhone) {
        if (!exists(username) && isValidPassword(password) && (isValidEmail(emailOrPhone) || isValidPhone(emailOrPhone))) {
            DatabaseManager.createUser(username, password, emailOrPhone);
            return DatabaseManager.getUserIdByUsername(username);
        }
        return -1;
    }

    public static void deleteAccount(int userId) {
        DatabaseManager.deleteUser(userId);
    }

    public static boolean exists(String username) {
        return DatabaseManager.usernameExists(username);
    }

    public static List<String[]> getAllUsers() {
        return DatabaseManager.getAllUsers();
    }

    // Validation Methods
    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone.matches("\\d{10,15}");
    }

    public static boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        String specialChars = "!@#$%^&*()-_=+";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
            
            if (c < 32) return false;
        }

        if (!(hasUpper && hasLower && hasDigit && hasSpecial)) return false;

        return !password.contains("\\");
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

    public void addCostStream(FinancialStream cost) {
        if (cost != null) {
            costStreams.add(cost);
        }
    }

    public void addRevenueStream(FinancialStream revenue) {
        if (revenue != null) {
            revenueStreams.add(revenue);
        }
    }

    public List<FinancialStream> getCostStreams() {
        return Collections.unmodifiableList(costStreams);
    }

    public List<FinancialStream> getRevenueStreams() {
        return Collections.unmodifiableList(revenueStreams);
    }

    public String getLoadingMessage() {
        return "Loading account for " + username + "...";
    }

    public String getAdvice() {
        return advice.get(new Random().nextInt(advice.size()));
    }

    public void showLoadingScreen() {
        Utils.clearScreen();
        String[] messages = {
            "Connecting to secure server...",
            "Fetching encrypted data for " + username + "...",
            "Verifying data integrity...",
            "Calculating budget projections...",
            "Ready!"
        };
        for (String msg : messages) {
            System.out.println("[System] " + msg);
            Utils.sleep(600);
        }
        Utils.sleep(400);
    }

    public static User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return null;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getString("username"),
                        "", 
                        DatabaseManager.getTotalIncome(userId) - DatabaseManager.getTotalExpenses(userId)
                    );
                    user.twoFactorEnabled = rs.getInt("two_fa_enabled") == 1;
                    user.contactDestination = rs.getString("email_or_phone");
                    user.costStreams = deserializeStreams(rs.getString("cost_streams"));
                    user.revenueStreams = deserializeStreams(rs.getString("revenue_streams"));
                    user.savingsRate = rs.getDouble("savings_rate");
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to fetch user: " + e.getMessage());
        }
        return null;
    }

    public void saveToDatabase(int userId) {
        String query = "UPDATE users SET two_fa_enabled = ?, cost_streams = ?, revenue_streams = ?, savings_rate = ? WHERE user_id = ?";
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        
        // Recalculate savings rate
        double income = DatabaseManager.getTotalIncome(userId);
        double expense = DatabaseManager.getTotalExpenses(userId);
        if (income > 0) {
            this.savingsRate = (income - expense) / income;
        } else {
            this.savingsRate = 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, twoFactorEnabled ? 1 : 0);
            pstmt.setString(2, serializeStreams(costStreams));
            pstmt.setString(3, serializeStreams(revenueStreams));
            pstmt.setDouble(4, savingsRate);
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to update user: " + e.getMessage());
        }
    }

    private static String serializeStreams(ArrayList<FinancialStream> streams) {
        if (streams == null || streams.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < streams.size(); i++) {
            sb.append(streams.get(i).toString());
            if (i < streams.size() - 1) sb.append("|");
        }
        return sb.toString();
    }

    private static ArrayList<FinancialStream> deserializeStreams(String data) {
        ArrayList<FinancialStream> list = new ArrayList<>();
        if (data == null || data.isEmpty()) return list;
        String[] parts = data.split("\\|");
        for (String p : parts) {
            if (!p.isBlank()) {
                FinancialStream fs = FinancialStream.fromString(p);
                if (fs != null) list.add(fs);
            }
        }
        return list;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (100000 + Math.random() * 900000));
    }
}
