package src.model;

/**
 * Abstract base class for all financial transactions.
 * Demonstrates Inheritance and Polymorphism.
 */
public abstract class Transaction {
    protected String date;
    protected String description;
    protected double amount;

    /**
     * Constructs a new Transaction.
     * @param date The transaction date.
     * @param description A brief description.
     * @param amount The transaction amount.
     */
    public Transaction(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    /** @return The date of the transaction. */
    public String getDate() { return date; }
    /** @return The description of the transaction. */
    public String getDescription() { return description; }
    /** @return The amount of the transaction. */
    public double getAmount() { return amount; }

    /**
     * Abstract method to get the type of transaction.
     * To be overridden by subclasses (Method Overriding).
     * @return The transaction type (e.g., "Income", "Expense").
     */
    public abstract String getType();

    /**
     * Polymorphic method to get a display string for the transaction.
     * @return A formatted string suitable for UI display.
     */
    public String getDisplayString() {
        return String.format("%-10s | %-20s | $%10.2f | %-7s", date, description, amount, getType());
    }
}
