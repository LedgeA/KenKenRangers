package algorangers.kenkenrangers.utils;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
            Font currentFont = text.getFont();
            text.setFont(Font.font(currentFont.getFamily(), newVal.doubleValue()));
        });
    }
}
