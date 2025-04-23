package algorangers.kenkenrangers.controllers.menu;

import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


public class InitialMainMenuController {
    
    @FXML
    private Pane p_main;

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

    @FXML
    public void initialize() {
        setTextAppearance();
        setOnClickListeners();
       
        // DatabaseManager.updateInitialGameSession("Ledge", 4, 10, 3, 3, 3, 1);
    }

    private void setTextAppearance() {
        changeEachTextAppearance(t_newGame);
        changeEachTextAppearance(t_continue);
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
        t_newGame.setOnMouseClicked(event -> {
            GameUtils.navigate("tutorial.fxml", p_main);
        });

        t_continue.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", p_main);
            
        });

    }

    
}
