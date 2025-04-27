package algorangers.kenkenrangers.controllers.menu;

import java.sql.ResultSet;
import java.sql.SQLException;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class SelectChapterController {

    @FXML 
    private Pane p_main;

    @FXML
    private GridPane g_villains;

    @FXML
    private ImageView i_orc, i_gnome, i_goblin, i_dragon, i_beast, i_demon;

    @FXML
    private Text t_cancel;

    private String name;

    private int latestFinishedChapter;
    
    @FXML
    private void initialize() throws SQLException{
        retrievePlayerSession();
        retrievePlayerProgress();

        setImagesAsButtons();

        GameUtils.setMenuTextAppearance(t_cancel);
        t_cancel.setOnMouseClicked(event -> {
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }


    private void setImagesAsButtons() {
        int chapterCount = 0; 

        // Iterate through the children nodes of gridpane
        for (Node node : g_villains.getChildren()) {
            chapterCount++;

            // set the images gray if the chapter is unfinished
            if (chapterCount > latestFinishedChapter + 1) {
                setVillainGray(node);
                continue;
            }

            setImageAsButton(node, chapterCount); // make images clickable when playable by player
        }
    }

    private void setVillainGray(Node node) {
        // set the border gray
        Pane pane = (Pane) node;
        pane.setStyle(
            "-fx-border-width:  5; " +
            "-fx-border-color: #000000; " +
            "-fx-effect: dropshadow(gaussian, black, 10, 0, 0, 0), dropshadow(gaussian, black, 20, 0, 0, 0), dropshadow(gaussian, black, 30, 0, 0, 0);");
        
        // object for turning image to gray
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1);

        // get image from pane and turn into gray
        ImageView imageView = (ImageView) pane.getChildren().get(0);
        imageView.setEffect(colorAdjust);

    }

    private void setImageAsButton(Node node, int chapterCount) {
        // get image from pane
        Pane pane = (Pane) node;
        ImageView imageView = (ImageView) pane.getChildren().get(0);

        // set navigation for when buttons are clicked
        imageView.setOnMouseClicked(event -> {
            String fxml = "chapter-" + numToWord(chapterCount) + ".fxml";

            GameUtils.navigate(fxml, p_main);
        });

    }

    // convert number to word form
    private String numToWord(int num) {
        return switch (num) {
            case 1 -> "one";
            case 2 -> "two";
            case 3 -> "three";
            case 4 -> "four";
            case 5 -> "five";
            case 6 -> "six";
            default -> "zero";
        };
    }

    private void retrievePlayerSession() throws SQLException {
        ResultSet rs = DatabaseManager.retrieveGameSession();

        if (rs.next()) {
            name = rs.getString("name");
        }

        rs.close();
    }

    private void retrievePlayerProgress() throws SQLException {
        ResultSet rs = DatabaseManager.retrievePlayerData(name);

        if (rs.next()) {
            latestFinishedChapter = rs.getInt("latest_finished_chap");
        }

        rs.close();
    }


}