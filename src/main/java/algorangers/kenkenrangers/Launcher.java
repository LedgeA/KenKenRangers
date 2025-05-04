package algorangers.kenkenrangers;

import java.io.IOException;
import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Launcher extends Application {

    private static final double BASE_WIDTH = 1280;
    private static final double BASE_HEIGHT = 720;

    @Override
    public void start(Stage stage) throws IOException {
        loadFont(); // load font before loading the stage

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("views/initial-main-menu.fxml"));
        Parent root = fxmlLoader.load();
        
        StackPane wrapper = new StackPane();
        wrapper.getChildren().add(root);

        Scene scene = new Scene(wrapper, BASE_WIDTH, BASE_HEIGHT);

        root.scaleXProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(scene.getWidth() / BASE_WIDTH, scene.getHeight() / BASE_HEIGHT),
                scene.widthProperty(), scene.heightProperty()));
        root.scaleYProperty().bind(root.scaleXProperty());
        
        setStageProperties(stage, scene);
        SoundUtils.preloadSounds(); // load the sounds

        stage.show();

    }
    
    private void loadFont() {
        Font.loadFont(
            getClass().getResourceAsStream(
                "/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), 20);
        Font.loadFont(
            getClass().getResourceAsStream(
                "/algorangers/kenkenrangers/fonts/VT323-Regular.ttf"), 20);
    }

    private void setStageProperties(Stage stage, Scene scene) {
        stage.setTitle("KenKenRangers");
        stage.setScene(scene);
        stage.setMinWidth(BASE_WIDTH);
        stage.setMinHeight(BASE_HEIGHT);

        stage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("F11"));
        stage.setOnCloseRequest(event -> {
            try {
                DatabaseManager.resetGameSession();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        stage.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
            if (isMaximized) {
                stage.setFullScreen(true);
            } else {
                stage.setFullScreen(false);
            }
        });

        stage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch();
    }
}