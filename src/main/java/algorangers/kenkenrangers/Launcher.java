package algorangers.kenkenrangers;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("view/initial-main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("KenKenRangers");
        stage.setScene(scene);

        stage.setMinHeight(720);
        stage.setMinWidth(1280);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
}