package algorangers.kenkenrangers.controllers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import algorangers.models.Cage;
import algorangers.models.Cell;
import algorangers.models.Grid;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    int[][] inputGrid;
    Button[][] buttonReferences;
    int pixelSize = 850;
    int dimension;

    Grid grid;
    List<Cage> cages;
    int hp = 100;
    int dps;

    ScheduledExecutorService scheduler;
    
    public PuzzleController() {
        this.inputGrid = new int[4][4];
        this.dimension = 4;
        this.dps = 15;
        
        this.grid = new Grid(this.dimension);
        this.cages = grid.getCages();
        this.buttonReferences = new Button[this.dimension][this.dimension];
    }

    @FXML
    public void initialize() {
        setConstraints();
        addCells();
        setColors();
        startDecrementingHP();

        System.out.println("Done initializing");
    }

    private void setConstraints() {
        double cellSize = pixelSize / dimension;
        for (int i = 0; i < this.dimension; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(cellSize * 100);
            gridPane.getColumnConstraints().add(colConstraints);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(cellSize);
            gridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private void addCells() {
        for (int row = 0; row < this.dimension; row++) {
            for (int col = 0; col < this.dimension; col++) {
                Button button = new Button();
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);

                button.setFont(Font.loadFont(
                    getClass().getResourceAsStream(
                        "/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), 35));
                
                button.focusedProperty().addListener((obs, oldVal, newVal) -> buttonOnFocus(button, obs, oldVal, newVal));
                button.setOnKeyPressed(event -> handleKeyPressed(button, event));

                StackPane stackPane = new StackPane();

                int cagePosition = findCage(row, col);
                List<Cell> cells = cages.get(cagePosition).getCells();

                if (cells.get(0).getRow() == row && cells.get(0).getColumn() == col ) {
                    Label label = new Label(cages.get(cagePosition).getTarget() + " " + Character.toString(cages.get(cagePosition).getOperation()));
                    
                    label.setFont(Font.loadFont(
                        getClass().getResourceAsStream(
                            "/algorangers/kenkenrangers/fonts/VT323-Regular.ttf"), 40));

                    label.setAlignment(Pos.TOP_LEFT);
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setMaxHeight(Double.MAX_VALUE);
                    label.setPadding(new Insets(10, 10, 10, 10));

                    label.setMouseTransparent(true);

                    stackPane.getChildren().addAll(button, label);
                } else {
                    stackPane.getChildren().add(button);
                }

                buttonReferences[row][col] = button;
                gridPane.add(stackPane, col, row);
            }
        }
    }
    

    private int findCage(int row, int col) {
        int cageSize = cages.size();

        for (int cageIndex = 0; cageIndex < cageSize; cageIndex++) {
            List<Cell> cells = cages.get(cageIndex).getCells();
            int cellSize = cells.size();

            for (int cellIndex = 0; cellIndex < cellSize; cellIndex++) {
                if (cells.get(cellIndex).getRow() == row && cells.get(cellIndex).getColumn() == col) {
                    return cageIndex;
                }
            }
        }

        return -1;
    }

    private void setColors() {
        int cageSize = this.cages.size();

        for (int i = 0; i < cageSize; i++) {
            String color = cages.get(i).getColor();
            List<Cell> cells = cages.get(i).getCells();
            int cellSize = cells.size();
            
            for (int j = 0; j < cellSize; j++) {
                changeCellColor(cells.get(j).getRow(), cells.get(j).getColumn(), color);
            }
        }
    }

    private void changeCellColor(int row, int col, String color) {
        buttonReferences[row][col].setBackground(
            new Background(
                new BackgroundFill(
                Color.web(color), CornerRadii.EMPTY, Insets.EMPTY)));
    }

// LISTENERS
    private void buttonOnFocus(Button button, ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) {
        if (newVal) {
            System.out.println("Button gained focus");
        } else {
            System.out.println("Button lost focus");
        }
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
                List<Cell> cells = this.cages.get(i).getCells();
                
                for (int j = 0; j < cells.size(); j++) {
                    findCell(cells.get(j).getRow(), cells.get(j).getColumn());
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
            return num > 0 && num <= this.dimension;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void findCell(int row, int col) {
        buttonReferences[row][col].setDisable(true);
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

    
}
