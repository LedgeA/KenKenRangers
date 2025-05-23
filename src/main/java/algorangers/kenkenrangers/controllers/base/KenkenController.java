package algorangers.kenkenrangers.controllers.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import algorangers.kenkenrangers.models.*;
import algorangers.kenkenrangers.models.Cage.Cell;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
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
    private Button[][] buttonRefs;
    private int DIMENSION;

    private int hp = 100, dot;
    private int powerSurge, invincibility, cellReveal;
    private boolean invincible;
    private int multiplier = 1;
    
    private Random rand = new Random();

    public KenkenController(int DIMENSION, int dot, int powerSurge, int invincibility, int cellReveal) {

        SoundUtils.preloadSounds();
        
        this.inputGrid = new int[DIMENSION][DIMENSION];
        this.DIMENSION = DIMENSION;

        this.dot = dot;
        this.powerSurge = powerSurge;
        this.invincibility = invincibility;
        this.cellReveal = cellReveal;

        // instantiate model
        k_model = new Kenken(DIMENSION);

        // retrieve reference of solution grid and cages
        this.solutionGrid = k_model.getGrid();
        k_cages = k_model.getCages();

        // initialize an array to save reference of the generate buttons
        this.buttonRefs = new Button[DIMENSION][DIMENSION];

        setupGrid();
    }

    // setup gui
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

    // set cell constraints of grid pane
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

    // setup cells
    // iterate to cages then cells
    // set the first cell to include both target and operation in display
    private void addCells() {
        InnerShadow shadowFx = shadowFx();

        for (Cage cage : k_cages) {
            String color = cage.getColor();
            List<Cell> cells = cage.getCells();
    
            Iterator<Cell> iterator = cells.iterator();
            addCell(cage, iterator.next(), shadowFx, color);
            
            // cage passed to keep the cells from being generated with target and operation
            while (iterator.hasNext()) addCell(null, iterator.next(), shadowFx, color);
        }
    }

    // create, add the cell, and save the reference to the buttonRefs array
    private void addCell(Cage cage, Cell cell, InnerShadow innerShadow, String color) {
        int row = cell.row();
        int col = cell.col();
    
        StackPane stackPane = new StackPane();
        Button button = createButton(innerShadow, color);
        stackPane.getChildren().add(button);
    
        if (cage != null) {
            int target = cage.getTarget();
            char operation = cage.getOperation();
            Label label = createLabel(target, operation);
            stackPane.getChildren().add(label);
        }
    
        k_view.add(stackPane, col, row);
        this.buttonRefs[row][col] = button;
    }

    // helper function to generate fx when button is in focus
    private InnerShadow shadowFx() {
        InnerShadow shadowFx = new InnerShadow();
        shadowFx.setOffsetX(0);
        shadowFx.setOffsetY(0);
        shadowFx.setRadius(20);   
        shadowFx.setChoke(0.2); 
        shadowFx.setColor(Color.BLACK);

        return shadowFx;
    }

    // creates label for the first cells of the cage
    private Label createLabel(int target, char operation) {
        Label label = new Label(target + " " + operation);
        
        double fontSizeRatio = 40.0 / 125;
        double fontSize = fontSizeRatio * (500.0 / DIMENSION);

        label.setFont(Font.loadFont(
            getClass().getResourceAsStream(
                "/algorangers/kenkenrangers/fonts/VT323-Regular.ttf"), fontSize));

        label.setAlignment(Pos.TOP_LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setPadding(new Insets(10));

        label.setMouseTransparent(true);

        return label;
    }

    // creates button of which are added to the grid gui cells
    private Button createButton(InnerShadow innerShadow, String color) {
        Button button = new Button();

        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);  

        double fontSizeRatio = 35.0 / 125;
        double fontSize = fontSizeRatio * (500.0 / DIMENSION);

        button.setFont(Font.loadFont(
            getClass().getResourceAsStream(
                "/algorangers/kenkenrangers/fonts/PressStart2P-Regular.ttf"), fontSize));
        
        button.setBackground(
            new Background(
                new BackgroundFill(
                Color.web(color), CornerRadii.EMPTY, Insets.EMPTY)));
                
        // darken button when in focus
        button.focusedProperty().addListener((obs, oldVal, newVal) -> {
            button.setEffect(newVal ? innerShadow : null);
        });

        // play sound when key is pressed while button is in focus
        // process input. if input is valid, update button and check if it follows the solution
        button.setOnKeyPressed(event -> {
            if (!inputIsValid(event.getText())) return;
            
            SoundUtils.insert();
            button.setText(event.getText());
            updateInputGrid(button);
            evaluateAllInputs();
        });

        return button;
    }

    // check if input is number from 1 to DIMENSION
    private boolean inputIsValid(String input) {
        try {
            int num = Integer.parseInt(input);
            return num > 0 && num <= DIMENSION;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // find button position
    // update input grid cell to the current text of the button
    private void updateInputGrid(Button button) {
        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                if (buttonRefs[row][col] == button) {
                    inputGrid[row][col] = Integer.parseInt(button.getText());

                    return;
                }
            }
        }
    }

    // iterate through all cages and check if correct
    // if yes, disable all buttons, play sound, remove cage reference, and increase hp
    protected void evaluateAllInputs() {
        Iterator<Cage> iterator = k_cages.iterator();

        while (iterator.hasNext()) {
            Cage cage = iterator.next();
    
            if (!cage.areCageEntriesAreValid(solutionGrid, inputGrid)) continue;
            List<Cell> cells = cage.getCells();
            disableCells(cells);
            SoundUtils.cleared();

            iterator.remove(); 
            
            if (hp <= 100 - dot) this.hp += 5;
        }
    }

    // if all the cages are valid, disable all buttons through buttonRefs
    private void disableCells(List<Cell> cells) {
        int cellSize = cells.size();

        for (int cellIndex = 0; cellIndex < cellSize; cellIndex++) {
            Cell cell = cells.get(cellIndex);
            int row = cell.row();
            int col = cell.col();

            buttonRefs[row][col].setDisable(true);
        }
    }

    // used for cell reveal powerup
    // find all available cells first
    // set limit and counter for the revealed cells
    // continue revealing cells until either counter = limit or no more available cell exists
    private void revealCells() {
        List<Cell> availableCells = findAvailableCells();
        int limit = multiplier * 2;
        int counter = 0;

        while (counter < limit && availableCells.size() != 0) {
            int randCell = rand.nextInt(availableCells.size());
            Cell cell = availableCells.get(randCell);
            availableCells.remove(randCell);

            int row = cell.row(), col = cell.col();
            int value = solutionGrid[row][col];

            inputGrid[row][col] = value;

            Button button = buttonRefs[row][col];
            button.setText(String.valueOf(value));
            button.setDisable(true);
            counter++;
        }
    }

    // find not disabled buttons
    private List<Cell> findAvailableCells() {
        List<Cell> availableCells = new ArrayList<>();
        
        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                if (!buttonRefs[row][col].isDisabled()) {
                    availableCells.add(new Cell(row, col));
                }
            }
        }

        return availableCells;
    }

    public GridPane getK_view() {
        return k_view;
    }

    public int getRemainingCageAmount() {
        return this.k_cages.size();
    }

    public void decreaseHp() {
        this.hp -= this.dot;
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
        evaluateAllInputs();
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

    public void multiplierWearOff() {
        this.multiplier = 1;
    }

    public void invincibilityWearOff() {
        this.invincible = false;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public boolean getInvincibleState() {
        return this.invincible;
    }
}
