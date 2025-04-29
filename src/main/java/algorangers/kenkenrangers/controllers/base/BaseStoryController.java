package algorangers.kenkenrangers.controllers.base;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public abstract class BaseStoryController extends BaseGameController {
    
    @FXML
    protected TextFlow tf_player, tf_villain;

    @FXML
    protected Text t_player, t_villain;

    @FXML
    protected ImageView i_senior, i_player;

    @FXML
    protected StackPane s_finish;

    protected Timeline gameResultChecker;
    
    protected int CONVERSE_COUNT = 0;
    protected String[] dialogue;

    protected int lastIntroDialogue, lastOutroDialogue;

    protected abstract void insertDialogues();
    protected abstract void introDialogue(String text);
    protected abstract void winningDialogue(String text);
    protected abstract void losingDialogue(String text);

    protected void updateDialogue() {
        String text = dialogue[CONVERSE_COUNT];
        System.out.println(CONVERSE_COUNT);

        if (CONVERSE_COUNT <= lastIntroDialogue) {
            introDialogue(text);
            CONVERSE_COUNT++;
            return;
        }
        
        tf_player.setVisible(false);
        tf_villain.setVisible(false);

        if (!gameOver) return;
        modifyGridFocus(k_view, false);
        s_finish.setVisible(true);

        if (gameWon) {
            winningDialogue(text);
        } else {
            losingDialogue(text);
        }

        if (CONVERSE_COUNT == lastOutroDialogue) return;

        CONVERSE_COUNT++;
    }

    protected void setUpDialogue() {
        tf_player.setVisible(true);
        t_player.setText(dialogue[CONVERSE_COUNT]);

        Background background = getTextFill();

        tf_player.setBackground(background);
        tf_villain.setBackground(background);
        
        p_main.setOnMouseClicked(event -> {
            if (paused) return;
            updateDialogue();
        });
    }
    
    protected void switchDialogue(boolean isPlayer) {
        tf_player.setVisible(isPlayer);
        tf_villain.setVisible(!isPlayer);
    }

    protected void switchRanger(boolean isPlayer) {
        i_player.setVisible(isPlayer);
        i_senior.setVisible(!isPlayer);
    }

    protected void addGameResultChecker() {
        gameResultChecker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (gameOver) {
                if (!gameWon) CONVERSE_COUNT = 5;
                
                updateDialogue();
                gameResultChecker.stop();
                gameResultChecker = null;
            }
        }));

        gameResultChecker.setCycleCount(Animation.INDEFINITE);
        gameResultChecker.play();
    }

    protected void setupFinishButton() {
        s_finish.setOnMouseClicked(event -> {
            gameEnd(gameWon);
        });
    }

}
