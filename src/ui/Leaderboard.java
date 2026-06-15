package src.ui;

import src.ui.Utils.*;

import src.DatabaseManager;
import java.util.List;

/**
 * Displays the global savings leaderboard showing top savers by savings rate.
 */
public class Leaderboard {
    /**
     * Displays the top 10 users ranked by savings rate.
     */
    public static void display() {
        Utils.clearScreen();
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));
        System.out.println("                GLOBAL SAVINGS LEADERBOARD               ");
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));
        System.out.println("  Rank | Username           | Savings Rate (%)           ");
        System.out.println("  -----+--------------------+----------------------------");

        List<String[]> topSavers = DatabaseManager.getTopSavers();
        if (topSavers.isEmpty()) {
            System.out.println("  No users found yet. Start budgeting to climb the ranks!");
        } else {
            for (int i = 0; i < topSavers.size(); i++) {
                String[] saver = topSavers.get(i);
                System.out.printf("  %03d  | %-18s | %-26s\n", 
                                  i + 1, saver[0], saver[1] + "%");
            }
        }

        System.out.println("  -----+--------------------+----------------------------");
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));
        System.out.println("  Savings Rate = (Total Income - Total Expenses) / Income");
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));
        Utils.pauseScreen();
    }
}
