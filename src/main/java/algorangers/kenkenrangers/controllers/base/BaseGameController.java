package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;

import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.*;

public class BaseGameController {
    
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

    protected String name;
    protected int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    protected int characterExists, stars;

    protected boolean paused = false;
    protected boolean gameOver = false, gameWon = true;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

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
        
        // Game Duration Counter and Clear Verifier
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

    protected void gameEnd(boolean cleared) {
        timer.stop();

        if (attackInterval != null) attackInterval.stop();
        
        DatabaseManager.updateEndGameSession(
            name, 
            k_controller.getPowerSurge(), 
            k_controller.getInvincibility(), 
            k_controller.getCellReveal(), 
            cleared ? timeCount : 0, // if the game is cleared, set timeCount as time, else set 0 as time
            computeStars());
            
        GameUtils.navigate("game-over.fxml", p_main); 
    }
    
    protected void powerUpsHandler() {
        setupPowerUp(s_powerSurge, k_controller::consumePowerSurge, k_controller::getPowerSurge);
        setupPowerUp(s_invincibility, k_controller::consumeInvincibility, k_controller::getInvincibility);
        setupPowerUp(s_cellReveal, k_controller::consumeCellReveal, k_controller::getCellReveal);
    }

    private void setupPowerUp(StackPane powerUp, Runnable consume, Supplier<Integer> remainingCount) {
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
                return;
            } 

            setCooldown(powerUp);
        });
    }

    private void setCooldown(StackPane powerUp) {

        // set duration of cooldown to duration times the current multiplier value (modifiable by powerSurge)
        PauseTransition cooldown = new PauseTransition(Duration.seconds(5 * k_controller.getMultiplier()));
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

        // limit meter height if it is too big or too small
        if (currentMeterHeight < 25) {
            currentMeterHeight = 25;
        } else if (currentMeterHeight > 540) {
            currentMeterHeight = 540;
        }

        // update size
        r_gaugeMeter.setHeight(currentMeterHeight);
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

    protected void modifyGridFocus(Parent parent, boolean isFocusable) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            child.setFocusTraversable(isFocusable);

            if (child instanceof Parent) {
                modifyGridFocus((Parent) child, isFocusable);
            }
        }
    }

    protected Background getTextFill() {
        Color backgroundColor = Color.web("#F3EAD1", 0.8);
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(25), Insets.EMPTY);

        return new Background(backgroundFill);
    }
}
