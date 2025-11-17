package model;

import java.util.Date;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected Date dateOpened;
    protected Customer customer;
    protected String status;

    private static final double MIN_BALANCE_CHECK = 0.0;

    public Account(String accountNumber, Customer customer, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.dateOpened = new Date(); 
        this.balance = initialDeposit;
        this.status = "Active";
    }
    
    // NEW Constructor used by DAO to reconstruct an object from the database
    public Account(String accountNumber, Customer customer) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = 0.0; // Will be set by setBalance after fetching from DB
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public void deposit(double amount, String sourceDetails) {
        deposit(amount);
    }

    public boolean withdraw(double amount) {
        // Core withdrawal logic (overridden in SavingsAccount for TP-008)
        if (amount > 0 && this.balance - amount >= MIN_BALANCE_CHECK) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public double checkBalance() {
        return this.balance;
    }

    public abstract void calculateInterest();

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }
    
    // NEW Setter for DAO to initialize balance from database.
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    // Add the setter
    public void setStatus(String status) {
        this.status = status;
    }
}