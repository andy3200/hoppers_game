package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Create configuration for the hopper puzzle. Has constructor that reads files and a copy constructor. Generates valid
 * neighbors for given chess piece. Has a isSolution, hashcode, equals and toString method.
 * @author Andy Zheng Zheng
 * @github arz4166
 */


public class HoppersConfig implements Configuration{
    /** rows of grid **/
    public static int rows;
    /** columns of grid **/
    public static int columns;
    /** grid of grid **/
    public String [][] grid;
    /** the cursor for row **/
    public int rowcursor;
    /** the cursor for column **/
    public int columncursor;
    /** number of greenfrogs left **/
    private int greenFrogs;

    /**
     * constructor for hoppers config that reads in the file and creates the grid
     * @param filename the file name
     * @throws IOException if file not found
     */
    public HoppersConfig(String filename) throws IOException {
        try(BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line = in.readLine();
            String[] fields = line.split("\\s+");
            rows = Integer.parseInt(fields[0]);
            columns = Integer.parseInt(fields[1]);
            //creating the grid
            grid = new String[rows][columns];
            greenFrogs = 0;
            // filling the grid
            for(int CurrentRow = 0 ; CurrentRow < rows ; CurrentRow++){
                line = in.readLine();
                String[] lineSplit = line.split("\\s+");
                for(int CurrentColumn = 0 ; CurrentColumn < columns ; CurrentColumn++){
                    grid[CurrentRow][CurrentColumn] = lineSplit[CurrentColumn];
                    if(lineSplit[CurrentColumn].equals("G")){
                        greenFrogs++;
                    }
                }
            }
            rowcursor = 0;
            columncursor = 0;
        }catch (IOException ioe) {
            System.err.println("File not found");

        }
    }

    /**
     * copy constructor
     * @param other the config you want to copy
     * @param destrow the row of the destination
     * @param destcol the col of the destination
     * @param passrow  the row that your hopper passes through
     * @param passcol the column that your hopper passes through
     */
    public HoppersConfig(HoppersConfig other,int destrow, int destcol, int passrow, int passcol){
        this.rowcursor = other.rowcursor;
        this.columncursor = other.columncursor;
        this.greenFrogs = other.greenFrogs;
        this.grid = new String[rows][columns];
        for (int currentRow = 0 ; currentRow < rows ; currentRow++){
            System.arraycopy(other.grid[currentRow],0,this.grid[currentRow],0,columns);
        }
        if(this.grid[rowcursor][columncursor].equals("R")){
            this.grid[destrow][destcol] = "R";
        }
        else{
            this.grid[destrow][destcol] = "G";
        }
        this.grid[rowcursor][columncursor] = ".";
        this.grid[passrow][passcol] = ".";
        this.greenFrogs = greenFrogs - 1;
    }

    /**
     * whether the config is the solution
     * @return yes if the config is the solution and no otherwise
     */
    @Override
    public boolean isSolution() {
        return greenFrogs == 0;
    }

    /**
     * get the neighbors for the current configuration
     * @return a collection of neighbors of the current configuration
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        for(int CurrentRow = 0 ; CurrentRow < rows ; CurrentRow++){
            for(int CurrentColumn = 0 ; CurrentColumn < columns ; CurrentColumn++){
                if(grid[CurrentRow][CurrentColumn].matches("[a-zA-Z]+")){
                    rowcursor = CurrentRow;
                    columncursor = CurrentColumn;
                    if((columncursor % 2 ==0) && (rowcursor %2 ==0)){
                        //horizontal right
                        if(columncursor + 4 < columns){
                            int col = columncursor + 4;
                            String dest = grid[rowcursor][col];
                            String pass = grid[rowcursor][col-2];
                            if((checkLanding(dest)) && (checkPass(pass))){
                                neighbors.add(new HoppersConfig(this, rowcursor, col, rowcursor,col-2));

                            }

                        }
                    }
                    if((columncursor % 2 ==0) && (rowcursor %2 ==0)){
                        //horizontal left
                        if(columncursor - 4 >= 0){
                            int col = columncursor - 4;
                            String dest = grid[rowcursor][col];
                            String pass = grid[rowcursor][col+2];
                            if((checkLanding(dest)) && (checkPass(pass))){
                                neighbors.add(new HoppersConfig(this, rowcursor, col, rowcursor,col+2));
                            }
                        }
                    }
                    if((columncursor % 2 ==0) && (rowcursor %2 ==0)){
                        //vertical top
                        if(rowcursor - 4 >= 0){
                            int row = rowcursor - 4;
                            String dest = grid[row][columncursor];
                            String pass = grid[row+2][columncursor];
                            if((checkLanding(dest)) && (checkPass(pass))){
                                neighbors.add(new HoppersConfig(this, row, columncursor, row+2,columncursor));
                            }
                        }
                    }
                    if((columncursor % 2 ==0) && (rowcursor %2 ==0)) {
                        //vertical bottom
                        if (rowcursor + 4 < rows) {
                            int row = rowcursor + 4;
                            String dest = grid[row][columncursor];
                            String pass = grid[row - 2][columncursor];
                            if ((checkLanding(dest)) && (checkPass(pass))) {
                                neighbors.add(new HoppersConfig(this, row, columncursor, row - 2, columncursor));
                            }

                        }
                    }
                    //top right
                    if((rowcursor -2 >= 0) && (columncursor + 2 < columns)){
                        int row = rowcursor-2;
                        int col = columncursor +2;
                        String dest = grid[row][col];
                        String pass = grid[row+1][col-1];
                        if((checkLanding(dest)) && (checkPass(pass))){
                            neighbors.add(new HoppersConfig(this, row, col, row+1,col-1));
                        }

                    }
                    //top left
                    if((rowcursor -2 >= 0) && (columncursor - 2 >= 0)){
                        int row = rowcursor-2;
                        int col = columncursor -2;
                        String dest = grid[row][col];
                        String pass = grid[row+1][col+1];
                        if((checkLanding(dest)) && (checkPass(pass))){
                            neighbors.add(new HoppersConfig(this, row, col, row+1,col+1));
                        }


                    }
                    //bottom right
                    if((rowcursor + 2 < rows) && (columncursor + 2 < columns)){
                        int row = rowcursor + 2;
                        int col = columncursor + 2;
                        String dest = grid[row][col];
                        String pass = grid[row-1][col-1];
                        if((checkLanding(dest)) && (checkPass(pass))){
                            neighbors.add(new HoppersConfig(this, row, col, row-1,col-1));
                        }

                    }
                    //bottom left
                    if((rowcursor + 2 < rows) && (columncursor - 2 >= 0)){
                        int row = rowcursor + 2;
                        int col = columncursor - 2;
                        String dest = grid[row][col];
                        String pass = grid[row-1][col+1];
                        if((checkLanding(dest)) && (checkPass(pass))){
                            neighbors.add(new HoppersConfig(this, row, col, row-1,col+1));
                        }
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * checks whether the position that was pass through contains a Green frog
     * @param Pass what the position that was pass through contains
     * @return true if it contains a green frog
     */
    private boolean checkPass(String Pass ){
        return Pass.equals("G");

    }

    /**
     * whether the place the frog is jumping to is valid
     * @param destination  what the destination contains. if it contains "." then it's valid
     * @return true if the destination contains ".". false otherwise.
     */
    private boolean checkLanding(String destination){
        return destination.equals(".");
    }

    /**
     * get the hashcode for the configuration's grid
     * @return the configuration's grid hashcode.
     */
    @Override
    public int hashCode(){
        return Arrays.deepHashCode(this.grid);
    }

    public String toString() {
        StringBuilder gridstring = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                gridstring.append(grid[row][col]);
                if (col != columns - 1) {
                    gridstring.append(" ");
                }
            }
            gridstring.append("\n");
        }
        return gridstring.toString();
    }

    /**
     *  override equals to compare two objects.
     * @param other the other object
     * @return true if two objects are equal
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof HoppersConfig) {
            HoppersConfig OtherHopper = (HoppersConfig) other;
            return Arrays.deepEquals(this.grid,OtherHopper.grid);
        }
        return result;
    }

    /**
     * this prints the grid out but also showing rows and columns for the PTUI
     */
    public void PTUIGRID() {
        System.out.print("   ");
        // prints the number of cols horizontally
        for (int col = 0; col < columns; col++) {
            System.out.print(col + " ");
        }
        System.out.println("");
        System.out.print("   ");
        for (int col = 0; col < columns* 2; col++) {
            System.out.print("-");
        }
        System.out.println("");

        //prints the number of rows vertically
        for (int row = 0; row < rows; row++) {
            System.out.print(row  + "| ");
            for (int col = 0; col < columns; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }


}
