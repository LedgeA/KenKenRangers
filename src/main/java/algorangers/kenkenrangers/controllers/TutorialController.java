package algorangers.kenkenrangers.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;

public class TutorialController extends BaseGameController{
    
    protected final String[] dialogues = {
        "Hello Ranger! Welcome to KENKEN RANGERS",
        "Your mission is to fill the grid with numbers, just like Sudoku. But there's a twist!",
        "The grid is divided into color-coded cages.",
        "Your goal is to use the numbers inside the cage to match the target number using the given math operation.",
        "+ and × deals damage to the enemy. - and ÷ reduces the damage you receive.",
        "Numbers must NOT repeat in anyrow or column.",
        "The numbers you use depend on the grid size:\n- For a 3×3 grid, use only\nnumbers 1 to 3.\n- For a 4×4 grid, use numbers 1 to 4\n... and so on!",
        "Take too long in solving the Kenken Puzzle and the enemy will beat you.",
        "To make things exciting, we've added special power-ups!",
        "Invincibility makes you invincible for a set amount of time.",
        "Power Surge doubles the power of any super power casted within a set amount of time.",
        "Cell Reveal reveals 2 of your unsolved tiles.",
        "Let's practice! Tap on a cell to enter a number.",
        "Use your skills, think strategically, and become a true KenKen master!",
        "Good luck, and may the numbers be ever in your favor!"
    };

    @Override
    @FXML
    protected void initialize() {
        k_controller = new KenkenController(4, 100, 10, 3, 3, 3);
        k_view = k_controller.getK_view();

        setBindings();
        setTextFlowContent();
        disableGridFocus(k_view);
        powerUpsHandler();
        

        // Add k_view just below the top most component 
        a_main.getChildren().add(a_main.getChildren().size() - 1, k_view);
    }

    @Override
    protected void setBindings() {
        
        bindGaugeSize();
        bindPowerUpsSize();
        bindCharacterSize();

        a_main.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            a_mainHeight = newValue.getHeight();
            a_mainWidth = newValue.getWidth();

            if (a_mainHeight > 0 && a_mainWidth > 0) {
                bindGaugePosition();

                bindPowerUpsPosition();

                bindCharacterPosition();

                bindDialogueProperties();

                bindKenKenProperties();
            }
        });
    }

    @Override
    protected void setTextFlowContent() {

        t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

        a_main.setOnMouseClicked(event -> {
            DIALOGUE_COUNT++;

            if (DIALOGUE_COUNT == 12) {
                startTimer();
                enableGridFocus(k_view);
            };
            if (DIALOGUE_COUNT >= dialogues.length) {
                a_main.setOnMouseClicked(null);
    
                return;
            }

            t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

            System.out.println(k_controller.getPowerSurge());
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
