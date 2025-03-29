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
        int size;

        Cage() {
            
        }
        
    }

    static class Cell {
        private int row, col, value;
    }

}