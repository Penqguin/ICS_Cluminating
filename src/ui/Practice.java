package src.ui;

import java.util.Scanner;

import src.InflationFetcher;

public class Practice {
    private static final Scanner sc = Utils.sc;
    // TODO: add some more
    private static final String[][] savingsProblem = {
        {"You earn $3,000 per month and spend $2,400. What is your monthly savings rate?","The answer is 20.0% (Calculation: (3000-2400)/3000 * 100)"}, {
        "You have a savings goal of $10,000. If you save $500 per month, how many months will it take to reach your goal?","The answer is 20 months (Calculation: 10000/500)"}, {
        "You have $5,000 in savings and want to reach $10,000. If you save $500 per month, how many months will it take to reach your goal?","The answer is 10 months (Calculation: (10000-5000)/500)"}, {
        }
    };
    
    private static final String[][] inflationProblem = {
        {"A basket of groceries costs $100.00 today. If the annual inflation rate is 2.5%, what will it cost in 1 year?","The answer is $102.50 (Calculation: 100.00 * (1 + 2.5/100))"}, {
        "An item costs $50.00 today. With a 3.0% inflation rate, what will it cost in 2 years?","The answer is $53.05 (Calculation: 50.00 * (1 + 3.0/100)^2)"}, {
        "A service costs $200.00 today. If inflation is 1.5% per year, how much will it cost in 5 years?","The answer is $215.61 (Calculation: 200.00 * (1 + 1.5/100)^5)"}, {
        }
    };

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
                    runProblem("Inflation");
                    break;
                case "2":
                    runProblem("Savings");
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

    private static void runProblem(String type) {
        String[][] problems;
        String moduleTitle;
        
        if (type.equalsIgnoreCase("Inflation")) {
            problems = inflationProblem;
            moduleTitle = "Inflation Impact";
        } else {
            problems = savingsProblem;
            moduleTitle = "Savings Goal";
        }
        
        Utils.clearScreen();
        System.out.printf("\n--- Module: %s ---\n", moduleTitle);

        // Cycle through problems defined in the problems array
        for (int i = 0; i < problems.length; i++) {
            String[] entry = problems[i];
            if (entry == null || entry.length < 2) continue;
            String prompt = entry[0];
            String solution = entry[1];

            // empty sentinel to stop
            if (prompt == null || prompt.trim().isEmpty()) break;

            Utils.clearScreen();
            System.out.println("Problem: " + prompt);
            System.out.print("\nYour Answer: ");
            sc.nextLine(); // consume user's answer (not validated)

            System.out.println("\n[A] Show Answer  |  [B] Back to Dashboard");
            String choice = sc.nextLine().trim().toUpperCase();
            if (choice.equals("B")) {
                return;
            } else {
                System.out.println("\n" + solution);
                Utils.pauseScreen();
            }
        }
        // finished all problems, return to dashboard
        Utils.clearScreen();
        System.out.printf("You have completed all %s problems. Returning to dashboard...%n", moduleTitle);
    }
}