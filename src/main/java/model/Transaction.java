package model;

import java.util.Date;

/**
 * Records details of a financial operation (SA-007).
 */
public class Transaction {
    private String transactionId;
    private double amount;
    private String type; // e.g., "Transfer", "Deposit", "Withdrawal"
    private Date date;
    private String sourceAccount;
    private String destinationAccount;

    public Transaction(String transactionId, double amount, String type, String sourceAccount, String destinationAccount, Date date) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.date = date != null ? date : new Date();
    }
    
    // For new transactions
    public Transaction(String transactionId, double amount, String type, String sourceAccount, String destinationAccount) {
        this(transactionId, amount, type, sourceAccount, destinationAccount, null);
    }
    
    // For single-party transactions (like Deposit/Withdrawal, where destination is N/A or source is N/A)
    public Transaction(String transactionId, double amount, String type, String account, boolean isSource) {
        this(transactionId, amount, type, isSource ? account : "N/A", isSource ? "N/A" : account, null);
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getSourceAccount() { return sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public Date getDate() { return date; }

    public String getTransactionDetails() {
        return String.format("%s of $%.2f from %s to %s on %s", type, amount, sourceAccount, destinationAccount, date);
    }
}