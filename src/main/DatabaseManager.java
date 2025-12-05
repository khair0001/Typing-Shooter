import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/typing_shooter";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1 FROM scores LIMIT 1");
                System.out.println("Scores table is accessible");
            } catch (SQLException e) {
                System.err.println("Error accessing scores table: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public boolean insertScore(String username, int score) {
        String query = "INSERT INTO scores (username, score, timestamp) VALUES (?, ?, NOW())";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting score: " + e.getMessage());
            return false;
        }
    }

    public List<ScoreEntry> getTop10Scores() {
        List<ScoreEntry> scores = new ArrayList<>();
        String query = "SELECT username, score, timestamp FROM scores ORDER BY score DESC LIMIT 10";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                scores.add(new ScoreEntry(username, score, timestamp));
            }

        } catch (SQLException e) {
            System.err.println("Error getting top scores: " + e.getMessage());
        }

        return scores;
    }

    public boolean isTop10Score(int score) {
        String query = "SELECT COUNT(*) as total FROM scores WHERE score > ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, score);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int higherScores = rs.getInt("total");
                return higherScores < 10;
            }

        } catch (SQLException e) {
            System.err.println("Error checking top 10: " + e.getMessage());
        }

        return false;
    }

    public List<String> getSoalByKesulitan(String kesulitan) {
        List<String> soalList = new ArrayList<>();
        String query = "SELECT data FROM soal WHERE kesulitan = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, kesulitan);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String data = rs.getString("data");
                String[] words = data.split(",\\s*");
                for (String word : words) {
                    if (!word.trim().isEmpty()) {
                        soalList.add(word.trim());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting soal: " + e.getMessage());
        }

        return soalList;
    }

    public static class ScoreEntry {
        public String username;
        public int score;
        public Timestamp timestamp;

        public ScoreEntry(String username, int score, Timestamp timestamp) {
            this.username = username;
            this.score = score;
            this.timestamp = timestamp;
        }
    }
}