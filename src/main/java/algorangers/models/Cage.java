package algorangers.models;

import java.util.Arrays;
import java.util.List;

public class Cage {
  List<int[]> cells;
  int target;
  char operation;
  String color;
  
    public Cage(List<int[]> cells, int target, char operation, String color) {
        this.cells = cells;
        this.target = target;
        this.operation = operation;
        this.color = color;

    }

    public boolean areCageEntriesAreValid(int[][] solutionGrid, int[][] inputGrid) {
        int[][] inputGridCopy = Arrays.stream(inputGrid)
                              .map(int[]::clone)
                              .toArray(int[][]::new);

        for (int i = 0; i < cells.size(); i++) {

            int row = this.cells.get(i)[0];
            int col = this.cells.get(i)[1];

            int inputValue = inputGridCopy[row][col];
            int solutionValue = inputGridCopy[row][col];

            System.out.print("|Input Val: " + inputValue + " Sol Val: " + solutionValue); 
            if (solutionGrid[row][col] != inputGridCopy[row][col]) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<int[]> getCells() {
        return this.cells;
    }

    public String getColor() {
        return this.color;
    }

    public int getTarget() {
        return this.target;
    }

    public int getOperation() {
        return this.operation;
    }

    private String cellsToString() {
        String cells = "[";

        for (int[] cell : this.cells) {
            cells += "[" + cell[0] + ", " + cell[1] + "],";
        }

        return cells + "] ";
    }
    
    @Override
    public String toString() {
        return "Cage: " + cellsToString() + " Target: " + this.target + " Operation: " + this.operation;
    }
}

