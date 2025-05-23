package algorangers.kenkenrangers.controllers;

import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.database.DatabaseManager.GameSession;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class GameOverController {
    
    @FXML
    private Pane p_main;

    @FXML
    private Text t_time;

    @FXML
    private HBox h_stars;

    @FXML 
    private VBox v_score;

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
    private String gameMode;

    private int bestScore = 0;

    @FXML
    private void initialize() throws SQLException {
        
        retrieveGameData();
        setMenuButton();
    }

    private void setMenuButton() {
        i_menu.setOnMouseClicked(event -> {
            SoundUtils.press();
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    // retrieve all data first
    private void retrieveGameData() throws SQLException {
        GameSession gameSession = DatabaseManager.retrieveGameSession();

        name = gameSession.name();
        gameMode = gameSession.gameMode();
        powerSurge = gameSession.init_ps();
        invincibility = gameSession.init_i();
        cellReveal = gameSession.init_cr();
        powerSurgeUsed = gameSession.rem_ps();
        invincibilityUsed = gameSession.rem_i();
        cellRevealUsed = gameSession.rem_cr();
        score = gameSession.score();
        timeFinished = gameSession.time();
        stars = gameSession.stars();
        

        if (gameMode.equals("tutorial") || gameMode.equals("custom_trial")) {
            v_score.getChildren().remove(1); // remove "Best Score"
        } else {
            bestScore = DatabaseManager.retrieveHighScore(name, gameMode); 
        }

        if (score > bestScore) {
            bestScore = score;
            DatabaseManager.updateHighscore(name, gameMode, score);
        }

        // when game mode is not tutorial or custom_trial
        if (score != 0) {
            int lastFinishedChapter = DatabaseManager.retrieveLatestFinishedChapter(name);
            int currentChapter = chapterToInt(gameMode);

            if (currentChapter > lastFinishedChapter) {
                DatabaseManager.updateLatestFinishedChapter(name, currentChapter);
            }
        }
        
        loadRecords();
        DatabaseManager.resetGameSession();
    }

    // update time, scores, and stars
    private void loadRecords() {
        // update texts
        t_time.setText(GameUtils.timeToString(timeFinished));

        t_powerSurge.setText(String.valueOf(powerSurgeUsed + "/" + powerSurge));
        t_invincibility.setText(String.valueOf(invincibilityUsed) + "/" + invincibility);
        t_cellReveal.setText(String.valueOf(cellRevealUsed) + "/" + cellReveal);

        Image starImage = new Image(getClass().getResource("/algorangers/kenkenrangers/icons/star.png").toExternalForm());

        // load images
        for (int i = 0; i < this.stars; i++) {
            ImageView imageView = new ImageView(starImage);

            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            imageView.setPreserveRatio(false);

            h_stars.getChildren().add(imageView);
        }

        t_scoreNum.setText(String.valueOf(score));
        t_bestNum.setText(String.valueOf(bestScore));

    }

    private int chapterToInt(String chapter) {
        return switch (chapter) {
            case "chap_1" -> 1;
            case "chap_2" -> 2;
            case "chap_3" -> 3;
            case "chap_4" -> 4;
            case "chap_5" -> 5;
            case "chap_6" -> 6;
            default -> 6;
        };

    }
    

}
