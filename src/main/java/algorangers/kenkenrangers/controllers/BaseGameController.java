package algorangers.kenkenrangers.controllers;

import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import algorangers.kenkenrangers.utils.*;;

public class BaseGameController {
    
    @FXML
    protected AnchorPane a_main;

    @FXML
    protected StackPane s_gauge;

    @FXML
    protected Rectangle r_gaugeBase, r_gaugeMeter;

    @FXML
    protected AnchorPane a_powerUps;

    @FXML
    protected StackPane s_powerUps;
    
    @FXML
    protected Rectangle r_powerUps;

    @FXML
    protected ImageView i_powerSurge, i_invincibility, i_cellReveal;

    @FXML
    protected ImageView i_character;

    @FXML
    protected TextFlow tf_dialogue;

    @FXML
    protected Text t_dialogue;

    protected double a_mainWidth, a_mainHeight;

    protected int DIALOGUE_COUNT = 0;

    protected Timeline attackInterval;
    protected int multiplier = 1;
    protected int counter = 0;

    protected final String[] dialogues = {
        "Hello Ranger! Welcome to KENKEN RANGERS",
        "Your mission is to fill the grid with numbers, just like Sudoku. But there's a twist!",
        "The grid is divided into color-coded cages.",
        "Your goal is to use the numbers inside the cage to match the target number using the given math operation.",
        "+ and × deals damage to the enemy. - and ÷ reduces the damage you receive.",
        "Numbers must NOT repeat in anyrow or column.",
        "The numbers you use depend on the grid size:\n- For a 3×3 grid, use only\nnumbers 1 to 3.\n- For a 4×4 grid, use numbers 1 to 4\n... and so on!",
        "Take too long in solving the Kenken Puzzle and the enemy will beat you.",
        "To make things exciting, we've added special power-ups!",
        "Invincibility makes you invincible for a set amount of\ntime.",
        "Damage Multiplier doubles your damage for a set amount of\ntime.",
        "3-Cell Reveal reveals 3 of your unsolved tiles.",
        "Let's practice! Tap on a cell to enter a number.",
        "Use your skills, think strategically, and become a true KenKen master!",
        "Good luck, and may the numbers be ever in your favor!"
    };
    
    protected KenkenController k_controller;

    protected GridPane k_view;


    protected final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    @FXML
    protected void initialize() {
        k_controller = new KenkenController(4);
        k_view = k_controller.getK_view();

        setBindings();
        
        // Add k_view just below the top most component
        a_main.getChildren().add(a_main.getChildren().size() - 1, k_view);
    }

    protected void startTimer() {
        Timeline timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            counter++;

            if (k_controller.getRemainingCageAmount() == 0) {
                timer.stop();
                navigate("game-over.fxml");
                saveGameState();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        

        attackInterval = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (!k_controller.getInvincibleState()) {
                k_controller.decreaseHp(); 
                bindGaugeSize();             // Update Gauge Size
                System.out.println(k_controller.getHp());
            }

            if (k_controller.getHp() <= 0) {
                attackInterval.stop();
                navigate("game-over.fxml");     
                saveGameState();    
            }
        }));
        
        attackInterval.setCycleCount(Timeline.INDEFINITE); 
        attackInterval.play();
        
        
    }

    protected void setTextFlowContent() {

        t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

        a_main.setOnMouseClicked(event -> {
            DIALOGUE_COUNT++;

            if (DIALOGUE_COUNT >= dialogues.length) {
                a_main.setOnMouseClicked(null);
                return;
            }

            t_dialogue.setText(dialogues[DIALOGUE_COUNT]);
        });
    }

    protected void setBindings() {
        a_main.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            a_mainHeight = newValue.getHeight();
            a_mainWidth = newValue.getWidth();

            if (a_mainHeight > 0 && a_mainWidth > 0) {

                bindKenKenProperties();
            }
        });
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
    
    protected void bindKenKenProperties() {
        double k_viewWidth = 500.0 / BASE_WIDTH * a_mainWidth;
        double k_viewHeight = 500.0 / BASE_HEIGHT * a_mainHeight;

        k_view.setPrefWidth(k_viewWidth);
        k_view.setPrefHeight(k_viewHeight);

        double k_viewRelX = 384.0 / BASE_WIDTH * a_mainWidth;
        double k_viewRelY = 91.0 / BASE_HEIGHT * a_mainHeight;

        k_view.setLayoutX(k_viewRelX);
        k_view.setLayoutY(k_viewRelY);


    }

    protected void bindDialogueProperties() {
        
        double tf_dialogueWidth = 280.0 / BASE_WIDTH * a_mainWidth;
        tf_dialogue.setPrefWidth(tf_dialogueWidth);

        double tf_dialogueRelX = 840.0 / 1280 * a_mainWidth;
        double tf_dialogueRelY = 380.0 / 720 * a_mainHeight;

        tf_dialogue.setLayoutX(tf_dialogueRelX);
        tf_dialogue.setLayoutY(tf_dialogueRelY);

        // Change Padding
        double tf_dialogueVertical = 10.0 / BASE_HEIGHT * a_mainHeight;
        double tf_dialogueHorizontal = 10.0 / BASE_WIDTH * a_mainWidth;
        tf_dialogue.setPadding(
            new Insets(tf_dialogueVertical, tf_dialogueHorizontal, 
                       tf_dialogueVertical, tf_dialogueHorizontal));
        
        // Change Font Size
        Font textFont = t_dialogue.getFont();
        double fontSize = Math.min(a_mainWidth, a_mainHeight) * 0.03;
        t_dialogue.setFont(Font.font(textFont.getFamily(), fontSize));

        // Change Radius and Background
        double tf_dialogueRadius = 20.0 / BASE_HEIGHT * a_mainHeight;
        double tf_dialogueBorderWidth = 2.0 / BASE_HEIGHT * a_mainHeight;
        tf_dialogue.setStyle(
            "-fx-border-radius: " + tf_dialogueRadius + "; " + 
            "-fx-border-width: " + tf_dialogueBorderWidth + "; " + 
            "-fx-background-radius: " + tf_dialogueRadius + "; " + 
            "-fx-background-color: #FFFFFF; " + 
            "-fx-border-color: #000000");
    }

    protected void bindCharacterSize() {
        GameUtils.bindSize(i_character, a_main, 230, 300);

    }

    protected void bindCharacterPosition() {
        double i_characterRelX = 990.0 / BASE_WIDTH;
        double i_characterRelY = 380.0 / BASE_HEIGHT;

        double tf_dialogueRelX = 824.0 / BASE_WIDTH;
        double tf_dialogueRelY = 369.0 / BASE_HEIGHT;

        changeEachPosition(i_character, i_characterRelX, i_characterRelY);
        changeEachPosition(tf_dialogue, tf_dialogueRelX, tf_dialogueRelY);

    }

    protected void bindPowerUpsSize() {
        GameUtils.bindSize(s_powerUps, a_main, 80, 230);
        GameUtils.bindSize(r_powerUps, a_main, 80, 230);
        GameUtils.bindSize(a_powerUps, a_main, 80, 230);

        GameUtils.bindSize(i_powerSurge, a_main, 70, 70);
        GameUtils.bindSize(i_invincibility, a_main, 70, 70);
        GameUtils.bindSize(i_cellReveal, a_main, 70, 70);
    }

    protected void bindPowerUpsPosition() {
        double s_powerUpsRelX = 933.0 / BASE_WIDTH;
        double s_powerUpsRelY = 22.0 / BASE_HEIGHT;

        changeEachPosition(s_powerUps, s_powerUpsRelX, s_powerUpsRelY);

        double i_powerUpsRelX = 5.0 / BASE_WIDTH;

        double i_powerSurgeRelY = 14.0 / BASE_HEIGHT;
        double i_invincibilityRelY = 80.0 / BASE_HEIGHT;
        double i_cellRevealRelY = 150.0 / BASE_HEIGHT;
        
        changeEachPosition(i_powerSurge, i_powerUpsRelX, i_powerSurgeRelY);
        changeEachPosition(i_invincibility, i_powerUpsRelX, i_invincibilityRelY);
        changeEachPosition(i_cellReveal, i_powerUpsRelX, i_cellRevealRelY);
    }

    protected void bindGaugeSize() {
        double hpPercentage = k_controller.getHp() / 100.0;

        GameUtils.bindSize(s_gauge, a_main, 200, 150);
        GameUtils.bindSize(r_gaugeBase, a_main, 64, 540);
        GameUtils.bindSize(r_gaugeMeter, a_main, 64, 540 * hpPercentage);
    }

    protected void bindGaugePosition() {
        double s_gaugeRelX = 0.0;
        double s_gaugeRelY = 71.0 / BASE_HEIGHT;

        changeEachPosition(s_gauge, s_gaugeRelX, s_gaugeRelY);
    }

    protected void changeEachPosition(StackPane stackPane, double relX, double relY) {
        stackPane.setLayoutX(relX * a_mainWidth);
        stackPane.setLayoutY(relY * a_mainHeight);
    }

    protected void changeEachPosition(ImageView imageView, double relX, double relY) {
        imageView.setLayoutX(relX * a_mainWidth);
        imageView.setLayoutY(relY * a_mainHeight);
    }

    protected void changeEachPosition(TextFlow textFlow, double relX, double relY) {
        textFlow.setLayoutX(relX * a_mainWidth);
        textFlow.setLayoutY(relY * a_mainHeight);
    }

    protected void navigate(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/algorangers/kenkenrangers/view/" + fxml));

            Stage stage = (Stage) a_main.getScene().getWindow();
            Parent root = fxmlLoader.load();

            StackPane wrapper = new StackPane();
            wrapper.getChildren().add(root);

            Scene scene = new Scene(wrapper, BASE_WIDTH, BASE_HEIGHT);

            root.scaleXProperty().bind(Bindings.createDoubleBinding(() ->
                    Math.min(scene.getWidth() / BASE_WIDTH, scene.getHeight() / BASE_HEIGHT),
                    scene.widthProperty(), scene.heightProperty()));
            root.scaleYProperty().bind(root.scaleXProperty());

            stage.setTitle("KenKenRangers");
            stage.setScene(scene);
            stage.setMinWidth(BASE_WIDTH);
            stage.setMinHeight(BASE_HEIGHT);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void saveGameState() {
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

            pstmt.setInt(1, 4);
            pstmt.setInt(2, 10);
            pstmt.setInt(3, 9);
            pstmt.setInt(4, counter);
            pstmt.setInt(5, k_controller.getPowerSurge());
            pstmt.setInt(6, k_controller.getInvincibility());
            pstmt.setInt(7, k_controller.getCellReveal());
            pstmt.setInt(8, k_controller.getPowerSurge() + k_controller.getInvincibility() + k_controller.getCellReveal());
            pstmt.setInt(9, computeStars());

            System.out.println(k_controller.getPowerSurge());
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
