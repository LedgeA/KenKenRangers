package algorangers.kenkenrangers.controllers.menu;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainMenuController {
    
    @FXML
    private Pane p_main;

    @FXML
    private ImageView background;

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

    @FXML
    public void initialize() {
        setTextAppearance();
        setOnClickListeners();
    }

    private void setTextAppearance() {
        changeEachTextAppearance(t_rangersSaga);
        changeEachTextAppearance(t_bottomlessAbyss);
        changeEachTextAppearance(t_customTrial);
        changeEachTextAppearance(t_tutorial);

    }

    private void changeEachTextAppearance(Text text) {
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
