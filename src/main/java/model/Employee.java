package model;

public class Employee extends User {
    private String role; // e.g., "Teller", "Manager", "SystemAdministrator"

    public Employee(String userID, String username, String passwordHash, String role) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public boolean hasPermission(String requiredRole) {
        // SA-001: Role-based access control check
        return this.role.equalsIgnoreCase(requiredRole);
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}