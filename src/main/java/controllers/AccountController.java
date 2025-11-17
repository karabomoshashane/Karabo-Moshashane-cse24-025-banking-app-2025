package controllers;

import dao.AccountDAO;
import dao.CustomerDAO;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class AccountController {

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;

    public AccountController() {
        this.accountDAO = new AccountDAO();
        this.customerDAO = new CustomerDAO();
    }

    // --- Get account details ---
    public Account getAccountDetails(String accountNumber) {
        try {
            return accountDAO.getAccountByNumber(accountNumber, customerDAO);
        } catch (Exception e) {
            System.err.println("Error retrieving account details: " + e.getMessage());
            return null;
        }
    }

    // --- Create a new account ---
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

            // Deposit initial amount if applicable
            if (initialDeposit > 0) {
                account.deposit(initialDeposit);
            }

            return accountDAO.createAccount(account);

        } catch (Exception e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    // --- Close/Delete account ---
    public boolean closeAccount(String accountNumber) {
        try {
            return accountDAO.deleteAccount(accountNumber);
        } catch (Exception e) {
            System.err.println("Error closing account: " + e.getMessage());
            return false;
        }
    }

    // --- Deposit to an account ---
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

    // --- Withdraw from an account ---
    public boolean processWithdrawal(String accountNumber, double amount) {
        if (amount <= 0) return false;
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber, customerDAO);
            if (account == null) return false;

            boolean success = account.withdraw(amount);
            if (success) {
                return accountDAO.updateAccountBalance(account);
            } else {
                return false; // insufficient funds or savings account restriction
            }

        } catch (Exception e) {
            System.err.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }

    // --- Transfer funds between accounts ---
    public boolean processTransfer(String fromAccountNum, String toAccountNum, double amount) {
        if (amount <= 0) return false;

        try {
            Account fromAccount = accountDAO.getAccountByNumber(fromAccountNum, customerDAO);
            Account toAccount = accountDAO.getAccountByNumber(toAccountNum, customerDAO);

            if (fromAccount == null || toAccount == null) return false;

            if (fromAccount.withdraw(amount)) {
                toAccount.deposit(amount);

                accountDAO.updateAccountBalance(fromAccount);
                accountDAO.updateAccountBalance(toAccount);

                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error transferring funds: " + e.getMessage());
            return false;
        }
    }

    // --- Get all accounts for dashboard refresh ---
    public List<Account> getAllAccounts() {
        try {
            return accountDAO.getAllAccounts(customerDAO);
        } catch (Exception e) {
            System.err.println("Error retrieving all accounts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // --- Calculate interest for eligible accounts ---
    public boolean calculateInterest(String accountNumber) {
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber, customerDAO);
            if (account != null && account instanceof IInterestCalculable) {
                ((IInterestCalculable) account).calculateInterest();
                return accountDAO.updateAccountBalance(account);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error calculating interest: " + e.getMessage());
            return false;
        }
    }

    // Alias for processTransfer to match CustomerDashboard usage
    public boolean transferFunds(String fromAccount, String toAccount, double amount) {
        return processTransfer(fromAccount, toAccount, amount);
    }

    // Alias for processWithdrawal to match CustomerDashboard usage
    public boolean withdrawFunds(String accountNumber, double amount) {
        return processWithdrawal(accountNumber, amount);
    }

    // Alias for createAccount to match BankEmployeeDashboard usage
    public boolean openAccount(String customerID, String accountType) {
        // Generate a unique account number
        String accountNumber = "ACC" + System.currentTimeMillis();
        return createAccount(accountNumber, customerID, accountType, 0.0);
    }
}
