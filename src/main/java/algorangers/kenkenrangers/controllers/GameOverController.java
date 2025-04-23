package algorangers.kenkenrangers.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
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

    private String name;
    private int DIMENSION, dps;
    private int powerSurge, invincibility, cellReveal;
    private int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    private int timeFinished, stars;

    @FXML
    private void initialize() throws SQLException {
        
        retrievePlayerData(name);
        retrieveGameData();
        setMenuButton();
    }

    private void retrieveGameData() throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        if (!rs.next()) {
            System.err.println("No game session found.");
            return; 
        }

        name = rs.getString("player_name");
        DIMENSION = rs.getInt("dimension");
        dps = rs.getInt("dps");
        powerSurge = rs.getInt("powersurge_initial");
        invincibility = rs.getInt("invincibility_initial");
        cellReveal = rs.getInt("cellreveal_initial");
        powerSurgeUsed = rs.getInt("powersurge_used");
        invincibilityUsed = rs.getInt("invincibility_used");
        cellRevealUsed = rs.getInt("cellreveal_used");
        timeFinished = rs.getInt("time");
        stars = rs.getInt("stars");

        loadRecords();
    }

    private void retrievePlayerData(String name) {

    }

    private void setMenuButton() {
        c_menu.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    private void loadRecords() {
        int min = timeFinished / 60;
        int sec = timeFinished % 60;

        String strMin = String.valueOf(min).length() < 2 ? "0" + String.valueOf(min) : String.valueOf(min);
        String strSec = String.valueOf(sec).length() < 2 ? "0" + String.valueOf(sec) : String.valueOf(sec);
        
        t_time.setText(strMin + ":" + strSec);

        t_powerSurge.setText(String.valueOf(powerSurgeUsed + "/" + powerSurge));
        t_invincibility.setText(String.valueOf(invincibilityUsed) + "/" + invincibility);
        t_cellReveal.setText(String.valueOf(cellRevealUsed) + "/" + cellReveal);

        Image starImage = new Image(getClass().getResource("/algorangers/kenkenrangers/icons/star.png").toExternalForm());

        for (int i = 0; i < this.stars; i++) {
            ImageView imageView = new ImageView(starImage);

            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            imageView.setPreserveRatio(false);

            h_stars.getChildren().add(imageView);
        }

        int initialPowerUps = powerSurge + invincibility + cellReveal;
        int remainingPowerUps = initialPowerUps - (powerSurgeUsed + invincibilityUsed + cellRevealUsed);

        int score = (120 - timeFinished) * 1000 * DIMENSION / dps + (1000 * remainingPowerUps);
        t_bestNum.setText("0");
        t_scoreNum.setText(String.valueOf(score));

    }

}
