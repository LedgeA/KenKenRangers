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

public class CustomTrialController extends BaseGameController {

    @FXML
    protected void initialize() throws SQLException {
        gameMode = "custom_trial";
        setInitialSettings();

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
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

    private void setInitialSettings() throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        if (!rs.next()) return;

        DIMENSION = rs.getInt("dimension");
        dot = rs.getInt("dps");
        powerSurge = rs.getInt("powersurge_initial");
        invincibility = rs.getInt("invincibility_initial");
        cellReveal = rs.getInt("cellreveal_initial");
        
        rs.close();
    }

    @Override
    protected void startGameResultChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!gameOver) return;

            stopAllTimelines();
            nullifyAllTimelines();

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
        
        int powerUpDeductions = 10 * (9 - countRemainingPowerups());
        int difficultyBonusRate = DIMENSION * dot;
        score = difficultyBonusRate * (120 - timeCount) * 100 + powerUpDeductions;

        if (!cleared) score = 0;

        DatabaseManager.updateInitialGameSession(
            DIMENSION, 
            dot, 
            gameMode,
            powerSurge, 
            invincibility, 
            cellReveal);

        DatabaseManager.updateEndGameSession(
            k_controller.getPowerSurge(), 
            k_controller.getInvincibility(), 
            k_controller.getCellReveal(), 
            timeCount, 
            score, 
            computeStars());
        
        GameUtils.navigate("game-over.fxml", p_main);
    }
}
