package algorangers.kenkenrangers.controllers.menu;

import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
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
        setEscapePressed();
    }

    private void setTextAppearances() {
        GameUtils.setMenuTextAppearance(t_rangersSaga);
        GameUtils.setMenuTextAppearance(t_bottomlessAbyss);
        GameUtils.setMenuTextAppearance(t_customTrial);
        GameUtils.setMenuTextAppearance(t_tutorial);

    }

    private void setEscapePressed() {
        p_main.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) return; 

            newScene.setOnKeyPressed(event -> {
                if (event.getCode() != KeyCode.ESCAPE) return;
                SoundUtils.press();
                GameUtils.navigate("continue.fxml", p_main);
            });
        });
    }

    
    private void setOnClickListeners() {
        t_rangersSaga.setOnMouseClicked(event -> {
            SoundUtils.press();
            GameUtils.navigate("select-chapter.fxml", p_main);
        });

        t_bottomlessAbyss.setOnMouseClicked(event -> {
            SoundUtils.press();
            GameUtils.navigate("bottomless-abyss.fxml", p_main);
        });

        t_customTrial.setOnMouseClicked(event -> {
            SoundUtils.press();
            GameUtils.navigate("configure-trial.fxml", p_main);
        });

        t_tutorial.setOnMouseClicked(event -> {
            SoundUtils.press();
            GameUtils.navigate("tutorial.fxml", p_main);
        });

    }
}
