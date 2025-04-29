package algorangers.kenkenrangers.controllers.game_modes.rangers_saga;

import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseGameController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class ChapterOneController extends BaseGameController {
    
    @FXML
    private TextFlow tf_player, tf_villain;

    @FXML
    private Text t_monologue, t_player, t_villain;

    @FXML
    private ImageView i_senior, i_player;

    @FXML
    private Pane p_monologue;

    @FXML
    private StackPane s_finish;

    @FXML 
    private Text t_sTime;

    private Timeline gameResultChecker;
    
    private int MONOLOGUE_COUNT = 0, CONVERSE_COUNT = 0;
    private String[] dialogue;
    private String[] backstoryMonologue;
    
    @FXML
    protected void initialize() throws SQLException {

        k_controller = new KenkenController(DIMENSION, dps, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        // Setup Story Dialogue
        insertDialogues();
        modifyGridFocus(k_view, false);
        startMonologue();

        // enable powerups
        powerUpsHandler();

        // Setup Pause Menu Visiblity
        setUpPause();

        // Add k_view just below tf_dialogue
        int pos = p_main.getChildren().indexOf(tf_villain);
        p_main.getChildren().add(pos, k_view);
    }

    private void insertDialogues() {
        // Lost Dialogue starts at index 5
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

        p_monologue.setOnMousePressed(event -> {
            // hide monologue if pressed during animation
            p_monologue.setVisible(false);
            p_monologue.setOnMousePressed(null);

            setUpDialogue();
            monologueTimeline.stop();
        });  
    }

    private void updateMonologue() {
        if (MONOLOGUE_COUNT == backstoryMonologue.length) return;
        
        AnimationUtils.fadeOut(() -> {
            t_monologue.setText(backstoryMonologue[MONOLOGUE_COUNT]);
            AnimationUtils.fadeIn(t_monologue);

            MONOLOGUE_COUNT++;
        }, t_monologue);
    }

    private void setUpDialogue() {
        tf_player.setVisible(true);
        t_player.setText(dialogue[CONVERSE_COUNT]);

        Background background = getTextFill();

        tf_player.setBackground(background);
        tf_villain.setBackground(background);
        
        p_main.setOnMouseClicked(event -> {
            if (paused) return;
            updateDialogue(2, 9);
        });
    }

    private void introDialogue(String text) {
        switch(CONVERSE_COUNT) {
            case 1 -> {
                switchRanger(true);
                t_player.setText(text);
            }
            case 2 -> {
                switchDialogue(false);
                t_villain.setText(text);
                
                modifyGridFocus(k_view, true);
                startTimer();
                startAttackInterval();
                addGameOverChecker();
            }
        }
    }

    private void winningDialogue(String text) {
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

    private void losingDialogue(String text) {
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

    private void updateDialogue(int gameStart, int gameEnd) {
        String text = dialogue[CONVERSE_COUNT];
        System.out.println(CONVERSE_COUNT);

        if (CONVERSE_COUNT <= gameStart) {
            introDialogue(text);
            CONVERSE_COUNT++;
            return;
        }
        
        tf_player.setVisible(false);
        tf_villain.setVisible(false);

        if (!gameOver) return;
        modifyGridFocus(k_view, false);
        s_finish.setVisible(true);

        if (!gameLost) {
            winningDialogue(text);
        } else {
            losingDialogue(text);
        }

        if (CONVERSE_COUNT == gameEnd) s_finish.setVisible(true);

        CONVERSE_COUNT++;
    }

    private void addGameOverChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (gameOver) {
                updateDialogue();
                gameResultChecker.stop();
                gameResultChecker = null;
            }
        }));

        gameResultChecker.setCycleCount(Animation.INDEFINITE);
        gameResultChecker.play();
    }
    
    private void switchDialogue(boolean isPlayer) {
        tf_player.setVisible(isPlayer);
        tf_villain.setVisible(!isPlayer);
    }

    private void switchRanger(boolean isPlayer) {
        i_player.setVisible(isPlayer);
        i_senior.setVisible(!isPlayer);
    }

}
