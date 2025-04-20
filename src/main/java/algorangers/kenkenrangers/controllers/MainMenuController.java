package algorangers.kenkenrangers.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MainMenuController {
    
    @FXML
    private AnchorPane a_main;

    @FXML
    private Text t_kenken;

    @FXML
    private Text t_rangers;

    @FXML
    private Text t_rangersSaga;

    @FXML
    private Text t_bottomlessAbyss;

    @FXML
    private Text t_customTrial;

    @FXML
    private Text t_tutorial;

    double a_mainHeight, a_mainWidth;

    private final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    public MainMenuController() {

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
        changeEachTextPosition(t_kenken,          454.0 / BASE_WIDTH, 198.0 / BASE_HEIGHT);
        changeEachTextPosition(t_rangers,         422.0 / BASE_WIDTH, 277.0 / BASE_HEIGHT);
        changeEachTextPosition(t_rangersSaga,     412.0 / BASE_WIDTH, 416.0 / BASE_HEIGHT);
        changeEachTextPosition(t_bottomlessAbyss, 358.0 / BASE_WIDTH, 488.0 / BASE_HEIGHT);
        changeEachTextPosition(t_customTrial,     430.0 / BASE_WIDTH, 560.0 / BASE_HEIGHT);
        changeEachTextPosition(t_tutorial,        502.0 / BASE_WIDTH, 632.0 / BASE_HEIGHT);
    }

    private void changeEachTextPosition(Text text, double relX, double relY) {
        text.setLayoutX(relX * a_mainWidth);
        text.setLayoutY(relY * a_mainHeight);
    }
    
    private void bindFontSizes() {
        double titleFontSize = 0.1;
        double optionFontSize = 0.05;

        changeEachFontBinding(t_kenken, titleFontSize);
        changeEachFontBinding(t_rangers, titleFontSize);
        
        changeEachFontBinding(t_rangersSaga, optionFontSize);
        changeEachFontBinding(t_bottomlessAbyss, optionFontSize);
        changeEachFontBinding(t_customTrial, optionFontSize);
        changeEachFontBinding(t_tutorial, optionFontSize);
    }

    private void changeEachFontBinding(Text text, double fontSize) {
        Font textFont = text.getFont();
        double textFontSize = Math.min(a_mainWidth, a_mainHeight) * fontSize; 
    
        text.setFont(Font.font(textFont.getFamily(), textFontSize));
    }

    private void setTextAppearance() {
        changeEachTextAppearance(t_rangersSaga);
        changeEachTextAppearance(t_bottomlessAbyss);
        changeEachTextAppearance(t_customTrial);
        changeEachTextAppearance(t_tutorial);

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
        t_rangersSaga.setOnMouseClicked(event -> {
            System.out.println("1");
        });

        t_bottomlessAbyss.setOnMouseClicked(event -> {
            System.out.println("2");
        });

        t_customTrial.setOnMouseClicked(event -> {
            System.out.println("3");
        });

        t_tutorial.setOnMouseClicked(event -> {
            System.out.println("4");
        });

    }
}
