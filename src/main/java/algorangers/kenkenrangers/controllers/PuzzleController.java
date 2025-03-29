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
    
    int[][] solutionSet;
    int size = 4;

    public PuzzleController() {
        this.solutionSet = new int[size][size];
        generateSolutionSet();
        printBoard();
        
    }

    void generateSolutionSet() {
        fillSolutionSet(0, 0);
    }

    boolean fillSolutionSet(int row, int col) {

        if (row == size) return true;
        if (col == size) return fillSolutionSet(row + 1, 0);

        List<Integer> numbers = generateRandomNumbers();

        for (int num : numbers) {
            if (isSafe(row, col, num)) {
                solutionSet[row][col] = num;
                if (fillSolutionSet(row, col + 1)) return true;
                solutionSet[row][col] = 0;
            }
        }

        return false;
    }

    boolean isSafe(int row, int col, int num) {
        for (int i = 0; i < size; i++) {
            if (solutionSet[row][i] == num || solutionSet[i][col] == num) return false;
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
                System.out.print((solutionSet[i][j] == 0 ? " . " : " " + solutionSet[i][j] + " "));
            }
            System.out.println();
        }
    }


    static class Cage {

        Cell[] cells;
        char operator;
        int result;
        int size;

        Cage() {
            generateRandomOperator();
            this.cells = new Cell[size];
        }

        void generateRandomOperator() {
            switch ((int)(Math.random() * 4) + 1) {
                case 1 -> {
                    this.operator = '+';
                    this.size = (int)(Math.random() * 4) + 3;
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

        void computeForResult() {
            this.result = cells[0].value;

            for (int i = 1; i < size; i++) {
                this.result = performArithmetic(this.result, cells[i].value);
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

        void addCells(int row, int col, int value) {
            for (int i = 0; i < size; i++) {
                if (cells[i] == null) cells[i] = new Cell(row, col, value);
            }
        }

        Cell getCell(int row, int col) {
            for (int i = 0; i < size; i++) {
                if (cells[i].row == row && cells[i].col == col) return cells[i];
            }

            return null;
        }
    }

    static class Cell {
        int row, col, value;

        Cell(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

}