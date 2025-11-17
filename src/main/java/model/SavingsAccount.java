package model;

/**
 * Savings Account, implements IInterestCalculable.
 */
public class SavingsAccount extends Account implements IInterestCalculable {
    private static final double MONTHLY_INTEREST_RATE = 0.0005; // 0.05% (AM-002.2)

    /**
     * FIX: Updated constructor to accept initialDeposit to align with Account superclass.
     */
    public SavingsAccount(String accountNumber, Customer customer, double initialDeposit) {
        super(accountNumber, customer, initialDeposit);
    }
    
    // Optional DAO constructor for data retrieval
    public SavingsAccount(String accountNumber, Customer customer, double balance, java.util.Date dateOpened) {
        super(accountNumber, customer, balance);
        this.dateOpened = dateOpened;
    }
    
    public String getAccountType() { return "SavingsAccount"; }

    /**
     * Overrides the withdraw method to strictly prohibit direct withdrawals (TP-008).
     * Funds must be transferred out first.
     */
    @Override
    public boolean withdraw(double amount) {
        System.out.println("ERROR: Withdrawals are strictly prohibited from a Savings Account (TP-008). Funds must be transferred.");
        return false;
    }

    /**
     * Calculates and applies the monthly interest rate of 0.05% (AM-002.2).
     */
    @Override
    public void calculateInterest() {
        double interest = this.balance * MONTHLY_INTEREST_RATE;
        this.balance += interest;
        System.out.printf("Applied $%.2f interest to Savings Account %s. New balance: $%.2f\n", interest, this.accountNumber, this.balance);
    }
}