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

    public static void updateInitialGameSession(String name, int dimension, int dps, int init_ps, int init_i, int init_cr, int char_exists) {
        String sql = "UPDATE game_session SET " +
            "name = ?, " + 
            "dimension = ?, " +
            "dps = ?, " +
            "powersurge_initial = ?, " +
            "invincibility_initial = ?, " +
            "cellreveal_initial = ?, " +
            "character_exists = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, dimension);
            pstmt.setInt(3, dps);
            pstmt.setInt(4, init_ps);
            pstmt.setInt(5, init_i);
            pstmt.setInt(6, init_cr);
            pstmt.setInt(7, char_exists);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }       
    }

    public static void updateEndGameSession(String name, int used_ps, int used_i, int used_cr, int time, int stars) {
        String sql = "UPDATE game_session SET " +
            "name = ?, " + 
            "powersurge_used = ?, " +
            "invincibility_used = ?, " +
            "cellreveal_used = ?, " +
            "time = ?, " + 
            "stars = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, used_ps);
            pstmt.setInt(3, used_i);
            pstmt.setInt(4, used_cr);
            pstmt.setInt(5, time);
            pstmt.setInt(6, stars);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void updateGameSession(String name) {
        String sql = "UPDATE players SET name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void createNewPlayer(String name) {
        String sql = "INSERT INTO players (name) VALUES (?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);
            
            pstmt.executeUpdate();
            System.out.println("Player inserted successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
        String sql = "SELECT * FROM players WHERE name = " + name;
        PreparedStatement pstmt = getConnection().prepareStatement(sql);

        return pstmt.executeQuery();
    }

}
