package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
import javafx.fxml.FXML;

public class BottomlessAbyssController extends BaseGameController {
    
    String[] villains = {"orc", "gnome", "goblin", "dragon", "beast", "demon"};
    String[] backgrounds = {"forest-entrance", "forest", "city", "mountain", "dusk", "moon"};

    protected int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    protected int score = 0;

    @FXML
    protected void initialize() throws SQLException {

        k_controller = new KenkenController(DIMENSION, dps, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        startTimer();
        startAttackInterval();

        // enable powerups
        powerUpsHandler();

        // Setup Pause Menu Visiblity
        setUpPause();
    
        // Add k_view at the below tf_dialogue
        p_main.getChildren().add(k_view);
    }

    @Override
    protected void gameEnd(boolean cleared) throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        if (!rs.next()) return;

        int newPowerSurge = rs.getInt("powersurge_initial") + powerSurge;
        int newInvincibility = rs.getInt("invincibility_initial") + invincibility;
        int newCellReveal = rs.getInt("cellreveal_initial") + cellReveal;

        int newPowerSurgeUsed = rs.getInt("powersurge_used") + k_controller.getPowerSurge();
        int newInvincibilityUsed = rs.getInt("invincibility_used") + k_controller.getInvincibility();
        int newCellRevealUsed = rs.getInt("cellreveal_used") + k_controller.getCellReveal();

        int newTime = rs.getInt("time") + timeCount;

        int allPowerUps = newPowerSurge + newInvincibility + newCellReveal;
        int allPowerUpsUsed = newPowerSurgeUsed + newInvincibilityUsed + newCellRevealUsed;

        rs.close();

        if (cleared) {
            int timesCleared = newPowerSurge / 3;

            score = (120 * timesCleared - newTime) * 10 * (allPowerUps - allPowerUpsUsed);
        }

        DatabaseManager.updateInitialGameSession(
            DIMENSION, 
            dps, 
            newPowerSurge, 
            newInvincibility, 
            newCellReveal);

        DatabaseManager.updateEndGameSession(
            newPowerSurgeUsed, 
            newInvincibilityUsed, 
            newCellRevealUsed, 
            newTime, 
            score, 
            3);

    }
    

}
