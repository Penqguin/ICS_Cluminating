package src.ui;

import java.util.Scanner;

class Home {
  private static final Scanner sc = new Scanner(System.in);

  // Mock data structures representing application state 
  // TODO: replace with fetched account info
  private static double totalBalance = 2450.75;
  private static double totalIncome = 3200.00;
  private static double totalExpenses = 749.25;

  private static String[][] recentTransactions = {
    {"06/05", "Part-time Job ", "1200.00", "Income "},
    {"06/06", "Grocery Store ", " 124.50", "Expense"},
    {"06/07", "Transit Pass  ", "  55.00", "Expense"},
    {"06/08", "Freelance Gig ", " 350.00", "Income "}
  };

  public static void displayHomepage() {
    boolean inHomepage = true;

    while (inHomepage) {
      Utils.clearScreen();

      // 1. Header Banner
      System.out.println("=========================================================");
      System.out.println("      __  ___                             _  __ ");
      System.out.println("     /  |/  /___  ____  ___  __  __      | |/ / ");
      System.out.println("    / /|_/ / __ \\/ __ \\/ _ \\/ / / /______| ' /  ");
      System.out.println("   / /  / / /_/ / / / /  __/ /_/ /______/ . \\   ");
      System.out.println("  /_/  /_/\\____/_/ /_/\\___/\\__, /      /_/|\\_\\  ");
      System.out.println("                          /____/                ");
      System.out.println("=========================================================");
      System.out.println("                FINANCIAL DASHBOARD                      ");
      System.out.println("=========================================================");

      // 2. Financial Summary Block (Formatted Outputs)
      System.out.printf("  Net Balance:  $%,12.2f\n", totalBalance);
      System.out.printf("  Total Income: $%,12.2f   |  Total Expenses: $%,12.2f\n", totalIncome, totalExpenses);
      System.out.println("=========================================================");

      // 3. Mini 2D Array Quick-View (Recent Activities)
      System.out.println("  RECENT TRANSACTIONS:");
      System.out.println("  -----------------------------------------------------");
      System.out.printf("  | %-5s | %-15s | %-10s | %-7s |\n", "Date", "Description", "Amount", "Type");
      System.out.println("  -----------------------------------------------------");
      
      // Loop through the 2D array matrix to print rows
      for (int i = 0; i < recentTransactions.length; i++) {
          System.out.printf("  | %-5s | %-15s | $%9s | %-7s |\n", 
                            recentTransactions[i][0], 
                            recentTransactions[i][1], 
                            recentTransactions[i][2], 
                            recentTransactions[i][3]);
      }
      System.out.println("  -----------------------------------------------------");
      System.out.println("=========================================================");

      // 4. Navigation Menu
      System.out.println("  1. [View Full Ledger]      2. [Add Income/Expense]");
      System.out.println("  3. [Analyze Graphs/Trends]  4. [Practice Problems]");
      System.out.println("  5. [Logout and Exit]");
      System.out.println("=========================================================");
      System.out.print("  Select an option (1-5): ");

      String choice = sc.nextLine().trim();

      switch (choice) {
        case "1":
          // viewFullLedger(); 
          System.out.println("\n  Opening full budget sheet...");
          pauseScreen();
          break;
        case "2":
          // modifyTransactions();
          System.out.println("\n  Opening transaction manager...");
          pauseScreen();
          break;
        case "3":
          // viewVisualizations();
          System.out.println("\n  Generating financial trends...");
          pauseScreen();
          break;
        case "4":
          // runProblems();
          System.out.println("\n  Loading sample mathematical practice modules...");
          pauseScreen();
          break;
        case "5":
          System.out.println("\n  Logging out secure session. Goodbye!");
          inHomepage = false;
          break;
        default:
          System.out.println("\n  [!] Invalid choice. Please select an option from 1 to 5.");
          pauseScreen();
          break;
      }
    }
  }


  private static void pauseScreen() {
    System.out.print("  Press [Enter] to continue...");
    sc.nextLine();
  }

  public static void main(String[] args) {
    displayHomepage();
  }
}
