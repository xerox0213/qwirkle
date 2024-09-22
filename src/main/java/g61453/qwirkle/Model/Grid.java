package g61453.qwirkle.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the game grid for the Qwirkle game.
 */
public class Grid implements Serializable {
    private final Tile[][] tiles;
    private boolean isEmpty;
    private static final int GRID_ROW_SIZE = 91;
    private static final int GRID_COL_SIZE = 91;
    private static final int MAX_TILES_PER_LINE = 6;

    /**
     * Constructs a new Grid object.
     */
    Grid() {
        tiles = new Tile[GRID_COL_SIZE][GRID_ROW_SIZE];
        isEmpty = true;
    }

    /**
     * Adds tiles for the first move on the grid.
     *
     * @param direction The direction of the tiles.
     * @param line      The tiles to be added.
     * @return The score obtained for the move.
     * @throws QwirkleException If the method is called with tiles already present on the grid.
     */
    public int firstAdd(Direction direction, Tile... line) {
        int score = 0;
        if (!isEmpty)
            throw new QwirkleException("Utilize this method strictly during the very first turn of the game!");
        ArrayList<Tile> copyLine = new ArrayList<>(Arrays.asList(line));
        checkLineValidity(copyLine);
        score += getScore(copyLine);
        int col = 45;
        int row = 45;
        isEmpty = false;
        for (Tile tile : line) {
            tiles[col][row] = tile;
            col += direction.getDeltaCol();
            row += direction.getDeltaRow();
        }
        return score;
    }

    /**
     * Adds a tile to a specified position on the grid.
     *
     * @param row  The row where the tile should be added.
     * @param col  The column where the tile should be added.
     * @param tile The tile to be added.
     * @return The score obtained for the move.
     * @throws QwirkleException If various conditions for adding the tile are not met.
     */
    public int add(int row, int col, Tile tile) {
        int score = 0;
        if (isEmpty) throw new QwirkleException("Method not to be used during the initial turn of the game!");

        checkCellValidity(row, col);
        ArrayList<Direction> directions = getPossibleDirections(row, col);

        if (directions.isEmpty()) throw new QwirkleException("Prohibited action: Tile must connect to existing line.");

        for (Direction direction : directions) {
            ArrayList<Tile> chunk1 = getChunkOfLine(row, col, direction);
            chunk1.add(tile);
            if (directions.contains(direction.opposite())) {
                ArrayList<Tile> chunk2 = getChunkOfLine(row, col, direction.opposite());
                chunk1.addAll(chunk2);
                directions.remove(direction.opposite());
            }
            checkLineValidity(chunk1);
            score += getScore(chunk1);
        }

        tiles[col][row] = tile;
        return score;
    }

    /**
     * Adds multiple tiles to a specified position on the grid in a specified direction.
     *
     * @param row       The starting row.
     * @param col       The starting column.
     * @param direction The direction of the tiles.
     * @param line      The tiles to be added.
     * @return The score obtained for the move.
     * @throws QwirkleException If various conditions for adding the tiles are not met.
     */
    public int add(int row, int col, Direction direction, Tile... line) {
        int score = 0;
        if (isEmpty) throw new QwirkleException("Method not to be used during the initial turn of the game!");
        ArrayList<Tile> copyLine = new ArrayList<>(10);
        for (int i = 0; i < line.length; i++) {
            Tile currTile = line[i];
            copyLine.add(currTile);
            int rowPlusOffset = row + i * direction.getDeltaRow();
            int colPlusOffset = col + i * direction.getDeltaCol();
            checkCellValidity(rowPlusOffset, colPlusOffset);
            ArrayList<Direction> possibleDirections = getPossibleDirections(rowPlusOffset, colPlusOffset);
            if (i == 0 && possibleDirections.isEmpty()) {
                throw new QwirkleException("Prohibited action: First tile must connect to existing line.");
            }
            score += completeAndCheckChunks(rowPlusOffset, colPlusOffset, copyLine, currTile, possibleDirections, direction);
        }
        checkLineValidity(copyLine);
        score += getScore(copyLine);

        for (int i = 0; i < line.length; i++) {
            int rowPlusOffset = row + i * direction.getDeltaRow();
            int colPlusOffset = col + i * direction.getDeltaCol();
            tiles[colPlusOffset][rowPlusOffset] = line[i];
        }
        return score;
    }

    /**
     * Adds multiple tiles to the grid at specified positions.
     *
     * @param line The array of TileAtPosition objects representing tiles and their positions.
     * @return The score obtained for the move.
     * @throws QwirkleException If various conditions for adding the tiles are not met.
     */
    public int add(TileAtPosition... line) {
        int score = 0;
        if (isEmpty) throw new QwirkleException("Method not to be used during the initial turn of the game!");

        if (line.length == 1) {
            TileAtPosition tileAtPos = line[0];
            return add(tileAtPos.row(), tileAtPos.col(), tileAtPos.tile());
        }

        Direction direction = checkIfTilesAreAlignInSameDirection(line);
        ArrayList<Tile> copyLine = new ArrayList<>(10);

        for (int i = 0; i < line.length; i++) {
            TileAtPosition tileAtPos = line[i];
            ArrayList<Direction> possibleDirections = getPossibleDirections(tileAtPos.row(), tileAtPos.col());

            copyLine.add(tileAtPos.tile());
            checkCellValidity(line[i].row(), line[i].col());

            if (i == 0 && possibleDirections.isEmpty()) {
                throw new QwirkleException("Prohibited action: First tile must connect to existing line.");
            }

            if (i > 0 && possibleDirections.isEmpty() && !isAdjacentToPrevTile(tileAtPos, line[i - 1], direction)) {
                throw new QwirkleException("Prohibited action: Tiles must connect to existing line or tile of your hand.");
            }

            score += completeAndCheckChunks(tileAtPos.row(), tileAtPos.col(), copyLine, tileAtPos.tile(), possibleDirections, direction);
        }

        checkLineValidity(copyLine);
        score += getScore(copyLine);

        for (TileAtPosition tileAtPos : line) {
            tiles[tileAtPos.col()][tileAtPos.row()] = tileAtPos.tile();
        }
        return score;
    }

    /**
     * Returns a tile at the specified row and column.
     *
     * @param row The row of the tile.
     * @param col The column of the tile.
     * @return The tile at the specified position or Null if there is nothing.
     */
    public Tile get(int row, int col) {
        return isCellExists(row, col) ? tiles[col][row] : null;
    }

    /**
     * Returns whether the grid is empty.
     *
     * @return True if the grid is empty, false otherwise.
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Completes and checks chunks of tiles in different directions from a given position, and updates the copyLine accordingly.
     *
     * @param row                The starting row.
     * @param col                The starting column.
     * @param copyLine           The list of tiles being built and checked.
     * @param currTile           The current tile being added to the line.
     * @param possibleDirections The list of possible directions to extend the line.
     * @param mainDirection      The main direction of the line.
     * @return The score obtained for a move.
     * @throws QwirkleException If there are issues with the tile placement or line validity.
     */
    private int completeAndCheckChunks(int row, int col, ArrayList<Tile> copyLine, Tile currTile,
                                       ArrayList<Direction> possibleDirections, Direction mainDirection) {
        int score = 0;
        for (Direction possibleDirection : possibleDirections) {
            ArrayList<Tile> chunk1 = getChunkOfLine(row, col, possibleDirection);
            if (possibleDirection == mainDirection.opposite() || possibleDirection == mainDirection) {
                if (!copyLine.containsAll(chunk1)) {
                    copyLine.addAll(chunk1);
                }
                continue;
            }

            if (possibleDirections.contains(possibleDirection.opposite())) {
                ArrayList<Tile> chunk2 = getChunkOfLine(row, col, possibleDirection.opposite());
                chunk1.addAll(chunk2);
                possibleDirections.remove(possibleDirection.opposite());
            }
            chunk1.add(currTile);
            checkLineValidity(chunk1);
            score += getScore(chunk1);
        }
        return score;
    }


    /**
     * Checks if the current tile is adjacent to the previous tile of the hand's player.
     *
     * @param currTile  The current tile.
     * @param prevTile  The previous tile.
     * @param direction The direction to check.
     * @return True if there is a previous tile in the specified direction, false otherwise.
     */
    private static boolean isAdjacentToPrevTile(TileAtPosition currTile, TileAtPosition prevTile, Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            int prevRow = currTile.row() - direction.getDeltaRow();
            return prevRow == prevTile.row();
        } else {
            int prevCol = currTile.col() - direction.getDeltaCol();
            return prevCol == prevTile.col();
        }
    }

    /**
     * Checks if all tiles in the array are aligned in the same direction.
     *
     * @param line The array of TileAtPosition objects representing tiles and their positions.
     * @return The common direction in which tiles are aligned.
     * @throws QwirkleException If tiles are not aligned in the same direction.
     */
    private Direction checkIfTilesAreAlignInSameDirection(TileAtPosition[] line) {
        Direction direction = getDirection(line[0], line[1]);
        for (int i = 0; i < line.length; i++) {
            for (int j = 0; j < i; j++) {
                if (getDirection(line[j], line[i]) != direction) {
                    throw new QwirkleException("You have to keep the same direction.");
                }
            }
        }
        return direction;
    }

    /**
     * Checks if two TileAtPosition objects share the same cell coordinates.
     *
     * @param tap1 The first TileAtPosition object.
     * @param tap2 The second TileAtPosition object.
     * @return True if the two TileAtPosition objects have the same cell coordinates, false otherwise.
     */
    private static boolean areUsingSameCell(TileAtPosition tap1, TileAtPosition tap2) {
        return tap1.col() == tap2.col() && tap1.row() == tap2.row();
    }

    /**
     * Checks if two TileAtPosition objects have a common row or column.
     *
     * @param tap1 The first TileAtPosition object.
     * @param tap2 The second TileAtPosition object.
     * @return True if the two TileAtPosition objects share a common row or column, false otherwise.
     */
    private static boolean haveCommonRowOrCol(TileAtPosition tap1, TileAtPosition tap2) {
        return tap1.col() == tap2.col() || tap1.row() == tap2.row();
    }

    /**
     * Determines the direction between two TileAtPosition objects.
     *
     * @param tap1 The first tile.
     * @param tap2 The second tile.
     * @return The direction from tile1 to tile2.
     * @throws QwirkleException If tiles are in the same position.
     */
    private static Direction getDirection(TileAtPosition tap1, TileAtPosition tap2) {
        if (areUsingSameCell(tap1, tap2)) {
            throw new QwirkleException("Prohibited: Tiles sharing the same cell.");
        }

        if (!haveCommonRowOrCol(tap1, tap2)) {
            throw new QwirkleException("Prohibited: Tiles are not aligned.");
        }


        if (tap2.row() - tap1.row() > 0) return Direction.DOWN;
        else if (tap2.row() - tap1.row() < 0) return Direction.UP;
        else if (tap2.col() - tap1.col() > 0) return Direction.RIGHT;
        else return Direction.LEFT;
    }

    /**
     * Checks whether a player is playing by color or shape.
     *
     * @param tile1 The first tile.
     * @param tile2 The second tile.
     * @return True if playing by color, false if playing by shape.
     * @throws QwirkleException If both tiles have the same color and shape.
     */

    private static boolean checkWhatIsHePlaying(Tile tile1, Tile tile2) {
        boolean areSameColor = tile1.color() == tile2.color();
        boolean areSameShape = tile1.shape() == tile2.shape();
        if (areSameShape && areSameColor) throw new QwirkleException("Duplicate tiles found on a single line.");
        else if (!areSameShape && !areSameColor) throw new QwirkleException("Tiles have no shared attributes.");
        else return areSameColor;
    }

    /**
     * Checks the validity of a line of tiles.
     *
     * @param line The line of tiles to check.
     * @throws QwirkleException If the line contains more than 6 tiles or duplicates.
     */
    private static void checkLineValidity(ArrayList<Tile> line) {
        if (line.isEmpty()) throw new QwirkleException("At least one tile required on a line.");
        if (line.size() > MAX_TILES_PER_LINE) throw new QwirkleException("Maximum of six tiles allowed on a line.");
        if (line.size() > 1) {
            boolean result = checkWhatIsHePlaying(line.get(0), line.get(1));
            for (int i = 2; i < line.size(); i++) {
                for (int j = 0; j < i; j++) {
                    if (result != checkWhatIsHePlaying(line.get(i), line.get(j))) {
                        throw new QwirkleException("Tiles have no shared attributes.");
                    }
                }
            }
        }
    }

    /**
     * Retrieves a line of tiles in a specified direction from a given position.
     *
     * @param row       The starting row.
     * @param col       The starting column.
     * @param direction The direction of the line.
     * @return An array of tiles in the specified line.
     */
    private ArrayList<Tile> getChunkOfLine(int row, int col, Direction direction) {
        ArrayList<Tile> line = new ArrayList<>(MAX_TILES_PER_LINE);
        row += direction.getDeltaRow();
        col += direction.getDeltaCol();
        while (isCellExists(row, col) && isCellOccupied(row, col)) {
            if (line.size() > MAX_TILES_PER_LINE) throw new QwirkleException("Maximum of six tiles allowed on a line.");
            line.add(tiles[col][row]);
            row += direction.getDeltaRow();
            col += direction.getDeltaCol();
        }
        return line;
    }

    /**
     * Checks the validity of a cell for tile placement.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @throws QwirkleException If the cell is outside the grid, already occupied, or not adjacent to a line.
     */
    private void checkCellValidity(int row, int col) {
        if (!isCellExists(row, col)) throw new QwirkleException("The chosen cell is located outside the grid.");
        if (isCellOccupied(row, col)) throw new QwirkleException("The chosen cell is already occupied by a tile.");
    }

    /**
     * Checks whether a cell exists within the grid boundaries.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return True if the cell exists, false otherwise.
     */
    private static boolean isCellExists(int row, int col) {
        return row < GRID_ROW_SIZE && col < GRID_COL_SIZE;
    }


    /**
     * Checks whether a cell is occupied by a tile.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return True if the cell is occupied, false otherwise.
     */
    private boolean isCellOccupied(int row, int col) {
        return tiles[col][row] != null;
    }


    /**
     * Retrieves a list of possible directions for placing a tile at the specified cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return A list of valid directions for tile placement.
     * @throws QwirkleException If the tile is not adjacent to any line on the grid.
     */
    private ArrayList<Direction> getPossibleDirections(int row, int col) {
        ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        directions.removeIf(d -> {
            int rowWithOffset = row + d.getDeltaRow();
            int colWithOffset = col + d.getDeltaCol();
            return !isCellExists(rowWithOffset, colWithOffset) || !isCellOccupied(rowWithOffset, colWithOffset);
        });
        return directions;
    }

    /**
     * Calculates the score for a given line of tiles.
     *
     * @param line The line of tiles to calculate the score for.
     * @return The score obtained for the line.
     */
    private int getScore(ArrayList<Tile> line) {
        if (line.size() == 6) {
            return line.size() * 2;
        } else {
            return line.size();
        }
    }
}
