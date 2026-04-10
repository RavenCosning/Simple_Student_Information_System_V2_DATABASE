package information_management_practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:student.db";
    private static Connection instance = null;

    // Private constructor — no one should instantiate this class
    private DBConnection() {}

    // Singleton: reuse the same connection throughout the app
    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = DriverManager.getConnection(URL);
                applyPragmas(instance);
                System.out.println("Database connected.");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return instance;
    }

    // Apply SQLite settings on every new connection
    private static void applyPragmas(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA journal_mode = WAL");   // better write performance
            stmt.execute("PRAGMA synchronous = NORMAL"); // balanced safety vs speed
        }
    }

    // Call this when closing the app
    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                instance = null;
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }

    // Null-safe helpers for closing resources without try-catch everywhere
    public static void close(Statement stmt) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Failed to close statement: " + e.getMessage());
        }
    }
}