package algorangers.kenkenrangers.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import algorangers.kenkenrangers.models.Cage.Cell;

public class Kenken {
    private final int DIMENSION;
    private final int[][] grid;
    private final List<Cage> cages = new ArrayList<>();

    List<String> colors = new ArrayList<>(Arrays.asList(
        "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", 
        "#FFA500", "#800080", "#008000", "#008080", "#800000", "#808000", 
        "#00CED1", "#DC143C", "#FF1493", "#1E90FF", "#32CD32", "#FFD700", 
        "#ADFF2F", "#FF69B4", "#4B0082", "#40E0D0", "#FF7F50", "#7FFF00", 
        "#BA55D3", "#F08080", "#00FA9A", "#FF6347", "#DA70D6", "#87CEEB", 
        "#66CDAA", "#CD5C5C", "#20B2AA", "#9370DB", "#3CB371", "#FFDAB9", 
        "#F0E68C", "#D2691E", "#D3D3D3", "#FF8C00", "#B8860B", "#8A2BE2", 
        "#A52A2A", "#B0E0E6", "#FFB6C1", "#9ACD32", "#C71585", "#C0C0C0"
    ));


    
    private final Random RANDOM = new Random();
    
    public Kenken(int DIMENSION) {
        this.DIMENSION = DIMENSION;
        this.grid = new int[DIMENSION][DIMENSION];

        generateGrid();
        generateCages();

    }
    
    // starts recursive function
    private void generateGrid() {
        fillGrid(0, 0);
    }
    
    // backtracking algorithm
    private boolean fillGrid(int row, int col) {
        if (row == DIMENSION) return true;
        if (col == DIMENSION) return fillGrid(row + 1, 0);
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= DIMENSION; i++) numbers.add(i);
        Collections.shuffle(numbers, RANDOM);
        
        for (int num : numbers) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(row, col + 1)) return true;
                grid[row][col] = 0;
            }
        }
        return false;
    }

    // checks uniqueness in orthogonal directions
    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < DIMENSION; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return false;
            }
        }
        return true;
    }
    
    // iterate through grid while iterating cages
    private void generateCages() {
        // mark each visited cells (cells who already belong to a cage) 
        boolean[][] visited = new boolean[DIMENSION][DIMENSION];     

        for (int row = 0; row < DIMENSION; row++) {
            for (int col = 0; col < DIMENSION; col++) {
                setCell(visited, row, col);
            }
        }
    }
    
    private void setCell(boolean[][] visited, int row, int col) {
        if (visited[row][col]) return; 

        List<Cell> cells = new ArrayList<>();
        Queue<Cell> openCells = new LinkedList<>();
        openCells.add(new Cell(row, col));

        int randomColor = RANDOM.nextInt(colors.size());
        String color = this.colors.get(randomColor);
        this.colors.remove(randomColor); 

        int cageSize = RANDOM.nextInt(4) + 1;

        while (!openCells.isEmpty() && cells.size() < cageSize) {
            Cell cell = openCells.poll();
            int cRow = cell.row(), cCol = cell.col();
            if (visited[cRow][cCol]) continue;
            
            visited[cRow][cCol] = true;
            cells.add(cell);
            
            // add orthogonally adjacent unassigned cells to openCells
            if (cRow + 1 < DIMENSION && !visited[cRow + 1][cCol]) openCells.add(new Cell(cRow + 1, cCol));
            if (cCol + 1 < DIMENSION && !visited[cRow][cCol + 1]) openCells.add(new Cell(cRow, cCol + 1));
        }
        
        // no operation (cell = 1)
        char operation = ' ';

        // generate an operation based on the number of cells
        switch (cells.size()) {
            case 2: operation = RANDOM.nextBoolean() ? '-' : '/';
            case 3, 4: operation = RANDOM.nextBoolean() ? '+' : '*';
        }

        int target = computeTarget(cells, operation); 
        
        // if target is not int and target = 0
        if (target == -1) {
            operation = RANDOM.nextBoolean() ? '+' : '*';
            target = computeTarget(cells, operation);
        } 

        // if target is too big (mostly due to 4 cell multiplication)
        if (target > 60) {
            operation = '+';
            target = computeTarget(cells, operation);
        }  

        cages.add(new Cage(cells, target, operation, color));
        
    }
        
    private int computeTarget(List<Cell> cells, char operation) {
        // set first cell as target
        // target has to be float initially to check non-integer quotients
        float target = grid[cells.get(0).row()][cells.get(0).col()];

        // iterate through all cells starting from second cell
        // while performing operation
        for (int i = 1; i < cells.size(); i++) {
            int value = grid[cells.get(i).row()][cells.get(i).col()];
            target = performArithmetic(target, value, operation); 
        }
        
         // check if non-integer quotient or = 0
        if (target - (int) target != 0 || target == 0) {
            return -1;
        }

        return (int) target;
    }

    private float performArithmetic(float num1, int num2, char operation) {
        return switch (operation) {
            case '+' -> num1 + num2;
            case '-' -> num1 < num2 ? num2 - num1 : num1 - num2;
            case '*' -> num1 * num2;
            case '/' -> num1 < num2 ? num2 / num1 : num1 / num2;
            default -> -1;
        };
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public List<Cage> getCages() {
        return this.cages;
    }


}
