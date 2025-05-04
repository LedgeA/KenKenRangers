package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class TutorialController extends BaseGameController {
    
    @FXML
    private TextFlow tf_dialogue;

    @FXML
    private Text t_dialogue;
    
    protected int DIALOGUE_COUNT;
    protected String[] dialogue;
    
    @FXML
    protected void initialize() throws SQLException {
        gameMode = "tutorial";

        // diffculty
        DIMENSION = 3;
        dot = 5;
        
        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        // setup tutorial dialogue
        insertDialogues();
        setTextFlowContent();

        // enable powerups
        setupAllPowerUps();

        // disable powerups and grid
        GameUtils.setGridUnclickable(k_view, false);
        GameUtils.setComponentsClickable(new Node[]{s_powerSurge, s_invincibility, s_cellReveal}, false);

        // setup pause menu
        setUpPause();
    
        // add k_view below tf_dialogue
        int pos = p_main.getChildren().indexOf(tf_dialogue);
        p_main.getChildren().add(pos, k_view);
    }

    protected void insertDialogues() {
        dialogue = new String[] {
            "Hello Ranger! Welcome to KENKEN RANGERS",
            "Your mission is to fill the grid with numbers, just like Sudoku. But there's a twist!",
            "The grid is divided into color-coded cages.",
            "Your goal is to use the numbers inside the cage to match the target number using the given math operation.",
            "Numbers must NOT repeat in any row or column.",
            "The numbers you use depend on the grid size:\n- For a 3×3 grid, use only numbers 1 to 3.\n- For a 4×4 grid, use numbers 1 to 4\n... and so on!",
            "Take too long in solving the Kenken Puzzle and the enemy will beat you.",
            "To make things exciting, we've added special power-ups!",
            "Invincibility makes you invincible for a set amount of time.",
            "Power Surge doubles the power of any super power casted within a set amount of time.",
            "Cell Reveal reveals 2 of your unsolved tiles.",
            "Try it out! Tap on a cell to enter a number."
        };
    }
    
    private void setTextFlowContent() {

        tf_dialogue.setVisible(true);
        t_dialogue.setText(dialogue[DIALOGUE_COUNT]);
        
        // background fill for tf_dialogue
        Color backgroundColor = Color.web("#F3EAD1", 0.8);
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(25), Insets.EMPTY);
        tf_dialogue.setBackground(new Background(backgroundFill));
        
        p_main.setOnMouseClicked(event -> {
            if (paused) return;
            DIALOGUE_COUNT++;

            SoundUtils.press();
            if (DIALOGUE_COUNT == 12) {
                // Start Timelines and enable buttons
                startTimer();
                startAttackInterval();
                startGameResultChecker();
                SoundUtils.musicOn();
                GameUtils.setGridUnclickable(k_view, true);
                GameUtils.setComponentsClickable(new Node[]{s_powerSurge, s_invincibility, s_cellReveal}, true);
            };

            if (DIALOGUE_COUNT >= dialogue.length) {
                // remove onclick property and hide dialogue
                p_main.setOnMouseClicked(null);
                tf_dialogue.setVisible(false);
                return;
            }

            t_dialogue.setText(dialogue[DIALOGUE_COUNT]);

        });
    }

    @Override
    protected void startGameResultChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!gameOver) return;
            gameEnd(true);
            timer.stop();
            attackInterval.stop();
            gameResultChecker.stop();
        }));

        gameResultChecker.setCycleCount(Timeline.INDEFINITE);
        gameResultChecker.play();
    }

    @Override
    protected void gameEnd(boolean cleared) {
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

        SoundUtils.musicOff();
        SoundUtils.finished(gameOver);
        GameUtils.navigate("game-over.fxml", p_main);
    }

}
