package algorangers.kenkenrangers.controllers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import algorangers.models.Cage;
import algorangers.models.Grid;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PuzzleController {

    @FXML
    GridPane gridPane;
    
    int pixelSize = 850;
    int[] buttonFocus;

    int[][] inputGrid;
    int size;
    int hp = 100;
    int dps;

    int invincibility = 3;
    int dmgMultiplier = 3;
    int cellReveal = 3; // random.nextInt(4) -> get cell from solutionGrid -> assign to inputGrid and disable

    Grid grid;
    List<Cage> cages;

    ScheduledExecutorService scheduler;
    
    public PuzzleController() {
        this.inputGrid = new int[4][4];
        this.size = 4;
        this.dps = 15;
        
        this.grid = new Grid(this.size);
        this.cages = grid.getCages();
    }

    @FXML
    public void initialize() {
        setConstraints();
        addCells();
        setColors();
        startDecrementingHP();

        System.out.println("Done initializing");
    }

    private void decrementHP() {    
        if (this.hp <= 0) {
            stopDecrementingHP();  
            return;
        }

        this.hp -= this.dps;
        System.out.println(this.hp + " ");
    }

    public void startDecrementingHP() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::decrementHP, 0, 5, TimeUnit.SECONDS);
    }

    public void stopDecrementingHP() {
        scheduler.shutdown();
    }

    private void setColors() {
        int cageSize = this.cages.size();

        for (int i = 0; i < cageSize; i++) {
            String color = cages.get(i).getColor();
            List<int[]> cells = cages.get(i).getCells();
            int cellSize = cells.size();
            
            for (int j = 0; j < cellSize; j++) {
                findCell(cells.get(j)[0], cells.get(j)[1], color);
            }
        }
    }

    private void setConstraints() {
        double cellSize = pixelSize / size;
        for (int i = 0; i < this.size; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(cellSize * 100);
            gridPane.getColumnConstraints().add(colConstraints);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(cellSize);
            gridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private void addCells() {
        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                Button button = new Button();
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);

                Font customFont = Font.loadFont(
                    getClass().getResourceAsStream(
                        "/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), 16);

                button.setFont(customFont);
                
                button.setOnAction(event -> handleButtonClick(button));
                button.setOnKeyPressed(event -> handleKeyPressed(button, event));

                StackPane stackPane = new StackPane();

                int cagePosition = findCage(row, col);
                List<int[]> cells = cages.get(cagePosition).getCells();

                if (cells.get(0)[0] == row && cells.get(0)[1] == col ) {
                    Label label = new Label(cages.get(cagePosition).getTarget() + " " + Character.toString(cages.get(cagePosition).getOperation()));
                    label.setFont(Font.loadFont(
                        getClass().getResourceAsStream(
                            "/algorangers/kenkenrangers/fonts/VT323-Regular.ttf"), 20));
                    label.setAlignment(Pos.TOP_LEFT);
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setMaxHeight(Double.MAX_VALUE);
                    label.setMouseTransparent(true);
                    label.setPadding(new Insets(10, 10, 10, 10));

                    stackPane.getChildren().addAll(button, label);
                } else {
                    stackPane.getChildren().add(button);
                }

                gridPane.add(stackPane, col, row);
            }
        }
    }

    private int findCage(int row, int col) {
        int cageSize = cages.size();

        for (int cageIndex = 0; cageIndex < cageSize; cageIndex++) {
            List<int[]> cells = cages.get(cageIndex).getCells();
            int cellSize = cells.size();

            for (int cellIndex = 0; cellIndex < cellSize; cellIndex++) {
                if (cells.get(cellIndex)[0] == row && cells.get(cellIndex)[1] == col) {
                    return cageIndex;
                }
            }
        }

        return -1;
    }

    private void findCell(int row, int col, String color) {
        for (Node node : this.gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                changeCellColor((Button) ((StackPane) node).getChildren().get(0), color);
            }
        }
    }

    private void findCell(int row, int col) {
        for (Node node : this.gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                Button button = (Button) ((StackPane) node).getChildren().get(0);
                button.setDisable(true);
            }
        }
    }

    private void changeCellColor(Button button, String color) {
        button.setBackground(new Background(new BackgroundFill(Color.web(color), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void handleButtonClick(Button button) {
        int row = GridPane.getRowIndex((StackPane) button.getParent());
        int col = GridPane.getColumnIndex((StackPane) button.getParent());

        buttonFocus = new int[]{row, col};
    }

    private void handleKeyPressed(Button button, KeyEvent event) {
        System.err.print(event.getText());

        if (isValidNumber(event.getText())) {
            button.setText(event.getText());
            inputGrid[GridPane.getRowIndex((StackPane) button.getParent())][GridPane.getColumnIndex((StackPane) button.getParent())] = Integer.parseInt(button.getText());
        }

        int[][] solutionGrid = grid.getGrid();

        for (int i = 0; i < this.cages.size(); i++) {
            if (this.cages.get(i).areCageEntriesAreValid(solutionGrid, inputGrid)) {
                List<int[]> cells = this.cages.get(i).getCells();
                
                for (int j = 0; j < cells.size(); j++) {
                    findCell(cells.get(j)[0], cells.get(j)[1]);
                }
                
                this.cages.remove(i);
                i--;
                hp += 15;
            }
        }

        System.err.println("HP: " + hp);


    }

    private boolean isValidNumber(String input) {
        try {
            int num = Integer.parseInt(input);
            return num > 0 && num <= this.size;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    
}
