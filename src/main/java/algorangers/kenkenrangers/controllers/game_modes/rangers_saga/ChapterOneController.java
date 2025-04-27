package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChapterOneController extends BaseStoryController{
    
    @FXML
    private TextFlow tf_dialogue;

    @FXML
    private Text t_dialogue;
    
    protected String[] dialogues;
    
    @FXML
    protected void initialize() throws SQLException {

        k_controller = new KenkenController(DIMENSION, dps, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        insertDialogues();
        setTextFlowContent();
        disableGridFocus(k_view);
        powerUpsHandler();

        // Add k_view just below the top most component 
        p_main.getChildren().add(p_main.getChildren().size() - 1, k_view);
    }

    protected void insertDialogues() {
        dialogues = new String[] {
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

        t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

        Color backgroundColor = Color.web("#F3EAD1", 0.8);
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(25), Insets.EMPTY);
        tf_dialogue.setBackground(new Background(backgroundFill));

        p_main.setOnMouseClicked(event -> {
            DIALOGUE_COUNT++;

            if (DIALOGUE_COUNT == 12) {
                startTimer();
                enableGridFocus(k_view);
            };
            if (DIALOGUE_COUNT >= dialogues.length) {
                p_main.setOnMouseClicked(null);
                tf_dialogue.setVisible(false);
                return;
            }

            t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

        });
    }

    private void disableGridFocus(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            child.setFocusTraversable(false);

            if (child instanceof Parent) {
                disableGridFocus((Parent) child);
            }
        }
    }

    private void enableGridFocus(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            child.setFocusTraversable(true);

            if (child instanceof Parent) {
                enableGridFocus((Parent) child);
            }
        }
    }

}
