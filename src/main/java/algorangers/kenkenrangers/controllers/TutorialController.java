package algorangers.kenkenrangers.controllers;

import javafx.fxml.FXML;

public class TutorialController {

    // @FXML
    // Button invincibility;

    // @FXML
    // Button dmgMultiplier;

    // @FXML 
    // Button cellReveal;

    // @FXML
    // GridPane puzzleInclude;
    
    // private PuzzleController puzzleController;

    @FXML
    public void initialize() {
        // try {
        //     FXMLLoader puzzleLoader = new FXMLLoader(getClass().getResource("/algorangers/kenkenrangers/view/puzzle.fxml"));
        //     GridPane puzzleGrid = puzzleLoader.load();
        //     puzzleInclude.getChildren().add(puzzleGrid);

        //     // Retrieve PuzzleController from FXMLLoader
        //     puzzleController = puzzleLoader.getController();

        //     if (puzzleController != null) {
        //         // Now you can interact with the PuzzleController instance
        //         System.out.println("PuzzleController is ready!");
        //     } else {
        //         System.out.println("PuzzleController is not initialized.");
        //     }

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // addButtonActionListener();
    }

    // private void addButtonActionListener() {
    //     invincibility.setOnAction(event -> {

    //         System.out.println("Invincibility Pressed");
    //         if (this.puzzleController.invincibility >= 0) {
    //             this.puzzleController.invincibility -= 1;
    //             this.puzzleController.stopDecrementingHP();
    //             System.out.println("Stop decreasing HP");

    //             // Create a Timeline to run once after 5 seconds
    //             Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(15), e -> {
    //                 this.puzzleController.startDecrementingHP();
    //                 System.out.println("Continue decreasing HP");

    //             }));
    //             timeline.setCycleCount(1);
    //             timeline.play();
    //         }
    //     });

    //     dmgMultiplier.setOnAction(event -> {

    //     });

    //     cellReveal.setOnAction(event -> {

    //     });

    // }

    


}

