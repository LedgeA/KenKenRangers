package algorangers.kenkenrangers.controllers.game_modes;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.controllers.base.BaseStoryController;
import algorangers.kenkenrangers.controllers.base.KenkenController;
import algorangers.kenkenrangers.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class BottomlessAbyssController extends BaseStoryController {
    
    @FXML
    private TextFlow tf_dialogue;

    @FXML
    private Text t_dialogue;
    
    protected final String[] dialogues = {

    };
    
    @FXML
    protected void initialize() throws SQLException {
        retrieveGameData();

        k_controller = new KenkenController(DIMENSION, dps, powerSurge, invincibility, cellReveal);
        k_view = k_controller.getK_view();

        setTextFlowContent();
        disableGridFocus(k_view);
        powerUpsHandler();

        // Add k_view just below the top most component 
        p_main.getChildren().add(p_main.getChildren().size() - 1, k_view);
    }

    protected void retrieveGameData() throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        while (rs.next()) {
            this.name = rs.getString("player_name");
            this.dps = rs.getInt("dps");

            this.powerSurge = rs.getInt("powersurge_initial");
            this.invincibility = rs.getInt("invincibility_initial");
            this.cellReveal = rs.getInt("cellreveal_initial");

            this.powerSurgeUsed = rs.getInt("powersurge_used");
            this.invincibilityUsed = rs.getInt("invincibility_used");
            this.cellRevealUsed = rs.getInt("cellreveal_used");

            this.characterExists = rs.getInt("character_exists");
            this.stars = rs.getInt("stars");
            
        }
    }

    private void setTextFlowContent() {

        t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

        Color backgroundColor = Color.web("#F3EAD1", 0.8);
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(25), Insets.EMPTY);
        tf_dialogue.setBackground(new Background(backgroundFill));

        p_main.setOnMouseClicked(event -> {
            DIALOGUE_COUNT++;

            if (DIALOGUE_COUNT == 12) {
                startTimer();
                enableGridFocus(k_view);
            };
            if (DIALOGUE_COUNT >= dialogues.length) {
                p_main.setOnMouseClicked(null);
    
                return;
            }

            t_dialogue.setText(dialogues[DIALOGUE_COUNT]);

        });
    }

    private void disableGridFocus(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            child.setFocusTraversable(false);

            if (child instanceof Parent) {
                disableGridFocus((Parent) child);
            }
        }
    }

    private void enableGridFocus(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            child.setFocusTraversable(true);

            if (child instanceof Parent) {
                enableGridFocus((Parent) child);
            }
        }
    }

}
