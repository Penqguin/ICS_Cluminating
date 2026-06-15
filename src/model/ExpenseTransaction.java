package src.model;

/**
 * Represents an expense transaction.
 * Demonstrates Inheritance from the Transaction class.
 */
public class ExpenseTransaction extends Transaction {

    /**
     * Constructs a new ExpenseTransaction.
     * @param date The date.
     * @param description The description.
     * @param amount The expense amount.
     */
    public ExpenseTransaction(String date, String description, double amount) {
        super(date, description, amount);
    }

    @Override
    public String getType() {
        return "Expense";
    }

    @Override
    public String getDisplayString() {
        return "[-] " + super.getDisplayString();
    }
}
