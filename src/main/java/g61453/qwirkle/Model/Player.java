package g61453.qwirkle.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player in the Qwirkle game, including their name and hand of tiles.
 */
public class Player implements Serializable {
    private String name;
    private List<Tile> tiles;
    private int score;

    /**
     * Constructs a new Player object with the given name.
     *
     * @param name The name of the player.
     */
    Player(String name) {
        this.name = name;
        Bag bag = Bag.getInstance();
        Tile[] tiles = bag.getRandomTiles(6);
        this.tiles = new ArrayList<>(Arrays.asList(tiles));
        this.score = 0;
    }

    /**
     * Gets the name of the player.
     *
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    /**
     * Gets an unmodifiable view of the player's hand of tiles.
     *
     * @return An unmodifiable list of tiles in the player's hand.
     */
    public List<Tile> getHand() {
        return Collections.unmodifiableList(tiles);
    }

    /**
     * Refills the player's hand by drawing missing tiles from the bag.
     */
    public void refill() {
        Bag bag = Bag.getInstance();
        int numberOfMissingTile = 6 - tiles.size();
        Tile[] missingTiles = bag.getRandomTiles(numberOfMissingTile);
        this.tiles.addAll(Arrays.asList(missingTiles));
    }

    /**
     * Removes specified tiles from the player's hand.
     *
     * @param ts The tiles to be removed from the player's hand.
     */
    public void removeTile(Tile... ts) {
        for (Tile tile : ts) {
            tiles.remove(tile);
        }
    }

    public void addScore(int value) {
        score += value;
    }
}
