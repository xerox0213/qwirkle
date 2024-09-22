package g61453.qwirkle.Model;

import java.io.Serializable;

/**
 * The `GridView` class represents a view of the game grid, allowing access to tile information and grid properties.
 */
public class GridView implements Serializable {
    private Grid grid;

    /**
     * Constructs a new `GridView` with a reference to the game grid.
     *
     * @param grid The game grid to be associated with this view.
     */
    GridView(Grid grid){
        this.grid = grid;
    }

    /**
     * Retrieves the tile at the specified position on the game grid.
     *
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * @return The tile at the specified position, or null if no tile is present.
     */
    public Tile get(int row, int col){
        return grid.get(row, col);
    }

    /**
     * Checks if the game grid is empty.
     *
     * @return `true` if the grid is empty, `false` otherwise.
     */
    public boolean isEmpty(){
        return grid.isEmpty();
    }
}
