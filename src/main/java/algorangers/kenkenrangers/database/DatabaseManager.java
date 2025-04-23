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

    public static void updateInitialGameSession(int dimension, int hp, int dps, int init_ps, int init_i, int init_cr, int char_exists) {
        String sql = "UPDATE game_session SET " +
            "name = ? " + 
            "dimension = ?, " +
            "hp = ?, " +
            "dps = ?, " +
            "powersurge_initial = ?, " +
            "invincibility__initial = ?, " +
            "cellreveal_initial = ?, " +
            "character_exists = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, dimension);
            pstmt.setInt(2, hp);
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

    public static void updateEndGameSession(int score, int init_ps, int init_i, int init_cr, int used_ps, int used_i, int used_cr, int stars) {
        String sql = "UPDATE game_session SET " +
            "name = ? " + 
            "score = ?, " +
            "powersurge_initial = ?, " +
            "invincibility_initial = ?, " +
            "cellreveal_initial = ?, " +
            "powersurge_used = ?, " +
            "invincibility_used = ?, " +
            "cellreveal_used = ?, " +
            "stars = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, score);
            pstmt.setInt(2, init_ps);
            pstmt.setInt(3, init_i);
            pstmt.setInt(4, init_cr);
            pstmt.setInt(5, used_ps);
            pstmt.setInt(6, used_i);
            pstmt.setInt(7, used_cr);
            pstmt.setInt(8, stars);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }     
    }

    public static ResultSet retrieveGameSession(String table_name) {
        String sql = "SELECT * FROM " + table_name;

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
