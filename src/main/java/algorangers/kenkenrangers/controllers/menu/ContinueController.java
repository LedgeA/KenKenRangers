package algorangers.kenkenrangers.controllers.menu;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
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
    private Text t_cancel;

    @FXML
    private void initialize() {
        setTextAppearances();
        setOnClickListeners();

        try {
            showPlayers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setTextAppearances() {
        GameUtils.setMenuTextAppearance(t_cancel);
    }

    private void setOnClickListeners() {
        t_cancel.setOnMouseClicked(event -> {
            GameUtils.navigate("initial-main-menu.fxml", p_main);
        });
    }

    private void showPlayers() throws SQLException{
        Image fieldImage = new Image(getClass().getResource("/algorangers/kenkenrangers/icons/field.png").toExternalForm());
        Font defaultFont = Font.loadFont(getClass().getResourceAsStream("/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), 20);
        
        ResultSet rs = DatabaseManager.getListOfPlayers();

        while (rs.next()) {

            String name = rs.getString("name");
            Text text = new Text(name);
            text.setFont(defaultFont);

            ImageView fieldView = new ImageView(fieldImage);
            fieldView.setFitWidth(436);  
            fieldView.setPreserveRatio(true);

            StackPane stackPane = new StackPane(fieldView, text);
            stackPane.setPrefWidth(436);  
            stackPane.setOnMouseClicked(event -> {
                DatabaseManager.updateGameSession(name);
                GameUtils.navigate("main-menu.fxml", p_main);
            });

            v_players.getChildren().add(stackPane);
        }

        rs.close();
    }
}
