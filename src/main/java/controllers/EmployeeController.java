package controllers;

import dao.CustomerDAO;
import dao.AccountDAO;
import dao.TransactionDAO;
import model.*;

import java.util.List;
import java.util.ArrayList;

public class EmployeeController {

    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public EmployeeController() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // --- Customer Operations ---

    public boolean createCustomer(String userID, String username, String customerName, String plainPassword) {
        try {
            Customer existing = customerDAO.getCustomerByID(userID);
            if (existing != null) return false; // Customer already exists

            Customer customer = new Customer(userID, username, "", customerName);
            return customerDAO.createCustomer(customer, plainPassword);
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.getAllCustomers();
        } catch (Exception e) {
            System.err.println("Error retrieving all customers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean updateCustomerPassword(String customerID, String newPassword) {
        try {
            return customerDAO.updateCustomerPassword(customerID, newPassword);
        } catch (Exception e) {
            System.err.println("Error updating customer password: " + e.getMessage());
            return false;
        }
    }

    // --- Account Operations ---

    public boolean createAccount(String accountNumber, String customerID, String accountType, double initialDeposit) {
        try {
            Customer customer = customerDAO.getCustomerByID(customerID);
            if (customer == null) return false;

            Account account;
            switch (accountType) {
                case "SavingsAccount":
                    account = new SavingsAccount(accountNumber, customer, initialDeposit);
                    break;
                case "ChequeAccount":
                    account = new ChequeAccount(accountNumber, customer);
                    break;
                case "InvestmentAccount":
                    account = new InvestmentAccount(accountNumber, customer, initialDeposit);
                    break;
                default:
                    return false;
            }

            if (initialDeposit > 0) account.deposit(initialDeposit);

            return accountDAO.createAccount(account);

        } catch (Exception e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    public boolean closeAccount(String accountNumber) {
        try {
            return accountDAO.deleteAccount(accountNumber);
        } catch (Exception e) {
            System.err.println("Error closing account: " + e.getMessage());
            return false;
        }
    }

    public List<Account> getAllAccounts() {
        try {
            return accountDAO.getAllAccounts(customerDAO);
        } catch (Exception e) {
            System.err.println("Error retrieving all accounts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean processDeposit(String accountNumber, double amount) {
        if (amount <= 0) return false;
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber, customerDAO);
            if (account == null) return false;

            account.deposit(amount);
            return accountDAO.updateAccountBalance(account);

        } catch (Exception e) {
            System.err.println("Error processing deposit: " + e.getMessage());
            return false;
        }
    }

    public boolean processWithdrawal(String accountNumber, double amount) {
        if (amount <= 0) return false;
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber, customerDAO);
            if (account == null) return false;

            boolean success = account.withdraw(amount);
            if (success) {
                return accountDAO.updateAccountBalance(account);
            } else {
                return false; // insufficient funds or restrictions
            }

        } catch (Exception e) {
            System.err.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }

    // --- Transaction Operations ---

    public List<Transaction> getAllTransactions() {
        try {
            return transactionDAO.getAllTransactions();
        } catch (Exception e) {
            System.err.println("Error retrieving all transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
