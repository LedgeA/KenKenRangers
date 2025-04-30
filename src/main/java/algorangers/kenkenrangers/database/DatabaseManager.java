package algorangers.kenkenrangers.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:kenken.db"); 
        } 

        return connection;
    }

    public static void updateInitialGameSession(int dimension, int dps, int init_ps, int init_i, int init_cr) {
        String sql = "UPDATE game_session SET " +
            "dimension = ?, " +
            "dps = ?, " +
            "powersurge_initial = ?, " +
            "invincibility_initial = ?, " +
            "cellreveal_initial = ?, ";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, dimension);
            pstmt.setInt(2, dps);
            pstmt.setInt(3, init_ps);
            pstmt.setInt(4, init_i);
            pstmt.setInt(5, init_cr);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }       
    }

    public static void updateEndGameSession(int used_ps, int used_i, int used_cr, int time, int score, int stars) {
        String sql = "UPDATE game_session SET " +
            "powersurge_used = ?, " +
            "invincibility_used = ?, " +
            "cellreveal_used = ?, " +
            "time = ?, " + 
            "score = ?, " +
            "stars = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, used_ps);
            pstmt.setInt(2, used_i);
            pstmt.setInt(3, used_cr);
            pstmt.setInt(4, time);
            pstmt.setInt(5, score);
            pstmt.setInt(6, stars);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void updateGameSession(String name) {
        String sql = "UPDATE game_session SET name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void createNewPlayer(String name) {
        insertIntoTable("players", name);
        insertIntoTable("highscores", name);
    }
    
    private static void insertIntoTable(String table, String name) {
        String sql = "INSERT INTO " + table + " (name) VALUES (?)";
    
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Inserted into " + table + " successfully!");
        } catch (SQLException e) {
            System.out.println("Error inserting into " + table + ": " + e.getMessage());
        }
    }

    public static ResultSet getListOfPlayers() throws SQLException {
        String sql = "SELECT name FROM players;";
        PreparedStatement pstmt = getConnection().prepareStatement(sql);

        return pstmt.executeQuery();
    }

    public static ResultSet retrieveGameSession() throws SQLException {
        String sql = "SELECT * FROM game_session";
        PreparedStatement pstmt = getConnection().prepareStatement(sql);

        return pstmt.executeQuery();
    }

    public static ResultSet retrievePlayerData(String name) throws SQLException {
        String sql = "SELECT * FROM players WHERE name = ?";
        PreparedStatement pstmt = getConnection().prepareStatement(sql);

        pstmt.setString(1, name);
        
        return pstmt.executeQuery();
    }

    public static int retrieveHighScore(String name, String gameMode) throws SQLException {
        String sql = "SELECT * FROM highscores WHERE name = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        pstmt.setString(1, name);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(gameMode);
        }

        return -1;
    }

}
