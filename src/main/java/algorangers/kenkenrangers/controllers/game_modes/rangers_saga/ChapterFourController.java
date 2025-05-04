package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;
import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class ChapterFourController extends BaseStoryController {
    
    @FXML
    protected void initialize() throws SQLException {

        gameMode = "chap_4";

        // difficulty
        DIMENSION = 5;
        dot = 15;

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
            "No matter how high you fly... I’ll bring your beacon down.",
            "You speak boldly for a creature of dust. Prepare to be scorched.",
            "So… even flame yields to your mind… impressive…",
            "Wings burn. Beacons break. Nothing is untouchable.",
            "You aim for the heavens with weak arms. Fall!",
            "Too slow… but I’ve learned. And I’ll rise again.",
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
