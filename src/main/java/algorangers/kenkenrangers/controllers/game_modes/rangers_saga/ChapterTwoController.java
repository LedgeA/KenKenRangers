package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;
import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class ChapterTwoController extends BaseStoryController {
    
    @FXML
    protected void initialize() throws SQLException {

        gameMode = "chap_2";

        DIMENSION = 4;
        dot = 20;

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        outroDialogueStart = 6;
        outroDialogueEnd = 8;

        // Setup Story Dialogue
        insertDialogues();
        setUpDialogue();

        // enable powerups
        powerUpsHandler();

        // disable powerups and grid
        GameUtils.setGridFocusable(k_view, false);
        GameUtils.setComponentsClickable(new Node[]{s_powerSurge, s_invincibility, s_cellReveal}, true);
        
        // Setup Pause Menu Visiblity
        setUpPause();
        setupFinishButton();

        // Add k_view just below tf_dialogue
        int pos = p_main.getChildren().indexOf(tf_villain);
        p_main.getChildren().add(pos, k_view);
    }

    @Override
    protected void insertDialogues() {
        dialogue = new String[] {
            "You’re in my way. But you won’t be for long.",
            "You think you can defeat me? I’ve crushed countless foes. You’ll be no different.",
            "How…?",
            "How could I fail",
            "Strength is nothing without purpose. And you had neither.",
            "Pathetic. You never had a chance. I’ll make sure of that.",
            "I slipped up…",
            "But I won’t again. You’ll fall next time.",
        };
	}

    @Override
    protected void introDialogue(String text) {
        switch(CONVERSE_COUNT) {
            case 1 -> {
                t_player.setText(text);
            }
            case 2 -> {
                switchDialogue(false);
                t_villain.setText(text);
                
                GameUtils.setGridFocusable(k_view, true);
                GameUtils.setComponentsClickable(new Node[]{s_powerSurge, s_invincibility, s_cellReveal}, true);
                startTimer();
                startAttackInterval();
                startGameResultChecker();
            }
        }
    }
    
    @Override
    protected void winningDialogue(String text) {
        switch (CONVERSE_COUNT) {
            case 3 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
            case 4 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
        }
    }

    @Override
    protected void losingDialogue(String text) {
        switch (CONVERSE_COUNT) {
            case 5, 6 -> {
                switchDialogue(false);
                t_villain.setText(text);
            }
            case 7, 8 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
        }
    }

}
