package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;

import java.sql.SQLException;
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
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.*;

public abstract class BaseGameController {
    
    @FXML
    protected Pane p_main;

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
    protected ImageView i_powerSurge, i_invincibility, i_cellReveal;

    @FXML
    protected ImageView i_character;

    protected int DIALOGUE_COUNT = 0;

    protected Timeline attackInterval;
    protected int powerMultiplier = 1;
    protected int timeCounter = 0;
    
    protected KenkenController k_controller;

    protected GridPane k_view;
    
    // Default values
    protected int DIMENSION = 4;
    protected int hp = 100, dps = 10;
    protected int powerSurge = 3, invincibility = 3, cellReveal = 3;

    protected String name;
    protected int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    protected int characterExists, stars;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    protected abstract void retrieveGameData() throws SQLException;

    protected void startTimer() {
        Timeline timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            timeCounter++;

            if (k_controller.getRemainingCageAmount() == 0) {
                timer.stop();

                DatabaseManager.updateEndGameSession(
                    name, k_controller.getPowerSurge(), 
                    k_controller.getInvincibility(), 
                    k_controller.getCellReveal(), 
                    timeCounter, computeStars());
                
                GameUtils.navigate("game-over.fxml", p_main);
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        

        attackInterval = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (!k_controller.getInvincibleState()) {
                k_controller.decreaseHp(); 
                updateGaugeMeter();            
            }

            if (k_controller.getHp() <= 0) {
                attackInterval.stop();
                
                DatabaseManager.updateEndGameSession(
                    name, k_controller.getPowerSurge(), 
                    k_controller.getInvincibility(), 
                    k_controller.getCellReveal(), 
                    0, computeStars());
                    
                GameUtils.navigate("game-over.fxml", p_main);   
            }
        }));
        
        attackInterval.setCycleCount(Timeline.INDEFINITE); 
        attackInterval.play();
        
    }

    protected void powerUpsHandler() {
        setupPowerUp(i_powerSurge, k_controller::consumePowerSurge, k_controller::getPowerSurge);
        setupPowerUp(i_invincibility, k_controller::consumeInvincibility, k_controller::getInvincibility);
        setupPowerUp(i_cellReveal, k_controller::consumeCellReveal, k_controller::getCellReveal);
    }

    protected void setupPowerUp(ImageView powerUp, Runnable consume, Supplier<Integer> remainingCount) {
        powerUp.setOnMouseClicked(event -> {
            consume.run();
            powerUp.setDisable(true);

            if (remainingCount.get() == 0) {
                powerUp.setOnMouseClicked(null);
            } else {
                setCooldown(powerUp);
            }
        });
    }

    protected void setCooldown(ImageView powerUp) {
        int duration = (powerUp == i_invincibility) ? 10 : 5;

        PauseTransition cooldown = new PauseTransition(Duration.seconds(duration * k_controller.getMultiplier()));
        cooldown.setOnFinished(e -> {
            powerUp.setDisable(false);
            if (powerUp == i_invincibility) k_controller.invincibilityWearOff();
            if (powerUp == i_powerSurge) k_controller.multiplierWearOff();
        });

        cooldown.play();
    }

    protected void updateGaugeMeter() {
        double hpPercentage = k_controller.getHp() / 100.0;
        double currentMeterWidth = 540 * hpPercentage;

        if (currentMeterWidth <= 64) {
            return;
        }

        GameUtils.bindSize(r_gaugeMeter, p_main, 64, currentMeterWidth);
    }

    protected int computeStars() {
        int stars = 1;

        if (k_controller.getPowerSurge() == this.powerSurge && k_controller.getInvincibility() == this.invincibility && k_controller.getCellReveal() == this.cellReveal) {
            stars++;
        }

        if (timeCounter <= 120) {
            stars++;
        }

        return stars;
    }

}
