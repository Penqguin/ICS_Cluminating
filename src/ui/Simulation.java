package src.ui;

import src.DatabaseManager;
import src.ui.Utils;
import java.util.Random;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

/**
 * Runs an interactive financial simulation showing inflation and salary change effects.
 * Users adjust expenses to maintain their income/expense ratio.
 */
public class Simulation {

    private static final Scanner sc = new Scanner(System.in);

    private static final String CONFIG_DIR = "config";
    private static final String SKIP_FILE = "skip_simulation_instructions.txt";

    /**
     * Checks whether the instructions file exists (indicating user chose to skip).
     * @return true if instructions should be shown
     */
    private static boolean shouldShowInstructions() {
        return !new File(CONFIG_DIR, SKIP_FILE).exists();
    }

    /**
     * Creates or deletes the skip file based on user preference.
     * @param skip true to skip instructions in the future
     */
    private static void setSkipInstructions(boolean skip) {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) dir.mkdirs();
            File skipFile = new File(CONFIG_DIR, SKIP_FILE);
            if (skip) {
                skipFile.createNewFile();
            } else {
                skipFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays simulation instructions to the user.
     */
    private static void showInstructions() {
        Utils.clearScreen();
        System.out.println("=".repeat(Utils.STANDARD_WIDTH));
        System.out.println("--- Welcome to the Financial Simulation ---");
        System.out.println("In this simulation, you will see how inflation and salary changes");
        System.out.println("affect your I/E (Income/Expense) ratio.");
        System.out.println("\nYour goal is to maintain or improve your I/E ratio");
        System.out.println("by adding, removing, or modifying your expenses.");
        System.out.println("\nInstructions:");
        System.out.println("1. Use the menu to adjust your expenses.");
        System.out.println("2. Aim for a I/E ratio close to your original.");
        System.out.println("3. Finish the simulation to see your final score and tips.");
        System.out.println("\n" + "-".repeat(Utils.STANDARD_WIDTH));
        System.out.print("Don't show instructions again? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            setSkipInstructions(true);
        }
        Utils.pauseScreen();
    }

    /**
     * Runs the interactive financial simulation for a user.
     * @param userId The user ID
     */
    public static void runSimulation(int userId) {
        if (shouldShowInstructions()) {
            showInstructions();
        }

        double currentIncome = DatabaseManager.getTotalIncome(userId);
        double currentExpense = DatabaseManager.getTotalExpenses(userId);

        if (currentExpense == 0) {
            Utils.clearScreen();
            System.out.println("Cannot simulate: No expenses recorded.");
            Utils.pauseScreen();
            return;
        }

        double originalIERatio = currentIncome / currentExpense;

        Random rand = new Random();
        double inflation = 0.02 + (0.10 - 0.0) * rand.nextDouble();
        double salaryIncrease = 0.0 + (0.02 - 0.0) * rand.nextDouble();

        double simulatedIncome = currentIncome * (1 + salaryIncrease);
        double simulatedExpense = currentExpense * (1 + inflation);

        boolean running = true;
        while (running) {
            Utils.clearScreen();
            System.out.println("=".repeat(Utils.STANDARD_WIDTH));
            System.out.println(Utils.HEADER_BANNER);
            System.out.println("=".repeat(Utils.STANDARD_WIDTH));
            System.out.println("--- Interactive Financial Simulation ---");
            System.out.println("Goal I/E Ratio: " + String.format("%.4f", originalIERatio));
            System.out.println();
            
            double currentIERatio = simulatedIncome / simulatedExpense;
            System.out.printf("%-22s $%,12.2f%n", "Current Income:", simulatedIncome);
            System.out.printf("%-22s $%,12.2f%n", "Current Expenses:", simulatedExpense);
            System.out.printf("%-22s %12.4f%n", "Current I/E Ratio:", currentIERatio);
            
            if (currentIERatio >= originalIERatio) {
                System.out.println("Status: I/E Ratio Goal Met!");
            } else {
                System.out.printf("Gap to Goal: %.4f%n", (originalIERatio - currentIERatio));
            }
            
            System.out.println("\n--- Actions ---");
            System.out.println("1. Add Expense      2. Remove Expense");
            System.out.println("3. Modify Expense   4. Finish & See Tips");
            System.out.print("Select an option (1-4): ");
            
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Enter amount to add: ");
                    try { simulatedExpense += Double.parseDouble(sc.nextLine()); } catch (NumberFormatException e) { System.out.println("Invalid input."); Utils.pauseScreen(); }
                    break;
                case "2":
                    System.out.print("Enter amount to remove: ");
                    try { simulatedExpense -= Double.parseDouble(sc.nextLine()); } catch (NumberFormatException e) { System.out.println("Invalid input."); Utils.pauseScreen(); }
                    break;
                case "3":
                    System.out.print("Enter new total expense amount: ");
                    try { simulatedExpense = Double.parseDouble(sc.nextLine()); } catch (NumberFormatException e) { System.out.println("Invalid input."); Utils.pauseScreen(); }
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    Utils.pauseScreen();
            }
        }

        Utils.clearScreen();
        System.out.println("--- Simulation Complete ---");
        double finalPERatio = simulatedIncome / simulatedExpense;
        System.out.println("Final I/E Ratio: " + String.format("%.4f", finalPERatio));

        if (Math.abs(finalPERatio - originalIERatio) / originalIERatio <= 0.02) {
            System.out.println("\n*** Congratulations! You are within 2% of your goal I/E ratio! ***");
        }

        System.out.println("\n--- Tips for Managing Financial Changes ---");
        System.out.println("- Prioritize high-interest debt repayment.");
        System.out.println("- Look for opportunities to diversify revenue streams.");
        System.out.println("- Consider subscription audits to reduce recurring costs.");
        System.out.println("- Build an emergency fund to buffer against inflation.");
        System.out.println("\nReturning to dashboard...");
        Utils.pauseScreen();
    }
}
