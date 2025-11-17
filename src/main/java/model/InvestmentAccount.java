package model;

import java.util.Date;

/**
 * Investment Account, implements IInterestCalculable.
 * Enforces a minimum initial deposit for activation (TP-009).
 */
public class InvestmentAccount extends Account implements IInterestCalculable {
    private static final double MONTHLY_INTEREST_RATE = 0.05; // 5% (AM-002.1) 
    private static final double MIN_INITIAL_DEPOSIT = 500.00; // BWP500.00 (TP-009) 
    private boolean isActivated = false;

    // Standard constructor for opening a new account with an initial deposit.
    public InvestmentAccount(String accountNumber, Customer customer, double initialDeposit) {
        // Call parent constructor with 0.0 balance, as deposit will handle setting the actual balance
        super(accountNumber, customer, 0.0); 
        
        // Use the overridden deposit method to activate the account if the deposit is sufficient.
        this.deposit(initialDeposit);
    }
    
    // DAO constructor for reconstructing an account from the database.
    public InvestmentAccount(String accountNumber, Customer customer, double balance, Date dateOpened, boolean isActivated) {
        super(accountNumber, customer, balance);
        this.dateOpened = dateOpened; // Must set dateOpened manually since super() defaults it to new Date()
        this.isActivated = isActivated;
        // The 'status' property is inherited from Account.java (once fixed).
        this.status = isActivated ? "Active" : "Pending Initial Deposit";
    }
    
    // Overriding the base Account constructor from the original snippet (which didn't take deposit)
    // NOTE: If your AccountDAO needs a constructor without initialDeposit for accounts like ChequeAccount, 
    // you might need to adjust the way accounts are created/reconstructed in the DAO. 
    // Assuming the DAO is fixed to use the correct constructor with balance and activation status.

    /**
     * Overrides deposit to enforce minimum initial deposit for activation (TP-009).
     */
    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            // Standard check for valid deposit amount
            System.out.println("ERROR: Deposit amount must be positive.");
            return;
        }

        if (!isActivated) {
            if (amount >= MIN_INITIAL_DEPOSIT) {
                this.balance += amount; // Manually update balance since super.deposit() is bypassed for logging
                this.isActivated = true;
                this.status = "Active";
                System.out.println("Investment Account activated with initial deposit (TP-009).");
            } else {
                this.status = "Pending Initial Deposit";
                System.out.println("ERROR: Initial deposit must be at least BWP" + MIN_INITIAL_DEPOSIT + " to activate the Investment Account (TP-009).");
            }
        } else {
            this.balance += amount; // Standard deposit
        }
    }

    /**
     * Calculates and applies the monthly interest rate of 5% (AM-002.1).
     */
    @Override
    public void calculateInterest() {
        if (isActivated) {
            double interest = this.balance * MONTHLY_INTEREST_RATE;
            this.balance += interest;
            System.out.printf("Applied $%.2f interest to Investment Account %s. New balance: $%.2f\n", interest, this.accountNumber, this.balance);
        } else {
            System.out.println("Interest not applied. Investment Account " + this.accountNumber + " is not activated (TP-009).");
        }
    }
    
    // Custom getters and setters
    public boolean isActivated() { return isActivated; }
    
    public void setActivated(boolean isActivated) { 
        this.isActivated = isActivated;
        this.status = isActivated ? "Active" : "Pending Initial Deposit";
    }
}