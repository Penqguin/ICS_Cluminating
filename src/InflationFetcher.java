package src;

/**
 * Provides inflation rate data for the application.
 * Uses a static value based on StatCan data (April 2026).
 */
public class InflationFetcher {
    /**
     * Gets the current inflation rate for Canada.
     * @return Inflation rate as a percentage (e.g., 2.8 for 2.8%)
     */
    public static double getCanadaInflationRate() {
        return 2.8;
    }
}
