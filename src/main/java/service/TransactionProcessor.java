package service; // Changed to 'service' for better architecture

import model.Account;

/**
 * Represents the business logic service for handling financial operations.
 */
public interface TransactionProcessor {
    /**
     * Processes a transfer between two accounts.
     * @param fromAccount The source account.
     * @param toAccount The destination account.
     * @param amount The amount to transfer.
     * @return true if the transfer was successful, false otherwise.
     */
    boolean processTransfer(Account fromAccount, Account toAccount, double amount);

    /**
     * Processes a deposit into an account.
     * @param toAccount The destination account.
     * @param amount The amount to deposit.
     * @return true if the deposit was successful, false otherwise.
     */
    boolean processDeposit(Account toAccount, double amount);

    /**
     * Processes a withdrawal from an account.
     * @param fromAccount The source account.
     * @param amount The amount to withdraw.
     * @return true if the withdrawal was successful, false otherwise.
     */
    boolean processWithdrawal(Account fromAccount, double amount);
}