package puzzles.chess.model;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.*;
import java.util.*;

/**
 * the model part of the chess puzzle, has the logic behind load() reset() hint()
 * @author KUNLIN WEN
 * @github KW9521
 */
public class ChessModel{
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private static ChessConfig currConfig;

    public enum GameState {
        LOADED, FAILED_LOAD, SELECTED, CAPTURED, CANT_CAPTURE, NEXT_STEP, RESET_PUZZLE, SOLUTION, NO_SOLUTION
    }
    public static final EnumMap<GameState, String> STATE_MSGS = new EnumMap<>(Map.of(
            GameState.LOADED, "Loaded: ",
            GameState.FAILED_LOAD, "Failed To Load: ",
            GameState.SELECTED, "Selected: ",
            GameState.CAPTURED, "Captured From: ",
            GameState.CANT_CAPTURE, "Can't Capture From ",
            GameState.NEXT_STEP, "Next Step!",
            GameState.RESET_PUZZLE, "Puzzle reset!",
            GameState.SOLUTION, "Solution!",
            GameState.NO_SOLUTION, "No Solution!"
    ));

    private GameState gameState;

    /** absolute path **/
    private static String FILENAME;

    /** not abs path, just filename**/
    private static String justFileName;
    public int rowDim;
    public int colDim;
    private int cPos;
    private int rPos;

    /**
     * calls the loadFile() and tries to load file
     * if fail loads, sets file to wtv the last file to be loaded is
     * @param filename file to be loaded
     * @throws IOException "Fail to load" + file name
     */
    public ChessModel(String filename) throws IOException {
        try{
            loadFile(filename);
            alertObservers(STATE_MSGS.get(GameState.LOADED) + filename);
        }
        catch (RuntimeException e){
            alertObservers(STATE_MSGS.get(GameState.FAILED_LOAD) + filename);
            System.out.println("Failed to load: "+ filename);
            currConfig = new ChessConfig(FILENAME);
            currConfig.modelGrid();
        }

    }

    /**
     * tries to load the file
     * @param filename file to be loaded
     */
    public void loadFile(String filename)  {
        try(BufferedReader in = new BufferedReader(new FileReader(filename))){
            FILENAME = filename;
            justFileName = new File(filename).getName();
            System.out.println("Loaded: "+ justFileName);

            currConfig = new ChessConfig(filename);
            currConfig.modelGrid();

            rowDim = ChessConfig.horizontalRow;
            colDim = ChessConfig.verticalCol;

            alertObservers(STATE_MSGS.get(GameState.LOADED) + FILENAME);
            gameState = GameState.LOADED;
            rPos = -1;
            cPos = -1;

        } catch(RuntimeException e){
            alertObservers(STATE_MSGS.get(GameState.FAILED_LOAD ) + filename);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * displays initial config with #s on the side
     * used in ptui
     */
    public void displayGridWNumbers(){
        currConfig.modelGrid();
    }

    /**
     * solves the puzzle based on current config
     * if the list of solution is empty, there is no solution
     * if list is not empty, returns the config at index 1
     * @return
     */
    public ChessConfig hint(){
        Solver solver = new Solver(currConfig);
            if(currConfig.isSolution()){
                alertObservers(STATE_MSGS.get(GameState.SOLUTION));
                return currConfig;
            }
            else {
                List<Configuration> pathList= solver.solve(currConfig);
                if(!pathList.isEmpty()){
                    currConfig = (ChessConfig) pathList.get(1);
                    alertObservers(STATE_MSGS.get(GameState.NEXT_STEP));
                    currConfig.modelGrid();

                    // if it is last piece on gui board, then is solution
                    if(currConfig.getNumberOfPieces() == 1){
                        alertObservers(STATE_MSGS.get(GameState.SOLUTION));
                    }

                    return currConfig; // returns the next config towards solution
                } else {
                    alertObservers(STATE_MSGS.get(GameState.NO_SOLUTION));
                }
        }
        return currConfig;
    }

    /**
     * loads a new file whenever gui and ptui presses/types load
     * just makes a new model of the file passed in
     * @param filename the file to be loaded
     * @throws IOException
     */
    public void load(String filename) throws IOException {
        try {
            FILENAME = filename;
            new ChessModel(FILENAME);
            alertObservers(STATE_MSGS.get(GameState.LOADED) + filename);

        } catch (IOException e){
            alertObservers(STATE_MSGS.get(GameState.FAILED_LOAD) + filename);
        }
    }

    /**
     * to check if first selected coordinate is NOT valid, meaning it is a "."
     * @param startCoord the coordinate to check
     * @return true if starting coordinate does not have a piece, false otherwise
     */
    public boolean startCoordNoPiece(Coordinates startCoord){
        rPos = startCoord.row();
        cPos = startCoord.col();
        return currConfig.grid[rPos][cPos] == ChessConfig.EMPTY;
    }

    /**
     * checks if starting coord can capture dest coord
     * if it can, capture it. if not, print a message
     * @param startCoord the coord that captures
     * @param destCoord the coord to be captured
     */
    public void select(Coordinates startCoord, Coordinates destCoord){
        char type = currConfig.grid[startCoord.row()][startCoord.col()];

        if (!validCaptureModel(destCoord, type)){
            alertObservers(STATE_MSGS.get(GameState.CANT_CAPTURE) + startCoord + " to " + destCoord);
            System.out.println("Can't capture from "+ startCoord + " to "+ destCoord);
            currConfig.modelGrid();
        }
        else{
            currConfig.grid[startCoord.row()][startCoord.col()] = '.';
            currConfig.grid[destCoord.row()][destCoord.col()] = type;
            currConfig.decreaseOnce();

            if(currConfig.getNumberOfPieces() == 1){
                alertObservers(STATE_MSGS.get(GameState.SOLUTION));
            } else {
                alertObservers(STATE_MSGS.get(GameState.CAPTURED) + startCoord + " to " + destCoord);
                currConfig.modelGrid();
            }
        }
    }

    /**
     * generates a set of neighbors for current config, checks if each neighbor's dest coord is
     * changed to wtv is at starting coord
     * @param destCoord coord to check
     * @param type type of chess piece at starting coord
     * @return true if one of the neighbor's dest coord is changed to starting coord's chess piece, false otherwise
     */
    private boolean validCaptureModel(Coordinates destCoord, char type) {
        if (isWithinBounds(destCoord)) {

            Collection<Configuration> neighbors = currConfig.getNeighbors();
            for (Configuration x : neighbors) {
                ChessConfig y = (ChessConfig) x;

                // checks if piece at dest coord is same as start coord
                if (y.grid[destCoord.row()][destCoord.col()] == type) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if dest coord is within bounds
     * @param destCoord coord to check
     * @return true if it is wihin bounds, false otherwise
     */
    private boolean isWithinBounds(Coordinates destCoord){
        return destCoord.row() >= 0 && destCoord.row() < ChessConfig.horizontalRow &&
                destCoord.col() >= 0 && destCoord.col() < ChessConfig.verticalCol;

    }

    /**
     * resets the ChessConfig to the most recent file
     * @throws IOException
     */
    public void reset() throws IOException {
        try {
            load(FILENAME);
            currConfig = new ChessConfig(FILENAME);
            alertObservers(STATE_MSGS.get(GameState.RESET_PUZZLE));
            currConfig.modelGrid();
        } catch (IOException ioe) {}
    }

    /**
     * @return the current config
     */
    public ChessConfig getCurrConfig(){
        return currConfig;
    }

    /**
     * The view calls this to add itself as an observer.
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String message) {
        for (var observer : observers) {
            observer.update(this, message);
        }
    }
}
