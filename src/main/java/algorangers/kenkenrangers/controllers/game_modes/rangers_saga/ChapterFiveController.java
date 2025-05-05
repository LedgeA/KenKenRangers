package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;
import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class ChapterFiveController extends BaseStoryController {
    
    @FXML
    protected void initialize() throws SQLException {

        gameMode = "chap_5";
        
        // difficulty
        DIMENSION = 6;
        dot = 5;

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

        // add k_view just below dialogue box
        int pos = p_main.getChildren().indexOf(tf_villain);
        p_main.getChildren().add(pos, k_view);
    }

    @Override
    protected void insertDialogues() {
        dialogue = new String[] {
            "No tricks. No flames. Just raw fury. And still… I’m not backing down.",
            "You wear strength like armor. But we are strength. And we will break you.",
            "So this… is your world after all…",
            "Even nature's fury couldn’t stop me. The end is near.",
            "The jungle consumes the weak. You were prey the moment you stepped in.",
            "I underestimated them… but they won’t get a second chance."
        };
	}

    @Override
    protected void introDialogue(String text) {
        if (CONVERSE_COUNT != 1) return;

        switchDialogue(false);
        t_villain.setText(text);
        
        gameStart();
   
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
