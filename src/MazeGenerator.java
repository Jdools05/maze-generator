import processing.core.PApplet;

import java.util.*;
import java.util.stream.Collectors;

public class MazeGenerator {
    // hold the random object
    Random random = new Random();

    // store the width and height of maze
    int mazeWidth = 20;
    int mazeHeight = 20;

    // store the position of the start
    int startX = 0;
    int startY = mazeHeight / 2;

    // store the position of the end
    int exitX = mazeWidth - 1;
    int exitY = random.nextInt(mazeHeight);

    // store the position of a pointer to navigate the maze
    int pointerX = exitX;
    int pointerY = exitY;

    // how many mazes to produce (used for performance benchmarking)
    int mazeGenSampleSize = 1;

    // store the processing times of the maze generation
    double mazeGenElapsedTime = 0;
    double mazeGenAverageTime = 0;

    // store the distance of the farthest point from the exit
    int highestStackSize = 0;

    // store the stack of the shortest path to exit from start
    Stack<Tile> shortestPath = new Stack<>();

    // stores the maze a 2D array
    Tile[][] maze = new Tile[mazeWidth][mazeHeight];

    // stores the arguments passed in
    String[] appletArgs;

    // constructor that takes in arguments and saves it
    MazeGenerator(String[] appletArgs) {
        this.appletArgs = appletArgs;
    }

    // function that gets the neighbors of a tile
    List<Tile> getNeighbors(int x, int y) {
        // create neighbors list
        List<Tile> neighbors = new ArrayList<>();
        // if not on left edge, get neighbor to the left
        if (x != 0)
            neighbors.add(maze[x - 1][y]);
        // if not on right edge, get neighbor to the right
        if (x != mazeWidth - 1)
            neighbors.add(maze[x + 1][y]);
        // if not on top edge, get neighbor to the top
        if (y != 0)
            neighbors.add(maze[x][y - 1]);
        // if not on bottom edge, get neighbor to the bottom
        if (y != mazeHeight - 1)
            neighbors.add(maze[x][y + 1]);
        // return the neighboring tiles
        return neighbors;
    }

    // function that determines the shortest path from the start to the end
    // maybe will change to take in any position
    void findShortestPath() {
        // saves the history of the path in a stack
        Stack<Tile> history = new Stack<>();

        // set the pointer to be at the start
        pointerX = startX;
        pointerY = startY;

        // save a counter to prevent infinite loops (shouldn't have to, but you know)
        int counter = 0;
        // loop till pointer is on exit
        while (!(pointerX == exitX && pointerY == exitY)) {
            // get the neighboring tiles
            List<Tile> neighbors = getNeighbors(pointerX, pointerY);

            // loop over each neighboring tiles
            for (Tile t : neighbors) {
                // if the difference in tiles is 1, then it is one step closer to the exit (Most likely no wall between them, might have to change)
                if (maze[pointerX][pointerY].stepsToExit - t.stepsToExit == 1) {
                    // add to history
                    history.push(t);
                    // set pointer to neighboring tile
                    pointerX = t.x;
                    pointerY = t.y;
                    break;
                }
            }
            // prevent infinite loops
            if (counter >= highestStackSize) break;
            counter++;
        }
        // sets the shortest path to the class variable
        // may change to return
        shortestPath = history;
    }

    // function to generate a path with no excluded tiles
    // any tile is connected to any other tile by some path
    void setupMaze() {
        // set up history
        Stack<Tile> history = new Stack<>();

        // initialize history at the exit
        // exit is used for gradient
        history.push(maze[exitX][exitY]);
        maze[exitX][exitY].setHasBeenVisited();

        // set pointer to the exit
        pointerX = exitX;
        pointerY = exitY;

        // while pointer is not back at the start
        while (!history.empty()) {
            // if the pointer found the start then turn around
            if (pointerX == startX && pointerY == startY) {
                // remove current position
                history.pop();
                // check if this is also the start?
                if (history.empty()) break;
                // set pointer to the latest position
                pointerX = history.peek().x;
                pointerY = history.peek().y;
                continue;
            }

            // get the neighbors that haven't been visited yet
            List<Tile> neighbors = getNeighbors(pointerX, pointerY).stream().filter(n -> !n.hasBeenVisited).collect(Collectors.toList());

            // if there are no new neighbors
            if (neighbors.isEmpty()) {
                // remove current position
                history.pop();
                // check if we are back to the starting point
                if (history.empty()) break;
                // set pointer to the latest position
                pointerX = history.peek().x;
                pointerY = history.peek().y;
                continue;
            }

            // chose a random unvisited neighbor and visit it
            Tile next = neighbors.get(random.nextInt(neighbors.size()));
            next.setHasBeenVisited();

            // save the distance from exit (used for gradient)
            next.stepsToExit = history.size();

            // if it is the farthest distance, it will be saved
            highestStackSize = Math.max(history.size(), highestStackSize);

            // determine what direction the pointer moved
            int xOffset = next.x - history.peek().x;
            int yOffset = next.y - history.peek().y;

            // translate the offset to the enum (maybe will change to mapping)
            Direction moveDir = Direction.UP;
            if (xOffset == 1) moveDir = Direction.RIGHT;
            if (xOffset == -1) moveDir = Direction.LEFT;
            if (yOffset == 1) moveDir = Direction.DOWN;
            if (yOffset == -1) moveDir = Direction.UP;

            // remove the wall of the latest / current position relative to the move direction
            history.peek().removeWall(moveDir);
            // remove the wall of the new position reversed to the move direction
            next.removeWall(Direction.reverse(moveDir));
            // save the new position in the history
            history.push(next);
            // move the pointer to the new position
            pointerX = next.x;
            pointerY = next.y;
        }
    }

    // function used to reset and regenerate the maze
    void reload() {
        // reset the exit y position
        exitY = random.nextInt(mazeHeight);
        // start the reload timer
        long reloadStart = System.nanoTime();
        // reset all the tiles of the maze (could optimize, but it is not crucial)
        for (int x = 0; x < mazeWidth; x++) {
            for (int y = 0; y < mazeHeight; y++) {
                maze[x][y] = new Tile(x, y);
            }
        }
        // resetup the maze
        setupMaze();
        // measure the time it took and print it out
        double elapsedReloadTime = System.nanoTime() - reloadStart;
        System.out.printf("\nReloaded in: %sms\n", elapsedReloadTime / 1000000);
    }

    // starting point of the maze generator
    void run() {
        // try to parse the arguments
        try {
            // optional maze generation sample size, default 1
            mazeGenSampleSize = Integer.parseInt(Arrays.asList(appletArgs).get(appletArgs.length-1));
        } catch (NumberFormatException e) {
            // no argument was given
            System.out.println("No maze generation sample size set, using 1");
        }

        // measure the starting time for the setup of the maze
        long timeStart = System.nanoTime();
        // loop over each sample size generating a maze
        for (int i = 0; i < mazeGenSampleSize; i++) {
            // setup all the tiles
            for (int x = 0; x < mazeWidth; x++) {
                for (int y = 0; y < mazeHeight; y++) {
                    maze[x][y] = new Tile(x, y);
                }
            }
            // generate the paths between tiles
            setupMaze();
        }

        // run without GUI argument useful if you want to benchmark generation without GUIs
        if (!Arrays.asList(appletArgs).contains("noGui")) {
            // if GUI, create GUI
            PApplet.runSketch(appletArgs, new Graphics(this));
        }

        // measure the generation time and calculate average
        mazeGenElapsedTime = System.nanoTime() - timeStart;
        mazeGenAverageTime = mazeGenElapsedTime / mazeGenSampleSize;

        // format messages
        String mazeGenTimeMessage = String.format("Elapsed maze generation time in milliseconds: %sms\nMazes created: %s\nAverage time: %sms", mazeGenElapsedTime / 1000000, mazeGenSampleSize, mazeGenAverageTime / 1000000);
        String mazeGenCellsMessage = String.format("Created %s cells per maze (%s X %s)", mazeWidth * mazeHeight, mazeWidth, mazeHeight);

        // output messages
        System.out.println(mazeGenTimeMessage);
        System.out.println(mazeGenCellsMessage);

    }
}
