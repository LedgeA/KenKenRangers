package algorangers.kenkenrangers.controllers;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class InitialMainMenuController {
    
    @FXML
    private AnchorPane a_main;

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
            navigate("tutorial.fxml");
        });

        t_continue.setOnMouseClicked(event -> {
            navigate("main-menu.fxml");
            
        });

    }

    private void navigate(String fxml) {
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
    
}
