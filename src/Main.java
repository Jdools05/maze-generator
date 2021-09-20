import processing.core.PApplet;

import java.lang.reflect.Array;
import java.util.*;



public class Main {

    // Function to concatenate two arrays of the same type
    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    // main entry point of the program
    public static void main(String[] passedArgs) {
        // parse the args to satisfy Processing
        String[] appletArgs = concatenate(new String[]{"Main"}, passedArgs);
        // create a new instance of the maze generator class with the passed arguments
        MazeGenerator mazeGenerator = new MazeGenerator(appletArgs);
        // run the maze generator
        mazeGenerator.run();
    }
}
