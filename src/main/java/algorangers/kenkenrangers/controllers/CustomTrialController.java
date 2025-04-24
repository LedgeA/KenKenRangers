package algorangers.kenkenrangers.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
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

public class CustomTrialController extends BaseGameController {
    
    @FXML
    private TextFlow tf_dialogue;

    @FXML
    private Text t_dialogue;
    
    protected final String[] dialogues = {
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
    
    @FXML
    protected void initialize() throws SQLException {
        retrieveGameData();

        k_controller = new KenkenController(DIMENSION, dps, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        setTextFlowContent();
        disableGridFocus(k_view);
        powerUpsHandler();

        // Add k_view just below the top most component 
        p_main.getChildren().add(p_main.getChildren().size() - 1, k_view);
    }

    @Override
    protected void retrieveGameData() throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        while (rs.next()) {
            this.name = rs.getString("player_name");
            this.dps = rs.getInt("dps");

            this.powerSurge = rs.getInt("powersurge_initial");
            this.invincibility = rs.getInt("invincibility_initial");
            this.cellReveal = rs.getInt("cellreveal_initial");

            this.powerSurgeUsed = rs.getInt("powersurge_used");
            this.invincibilityUsed = rs.getInt("invincibility_used");
            this.cellRevealUsed = rs.getInt("cellreveal_used");

            this.characterExists = rs.getInt("character_exists");
            this.stars = rs.getInt("stars");
            
        }
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
