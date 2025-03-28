package algorangers.kenkenrangers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PuzzleController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}