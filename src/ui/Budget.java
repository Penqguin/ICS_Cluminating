package src.ui;

import src.DatabaseManager;
import src.model.Transaction;
import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Manages budget-related operations: transaction ledger, graphs, and scatterplots.
 */
public class Budget {
  private static final Scanner sc = Utils.sc;

  /**
   * Parses a date input in various formats (DD, DD/MM, DD/MM/YYYY) into DD/MM/YYYY.
   * @param input The date input string
   * @return Formatted date string in DD/MM/YYYY
   */
  public static String parseDate(String input) {
    try {
        int day, month, year;
        if (input.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] parts = input.split("/");
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } else if (input.matches("\\d{1,2}/\\d{1,2}")) {
            String[] parts = input.split("/");
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = LocalDate.now().getYear();
        } else if (input.matches("\\d{1,2}")) {
            day = Integer.parseInt(input);
            month = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
        } else {
            return null;
        }
        LocalDate.of(year, month, day);
        return String.format("%02d/%02d/%d", day, month, year);
    } catch (Exception e) {
        return null;
    }
  }

  /**
   * Displays the full transaction ledger with options to add, delete, or view graphs.
   * @param userId The user ID
   */
  public static void displayFullLedger(int userId) {
    boolean inLedger = true;

    while (inLedger) {
        Utils.clearScreen();

        double totalIncome = DatabaseManager.getTotalIncome(userId);
        double totalExpenses = DatabaseManager.getTotalExpenses(userId);
        List<Transaction> recentTransactions = DatabaseManager.getRecentTransactions(userId);
        src.User user = src.User.getUserById(userId);

        System.out.println("=".repeat(Utils.STANDARD_WIDTH));
        System.out.println("  DASHBOARD > FULL TRANSACTION LEDGER                    ");
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));

        System.out.printf("  [Total Records: %3d]   [Incomes: $%,10.2f]   [Expenses: $%,10.2f]\n", 
                          recentTransactions.size(), totalIncome, totalExpenses);
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));

        System.out.println("  ID   | Date       | Description          | Amount      | Type    ");
        System.out.println("  -----+------------+----------------------+-------------+---------");

        if (recentTransactions.isEmpty()) {
            System.out.println("  No transactions found. Use [A] to add your first entry!");
        } else {
            for (int i = 0; i < recentTransactions.size(); i++) {
                Transaction t = recentTransactions.get(i);
                int displayId = i + 1; 
                System.out.printf("  %03d  | %s\n", displayId, t.getDisplayString());
            }
        }

        System.out.println("  -----+------------+----------------------+-------------+---------");
        System.out.println(Utils.equalSignLine);

        System.out.println("  [A] Add New  | [D] Delete | [G] Graphs | [B] Back");
        System.out.println(Utils.equalSignLine);
        System.out.print("  Execute Command: ");

        String command = sc.nextLine().trim().toUpperCase();

        switch (command) {
            case "A":
                handleAddition(userId);
                break;
            case "D":
                System.out.print("\n  Enter ID row number to purge: ");
                try {
                    int deleteIdx = Integer.parseInt(sc.nextLine()) - 1;
                    DatabaseManager.deleteTransaction(userId, deleteIdx);
                    
                    if (user != null) user.saveToDatabase(userId);
                } catch (NumberFormatException e) {
                    System.out.println("  [!] Invalid ID.");
                }
                Utils.pauseScreen();
                break;
            case "G":
                displayGraphs(userId);
                break;
            case "B":
                System.out.println("\n  Returning to Financial Dashboard...");
                inLedger = false;
                break;
            default:
                System.out.println("\n  [!] Command unrecognized.");
                Utils.pauseScreen();
                break;
        }
    }
  }

  /**
   * Handles adding a new transaction (income or expense) for the user.
   * @param userId The user ID
   */
  public static void handleAddition(int userId) {
      System.out.println("\n  --- ADD NEW ENTRY ---");
      System.out.print("  Enter Date (DD/MM/YYYY, DD/MM, or DD): ");
      String dateInput = sc.nextLine();
      String date = parseDate(dateInput);
      if (date == null) {
          System.out.println("  [!] Invalid date. Please enter a real calendar date.");
          Utils.pauseScreen();
          return;
      }
      System.out.print("  Enter Description: ");
      String desc = sc.nextLine();
      System.out.print("  Enter Amount: ");
      double amount = 0;
      try {
          amount = Double.parseDouble(sc.nextLine());
      } catch (NumberFormatException e) {
          System.out.println("  [!] Invalid amount.");
          Utils.pauseScreen();
          return;
      }
      System.out.print("  Type ( (I)ncome / (E)xpense ): ");
      String typeChoice = sc.nextLine().trim().toUpperCase();
      String type;
      if (typeChoice.startsWith("I")) {
         type = "Income"; 
      } else if (typeChoice.startsWith("E")) {
          type = "Expense";
      } else {
          System.out.println("  [!] Type must be 'Income' or 'Expense'.");
          Utils.pauseScreen();
          return;
      }
      DatabaseManager.addTransaction(userId, date, desc, amount, type);

      src.User user = src.User.getUserById(userId);
      if (user != null) user.saveToDatabase(userId);

      Utils.pauseScreen();
  }

  /**
   * Displays visual bar chart comparing income vs expenses, plus savings rate.
   * @param userId The user ID
   */
  public static void displayGraphs(int userId) {

      Utils.clearScreen();
      double income = DatabaseManager.getTotalIncome(userId);
      double expenses = DatabaseManager.getTotalExpenses(userId);

      System.out.println("=== Financial Visualizations ===");
      System.out.printf("Total Income:   $%,.2f\n", income);
      System.out.printf("Total Expenses: $%,.2f\n", expenses);
      System.out.println();

      int totalWidth = 50;
      double max = Math.max(income, expenses);
      if (max == 0) max = 1;

      int incomeWidth = (int) ((income / max) * totalWidth);
      int expenseWidth = (int) ((expenses / max) * totalWidth);

      System.out.print("Income:   ");
      for (int i = 0; i < incomeWidth; i++) System.out.print("#");
      System.out.println();

      System.out.print("Expenses: ");
      for (int i = 0; i < expenseWidth; i++) System.out.print("!");
      System.out.println();

      System.out.println("\nLegend: # = Income, ! = Expense");

      src.User user = src.User.getUserById(userId);
      if (user != null) {
          System.out.printf("\nSavings Rate: %.1f%%\n", user.getSavingsRate() * 100);
      }

      System.out.println("\n[S] View Balance Over Time Scatterplot | [B] Back");
      System.out.print("Selection: ");
      String choice = sc.nextLine().trim().toUpperCase();
      if (choice.equals("S")) {
          displayScatterplot(userId);
      }
  }

  /**
   * Displays an ASCII scatterplot of balance over time for a selected period.
   * Uses a 2D char array to render the plot.
   * @param userId The user ID
   */
  public static void displayScatterplot(int userId) {
      List<Transaction> transactions = DatabaseManager.getAllTransactionsSorted(userId);
      if (transactions.isEmpty()) {
          System.out.println("No transactions to visualize.");
          Utils.pauseScreen();
          return;
      }

      System.out.println("\nSelect Period: [1] 1 Week | [2] 1 Month | [3] 1 Year");
      System.out.print("Choice: ");
      String periodChoice = sc.nextLine().trim();
      int days = 30; 
      if (periodChoice.equals("1")) days = 7;
      else if (periodChoice.equals("2")) days = 30;
      else if (periodChoice.equals("3")) days = 365;

      LocalDate endDate = LocalDate.now();
      LocalDate startDate = endDate.minusDays(days);

      double cumulative = 0;
      List<LocalDate> plotDates = new java.util.ArrayList<>();
      List<Double> plotBalances = new java.util.ArrayList<>();

      for (Transaction t : transactions) {
          try {
              LocalDate d = LocalDate.parse(t.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
              double amt = t.getAmount();
              if (t.getType().equalsIgnoreCase("Expense")) amt = -amt;
              cumulative += amt;

              if (!d.isBefore(startDate) && !d.isAfter(endDate)) {
                  plotDates.add(d);
                  plotBalances.add(cumulative);
              }
          } catch (Exception e) {}
      }

      if (plotBalances.isEmpty()) {
          System.out.println("No data for the selected period.");
          Utils.pauseScreen();
          return;
      }

      Utils.clearScreen();
      System.out.println("=== Balance Over Time Scatterplot ===");
      
      double minB = plotBalances.stream().min(Double::compare).get();
      double maxB = plotBalances.stream().max(Double::compare).get();
      if (maxB == minB) maxB += 1;

      int height = 15;
      int width = 60;
      char[][] grid = new char[height + 1][width + 1];
      for (int i = 0; i <= height; i++) {
          for (int j = 0; j <= width; j++) grid[i][j] = ' ';
      }

      long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
      if (totalDays == 0) totalDays = 1;

      for (int i = 0; i < plotDates.size(); i++) {
          long dPos = java.time.temporal.ChronoUnit.DAYS.between(startDate, plotDates.get(i));
          int x = (int) ((double) dPos / totalDays * width);
          int y = (int) ((plotBalances.get(i) - minB) / (maxB - minB) * height);
          
          if (x >= 0 && x <= width && y >= 0 && y <= height) {
              grid[height - y][x] = '*';
          }
      }

      for (int i = 0; i <= height; i++) {
          double threshold = maxB - (i * (maxB - minB) / height);
          System.out.printf("%10.2f |", threshold);
          for (int j = 0; j <= width; j++) {
              System.out.print(grid[i][j]);
          }
          System.out.println();
      }
      System.out.println("           " + "-".repeat(width + 1));
      System.out.println("           " + startDate + " ".repeat(width - 20) + endDate);
      
      Utils.pauseScreen();
  }
}
