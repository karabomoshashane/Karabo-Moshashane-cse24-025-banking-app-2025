package controllers;

import dao.TransactionDAO;
import model.Transaction;
import model.Account;
import java.util.List;
import java.util.ArrayList;

public class TransactionController {
    private TransactionDAO transactionDAO;
    private final double OTP_THRESHOLD = 1000.00; // TP-010: Threshold for OTP/PIN validation 

    public TransactionController() {
        this.transactionDAO = new TransactionDAO();
    }
    
    private String generateTransactionId() {
        return "TX" + System.currentTimeMillis() + "_" + (int)(Math.random() * 900 + 100);
    }
    
    public boolean logTransaction(String transactionType, double amount, 
                                 String sourceAccount, String destinationAccount) {
        try {
            Transaction transaction = new Transaction(
                generateTransactionId(),
                amount,
                transactionType,
                sourceAccount,
                destinationAccount
            );
            
            return transactionDAO.logTransaction(transaction);
        } catch (Exception e) {
            System.err.println("Error logging transaction: " + e.getMessage());
            return false;
        }
    }
    
    public List<Transaction> getTransactionHistory(String accountNumber) {
        try {
            return transactionDAO.getTransactionHistory(accountNumber);
        } catch (Exception e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public double getTotalTransactionAmount(String accountNumber, String transactionType) {
        // ... (Implementation logic from snippet remains the same)
        return 0.0;
    }
    
    // New method for checking TP-010 
    public boolean requiresPinOrOTP(double amount) {
        return amount > OTP_THRESHOLD;
    }
}