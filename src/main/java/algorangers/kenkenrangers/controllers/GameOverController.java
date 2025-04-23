package algorangers.kenkenrangers.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


public class GameOverController {
    
    @FXML
    private Pane p_main;

    @FXML
    private Text t_time;

    @FXML
    private Text t_gameOver;

    @FXML
    private Rectangle r_gameOver;

    @FXML
    private Rectangle r_board;

    @FXML
    private HBox h_stars;

    @FXML
    private BorderPane b_score, b_best;

    @FXML
    private Text t_score, t_scoreNum;

    @FXML
    private Text t_best, t_bestNum;

    @FXML
    private ImageView i_powerSurge, i_invincibility, i_cellReveal;

    @FXML
    private Text t_powerSurge, t_invincibility, t_cellReveal;

    @FXML
    private Circle c_menu;

    private int powerSurgeUsed, invincibilityUsed, cellRevealUsed, remainingPowerUps;
    private int powerUps, timeFinished, stars;

    @FXML
    private void initialize() {
        
        retrieveGameState();
        setMenuButton();
    }

    private void setMenuButton() {
        c_menu.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    private void retrieveGameState() {
        String sql = "SELECT * FROM active_game";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kenken.db");
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int dimensions = rs.getInt("dimensions");
                int dps = rs.getInt("dps");
                this.powerUps = rs.getInt("power_ups");
                this.timeFinished = rs.getInt("time_finished");
                this.powerSurgeUsed = rs.getInt("power_surge_used");
                this.invincibilityUsed = rs.getInt("invincibility_used");
                this.cellRevealUsed = rs.getInt("cell_reveal_used");
                this.remainingPowerUps = rs.getInt("remaining_power_ups");
                this.stars = rs.getInt("stars");

                System.out.println("Dimensions: " + dimensions);
                System.out.println("DPS: " + dps);
                System.out.println("Power Ups: " + powerUps);
                System.out.println("Time Finished: " + timeFinished);
                System.out.println("Power Surge Used: " + powerSurgeUsed);
                System.out.println("Invincibility Used: " + invincibilityUsed);
                System.out.println("Cell Reveal Used: " + cellRevealUsed);
                System.out.println("Remaining Power Ups: " + remainingPowerUps);
                System.out.println("Stars: " + stars);
                System.out.println("-----");
            }


            loadRecords();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecords() {
        int min = this.timeFinished / 60;
        int sec = this.timeFinished % 60;

        String strMin = String.valueOf(min).length() < 2 ? "0" + String.valueOf(min) : String.valueOf(min);
        String strSec = String.valueOf(sec).length() < 2 ? "0" + String.valueOf(sec) : String.valueOf(sec);
        
        t_time.setText(strMin + ":" + strSec);

        t_powerSurge.setText(String.valueOf(powerSurgeUsed));
        t_invincibility.setText(String.valueOf(powerSurgeUsed));
        t_cellReveal.setText(String.valueOf(powerSurgeUsed));

        Image starImage = new Image(getClass().getResource("/algorangers/kenkenrangers/icons/star.png").toExternalForm());

        for (int i = 0; i < this.stars; i++) {
            ImageView imageView = new ImageView(starImage);

            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            imageView.setPreserveRatio(false);

            h_stars.getChildren().add(imageView);
        }

        int score = (120 - this.timeFinished) * 100 + (1000 * this.remainingPowerUps);
        t_bestNum.setText("0");
        t_scoreNum.setText(String.valueOf(score));

    }

}
