package algorangers.kenkenrangers.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {
    
    public static void fadeOut(Runnable onFinish, Node node) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), node);
        fadeOut.setToValue(0); 
        fadeOut.setOnFinished(e -> onFinish.run()); 
        fadeOut.play();
    }

    public static void fadeIn(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), node);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
