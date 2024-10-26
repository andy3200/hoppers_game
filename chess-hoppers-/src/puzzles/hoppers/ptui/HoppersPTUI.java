package puzzles.hoppers.ptui;

import puzzles.chess.model.ChessModel;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;

import java.io.IOException;
import java.util.Scanner;
/**
 * the PTUI part of the Hoppers puzzle
 * @author Andy Zheng Zheng
 * @github arz4166
 */

public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;
    private Scanner scanner;
    private String file;
    private int startRow;
    private int startCol;

    /**
     * initializes the ptui
     * @param filename the file input
     * @throws IOException when file doesn't exist
     */
    public void init(String filename) throws IOException {
        file = filename;
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        scanner = new Scanner(System.in);
        displayHelp();
    }

    /**
     * updates the gui when it is notified to update
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel model, String data) {
        // for demonstration purposes
        System.out.println(data);
    }

    /**
     * displays the commands the user can use
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * process the command and run the ptui
     */
    public void run() throws IOException {
        Scanner in = new Scanner( System.in );
        boolean from = true;
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith( "q" )) {
                    break;
                }
                else if(words[0].startsWith( "h" )){
                    model.hint();
                }
                else if(words[0].startsWith( "l" )){
                    try{
                        model = new HoppersModel(words[1]);
                    } catch (IOException e) {}
                }
                else if(words[0].startsWith( "s" )){
                    if(from){
                         startRow = Integer.parseInt(words[1]);
                         startCol = Integer.parseInt(words[2]);
                        if(!(model.checkMoveFrom(startRow, startCol))){
                            System.out.println("Invalid Selection ("+ startRow + "," + startCol + ")" );
                            this.model.printPTUIGrid();
                        }
                        else{
                            System.out.println("Selected ("+ startRow + "," + startCol + ")" );
                            this.model.printPTUIGrid();
                            from = false;
                        }
                    }
                    else{
                        int destRow = Integer.parseInt(words[1]);
                        int destCol = Integer.parseInt(words[2]);
                        model.validjump(startRow,startCol,destRow,destCol);
                        from = true;

                        }
                    }
                else if(words[0].startsWith( "r" )){
                    model.reset();
                }
                else {
                    System.out.println("Unknown command, check the following commands: ");
                    System.out.println("");
                    displayHelp();
                }

                }

        }
    }

    /**
     * the main method of the ptui. initialize the ptui and runs its method.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {
                HoppersPTUI ptui = new HoppersPTUI();
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
