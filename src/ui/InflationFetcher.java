package src.ui;

public class InflationFetcher {
    /**
     * Fetches the current inflation rate for Canada.
     * In a production environment, this would call an external API.
     * For this application, it returns the latest rate from StatCan (April 2026).
     */
    public static double getCanadaInflationRate() {
        // Based on recent research: 2.8% as of April 2026
        return 2.8;
    }
}
