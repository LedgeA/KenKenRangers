package algorangers.kenkenrangers.controllers.menu;

import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class MainMenuController {
    
    @FXML
    private Pane p_main;

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
        setTextAppearances();
        setOnClickListeners();
    }

    private void setTextAppearances() {
        GameUtils.setMenuTextAppearance(t_rangersSaga);
        GameUtils.setMenuTextAppearance(t_bottomlessAbyss);
        GameUtils.setMenuTextAppearance(t_customTrial);
        GameUtils.setMenuTextAppearance(t_tutorial);

    }
    
    private void setOnClickListeners() {
        t_rangersSaga.setOnMouseClicked(event -> {
            GameUtils.navigate("select-chapter.fxml", p_main);
        });

        t_bottomlessAbyss.setOnMouseClicked(event -> {
            GameUtils.navigate("bottomless-abyss.fxml", p_main);
        });

        t_customTrial.setOnMouseClicked(event -> {
            GameUtils.navigate("configure-trial.fxml", p_main);
        });

        t_tutorial.setOnMouseClicked(event -> {
            GameUtils.navigate("tutorial.fxml", p_main);
        });

    }
}
