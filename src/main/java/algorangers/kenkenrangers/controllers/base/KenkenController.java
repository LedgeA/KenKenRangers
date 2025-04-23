package algorangers.kenkenrangers.controllers.base;

import java.util.List;
import java.util.Random;

import algorangers.kenkenrangers.models.*;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class KenkenController {

    private GridPane k_view;
    private Kenken k_model;
    private List<Cage> k_cages;

    private int[][] inputGrid;
    private int[][] solutionGrid;
    private Button[][] buttonReferences;
    private int DIMENSION;

    private int hp, dps;
    private int powerSurge, invincibility, cellReveal;
    private boolean invincible;
    private int multiplier = 1;
    
    private Random rand = new Random();

    public KenkenController(int DIMENSION, int hp, int dps, int powerSurge, int invincibility, int cellReveal) {
        this.inputGrid = new int[DIMENSION][DIMENSION];
        this.DIMENSION = DIMENSION;

        this.hp = hp;
        this.dps = dps;
        this.powerSurge = powerSurge;
        this.invincibility = invincibility;
        this.cellReveal = cellReveal;

        k_model = new Kenken(DIMENSION);
        this.solutionGrid = k_model.getGrid();

        k_cages = k_model.getCages();
        this.buttonReferences = new Button[DIMENSION][DIMENSION];

        setupGrid();
    }

    public KenkenController(int DIMENSION, int hp, int dps) {
        this.inputGrid = new int[DIMENSION][DIMENSION];
        this.DIMENSION = DIMENSION;

        this.hp = hp;
        this.dps = dps;

        k_model = new Kenken(DIMENSION);
        this.solutionGrid = k_model.getGrid();
        
        k_cages = k_model.getCages();
        this.buttonReferences = new Button[DIMENSION][DIMENSION];

        setupGrid();
    }

    public KenkenController(int DIMENSION) {
        this.inputGrid = new int[DIMENSION][DIMENSION];
        this.DIMENSION = DIMENSION;

        k_model = new Kenken(DIMENSION);
        this.solutionGrid = k_model.getGrid();
        
        k_cages = k_model.getCages();
        this.buttonReferences = new Button[DIMENSION][DIMENSION];

        setupGrid();

    }

    private void setupGrid() {
        k_view = new GridPane();
        k_view.setPrefSize(500, 500);
        k_view.setLayoutX(390);
        k_view.setLayoutY(91);
        k_view.setHgap(5);
        k_view.setVgap(5);
        k_view.setBorder(new Border(new BorderStroke(
                Color.BLACK,                
                BorderStrokeStyle.SOLID,    
                CornerRadii.EMPTY,          
                new BorderWidths(5)  
            )));

        k_view.setBackground(new Background(new BackgroundFill(
                Color.BLACK,        
                CornerRadii.EMPTY,  
                Insets.EMPTY      
            )));
        
        setConstraints();
        addCells();
    }

    private void setConstraints() {
        double percentage = 100.0 / DIMENSION;

        for (int i = 0; i < DIMENSION; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(percentage);
            k_view.getColumnConstraints().add(colConstraints);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(percentage);
            k_view.getRowConstraints().add(rowConstraints);
        }
    }

    private void addCells() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(0);
        innerShadow.setRadius(20);   
        innerShadow.setChoke(0.2); 
        innerShadow.setColor(Color.BLACK);

        int cageSize = k_cages.size();

        for (int cageIndex = 0; cageIndex < cageSize; cageIndex++) {

            Cage cage = k_cages.get(cageIndex);
            List<Cell> cells = cage.getCells();
            int cellSize = cells.size();
            String color = cage.getColor();

            addCell(cage, cells.get(0), innerShadow, color);

            for (int cellIndex = 1; cellIndex < cellSize; cellIndex++) {
                addCell(cells.get(cellIndex), innerShadow, color);
            }
        }
    }

    private void addCell(Cage cage, Cell cell, InnerShadow innerShadow, String color) {
        int target = cage.getTarget();
        char operation = cage.getOperation();

        int row = cell.getRow();
        int col = cell.getColumn();

        StackPane stackPane = new StackPane();
        Button button = createButton(innerShadow, color);
        Label label = createLabel(target, operation);

        stackPane.getChildren().addAll(button, label);
        k_view.add(stackPane, col, row);
        this.buttonReferences[row][col] = button;
    }

    private void addCell(Cell cell, InnerShadow innerShadow, String color) {
        int row = cell.getRow();
        int col = cell.getColumn();

        StackPane stackPane = new StackPane();
        Button button = createButton(innerShadow, color);

        stackPane.getChildren().add(button);
        k_view.add(stackPane, col, row);
        this.buttonReferences[row][col] = button;
    }

    private Label createLabel(int target, char operation) {
        Label label = new Label(target + " " + operation);
        
        label.setFont(Font.loadFont(
            getClass().getResourceAsStream(
                "/algorangers/kenkenrangers/fonts/VT323-Regular.ttf"), 40));

        label.setAlignment(Pos.TOP_LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setPadding(new Insets(10));

        label.setMouseTransparent(true);

        return label;
    }

    private Button createButton(InnerShadow innerShadow, String color) {
        Button button = new Button();

        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);  

        button.setFont(Font.loadFont(
            getClass().getResourceAsStream(
                "/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), 35));
        
        button.setBackground(
            new Background(
                new BackgroundFill(
                Color.web(color), CornerRadii.EMPTY, Insets.EMPTY)));
                
        button.focusedProperty().addListener((obs, oldVal, newVal) -> buttonOnFocus(button, innerShadow, obs, oldVal, newVal));
        button.setOnKeyPressed(event -> handleKeyPressed(button, event));

        return button;
    }

    private void buttonOnFocus(Button button, InnerShadow innerShadow, ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) {
        if (newVal) {
            button.setEffect(innerShadow);
        } else {
            button.setEffect(null);
        }
    }

    private void handleKeyPressed(Button button, KeyEvent event) {

        if (inputIsValidNo(event.getText())) {
            button.setText(event.getText());

            setInputValue(button);
        }

        evaluateInputGrid();
    }

    protected void evaluateInputGrid() {
        for (int cageIndex = 0; cageIndex < k_cages.size(); cageIndex++) {
            Cage cage = k_cages.get(cageIndex);

            if (cage.areCageEntriesAreValid(solutionGrid, inputGrid)) {
                List<Cell> cells = cage.getCells();
                int cellSize = cells.size();
                
                for (int cellIndex = 0; cellIndex < cellSize; cellIndex++) {
                    disableCageCells(cells.get(cellIndex));
                }
                
                k_cages.remove(cageIndex);
                cageIndex--;
                this.hp += 5;
            }
        }
    }

    private boolean inputIsValidNo(String input) {
        try {
            int num = Integer.parseInt(input);
            return num > 0 && num <= DIMENSION;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setInputValue(Button button) {
        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                if (buttonReferences[row][col] == button) {
                    inputGrid[row][col] = Integer.parseInt(button.getText());

                    return;
                }
            }
        }
    }

    public GridPane getK_view() {
        return k_view;
    }

    private void disableCageCells(Cell cell) {
        int row = cell.getRow();
        int col = cell.getColumn();

        buttonReferences[row][col].setDisable(true);
    }

    private void revealCells() {
        int cellCount = multiplier * 2;
        int counter = 0;

        while (cellCount > counter) {
            int row = rand.nextInt(DIMENSION);
            int col = rand.nextInt(DIMENSION);
            Button button = buttonReferences[row][col];

            if (!button.isDisabled()) {
                int value = solutionGrid[row][col];
                inputGrid[row][col] = value;

                button.setText(String.valueOf(value));
                button.setDisable(true);
                counter++;
            }
        }

    }

    public void increaseHp() {
        this.hp += this.dps;
    }

    public void decreaseHp() {
        this.hp -= this.dps;
    }

    public void consumePowerSurge() {
        this.multiplier = 2;
        this.powerSurge--;
    }

    public void consumeInvincibility() {
        this.invincible = true;
        this.invincibility--;
    }

    public void consumeCellReveal() {
        revealCells();
        evaluateInputGrid();
        this.cellReveal--;
    }

    public int getHp() {
        return this.hp;
    }

    public int getPowerSurge() {
        return this.powerSurge;
    }

    public int getInvincibility() {
        return this.invincibility;
    }

    public int getCellReveal() {
        return this.cellReveal;
    }

    public void invincibilityWearOff() {
        this.invincible = false;
    }

    public boolean getInvincibleState() {
        return this.invincible;
    }
    
    public void multiplierWearOff() {
        this.multiplier = 1;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public int getRemainingCageAmount() {
        return this.k_cages.size();
    }
}
