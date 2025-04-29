package algorangers.kenkenrangers.models;

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

            int row = this.cells.get(i).row();
            int col = this.cells.get(i).col();

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

    public char getOperation() {
        return this.operation;
    }

}

