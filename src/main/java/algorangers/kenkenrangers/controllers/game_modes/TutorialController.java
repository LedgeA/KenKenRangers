package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TutorialController extends BaseGameController {
    
    @FXML
    private TextFlow tf_dialogue;

    @FXML
    private Text t_dialogue;
    
    protected int DIALOGUE_COUNT;
    protected String[] dialogue;
    
    @FXML
    protected void initialize() throws SQLException {

        k_controller = new KenkenController(DIMENSION, dps, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        // Setup Tutorial Dialogue
        insertDialogues();
        setTextFlowContent();
        GameUtils.setAllUnfocusable(k_view, false);

        // enable powerups
        powerUpsHandler();

        // Setup Pause Menu Visiblity
        setUpPause();
    
        // Add k_view at the below tf_dialogue
        int pos = p_main.getChildren().indexOf(tf_dialogue);
        p_main.getChildren().add(pos, k_view);
    }

    protected void insertDialogues() {
        dialogue = new String[] {
            "Hello Ranger! Welcome to KENKEN RANGERS",
            "Your mission is to fill the grid with numbers, just like Sudoku. But there's a twist!",
            "The grid is divided into color-coded cages.",
            "Your goal is to use the numbers inside the cage to match the target number using the given math operation.",
            "+ and × deals damage to the enemy. - and ÷ reduces the damage you receive.",
            "Numbers must NOT repeat in anyrow or column.",
            "The numbers you use depend on the grid size:\n- For a 3×3 grid, use only numbers 1 to 3.\n- For a 4×4 grid, use numbers 1 to 4\n... and so on!",
            "Take too long in solving the Kenken Puzzle and the enemy will beat you.",
            "To make things exciting, we've added special power-ups!",
            "Invincibility makes you invincible for a set amount of time.",
            "Power Surge doubles the power of any super power casted within a set amount of time.",
            "Cell Reveal reveals 2 of your unsolved tiles.",
            "Let's practice! Tap on a cell to enter a number.",
            "Use your skills, think strategically, and become a true KenKen master!",
            "Good luck, and may the numbers be ever in your favor!"
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
            if (!paused) DIALOGUE_COUNT++;

            if (DIALOGUE_COUNT == 12) {
                // Start Timelines and enable buttons
                startTimer();
                startAttackInterval();
                GameUtils.setAllUnfocusable(k_view, true);
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
    protected void gameEnd(boolean cleared) {

    }

}
