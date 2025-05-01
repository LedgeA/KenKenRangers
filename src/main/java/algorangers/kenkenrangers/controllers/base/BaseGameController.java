package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;

import java.sql.SQLException;
import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import algorangers.kenkenrangers.helpers.ComponentCreator;
import algorangers.kenkenrangers.utils.*;

public abstract class BaseGameController {
    
    @FXML
    protected Pane p_main;

    @FXML
    protected Pane p_pause;

    @FXML
    protected Text t_time, t_sTime;

    @FXML
    protected Rectangle r_dim;
    
    @FXML
    protected ImageView i_resume, i_quit;

    @FXML
    protected StackPane s_gauge;

    @FXML
    protected Rectangle r_gaugeBase, r_gaugeMeter;

    @FXML
    protected StackPane s_powerSurge, s_invincibility, s_cellReveal;

    @FXML
    protected Text t_powerSurge, t_invincibility, t_cellReveal;

    @FXML
    protected ImageView i_character;

    @FXML
    protected VBox v_cooldowns;

    protected KenkenController k_controller;
    protected GridPane k_view;

    protected Timeline timer, attackInterval, gameResultChecker;
    protected int timeCount = 0;

    // Database Parameters
    protected String name;
    protected int DIMENSION = 4;
    protected int hp = 100, dot = 10;
    protected int powerSurge = 3, invincibility = 3, cellReveal = 3;
    protected String gameMode;

    protected boolean paused = false;
    protected boolean gameOver = false, gameWon = true;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    protected abstract void startGameResultChecker();
    protected abstract void gameEnd(boolean cleared) throws SQLException;
    protected int score = 0;

    protected void setUpPause() {
        p_main.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) return; 
            newScene.setOnKeyPressed(event -> escapePressed(event));
        });

        i_resume.setOnMouseClicked(event -> {
            playAllTimelines();
            paused = false;

            p_pause.setVisible(false);
        });

        i_quit.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    private void escapePressed(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE) return;

        paused = !paused;
        
        if (paused) {
            stopAllTimelines();
            t_time.setText(GameUtils.timeToString(timeCount)); // update time
        } else {
            playAllTimelines();
        }
    
        p_pause.setVisible(paused);
    }

    protected void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeCount++;
            updateGaugeMeter();

            t_sTime.setText(GameUtils.timeToString(timeCount));

            if (k_controller.getRemainingCageAmount() == 0) {
                timer.stop();
                if (attackInterval != null) attackInterval.stop();
                
                gameOver = true;
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        
    }

    protected void startAttackInterval() {
        attackInterval = new Timeline(new KeyFrame(Duration.seconds(5), event -> {

            if (!k_controller.getInvincibleState()) k_controller.decreaseHp();            

            // if hp runs out, end the game
            if (k_controller.getHp() <= 0) {
                gameWon = false;
                gameOver = true;
                
                timer.stop();
                attackInterval.stop();
            }
        }));
        
        attackInterval.setCycleCount(Timeline.INDEFINITE); 
        attackInterval.play();
    }
    
    protected void powerUpsHandler() {
        setupPowerUp(s_powerSurge, k_controller::consumePowerSurge, k_controller::getPowerSurge, "double-damage");
        setupPowerUp(s_invincibility, k_controller::consumeInvincibility, k_controller::getInvincibility, "invincibility");
        setupPowerUp(s_cellReveal, k_controller::consumeCellReveal, k_controller::getCellReveal, "cell-reveal");
    }

    private void setupPowerUp(StackPane powerUp, Runnable consume, Supplier<Integer> remainingCount, String imgName) {
        powerUp.setOnMouseClicked(event -> {
            consume.run();
            powerUp.setDisable(true);

            updatePowerUpCount(powerUp, remainingCount.get());
            Arc arc = ComponentCreator.addCooldownImages(v_cooldowns, imgName);
            setCooldown(powerUp, arc);

            if (remainingCount.get() == 0) {
                powerUp.setOnMouseClicked(null);
                return;
            } 
        });
    }
    
    private void setCooldown(StackPane powerUp, Arc arc) {
        double cooldownTime = 5 * k_controller.getMultiplier();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(arc.lengthProperty(), 360)),
            new KeyFrame(Duration.seconds(cooldownTime), new KeyValue(arc.lengthProperty(), 0))
        );

        timeline.setOnFinished(e -> {
            powerUp.setDisable(false);

            if (powerUp == s_invincibility) k_controller.invincibilityWearOff();
            if (powerUp == s_powerSurge) k_controller.multiplierWearOff();

            v_cooldowns.getChildren().remove(0); 
        });

        timeline.play();
    }

    private void updatePowerUpCount(StackPane powerUp, int remainingCount) {
        Pane pane = (Pane) powerUp.getChildren().get(1);
        Text text = (Text) pane.getChildren().get(0);

        text.setText(String.valueOf(remainingCount));
    }
    
    protected void setPowerUpFocus(boolean isFocusable) {
        s_powerSurge.setFocusTraversable(isFocusable);
        s_invincibility.setFocusTraversable(isFocusable);
        s_cellReveal.setFocusTraversable(isFocusable);
    }

    protected void updateGaugeMeter() {
        double hpPercentage = k_controller.getHp() / 100.0;
        double currentMeterHeight = 540 * hpPercentage;

        if (currentMeterHeight < 25) {
            currentMeterHeight = 25;
        } else if (currentMeterHeight > 540) {
            currentMeterHeight = 540;
        }

        // update size
        r_gaugeMeter.setHeight(currentMeterHeight);
    }

    protected int computeStars() {
        if (timeCount == 0) return 0;

        int stars = 1; 
        
        if (arePowerupsUnused()) {
            stars++;
        }

        if (timeCount <= 120) {
            stars++;
        }

        return stars;
    }

    private boolean arePowerupsUnused() {
        return k_controller.getPowerSurge() == powerSurge &&
               k_controller.getInvincibility() == invincibility &&
               k_controller.getCellReveal() == cellReveal;
    }

    protected int countRemainingPowerups() {
        return  k_controller.getPowerSurge() +
                k_controller.getInvincibility() + 
                k_controller.getCellReveal();
    }

    protected void playAllTimelines() {
        if (timer != null) timer.play();
        if (attackInterval != null) attackInterval.play();
        if (gameResultChecker != null) gameResultChecker.play();
    }

    protected void stopAllTimelines() {
        if (timer != null) timer.stop();
        if (attackInterval != null) attackInterval.stop();
        if (gameResultChecker != null) gameResultChecker.stop();
    }

    protected void nullifyAllTimelines() {
        timer = null;
        attackInterval = null;
        gameResultChecker = null;
    }
}
