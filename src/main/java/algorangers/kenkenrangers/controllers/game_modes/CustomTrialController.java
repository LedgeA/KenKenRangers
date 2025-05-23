package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.database.DatabaseManager.GameSession;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CustomTrialController extends BaseGameController {

    @FXML
    protected void initialize() throws SQLException {
        gameMode = "custom_trial";
        setInitialSettings();
        SoundUtils.musicOn();
        
        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        startTimer();
        startAttackInterval();
        startGameResultChecker();

        // enable powerups
        setupAllPowerUps();
        updatePowerUps();

        // Setup Pause Menu Visiblity
        setUpPause();
    
        // Add k_view at the below pause menu
        int pos = p_main.getChildren().indexOf(p_pause);
        p_main.getChildren().add(pos, k_view);
    }

    private void updatePowerUps() {
        updatePowerUp(s_powerSurge, k_controller.getPowerSurge());
        updatePowerUp(s_invincibility, k_controller.getInvincibility());
        updatePowerUp(s_cellReveal, k_controller.getCellReveal());

    }
    
    private void updatePowerUp(StackPane stackPane, int remainingCount) {
        Text text = (Text) ((Pane) stackPane.getChildren().get(1)).getChildren().get(0);
        text.setText(String.valueOf(remainingCount));
        if (remainingCount == 0) stackPane.setOnMouseClicked(null);
    }

    private void setInitialSettings() throws SQLException {
        GameSession gameSession = DatabaseManager.retrieveGameSession();

        DIMENSION = gameSession.dimension();
        dot = gameSession.dot();
        powerSurge = gameSession.init_ps();
        invincibility = gameSession.init_i();
        cellReveal = gameSession.init_cr();

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
        
        SoundUtils.musicOn();
        SoundUtils.finished(gameOver);
        GameUtils.navigate("game-over.fxml", p_main);
    }
}
