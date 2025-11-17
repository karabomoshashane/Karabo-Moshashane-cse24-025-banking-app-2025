package dao;

import model.*;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private final String ACCOUNT_FIELDS = "accountNumber, balance, dateOpened, customerID, accountType, status, isActivated";

    // --- Helper Method to instantiate the correct Account subclass ---
    private Account createAccountFromResultSet(ResultSet rs, CustomerDAO customerDAO) throws SQLException {
        String accountNumber = rs.getString("accountNumber");
        double balance = rs.getDouble("balance");
        Date dateOpened = rs.getDate("dateOpened");
        String customerID = rs.getString("customerID");
        String accountType = rs.getString("accountType");
        boolean isActivated = rs.getBoolean("isActivated");

        // Fetch the associated Customer object
        Customer customer = customerDAO.getCustomerByID(customerID);
        if (customer == null) {
            System.err.println("DAO Error: Customer not found for account " + accountNumber);
            return null;
        }

        // Instantiate the correct subclass
        Account account;
        switch (accountType) {
            case "SavingsAccount":
                account = new SavingsAccount(accountNumber, customer, balance);
                break;
            case "ChequeAccount":
                account = new ChequeAccount(accountNumber, customer);
                break;
            case "InvestmentAccount":
                InvestmentAccount invAccount = new InvestmentAccount(accountNumber, customer, balance);
                invAccount.setActivated(isActivated);
                account = invAccount;
                break;
            default:
                System.err.println("DAO Error: Unknown account type: " + accountType);
                return null;
        }

        // Set common fields
        account.setBalance(balance);
        account.setDateOpened(dateOpened);
        return account;
    }

    // --- CREATE Operation ---
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (" + ACCOUNT_FIELDS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setDouble(2, account.checkBalance());
            pstmt.setDate(3, new java.sql.Date(account.getDateOpened().getTime()));
            pstmt.setString(4, account.getCustomer().getUserID());
            pstmt.setString(5, account.getClass().getSimpleName());

            String status = "Active";
            boolean isActivated = true;
            if (account instanceof InvestmentAccount) {
                if (account.checkBalance() < 500.00) {
                    status = "Pending Initial Deposit";
                    isActivated = false;
                }
            }

            pstmt.setString(6, status);
            pstmt.setBoolean(7, isActivated);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error creating account: " + e.getMessage());
            return false;
        }
    }

    // --- DELETE Operation ---
    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE accountNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error deleting account: " + e.getMessage());
            return false;
        }
    }

    // --- READ Operations ---
    public Account getAccountByNumber(String accountNumber, CustomerDAO customerDAO) {
        String sql = "SELECT " + ACCOUNT_FIELDS + " FROM accounts WHERE accountNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return createAccountFromResultSet(rs, customerDAO);
            }

        } catch (SQLException e) {
            System.err.println("DAO Error reading account by number: " + e.getMessage());
        }
        return null;
    }

    public List<Account> getAccountsByCustomerID(String customerID, CustomerDAO customerDAO) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT " + ACCOUNT_FIELDS + " FROM accounts WHERE customerID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Account account = createAccountFromResultSet(rs, customerDAO);
                    if (account != null) accounts.add(account);
                }
            }

        } catch (SQLException e) {
            System.err.println("DAO Error reading accounts by customer ID: " + e.getMessage());
        }
        return accounts;
    }

    public List<Account> getAllAccounts(CustomerDAO customerDAO) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT " + ACCOUNT_FIELDS + " FROM accounts";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Account account = createAccountFromResultSet(rs, customerDAO);
                if (account != null) accounts.add(account);
            }

        } catch (SQLException e) {
            System.err.println("DAO Error retrieving all accounts: " + e.getMessage());
        }
        return accounts;
    }

    // --- UPDATE Operations ---
    public boolean updateAccountBalance(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE accountNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, account.checkBalance());
            pstmt.setString(2, account.getAccountNumber());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error updating account balance: " + e.getMessage());
            return false;
        }
    }

    // --- STATISTICS ---
    public AccountStatistics getAccountStatistics() {
        AccountStatistics stats = new AccountStatistics();
        try (Connection conn = DBConnection.getConnection()) {
            String totalSQL = "SELECT COUNT(accountNumber), SUM(balance) FROM accounts";
            try (PreparedStatement pstmt = conn.prepareStatement(totalSQL);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalAccounts(rs.getInt(1));
                    stats.setTotalBalance(rs.getDouble(2));
                }
            }

            String typeSQL = "SELECT accountType, COUNT(*) FROM accounts GROUP BY accountType";
            try (PreparedStatement pstmt = conn.prepareStatement(typeSQL);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stats.getAccountsByType().put(rs.getString(1), rs.getInt(2));
                }
            }

        } catch (SQLException e) {
            System.err.println("DAO Error retrieving account statistics: " + e.getMessage());
        }
        return stats;
    }

    // --- Account Statistics Inner Class ---
    public static class AccountStatistics {
        private int totalAccounts;
        private double totalBalance;
        private java.util.Map<String, Integer> accountsByType;

        public AccountStatistics() {
            this.accountsByType = new java.util.HashMap<>();
        }

        public int getTotalAccounts() { return totalAccounts; }
        public void setTotalAccounts(int totalAccounts) { this.totalAccounts = totalAccounts; }
        public double getTotalBalance() { return totalBalance; }
        public void setTotalBalance(double totalBalance) { this.totalBalance = totalBalance; }
        public java.util.Map<String, Integer> getAccountsByType() { return accountsByType; }
        public void setAccountsByType(java.util.Map<String, Integer> accountsByType) { this.accountsByType = accountsByType; }
    }
}
