import java.util.ArrayList;
import java.util.List;

public class Tile {
    // save if has been visited by THE POINTER ;)
    boolean hasBeenVisited = false;

    // save the distance to the exit
    int stepsToExit = 0;

    // save the position in the maze
    int x, y;

    // initialize with all the walls there
    List<Direction> walls = new ArrayList<>() {
        {
            add(Direction.UP);
            add(Direction.DOWN);
            add(Direction.LEFT);
            add(Direction.RIGHT);
        }
    };

    // constructor with the tile position as parameter
    Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // removes a wall in the given direction
    void removeWall(Direction dir) {
        walls.remove(dir);
    }

    // has been visited by THE POINTER
    void setHasBeenVisited() {
        hasBeenVisited = true;
    }
}