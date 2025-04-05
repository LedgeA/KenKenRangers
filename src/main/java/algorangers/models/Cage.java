package algorangers.models;

import java.util.List;

public class Cage {
  List<Cell> cells;
  int target;
  char operation;
  String color;
  
    public Cage(List<Cell> cells, int target, char operation, String color) {
        this.cells = cells;
        this.target = target;
        this.operation = operation;
        this.color = color;

    }

    public boolean areCageEntriesAreValid(int[][] solutionGrid, int[][] inputGrid) {
        for (int i = 0; i < cells.size(); i++) {

            int row = this.cells.get(i).getRow();
            int col = this.cells.get(i).getColumn();

            if (solutionGrid[row][col] != inputGrid[row][col]) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<Cell> getCells() {
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

        for (Cell cell : this.cells) {
            cells += "[" + cell.getRow() + ", " + cell.getColumn() + "],";
        }

        return cells + "] ";
    }
    
    @Override
    public String toString() {
        return "Cage: " + cellsToString() + " Target: " + this.target + " Operation: " + this.operation;
    }
}

