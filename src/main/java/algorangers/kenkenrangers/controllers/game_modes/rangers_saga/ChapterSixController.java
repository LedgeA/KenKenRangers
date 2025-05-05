package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;
import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class ChapterSixController extends BaseStoryController {
    
    @FXML
    protected void initialize() throws SQLException {

        gameMode = "chap_5";

        // difficulty
        DIMENSION = 6;
        dot = 10;

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        // dialogue points
        outroDialogueStart = 2;
        losingDialogueStart = 4;
        outroDialogueEnd = 6;

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
            "You’re the last. When your beacon breaks, this war ends.",
            "I am not a monster. I am the balance. The counterforce to your chaos.",
            "Break me… and you break everything.",
            "It’s over. We’re free. And I… I’m still standing.",
            "You reached the end, only to fall. Pitiful.",
            "Let the invasion continue.",
            "No... Not yet. I’ll come back. And next time, I’ll finish this.",
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
            case 2 -> {
                switchDialogue(false);
                t_villain.setText(text);
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
                switchDialogue(false);
                t_villain.setText(text);
            }
            case 6 -> {
                switchDialogue(true);
                t_player.setText(text);
            }
        }
    }

}
