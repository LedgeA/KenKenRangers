package algorangers.kenkenrangers.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class PuzzleController {
    @FXML
    GridPane puzzleGridPane;
    
    Cell[][] cells;
    List<Cage> cages;
    int size = 4;

    Random random;

    public PuzzleController() {
        initializeCells();
        generateSolutionSet();
        printBoard();
        
    }

    private void createCages() {
        boolean[][] used = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!used[i][j]) {
                    createRandomCage(i, j);
                }
            }
        }
    }

    private void createRandomCage(int row, int col) {
        int i = 0;
        this.cells[row][col].cageNumber = cages.size();

        int cageSize = random.nextInt(3) + 1;
        // while (i < cageSize) {
        //     int[] lastCell = cells.get(random.nextInt(cells.size()));
        //     int newRow = lastCell[0] + (random.nextBoolean() ? 1 : -1);
        //     int newCol = lastCell[1] + (random.nextBoolean() ? 1 : -1);

        //     if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && !used[newRow][newCol]) {
        //         cells.add(new int[]{newRow, newCol});
        //         used[newRow][newCol] = true;
        //     }
        // }

        cages.add(new Cage());
    }

    void initializeCells() {
        this.cells = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.cells[i][j] = new Cell(i, j, 0);
            }
        }
    }

    void generateSolutionSet() {
        fillCells(0, 0);
    }

    boolean fillCells(int row, int col) {

        if (row == size) return true;
        if (col == size) return fillCells(row + 1, 0);

        List<Integer> numbers = generateRandomNumbers();

        for (int num : numbers) {
            if (isSafe(row, col, num)) {
                cells[row][col].value = num;
                if (fillCells(row, col + 1)) return true;
                cells[row][col].value = 0;
            }
        }

        return false;
    }

    boolean isSafe(int row, int col, int num) {
        for (int i = 0; i < size; i++) {
            if (cells[row][i].value == num || cells[i][col].value == num) return false;
        }

        return true;
    }

    List<Integer> generateRandomNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers, new Random());

        return numbers;
    }

    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print((cells[i][j].value == 0 ? " . " : " " + cells[i][j].value + " "));
            }
            System.out.println();
        }
    }


    static class Cage {

        char operator;
        int result;
        int size;
        List<Integer> row, col;

        Random random;
        Cage() {
            List<Integer> row = new ArrayList<>();
            List<Integer> col = new ArrayList<>();
            
            generateRandomOperator();
        }

        void generateRandomOperator() {
            switch (random.nextInt(1, 3)) {
                case 1 -> {
                    this.operator = '+';
                    this.size = random.nextInt(3, 5);
                }
                case 2 -> {
                    this.operator = '-';
                    this.size = 2;
                }
                case 3 -> {
                    this.operator = '*';
                    this.size = 3;
                }
                case 4 -> {
                    this.operator = '/';
                    this.size = 2;
                }
            
            }
        }

        int performArithmetic(int operandOne, int operandTwo) {
            switch (this.operator) {
                case '+' -> {
                    return operandOne + operandTwo;
                }
                case '-' -> {
                    return operandOne - operandTwo;
                }
                case '*' -> {
                    return operandOne * operandTwo;
                }
                case '/' -> {
                    float result = operandOne / operandTwo;
                    
                    // return if the result is an integer
                    if ((int) result == operandOne / operandTwo) {
                        return operandOne / operandTwo;
                    }

                    this.operator = '-';

                    return -1;
                }
            }

            return -1;
        }

        void addCellPosition(int row, int col) {
            this.row.add(row);
            this.col.add(col);
        }

    }

    static class Cell {
        int row, col, value = 0, cageNumber = -1;

        Cell(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

}