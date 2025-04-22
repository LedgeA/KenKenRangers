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

    private final List<String> colors = new ArrayList<>(
        Arrays.asList(
            "#EB6000", "#D9232E", "#FFA300", "#8F39D1", "#00BB3E", "#1993DC", "#FFC0CB", "#FFD700", "#DC143C"
        ));

    private final Random RANDOM = new Random();
    
    public Kenken(int DIMENSION) {
        this.DIMENSION = DIMENSION;
        this.grid = new int[DIMENSION][DIMENSION];

        generateGrid();
        generateCages();

    }
    
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
        List<Character> operations = Arrays.asList('+', '-', '*', '/');       

        for (int r = 0; r < DIMENSION; r++) {
            for (int c = 0; c < DIMENSION; c++) {
                setCell(visited, operations, r, c);
            }
        }
    }

    private void setCell(boolean[][] visited, List<Character> operations, int r, int c) {
        if (!visited[r][c]) {
            List<Cell> cells = new ArrayList<>();
            Queue<Cell> queue = new LinkedList<>();
            queue.add(new Cell(r, c, this.cages.size()));
            String color = this.colors.get(RANDOM.nextInt(colors.size()));
            this.colors.remove(colors.indexOf(color)); 

            while (!queue.isEmpty() && cells.size() < RANDOM.nextInt(DIMENSION) + 1) {
                Cell cell = queue.poll();
                int row = cell.getRow(), col = cell.getColumn();
                if (visited[row][col]) continue;
                
                visited[row][col] = true;
                cells.add(cell);
                
                if (row + 1 < DIMENSION && !visited[row + 1][col]) queue.add(new Cell(row + 1, col, this.cages.size()));
                if (col + 1 < DIMENSION && !visited[row][col + 1]) queue.add(new Cell(row, col + 1, this.cages.size()));
            }
            
            char operation = cells.size() > 1 ? operations.get(RANDOM.nextInt(operations.size())): ' ';
            int target = computeTarget(cells, operation);

            // If target is not an integer, switch operation to subtraction and recompute
            if (target == -404) {
                operation = '-'; 
                target = computeTarget(cells, operation);
            } 

            // If target is equal or less than zero, switch operation to either addition or multiplication then recompute
            if (target <= 0) {
                operation = RANDOM.nextBoolean() ? '+' : '*';
                target = computeTarget(cells, operation);
            } 

            cages.add(new Cage(cells, target, operation, color));
        }
    }
    
    private int computeTarget(List<Cell> cells, char operation) {
        float target = grid[cells.get(0).getRow()][cells.get(0).getColumn()];
        for (int i = 1; i < cells.size(); i++) {
            int value = grid[cells.get(i).getRow()][cells.get(i).getColumn()];
            target = performArithmetic(target, value, operation); 
        }

        // If target is not an integer (due to division)
        if (target - (int) target != 0) {
            return -404;
        }

        return (int) target;
    }

    private float performArithmetic(float num1, int num2, char operation) {
        switch (operation) {
            case '+' -> { return num1 + num2; } 
            case '-' -> { return num1 < num2 ? num2 - num1 : num1 - num2; }
            case '*' -> { return num1 * num2; }
            case '/' -> { return num1 < num2 ? num2 / num1 : num1 / num2; }  
        }

        return -1;
    }
    
    public void printGrid() {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }
    
    public void printCages() {
        for (Cage cage : cages) {
            System.out.println(cage);
        }
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public List<Cage> getCages() {
        return this.cages;
    }


}
