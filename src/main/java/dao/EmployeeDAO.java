package dao;

import model.Employee;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Create a new employee
    public boolean createEmployee(Employee employee) {
        String sql = "INSERT INTO employees (userID, username, passwordHash, role, accountLocked, failedLoginAttempts) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employee.getUserID());
            pstmt.setString(2, employee.getUsername());
            pstmt.setString(3, employee.getPasswordHash());
            pstmt.setString(4, employee.getRole());
            pstmt.setBoolean(5, employee.isLocked());
            pstmt.setInt(6, employee.getFailedLoginAttempts());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error creating employee: " + e.getMessage());
            return false;
        }
    }

    // Delete an employee by ID
    public boolean deleteEmployee(String employeeID) {
        String sql = "DELETE FROM employees WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    // Get employee by ID
    public Employee getEmployeeByID(String employeeID) {
        String sql = "SELECT * FROM employees WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee(
                            rs.getString("userID"),
                            rs.getString("username"),
                            rs.getString("passwordHash"),
                            rs.getString("role")
                    );
                    employee.setLocked(rs.getBoolean("accountLocked"));
                    employee.setFailedLoginAttempts(rs.getInt("failedLoginAttempts"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error reading employee by ID: " + e.getMessage());
        }
        return null;
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getString("userID"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getString("role")
                );
                employee.setLocked(rs.getBoolean("accountLocked"));
                employee.setFailedLoginAttempts(rs.getInt("failedLoginAttempts"));
                employees.add(employee);
            }

        } catch (SQLException e) {
            System.err.println("DAO Error retrieving all employees: " + e.getMessage());
        }

        return employees;
    }

    // Lock or unlock employee account
    public boolean updateEmployeeSecurityStatus(Employee employee) {
        String sql = "UPDATE employees SET accountLocked = ?, failedLoginAttempts = ? WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, employee.isLocked());
            pstmt.setInt(2, employee.getFailedLoginAttempts());
            pstmt.setString(3, employee.getUserID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error updating employee security status: " + e.getMessage());
            return false;
        }
    }

    // Update employee password
    public boolean updateEmployeePassword(String employeeID, String newPassword) {
        String sql = "UPDATE employees SET passwordHash = ? WHERE userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, employeeID);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DAO Error updating employee password: " + e.getMessage());
            return false;
        }
    }

    // Get employee by username
    public Employee getEmployeeByUsername(String username) {
        String sql = "SELECT * FROM employees WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee(
                            rs.getString("userID"),
                            rs.getString("username"),
                            rs.getString("passwordHash"),
                            rs.getString("role")
                    );
                    employee.setLocked(rs.getBoolean("accountLocked"));
                    employee.setFailedLoginAttempts(rs.getInt("failedLoginAttempts"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error reading employee by username: " + e.getMessage());
        }
        return null;
    }

    // Create sample employees (admin/teller) if not present
    public void createSampleEmployees() {
        if (getEmployeeByID("EMP001") == null) {
            createEmployee(new Employee("EMP001", "admin", PasswordUtil.hashPassword("admin123"), "SystemAdministrator"));
        }
        if (getEmployeeByID("EMP002") == null) {
            createEmployee(new Employee("EMP002", "teller", PasswordUtil.hashPassword("teller123"), "Teller"));
        }
    }
}
