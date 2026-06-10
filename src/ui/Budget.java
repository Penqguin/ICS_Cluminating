package src.ui;

import src.DatabaseManager;
import java.util.Scanner;
import java.util.List;

public class Budget {
  private static final Scanner sc = new Scanner(System.in);
  private static final int CURRENT_USER_ID = 1; // Default user for now

  public static void displayFullLedger() {
    boolean inLedger = true;

    while (inLedger) {
        Utils.clearScreen();

        // Fetch dynamic data from database
        double totalIncome = DatabaseManager.getTotalIncome(CURRENT_USER_ID);
        double totalExpenses = DatabaseManager.getTotalExpenses(CURRENT_USER_ID);
        List<String[]> recentTransactions = DatabaseManager.getRecentTransactions(CURRENT_USER_ID);

        // 1. Header & Navigation Path
        System.out.println("=========================================================");
        System.out.println("  DASHBOARD > FULL TRANSACTION LEDGER                    ");
        System.out.println("=========================================================");

        // 2. Local Performance Metrics
        System.out.printf("  [Total Records: %-3d]   [Incomes: $%,-10.2f]   [Expenses: $%,-10.2f]\n", 
                          recentTransactions.size(), totalIncome, totalExpenses);
        System.out.println("=========================================================");

        // 3. Main Data Table Frame
        System.out.println("  ID   | Date       | Description          | Amount      | Type    ");
        System.out.println("  -----+------------+----------------------+-------------+---------");

        for (int i = 0; i < recentTransactions.size(); i++) {
            String[] transaction = recentTransactions.get(i);
            int displayId = i + 1; 
            String date = transaction[0];
            String desc = transaction[1].trim();
            String amtStr = transaction[2].trim();
            String type = transaction[3].trim();

            String typeIndicator = type.equalsIgnoreCase("Income") ? "[+]" : "[-]";

            System.out.printf("  %03d  | 2026-%-5s | %-20s | $%10s | %-3s %-7s\n", 
                              displayId, date, desc, amtStr, typeIndicator, type);
        }

        System.out.println("  -----+------------+----------------------+-------------+---------");
        System.out.println("=========================================================");
        
        // 4. Ledger Context Actions
        System.out.println("  [A] Add New Entry  |  [D] Delete Entry  |  [B] Back to Dashboard");
        System.out.println("=========================================================");
        System.out.print("  Execute Command: ");

        String command = sc.nextLine().trim().toUpperCase();

        switch (command) {
            case "A":
                System.out.println("\n  --- ADD NEW ENTRY ---");
                System.out.print("  Enter Date (MM/DD): ");
                String date = sc.nextLine();
                System.out.print("  Enter Description: ");
                String desc = sc.nextLine();
                System.out.print("  Enter Amount: ");
                double amount = 0;
                try {
                    amount = Double.parseDouble(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("  [!] Invalid amount.");
                    Utils.pauseScreen();
                    break;
                }
                System.out.print("  Type (Income/Expense): ");
                String type = sc.nextLine();
                if (!type.equalsIgnoreCase("Income") && !type.equalsIgnoreCase("Expense")) {
                    System.out.println("  [!] Type must be 'Income' or 'Expense'.");
                    Utils.pauseScreen();
                    break;
                }
                DatabaseManager.addTransaction(CURRENT_USER_ID, date, desc, amount, 
                    type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
                Utils.pauseScreen();
                break;
            case "D":
                System.out.print("\n  Enter ID row number to purge: ");
                try {
                    int deleteIdx = Integer.parseInt(sc.nextLine()) - 1;
                    DatabaseManager.deleteTransaction(CURRENT_USER_ID, deleteIdx);
                } catch (NumberFormatException e) {
                    System.out.println("  [!] Invalid ID.");
                }
                Utils.pauseScreen();
                break;
            case "B":
                System.out.println("\n  Returning to Financial Dashboard...");
                inLedger = false;
                break;
            default:
                System.out.println("\n  [!] Command unrecognized. Use A, D, or B.");
                Utils.pauseScreen();
                break;
        }
    }
}
}
