package g61453.qwirkle.Model;

import g61453.qwirkle.View.View;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game of Qwirkle, including the players, game grid, and gameplay logic.
 */
public class Game implements Serializable {
    private final GridView gridView;
    private final Grid grid;
    private final Player[] players;
    private int indexCurrPlayer;
    private int countPass;

    /**
     * Constructs a new Game object with the specified list of player names.
     *
     * @param names The list of player names.
     */
    public Game(List<String> names) {
        Player[] players = new Player[names.size()];
        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(names.get(i));
            players[i] = player;
        }
        this.players = players;
        this.indexCurrPlayer = 0;
        this.grid = new Grid();
        this.gridView = new GridView(this.grid);
    }

    /**
     * Places tiles for the first move on the grid.
     *
     * @param d  The direction of the tiles.
     * @param is The indexes of tiles in the current player's hand.
     * @throws QwirkleException If there are issues with the first move.
     */
    public void first(Direction d, int... is) {
        try {
            Player currPlayer = players[indexCurrPlayer];
            List<Tile> handCurrPlayer = getCurrentPlayerHand();
            Tile[] line = getLineOfTiles(handCurrPlayer, is);
            int score = grid.firstAdd(d, line);
            currPlayer.removeTile(line);
            currPlayer.refill();
            currPlayer.addScore(score);
            nextPlayer();
            resetCountPass();
        } catch (QwirkleException e) {
            View.displayError(e.getMessage());
        }

    }

    /**
     * Places a single tile on the grid.
     *
     * @param row   The row where the tile should be placed.
     * @param col   The column where the tile should be placed.
     * @param index The index of the tile in the current player's hand.
     * @throws QwirkleException If there are issues with tile placement.
     */
    public void play(int row, int col, int index) {
        try {
            Player currPlayer = players[indexCurrPlayer];
            List<Tile> handCurrPlayer = getCurrentPlayerHand();

            if (handCurrPlayer.isEmpty()) {
                throw new QwirkleException("Insufficient tiles for the action.");
            }

            Tile[] line = getLineOfTiles(handCurrPlayer, index);
            int score = grid.add(row, col, line[0]);
            currPlayer.removeTile(line[0]);
            currPlayer.refill();
            currPlayer.addScore(score);
            nextPlayer();
            resetCountPass();
        } catch (QwirkleException e) {
            View.displayError(e.getMessage());
        }

    }

    /**
     * Places multiple tiles on the grid in a specified direction.
     *
     * @param row     The row where the tiles should start.
     * @param col     The column where the tiles should start.
     * @param d       The direction of the tiles.
     * @param indexes The indexes of tiles in the current player's hand.
     * @throws QwirkleException If there are issues with tile placement.
     */
    public void play(int row, int col, Direction d, int... indexes) {
        try {
            Player currPlayer = players[indexCurrPlayer];
            List<Tile> handCurrPlayer = getCurrentPlayerHand();

            if (areThereInsufficientTiles(handCurrPlayer, indexes.length)) {
                throw new QwirkleException("Insufficient tiles for the action.");
            }

            Tile[] line = getLineOfTiles(handCurrPlayer, indexes);
            int score = grid.add(row, col, d, line);
            currPlayer.removeTile(line);
            currPlayer.refill();
            currPlayer.addScore(score);
            nextPlayer();
            resetCountPass();
        } catch (QwirkleException e) {
            View.displayError(e.getMessage());
        }
    }

    /**
     * Places multiple tiles on the grid based on a specific pattern.
     *
     * @param is The indexes of tiles in the current player's hand.
     * @throws QwirkleException If there are issues with tile placement.
     */
    public void play(int... is) {
        try {
            Player currPlayer = players[indexCurrPlayer];
            List<Tile> handCurrPlayer = getCurrentPlayerHand();

            if (areThereInsufficientTiles(handCurrPlayer, is.length / 3)) {
                throw new QwirkleException("Insufficient tiles for the action.");
            }

            Tile[] tilesToRemove = new Tile[is.length / 3];
            TileAtPosition[] line = getLineOfTAPS(handCurrPlayer, tilesToRemove, is);
            int score = grid.add(line);
            currPlayer.removeTile(tilesToRemove);
            currPlayer.refill();
            currPlayer.addScore(score);
            nextPlayer();
            resetCountPass();
        } catch (QwirkleException e) {
            View.displayError(e.getMessage());
        }
    }

    /**
     * Checks if there are an insufficient number of tiles in the provided hand of the current player.
     *
     * @param handCurrPlayer The list of tiles in the hand of the current player.
     * @param numberOfTiles  The threshold value to compare the tile count against.
     * @return true if the number of tiles in the hand is less than the threshold value, false otherwise.
     */
    private boolean areThereInsufficientTiles(List<Tile> handCurrPlayer, int numberOfTiles) {
        return handCurrPlayer.size() < numberOfTiles;
    }

    /**
     * Gets the name of the current player.
     *
     * @return The name of the current player.
     */
    public String getCurrentPlayerName() {
        return players[indexCurrPlayer].getName();
    }

    /**
     * Gets the current player's hand of tiles.
     *
     * @return The list of tiles in the current player's hand.
     */
    public List<Tile> getCurrentPlayerHand() {
        return players[indexCurrPlayer].getHand();
    }

    public int getCurrentPlayerScore() {
        return players[indexCurrPlayer].getScore();
    }

    /**
     * Advances the turn to the next player.
     */
    public void pass() {
        nextPlayer();
        if (isBagEmpty()) {
            countPass++;
        }
    }

    /**
     * Gets the game grid view.
     *
     * @return The game grid view.
     */
    public GridView getGrid() {
        return gridView;
    }

    /**
     * Checks if the game is over.
     * This method determines whether the game is over by evaluating two conditions:
     * 1. The bag of tiles is empty, indicating no more tiles are available.
     * 2. Either at least one player has an empty hand (indicating the game round has finished),
     * or every player has passed their turn.
     *
     * @return true if the game is over based on the specified conditions, false otherwise.
     */
    public boolean isOver() {
        return isBagEmpty() && (isThereEmptyHand() || didEveryPlayerPass());
    }

    /**
     * Retrieves an array of tiles based on their indexes in the player's hand.
     *
     * @param handCurrPlayer The current player's hand of tiles.
     * @param indexes        The indexes of tiles in the player's hand.
     * @return An array of tiles corresponding to the specified indexes.
     */
    private Tile[] getLineOfTiles(List<Tile> handCurrPlayer, int... indexes) {
        Tile[] line = new Tile[indexes.length];
        ArrayList<Integer> blacklistIndex = new ArrayList<>();
        for (int i = 0; i < indexes.length; i++) {
            int indexTile = indexes[i];

            if (indexTile >= handCurrPlayer.size()) {
                throw new QwirkleException("The selected tiles do not exist.");
            }

            if (blacklistIndex.contains(indexes[i])) {
                throw new QwirkleException("You cannot select many times the same tile.");
            }
            Tile tile = handCurrPlayer.get(indexTile);
            line[i] = tile;
            blacklistIndex.add(indexTile);
        }
        return line;
    }

    /**
     * Retrieves an array of TileAtPosition objects based on the indexes of tiles in the player's hand.
     *
     * @param handCurrPlayer The current player's hand of tiles.
     * @param tilesToRemove  An array to store the tiles that will be removed from the hand.
     * @param indexes        The indexes of tiles in the player's hand.
     * @return An array of TileAtPosition objects representing tiles and their positions.
     */
    private TileAtPosition[] getLineOfTAPS(List<Tile> handCurrPlayer, Tile[] tilesToRemove, int... indexes) {
        TileAtPosition[] line = new TileAtPosition[indexes.length / 3];
        ArrayList<Integer> blacklistIndex = new ArrayList<>();
        int indexLine = 0;
        for (int j = 0; j < indexes.length; j += 3) {
            int row = indexes[j];
            int col = indexes[j + 1];
            int indexTile = indexes[j + 2];

            if (indexTile >= handCurrPlayer.size()) {
                throw new QwirkleException("The selected tiles do not exist.");
            }

            if (blacklistIndex.contains(indexTile)) {
                throw new QwirkleException("You cannot select many times the same tile.");
            }

            Tile tile = handCurrPlayer.get(indexTile);
            TileAtPosition tap = new TileAtPosition(row, col, tile);
            line[indexLine] = tap;
            tilesToRemove[indexLine] = tile;
            indexLine++;
            blacklistIndex.add(indexTile);
        }
        return line;
    }

    /**
     * Checks whether the bag of tiles is empty.
     *
     * @return true if the bag of tiles has no remaining tiles, false otherwise.
     */
    private boolean isBagEmpty() {
        return Bag.getInstance().size() == 0;
    }

    /**
     * Checks whether there is any player with an empty hand.
     * This method iterates through the list of players and checks if any player's hand is empty.
     * If an empty hand is found, the player is awarded six points for finishing their tiles.
     *
     * @return true if at least one player has an empty hand, indicating the game is finishing,
     * false otherwise.
     */
    private boolean isThereEmptyHand() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                addSixPointsForFinishing(player);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if every player has passed their turn.
     * This method compares the count of passed turns to the total number of players to determine
     * if every player has passed their turn in the current round.
     *
     * @return true if the count of passed turns is equal to the total number of players,
     * indicating that every player has passed, false otherwise.
     */
    private boolean didEveryPlayerPass() {
        return countPass == players.length;
    }

    /**
     * Adds six points to a player's score for finishing their tiles.
     * This method increases the score of the provided player by six points as a reward
     * for successfully playing all their tiles and finishing the game round.
     *
     * @param player The player to whom six points are to be added for finishing.
     */
    private void addSixPointsForFinishing(Player player) {
        player.addScore(6);
    }

    /**
     * Advances the game to the next player's turn.
     * This method updates the current player index to point to the next player in line. If the current
     * player is the last in the array of players, the index wraps around to the first player.
     */
    private void nextPlayer() {
        if (indexCurrPlayer == players.length - 1) {
            indexCurrPlayer = 0;
        } else {
            indexCurrPlayer++;
        }
    }

    /**
     * Resets the count of passed turns to zero.
     * This method sets the count of passed turns to zero, effectively resetting the tracking
     * of how many players have passed their turn in the current round.
     */
    private void resetCountPass() {
        countPass = 0;
    }

    /**
     * Determines the winner of the game.
     * This method iterates through the array of players and compares their scores to find the player
     * with the highest score, who is then considered the winner of the game.
     *
     * @return The player with the highest score, thus being declared the winner of the game.
     */
    public Player getWinner() {
        Player player = players[0];
        for (int i = 1; i < players.length; i++) {
            Player nextPlayer = players[i];
            if (nextPlayer.getScore() > player.getScore()) {
                player = nextPlayer;
            }
        }
        return player;
    }

    /**
     * Serializes and writes the current Game instance to a file.
     *
     * @param fileName The name of the file to write the serialized Game object to.
     * @throws QwirkleException If there's an issue with creating directories, writing the file, or serializing the object.
     */
    public void write(String fileName) {
        String directoryName = "backups" + File.separator;
        try {
            Path directory = Paths.get(directoryName);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            FileOutputStream fos = new FileOutputStream(directoryName + fileName);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new QwirkleException("An error has occurred.");
        }
    }

    /**
     * Restores a serialized Game object from a file.
     *
     * @param fileName The name of the file containing the serialized Game object.
     * @return The deserialized Game instance restored from the file.
     * @throws QwirkleException If there's an issue with reading the file or deserializing the object.
     */
    public static Game getFromFile(String fileName) {
        String directoryName = "backups" + File.separator;
        try {
            FileInputStream fis = new FileInputStream(directoryName + fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Game game = (Game) ois.readObject();
            ois.close();
            return game;
        } catch (Exception e) {
            throw new QwirkleException("The file doesn't exist");
        }
    }
}
