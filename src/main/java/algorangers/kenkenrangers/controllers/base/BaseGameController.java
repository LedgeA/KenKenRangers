package algorangers.kenkenrangers.controllers.base;

import javafx.util.Duration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import algorangers.kenkenrangers.utils.*;;

public class BaseGameController {
    
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
    protected int multiplier = 1;
    protected int counter = 0;
    
    protected KenkenController k_controller;

    protected GridPane k_view;
    
    // Default values
    protected int DIMENSION = 4;
    protected int hp = 100, dps = 10;
    protected int powerSurge = 3, invincibility = 3, cellReveal = 3;

    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    protected void startTimer() {
        Timeline timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            counter++;

            if (k_controller.getRemainingCageAmount() == 0) {
                timer.stop();

                int powerUps = powerSurge + invincibility + cellReveal;

                saveGameState(DIMENSION, dps, powerUps);
                GameUtils.navigate("game-over.fxml", p_main);
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        

        attackInterval = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (!k_controller.getInvincibleState()) {
                k_controller.decreaseHp(); 
                updateGaugeMeter();            
                System.out.println(k_controller.getHp());
            }

            if (k_controller.getHp() <= 0) {
                attackInterval.stop();
                GameUtils.navigate("game-over.fxml", p_main);   
                
                int powerUps = powerSurge + invincibility + cellReveal;
                saveGameState(DIMENSION, dps, powerUps);   
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

    protected void saveGameState(int DIMENSION, int dps, int powerUps) {
        String sql = "UPDATE active_game SET " +
                "dimensions = ?, " +
                "dps = ?, " +
                "power_ups = ?, " +
                "time_finished = ?, " +
                "power_surge_used = ?, " +
                "invincibility_used = ?, " +
                "cell_reveal_used = ?, " +
                "remaining_power_ups = ?, " +
                "stars = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kenken.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, DIMENSION);
            pstmt.setInt(2, dps);
            pstmt.setInt(3, powerUps);
            pstmt.setInt(4, counter);
            pstmt.setInt(5, k_controller.getPowerSurge());
            pstmt.setInt(6, k_controller.getInvincibility());
            pstmt.setInt(7, k_controller.getCellReveal());
            pstmt.setInt(8, k_controller.getPowerSurge() + k_controller.getInvincibility() + k_controller.getCellReveal());
            pstmt.setInt(9, computeStars());

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    protected int computeStars() {
        int stars = 1;

        if (k_controller.getPowerSurge() == 0 && k_controller.getInvincibility() == 0 && k_controller.getCellReveal() == 0) {
            stars++;
        }

        if (counter <= 120) {
            stars++;
        }

        return stars;
    }

}
