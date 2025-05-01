package algorangers.kenkenrangers.controllers.base;

import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.helpers.ComponentCreator;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public abstract class BaseStoryController extends BaseGameController {
    
    @FXML
    protected TextFlow tf_player, tf_villain;

    @FXML
    protected Text t_player, t_villain;

    @FXML
    protected ImageView i_player;

    @FXML
    protected StackPane s_finish;
    
    protected int CONVERSE_COUNT = 0;
    protected String[] dialogue;

    protected String name;
    protected int outroDialogueStart, outroDialogueEnd;

    protected abstract void insertDialogues();
    protected abstract void introDialogue(String text);
    protected abstract void winningDialogue(String text);
    protected abstract void losingDialogue(String text);

    protected void updateDialogue() {
        if (CONVERSE_COUNT == outroDialogueEnd) {
            p_main.setOnMouseClicked(null);
            return;
        }

        String text = dialogue[CONVERSE_COUNT];

        if (CONVERSE_COUNT < outroDialogueStart) {
            introDialogue(text);
            CONVERSE_COUNT++;
            return;
        }
        
        tf_player.setVisible(false);
        tf_villain.setVisible(false);

        if (!gameOver) return;
        GameUtils.setGridFocusable(k_view, false);
        s_finish.setVisible(true);

        if (gameWon) {
            winningDialogue(text);
        } else {
            losingDialogue(text);
        }

        CONVERSE_COUNT++;
    }

    protected void setUpDialogue() {
        tf_player.setVisible(true);
        t_player.setText(dialogue[CONVERSE_COUNT]);

        Background background = ComponentCreator.createTextFlowFill();

        tf_player.setBackground(background);
        tf_villain.setBackground(background);
        
        p_main.setOnMouseClicked(event -> {
            if (paused) return;
            updateDialogue();
        });
    }
    
    protected void switchDialogue(boolean isPlayer) {
        tf_player.setVisible(isPlayer);
        tf_villain.setVisible(!isPlayer);
    }

    protected void startGameResultChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!gameOver) return;

            if (!gameWon) CONVERSE_COUNT = 5;
            
            updateDialogue();
            stopAllTimelines();
            nullifyAllTimelines();
        }));

        gameResultChecker.setCycleCount(Animation.INDEFINITE);
        gameResultChecker.play();
    }

    protected void setupFinishButton() {
        s_finish.setOnMouseClicked(event -> {
            try {
                gameEnd(gameWon);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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

        int highscore = DatabaseManager.retrieveHighScore(name, gameMode);
        if (score > highscore) DatabaseManager.updateHighscore(name, gameMode, score);
        
        
        GameUtils.navigate("game-over.fxml", p_main);
    }
}
