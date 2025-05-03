package algorangers.kenkenrangers.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundUtils {

    private static final String path = "/algorangers/kenkenrangers/sounds/";

    private static final Media tap = new Media(SoundUtils.class.getResource(path + "tap.wav").toExternalForm());
    private static final Media cleared = new Media(SoundUtils.class.getResource(path + "clear.wav").toExternalForm());
    private static final Media cast = new Media(SoundUtils.class.getResource(path + "cast.wav").toExternalForm());
    private static final Media recharge = new Media(SoundUtils.class.getResource(path + "recharge.wav").toExternalForm());
    private static final Media blocked = new Media(SoundUtils.class.getResource(path + "blocked.wav").toExternalForm());
    private static final Media win = new Media(SoundUtils.class.getResource(path + "win.wav").toExternalForm());
    private static final Media lose = new Media(SoundUtils.class.getResource(path + "lose.wav").toExternalForm());
    private static final Media press = new Media(SoundUtils.class.getResource(path + "press.wav").toExternalForm());

    private static final MediaPlayer music =
        new MediaPlayer(new Media(SoundUtils.class.getResource(path + "game.mp3").toExternalForm()));

    static {
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.setVolume(0.7);
    }

    public static void preloadSounds() {}

    private static void play(Media media) {
        MediaPlayer player = new MediaPlayer(media);
        player.setOnEndOfMedia(player::dispose);
        player.play();
    }

    public static void insert() {
        play(tap);
    }

    public static void cleared() {
        play(cleared);
    }

    public static void cast() {
        play(cast);
    }

    public static void recharge() {
        play(recharge);
    }

    public static void blocked() {
        play(blocked);
    }

    public static void finished(boolean gameWon) {
        play(gameWon ? win : lose);
    }

    public static void press() {
        play(press);
    }

    public static void musicOn() {
        music.play();
    }

    public static void musicPause() {
        music.pause();
    }

    public static void musicOff() {
        music.stop();
        music.seek(Duration.ZERO);
    }
}
