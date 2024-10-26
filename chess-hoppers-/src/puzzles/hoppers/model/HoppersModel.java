package puzzles.hoppers.model;
import puzzles.chess.model.ChessConfig;
import puzzles.chess.model.ChessModel;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.gui.HoppersGUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * the model part of the Hoppers puzzle
 * @author Andy Zheng Zheng
 * @github arz4166
 */

public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private static HoppersConfig currentConfig;
    /** enum for game state **/
    public enum GameState{SELECTED, JUMPED, LOADED, NEXT, RESET, CANTJUMP, INVALID, SOLVED, NOSOLUTION,LOADFAIL,}

    /** hashmap that maps gamestate to their message **/
    public static final EnumMap<HoppersModel.GameState, String> GameStateMap = new EnumMap<>(Map.of(
            HoppersModel.GameState.SELECTED, "Selected: ",
            HoppersModel.GameState.JUMPED,  "Jumped from: ",
            HoppersModel.GameState.LOADED, "Loaded: ",
            HoppersModel.GameState.NEXT, "Next Step!",
            HoppersModel.GameState.RESET, "Puzzle Reset!",
            HoppersModel.GameState.CANTJUMP, "Can't jump from ",
            HoppersModel.GameState.INVALID, "Invalid Selection: ",
            HoppersModel.GameState.SOLVED, "Already Solved!",
            HoppersModel.GameState.NOSOLUTION, "No Solution!",
            HoppersModel.GameState.LOADFAIL, "Load Failed!"
    ));
    /** file loaded **/
    private static String file;
    /** the current gamestate **/
    private GameState gameState;
    /** cursor for row **/
    private int rowcursor;
    /** cursor for column **/
    private int columncursor;
    /** whether the coordinate is jumpable **/
    public boolean canjump;


    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * constructor for the model that creates the initial config
     * @param filename the file to load
     * @throws IOException when there isn't such file.
     */
    public HoppersModel(String filename) throws IOException {
        try{
            loadFile(filename);
            alertObservers(GameStateMap.get(GameState.LOADED) + filename);

        }
        catch(RuntimeException e) {
            alertObservers(GameStateMap.get(GameState.LOADFAIL) + filename);
            System.out.println("Failed to load: "+ filename);
            currentConfig = new HoppersConfig(file);
            currentConfig.PTUIGRID();
        }
    }

    public void loadFile(String filename)  {
        try(BufferedReader in = new BufferedReader(new FileReader(filename))){
            file = filename;
            System.out.println("Loaded: "+ new File(filename).getName());

            currentConfig = new HoppersConfig(filename);
            currentConfig.PTUIGRID();

            alertObservers(GameStateMap.get(GameState.LOADED) + file);

        } catch(RuntimeException e){
            alertObservers(GameStateMap.get(GameState.LOADFAIL ) + filename);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     *  generates a hint for the user
     * @return the new hoppersConfig after it is moved one step forward.
     */
    public HoppersConfig hint(){
        /** the solver **/
        Solver solver = new Solver(currentConfig);
        if(!(currentConfig.isSolution())){
            List<Configuration> path = solver.solve(currentConfig);
            if(path.size()!= 0){
                currentConfig = (HoppersConfig) path.get(1);
                gameState = GameState.NEXT;
                currentConfig.PTUIGRID();
                alertObservers(GameStateMap.get(GameState.NEXT));
                return currentConfig;
            }
            else{
                gameState = GameState.NOSOLUTION;
                alertObservers(GameStateMap.get(GameState.NOSOLUTION));
                return currentConfig;
            }

        }
        else{
            gameState = GameState.SOLVED;
            alertObservers(GameStateMap.get(GameState.SOLVED));
            return currentConfig;
        }
    }

    /**
     * loads a new file.
     * @param filename the new file to load
     */
    public void load(String filename) throws IOException{
        try{
            file = filename;
            new HoppersModel(file);
            alertObservers("");
        }catch(IOException e){
            alertObservers(GameStateMap.get(GameState.LOADFAIL) + filename);
        }
    }

    /**
     * checks if the  coordinate you want to move from is valid
     * @param startRow the start row
     * @param startCol the start column
     * @return true if the starting coordinate is valid(has green or red frog);
     */
    public boolean checkMoveFrom(int startRow, int startCol){
        return (currentConfig.grid[startRow][startCol].equals("G") || currentConfig.grid[startRow][startCol].equals("R"));
    }

    /**
     * checks if the place you want to jump to is valid.
     * @param startRow the start row
     * @param startCol the start col
     * @param destRow the destination row
     * @param destCol the destination column
     * @return return the hoppersConfig after you make the jump. if jump is invalid then return the original one.
     * if it is valid then return the config after you make the jump.
     */
    public HoppersConfig validjump(int startRow, int startCol , int destRow, int destCol){
        currentConfig.rowcursor = startRow;
        currentConfig.columncursor = startCol;
        if(!(coordexists(destRow, destCol))){
            gameState = GameState.CANTJUMP;
            System.out.println("Can't jump from ("+ startRow + "," + startCol + ") to ("+ destRow + "," + destCol + ")");
            alertObservers(GameStateMap.get(GameState.CANTJUMP));
            currentConfig.PTUIGRID();
            return currentConfig;
        }
        else{
            Collection<Configuration> validMoves = currentConfig.getNeighbors();
            currentConfig.rowcursor = startRow;
            currentConfig.columncursor = startCol;
            int middleRow = (startRow + destRow)/2;
            int middleCol= (startCol + destCol)/2;
            HoppersConfig tempconfig = new HoppersConfig(currentConfig,destRow,destCol,middleRow,middleCol);
            for (Configuration validconfig : validMoves) {
                HoppersConfig validHopper = (HoppersConfig) validconfig;

                if(tempconfig.equals(validHopper)){
                    currentConfig = tempconfig;
                    gameState = GameState.NEXT;
                    canjump = true;
                    System.out.println("Jumped from ("+ startRow + "," + startCol + ") to ("+ destRow + "," + destCol + ")");
                    alertObservers(GameStateMap.get(GameState.NEXT));
                    currentConfig.PTUIGRID();
                    return currentConfig;
                }
            }
            gameState = GameState.CANTJUMP;
            canjump = false;
            alertObservers(GameStateMap.get(GameState.CANTJUMP) + "("+ startRow + "," + startCol + ") to ("+ destRow + "," + destCol + ")");
            currentConfig.PTUIGRID();
            return currentConfig;
        }
    }

    /**
     * checks if the destination coordinate exists in our grid
     * @param row the row of destination
     * @param col the col of destination
     * @return true if the coordinate of destination exists. false otherwise.
     */
    private boolean coordexists(int row, int col){
        return row >= 0 && row < HoppersConfig.rows && col>= 0 && col < HoppersConfig.columns;

    }

    /**
     * resets the puzzle
     */
    public void reset() throws IOException{
        try {
            load(file);
            currentConfig = new HoppersConfig(file);
            alertObservers(GameStateMap.get(GameState.RESET));
        } catch (IOException ioe) {}
    }

    /**
     * prints the grid in PTUI
     */
    public void printPTUIGrid(){
        currentConfig.PTUIGRID();
    }

    /**
     * gets the current config
     * @return the current config
     */
    public HoppersConfig getCurrentConfig(){
        return currentConfig;
    }

}
