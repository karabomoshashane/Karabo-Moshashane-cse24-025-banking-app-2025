package dao;

import model.Customer;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Create a new customer
    public boolean createCustomer(Customer customer, String plainPassword) {
        String sql = "INSERT INTO customers (userID, username, passwordHash, customerName, accountLocked, failedLoginAttempts) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getUserID());
            pstmt.setString(2, customer.getUsername());
            pstmt.setString(3, PasswordUtil.hashPassword(plainPassword));
            pstmt.setString(4, customer.getCustomerName());
            pstmt.setBoolean(5, customer.isLocked());
            pstmt.setInt(6, customer.getFailedLoginAttempts());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error creating customer: " + e.getMessage());
            return false;
        }
    }

    // Delete a customer by ID
    public boolean deleteCustomer(String customerID) {
        String sql = "DELETE FROM customers WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    // Get a customer by ID
    public Customer getCustomerByID(String userID) {
        String sql = "SELECT * FROM customers WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getString("userID"),
                            rs.getString("username"),
                            rs.getString("passwordHash"),
                            rs.getString("customerName")
                    );
                    customer.setLocked(rs.getBoolean("accountLocked"));
                    customer.setFailedLoginAttempts(rs.getInt("failedLoginAttempts"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error reading customer by ID: " + e.getMessage());
        }
        return null;
    }

    // Get a customer by username (for login)
    public Customer getCustomerByUsername(String username) {
        String sql = "SELECT * FROM customers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getString("userID"),
                            rs.getString("username"),
                            rs.getString("passwordHash"),
                            rs.getString("customerName")
                    );
                    customer.setLocked(rs.getBoolean("accountLocked"));
                    customer.setFailedLoginAttempts(rs.getInt("failedLoginAttempts"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error reading customer by username: " + e.getMessage());
        }
        return null;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("userID"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getString("customerName")
                );
                customer.setLocked(rs.getBoolean("accountLocked"));
                customer.setFailedLoginAttempts(rs.getInt("failedLoginAttempts"));
                customers.add(customer);
            }

        } catch (SQLException e) {
            System.err.println("DAO Error retrieving all customers: " + e.getMessage());
        }

        return customers;
    }

    // Lock/unlock customer account
    public boolean updateCustomerSecurityStatus(Customer customer) {
        String sql = "UPDATE customers SET accountLocked = ?, failedLoginAttempts = ? WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, customer.isLocked());
            pstmt.setInt(2, customer.getFailedLoginAttempts());
            pstmt.setString(3, customer.getUserID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error updating customer security status: " + e.getMessage());
            return false;
        }
    }

    // Update customer password
    public boolean updateCustomerPassword(String customerID, String newPassword) {
        String sql = "UPDATE customers SET passwordHash = ? WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, customerID);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error updating customer password: " + e.getMessage());
            return false;
        }
    }

    // Sample customers for testing
    public void createSampleCustomers() {
        if (getCustomerByUsername("user1") == null && getCustomerByID("CUST001") == null) {
            createCustomer(new Customer("CUST001", "user1", "", "Alice Smith"), "pass123");
        }
        if (getCustomerByUsername("user2") == null && getCustomerByID("CUST002") == null) {
            createCustomer(new Customer("CUST002", "user2", "", "Bob Johnson"), "pass123");
        }
    }
}
