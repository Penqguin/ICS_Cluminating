package src.ui;

import src.ui.Utils.*;

import src.DatabaseManager;
import src.InflationFetcher;
import src.model.Transaction;

import java.util.Scanner;
import java.util.List;

/**
 * Main dashboard homepage displayed after user login.
 * Shows financial summary, recent transactions, and navigation menu.
 */
public class Home {
  private static final Scanner sc = new Scanner(System.in);

  /**
   * Displays the main financial dashboard homepage with navigation.
   * @param userId The authenticated user's ID
   */
  public static void displayHomepage(int userId) {
    boolean inHomepage = true;

    while (inHomepage) {
      Utils.clearScreen();

      double totalIncome = DatabaseManager.getTotalIncome(userId);
      double totalExpenses = DatabaseManager.getTotalExpenses(userId);
      double totalBalance = totalIncome - totalExpenses;
      List<Transaction> recentTransactions = DatabaseManager.getRecentTransactions(userId);

      System.out.println("=".repeat(Utils.STANDARD_WIDTH));
      System.out.println(Utils.HEADER_BANNER);
      System.out.println("=".repeat(Utils.STANDARD_WIDTH));

      System.out.println("                FINANCIAL DASHBOARD                      ");
      System.out.println("=".repeat(Utils.STANDARD_WIDTH));

      System.out.printf("    Net Balance: $%,12.2f\n", totalBalance);
      System.out.printf("   Total Income: $%,12.2f\n", totalIncome);
      System.out.printf(" Total Expenses: $%,12.2f\n", totalExpenses);
      
      double inflationRate = InflationFetcher.getCanadaInflationRate();
      System.out.printf("  CAN Inflation: %12.1f%%\n", inflationRate);
      System.out.println("=".repeat(Utils.STANDARD_WIDTH));

      System.out.println("  RECENT TRANSACTIONS:");
      System.out.println("-".repeat(Utils.STANDARD_WIDTH));
      System.out.printf("  | %-10s | %-12s | %-10s | %-7s |\n", "Date", "Description", "Amount", "Type");
      System.out.println("-".repeat(Utils.STANDARD_WIDTH));

      for (Transaction t : recentTransactions) {
        System.out.printf("  | %-10s | %-12s | $%9.2f | %-7s |\n",
            t.getDate(),
            t.getDescription(),
            t.getAmount(),
            t.getType());
      }
      System.out.println("-".repeat(Utils.STANDARD_WIDTH));
      System.out.println("=".repeat(Utils.STANDARD_WIDTH));

      System.out.println("  1. [View Full Ledger]       2. [Add Income/Expense]");
      System.out.println("  3. [Analyze Graphs/Trends]  4. [Practice Problems]");
      System.out.println("  5. [Global Leaderboard]     6. [Run Budget Simulation]");
      System.out.println("  7. [Settings]               8. [Logout and Exit]");
      System.out.println("=".repeat(Utils.STANDARD_WIDTH));
      System.out.print("  Select an option (1-8): ");

      String choice = sc.nextLine().trim();

      switch (choice) {
        case "1":
          Budget.displayFullLedger(userId);
          break;
        case "2":
          Budget.handleAddition(userId);
          break;
        case "3":
          Budget.displayGraphs(userId);
          break;
        case "4":
          Practice.runProblems();
          break;
        case "5":
          Leaderboard.display();
          break;
        case "6":
          Simulation.runSimulation(userId);
          break;
        case "7":
          Settings.displaySettingsMenu(userId);
          break;
        case "8":
          System.out.println("\n  Logging out secure session. Goodbye!");
          inHomepage = false;
          break;
        default:
          System.out.println("\n  [!] Invalid choice.");
          Utils.pauseScreen();
          break;
      }

    }
  }
}
