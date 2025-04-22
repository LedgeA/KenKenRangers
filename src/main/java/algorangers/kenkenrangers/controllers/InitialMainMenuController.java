package algorangers.kenkenrangers.controllers;

import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class InitialMainMenuController {
    
    @FXML
    private AnchorPane a_main;

    @FXML
    private ImageView background;

    @FXML
    private Text t_kenken;

    @FXML
    private Text t_rangers;

    @FXML
    private Text t_newGame;

    @FXML
    private Text t_continue;

    private double a_mainHeight, a_mainWidth;

    private final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    public InitialMainMenuController() {

    }

    @FXML
    public void initialize() {
        setTextAppearance();
        setOnClickListeners();
       
        setBindings();
    }

    private void setBindings() {
        a_main.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            a_mainHeight = newValue.getHeight();
            a_mainWidth = newValue.getWidth();

            if (a_mainHeight > 0) {
                bindFontSizes();
                bindTextPositions();
            }
        });
    }

    private void bindTextPositions() {
        changeEachTextPosition(t_kenken, 448.0 / BASE_WIDTH, 198.0 / BASE_HEIGHT);
        changeEachTextPosition(t_rangers, 416.0 / BASE_WIDTH, 277.0 / BASE_HEIGHT);

        changeEachTextPosition(t_newGame, 496.0 / BASE_WIDTH, 468.0 / BASE_HEIGHT);
        changeEachTextPosition(t_continue, 496.0 / BASE_WIDTH, 542.0 / BASE_HEIGHT);
    }
    
    private void changeEachTextPosition(Text text, double relX, double relY) {
        text.setLayoutX(relX * a_mainWidth);
        text.setLayoutY(relY * a_mainHeight);
    }

    private void bindFontSizes() {
        double titleFontSize = 0.1;
        double optionFontSize = 0.05;

        changeEachFontSize(t_kenken, titleFontSize);
        changeEachFontSize(t_rangers, titleFontSize);

        changeEachFontSize(t_newGame, optionFontSize);
        changeEachFontSize(t_continue, optionFontSize);
    }

    private void changeEachFontSize(Text text, double fontSize) {
        Font textFont = text.getFont();
        double textFontSize = Math.min(a_mainWidth, a_mainHeight) * fontSize; 
    
        text.setFont(Font.font(textFont.getFamily(), textFontSize));
    } 

    private void setTextAppearance() {
        changeEachTextAppearance(t_newGame);
        changeEachTextAppearance(t_continue);
    }

    private void changeEachTextAppearance(Text text) {
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(3);

        text.setOnMouseEntered(event -> {
            text.setFill(Color.YELLOW);
        });

        text.setOnMouseExited(event -> {
            text.setFill(Color.WHITE);
        });

    }
    
    private void setOnClickListeners() {
        t_newGame.setOnMouseClicked(event -> {
            GameUtils.navigate("tutorial.fxml", a_main);
        });

        t_continue.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", a_main);
            
        });

    }

    
}
