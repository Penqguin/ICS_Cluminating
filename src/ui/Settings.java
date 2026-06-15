package src.ui;

import src.User;
import src.DatabaseManager;

/**
 * Handles account settings: username, password, and 2FA changes.
 */
public class Settings {
    private static final java.util.Scanner sc = Utils.sc;

    /**
     * Displays the settings menu and handles user selections.
     * @param userId The user ID
     */
    public static void displaySettingsMenu(int userId) {
        boolean inSettings = true;
        while (inSettings) {
            Utils.clearScreen();
            System.out.println("=".repeat(Utils.STANDARD_WIDTH));
            System.out.println(Utils.HEADER_BANNER);
            System.out.println("=".repeat(Utils.STANDARD_WIDTH));
            System.out.println("  Dashboard > Account Settings");
            System.out.println("-".repeat(Utils.STANDARD_WIDTH));
            System.out.println("1. Change Username");
            System.out.println("2. Change Password");
            System.out.println("3. Toggle 2FA");
            System.out.println("4. Back to Dashboard");
            System.out.print("Select an option (1-4): ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    changeUsername(userId);
                    break;
                case "2":
                    changePassword(userId);
                    break;
                case "3":
                    toggle2FA(userId);
                    break;
                case "4":
                    inSettings = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    Utils.pauseScreen();
            }
        }
    }

    /**
     * Changes the username for the given user.
     * @param userId The user ID
     */
    private static void changeUsername(int userId) {
        System.out.print("Enter new username: ");
        String newUsername = sc.nextLine().trim();
        if (!User.exists(newUsername)) {
            String query = "UPDATE users SET username = ? WHERE user_id = ?";
            try (java.sql.PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(query)) {
                pstmt.setString(1, newUsername);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                System.out.println("Username updated successfully.");
            } catch (java.sql.SQLException e) {
                System.err.println("Error updating username: " + e.getMessage());
            }
        } else {
            System.out.println("Username already exists.");
        }
        Utils.pauseScreen();
    }

    /**
     * Changes the password for the given user.
     * @param userId The user ID
     */
    private static void changePassword(int userId) {
        System.out.print("Enter new password (min 8 chars, mixed case, digit, special): ");
        String newPassword = sc.nextLine().trim();
        if (User.isValidPassword(newPassword)) {
            String query = "UPDATE users SET password_hash = ? WHERE user_id = ?";
            try (java.sql.PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(query)) {
                pstmt.setString(1, DatabaseManager.hashPassword(newPassword));
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                System.out.println("Password updated successfully.");
            } catch (java.sql.SQLException e) {
                System.err.println("Error updating password: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid password format.");
        }
        Utils.pauseScreen();
    }

    /**
     * Toggles two-factor authentication on/off for the given user.
     * @param userId The user ID
     */
    private static void toggle2FA(int userId) {
        String query = "UPDATE users SET two_fa_enabled = NOT two_fa_enabled WHERE user_id = ?";
        try (java.sql.PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("2FA status toggled.");
        } catch (java.sql.SQLException e) {
            System.err.println("Error updating 2FA: " + e.getMessage());
        }
        Utils.pauseScreen();
    }
}
