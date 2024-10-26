package puzzles.chess.model;

import puzzles.common.solver.Configuration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Has constructor that reads files and a copy constructor. Generates valid
 * neighbors for given chess piece. Has a isSolution, hashcode, equals and toString method.
 * @author KUNLIN WEN
 * @github KW9521
 */
public class ChessConfig implements Configuration {

    public static int horizontalRow; // dimensions
    public static int verticalCol; // dimensions
    public Character[][] grid;
    static char ROOK = 'R';
    static char BISHOP = 'B';
    static char QUEEN = 'Q';
    static char PAWN = 'P';
    static char KING = 'K';
    static char KNIGHT = 'N';
    static char EMPTY = '.';
    private int numberOfPieces;

    /**
     * Reads file, extract the number of rows and columns. Populates a 2D array and keeps track of how many chess
     * pieces are in this file. Sets current row and col position to 0
     * @param filename name of time to read
     * @throws IOException with message "File not Found"
     */
    public ChessConfig(String filename) throws IOException {
        try (BufferedReader line = new BufferedReader(new FileReader(filename))) {

            // reads first line
            String firstLine = line.readLine();
            String[] dimensions = firstLine.split(" ");
            horizontalRow = Integer.parseInt(dimensions[0]);
            verticalCol = Integer.parseInt(dimensions[1]);

            // initializes grid
            grid = new Character[horizontalRow][verticalCol];

            // populates the grid
            for (int i = 0; i < this.horizontalRow; i++) {
                String[] gridReading = line.readLine().split(" ");
                for (int j = 0; j < this.verticalCol; j++) {
                    grid[i][j] = gridReading[j].charAt(0);
                    if (gridReading[j].matches("[a-zA-Z]+")) {
                        numberOfPieces++;
                    }
                }
            }
        } catch (IOException ioe) {
            System.err.println("File not found");
        }
    }

    /**
     * copy constructor
     * @param other a copy of whatever is in other
     */
    private ChessConfig(ChessConfig other, char piece, int currentPositionRow, int currentPositionCol,
                        int capturePositionRow, int capturePositionCol) {

        // copies from other config
        this.horizontalRow = other.horizontalRow;
        this.verticalCol = other.verticalCol;
        this.numberOfPieces = other.numberOfPieces;
        this.numberOfPieces--;

        // creates copy of current grid
        this.grid = new Character[horizontalRow][verticalCol];
        for (int row = 0; row < horizontalRow; ++row) {
            for (int col = 0; col < verticalCol; ++col) {
                this.grid[row][col] = other.grid[row][col];
            }
        }

        //captures
        this.grid[currentPositionRow][currentPositionCol] = EMPTY;
        this.grid[capturePositionRow][capturePositionCol] = piece;
    }

    /**
     * if the number of pieces in current config is 1
     * @return true if numOfPiece is 1, false otherwise
     */
    @Override
    public boolean isSolution() {
        if (numberOfPieces == 1) {
            return true;
        }
        return false;
    }

    /**
     * helper function to generate all possible pawn moves, can only move top left or top right
     * @param row              current row position
     * @param col              current column position
     */
    public Collection<Configuration> pawnMoves(int row, int col) {
        List<Configuration> pawnSuccessors = new LinkedList<>();

        // top left
        if ((row - 1 >= 0) && (col - 1 >= 0) && (grid[row - 1][col - 1] != EMPTY)) {
            pawnSuccessors.add(new ChessConfig(this, PAWN, row, col, row - 1, col - 1));
        }

        // top right
        if ((row - 1 >= 0) && (col + 1 < verticalCol) && (grid[row - 1][col + 1] != EMPTY)) {
            pawnSuccessors.add(new ChessConfig(this, PAWN, row, col, row - 1, col + 1));
        }

        return pawnSuccessors;
    }

    /**
     * helper function to generate all possible moves for rooks, can go up down left right
     * @param row              current position's row
     * @param col              current position's column
     */
    public Collection<Configuration> rookMoves(int row, int col) {
        List<Configuration> rookSuccessors = new LinkedList<>();

        // keep track of start position
        int startRow = row;
        int startCol = col;

        // the capture's position
        int rowChange;
        int colChange;

        // generates valid neighbors in a spiral kinda way
        for (int direction = 0; direction < 4; direction++) {
            char piece = EMPTY;
            if (direction == 0) {           // down
                rowChange = 1;
                colChange = 0;
            } else if (direction == 1) {    // up
                rowChange = -1;
                colChange = 0;
            } else if (direction == 2) {    // right
                rowChange = 0;
                colChange = 1;
            } else {                        // left
                rowChange = 0;
                colChange = -1;
            }

            // checks if it's within bounds and if dest pos is a valid capture
            while ((row + rowChange >= 0) && (row + rowChange < horizontalRow) &&
                    (col + colChange >= 0) && (col + colChange < verticalCol))  {

                // goes to dest's row and col
                row = row + rowChange;
                col = col + colChange;
                piece = grid[row][col];
                if (piece != EMPTY) {
                    rookSuccessors.add(new ChessConfig(this, ROOK, startRow, startCol, row, col));
                    break;
                }
            }

            // resets it back to initial pos
            row = startRow;
            col = startCol;
        }
        return rookSuccessors;
    }

    /**
     * helper function to generate all possible bishop moves, can only move diagonal
     * @param row              current row position
     * @param col              current column position
     */
    public Collection<Configuration> bishopMoves(int row, int col) {
        List<Configuration> bishopSuccessors = new LinkedList<>();

        // keep track of start position
        int startRow = row;
        int startCol = col;

        // keeps track of capture's pos
        int rowChange;
        int colChange;

        // generates neighbors in a spiral kinda way
        for (int direction = 0; direction < 4; direction++) {
            char piece = EMPTY;
            if (direction == 0) {       // bottom right
                rowChange = 1;
                colChange = 1;
            } else if (direction == 1) { // top right
                rowChange = -1;
                colChange = 1;
            } else if (direction == 2) { // bottom left
                rowChange = 1;
                colChange = -1;
            } else {                    // top left
                rowChange = -1;
                colChange = -1;
            }

            while ((row + rowChange >= 0) && (row + rowChange < horizontalRow) &&
                    (col + colChange >= 0) && (col + colChange < verticalCol)) {

                // goes to dest's row and col
                row = row + rowChange;
                col = col + colChange;
                piece = grid[row][col];
                if (piece != EMPTY) {
                    bishopSuccessors.add(new ChessConfig(this, BISHOP, startRow, startCol, row, col));
                    break;
                }
            }

            // resets it back to original pos
            row = startRow;
            col = startCol;
        }
        return bishopSuccessors;
    }

    /**
     * helper function to generate all possible queen moves, can move like bishop and rook
     * @param row              current row position
     * @param col              current column position
     */
    public Collection<Configuration> queenMoves(int row, int col) {
        List<Configuration> queenSuccessors = new LinkedList<>();

        int startRow = row;
        int startCol = col;
        int rowChange;
        int colChange;

        for (int direction = 0; direction < 8; direction++) {
            char piece = EMPTY;
            if (direction == 0) {           // right
                rowChange = 0;
                colChange = 1;
            } else if (direction == 1) {    // top
                rowChange = -1;
                colChange = 0;
            } else if (direction == 2) {    // bottom
                rowChange = 1;
                colChange = 0;
            } else if (direction == 3) {    // left
                rowChange = 0;
                colChange = -1;
            } else if (direction == 4) {    // bottom right
                rowChange = 1;
                colChange = 1;
            } else if (direction == 5) {    // top right
                rowChange = -1;
                colChange = 1;
            } else if (direction == 6) {    // bottom left
                rowChange = 1;
                colChange = -1;
            } else {                        // top left
                rowChange = -1;
                colChange = -1;
            }

            while ((row + rowChange >= 0) && (row + rowChange < horizontalRow) &&
                    (col + colChange >= 0) && (col + colChange < verticalCol)) {
                row = row + rowChange;
                col = col + colChange;
                piece = grid[row][col];
                if (piece != EMPTY) {
                    queenSuccessors.add(new ChessConfig(this, QUEEN, startRow, startCol, row, col));
                    break;
                }
            }

            row = startRow;
            col = startCol;
        }
        return queenSuccessors;
    }

    /**
     * helper function to generate all possible king moves, can only 1 around itself
     * @param row              current row position
     * @param col              current column position
     */
    public Collection<Configuration> kingMoves(int row, int col) {
        List<Configuration> kingSuccessors = new LinkedList<>();

        int rowChange;
        int colChange;

        for (int direction = 0; direction < 8; direction++) {
            if (direction == 0) {           // bottom
                rowChange = 1;
                colChange = 0;
            } else if (direction == 1) {    // top
                rowChange = -1;
                colChange = 0;
            } else if (direction == 2) {    // right
                rowChange = 0;
                colChange = 1;
            } else if (direction == 3) {    // left
                rowChange = 0;
                colChange = -1;
            } else if (direction == 4) {    // bottom right
                rowChange = 1;
                colChange = 1;
            } else if (direction == 5) {    // top right
                rowChange = -1;
                colChange = 1;
            } else if (direction == 6) {    // bottom left
                rowChange = 1;
                colChange = -1;
            } else {                        // top left
                rowChange = -1;
                colChange = -1;
            }

            if ((row + rowChange >= 0) && (row + rowChange < horizontalRow) &&
                (col + colChange >= 0) && (col + colChange < verticalCol) &&
                (grid[row + rowChange][col + colChange] != EMPTY)) {
                    kingSuccessors.add(new ChessConfig(this, KING, row, col, row + rowChange, col + colChange));
            }
        }
        return kingSuccessors;
    }

    /**
     * helper function to generate all possible knight moves
     * @param row              current row position
     * @param col              current column position
     */
    public Collection<Configuration> knightMoves(int row, int col) {
        List<Configuration> knightSuccessors = new LinkedList<>();

        int rowChange;
        int colChange;

        for (int direction = 0; direction < 8; direction++) {
            if (direction == 0) {           // bottom right sideways L
                rowChange = 1;
                colChange = 2;
            } else if (direction == 1) {    // bottom right reg L
                rowChange = 2;
                colChange = 1;
            } else if (direction == 2) {    // top right reg L
                rowChange = -2;
                colChange = 1;
            } else if (direction == 3) {    // bottom left reg L
                rowChange = 2;
                colChange = -1;
            } else if (direction == 4) {    // top left reg L
                rowChange = -2;
                colChange = -1;
            } else if (direction == 5) {    // top right sideways L
                rowChange = -1;
                colChange = 2;
            } else if (direction == 6) {    // bottom left sideways L
                rowChange = 1;
                colChange = -2;
            } else {                        // top left sideways L
                rowChange = -1;
                colChange = -2;
            }

            if ((row + rowChange >= 0) && (row + rowChange < horizontalRow) &&
                    (col + colChange >= 0) && (col + colChange < verticalCol) &&
                    (grid[row + rowChange][col + colChange] != EMPTY)) {
                knightSuccessors.add(new ChessConfig(this, KNIGHT, row, col, row + rowChange, col + colChange));
            }
        }
        return knightSuccessors;
    }


    /**
     * Goes thru entire 2d array, calls helper methods makeMoves() and validCaptures() to generate valid neighbors
     * @return a LinkedHashSet of valid neighbor configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new LinkedList<>();

        for (int i = 0; i < this.horizontalRow; i++) { // loops thru row
            for (int j = 0; j < this.verticalCol; j++) { // loops thru col
                if (grid[i][j] != EMPTY) {
                    Character currentPiece = grid[i][j];

                    if (currentPiece == KNIGHT) {
                        Collection<Configuration> knightNeighbors = knightMoves(i, j);
                        neighbors.addAll(knightNeighbors);
                    } else if (currentPiece == PAWN) {
                        Collection<Configuration> pawnNeighbors = pawnMoves(i, j);
                        neighbors.addAll(pawnNeighbors);
                    } else if (currentPiece == QUEEN) {
                        Collection<Configuration> queenNeighbors = queenMoves(i, j);
                        neighbors.addAll(queenNeighbors);
                    } else if (currentPiece == KING) {
                        Collection<Configuration> kingNeighbors = kingMoves(i, j);
                        neighbors.addAll(kingNeighbors);
                    } else if (currentPiece == BISHOP) {
                        Collection<Configuration> bishopNeighbors = bishopMoves(i, j);
                        neighbors.addAll(bishopNeighbors);
                    } else if (currentPiece == ROOK) {
                        Collection<Configuration> rookNeighbors = rookMoves(i, j);
                        neighbors.addAll(rookNeighbors);
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Checks if this configuration's grid is equal to other configuration's grid
     * @param other the configuration being compared to
     * @return true if both grid r the same, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ChessConfig) {
            ChessConfig chessConfigOther = (ChessConfig) other;
            return Arrays.deepEquals(this.grid, chessConfigOther.grid);
        }
        return false;
    }

    /**
     * gets the deep hash code of this configuration's grid
     * @return this config's grid's hashcode
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }

    /**
     * prints the config's grid
     * formatted like:
     * . . P
     * N B .
     * @return a row x column string of the grid
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < horizontalRow; i++) {
            for (int j = 0; j < verticalCol; j++) {
                sb.append(grid[i][j]);
                if (j != verticalCol - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * displays the 2d array with numbers on the side
     * @return basically a toString but with extra info
     */
    public void modelGrid() {
        System.out.print("  ");
        for (int i = 0; i < verticalCol; i++) {
            System.out.print(i + " ");
        }

        System.out.println();
        System.out.print("   ");
        for (int col = 0; col < verticalCol* 2; col++) {
            System.out.print("-");
        }
        System.out.println();

        for (int i = 0; i < horizontalRow; i++) {
            System.out.print(i  + "| ");
            for (int j = 0; j < verticalCol; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

    }

    /**
     * decreases the number of pieces on the board by 1
     */
    public void decreaseOnce(){
        numberOfPieces--;
    }

    /**
     * @return the number of pieces left in current config
     */
    public int getNumberOfPieces(){
        return numberOfPieces;
    }

}