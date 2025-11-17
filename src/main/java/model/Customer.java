package model;

public class Customer extends User {
    private String customerName; // Added for completeness

    public Customer(String userID, String username, String passwordHash, String customerName) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.customerName = customerName;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    @Override
    public boolean hasPermission(String requiredRole) {
        // Customers do not have bank staff roles.
        return false;
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}