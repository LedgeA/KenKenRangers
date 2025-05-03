package algorangers.kenkenrangers.utils;

import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundUtils {
    
    private static final String path = "/algorangers/kenkenrangers/sounds/";

    private static final Media tap = new Media(SoundUtils.class.getResource(path + "tap.wav").toExternalForm());
    private static final Media cleared = new Media(SoundUtils.class.getResource(path + "clear.wav").toExternalForm());;

    private static final Media cast = new Media(SoundUtils.class.getResource(path + "cast.wav").toExternalForm());
    private static final Media recharge = new Media(SoundUtils.class.getResource(path + "recharge.wav").toExternalForm());
    private static final Media blocked = new Media(SoundUtils.class.getResource(path + "blocked.wav").toExternalForm());

    private static final Media win = new Media(SoundUtils.class.getResource(path + "win.wav").toExternalForm());
    private static final Media lose = new Media(SoundUtils.class.getResource(path + "lose.wav").toExternalForm());

    private static final Media press = new Media(SoundUtils.class.getResource(path + "press.wav").toExternalForm());

    private static final MediaPlayer music = 
        new MediaPlayer(
            new Media(SoundUtils.class.getResource(path + "game.mp3").toExternalForm()));

    public static void preloadSounds() {}

    static {
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.setVolume(0.5);
    }
    
    public static void playInput() {
        System.out.println("Tap");
        MediaPlayer mediaPlayer = new MediaPlayer(tap);
        mediaPlayer.play();
    }

    public static void playCageCleared() {
        MediaPlayer mediaPlayer = new MediaPlayer(cleared);
        mediaPlayer.play();
    }

    public static void playCast() {
        MediaPlayer mediaPlayer = new MediaPlayer(cast);
        mediaPlayer.play();
    }

    public static void playRecharge() {
        MediaPlayer mediaPlayer = new MediaPlayer(recharge);
        mediaPlayer.play();
    }

    public static void playBlocked() {
        MediaPlayer mediaPlayer = new MediaPlayer(blocked);
        mediaPlayer.play();
    }

    public static void playGameFinished(boolean gameWon) {
        MediaPlayer mediaPlayer = null;

        if (gameWon) {
            mediaPlayer = new MediaPlayer(win);
        } else {
            mediaPlayer = new MediaPlayer(lose);
        }

        mediaPlayer.play();
    }

    public static void playPress() {
        MediaPlayer mediaPlayer = new MediaPlayer(press);
        mediaPlayer.play();
    }

    public static void playMusic() {    
        music.play();
    }

    public static void pauseMusic() {
        music.pause();
    }

    public static void stopMusic() {
        music.stop();
        music.seek(Duration.ZERO);
    }


}
