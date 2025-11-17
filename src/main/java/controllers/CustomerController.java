package controllers;

import dao.CustomerDAO;
import dao.AccountDAO;
import dao.TransactionDAO;
import model.Customer;
import model.Account;
import model.Transaction;
import java.util.List;
import java.util.ArrayList;

public class CustomerController {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    
    public CustomerController() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    public Customer getCustomerProfile(String customerID) {
        try {
            return customerDAO.getCustomerByID(customerID);
        } catch (Exception e) {
            System.err.println("Error retrieving customer profile: " + e.getMessage());
            return null;
        }
    }
    
    public List<Account> getCustomerAccounts(String customerID) {
        try {
            // FIXED: Use the new method signature that requires CustomerDAO
            return accountDAO.getAccountsByCustomerID(customerID, customerDAO);
        } catch (Exception e) {
            System.err.println("Error retrieving customer accounts: " + e.getMessage());
            return new ArrayList<Account>();
        }
    }
    
    public List<Transaction> getAccountTransactions(String accountNumber) {
        try {
            return transactionDAO.getTransactionHistory(accountNumber);
        } catch (Exception e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
            return new ArrayList<Transaction>();
        }
    }
    
    // ... rest of the helper methods (hasInvestmentAccount, hasSavingsAccount, hasChequeAccount)
    // ... remain the same as they correctly use getCustomerAccounts(customerID)
    
    public boolean hasInvestmentAccount(String customerID) {
        try {
            List<Account> accounts = getCustomerAccounts(customerID);
            for (Account account : accounts) {
                if (account instanceof model.InvestmentAccount) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking for investment account: " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasSavingsAccount(String customerID) {
        try {
            List<Account> accounts = getCustomerAccounts(customerID);
            for (Account account : accounts) {
                if (account instanceof model.SavingsAccount) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking for savings account: " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasChequeAccount(String customerID) {
        try {
            List<Account> accounts = getCustomerAccounts(customerID);
            for (Account account : accounts) {
                if (account instanceof model.ChequeAccount) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking for cheque account: " + e.getMessage());
            return false;
        }
    }
}