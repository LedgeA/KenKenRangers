package algorangers.kenkenrangers.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameUtils {

    private static final int BASE_WIDTH = 1280, BASE_HEIGHT = 720;

    public static void bindPosition(Node node, Region region, double baseX, double baseY) {
        node.layoutXProperty().bind(region.widthProperty().multiply(baseX / BASE_WIDTH));
        node.layoutYProperty().bind(region.heightProperty().multiply(baseY / BASE_HEIGHT));
    }

    public static void bindSize(Node node, Region region, double nodeBaseWidth, double nodeBaseHeight) {
        if (node instanceof Rectangle rectangleRef) {
            rectangleRef.widthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            rectangleRef.heightProperty().bind(region.heightProperty().multiply(nodeBaseHeight / BASE_HEIGHT));

        } else if (node instanceof Circle circle) {
            double radiusRatio = (nodeBaseWidth + nodeBaseHeight) / (BASE_WIDTH + BASE_HEIGHT);
            circle.radiusProperty().bind(region.widthProperty().add(region.heightProperty()).divide(2).multiply(radiusRatio));

        } else if (node instanceof Region regionRef) {
            regionRef.prefWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            regionRef.prefHeightProperty().bind(region.heightProperty().multiply(nodeBaseHeight / BASE_HEIGHT));

        } else if (node instanceof ImageView imageViewRef) {
            imageViewRef.fitWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            imageViewRef.fitHeightProperty().bind(region.heightProperty().multiply(nodeBaseHeight / BASE_HEIGHT));

        } else {
            System.out.println("| OUT OF SCOPE | Node is instance of: " + node.getClass().getSimpleName());
        }
    }

    public static void bindSize(Node node, Region region, double nodeBaseWidth) {
        if (node instanceof Rectangle rectangleRef) {
            rectangleRef.widthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));

        } else if (node instanceof Region regionRef) {
            regionRef.prefWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));

        } else if (node instanceof ImageView imageViewRef) {
            imageViewRef.fitWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            
        } else {
            System.out.println("| OUT OF SCOPE | Node is instance of: " + node.getClass().getSimpleName());
        }
    }

    public static void bindFontSize(Text text, Region region, double baseFontSize) {
        DoubleBinding fontSizeBinding = region.heightProperty().multiply(baseFontSize / BASE_HEIGHT);

        fontSizeBinding.addListener((obs, oldVal, newVal) -> {
            Font font = text.getFont();
            text.setFont(Font.font(font.getFamily(), newVal.doubleValue()));
        });
    }

    public static void navigate(String fxml, Pane p_main) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                GameUtils.class.getResource("/algorangers/kenkenrangers/view/" + fxml));

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
