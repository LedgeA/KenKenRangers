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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Launcher extends Application {

    private static final double BASE_WIDTH = 1280;
    private static final double BASE_HEIGHT = 720;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("views/initial-main-menu.fxml"));
        Parent root = fxmlLoader.load();

        StackPane wrapper = new StackPane();
        wrapper.getChildren().add(root);

        Scene scene = new Scene(wrapper, BASE_WIDTH, BASE_HEIGHT);
        
        root.scaleXProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(scene.getWidth() / BASE_WIDTH, scene.getHeight() / BASE_HEIGHT),
                scene.widthProperty(), scene.heightProperty()));
        root.scaleYProperty().bind(root.scaleXProperty());

        stage.setTitle("KenKenRangers");
        stage.setScene(scene);
        stage.setMinWidth(BASE_WIDTH);
        stage.setMinHeight(BASE_HEIGHT);
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
        
        SoundUtils.preloadSounds();
        
        stage.setFullScreen(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}