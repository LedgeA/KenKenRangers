package algorangers.kenkenrangers.controllers.base;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import algorangers.kenkenrangers.models.*;

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
        this.inputGrid = new int[DIMENSION][DIMENSION];
        this.DIMENSION = DIMENSION;

        this.dot = dot;
        this.powerSurge = powerSurge;
        this.invincibility = invincibility;
        this.cellReveal = cellReveal;

        k_model = new Kenken(DIMENSION);
        this.solutionGrid = k_model.getGrid();

        k_cages = k_model.getCages();
        this.buttonRefs = new Button[DIMENSION][DIMENSION];

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
        InnerShadow shadowFx = shadowFx();

        for (Cage cage : k_cages) {
            String color = cage.getColor();
            List<Cell> cells = cage.getCells();
    
            Iterator<Cell> iterator = cells.iterator();
            addCell(cage, iterator.next(), shadowFx, color);
            
            while (iterator.hasNext()) addCell(null, iterator.next(), shadowFx, color);
            
        }
    }

    private InnerShadow shadowFx() {
        InnerShadow shadowFx = new InnerShadow();
        shadowFx.setOffsetX(0);
        shadowFx.setOffsetY(0);
        shadowFx.setRadius(20);   
        shadowFx.setChoke(0.2); 
        shadowFx.setColor(Color.BLACK);

        return shadowFx;
    }

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
                
        button.focusedProperty().addListener((obs, oldVal, newVal) -> {
            button.setEffect(newVal ? innerShadow : null);
        });

        button.setOnKeyPressed(event -> {
            if (!inputIsValid(event.getText())) return;
            
            button.setText(event.getText());
            updateButton(button);
            evaluateAllInputs();
        });

        return button;
    }

    protected void evaluateAllInputs() {
        Iterator<Cage> iterator = k_cages.iterator();

        while (iterator.hasNext()) {
            Cage cage = iterator.next();
    
            if (cage.areCageEntriesAreValid(solutionGrid, inputGrid)) {
                List<Cell> cells = cage.getCells();
                disableCells(cells);
    
                iterator.remove(); 
                this.hp += 5;
            }
        }
    }

    private void disableCells(List<Cell> cells) {
        int cellSize = cells.size();

        for (int cellIndex = 0; cellIndex < cellSize; cellIndex++) {
            Cell cell = cells.get(cellIndex);
            int row = cell.row();
            int col = cell.col();

            buttonRefs[row][col].setDisable(true);
        }
    }

    private boolean inputIsValid(String input) {
        try {
            int num = Integer.parseInt(input);
            return num > 0 && num <= DIMENSION;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateButton(Button button) {
        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                if (buttonRefs[row][col] == button) {
                    inputGrid[row][col] = Integer.parseInt(button.getText());

                    return;
                }
            }
        }
    }

    private void revealCells() {
        int cellCount = multiplier * 2;
        int counter = 0;

        while (cellCount > counter) {
            int row = rand.nextInt(DIMENSION);
            int col = rand.nextInt(DIMENSION);
            Button button = buttonRefs[row][col];

            if (button.isDisabled()) continue;
            int value = solutionGrid[row][col];
            inputGrid[row][col] = value;

            button.setText(String.valueOf(value));
            button.setDisable(true);
            counter++;
        }

    }

    public GridPane getK_view() {
        return k_view;
    }

    public int getRemainingCageAmount() {
        return this.k_cages.size();
    }

    public void increaseHp() {
        this.hp += this.dot;
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
}
