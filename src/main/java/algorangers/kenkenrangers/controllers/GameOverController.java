package algorangers.kenkenrangers.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameOverController {
    
    @FXML
    private AnchorPane a_main;

    @FXML
    private Text t_time;

    @FXML
    private Text t_gameOver;

    @FXML
    private Rectangle r_gameOver;

    @FXML
    private Rectangle r_board;

    @FXML
    private BorderPane b_score, b_best;

    @FXML
    private Text t_score, t_scoreNum;

    @FXML
    private Text t_best, t_bestNum;

    @FXML
    private ImageView i_powerSurge, i_invincibility, i_cellReveal;

    @FXML
    private Circle c_menu, c_play;

    private double a_mainHeight, a_mainWidth;

    private int BASE_WIDTH, BASE_HEIGHT;

    @FXML
    private void initialize() {
        setBindings();
    }

    private void setBindings() {
        a_main.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            a_mainHeight = newValue.getHeight();
            a_mainWidth = newValue.getWidth();

            if (a_mainHeight > 0) {
               
            }
        });
    }
}
