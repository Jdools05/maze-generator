// enum to measure direction
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    // function to reverse direction
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