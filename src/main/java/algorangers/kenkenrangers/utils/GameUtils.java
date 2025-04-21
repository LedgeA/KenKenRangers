package algorangers.kenkenrangers.utils;

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

        if (node instanceof Rectangle) {
            Rectangle rectangleRef = (Rectangle) node;
            rectangleRef.widthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            rectangleRef.heightProperty().bind(region.heightProperty().multiply(nodeBaseHeight / BASE_HEIGHT));

            return;

        } else if (node instanceof Region) {
            Region regionRef = (Region) node;
            regionRef.prefWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            regionRef.prefHeightProperty().bind(region.heightProperty().multiply(nodeBaseHeight / BASE_HEIGHT));


            return;

        } else if (node instanceof ImageView) {
            ImageView imageViewRef = (ImageView) node;
            imageViewRef.fitWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            imageViewRef.fitHeightProperty().bind(region.heightProperty().multiply(nodeBaseHeight / BASE_HEIGHT));

            return;
        }

        System.out.println("| OUT OF SCOPE | Node is instance of: " + node.getClass().getSimpleName());

    }

    public static void bindSize(Node node, Region region, double nodeBaseWidth) {
        if (node instanceof Rectangle) {
            Rectangle rectangleRef = (Rectangle) node;
            rectangleRef.widthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));

            return;

        } else if (node instanceof Region) {
            Region regionRef = (Region) node;
            regionRef.prefWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));

            return;

        } else if (node instanceof ImageView) {
            ImageView imageViewRef = (ImageView) node;
            imageViewRef.fitWidthProperty().bind(region.widthProperty().multiply(nodeBaseWidth / BASE_WIDTH));
            
            return;
        }
    }

    public static void bindFontSize(Text text, Region region, double baseFontSize) {
        Font font = text.getFont();
        text.setFont(Font.font(font.getFamily(), baseFontSize / BASE_HEIGHT * region.getHeight()));
    }


}
