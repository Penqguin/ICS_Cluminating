package src.ui;

import src.DatabaseManager;
import src.ui.Utils;
import java.util.Random;

public class Simulation {

    public static void runSimulation(int userId) {
        // Data Gathering
        double currentIncome = DatabaseManager.getTotalIncome(userId);
        double currentExpense = DatabaseManager.getTotalExpenses(userId);

        if (currentExpense == 0) {
            Utils.clearScreen();
            System.out.println("Cannot simulate: No expenses recorded.");
            Utils.pauseScreen();
            return;
        }

        double originalPERatio = currentIncome / currentExpense;

        // Simulation Logic
        Random rand = new Random();
        double inflation = 0.0 + (0.05 - 0.0) * rand.nextDouble(); // 0% to 5%
        double salaryIncrease = 0.0 + (0.03 - 0.0) * rand.nextDouble(); // 0% to 3%

        double simulatedIncome = currentIncome * (1 + salaryIncrease);
        double simulatedExpense = currentExpense * (1 + inflation);

        boolean running = true;
        while (running) {
            Utils.clearScreen();
            System.out.println(Utils.HEADER_BANNER);
            System.out.println("--- Interactive Financial Simulation ---");
            System.out.println("Goal P/E Ratio: " + String.format("%.4f", originalPERatio));
            System.out.println();
            
            double currentPERatio = simulatedIncome / simulatedExpense;
            System.out.printf("Current Income: $%,12.2f%n", simulatedIncome);
            System.out.printf("Current Expenses: $%,12.2f%n", simulatedExpense);
            System.out.printf("Current P/E Ratio: %.4f%n", currentPERatio);
            
            if (currentPERatio >= originalPERatio) {
                System.out.println("Status: P/E Ratio Goal Met!");
            } else {
                System.out.printf("Gap to Goal: %.4f%n", (originalPERatio - currentPERatio));
            }
            
            System.out.println("\n--- Actions ---");
            System.out.println("1. Add Expense      2. Remove Expense");
            System.out.println("3. Modify Expense   4. Finish & See Tips");
            System.out.print("Select an option (1-4): ");
            
            String choice = Utils.sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Enter amount to add: ");
                    try { simulatedExpense += Double.parseDouble(Utils.sc.nextLine()); } catch (NumberFormatException e) { System.out.println("Invalid input."); Utils.pauseScreen(); }
                    break;
                case "2":
                    System.out.print("Enter amount to remove: ");
                    try { simulatedExpense -= Double.parseDouble(Utils.sc.nextLine()); } catch (NumberFormatException e) { System.out.println("Invalid input."); Utils.pauseScreen(); }
                    break;
                case "3":
                    System.out.print("Enter new total expense amount: ");
                    try { simulatedExpense = Double.parseDouble(Utils.sc.nextLine()); } catch (NumberFormatException e) { System.out.println("Invalid input."); Utils.pauseScreen(); }
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    Utils.pauseScreen();
            }
        }

        // Final Tips
        Utils.clearScreen();
        System.out.println("--- Simulation Complete ---");
        System.out.println("Final P/E Ratio: " + String.format("%.4f", (simulatedIncome / simulatedExpense)));
        System.out.println("\n--- Tips for Managing Financial Changes ---");
        System.out.println("- Prioritize high-interest debt repayment.");
        System.out.println("- Look for opportunities to diversify revenue streams.");
        System.out.println("- Consider subscription audits to reduce recurring costs.");
        System.out.println("- Build an emergency fund to buffer against inflation.");
        System.out.println("\nReturning to dashboard...");
        Utils.pauseScreen();
    }
}
