package test;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DBConnectionTest {

    private Connection connection;

    @BeforeAll
    void setUpClass() {
        System.out.println("=== Starting DBConnection Tests ===");
    }

    @AfterAll
    void tearDownClass() {
        DBConnection.closeConnection();
        System.out.println("=== DBConnection Tests Completed ===");
    }

    @BeforeEach
    void setUp() throws Exception {
        connection = DBConnection.getConnection();
    }

    @Test
    void testGetConnection() throws Exception {
        System.out.println("Testing database connection...");
        assertNotNull(connection, "Connection should not be null");
        assertFalse(connection.isClosed(), "Connection should be open");
        assertTrue(connection.isValid(2), "Connection should be valid");
        System.out.println("✓ Connection test passed");
    }

    @Test
    void testSingletonPattern() throws Exception {
        System.out.println("Testing singleton pattern...");
        Connection conn1 = DBConnection.getConnection();
        Connection conn2 = DBConnection.getConnection();
        assertSame(conn1, conn2, "Should return the same connection instance");
        System.out.println("✓ Singleton pattern test passed");
    }

    @Test
    void testDatabaseInitialization() throws Exception {
        System.out.println("Testing database initialization...");
        
        // Check if tables exist
        String[] expectedTables = {"customers", "employees", "accounts", "transactions"};
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'")) {
            
            int tableCount = 0;
            while (rs.next()) {
                String tableName = rs.getString("name");
                System.out.println("Found table: " + tableName);
                tableCount++;
            }
            assertTrue(tableCount >= expectedTables.length, "Should have all required tables");
        }
        System.out.println("✓ Database initialization test passed");
    }

    @Test
    void testTableStructure() throws Exception {
        System.out.println("Testing table structure...");
        
        // Test that we can query each table
        String[] tables = {"customers", "employees", "accounts", "transactions"};
        
        for (String table : tables) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
                assertTrue(rs.next(), "Should be able to query table: " + table);
                System.out.println("Table " + table + " is accessible");
            }
        }
        System.out.println("✓ Table structure test passed");
    }

    @Test
    void testForeignKeys() throws Exception {
        System.out.println("Testing foreign keys...");
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys")) {
            assertTrue(rs.next(), "Should have foreign keys configuration");
            assertEquals(1, rs.getInt(1), "Foreign keys should be enabled");
        }
        System.out.println("✓ Foreign keys test passed");
    }

    @Test
    void testConnectionAfterClose() throws Exception {
        System.out.println("Testing connection recovery...");
        
        // Close connection
        if (!connection.isClosed()) {
            connection.close();
        }
        
        // Should be able to get a new connection
        Connection newConnection = DBConnection.getConnection();
        assertNotNull(newConnection, "Should handle closed connection and return new one");
        assertFalse(newConnection.isClosed(), "New connection should be open");
        System.out.println("✓ Connection recovery test passed");
    }
}