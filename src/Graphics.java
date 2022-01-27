import processing.core.PApplet;

import java.text.DecimalFormat;

public class Graphics extends PApplet {

    // store the instance of the maze generator
    MazeGenerator maze;

    // constructor that takes an instance of the maze generator
    Graphics(MazeGenerator maze) {
        this.maze = maze;
        previousStepTile = maze.maze[maze.startX][maze.startY];
    }

    // set some colors for the GUI
    int tileColor = unhex("FF587B74");
    int wallColor = unhex("FF2C2C34");
    int startColor = unhex("FF00FF00");
    int endColor = unhex("FFFFA987");
    int pathColor = unhex("FFC1292E");
    int farAwayColor = unhex("FF3590F3");
    int closeToColor = unhex("FFCFFCFF");

    // set the sizes of the tiles and walls (may change to set with arguments)

    int wallStrokeSize = 3;

    int pathStrokeSize = 3;

    // holds the steps for the animation
    int stepCounter = 0;
    int reloadCounter = 0;
    Tile previousStepTile;

    // set the timer for graphics display
    double graphicsDisplayElapsedTime = 0;

    boolean shouldDrawMaze = true;
    boolean paused = false;

    DecimalFormat numberFormatter = new DecimalFormat("#.00");

    // called before the first frame
    public void settings() {

        // set up canvas size
        size(maze.tileSize * maze.mazeWidth + 2, maze.tileSize * maze.mazeHeight + 2);

        fullScreen();

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
        if (key == ' '){
            // pause the maze
            paused = !paused;
            if (paused) noLoop();
            else loop();
        }
        // redraws on key press to help identify bugs
        redraw();
    }

    private void drawMaze() {
        frameRate(24);

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
                    // old code: ((float)t.stepsToExit / (float)maze.highestStackSize) * 200, 0, 200 - ((float)t.stepsToExit / (float)maze.highestStackSize) * 200
                } else {
                    fill(lerpColor(closeToColor, farAwayColor, (float)t.stepsToExit / (float)maze.highestStackSize));
                };
                // draw the tile
                rect(t.x * maze.tileSize, t.y * maze.tileSize, maze.tileSize, maze.tileSize);
                // if uncommented, will displace the steps to exit
//                fill(255);
//                text(t.stepsToExit, t.x * tileSize + 3, t.y * tileSize + 15);

                // set the color of the walls
                stroke(wallColor);
                // for each direction saved in each til
                for (Direction dir : t.walls) {
                    // draw a line for the wall based off of the directions
                    switch (dir) {
                        case UP:
                            line(t.x * maze.tileSize, t.y *maze.tileSize, t.x *maze.tileSize +maze.tileSize, t.y *maze.tileSize);
                            break;
                        case DOWN:
                            line(t.x *maze.tileSize, t.y *maze.tileSize +maze.tileSize, t.x *maze.tileSize +maze.tileSize, t.y *maze.tileSize +maze.tileSize);
                            break;
                        case LEFT:
                            line(t.x *maze.tileSize, t.y *maze.tileSize, t.x *maze.tileSize, t.y *maze.tileSize + maze.tileSize);
                            break;
                        case RIGHT:
                            line(t.x *maze.tileSize +maze.tileSize, t.y *maze.tileSize, t.x *maze.tileSize +maze.tileSize, t.y * maze.tileSize + maze.tileSize);
                            break;
                    }
                }
            }
        }
        shouldDrawMaze = false;
    }

    // main function for drawing
    public void draw() {
        int tileCount = maze.mazeHeight * maze.mazeWidth;
        double totalPathPercentage = (maze.shortestPath.size() / (double) tileCount);


        // reset the display bar
        push();
        fill(tileColor);
        noStroke();
        rect(0, height-maze.bottomBarHeight, width, height);
        pop();

        // displays the total tile count
        textSize(45);
        fill(0, 0, 0);
        text(String.format("Steps: %s", stepCounter), 10, height - 20);

        //display the total amount of tiles
        textSize(45);
        fill(0, 0, 0);
        text(String.format("Total Tiles: %s", tileCount), 270, height - 20);
        //display the total percentage of the maze taken up by the path
        textSize(45);
        fill(0, 0, 0);
        text(String.format("Percent Maze Traveled: %s%%", numberFormatter.format(totalPathPercentage * 100)), 700, height - 20);

        // displays the progress bar
        fill(0, 0, 0);
        noStroke();
        rect(1500, height - 50, 398, 40);
        fill(0, 255, 0);
        rect(1500, height - 50, 400 * (stepCounter / (float)maze.shortestPath.size()), 40);

        if (shouldDrawMaze) drawMaze();
        // set the stroke for the path
        stroke(pathColor);
        strokeWeight(pathStrokeSize);


        Tile t = maze.shortestPath.elementAt(stepCounter);
        // draw a line from the center of the previous tile to the center of the next tile
        line(t.x * maze.tileSize + maze.tileSize / 2f, t.y * maze.tileSize + maze.tileSize / 2f, previousStepTile.x * maze.tileSize + maze.tileSize / 2f, previousStepTile.y * maze.tileSize + maze.tileSize / 2f);
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
