package algorangers.kenkenrangers.database;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static Connection connection;
    
    public record GameSession(
        String name, String gameMode,
        int dimension, int dot, 
        int init_ps, int init_i, int init_cr, 
        int rem_ps, int rem_i, int rem_cr,
        int time, int score, int stars) {}

        public static Connection getConnection() throws SQLException {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found.", e);
            }
            
            if (connection == null || connection.isClosed()) {
                String appDataPath = System.getenv("LOCALAPPDATA"); 
                Path dbPath = Paths.get(appDataPath, "KenKen", "kenken.db");
                File dbFile = dbPath.toFile();
                dbFile.getParentFile().mkdirs(); 

                System.out.println("Using database at: " + dbPath); 
                

                connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

                Statement stmt = connection.createStatement();
                stmt.execute("PRAGMA foreign_keys = ON");
            
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS players (
                        name TEXT PRIMARY KEY,
                        latest_finished_chap INTEGER DEFAULT 0
                    )
                """);
            
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS highscores (
                        name TEXT,
                        chap_1 INTEGER DEFAULT 0,
                        chap_2 INTEGER DEFAULT 0,
                        chap_3 INTEGER DEFAULT 0,
                        chap_4 INTEGER DEFAULT 0,
                        chap_5 INTEGER DEFAULT 0,
                        chap_6 INTEGER DEFAULT 0,
                        bottomless_abyss INTEGER DEFAULT 0,
                        FOREIGN KEY (name) REFERENCES players(name) ON DELETE CASCADE
                    )
                """);
            
                ResultSet rs = stmt.executeQuery("""
                    SELECT name FROM sqlite_master WHERE type='table' AND name='game_session'
                """);
            
                if (!rs.next()) {
                    stmt.execute("""
                        CREATE TABLE game_session (
                            name TEXT,
                            game_mode TEXT,
                            dimension INTEGER,
                            dot INTEGER,
                            init_powersurge INTEGER,
                            init_invincibility INTEGER,
                            init_cellreveal INTEGER,
                            rem_powersurge INTEGER,
                            rem_invincibility INTEGER,
                            rem_cellreveal INTEGER,
                            time INTEGER,
                            score INTEGER,
                            stars INTEGER
                        )
                    """);
            
                    stmt.executeUpdate("""
                        INSERT INTO game_session (
                            name, game_mode, dimension, dot,
                            init_powersurge, init_invincibility, init_cellreveal,
                            rem_powersurge, rem_invincibility, rem_cellreveal,
                            time, score, stars
                        ) VALUES (
                            '', 'tutorial', 3, 0,
                            0, 0, 0,
                            0, 0, 0,
                            0, 0, 0
                        )
                    """);
                }
                
                rs.close();
            }            
            
            return connection;
        }

    public static void updateInitialGameSession(int dimension, int dps, String game_mode, int init_ps, int init_i, int init_cr) {
        String sql = "UPDATE game_session SET " +
            "dimension = ?, " +
            "dot = ?, " +
            "game_mode = ?, " +
            "init_powersurge = ?, " +
            "init_invincibility = ?, " +
            "init_cellreveal = ? ";

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
            "rem_powersurge = ?, " +
            "rem_invincibility = ?, " +
            "rem_cellreveal = ?, " +
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

    public static void updateCurrentPlayer(String name) {
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

    public static void updateLatestFinishedChapter(String name, int chapter) {
        String sql = "UPDATE players SET latest_finished_chap = ? WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, chapter);
            pstmt.setString(2, name);

            int rowsUpdated = pstmt.executeUpdate();

            System.out.println(
                "\nRows updated: " + rowsUpdated + 
                "\nChapter: " + chapter + 
                "\nName: " + name);

        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    public static List<String> getListOfPlayers() throws SQLException {
        String sql = "SELECT name FROM players;";
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        List<String> names = new ArrayList<>();

        while (rs.next()) {
            names.add(rs.getString("name"));
        }

        return names;
    }

    public static GameSession retrieveGameSession() throws SQLException {
        String sql = "SELECT * FROM game_session";
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return null;

        GameSession gameSession = new GameSession(
            rs.getString("name"), 
            rs.getString("game_mode"),
            rs.getInt("dimension"), 
            rs.getInt("dot"), 
            rs.getInt("init_powersurge"), 
            rs.getInt("init_invincibility"), 
            rs.getInt("init_cellreveal"), 
            rs.getInt("rem_powersurge"), 
            rs.getInt("rem_invincibility"), 
            rs.getInt("rem_cellreveal"), 
            rs.getInt("time"), 
            rs.getInt("score"), 
            rs.getInt("stars"));

        rs.close();

        System.out.println("\nRetrieved Game Session");

        return gameSession;
    }

    public static String retrieveCurrentPlayer() throws SQLException {
        String sql = "SELECT name FROM game_session";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return "";

        String name = rs.getString("name");

        rs.close();

        System.out.println(
            "\nRetrieved Current Player" +
            "\nName: " + name);

        return name;
    }

    public static int retrieveHighScore(String name, String gameMode) throws SQLException {
        if (gameMode == "custom_trial" || gameMode == "tutorial") return 0;

        String sql = "SELECT " + gameMode + " FROM highscores WHERE name = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        pstmt.setString(1, name);

        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return -1;

        int highscore = rs.getInt(gameMode);

        rs.close();

        System.out.println(
            "\nRetrieved Highscore" +
            "\nName: " + name + 
            "\nGame Mode: " + gameMode +
            "\nScore: " + highscore);

        return highscore;
    }

    public static int retrieveLatestFinishedChapter(String name) throws SQLException {
        String sql = "SELECT latest_finished_chap FROM players WHERE name = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return 0;

        int lastFinishedChapter = rs.getInt("latest_finished_chap");
        
        rs.close();
        System.out.println(
            "\nRetrieved Current Player" +
            "\nChapter: " + lastFinishedChapter);

        return lastFinishedChapter;
    }

    public static void removePlayer(String name) {
        String sql = "DELETE FROM players WHERE name = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, name);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println(
                "\nPlayer Deleted: " + rowsUpdated + 
                "\nName: " + name);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static void resetGameSession() throws SQLException {
        String sql = """
            UPDATE game_session SET
                dimension = 3,
                dot = 0,
                init_powersurge = 0,
                init_invincibility = 0,
                init_cellreveal = 0,
                rem_powersurge = 0,
                rem_invincibility = 0,
                rem_cellreveal = 0,
                time = 0,
                score = 0,
                stars = 0
        """;
    
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("\nResettted Game Session");
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

}
