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
        
        outroDialogueStart = 2;
        losingDialogueStart = 4;
        outroDialogueEnd = 6;

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
            "I see through your tricks. Your magic won’t protect you.",
            "You claim to be a solver... let’s see if your mind is truly unbreakable.",
            "The forest... fades. We were... only memories...",
            "They fought with riddles… but I answered with resolve.",
            "Even the clever trip when the path is crooked. You don’t belong in our domain.",
            "I let the puzzle beat me. But next time, I’ll crack it wide open.",
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
            case 2 -> {
                switchDialogue(false);
                t_villain.setText(text);
            }
            case 3 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
        }
    }

    @Override
    protected void losingDialogue(String text) {
        switch (CONVERSE_COUNT) {
            case 4 -> {
                switchDialogue(false);
                t_villain.setText(text);
            }
            case 5 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
        }
    }

}
