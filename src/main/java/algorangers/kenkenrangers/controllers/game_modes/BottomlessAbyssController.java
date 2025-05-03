package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.SQLException;
import java.util.Random;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.database.DatabaseManager.GameSession;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class BottomlessAbyssController extends BaseGameController {
    
    @FXML
    private ImageView background;

    String[] villains = {"orc", "gnome", "goblin", "dragon", "beast", "demon"};
    String[] backgrounds = {"forest-entrance", "forest", "city", "mountain", "dusk", "moon"};

    @FXML
    protected void initialize() throws SQLException {
        gameMode = "bottomless_abyss";
        generateRandomEnvironment();
        updateDifficulty();
        SoundUtils.playMusic();
        
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
        GameSession gameSession = DatabaseManager.retrieveGameSession();

        name = gameSession.name();
        int newPowerSurge = gameSession.init_ps() + powerSurge;
        int newInvincibility = gameSession.init_i() + invincibility;
        int newCellReveal = gameSession.init_cr() + cellReveal;

        int newPowerSurgeUsed = gameSession.rem_ps() + k_controller.getPowerSurge();
        int newInvincibilityUsed = gameSession.rem_i() + k_controller.getInvincibility();
        int newCellRevealUsed = gameSession.rem_cr()+ k_controller.getCellReveal();

        int newTime = gameSession.time() + timeCount;

        int allPowerUps = newPowerSurge + newInvincibility + newCellReveal;
        int allPowerUpsRemaining = newPowerSurgeUsed + newInvincibilityUsed + newCellRevealUsed;

        if (!cleared) {
            int timesCleared = newPowerSurge / 3;
            int powerUpDeductions = 10 * (allPowerUps - allPowerUpsRemaining);
            int difficultyBonusRate = DIMENSION * dot;
            score = difficultyBonusRate * (120 * timesCleared - newTime) * 100 + powerUpDeductions;
            
            int highscore = DatabaseManager.retrieveHighScore(name, "bottomless_abyss");
            if (score > highscore) DatabaseManager.updateHighscore(name, "bottomless_abyss", score);
        }

        DatabaseManager.updateInitialGameSession(
            DIMENSION, 
            dot, 
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
        
        SoundUtils.stopMusic();
        SoundUtils.playGameFinished(gameOver);    
        if (!cleared) {
            GameUtils.navigate("game-over.fxml", p_main);
            return;
        }

        gameOver = true;
        GameUtils.navigate("bottomless-abyss.fxml", p_main);
        
    }

    private void generateRandomEnvironment() {
        Random rand = new Random();

        int randomVillain = rand.nextInt(6);
        int randomBg = rand.nextInt(6);

        String villainPath = "/algorangers/kenkenrangers/sprites/" + villains[randomVillain] + ".png";
        String backgroundPath = "/algorangers/kenkenrangers/backgrounds/" + backgrounds[randomBg] + ".png";

        i_character.setImage(new Image(getClass().getResource(villainPath).toExternalForm()));
        background.setImage(new Image(getClass().getResource(backgroundPath).toExternalForm()));

    }

    private void updateDifficulty() throws SQLException {
        GameSession gameSession = DatabaseManager.retrieveGameSession();
        
        dot = gameSession.dps() + 5;
        if (dot > 15) DIMENSION = gameSession.dimension() + 1;

        if (DIMENSION < 3) DIMENSION = 3;
    }
}
