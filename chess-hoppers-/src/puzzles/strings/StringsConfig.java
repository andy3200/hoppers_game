package puzzles.strings;
import puzzles.common.solver.Configuration;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Turns the information from command line arguments into instances of StringsConfig configurations.
 * Has isSolution, getNeighbors, equals, toString, hashCode
 */
public class StringsConfig implements Configuration {

    private String currString;
    private static String destinationString;

    /**
     * initial constructor
     * @param start
     * @param dest
     */
    public StringsConfig(String start, String dest){
        this.currString = start;
        destinationString = dest;
    }

    /**
     * copy constructor but this takes in an instance of itself
     * @param other the other config to opy
     */
    public StringsConfig(StringsConfig other){
        this.currString = other.currString;
    }

    /**
     * another copy constructor but this takes in a string
     * @param other the string to set currString to
     */
    public StringsConfig(String other){
        this.currString = other;
    }

    /**
     * checks if current config's string is equal to the destination string
     * @return true if it is equal, false otherwise
     */
    @Override
    public boolean isSolution() {
        return currString.equals(destinationString);
    }

    /**
     * generates neighbors for the current string
     * example: AB generates neighbors ZB, BB, AA, AC
     * @return X number of neighbors depending on how long the string is
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Set<Configuration> neighbors = new LinkedHashSet<>();
        for(int i =0; i< currString.length(); i++){

            // current letter's ASCII value
            int currentIndexASCII = currString.charAt(i);
            StringBuffer sbf = new StringBuffer(currString);

            // if currString.charAt(i) is A
            if (currentIndexASCII-1 < 65){
                // moving to left: 'Z'
                sbf.setCharAt(i, 'Z');
                StringsConfig moveLeft = new StringsConfig(sbf.toString());
                neighbors.add(moveLeft);

                // moving to right: B
                sbf.setCharAt(i, 'B');
                StringsConfig moveRight = new StringsConfig(sbf.toString());
                neighbors.add(moveRight);
            }

            // if currString.charAt(i) is Z
            else if(currentIndexASCII+1 > 90){
                // moving to left: 'Y'
                sbf.setCharAt(i, 'Y');
                StringsConfig moveLeft = new StringsConfig(sbf.toString());
                neighbors.add(moveLeft);

                // moving to right: A
                sbf.setCharAt(i, 'A');
                StringsConfig moveRight = new StringsConfig(sbf.toString());
                neighbors.add(moveRight);
            }

            else{
                // moving to the left
                char leftChar = (char)(currentIndexASCII-1);
                sbf.setCharAt(i, leftChar);
                StringsConfig moveLeft = new StringsConfig(sbf.toString());
                neighbors.add(moveLeft);

                // moving to the right
                char rightChar = (char)(currentIndexASCII+1);
                sbf.setCharAt(i, rightChar);
                StringsConfig moveRight = new StringsConfig(sbf.toString());
                neighbors.add(moveRight);
            }
        }
        return neighbors;
    }

    /**
     * checks if current config's string is the same as the other config's string
     * @param other the other config
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof StringsConfig){
            StringsConfig strConfigOther = (StringsConfig) other;
            if (strConfigOther.currString.equals(this.currString)){
                return true;
            }
        }
        return false;
    }

    /**
     * @return current config's string's hashcode
     */
    @Override
    public int hashCode() {
        return currString.hashCode();
    }

    /**
     * @return the current string
     */
    @Override
    public String toString() {
        return this.currString;
    }
}
