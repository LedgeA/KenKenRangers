package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class BottomlessAbyssController extends BaseGameController {
    
    String[] villains = {"orc", "gnome", "goblin", "dragon", "beast", "demon"};
    String[] backgrounds = {"forest-entrance", "forest", "city", "mountain", "dusk", "moon"};


    protected int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    protected int score = 0;

    @FXML
    protected void initialize() throws SQLException {
        gameMode = "bottomless_abyss";
        
        k_controller = new KenkenController(DIMENSION, 10, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        startTimer();
        startAttackInterval();
        startGameResultChecker();

        // enable powerups
        powerUpsHandler();

        // Setup Pause Menu Visiblity
        setUpPause();
    
        // Add k_view at the below pause menu
        int pos = p_main.getChildren().indexOf(p_pause);
        p_main.getChildren().add(pos, k_view);
    }

    @Override
    protected void startGameResultChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!gameOver) return;

            try {
                gameEnd(gameWon);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }));

        gameResultChecker.setCycleCount(Timeline.INDEFINITE);
        gameResultChecker.play();
    }

    @Override
    protected void gameEnd(boolean cleared) throws SQLException {
        timer.stop();
        attackInterval.stop();
        gameResultChecker.stop();

        ResultSet rs = DatabaseManager.retrieveGameSession();

        if (!rs.next()) return;

        this.name = rs.getString("name");
        int newPowerSurge = rs.getInt("powersurge_initial") + powerSurge;
        int newInvincibility = rs.getInt("invincibility_initial") + invincibility;
        int newCellReveal = rs.getInt("cellreveal_initial") + cellReveal;

        int newPowerSurgeUsed = rs.getInt("powersurge_used") + k_controller.getPowerSurge();
        int newInvincibilityUsed = rs.getInt("invincibility_used") + k_controller.getInvincibility();
        int newCellRevealUsed = rs.getInt("cellreveal_used") + k_controller.getCellReveal();

        int newTime = rs.getInt("time") + timeCount;

        int allPowerUps = newPowerSurge + newInvincibility + newCellReveal;
        int allPowerUpsRemaining = newPowerSurgeUsed + newInvincibilityUsed + newCellRevealUsed;

        rs.close();

        if (!cleared) {
            int timesCleared = newPowerSurge / 3;
            score = (120 * timesCleared - newTime) * 100 + 10 * (allPowerUps - allPowerUpsRemaining);
        }

        DatabaseManager.updateInitialGameSession(
            DIMENSION, 
            dps, 
            gameMode,
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
            
        if (!cleared) {
            int highscore = DatabaseManager.retrieveHighScore(name, "bottomless_abyss");
            if (score > highscore) DatabaseManager.updateHighscore(name, "bottomless_abyss", score);

            GameUtils.navigate("game-over.fxml", p_main);
            return;
        }

        gameOver = true;
        GameUtils.navigate("bottomless-abyss.fxml", p_main);
        
    }
}
