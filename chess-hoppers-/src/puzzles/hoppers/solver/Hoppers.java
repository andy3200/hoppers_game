package puzzles.hoppers.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.IOException;
import java.util.List;

public class Hoppers {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        }
        else{
            String filename = args[0];
            System.out.println("File: "+args[0]);

            HoppersConfig hoppersConfig = new HoppersConfig(filename);
            Solver solver = new Solver(hoppersConfig);
            List<Configuration> pathList= solver.solve(hoppersConfig);


            System.out.print(hoppersConfig);
            solver.printPathList(pathList);
        }
    }
}
