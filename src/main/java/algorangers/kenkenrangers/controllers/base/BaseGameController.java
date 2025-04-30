package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;

import java.sql.SQLException;
import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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

    protected KenkenController k_controller;
    protected GridPane k_view;

    protected Timeline attackInterval;
    protected Timeline timer;
    protected int timeCount = 0;

    // Default values
    protected int DIMENSION = 4;
    protected int hp = 100, dps = 10;
    protected int powerSurge = 3, invincibility = 3, cellReveal = 3;


    protected boolean paused = false;
    protected boolean gameOver = false, gameWon = true;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    protected abstract void gameEnd(boolean cleared) throws SQLException;
    protected int score;

    protected void setUpPause() {
        p_main.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) return; 
            newScene.setOnKeyPressed(event -> escapePressed(event));
        });

        i_resume.setOnMouseClicked(event -> {
            if (timer != null) timer.play();
            if (attackInterval != null) attackInterval.stop(); 
            paused = false;

            // Hide pause menu
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
            if (timer != null) timer.stop();
            if (attackInterval != null) attackInterval.stop();

            t_time.setText(GameUtils.timeToString(timeCount)); // update time
        } else {
            if (timer != null) timer.play();
            if (attackInterval != null) attackInterval.play();
        }
    
        p_pause.setVisible(paused);
    }

    protected void startTimer() {
        timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
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

        // Deals damage every 5 seconds
        attackInterval = new Timeline();
        attackInterval.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {

            // decrease hp if player isn't invincible
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
        setupPowerUp(s_powerSurge, k_controller::consumePowerSurge, k_controller::getPowerSurge);
        setupPowerUp(s_invincibility, k_controller::consumeInvincibility, k_controller::getInvincibility);
        setupPowerUp(s_cellReveal, k_controller::consumeCellReveal, k_controller::getCellReveal);
    }

    private void setupPowerUp(StackPane powerUp, Runnable consume, Supplier<Integer> remainingCount) {
        powerUp.setOnMouseClicked(event -> {
            consume.run();
            powerUp.setDisable(true);

            Pane pane = (Pane) powerUp.getChildren().get(1);
            Text text = (Text) pane.getChildren().get(0);

            text.setText(String.valueOf(remainingCount.get()));

            if (remainingCount.get() == 0) {
                powerUp.setOnMouseClicked(null);
                return;
            } 

            setCooldown(powerUp);
        });
    }

    private void setCooldown(StackPane powerUp) {
        PauseTransition cooldown = new PauseTransition(Duration.seconds(5 * k_controller.getMultiplier()));
        cooldown.setOnFinished(e -> {
            powerUp.setDisable(false);

            if (powerUp == s_invincibility) k_controller.invincibilityWearOff();
            if (powerUp == s_powerSurge) k_controller.multiplierWearOff();
        });
        
        cooldown.play();
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

    protected Background getTextFill() {
        Color backgroundColor = Color.web("#F3EAD1", 0.8);
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(25), Insets.EMPTY);

        return new Background(backgroundFill);
    }
}
