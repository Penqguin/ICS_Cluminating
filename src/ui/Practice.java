package src.ui;

import java.util.Scanner;

public class Practice {
    private static final Scanner sc = Utils.sc;

    public static void runProblems() {
        boolean inPractice = true;
        while (inPractice) {
            Utils.clearScreen();
            System.out.println("=== Financial Literacy Practice ===");
            System.out.println("Test your knowledge on budgeting and inflation!");
            System.out.println();
            System.out.println("1. Inflation Impact Problem");
            System.out.println("2. Savings Goal Problem");
            System.out.println("3. Back to Dashboard");
            System.out.print("\nSelect a module: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    runInflationProblem();
                    break;
                case "2":
                    runSavingsProblem();
                    break;
                case "3":
                    inPractice = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    Utils.pauseScreen();
                    break;
            }
        }
    }

    private static void runInflationProblem() {
        Utils.clearScreen();
        double currentPrice = 100.0;
        double inflation = InflationFetcher.getCanadaInflationRate();
        
        System.out.println("--- Module: Inflation Impact ---");
        System.out.printf("Problem: A basket of groceries costs $%.2f today.\n", currentPrice);
        System.out.printf("If the annual inflation rate is %.1f%%, what will it cost in 1 year?\n", inflation);
        
        System.out.print("\nYour Answer ($): ");
        String answer = sc.nextLine();
        
        double expected = currentPrice * (1 + inflation / 100.0);
        
        System.out.println("\n[A] Show Answer  |  [B] Back");
        String choice = sc.nextLine().trim().toUpperCase();
        if (choice.equals("A")) {
            System.out.printf("The answer is $%.2f (Calculation: %.2f * 1.0%d)\n", expected, currentPrice, (int)(inflation * 10));
        }
        Utils.pauseScreen();
    }

    private static void runSavingsProblem() {
        Utils.clearScreen();
        System.out.println("--- Module: Savings Goal ---");
        System.out.println("Problem: You earn $3,000 per month and spend $2,400.");
        System.out.println("What is your monthly savings rate?");
        
        System.out.print("\nYour Answer (%): ");
        String answer = sc.nextLine();
        
        double expected = ((3000 - 2400) / 3000.0) * 100;
        
        System.out.println("\n[A] Show Answer  |  [B] Back");
        String choice = sc.nextLine().trim().toUpperCase();
        if (choice.equals("A")) {
            System.out.printf("The answer is %.1f%% (Calculation: (3000-2400)/3000 * 100)\n", expected);
        }
        Utils.pauseScreen();
    }
}
