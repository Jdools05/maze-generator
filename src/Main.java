import processing.core.PApplet;

import java.util.*;

enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Direction reverse(Direction dir) {
        switch (dir) {
            case UP:
                return DOWN;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return UP;
        }
    }
}

public class Main extends PApplet {
    Random random = new Random();

    int tileSize = 3;
    int mazeWidth = 1490;
    int mazeHeight = 300;

    int startX = 0;
    int startY = 0;

    int exitX = mazeWidth -1;
    int exitY = random.nextInt(mazeHeight);

    int pointerX, pointerY = 0;

    int wallStrokeSize = 1;

    double mazeGenElapsedTime = 0;
    double graphicsDisplayElapsedTime = 0;

    Tile[][] maze = new Tile[mazeWidth][mazeHeight];

    public void settings() {
        size(tileSize * mazeWidth + tileSize + 2, tileSize * mazeHeight + 100);
        for (int x = 0; x < mazeWidth; x++) {
            for (int y = 0; y < mazeHeight; y++) {
                maze[x][y] = new Tile(x, y);
            }
        }
        long timeStart = System.nanoTime();
        setupMaze();
        mazeGenElapsedTime = System.nanoTime() - timeStart;

        noLoop();
    }

    void setupMaze() {
        Stack<Tile> history = new Stack<>();

        history.push(maze[startX][startY]);
        maze[startX][startY].setHasBeenVisited();

        maze[exitX][exitY].removeWall(Direction.RIGHT);

        while(!history.empty()) {
            List<Tile> neighbors = new ArrayList<>();
            if (pointerX != 0 && !maze[pointerX-1][pointerY].hasBeenVisited) neighbors.add(maze[pointerX - 1][pointerY]);
            if (pointerX != mazeWidth -1 && !maze[pointerX+1][pointerY].hasBeenVisited) neighbors.add(maze[pointerX+1][pointerY]);
            if (pointerY != 0 && !maze[pointerX][pointerY-1].hasBeenVisited) neighbors.add(maze[pointerX][pointerY-1]);
            if (pointerY != mazeHeight-1 && !maze[pointerX][pointerY+1].hasBeenVisited) neighbors.add(maze[pointerX][pointerY+1]);

            if (neighbors.isEmpty()) {
                history.pop();
                if (history.empty()) break;
                pointerX = history.peek().x;
                pointerY = history.peek().y;
            } else {

                Tile next = neighbors.get(random.nextInt(neighbors.size()));
                next.setHasBeenVisited();

                int xOffset = next.x - history.peek().x;
                int yOffset = next.y - history.peek().y;

                Direction moveDir = Direction.UP;
                if (xOffset == 1) moveDir = Direction.RIGHT;
                if (xOffset == -1) moveDir = Direction.LEFT;
                if (yOffset == 1) moveDir = Direction.DOWN;
                if (yOffset == -1) moveDir = Direction.UP;

                // maybe will cause a bug?
                history.peek().removeWall(moveDir);
                next.removeWall(Direction.reverse(moveDir));
                history.push(next);
                pointerX = next.x;
                pointerY = next.y;
            }
        }
    }

    public void draw() {
        long graphicsTimeStart = System.nanoTime();
        background(64);
        strokeWeight(wallStrokeSize);
        for (Tile[] tl : maze) {
            for (Tile t : tl) {
                noStroke();
                if (t.x == startX && t.y == startY) fill(0, 0, 255); else fill(0, 255, 0);
                if (t.x == exitX && t.y == exitY) {
                    fill(255, 0, 0);
                    rect((t.x + 1) * tileSize, t.y * tileSize, tileSize, tileSize);
                }
                rect(t.x * tileSize, t.y * tileSize, tileSize, tileSize);


                stroke(0);
                for (Direction dir : t.walls) {
                    switch (dir) {
                        case UP:
                            line(t.x * tileSize, t.y * tileSize, t.x * tileSize + tileSize, t.y * tileSize);
                            break;
                        case DOWN:
                            line(t.x * tileSize, t.y * tileSize+ tileSize, t.x * tileSize+ tileSize, t.y * tileSize + tileSize);
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
        graphicsDisplayElapsedTime = System.nanoTime() - graphicsTimeStart;
        fill(255);
        String mazeGenTimeMessage = String.format("Elapsed maze generation time in milliseconds: %sms", mazeGenElapsedTime / 1000000);
        String mazeGenCellsMessage = String.format("Created %s cells (%s X %s)", mazeWidth * mazeHeight, mazeWidth, mazeHeight);
        String canvasSizeMessage = String.format("Size of canvas %spx X %spx", width, height);
        String graphicsDisplaceMessage = String.format("Elapsed graphics display time in milliseconds: %sms", graphicsDisplayElapsedTime / 1000000);
        System.out.println(mazeGenTimeMessage);
        System.out.println(mazeGenCellsMessage);
        System.out.println(canvasSizeMessage);
        System.out.println(graphicsDisplaceMessage);
        text(mazeGenTimeMessage, 10, height - 80);
        text(mazeGenCellsMessage, 10, height - 60);
        text(canvasSizeMessage, 10, height - 40);
        text(graphicsDisplaceMessage, 10, height -20);
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"Main"};
        PApplet.main(appletArgs);
    }
}

class Tile {

    boolean hasBeenVisited = false;

    int x, y;

    List<Direction> walls = new ArrayList<>() {
        {
            add(Direction.UP);
            add(Direction.DOWN);
            add(Direction.LEFT);
            add(Direction.RIGHT);
        }
    };

    Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void removeWall(Direction dir) {
        walls.remove(dir);
    }

    void setHasBeenVisited() {
        hasBeenVisited = true;
    }
}
