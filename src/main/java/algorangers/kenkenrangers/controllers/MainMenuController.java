package algorangers.kenkenrangers.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuController {
  
    @FXML
    private void handleButtonClick(ActionEvent event) {
        try {
            // Load Scene2.fxml
            StackPane root = FXMLLoader.load(getClass().getResource("/algorangers/kenkenrangers/view/tutorial.fxml"));
            
            // Create a new scene and set it in the stage
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
