package algorangers.kenkenrangers.controllers.base;

import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.helpers.ComponentCreator;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    protected int outroDialogueStart, outroDialogueEnd, losingDialogueStart, winningDialogueEnd;

    private boolean textSkipped;
    
    protected abstract void insertDialogues();
    protected abstract void introDialogue(String text);
    protected abstract void winningDialogue(String text);
    protected abstract void losingDialogue(String text);

    protected void updateDialogue() {

        // removes listener if dialogue has ended
        if (CONVERSE_COUNT == outroDialogueEnd || (CONVERSE_COUNT == losingDialogueStart && gameWon)) {
            tf_player.setVisible(false);
            p_main.setOnMouseClicked(null);
            return;
        }

        System.out.println("Dialogue Count: " + CONVERSE_COUNT);

        String text = dialogue[CONVERSE_COUNT]; 

        // intro dialogue
        if (CONVERSE_COUNT < outroDialogueStart) {
            SoundUtils.press();
            introDialogue(text);
            CONVERSE_COUNT++;
            return;
        }
        
        // hide both dialogues
        tf_player.setVisible(false);
        tf_villain.setVisible(false);

        // denies access to outro dialogues
        if (!gameOver) return;
        SoundUtils.press();
        
        // textSkipped flag used for skipping to losing dialogue if the game is lost
        if (!textSkipped && !gameWon) {
            CONVERSE_COUNT = losingDialogueStart;
            textSkipped = true;
        }

        GameUtils.setGridUnclickable(k_view, false);
        s_finish.setVisible(true);
        SoundUtils.musicOff();

        // branches to winning and losing dialogue
        if (gameWon) {
            winningDialogue(text);
        } else {
            losingDialogue(text);
        }

        CONVERSE_COUNT++;
    }

    // setup dialogue gui and add listener to main screen
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
    
    // used for switching visible dialogues boxes
    protected void switchDialogue(boolean isPlayer) {
        tf_player.setVisible(isPlayer);
        tf_villain.setVisible(!isPlayer);
    }

    // checks the if the game is over and if the game is won
    @Override
    protected void startGameResultChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!gameOver) return;

            if (!gameWon) CONVERSE_COUNT = losingDialogueStart;
            
            updateDialogue();
            stopAllTimelines();
            nullifyAllTimelines();
        }));

        gameResultChecker.setCycleCount(Animation.INDEFINITE);
        gameResultChecker.play();
    }

    // called after intro dialogue of chapter controllers
    protected void gameStart() {
        SoundUtils.musicOn();
        GameUtils.setGridUnclickable(k_view, true);
        GameUtils.setComponentsClickable(new Node[]{s_powerSurge, s_invincibility, s_cellReveal}, true);
        
        startTimer();
        startAttackInterval();
        startGameResultChecker();
    }

    // finish button for finishing game after outro dialogue
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
    protected void gameEnd(boolean gameWon) throws SQLException {

        // score computation
        int powerUpDeductions = 10 * (9 - countRemainingPowerups());
        int difficultyBonusRate = DIMENSION * dot;
        score = difficultyBonusRate * (120 - timeCount) * 100 + powerUpDeductions;

        if (!gameWon) score = 0; // sets score to 0 if you lost
        
        // update game_session
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

        // updates highscore if the current score is greater than highscore
        int highscore = DatabaseManager.retrieveHighScore(name, gameMode);
        if (score > highscore) DatabaseManager.updateHighscore(name, gameMode, score);
        
        SoundUtils.finished(gameOver);
        GameUtils.navigate("game-over.fxml", p_main);
    }
}
