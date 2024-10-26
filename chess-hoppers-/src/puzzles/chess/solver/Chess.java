package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.List;

/**
 * main method for the chess puzzle
 * will call other class's methods to solve the puzzle
 * checks if command line argument has the file
 * @author KUNLIN WEN
 * @github KW9521
 */
public class Chess {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        }
        else{
            String filename = args[0];
            System.out.println("File: "+args[0]);

            ChessConfig chessConfig = new ChessConfig(filename);
            Solver solver = new Solver(chessConfig);
            List<Configuration> pathList= solver.solve(chessConfig);

            // prints initial config, total & unique configs and the steps to get to solution
            System.out.print(chessConfig.toString());
            solver.printPathList(pathList);


        }

    }
}
