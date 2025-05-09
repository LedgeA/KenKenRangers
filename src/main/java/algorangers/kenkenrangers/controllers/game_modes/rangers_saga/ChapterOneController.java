package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;
import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.AnimationUtils;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ChapterOneController extends BaseStoryController {
    
    @FXML
    private Pane p_monologue;

    @FXML
    private Text t_monologue;

    @FXML
    private ImageView i_senior;

    private int MONOLOGUE_COUNT;
    private String[] backstoryMonologue;
    
    @FXML
    protected void initialize() throws SQLException {

        gameMode = "chap_1";

        // difficulty
        DIMENSION = 4;
        dot = 5;

        k_controller = new KenkenController(DIMENSION, dot, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();
        
        // dialogue points
        outroDialogueStart = 3;
        losingDialogueStart = 5;
        outroDialogueEnd = 9;

        // setup story dialogue
        insertDialogues();
        startMonologue();

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

    // starts monologue animation and texts
    // monologue is skipped if the screen is pressed
    private void startMonologue() {      
        Timeline monologueTimeline = new Timeline(
            new KeyFrame(Duration.seconds(3), event -> updateMonologue())
        );

        monologueTimeline.setCycleCount(backstoryMonologue.length); 
        monologueTimeline.setOnFinished(event -> {
            AnimationUtils.fadeOut(() -> {
                // hide monologue
                p_monologue.setVisible(false);

                // setup player-villain dialogue
                setUpDialogue();
            }, p_monologue);
        });

        monologueTimeline.play();

        // skip monologue if pressed
        p_monologue.setOnMousePressed(event -> {
            p_monologue.setVisible(false);
            p_monologue.setOnMousePressed(null);

            setUpDialogue();
            monologueTimeline.stop();
        });  
    }

    // triggers fade animation and replaces text
    private void updateMonologue() {
        if (MONOLOGUE_COUNT == backstoryMonologue.length) return;
        
        AnimationUtils.fadeOut(() -> {
            t_monologue.setText(backstoryMonologue[MONOLOGUE_COUNT]);
            AnimationUtils.fadeIn(t_monologue);

            MONOLOGUE_COUNT++;
        }, t_monologue);
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

        backstoryMonologue = new String[] {
            "Long ago, Earth was peaceful, protected by the KenKen Rangers",
            "Warriors who used both wisdom and power to maintain balance.",
            "Everything changed when a dimensional rift tore open beneath the earth",
            "From it came monsters—Goblin, Orc, Gnome, Dragon, Beast, and finally the Arcdemon.",
            "These creatures fed off the power of the Beacons, allowing them to survive, propagate, and spread.",
            "The KenKen Rangers rose to fight back.",
            "Among them, only a few could destroy the Beacons.",
            "You are one of them.",
            "A newcomer with the rare power to break their lifeline.",
            "The battle for Earth begins."
        };
	}

    @Override
    protected void introDialogue(String text) {
        switch(CONVERSE_COUNT) {
            case 1 -> {
                switchRanger(true);
                t_player.setText(text);
            }
            case 2 -> {
                switchDialogue(false);
                t_villain.setText(text);
                gameStart();
            }
        }
    }
    
    @Override
    protected void winningDialogue(String text) {
        switch (CONVERSE_COUNT) {
            case 3 -> {
                switchRanger(false);
                switchDialogue(true);
                t_player.setText(text);
            }
            case 4 -> {
                switchRanger(true);
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

    // used to switch between senior ranger and player
    private void switchRanger(boolean isPlayer) {
        i_player.setVisible(isPlayer);
        i_senior.setVisible(!isPlayer);
    }

}
