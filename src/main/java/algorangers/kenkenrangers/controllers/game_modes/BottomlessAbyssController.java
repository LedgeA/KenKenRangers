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
        SoundUtils.musicOn();

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        // start timelines
        startTimer();
        startAttackInterval();
        startGameResultChecker();

        // enable powerups
        setupAllPowerUps();

        // setup pause menu
        setUpPause();
    
        // add k_view at the below pause menu
        int pos = p_main.getChildren().indexOf(p_pause);
        p_main.getChildren().add(pos, k_view);
    }

    // difficulty is updated per game
    // dot first updates from 10 -> 15
    // if dot > 15, dimensinsion++ and reset dot = 10
    private void updateDifficulty() throws SQLException {
        GameSession gameSession = DatabaseManager.retrieveGameSession();
        
        dot = gameSession.dot() + 5;
        DIMENSION = gameSession.dimension();
        if (dot > 15) {
            DIMENSION += 1;
            dot = 10;
        }

        // since game_session has dimension = 0 at start, skip it to 3 immediately
        if (DIMENSION < 3) DIMENSION = 3; 
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
    protected void gameEnd(boolean gameWon) throws SQLException {
        GameSession gameSession = DatabaseManager.retrieveGameSession();

        // add all current values to the values inside the game session
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

        if (!gameWon) {
            int timesCleared = newPowerSurge / 3;
            int powerUpDeductions = 10 * (allPowerUps - allPowerUpsRemaining);
            int difficultyBonusRate = DIMENSION * dot;
            score = difficultyBonusRate * (120 * timesCleared - newTime) * 100 + powerUpDeductions;
            
            int highscore = DatabaseManager.retrieveHighScore(name, "bottomless_abyss");
            if (score > highscore) DatabaseManager.updateHighscore(name, "bottomless_abyss", score);
        }

        // update game session with the updated values
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

        
        if (!gameWon) {
            SoundUtils.musicOff();
            SoundUtils.finished(gameWon);
            GameUtils.navigate("game-over.fxml", p_main);
            return;
        }

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
}
