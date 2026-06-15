import src.ui.*;
import src.DatabaseManager;

/**
 * Main entry point for the Financial Budgeting Application.
 * Initializes the database and starts the login flow.
 */
class Main {
    /**
     * Program entry point.
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        int userId = Login.promptForUser();
        if (userId != -1) {
            Home.displayHomepage(userId);
        }
    }
}
