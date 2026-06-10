package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Persistent SQLite database file
    private static final String DB_URL = "jdbc:sqlite:data.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                // System.out.println("[Database] SQLite connected successfully.");
            } catch (SQLException e) {
                System.err.println("[Database Error] Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        Connection conn = getConnection();
        if (conn == null) return;

        try (Statement stmt = conn.createStatement()) {
            // Enforce relational reference rules
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Build Users Table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + " user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " username TEXT NOT NULL UNIQUE,"
                    + " password_hash TEXT NOT NULL,"
                    + " email_or_phone TEXT NOT NULL,"
                    + " two_fa_secret TEXT"
                    + ");";
            stmt.execute(createUsersTable);

            // Build Budgeting Info Table
            String createBudgetTable = "CREATE TABLE IF NOT EXISTS budgeting_info ("
                    + " transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " user_id INTEGER NOT NULL,"
                    + " transaction_date TEXT NOT NULL,"
                    + " description TEXT NOT NULL,"
                    + " amount REAL NOT NULL,"
                    + " type TEXT CHECK(type IN ('Income', 'Expense')) NOT NULL,"
                    + " FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                    + ");";
            stmt.execute(createBudgetTable);
            
            // Seed default program mock values if users table is empty
            if (isTableEmpty("users")) {
                seedMockData(conn);
            }

        } catch (SQLException e) {
            System.err.println("[Database Error] Setup execution failed: " + e.getMessage());
        }
    }

    private static boolean isTableEmpty(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        Connection conn = getConnection();
        if (conn == null) return true;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            return true;
        }
        return true;
    }

    private static void seedMockData(Connection conn) {
        String insertUser = "INSERT OR IGNORE INTO users (user_id, username, password_hash, email_or_phone) VALUES (1, 'michael', 'hashed_pass_placeholder', 'user@domain.com');";
        String insertTransaction = "INSERT INTO budgeting_info (user_id, transaction_date, description, amount, type) VALUES (?, ?, ?, ?, ?);";

        try (Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(insertTransaction)) {
            
            // Core user creation
            stmt.execute(insertUser);

            // Seed initial transactional entries targeting user_id 1
            Object[][] data = {
                {"06/05", "Part-time Job", 1200.00, "Income"},
                {"06/06", "Grocery Store", 124.50, "Expense"},
                {"06/07", "Transit Pass", 55.00, "Expense"},
                {"06/08", "Freelance Gig", 350.00, "Income"}
            };

            for (Object[] row : data) {
                pstmt.setInt(1, 1); // Bind to foreign key user_id = 1
                pstmt.setString(2, (String) row[0]);
                pstmt.setString(3, (String) row[1]);
                pstmt.setDouble(4, (Double) row[2]);
                pstmt.setString(5, (String) row[3]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("[Database] Mock dashboard states populated successfully.");

        } catch (SQLException e) {
            System.err.println("[Database Error] Seeding state context dropped: " + e.getMessage());
        }
    }

    public static double getTotalIncome(int userId) {
        String query = "SELECT SUM(amount) FROM budgeting_info WHERE user_id = ? AND type = 'Income'";
        return getSum(userId, query);
    }

    public static double getTotalExpenses(int userId) {
        String query = "SELECT SUM(amount) FROM budgeting_info WHERE user_id = ? AND type = 'Expense'";
        return getSum(userId, query);
    }

    private static double getSum(int userId, String query) {
        Connection conn = getConnection();
        if (conn == null) return 0.0;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to calculate sum: " + e.getMessage());
        }
        return 0.0;
    }

    public static List<String[]> getRecentTransactions(int userId) {
        List<String[]> transactions = new ArrayList<>();
        String query = "SELECT transaction_date, description, amount, type FROM budgeting_info WHERE user_id = ? ORDER BY transaction_id DESC LIMIT 10";
        Connection conn = getConnection();
        if (conn == null) return transactions;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new String[]{
                        rs.getString("transaction_date"),
                        rs.getString("description"),
                        String.format("%.2f", rs.getDouble("amount")),
                        rs.getString("type")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to fetch transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static void addTransaction(int userId, String date, String description, double amount, String type) {
        String query = "INSERT INTO budgeting_info (user_id, transaction_date, description, amount, type) VALUES (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, date);
            pstmt.setString(3, description);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, type);
            pstmt.executeUpdate();
            System.out.println("[Database] Transaction added successfully.");
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to add transaction: " + e.getMessage());
        }
    }

    public static void deleteTransaction(int userId, int transactionIndex) {
        // This is a bit tricky because 'transactionIndex' from UI might not match 'transaction_id'
        // For simplicity in this TUI, we'll fetch the IDs and delete the Nth one
        String findIdQuery = "SELECT transaction_id FROM budgeting_info WHERE user_id = ? ORDER BY transaction_id DESC LIMIT 1 OFFSET ?";
        Connection conn = getConnection();
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement(findIdQuery)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, transactionIndex);
            int transactionId = -1;
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transactionId = rs.getInt(1);
                }
            }

            if (transactionId != -1) {
                String deleteQuery = "DELETE FROM budgeting_info WHERE transaction_id = ?";
                try (PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery)) {
                    deletePstmt.setInt(1, transactionId);
                    deletePstmt.executeUpdate();
                    System.out.println("[Database] Transaction deleted successfully.");
                }
            } else {
                System.out.println("[Database] Transaction not found.");
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to delete transaction: " + e.getMessage());
        }
    }
}
