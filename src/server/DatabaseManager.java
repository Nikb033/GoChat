package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:chat.db";

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    String sql = "CREATE TABLE IF NOT EXISTS messages (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "sender TEXT NOT NULL," +
                            "content TEXT NOT NULL," +
                            "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                            ");";
                    stmt.execute(sql);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Database initialization failed. Chat history may not be saved.");
        }
    }

    public synchronized void logMessage(String sender, String message) {
        String sql = "INSERT INTO messages(sender, content) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getChatHistory() {
        List<String> history = new ArrayList<>();
        String sql = "SELECT sender, content FROM messages ORDER BY id ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                history.add(rs.getString("sender") + ": " + rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}
