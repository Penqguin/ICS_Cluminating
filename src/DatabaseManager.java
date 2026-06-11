package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

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
            
            // Add new columns if they don't exist
            addColumnIfNotExists(stmt, "users", "two_fa_enabled", "BOOLEAN DEFAULT 0");
            addColumnIfNotExists(stmt, "users", "cost_streams", "TEXT");
            addColumnIfNotExists(stmt, "users", "revenue_streams", "TEXT");
            addColumnIfNotExists(stmt, "users", "savings_rate", "REAL DEFAULT 0.0");

            // Seed default program mock values if users table is empty
            if (isTableEmpty("users")) {
                seedMockData(conn);
            }

        } catch (SQLException e) {
            System.err.println("[Database Error] Setup execution failed: " + e.getMessage());
        }
    }

    private static void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String columnType) {
        try {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType + ";");
            // System.out.println("[Database] Column " + columnName + " added to " + tableName);
        } catch (SQLException e) {
            // Column likely already exists
            if (!e.getMessage().contains("duplicate column name")) {
                // Ignore if it's just that the column already exists
            }
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
        String insertUser = "INSERT OR IGNORE INTO users (user_id, username, password_hash, email_or_phone) VALUES (?, ?, ?, ?)";
        String insertTransaction = "INSERT INTO budgeting_info (user_id, transaction_date, description, amount, type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement userStmt = conn.prepareStatement(insertUser);
             PreparedStatement transStmt = conn.prepareStatement(insertTransaction)) {

            // Core user creation
            userStmt.setInt(1, 1);
            userStmt.setString(2, "michael");
            userStmt.setString(3, hashPassword("password123")); // default password
            userStmt.setString(4, "user@domain.com");
            userStmt.executeUpdate();

            // Seed initial transactional entries targeting user_id = 1
            Object[][] data = {
                {"05/06/2026", "Part-time Job", 1200.00, "Income"},
                {"06/06/2026", "Grocery Store", 124.50, "Expense"},
                {"07/06/2026", "Transit Pass", 55.00, "Expense"},
                {"08/06/2026", "Freelance Gig", 350.00, "Income"}
            };

            for (Object[] row : data) {
                transStmt.setInt(1, 1); // Bind to foreign key user_id = 1
                transStmt.setString(2, (String) row[0]);
                transStmt.setString(3, (String) row[1]);
                transStmt.setDouble(4, (Double) row[2]);
                transStmt.setString(5, (String) row[3]);
                transStmt.addBatch();
            }
            transStmt.executeBatch();
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

    public static List<String[]> getAllTransactionsSorted(int userId) {
        List<String[]> transactions = new ArrayList<>();
        // Assuming date format is dd/mm/yyyy
        String query = "SELECT transaction_date, description, amount, type FROM budgeting_info WHERE user_id = ? " +
                       "ORDER BY substr(transaction_date, 7, 4) ASC, substr(transaction_date, 4, 2) ASC, substr(transaction_date, 1, 2) ASC";
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

    public static List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        String query = "SELECT user_id, username FROM users";
        Connection conn = getConnection();
        if (conn == null) return users;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new String[]{
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("username")
                });
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to fetch users: " + e.getMessage());
        }
        return users;
    }

    public static int getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        Connection conn = getConnection();
        if (conn == null) return -1;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to fetch user ID: " + e.getMessage());
        }
        return -1;
    }

    public static boolean usernameExists(String username) {
        return getUserIdByUsername(username) != -1;
    }

    public static void createUser(String username, String password, String emailOrPhone) {
        String query = "INSERT INTO users (username, password_hash, email_or_phone) VALUES (?, ?, ?)";
        Connection conn = getConnection();
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, emailOrPhone);
            pstmt.executeUpdate();
            System.out.println("[Database] User created successfully.");
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to create user: " + e.getMessage());
        }
    }

    public static boolean verifyPassword(String username, String password) {
        String query = "SELECT password_hash FROM users WHERE username = ?";
        Connection conn = getConnection();
        if (conn == null) return false;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    return storedHash.equals(hashPassword(password));
                }
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to verify password: " + e.getMessage());
        }
        return false;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("[Error] Hashing algorithm not found.");
            return password; // Fallback to plain text (not recommended)
        }
    }

    public static void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        Connection conn = getConnection();
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("[Database] User deleted successfully.");
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to delete user: " + e.getMessage());
        }
    }

    public static void deleteTransaction(int userId, int transactionIndex) {
        // Fetch the IDs and delete the Nth one for the given user
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

    public static List<String[]> getTopSavers() {
        List<String[]> savers = new ArrayList<>();
        String query = "SELECT username, savings_rate FROM users ORDER BY savings_rate DESC LIMIT 10";
        Connection conn = getConnection();
        if (conn == null) return savers;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                savers.add(new String[]{
                    rs.getString("username"),
                    String.format("%.2f", rs.getDouble("savings_rate") * 100)
                });
            }
        } catch (SQLException e) {
            System.err.println("[Database Error] Failed to fetch top savers: " + e.getMessage());
        }
        return savers;
    }
}
