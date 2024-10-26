package puzzles.strings;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.util.*;

/**
 * the main implementation for the Strings puzzle
 */
public class Strings {

    /**
     * the main method for Strings puzzle.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            String start = args[0];
            String end = args[1];
            System.out.println("Start: "+start+ ", End: "+end);

            StringsConfig startConfig = new StringsConfig(start, end);
            Solver solver = new Solver(startConfig);

            List<Configuration> pathList = solver.solve(startConfig);
            solver.printPathList(pathList);
        }
    }
}
