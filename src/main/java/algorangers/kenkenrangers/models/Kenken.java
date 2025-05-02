package algorangers.kenkenrangers.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Kenken {
    private final int DIMENSION;
    private final int[][] grid;
    private final List<Cage> cages = new ArrayList<>();

    List<String> colors = new ArrayList<>(Arrays.asList(
        "#FF5733", "#33FF57", "#5733FF", "#FF33A1", "#A1FF33", 
        "#33A1FF", "#FF9E33", "#9E33FF", "#33FF9E", "#9EFF33", 
        "#FF33FF", "#33FFFF", "#FF5733", "#5733FF", "#FF33A1", 
        "#A1FF33", "#33A1FF", "#FF9E33", "#9E33FF", "#33FF9E", 
        "#9EFF33", "#FF33FF", "#33FFFF", "#FF5733", "#5733FF", 
        "#FF33A1", "#A1FF33", "#33A1FF", "#FF9E33", "#9E33FF"
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
    
    private void generateCages() {
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
        
        char operation = ' ';

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

        // if product is too large and size = 4
        if (target > 60 && cells.size() == 4) {
            operation = '+';
            target = computeTarget(cells, operation);
        }  

        cages.add(new Cage(cells, target, operation, color));
        
    }
        
    private int computeTarget(List<Cell> cells, char operation) {
        float target = grid[cells.get(0).row()][cells.get(0).col()];

        for (int i = 1; i < cells.size(); i++) {
            int value = grid[cells.get(i).row()][cells.get(i).col()];
            target = performArithmetic(target, value, operation); 
        }
        
         // check if !int or = 0
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
