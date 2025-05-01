package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;
import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class ChapterThreeController extends BaseStoryController {
    
    @FXML
    protected void initialize() throws SQLException {

        gameMode = "chap_3";

        DIMENSION = 5;
        dot = 10;

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        outroDialogueStart = 3;
        outroDialogueEnd = 9;

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
            "Your tests were off the charts. This beacon’s yours. Prove you're more than just talk.",
            "I won’t let humanity fall. Let’s see what these goblins are made of.",
            "Another fleshling dare approach? You know not what you touch!",
            "You did it, Ranger " + name + "! That was faster than any rookie I’ve ever seen…",
            "They underestimated me. I’ll make sure the next ones regret that too.",
            "Another would-be hero crushed beneath our swarm.",
            "Tell your elders: the beacons are eternal!",
            "No…",
            "I was too slow. But next time, I’ll end this."
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
