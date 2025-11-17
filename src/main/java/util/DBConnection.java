package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String JDBC_URL = "jdbc:sqlite:banking_system.db";
    private static Connection connection = null;

    static {
        // Ensure the SQLite driver is loaded
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("✗ SQLite driver not found: " + e.getMessage());
            throw new RuntimeException("Missing SQLite JDBC Driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(JDBC_URL);
            // Enable foreign keys for SQLite
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = ON");
            
            // 1. Create Customers table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS customers (" +
                "    userID TEXT PRIMARY KEY," +
                "    username TEXT UNIQUE NOT NULL," +
                "    passwordHash TEXT NOT NULL," +
                "    customerName TEXT," +
                "    accountLocked BOOLEAN DEFAULT 0," + // SA-005 
                "    failedLoginAttempts INTEGER DEFAULT 0" + // SA-005 
                ")"
            );
            
            // 2. Create Employees table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "    userID TEXT PRIMARY KEY," +
                "    username TEXT UNIQUE NOT NULL," +
                "    passwordHash TEXT NOT NULL," +
                "    role TEXT NOT NULL," + // SA-001
                "    accountLocked BOOLEAN DEFAULT 0," + // SA-005 
                "    failedLoginAttempts INTEGER DEFAULT 0" + // SA-005 
                ")"
            );

            // 3. Create Accounts table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "    accountNumber TEXT PRIMARY KEY," +
                "    balance REAL NOT NULL," +
                "    dateOpened DATE NOT NULL," +
                "    customerID TEXT NOT NULL," +
                "    accountType TEXT NOT NULL," + // ChequeAccount, SavingsAccount, InvestmentAccount
                "    status TEXT DEFAULT 'Active'," + // Active, Closed, Pending Initial Deposit (TP-009) 
                "    isActivated BOOLEAN DEFAULT 0," + // For InvestmentAccount (TP-009) 
                "    FOREIGN KEY (customerID) REFERENCES customers(userID) ON DELETE CASCADE" +
                ")"
            );
            
            // 4. Create Transactions table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "    transactionID TEXT PRIMARY KEY," +
                "    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "    transactionType TEXT NOT NULL," +
                "    amount REAL NOT NULL," +
                "    sourceAccount TEXT NOT NULL," +
                "    destinationAccount TEXT NOT NULL," +
                "    FOREIGN KEY (sourceAccount) REFERENCES accounts(accountNumber) ON DELETE CASCADE" + // Simplified FK
                ")"
            );

            System.out.println("✓ SQLite database initialized successfully");

        } catch (SQLException e) {
            System.err.println("✗ Database initialization failed: " + e.getMessage());
            throw new RuntimeException("Database initialization error", e);
        }
    }

    public static Connection testConnection() {
        try {
            return getConnection();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return null;
        }
    }

    // util.DBConnection.java
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ SQLite database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}