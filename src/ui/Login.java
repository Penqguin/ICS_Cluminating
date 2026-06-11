package src.ui;

import src.DatabaseManager;
import src.ui.Utils;
import java.util.List;
import java.util.regex.Pattern;

public class Login {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static int promptForUser() {
        while (true) {
            Utils.clearScreen();
            System.out.println("=== Welcome to Financial App ===");
            System.out.println();

            List<String[]> users = DatabaseManager.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No existing users found.");
            } else {
                System.out.println("Existing Users:");
                for (String[] user : users) {
                    System.out.println(" - " + user[1]);
                }
            }
            System.out.println();

            System.out.println("[1] Login");
            System.out.println("[2] Create Account");
            System.out.println("[3] Delete Account");
            System.out.println("[4] Exit");
            System.out.print("\nSelect an option: ");

            String choice = Utils.sc.nextLine().trim();

            switch (choice) {
                case "1":
                    int userId = handleLogin();
                    if (userId != -1) return userId;
                    break;
                case "2":
                    return createNewUser("");
                case "3":
                    handleDeleteAccount();
                    break;
                case "4":
                    System.out.println("Exiting application...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    Utils.pauseScreen();
                    break;
            }
        }
    }

    private static int handleLogin() {
        System.out.print("Enter username: ");
        String username = Utils.sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            Utils.pauseScreen();
            return -1;
        }

        if (DatabaseManager.usernameExists(username)) {
            System.out.print("Enter password: ");
            String password = Utils.sc.nextLine().trim();

            if (DatabaseManager.verifyPassword(username, password)) {
                System.out.println("Login successful!");
                Utils.pauseScreen();
                return DatabaseManager.getUserIdByUsername(username);
            } else {
                System.out.println("Incorrect password.");
                Utils.pauseScreen();
                return -1;
            }
        } else {
            System.out.printf("User '%s' not found.%n", username);
            Utils.pauseScreen();
            return -1;
        }
    }

    private static void handleDeleteAccount() {
        System.out.print("Enter username to delete: ");
        String username = Utils.sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            Utils.pauseScreen();
            return;
        }

        int userId = DatabaseManager.getUserIdByUsername(username);
        if (userId == -1) {
            System.out.println("User not found.");
            Utils.pauseScreen();
            return;
        }

        System.out.printf("Are you sure you want to delete user '%s'? (y/n): ", username);
        String confirm = Utils.sc.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            DatabaseManager.deleteUser(userId);
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
        Utils.pauseScreen();
    }

    private static int createNewUser(String suggestedUsername) {
        String username = suggestedUsername;
        
        while (true) {
            System.out.println("\n--- Create New User ---");
            if (username == null || username.isEmpty()) {
                System.out.print("Enter username: ");
                username = Utils.sc.nextLine().trim();
                while (username.isEmpty()) {
                    System.out.print("Username cannot be empty. Enter username: ");
                    username = Utils.sc.nextLine().trim();
                }
            } else {
                System.out.printf("Username: %s%n", username);
            }

            if (DatabaseManager.usernameExists(username)) {
                System.out.println("Username already exists. Please try another.");
                Utils.pauseScreen();
                username = ""; // Clear to prompt again
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("Enter password (min 8 chars, upper, lower, digit, special, no escapes): ");
            password = Utils.sc.nextLine().trim();
            if (isValidPassword(password)) {
                break;
            } else {
                System.out.println("Invalid password. Must be at least 8 chars, contain uppercase, lowercase, digit, and special char (!@#$%^&*()-_=+), and no backslashes/escapes.");
            }
        }

        String emailOrPhone;
        while (true) {
            System.out.print("Enter email or phone: ");
            emailOrPhone = Utils.sc.nextLine().trim();
            if (isValidEmail(emailOrPhone) || isValidPhone(emailOrPhone)) {
                break;
            } else {
                System.out.println("Invalid format. Must be a valid email or a 10-15 digit phone number.");
            }
        }

        DatabaseManager.createUser(username, password, emailOrPhone);
        System.out.println("User created successfully!");
        Utils.pauseScreen();
        return DatabaseManager.getUserIdByUsername(username);
    }

    private static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {
        return phone.matches("\\d{10,15}");
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        String specialChars = "!@#$%^&*()-_=+";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
            
            // Check for control characters (ASCII 0-31)
            if (c < 32) return false;
        }

        if (!(hasUpper && hasLower && hasDigit && hasSpecial)) return false;

        // Block all backslashes to ensure no literal escape sequences are present
        if (password.contains("\\")) {
            return false;
        }

        return true;
    }
}