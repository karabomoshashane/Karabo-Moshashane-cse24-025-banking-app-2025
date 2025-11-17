package model;

import java.util.Date;

public abstract class User {
    protected String userID;
    protected String username;
    protected String passwordHash;
    protected boolean isLocked = false;
    protected int failedLoginAttempts = 0;
    
    // Abstract method for role-based access control
    public abstract boolean hasPermission(String requiredRole);
    public abstract void setPasswordHash(String passwordHash);

    // Getters
    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isLocked() { return isLocked; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }

    // Setters for Controller/DAO access
    public void setUserID(String userID) { this.userID = userID; }
    public void setUsername(String username) { this.username = username; }
    public void setLocked(boolean locked) { this.isLocked = locked; }
    public void setFailedLoginAttempts(int attempts) { this.failedLoginAttempts = attempts; }
}