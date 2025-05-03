package algorangers.kenkenrangers.controllers.menu;

import java.sql.SQLException;
import java.util.List;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ContinueController {
    
    @FXML
    private Pane p_main;

    @FXML
    private VBox v_players;

    @FXML
    private Text t_removePlayer, t_goBack, t_remove, t_cancel;
    
    private List<String> names;
    private boolean removePlayers;


    @FXML
    private void initialize() throws SQLException {
        setTextAppearances();
        setOnClickListeners();

        names = DatabaseManager.getListOfPlayers();
        showPlayers();
    }

    private void setTextAppearances() {
        GameUtils.setMenuTextAppearance(t_removePlayer);
        GameUtils.setMenuTextAppearance(t_goBack);
        GameUtils.setMenuTextAppearance(t_remove);
        GameUtils.setMenuTextAppearance(t_cancel);
    }

    private void setOnClickListeners() {
        t_goBack.setOnMouseClicked(event -> {
            SoundUtils.playPress();
            GameUtils.navigate("initial-main-menu.fxml", p_main);
        });

        t_removePlayer.setOnMouseClicked(event -> {
            removePlayers = true;
            t_removePlayer.setVisible(false);
            t_remove.setVisible(true);
            t_cancel.setVisible(true);
            SoundUtils.playPress();
        });

        t_remove.setOnMouseClicked(event -> {
            for (int i = v_players.getChildren().size() - 1; i >= 0; i--) {
                StackPane stackPane = (StackPane) v_players.getChildren().get(i);
                ImageView fieldView = (ImageView) stackPane.getChildren().get(0);
            
                if (fieldView.getEffect() == null) continue;
                
                DatabaseManager.removePlayer(names.get(i));
                v_players.getChildren().remove(i);
                names.remove(i);
            }
            SoundUtils.playPress();
            
        });
        t_cancel.setOnMouseClicked(event -> {
            removePlayers = false;
            t_removePlayer.setVisible(true);
            t_remove.setVisible(false);
            t_cancel.setVisible(false);
            SoundUtils.playPress();
        });


    }

    private void showPlayers() throws SQLException {
        Image fieldImage = new Image(getClass().getResource("/algorangers/kenkenrangers/icons/field.png").toExternalForm());
        Font defaultFont = Font.loadFont(getClass().getResourceAsStream("/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), 20);
    

        for (String name : names) {
            Text text = new Text(name);
            text.setFont(defaultFont);

            ImageView fieldView = new ImageView(fieldImage);
            fieldView.setFitWidth(436);  
            fieldView.setPreserveRatio(true);

            StackPane stackPane = new StackPane(fieldView, text);
            stackPane.setPrefWidth(436);  
            stackPane.setOnMouseClicked(event -> {
                SoundUtils.playPress();
                if (removePlayers) {
                    if (fieldView.getEffect() == null) {
                        fieldView.setEffect(new ColorAdjust(0, 0, -0.5, 0));
                    } else {
                        fieldView.setEffect(null);
                    }
                     return;

                }

                DatabaseManager.updateCurrentPlayer(name);
                GameUtils.navigate("main-menu.fxml", p_main);
            });

            v_players.getChildren().add(stackPane);
        }

    }
}
