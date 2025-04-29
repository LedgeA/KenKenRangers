package algorangers.kenkenrangers.models;

public class Cell {
  private int row, col;
  
  public Cell(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public int row() {
    return this.row;
  }
  
  public int col() {
    return this.col;
  }
  
}
