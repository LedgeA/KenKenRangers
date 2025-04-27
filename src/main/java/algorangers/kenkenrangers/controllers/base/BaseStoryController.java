package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;

import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.*;

public class BaseStoryController {
    
    @FXML
    protected Pane p_main;

    @FXML
    protected Text t_time;
    
    @FXML
    protected ImageView i_pause, i_board, i_resume, i_quit;

    @FXML
    protected StackPane s_gauge;

    @FXML
    protected Rectangle r_gaugeBase, r_gaugeMeter;

    @FXML
    protected Pane p_powerUps;

    @FXML
    protected StackPane s_powerUps;
    
    @FXML
    protected Rectangle r_powerUps;

    @FXML
    protected StackPane s_powerSurge, s_invincibility, s_cellReveal;

    @FXML
    protected Text t_powerSurge, t_invincibility, t_cellReveal;

    @FXML
    protected ImageView i_character;

    protected KenkenController k_controller;
    protected GridPane k_view;

    protected int powerMultiplier = 1;
    protected int timeCount = 0;

    protected Timeline attackInterval;
    protected Timeline timer;

    // Default values
    protected int DIMENSION = 4;
    protected int hp = 100, dps = 10;
    protected int powerSurge = 3, invincibility = 3, cellReveal = 3;

    protected String name;
    protected int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    protected int characterExists, stars;
    
    protected String[] dialogues;
    protected int DIALOGUE_COUNT = 0;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    protected void startTimer() {
        Timeline timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            timeCount++;
            updateGaugeMeter(); 

            if (k_controller.getRemainingCageAmount() == 0) {
                gameEnd();
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        
        attackInterval = new Timeline();
        attackInterval.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {
            if (!k_controller.getInvincibleState()) {
                k_controller.decreaseHp();            
            }

            if (k_controller.getHp() <= 0) {
                gameEnd();
            }
        }));
        
        attackInterval.setCycleCount(Timeline.INDEFINITE); 
        attackInterval.play();
        
    }

    protected void gameEnd() {
        timer.stop();
        attackInterval.stop();

        DatabaseManager.updateEndGameSession(
            name, 
            k_controller.getPowerSurge(), 
            k_controller.getInvincibility(), 
            k_controller.getCellReveal(), 
            0, 
            computeStars());
            
        GameUtils.navigate("game-over.fxml", p_main); 
    }

    protected void powerUpsHandler() {
        setupPowerUp(s_powerSurge, k_controller::consumePowerSurge, k_controller::getPowerSurge);
        setupPowerUp(s_invincibility, k_controller::consumeInvincibility, k_controller::getInvincibility);
        setupPowerUp(s_cellReveal, k_controller::consumeCellReveal, k_controller::getCellReveal);
    }

    protected void setupPowerUp(StackPane powerUp, Runnable consume, Supplier<Integer> remainingCount) {
        powerUp.setOnMouseClicked(event -> {
            consume.run(); // consume charge of power up
            powerUp.setDisable(true); // temporarily disable button

            // retrieve power up text
            Pane pane = (Pane) powerUp.getChildren().get(1);
            Text text = (Text) pane.getChildren().get(0);

            // set text to the remaning power up count
            text.setText(String.valueOf(remainingCount.get()));

            // disable power up if it is exhausted, else put on cooldown
            if (remainingCount.get() == 0) {
                powerUp.setOnMouseClicked(null);
            } else {
                setCooldown(powerUp);
            }
        });
    }

    protected void setCooldown(StackPane powerUp) {
        // set duration of cooldown to 10 seconds if the power up used is invincibility
        int duration = (powerUp == s_invincibility) ? 10 : 5;

        // set duration of cooldown to duration times the current multiplier value (modifiable by powerSurge)
        PauseTransition cooldown = new PauseTransition(Duration.seconds(duration * k_controller.getMultiplier()));
        cooldown.setOnFinished(e -> {
            // re-enable power up
            powerUp.setDisable(false);

            // set powerup status to false
            if (powerUp == s_invincibility) k_controller.invincibilityWearOff();
            if (powerUp == s_powerSurge) k_controller.multiplierWearOff();
        });
        
        cooldown.play(); // run cooldown
    }

    protected void updateGaugeMeter() {
        // get pixel height appropriate for current hp
        double hpPercentage = k_controller.getHp() / 100.0;
        double currentMeterHeight = 540 * hpPercentage;

        // if computed height makes meter smaller than a circle, sets height equivalent to circle
        if (currentMeterHeight < 64) {
            currentMeterHeight = 64;
        }

        // update size
        GameUtils.bindSize(r_gaugeMeter, p_main, 64, currentMeterHeight);
    }

    protected int computeStars() {
        int stars = 1; // default star count

        // if a power up is used, add a star
        if (k_controller.getPowerSurge() == this.powerSurge && k_controller.getInvincibility() == this.invincibility && k_controller.getCellReveal() == this.cellReveal) {
            stars++;
        }

        // if kenken is finished in less than 2 minutes, add a star
        if (timeCount <= 120) {
            stars++;
        }

        if (timeCount == 0) {
            return 0;
        }

        return stars;
    }

}
