package algorangers.kenkenrangers;

import java.io.IOException;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class Launcher extends Application {

    // private static final String URL = "jdbc:sqlite:kenken.db"; 
    private static final double BASE_WIDTH = 1280;
    private static final double BASE_HEIGHT = 720;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("view/initial-main-menu.fxml"));
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
            System.out.println("Closing app...");
    
        });

        stage.show();

        
    }

    // public static void createTable() {
    //     String sql = "CREATE TABLE IF NOT EXISTS players ("
    //                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
    //                + " name TEXT NOT NULL,"
    //                + " score INTEGER"
    //                + ");";
    
    //     try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
    //         stmt.execute();
    //         System.out.println("Table created.");
    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }

    // public static void insertPlayer(String name, int score) {
    //     String sql = "INSERT INTO players(name, score) VALUES(?, ?)";
    
    //     try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
    //         stmt.setString(1, name);
    //         stmt.setInt(2, score);
    //         stmt.executeUpdate();
    //         System.out.println("Player added.");
    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }

    
    // public static void showAllPlayers() {
    //     String sql = "SELECT * FROM players";
    
    //     try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
    //         ResultSet rs = stmt.executeQuery();
    
    //         while (rs.next()) {
    //             System.out.println(rs.getInt("id") + " | " +
    //                                rs.getString("name") + " | ");
    //         }
    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }
    

    public static void main(String[] args) {
        // createTable();
        // insertPlayer("Led", 101);
        // showAllPlayers();
        
        launch();
    }
}