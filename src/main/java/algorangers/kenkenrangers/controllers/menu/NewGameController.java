package algorangers.kenkenrangers.controllers.menu;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class NewGameController {
    
    @FXML
    private Pane p_main;

    @FXML
    private TextField tf_name;

    @FXML
    private Text t_proceed;
    
    @FXML
    private Text t_cancel;

    @FXML
    private void initialize() {
        setTextAppearances();
        setOnClickListeners();
    }

    private void setTextAppearances() {
        GameUtils.setMenuTextAppearance(t_proceed);
        GameUtils.setMenuTextAppearance(t_cancel);
    }

    private void setOnClickListeners() {
        tf_name.setOnKeyPressed(event -> {
            if (event.getCode() != KeyCode.ENTER) return;
            
            DatabaseManager.createNewPlayer(tf_name.getText().trim());
            DatabaseManager.updateCurrentPlayer(tf_name.getText());
            GameUtils.navigate("tutorial.fxml", p_main);
            
        });

        t_proceed.setOnMouseClicked(event -> {
            DatabaseManager.createNewPlayer(tf_name.getText().trim());
            DatabaseManager.updateCurrentPlayer(tf_name.getText());
            GameUtils.navigate("tutorial.fxml", p_main);
        });

        t_cancel.setOnMouseClicked(event -> {
            GameUtils.navigate("initial-main-menu.fxml", p_main);
        });
    }

}
