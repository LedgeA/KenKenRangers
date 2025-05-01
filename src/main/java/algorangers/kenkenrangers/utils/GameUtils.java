package algorangers.kenkenrangers.utils;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameUtils {

    private static final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    public static void setMenuTextAppearance(Text text) {
        text.setOnMouseEntered(event -> {
            text.setFill(Color.YELLOW);
        });

        text.setOnMouseExited(event -> {
            text.setFill(Color.WHITE);
        });
        
    }

    public static String timeToString(int time) {
        int min = time / 60;
        int sec = time % 60;

        return (min < 10 ? "0" : "") + String.valueOf(min) + ":"
             + (sec < 10 ? "0" : "") + String.valueOf(sec);
    }

    public static void setGridFocusable(Parent parent, boolean isFocusable) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            child.setFocusTraversable(isFocusable);

            if (child instanceof Parent) {
                setGridFocusable((Parent) child, isFocusable);
            }
        }
    }

    public static void setComponentsClickable(Node[] nodes, boolean isClickable) {
        for (Node node : nodes) {
            node.setMouseTransparent(!isClickable);
        }
    }

    public static void navigate(String fxml, Pane p_main) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                GameUtils.class.getResource("/algorangers/kenkenrangers/views/" + fxml));

            Stage stage = (Stage) p_main.getScene().getWindow();
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
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
