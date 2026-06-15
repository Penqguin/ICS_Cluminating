package src.model;

/**
 * Represents an income transaction.
 * Demonstrates Inheritance from the Transaction class.
 */
public class IncomeTransaction extends Transaction {
    
    /**
     * Constructs a new IncomeTransaction.
     * @param date The date.
     * @param description The description.
     * @param amount The income amount.
     */
    public IncomeTransaction(String date, String description, double amount) {
        super(date, description, amount);
    }

    @Override
    public String getType() {
        return "Income";
    }

    @Override
    public String getDisplayString() {
        return "[+] " + super.getDisplayString();
    }
}
