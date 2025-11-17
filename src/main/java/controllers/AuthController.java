package controllers;

import dao.CustomerDAO;
import dao.EmployeeDAO;
import model.Customer;
import model.Employee;
import model.User;
import util.PasswordUtil;

public class AuthController {
    private CustomerDAO customerDAO;
    private EmployeeDAO employeeDAO;

    public AuthController() {
        this.customerDAO = new CustomerDAO();
        this.employeeDAO = new EmployeeDAO();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create sample data if it doesn't exist
        customerDAO.createSampleCustomers();
        employeeDAO.createSampleEmployees();
    }

    public AuthResult authenticate(String userType, String username, String password) {
        User user = null;
        String userID = null;

        try {
            switch (userType) {
                case "Customer":
                    user = customerDAO.getCustomerByUsername(username);
                    break;
                case "Bank Employee":
                case "Administrator":
                    user = employeeDAO.getEmployeeByUsername(username);
                    break;
                default:
                    return new AuthResult(false, "Invalid user type.", null, null);
            }

            if (user == null) {
                return new AuthResult(false, "Invalid username or password.", null, null);
            }

            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return new AuthResult(true, "Login successful", user.getUserID(), userType);
            } else {
                return new AuthResult(false, "Invalid username or password.", null, userType);
            }

        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return new AuthResult(false, "An internal error occurred.", null, null);
        }
    }

    // AuthResult class implementation (remains the same as snippet)
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final String userID;
        private final String userType;

        public AuthResult(boolean success, String message, String userID, String userType) {
            this.success = success;
            this.message = message;
            this.userID = userID;
            this.userType = userType;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserID() { return userID; }
        public String getUserType() { return userType; }
    }

    // Other helper methods (createCustomer, createEmployee)
    public boolean createCustomer(String customerID, String username, String password, String name) {
        try {
            Customer customer = new Customer(customerID, username, PasswordUtil.hashPassword(password), name);
            return customerDAO.createCustomer(customer, password);
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

    public boolean createEmployee(String employeeID, String username, String password, String role) {
        try {
            Employee employee = new Employee(employeeID, username, PasswordUtil.hashPassword(password), role);
            return employeeDAO.createEmployee(employee);
        } catch (Exception e) {
            System.err.println("Error creating employee: " + e.getMessage());
            return false;
        }
    }
}
