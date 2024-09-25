package dev.stephenpearson.blockify.main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Board {
    private int[][] grid;
    private int width;
    private int height;


    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new int[height][width];
        // Initialize grid to 0 (empty)
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], 0);
        }
    }


    public boolean isValidPosition(Point[] coords, int x, int y) {
        for (Point p : coords) {
            int newX = x + p.x;
            int newY = y + p.y;
            if (newX < 0 || newX >= width || newY >= height) {
                return false;
            }
            if (newY >= 0 && grid[newY][newX] != 0) {
                return false;
            }
        }
        return true;
    }


    public void fixPiece(Tetromino piece) {
        for (Point p : piece.coordinates) {
            int x = piece.position.x + p.x;
            int y = piece.position.y + p.y;
            if (x >= 0 && x < width && y >= 0 && y < height) {
                grid[y][x] = piece.type + 1;
            }
        }
    }


    public List<Integer> clearFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            boolean isFull = true;
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullLines.add(i);
            }
        }

     
        for (int line : fullLines) {
            for (int i = line; i > 0; i--) {
                grid[i] = Arrays.copyOf(grid[i - 1], width);
            }
    
            Arrays.fill(grid[0], 0);
        }

        return fullLines;
    }

  
    public int[][] getGrid() {
        return grid;
    }


    public void reset() {
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], 0);
        }
    }
}
