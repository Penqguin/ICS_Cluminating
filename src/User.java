package src;

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

/**
 * Represents a user of the financial application.
 * Handles authentication, 2FA, balance, and savings tracking.
 */
public class User {
    private String username;
    private String password;
    private double balance;
    private boolean twoFactorEnabled;
    private String contactDestination;
    private String verificationCode;
    private double savingsRate;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Predefined advice messages for users
    private final List<String> advice = List.of(
            "Use a strong password and change it regularly.",
            "Enable two-factor authentication for added security.",
            "Review your income and expenses monthly to identify trends and opportunities.",
            "Make sure to spend less than you earn to maintain a positive balance!",
            "Consider trying Minecraft",
            "Money isn't everything! If you're down, it's not the end of the world!",
            "Remember to take breaks!",
            "When things aren't going right, try something new!"
    );

    /**
     * Constructs a new User with default 2FA disabled.
     * @param username The username
     * @param password The password (plain text, will be hashed by DB)
     * @param balance The starting balance
     */
    public User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.twoFactorEnabled = false;
    }
    
    /**
     * Constructs a new User with specified 2FA status.
     * @param username The username
     * @param password The password
     * @param balance The starting balance
     * @param twoFactorEnabled Whether 2FA is enabled
     */
    public User(String username, String password, double balance, boolean twoFactorEnabled) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    // Static User Management Functions
    /**
     * Attempts to log in a user.
     * @param username The username
     * @param password The plain-text password
     * @return User ID if successful, -1 otherwise
     */
    public static int login(String username, String password) {
        if (DatabaseManager.verifyPassword(username, password)) {
            return DatabaseManager.getUserIdByUsername(username);
        }
        return -1;
    }

    /**
     * Creates a new user account.
     * @param username The username
     * @param password The plain-text password
     * @param emailOrPhone Email or phone for 2FA
     * @return User ID if successful, -1 otherwise
     */
    public static int createAccount(String username, String password, String emailOrPhone) {
        if (!exists(username) && isValidPassword(password) && (isValidEmail(emailOrPhone) || isValidPhone(emailOrPhone))) {
            DatabaseManager.createUser(username, password, emailOrPhone);
            return DatabaseManager.getUserIdByUsername(username);
        }
        return -1;
    }

    /**
     * Deletes a user account.
     * @param userId The user ID to delete
     */
    public static void deleteAccount(int userId) {
        DatabaseManager.deleteUser(userId);
    }

    /**
     * Checks if a username exists.
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    public static boolean exists(String username) {
        return DatabaseManager.usernameExists(username);
    }

    /**
     * Gets all users from the database.
     * @return List of [user_id, username] arrays
     */
    public static List<String[]> getAllUsers() {
        return DatabaseManager.getAllUsers();
    }

    // Validation Methods
    /**
     * Validates an email address format.
     * @param email The email to validate
     * @return true if valid format
     */
    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates a phone number format (10-15 digits).
     * @param phone The phone number to validate
     * @return true if valid format
     */
    public static boolean isValidPhone(String phone) {
        return phone.matches("\\d{10,15}");
    }

    /**
     * Validates password strength requirements.
     * Minimum 8 chars, uppercase, lowercase, digit, special char, no backslash.
     * @param password The password to validate
     * @return true if valid
     */
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

    /**
     * Gets the username.
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password if it meets minimum length.
     * @param password The new password
     * @return true if set successfully
     */
    public boolean setPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        this.password = password;
        return true;
    }

    /**
     * Checks a password against the stored value.
     * @param password The password to check
     * @return true if matches
     */
    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    /**
     * Gets the account balance.
     * @return Balance amount
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the account balance.
     * @param balance The new balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Checks if two-factor authentication is enabled.
     * @return true if 2FA is enabled
     */
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    /**
     * Enables two-factor authentication and generates a verification code.
     * @param contactDestination The email or phone for 2FA
     */
    public void enableTwoFactor(String contactDestination) {
        this.twoFactorEnabled = true;
        this.contactDestination = contactDestination;
        this.verificationCode = generateVerificationCode();
    }

    /**
     * Gets the contact destination for 2FA.
     * @return Email or phone
     */
    public String getContactDestination() {
        return contactDestination;
    }

    /**
     * Gets the current verification code.
     * @return The 6-digit code
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * Verifies a 2FA code against the stored code.
     * @param code The code to verify
     * @return true if code matches
     */
    public boolean verifyCode(String code) {
        return verificationCode != null && verificationCode.equals(code);
    }

    /**
     * Gets a loading message for the user.
     * @return Loading message string
     */
    public String getLoadingMessage() {
        return "Loading account for " + username + "...";
    }

    /**
     * Gets a random advice message.
     * @return A random advice string
     */
    public String getAdvice() {
        return advice.get(new Random().nextInt(advice.size()));
    }

    /**
     * Displays a simulated loading screen with progressive messages.
     * Uses sleep() for animation effect.
     */
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

    /**
     * Retrieves a User object from the database by user ID.
     * @param userId The user ID
     * @return User object, or null if not found
     */
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
                    user.savingsRate = rs.getDouble("savings_rate");
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to fetch user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves the current user state (2FA status, savings rate) to the database.
     * @param userId The user ID to save to
     */
    public void saveToDatabase(int userId) {
        String query = "UPDATE users SET two_fa_enabled = ?, savings_rate = ? WHERE user_id = ?";
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
            pstmt.setDouble(2, savingsRate);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to update user: " + e.getMessage());
        }
    }

    /**
     * Gets the user's savings rate.
     * @return Savings rate as a decimal
     */
    public double getSavingsRate() {
        return savingsRate;
    }

    /**
     * Generates a random 6-digit verification code for 2FA.
     * @return A 6-digit code as string
     */
    private String generateVerificationCode() {
        return String.valueOf((int) (100000 + Math.random() * 900000));
    }
}
