import processing.core.PApplet;

public class Graphics extends PApplet {

    // store the instance of the maze generator
    MazeGenerator maze;

    // constructor that takes a instance of the maze generator
    Graphics(MazeGenerator maze) {
        this.maze = maze;
    }

    // set some colors for the GUI
    int tileColor = unhex("FF000000");
    int wallColor = unhex("FFFFFFFF");
    int startColor = unhex("FF009900");
    int endColor = unhex("FF990000");

    // set the sizes of the tiles and walls (may change to set with arguments)
    int tileSize = 20;
    int wallStrokeSize = 1;

    // set the timer for graphics display
    double graphicsDisplayElapsedTime = 0;

    // called before the first frame
    public void settings() {
        // set up canvas size
        size(tileSize * maze.mazeWidth + 2, tileSize * maze.mazeHeight + 2);
        // prevent redraw of canvas
        noLoop();
    }

    // check key presses
    public void keyPressed() {
        if (key == 'R' || key == 'r') {
            // reload the maze
            maze.reload();
        }
        // redraws on key press to help identify bugs
        redraw();
    }

    // main function for drawing
    public void draw() {
        // setup timer
        long graphicsTimeStart = System.nanoTime();

        // set GUI for colors and sizes
        background(tileColor);
        strokeWeight(wallStrokeSize);
        textSize(8);
        // loop over each tile in maze
        for (Tile[] tl : maze.maze) {
            for (Tile t : tl) {
                // don't draw any "walls" (these are the lines with the tiles)
                noStroke();

                // if starting tile, set color different
                if (t.x == maze.startX && t.y == maze.startY) {
                    fill(startColor);
                }
                // if exit tile, set color different
                else if (t.x == maze.exitX && t.y == maze.exitY) {
                    fill(endColor);
                    // else fill by gradient proportional to distance from exit
                } else fill(0, 0, 255 - ((float)t.stepsToExit / (float)maze.highestStackSize) * 255);
                // draw the tile
                rect(t.x * tileSize, t.y * tileSize, tileSize, tileSize);
                // if uncommented, will displace the steps to exit
//                fill(255);
//                text(t.stepsToExit, t.x * tileSize + 3, t.y * tileSize + 15);

                // set the color of the walls
                stroke(wallColor);
                // for each direction saved in each tile
                for (Direction dir : t.walls) {
                    // draw a line for the wall based off of the directions
                    switch (dir) {
                        case UP:
                            line(t.x * tileSize, t.y * tileSize, t.x * tileSize + tileSize, t.y * tileSize);
                            break;
                        case DOWN:
                            line(t.x * tileSize, t.y * tileSize + tileSize, t.x * tileSize + tileSize, t.y * tileSize + tileSize);
                            break;
                        case LEFT:
                            line(t.x * tileSize, t.y * tileSize, t.x * tileSize, t.y * tileSize + tileSize);
                            break;
                        case RIGHT:
                            line(t.x * tileSize + tileSize, t.y * tileSize, t.x * tileSize + tileSize, t.y * tileSize + tileSize);
                            break;
                    }
                }
            }
        }
        // ISSUES BEGIN HERE
        // set the stroke to red for the path
        stroke(255, 0, 0);
        // store the previous tile
        Tile previous = maze.maze[maze.startX][maze.startY];
        // set the shortest path of the maze (maybe will change to return the stack)
        maze.findShortestPath();
        // for each tile in the stack
        for (int i = 0; i < maze.shortestPath.size(); i++) {
            // save the current tile
            Tile t = maze.shortestPath.elementAt(i);
            // draw a line from the center of the previous tile to the center of the next tile
            line(t.x * tileSize + tileSize / 2, t.y * tileSize + tileSize / 2, previous.x * tileSize + tileSize / 2, previous.y * tileSize + tileSize / 2);
            // set the previous tile to the current tile
            previous = t;
        }

        // ISSUES END HERE

        // measure the time it took to display
        graphicsDisplayElapsedTime = System.nanoTime() - graphicsTimeStart;

        // format the messages
        String canvasSizeMessage = String.format("Size of canvas %spx X %spx", width, height);
        String graphicsDisplaceMessage = String.format("Elapsed graphics display time in milliseconds: %sms", graphicsDisplayElapsedTime / 1000000);

        // output the messages
        System.out.println(canvasSizeMessage);
        System.out.println(graphicsDisplaceMessage);
    }
}
