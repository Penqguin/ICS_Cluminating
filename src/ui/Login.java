package src.ui;

import src.DatabaseManager;
import src.User;
import src.ui.Utils;
import java.util.List;

/**
 * Handles user authentication: login, account creation, deletion, and 2FA.
 */
public class Login {

    /**
     * Main entry point for user authentication flow.
     * Displays login menu and handles user selection.
     * @return The authenticated user's ID, or -1 on failure
     */
    public static int promptForUser() {
        while (true) {
            Utils.clearScreen();
            System.out.println("=== Welcome to Financial App ===");
            System.out.println();

            List<String[]> users = User.getAllUsers();
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
            System.out.println("[4] Message Viewer");
            System.out.println("[5] Exit");
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
                    viewMessages();
                    break;
                case "5":
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

    /**
     * Handles the login flow: username/password entry with 2FA verification.
     * @return User ID if login successful, -1 otherwise
     */
    private static int handleLogin() {
        System.out.print("Enter username: ");
        String username = Utils.sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            Utils.pauseScreen();
            return -1;
        }

        if (User.exists(username)) {
            System.out.print("Enter password: ");
            String password = Utils.sc.nextLine().trim();

            int userId = User.login(username, password);
            if (userId != -1) {
                User user = User.getUserById(userId);
                if (user != null && user.isTwoFactorEnabled()) {
                    user.enableTwoFactor(user.getContactDestination()); // Regernerate code
                    logVerificationCode(user.getContactDestination(), user.getVerificationCode());
                    System.out.println("\n[2FA Required] A verification code has been sent to " + user.getContactDestination() + ". Check messages? (y/n)");
                    String choice = Utils.sc.nextLine().trim();
                    switch (choice) {
                        case "Y":
                        case "y":
                            viewMessages();
                    }
                    System.out.print("Enter 6-digit verification code: ");
                    String code = Utils.sc.nextLine().trim();
                    if (user.verifyCode(code)) {
                        System.out.println("2FA Verified!");
                    } else {
                        System.out.println("Invalid verification code. Login failed.");
                        Utils.pauseScreen();
                        return -1;
                    }
                }
                
                if (user != null) {
                    user.showLoadingScreen();
                    System.out.println("\n[Advice] " + user.getAdvice());
                    Utils.pauseScreen();
                }
                return userId;
            } else {
                System.out.println("Incorrect password.");
                System.out.print("Forgot password? (y/n): ");
                String forgot = Utils.sc.nextLine().trim().toLowerCase();
                if (forgot.equals("y") || forgot.equals("yes")) {
                    
                }
                Utils.pauseScreen();
                return -1;
            }
        } else {
            System.out.printf("User '%s' not found.%n", username);
            Utils.pauseScreen();
            return -1;
        }
    }

    /**
     * Handles account deletion with confirmation.
     */
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
            User.deleteAccount(userId);
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
        Utils.pauseScreen();
    }

    /**
     * Handles new user creation with password validation and optional 2FA setup.
     * @param suggestedUsername Optional pre-filled username
     * @return The new user's ID
     */
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

            if (User.exists(username)) {
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
            if (User.isValidPassword(password)) {
                break;
            } else {
                System.out.println("Invalid password. Must be at least 8 chars, contain uppercase, lowercase, digit, and special char (!@#$%^&*()-_=+), and no backslashes/escapes.");
            }
        }

        String emailOrPhone;
        while (true) {
            System.out.print("Enter email or phone: ");
            emailOrPhone = Utils.sc.nextLine().trim();
            if (User.isValidEmail(emailOrPhone) || User.isValidPhone(emailOrPhone)) {
                break;
            } else {
                System.out.println("Invalid format. Must be a valid email or a 10-15 digit phone number.");
            }
        }

        System.out.print("Enable Two-Factor Authentication? (y/n): ");
        boolean enable2FA = Utils.sc.nextLine().trim().toLowerCase().startsWith("y");

        int userId = User.createAccount(username, password, emailOrPhone);
        if (userId != -1) {
            User user = User.getUserById(userId);
            if (enable2FA && user != null) {
                user.enableTwoFactor(emailOrPhone);
                logVerificationCode(emailOrPhone, user.getVerificationCode());
                
                System.out.println("\n[2FA Setup] A verification code has been sent to " + emailOrPhone + ". Check messages? (y/n)");
                
                String choice = Utils.sc.nextLine().trim();
                switch (choice) {
                    case "Y":
                    case "y":
                        viewMessages();
                }

                System.out.print("Enter verification code to confirm: ");
                String code = Utils.sc.nextLine().trim();
                
                if (user.verifyCode(code)) {
                    System.out.println("2FA Enabled successfully!");
                    user.saveToDatabase(userId);
                } else {
                    System.out.println("Verification failed. 2FA will not be enabled.");
                }
            }
            System.out.println("User created successfully!");
            if (user != null) {
                user.showLoadingScreen();
                System.out.println("\n[Advice] " + user.getAdvice());
            }
            Utils.pauseScreen();
            return userId;
        } else {
            System.out.println("Failed to create user. Please check your details and try again.");
            Utils.pauseScreen();
            return -1;
        }
    }

    /**
     * Logs a verification code to a file for the simulated 2FA system.
     * @param contact The email or phone
     * @param code The 6-digit verification code
     */
    private static void logVerificationCode(String contact, String code) {
        String safeContact = contact.replaceAll("[^a-zA-Z0-9@.]", "_");
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("messages", safeContact + ".txt");
            java.nio.file.Files.createDirectories(path.getParent());
            String message = String.format("[%s] Your verification code is: %s\n", 
                java.time.LocalDateTime.now().toString().replace('T', ' ').substring(0, 19), code);
            java.nio.file.Files.write(path, message.getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (java.io.IOException e) {
            System.err.println("[Error] Failed to log verification code: " + e.getMessage());
        }
    }

    /**
     * Displays all messages sent to a given contact (for simulated 2FA).
     */
    private static void viewMessages() {
        System.out.print("Enter contact (email/phone) to view messages: ");
        String contact = Utils.sc.nextLine().trim();
        String safeContact = contact.replaceAll("[^a-zA-Z0-9@.]", "_");
        java.io.File file = new java.io.File("messages/" + safeContact + ".txt");
        if (file.exists()) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                System.out.println("\n--- Messages for " + contact + " ---");
                for (String line : lines) {
                    System.out.println(line);
                }
            } catch (java.io.IOException e) {
                System.out.println("Error reading messages.");
            }
        } else {
            System.out.println("No messages found for this contact.");
        }
        Utils.pauseScreen();
    }

    /**
     * Placeholder for password reset functionality.
     * @param contact The contact identifier
     */
    private static void resetPassword(String contact) {
        
        Utils.pauseScreen();
    }
}