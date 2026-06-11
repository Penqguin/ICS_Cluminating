package src.ui;

import src.DatabaseManager;
import src.InflationFetcher;

import java.util.Scanner;
import java.util.List;

public class Home {
  private static final Scanner sc = new Scanner(System.in);

  public static void displayHomepage(int userId) {
    boolean inHomepage = true;

    while (inHomepage) {
      Utils.clearScreen();

      // Fetch dynamic data from database
      double totalIncome = DatabaseManager.getTotalIncome(userId);
      double totalExpenses = DatabaseManager.getTotalExpenses(userId);
      double totalBalance = totalIncome - totalExpenses;
      List<String[]> recentTransactions = DatabaseManager.getRecentTransactions(userId);

      // 1. Header Banner
      System.out.println("=========================================================");
      System.out.println(" /$$$$$$$                  /$$           /$$          ");
      System.out.println("| $$__  $$                | $$          |__/          ");
      System.out.println("| $$  \\ $$ /$$   /$$  /$$$$$$$  /$$$$$$  /$$  /$$$$$$ ");
      System.out.println("| $$$$$$$ | $$  | $$ /$$__  $$ /$$__  $$| $$ /$$__  $$");
      System.out.println("| $$__  $$| $$  | $$| $$  | $$| $$  \\ $$| $$| $$$$$$$$");
      System.out.println("| $$  \\ $$| $$  | $$| $$  | $$| $$  | $$| $$| $$_____/");
      System.out.println("| $$$$$$$/|  $$$$$$/|  $$$$$$$|  $$$$$$$| $$|  $$$$$$$");
      System.out.println("|_______/  \\______/  \\_______/ \\____  $$|__/ \\_______/");
      System.out.println("                               /$$  \\ $$              ");
      System.out.println("                              |  $$$$$$/              ");
      System.out.println("                               \\______/               ");
      System.out.println("=========================================================");
      System.out.println("                FINANCIAL DASHBOARD                      ");
      System.out.println("=========================================================");

      // 2. Financial Summary Block (Formatted Outputs)
      System.out.printf("    Net Balance: $%,12.2f\n", totalBalance);
      System.out.printf("   Total Income: $%,12.2f\n", totalIncome);
      System.out.printf(" Total Expenses: $%,12.2f\n", totalExpenses);
      
      double inflationRate = InflationFetcher.getCanadaInflationRate();
      System.out.printf("  CAN Inflation: %12.1f%%\n", inflationRate);
      System.out.println("=========================================================");

      // 3. Mini 2D Array Quick-View (Recent Activities)
      System.out.println("  RECENT TRANSACTIONS:");
      System.out.println("  -----------------------------------------------------");
      System.out.printf("  | %-5s | %-15s | %-10s | %-7s |\n", "Date", "Description", "Amount", "Type");
      System.out.println("  -----------------------------------------------------");

      // Loop through the transactions to print rows
      for (String[] transaction : recentTransactions) {
        System.out.printf("  | %-5s | %-15s | $%9s | %-7s |\n",
            transaction[0],
            transaction[1],
            transaction[2],
            transaction[3]);
      }
      System.out.println("  -----------------------------------------------------");
      System.out.println("=========================================================");

      // 4. Navigation Menu
      System.out.println("  1. [View Full Ledger]       2. [Add Income/Expense]");
      System.out.println("  3. [Analyze Graphs/Trends]  4. [Practice Problems]");
      System.out.println("  5. [Global Leaderboard]     6. [Logout and Exit]");
      System.out.println("=========================================================");
      System.out.print("  Select an option (1-6): ");

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
