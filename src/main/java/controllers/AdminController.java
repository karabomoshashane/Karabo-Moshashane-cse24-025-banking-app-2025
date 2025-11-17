package controllers;

import dao.EmployeeDAO;
import dao.CustomerDAO;
import dao.AccountDAO;
import dao.TransactionDAO;
import model.Employee;
import model.Customer;
import model.Account;
import model.Transaction;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import dao.AccountDAO.AccountStatistics;

public class AdminController {
    private EmployeeDAO employeeDAO;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    
    public AdminController() {
        this.employeeDAO = new EmployeeDAO();
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (Exception e) {
            System.err.println("Error retrieving all employees: " + e.getMessage());
            return new ArrayList<Employee>();
        }
    }
    
    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.getAllCustomers();
        } catch (Exception e) {
            System.err.println("Error retrieving all customers: " + e.getMessage());
            return new ArrayList<Customer>();
        }
    }
    
    // --- Removed Locking/Unlocking Methods ---
    
    /**
     * Resets a user's password (for both Employee and Customer).
     * @param userID The ID of the user.
     * @param newPassword The new password.
     * @param userType "Customer" or "Employee".
     * @return true if successful, false otherwise.
     */
    public boolean resetUserPassword(String userID, String newPassword, String userType) {
        try {
            if ("Customer".equalsIgnoreCase(userType)) {
                return customerDAO.updateCustomerPassword(userID, newPassword);
            } else if ("Employee".equalsIgnoreCase(userType)) {
                return employeeDAO.updateEmployeePassword(userID, newPassword);
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error resetting password for user " + userID + ": " + e.getMessage());
            return false;
        }
    }

    public AccountStatistics getAccountStatistics() {
        try {
            return accountDAO.getAccountStatistics();
        } catch (Exception e) {
            System.err.println("Error retrieving account statistics: " + e.getMessage());
            return new AccountStatistics(); // Return empty stats on failure
        }
    }

    public List<Transaction> getAllTransactions() {
        try {
            // Need customerDAO to load accounts correctly
            return transactionDAO.getAllTransactions();
        } catch (Exception e) {
            System.err.println("Error retrieving all transactions: " + e.getMessage());
            return new ArrayList<Transaction>();
        }
    }

    // Create a new employee
    public boolean createEmployee(String username, String password, String role) {
        try {
            // Generate a unique employee ID
            String employeeID = "EMP" + System.currentTimeMillis();
            Employee employee = new Employee(employeeID, username, util.PasswordUtil.hashPassword(password), role);
            return employeeDAO.createEmployee(employee);
        } catch (Exception e) {
            System.err.println("Error creating employee: " + e.getMessage());
            return false;
        }
    }

    // Delete an employee
    public boolean deleteEmployee(String employeeID) {
        try {
            return employeeDAO.deleteEmployee(employeeID);
        } catch (Exception e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    // Create a new customer
    public boolean createCustomer(String username, String password, String name) {
        try {
            // Generate a unique customer ID
            String customerID = "CUST" + System.currentTimeMillis();
            model.Customer customer = new model.Customer(customerID, username, "", name);
            return customerDAO.createCustomer(customer, password);
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

    // Delete a customer
    public boolean deleteCustomer(String customerID) {
        try {
            return customerDAO.deleteCustomer(customerID);
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    // Backup database (placeholder implementation)
    public boolean backupDatabase() {
        try {
            // TODO: Implement actual database backup logic
            System.out.println("Database backup initiated...");
            // For now, just return true as a placeholder
            return true;
        } catch (Exception e) {
            System.err.println("Error backing up database: " + e.getMessage());
            return false;
        }
    }

    // Get audit trail (placeholder implementation)
    public List<String> getAuditTrail() {
        try {
            // TODO: Implement actual audit trail retrieval
            List<String> auditLogs = new ArrayList<>();
            auditLogs.add("Audit log entry 1: User login");
            auditLogs.add("Audit log entry 2: Account created");
            auditLogs.add("Audit log entry 3: Transaction processed");
            return auditLogs;
        } catch (Exception e) {
            System.err.println("Error retrieving audit trail: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
