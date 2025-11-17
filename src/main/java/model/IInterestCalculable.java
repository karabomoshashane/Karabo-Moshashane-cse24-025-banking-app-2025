package model;

/**
 * Defines the contract for all account types that accrue interest (AM-002).
 */
public interface IInterestCalculable {
    /**
     * Calculates and applies the monthly interest to the account balance.
     */
    void calculateInterest();
}