package algorangers.kenkenrangers.controllers.menu;

import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class InitialMainMenuController {
    
    @FXML
    private Pane p_main;

    @FXML
    private Text t_newGame;

    @FXML
    private Text t_continue;

    @FXML
    public void initialize() {
        setTextAppearances();
        setOnClickListeners();
    }

    private void setTextAppearances() {
        GameUtils.setMenuTextAppearance(t_newGame);
        GameUtils.setMenuTextAppearance(t_continue);
    }
    
    private void setOnClickListeners() {
        t_newGame.setOnMouseClicked(event -> {
            GameUtils.navigate("new-game.fxml", p_main);
        });

        t_continue.setOnMouseClicked(event -> {
            GameUtils.navigate("continue.fxml", p_main);
        });

    }

    
}
