package puzzles.chess.ptui;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import java.io.IOException;
import java.util.Scanner;

/**
 * playing chess in the terminal lol
 * the ptui part of chess
 * @author KUNLIN WEN
 * @github KW9521
 */
public class ChessPTUI implements Observer<ChessModel, String> {
    private ChessModel model;
    private Scanner scan;
    private static String FILENAME;

    /**
     * @param filename path from content root: data/chess/chess-4.txt
     */
    public ChessPTUI(String filename) throws IOException {
        FILENAME = filename;
        this.model = new ChessModel(FILENAME);
        this.model.addObserver(this);
        this.scan = new Scanner(System.in);
        displayHelp();
        System.out.println();
    }

    @Override
    public void update(ChessModel model, String data) {
        // for demonstration purposes
        System.out.println(data);
    }

    /**
     * displays the help table of what user can enter into terminal
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * basically the entire game...
     * waits for user input and processes those commands by calling methods from model
     */
    public void run() {
        Scanner in = new Scanner( System.in );
        int counter = 1;
        Coordinates selectedCoord = null;
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {

                if (words[0].startsWith( "h" )) {
                    model.hint();
                }

                else if(words[0].startsWith( "l" )){
                    try{
                        model = new ChessModel(words[1]);
                    } catch (IOException e) {}
                }

                else if(words[0].startsWith( "s" )){
                    if (counter % 2 == 1){ // the select part, this one is the "from"
                        int row = Integer.parseInt(words[1]);
                        int col = Integer.parseInt(words[2]);
                        selectedCoord = new Coordinates(row, col);

                        // if starting coordinate does not have a chess piece on grid
                        if(model.startCoordNoPiece(selectedCoord)){
                            System.out.println("Invalid Selection "+selectedCoord);
                            model.displayGridWNumbers();
                        } else {
                            System.out.println("Selected " + selectedCoord);
                            model.displayGridWNumbers(); // displays current grid
                            counter++;
                        }

                    }
                    else{ // alr selected something, this one is the "to"
                        int row = Integer.parseInt(words[1]);
                        int col = Integer.parseInt(words[2]);
                        Coordinates toThisCoord = new Coordinates(row, col);
                        model.select(selectedCoord, toThisCoord);
                        counter++;
                    }
                }

                else if(words[0].startsWith( "q" )){
                    break;
                }

                else if(words[0].startsWith( "r" )){
                    try{
                        model.reset();
                    } catch (IOException ioe){}
                }

                else {
                    System.out.println("Illegal Command...Choose from this table: ");
                    displayHelp();
                }
            }
        }
    }

    /**
     * the main call
     * @param args command line argument
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            try {
                ChessPTUI ptui = new ChessPTUI(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}

