package algorangers.models;

public class Cell {
  private int row, col, cageIndex;
  
  public Cell(int row, int col, int cageIndex) {
    this.row = row;
    this.col = col;
    this.cageIndex = cageIndex;
  }

  public int getRow() {
    return this.row;
  }
  
  public int getColumn() {
    return this.col;
  }

  public int cageIndex() {
    return this.cageIndex;
  }
}
