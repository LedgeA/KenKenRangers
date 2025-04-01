package algorangers.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Grid {
    private final int size;
    private final int[][] grid;
    private final Random random = new Random();
    private final List<Cage> cages = new ArrayList<>();
    private List<String> colors = new ArrayList<>(
        Arrays.asList(
            "#EB6000", "#D9232E", "#FFA300", "#8F39D1", "#00BB3E", "#1993DC", "#FFC0CB", "#FFD700", "#DC143C"
        ));

    
    public Grid(int size) {
        this.size = size;
        this.grid = new int[size][size];
        generateGrid();
        generateCages();

        printGrid();
        printCages();
    }
    
    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < size; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return false;
            }
        }
        return true;
    }
    
    private boolean fillGrid(int row, int col) {
        if (row == size) return true;
        if (col == size) return fillGrid(row + 1, 0);
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= size; i++) numbers.add(i);
        Collections.shuffle(numbers, random);
        
        for (int num : numbers) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(row, col + 1)) return true;
                grid[row][col] = 0;
            }
        }
        return false;
    }
    
    private void generateGrid() {
        fillGrid(0, 0);
    }
    
    private void generateCages() {
        boolean[][] visited = new boolean[size][size];
        List<Character> operations = Arrays.asList('+', '-', '*', '/');       

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (!visited[r][c]) {
                    List<int[]> cells = new ArrayList<>();
                    Queue<int[]> queue = new LinkedList<>();
                    queue.add(new int[]{r, c});
                    String color = this.colors.get(random.nextInt(colors.size() - 1));
                    this.colors.remove(colors.indexOf(color)); 

                    while (!queue.isEmpty() && cells.size() < random.nextInt(this.size) + 1) {
                        int[] cell = queue.poll();
                        int row = cell[0], col = cell[1];
                        if (visited[row][col]) continue;
                        
                        visited[row][col] = true;
                        cells.add(cell);
                        
                        if (row + 1 < size && !visited[row + 1][col]) queue.add(new int[]{row + 1, col});
                        if (col + 1 < size && !visited[row][col + 1]) queue.add(new int[]{row, col + 1});
                    }
                    
                    char operation = cells.size() > 1 ? operations.get(random.nextInt(operations.size())): ' ';
                    int target = computeTarget(cells, operation);
                    // If target is not an integer, switch operation to subtraction and recompute
                    if (target == -404) {
                        operation = '-';
                        target = computeTarget(cells, operation);
                    // If target is equal or less than zero, switch operation to either addition or multiplication then recompute
                    } else if (target <= 0) {
                        operation = random.nextBoolean() ? '+' : '*';
                        target = computeTarget(cells, operation);
                    } 

                    cages.add(new Cage(cells, target, operation, color));
                }
            }
        }
    }
    
    private int computeTarget(List<int[]> cells, char operation) {
        float target = grid[cells.get(0)[0]][cells.get(0)[1]];
        for (int i = 1; i < cells.size(); i++) {
            int value = grid[cells.get(i)[0]][cells.get(i)[1]];
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

    public int[][] getGrid() {
        return grid;
    }

    public List<Cage> getCages() {
        return this.cages;
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
  
}
