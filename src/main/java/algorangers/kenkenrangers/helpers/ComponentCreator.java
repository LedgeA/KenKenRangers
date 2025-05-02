package algorangers.kenkenrangers.helpers;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

public class ComponentCreator {
    public static Background createTextFlowFill() {
        Color backgroundColor = Color.web("#F3EAD1", 0.8);
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(25), Insets.EMPTY);

        return new Background(backgroundFill);
    }

    private static Arc createArc() {
        Arc arc = new Arc(20, 20, 20, 20, -90, 0);
        arc.setType(ArcType.ROUND);
        arc.setFill(Color.rgb(0, 0, 0, 0.5));

        arc.setClip(new Circle(20, 20, 20));

        return arc;
    }

    private static ImageView createImageView(String imgName) {
        String imagePath = "/algorangers/kenkenrangers/power-ups/" + imgName + ".png";
        Image image = new Image(ComponentCreator.class.getResource(imagePath).toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(40);
        imageView.setFitHeight(40);

        Circle clip = new Circle(20, 20, 20);
        imageView.setClip(clip);

        return imageView;
    }
    
    public static Arc addCooldownImages(VBox vbox, String imgName) {
        ImageView imageView = createImageView(imgName);
        imageView.setLayoutX(30);
        
        Arc arc = createArc();
        arc.setLayoutX(30);

        Pane imagePane = new Pane(imageView);
        imagePane.setPrefSize(100, 40);
        Pane arcPane = new Pane(arc);
        arcPane.setPrefSize(100, 40);

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(100, 40);
        stackPane.getChildren().addAll(imagePane, arcPane);

        vbox.getChildren().addAll(stackPane);

        return arc;
    }

    public static Image createImage(String imgName) {
        String imagePath = "/algorangers/kenkenrangers/power-ups/" + imgName + ".png";

        return new Image(ComponentCreator.class.getResource(imagePath).toExternalForm());
    }
}
