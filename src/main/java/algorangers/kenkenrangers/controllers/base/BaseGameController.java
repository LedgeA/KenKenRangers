package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;

import java.sql.SQLException;
import java.util.function.Supplier;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
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
    protected Text t_time, t_sTime, t_dot;

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
    protected ImageView i_character, i_powerUpUsed;

    @FXML
    protected VBox v_cooldowns;

    protected KenkenController k_controller;
    protected GridPane k_view;

    protected Timeline timer, attackInterval, gameResultChecker;
    protected int timeCount = 0;

    private Timeline powerUpUsed; 

    // Database Parameters
    protected String name;
    protected String gameMode;
    protected int DIMENSION = 4;
    protected int hp = 100, dot = 10;
    protected int powerSurge = 3, invincibility = 3, cellReveal = 3;
    protected int score = 0;

    protected boolean paused = false;
    protected boolean gameOver = false, gameWon = true;

    protected double prevMeterHeight = 540;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    protected abstract void startGameResultChecker();
    protected abstract void gameEnd(boolean gameWon) throws SQLException;

    // add listener to scene itself
    // stops timelines or plays it based on button pressed
    protected void setUpPause() {
        p_main.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) return; 
            newScene.setOnKeyPressed(event -> escapePressed(event));
        });

        i_resume.setOnMouseClicked(event -> {
            SoundUtils.press();
            paused = false;
            
            playAllTimelines();
            p_pause.setVisible(false);
        });

        i_quit.setOnMouseClicked(event -> {
            SoundUtils.press();
            SoundUtils.musicOff();
            
            stopAllTimelines();
            nullifyAllTimelines();
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    // triggers pause when escape is pressed
    private void escapePressed(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE) return;

        paused = !paused;
        SoundUtils.press();

        if (paused) {
            SoundUtils.musicPause();
            pauseAllTimelines();
            t_time.setText(GameUtils.timeToString(timeCount)); // update time
        } else {
            SoundUtils.musicOn();
            playAllTimelines();
        }
    
        p_pause.setVisible(paused);
    }

    // obligation is to update time and gauge and check if grid is already cleared 
    // set gameOver to true if so (gameWon is true by default so no need to update it)
    protected void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeCount++;
            updateGaugeMeter();

            t_sTime.setText(GameUtils.timeToString(timeCount));

            if (k_controller.getRemainingCageAmount() == 0) {
                gameOver = true;
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        
    }

    // obligation is to inflict damage to player if not in invincible state
    // checks if the player ran out of hp, then sets gameOver to true and gameWon to false
    protected void startAttackInterval() {
        attackInterval = new Timeline(new KeyFrame(Duration.seconds(5), event -> {

            if (k_controller.getInvincibleState()) {
                SoundUtils.blocked();
            } else {
                k_controller.decreaseHp();
            }
            
            // if hp runs out, end the game
            if (k_controller.getHp() <= 0) {
                updateGaugeMeter();
                gameWon = false;
                gameOver = true;
            }
        }));
        
        attackInterval.setCycleCount(Timeline.INDEFINITE); 
        attackInterval.play();
    }
    

    protected void setupAllPowerUps() {
        setupPowerUp(s_powerSurge, k_controller::consumePowerSurge, k_controller::getPowerSurge, "power-surge");
        setupPowerUp(s_invincibility, k_controller::consumeInvincibility, k_controller::getInvincibility, "invincibility");
        setupPowerUp(s_cellReveal, k_controller::consumeCellReveal, k_controller::getCellReveal, "cell-reveal");

        setupPowerUpCast();
    }

    // clips casted power up
    private void setupPowerUpCast() {
        Rectangle clip = new Rectangle(60, 60);
        clip.setArcWidth(20);  
        clip.setArcHeight(20);
        clip.widthProperty().bind(i_powerUpUsed.fitWidthProperty());
        clip.heightProperty().bind(i_powerUpUsed.fitHeightProperty());

        i_powerUpUsed.setClip(clip);
    }

    // when powerup is casted, decreases powerup count, starts cooldown, and applies cast effect
    // disable powerup if it ran out
    private void setupPowerUp(StackPane powerUp, Runnable consume, Supplier<Integer> remainingCount, String imgName) {
        powerUp.setOnMouseClicked(event -> {
            SoundUtils.cast();

            consume.run();
            powerUp.setDisable(true);

            updatePowerUpCount(powerUp, remainingCount.get());
            setCooldown(powerUp, ComponentCreator.addCooldownImages(v_cooldowns, imgName));
            setPowerUpCast(imgName);

            if (remainingCount.get() == 0) powerUp.setOnMouseClicked(null);
        });
    }
    
    // animates during cooldown
    // disables powered-up state after cooldown
    // removes cooldowned power up in gui 
    private void setCooldown(StackPane powerUp, Arc arc) {
        double cooldownTime = 5 * k_controller.getMultiplier(); // when powerup is buffed, cooldown is increased
        
        Timeline cooldown = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(arc.lengthProperty(), 360)),
            new KeyFrame(Duration.seconds(cooldownTime), new KeyValue(arc.lengthProperty(), 0))
        );

        cooldown.setOnFinished(e -> {
            SoundUtils.recharge();
            powerUp.setDisable(false);

            if (powerUp == s_invincibility) k_controller.invincibilityWearOff();
            if (powerUp == s_powerSurge) k_controller.multiplierWearOff();
            
            v_cooldowns.getChildren().remove(0); 
        });

        cooldown.play();
    }

    // set ups the animation for when a powerup is cast
    private void setPowerUpCast(String imgName) {
        // if another power up is casted and is in mid animation, cancel it
        if (powerUpUsed != null && powerUpUsed.getStatus() == Animation.Status.RUNNING) {
            powerUpUsed.stop();
        }

        ComponentCreator.updatePowerUpCastImage(i_powerUpUsed, imgName);
        
        powerUpUsed = new Timeline(
            new KeyFrame(Duration.millis(500),
                new KeyValue(i_powerUpUsed.fitWidthProperty(), 120, Interpolator.EASE_BOTH),
                new KeyValue(i_powerUpUsed.fitHeightProperty(), 120, Interpolator.EASE_BOTH),
                new KeyValue(i_powerUpUsed.opacityProperty(), 0, Interpolator.EASE_BOTH)
            )
        );
        
        powerUpUsed.setOnFinished(event -> i_powerUpUsed.setImage(null));

        powerUpUsed.play();
    }

    // updates count inside the powerup image
    private void updatePowerUpCount(StackPane powerUp, int remainingCount) {
        Pane pane = (Pane) powerUp.getChildren().get(1);
        Text text = (Text) pane.getChildren().get(0);

        text.setText(String.valueOf(remainingCount));
    }

    protected void updateGaugeMeter() {
        double hpPercentage = k_controller.getHp() / 100.0;
        double currentMeterHeight = 540 * hpPercentage;

        if (!gameWon) currentMeterHeight = 0;

        // track 
        if (currentMeterHeight <= 25 && currentMeterHeight > 0) {
            prevMeterHeight = 25;
            currentMeterHeight = 25;
        } else if (currentMeterHeight > 540) {
            prevMeterHeight = 540;
            currentMeterHeight = 540;
        }

        if (prevMeterHeight == currentMeterHeight) return; // cancel update if hp is unchanged

        prevMeterHeight = currentMeterHeight; // set current height as the previous

        // show damage count
        t_dot.setText("- " + String.valueOf(dot));
        t_dot.setVisible(true);

        // update animate size and text
        Timeline hpUpdater = 
            new Timeline(new KeyFrame(Duration.seconds(1),
                new KeyValue(r_gaugeMeter.heightProperty(), currentMeterHeight, Interpolator.EASE_OUT),
                new KeyValue(t_dot.layoutYProperty(), 121, Interpolator.EASE_OUT)
            )
        );

        hpUpdater.setOnFinished(event -> {
            t_dot.setLayoutY(230);
            t_dot.setVisible(false);
        });

        hpUpdater.play();
    }
    
    // computes stars with one being the default
    protected int computeStars() {
        if (timeCount == 0) return 0; // if player lost
        
        int stars = 1; 
        if (arePowerupsUnused()) stars++;
        if (timeCount <= 120) stars++;

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

    protected void pauseAllTimelines() {
        if (timer != null) timer.pause();
        if (attackInterval != null) attackInterval.pause();
        if (gameResultChecker != null) gameResultChecker.pause();
    }

    protected void nullifyAllTimelines() {
        timer = null;
        attackInterval = null;
        gameResultChecker = null;
    }
}
//      