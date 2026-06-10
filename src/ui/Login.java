package src.ui;

import src.DatabaseManager;
import src.ui.Utils;
import java.util.List;

public class Login {
    public static int promptForUser() {
        Utils.clearScreen();
        System.out.println("=== Welcome to Financial App ===");
        System.out.println();

        System.out.print("Enter username: ");
        String username = Utils.sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            Utils.pauseScreen();
            return promptForUser();
        }

        if (DatabaseManager.usernameExists(username)) {
            // Existing user, ask for password
            System.out.print("Enter password: ");
            String password = Utils.sc.nextLine().trim();

            if (DatabaseManager.verifyPassword(username, password)) {
                System.out.println("Login successful!");
                Utils.pauseScreen();
                return DatabaseManager.getUserIdByUsername(username);
            } else {
                System.out.println("Incorrect password. Please try again.");
                Utils.pauseScreen();
                return promptForUser();
            }
        } else {
            // User not found, ask to create
            System.out.printf("User '%s' not found. Would you like to create a new user? (y/n): ", username);
            String answer = Utils.sc.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return createNewUser(username);
            } else {
                System.out.println("Returning to login...");
                Utils.pauseScreen();
                return promptForUser();
            }
        }
    }

    private static int createNewUser(String suggestedUsername) {
        System.out.println("\n--- Create New User ---");
        String username = suggestedUsername;
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
            return createNewUser("");
        }

        System.out.print("Enter password: ");
        String password = Utils.sc.nextLine().trim();
        while (password.isEmpty()) {
            System.out.print("Password cannot be empty. Enter password: ");
            password = Utils.sc.nextLine().trim();
        }

        System.out.print("Enter email or phone: ");
        String emailOrPhone = Utils.sc.nextLine().trim();
        while (emailOrPhone.isEmpty()) {
            System.out.print("Email or phone cannot be empty. Enter email or phone: ");
            emailOrPhone = Utils.sc.nextLine().trim();
        }

        DatabaseManager.createUser(username, password, emailOrPhone);
        System.out.println("User created successfully!");
        Utils.pauseScreen();
        return DatabaseManager.getUserIdByUsername(username);
    }
}