package dao;

import model.Transaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean logTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (transactionID, amount, transactionType, sourceAccount, destinationAccount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getType());
            pstmt.setString(4, transaction.getSourceAccount());
            pstmt.setString(5, transaction.getDestinationAccount());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error logging transaction: " + e.getMessage());
            return false;
        }
    }
    
    public List<Transaction> getTransactionHistory(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        // Get transactions where the account is either the source or the destination
        String sql = "SELECT * FROM transactions WHERE sourceAccount = ? OR destinationAccount = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction(
                            rs.getString("transactionID"),
                            rs.getDouble("amount"),
                            rs.getString("transactionType"),
                            rs.getString("sourceAccount"),
                            rs.getString("destinationAccount"),
                            rs.getTimestamp("timestamp")
                    );
                    transactions.add(tx);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error retrieving transaction history: " + e.getMessage());
        }
        return transactions;
    }
    
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction tx = new Transaction(
                        rs.getString("transactionID"),
                        rs.getDouble("amount"),
                        rs.getString("transactionType"),
                        rs.getString("sourceAccount"),
                        rs.getString("destinationAccount"),
                        rs.getTimestamp("timestamp")
                );
                transactions.add(tx);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO Error retrieving all transactions: " + e.getMessage());
        }
        
        return transactions;
    }
}