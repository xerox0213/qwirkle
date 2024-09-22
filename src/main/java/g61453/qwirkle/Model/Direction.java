package g61453.qwirkle.Model;
/**
 * The Direction enumeration represents possible directions in which an object can move in a game.
 * Each direction is associated with a row (deltaRow) and column (deltaCol) offset, allowing the object to move
 * in a given direction.
 */
public enum Direction {
    /**
     * Up direction. The offset is (-1, 0), meaning the object moves one cell upwards.
     */
    UP(-1,0),
    /**
     * Down direction. The offset is (1, 0), meaning the object moves one cell downwards.
     */
    DOWN(1, 0),
    /**
     * Left direction. The offset is (0, -1), meaning the object moves one cell to the left.
     */
    LEFT(0, -1),
    /**
     * Right direction. The offset is (0, 1), meaning the object moves one cell to the right.
     */
    RIGHT(0, 1);
    private final int deltaRow;
    private final int deltaCol;
    /**
     * Direction constructor.
     *
     * @param row The row offset for this direction.
     * @param col The column offset for this direction.
     */
    Direction(int row, int col){
        this.deltaRow = row;
        this.deltaCol = col;
    }
    /**
     * Get the row offset for this direction.
     *
     * @return The row offset for this direction.
     */
    public int getDeltaRow() {
        return deltaRow;
    }
    /**
     * Get the column offset for this direction.
     *
     * @return The column offset for this direction.
     */
    public int getDeltaCol() {
        return deltaCol;
    }
    /**
     * Get the opposite direction of this direction.
     *
     * @return The opposite direction.
     */
    public Direction opposite(){
        if(this == UP){
            return DOWN;
        }else if(this == DOWN){
            return UP;
        }else if(this == LEFT){
            return RIGHT;
        }else{
            return LEFT;
        }
    }
}
