package algorangers.kenkenrangers.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class GameOverController {
    
    @FXML
    private Pane p_main;

    @FXML
    private Text t_time;

    @FXML
    private HBox h_stars;

    @FXML
    private Text t_bestNum, t_scoreNum;

    @FXML
    private Text t_powerSurge, t_invincibility, t_cellReveal;

    @FXML
    private ImageView i_menu;

    private String name;
    private int powerSurge, invincibility, cellReveal;
    private int powerSurgeUsed, invincibilityUsed, cellRevealUsed;
    private int timeFinished, score, stars;

    private int bestScore = 0;

    @FXML
    private void initialize() throws SQLException {
        
        retrieveGameData();
        setMenuButton();
    }

    private void retrieveGameData() throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        if (!rs.next()) return; 

        name = rs.getString("name");
        powerSurge = rs.getInt("powersurge_initial");
        invincibility = rs.getInt("invincibility_initial");
        cellReveal = rs.getInt("cellreveal_initial");
        powerSurgeUsed = rs.getInt("powersurge_used");
        invincibilityUsed = rs.getInt("invincibility_used");
        cellRevealUsed = rs.getInt("cellreveal_used");
        score = rs.getInt("score");
        timeFinished = rs.getInt("time");
        stars = rs.getInt("stars");

        bestScore = DatabaseManager.retrieveHighScore(name, "bottomless_abyss");
        
        loadRecords();
    }

    private void setMenuButton() {
        i_menu.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    private void loadRecords() {
        t_time.setText(GameUtils.timeToString(timeFinished));

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
        
        t_scoreNum.setText(score > bestScore ? String.valueOf(score) : String.valueOf(bestScore));

    }

}
