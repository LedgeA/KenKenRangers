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

    public static void updateInitialGameSession(int dimension, int dps, String game_mode, int init_ps, int init_i, int init_cr) {
        String sql = "UPDATE game_session SET " +
            "dimension = ?, " +
            "dps = ?, " +
            "game_mode = ?, " +
            "powersurge_initial = ?, " +
            "invincibility_initial = ?, " +
            "cellreveal_initial = ? ";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, dimension);
            pstmt.setInt(2, dps);
            pstmt.setString(3, game_mode);
            pstmt.setInt(4, init_ps);
            pstmt.setInt(5, init_i);
            pstmt.setInt(6, init_cr);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println(
                "\nInitial Game Session Updated: " + rowsUpdated +
                "\nDimension: " + dimension + 
                "\nDPS: " + dps + 
                "\nInitial Power Surge: " + init_ps +
                "\nInitial Invincibility: " + init_i +
                "\nInitial Cell Reveal: " + init_cr);

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

            System.out.println(
                "\nEnd Game Session Updated: " + rowsUpdated +
                "\nPower Surge Used: " + used_ps + 
                "\nInvincibility Used: " + used_i + 
                "\nCell Reveal: " + used_cr +
                "\nTime: " + time +
                "\nScore: " + score + 
                "\nStars: " + stars);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void updateGameSession(String name) {
        String sql = "UPDATE game_session SET name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println(
                "\nGame Session Updated: " + rowsUpdated + 
                "\nName: " + name);

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
            System.out.println("\nInserted into " + table + " successfully!");

        } catch (SQLException e) {
            System.out.println("\nError inserting into " + table + ": " + e.getMessage());
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

        System.out.println("\nRetrieved Game Session");

        return pstmt.executeQuery();
    }

    public static String retrieveCurrentPlayer() throws SQLException {
        String sql = "SELECT name FROM game_session";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return "";

        String name = rs.getString("name");

        System.out.println(
            "\nRetrieved Current Player" +
            "\nName: " + name);

        return name;
    }

    public static ResultSet retrievePlayerData(String name) throws SQLException {
        String sql = "SELECT * FROM players WHERE name = ?";
        PreparedStatement pstmt = getConnection().prepareStatement(sql);

        pstmt.setString(1, name);
        System.out.println("\nRetrieved " + name + " Data");

        return pstmt.executeQuery();
    }

    public static int retrieveHighScore(String name, String gameMode) throws SQLException {
        if (gameMode == "custom_trial" || gameMode == "tutorial") return -1;

        String sql = "SELECT " + gameMode + " FROM highscores WHERE name = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        pstmt.setString(1, name);

        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return -1;

        System.out.println(
            "\nRetrieved Highscore" +
            "\nName: " + name + 
            "\nGame Mode: " + gameMode +
            "\nScore: " + rs.getInt(gameMode));

        return rs.getInt(gameMode);
    }

    public static void updateHighscore(String name, String gameMode, int highscore) {
        String sql = "UPDATE highscores SET " + gameMode + " = ? WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, highscore);
            pstmt.setString(2, name);

            int rowsUpdated = pstmt.executeUpdate();

            System.out.println(
                "\nRows updated: " + rowsUpdated + 
                "\nHighscore: " + highscore + 
                "\nName: " + name);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void resetGameSession() throws SQLException {
        String sql = """
            UPDATE game_session SET
                dimension = 3,
                dps = 0,
                powersurge_initial = 0,
                invincibility_initial = 0,
                cellreveal_initial = 0,
                powersurge_used = 0,
                invincibility_used = 0,
                cellreveal_used = 0,
                time = 0,
                score = 0,
                stars = 0
        """;
    
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("\nResettted Game Session");
        }
    }
    

}
