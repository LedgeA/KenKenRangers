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

        // difficulty
        DIMENSION = 4;
        dot = 10;

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        // dialogue points
        outroDialogueStart = 2;
        losingDialogueStart = 5;
        outroDialogueEnd = 8;

        // setup story dialogue
        insertDialogues();
        setUpDialogue();

        // enable powerups
        setupAllPowerUps();

        // disable powerups and grid
        GameUtils.setGridUnclickable(k_view, false);
        GameUtils.setComponentsClickable(new Node[]{s_powerSurge, s_invincibility, s_cellReveal}, true);
        
        // setup pause menu and finish button
        setUpPause();
        setupFinishButton();

        // add k_view just below tf_dialogue
        int pos = p_main.getChildren().indexOf(tf_villain);
        p_main.getChildren().add(pos, k_view);
    }

    @Override
    protected void insertDialogues() {
        dialogue = new String[] {
            "You’re in my way. But you won’t be for long.",
            "You think you can defeat me? I’ve crushed countless foes. You’ll be no different.",
            "How…?",
            "How could I fail...",
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
                switchDialogue(false);
                t_villain.setText(text);
                
                gameStart();
            }
        }
    }
    
    @Override
    protected void winningDialogue(String text) {
        switch (CONVERSE_COUNT) {
            case 2, 3 -> {
                switchDialogue(false);
                t_villain.setText(text);
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
            case 5 -> {
                switchDialogue(false);
                t_villain.setText(text);
            }
            case 6, 7 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
        }
    }

}
