package model;

/**
 * Standard transaction account, inheriting core functionality from Account.
 */
public class ChequeAccount extends Account {

    public ChequeAccount(String accountNumber, Customer customer) {
        super(accountNumber, customer, 0.0);
    }
    
    // NEW Constructor for DAO (matches the new Account constructor)
    public ChequeAccount(String accountNumber, Customer customer, double initialDeposit) {
        super(accountNumber, customer, initialDeposit);
    }

    // Cheque accounts typically don't accrue interest in this simple model,
    // but the abstract method must be implemented.
    @Override
    public void calculateInterest() {
        // No interest calculation for standard Cheque Account
    }
}