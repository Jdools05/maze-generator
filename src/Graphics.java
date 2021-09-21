import processing.core.PApplet;

public class Graphics extends PApplet {

    // store the instance of the maze generator
    MazeGenerator maze;

    // constructor that takes an instance of the maze generator
    Graphics(MazeGenerator maze) {
        this.maze = maze;
        previousStepTile = maze.maze[maze.startX][maze.startY];
    }

    // set some colors for the GUI
    int tileColor = unhex("FF000000");
    int wallColor = unhex("FFFFFFFF");
    int startColor = unhex("FF00FF00");
    int endColor = unhex("FFFFFF00");

    // set the sizes of the tiles and walls (may change to set with arguments)
    int tileSize = 20;
    int wallStrokeSize = 1;

    int pathColor = unhex("FF00FF00");
    int pathStrokeSize = 3;

    // holds the steps for the animation
    int stepCounter = 0;
    int reloadCounter = 0;
    Tile previousStepTile;

    // set the timer for graphics display
    double graphicsDisplayElapsedTime = 0;

    boolean shouldDrawMaze = true;

    // called before the first frame
    public void settings() {
        // set up canvas size
        size(tileSize * maze.mazeWidth + 2, tileSize * maze.mazeHeight + 2);
    }

    // check key presses
    public void keyPressed() {
        if (key == 'R' || key == 'r') {
            // reload the maze
            maze.reload();
            shouldDrawMaze = true;
            stepCounter = 0;
            previousStepTile = maze.maze[maze.startX][maze.startY];
        }
        // redraws on key press to help identify bugs
        redraw();
    }

    private void drawMaze() {

        // set the shortest path of the maze (maybe will change to return the stack)
        maze.findShortestPath();

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
                } else fill(((float)t.stepsToExit / (float)maze.highestStackSize) * 200, 0, 200 - ((float)t.stepsToExit / (float)maze.highestStackSize) * 200);
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
        shouldDrawMaze = false;
    }

    // main function for drawing
    public void draw() {

        if (shouldDrawMaze) drawMaze();
        // set the stroke for the path
        stroke(pathColor);
        strokeWeight(pathStrokeSize);


        Tile t = maze.shortestPath.elementAt(stepCounter);
        // draw a line from the center of the previous tile to the center of the next tile
        line(t.x * tileSize + tileSize / 2f, t.y * tileSize + tileSize / 2f, previousStepTile.x * tileSize + tileSize / 2f, previousStepTile.y * tileSize + tileSize / 2f);
        // set the previous tile to the current tile

        if (stepCounter < maze.shortestPath.size() - 1) {
            previousStepTile = t;
            stepCounter++;
        } else {
            reloadCounter++;
        }
        if (reloadCounter == 100) {
            reloadCounter = 0;
            maze.reload();
            shouldDrawMaze = true;
            stepCounter = 0;
            previousStepTile = maze.maze[maze.startX][maze.startY];
        }
    }
}
