package model;

/**
 * Represents the business logic service for handling financial operations.
 */
public interface TransactionProcessor {
    // Note: Implementation details moved to a Service layer in a full MVC/Layered architecture,
    // but kept as an interface here per the class diagram.

    boolean logTransaction(String transactionType, String details);

    boolean processTransfer(Account fromAccount, Account toAccount, double amount);
}