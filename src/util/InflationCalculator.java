package src.util;

/**
 * Utility class for financial calculations involving inflation.
 * Demonstrates recursion through future value calculation.
 */
public class InflationCalculator {

    /**
     * Calculates the future value of an amount after a certain number of years
     * given an annual inflation rate, using recursion.
     * 
     * @param amount The initial amount
     * @param rate   The annual inflation rate as a decimal (e.g., 0.025 for 2.5%)
     * @param years  The number of years
     * @return The future value after inflation
     */
    public static double calculateFutureValueRecursive(double amount, double rate, int years) {
        if (years <= 0) {
            return amount;
        }
        return calculateFutureValueRecursive(amount * (1 + rate), rate, years - 1);
    }
}
