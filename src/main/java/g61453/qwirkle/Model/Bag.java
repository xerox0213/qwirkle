package g61453.qwirkle.Model;

import java.io.Serializable;
import java.util.*;

/**
 * The Bag class represents a bag of tiles containing different combinations of colors and shapes.
 * The bag contains 108 tiles, and it allows getting random sets of tiles from the bag.
 */
public class Bag implements Serializable {
    private static Bag instance;
    private final List<Tile> tiles;

    /**
     * Private constructor to initialize the bag with 108 tiles, combining colors and shapes.
     */
    private Bag() {
        Color[] colors = Color.values();
        Shape[] shapes = Shape.values();
        this.tiles = new ArrayList<>(108);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    Tile tile = new Tile(colors[k], shapes[j]);
                    tiles.add(tile);
                }
            }
        }
    }

    /**
     * Create an instance of the Bag class if it doesn't exist and return it otherwise return the existing instance.
     *
     * @return The instance of the Bag class.
     */
    public static Bag getInstance() {
        if (instance == null) {
            instance = new Bag();
        }
        return instance;
    }

    /**
     * Get a random array of tiles from the bag.
     *
     * @param n The number of tiles to be retrieved.
     * @return An array of tiles if there are enough tiles in the bag, or null if the bag is empty.
     */
    public Tile[] getRandomTiles(int n) {
        if(n <= 0){
            throw new QwirkleException("The number of tiles must be strictly positive.");
        }

        if(this.size() == 0){
            throw new QwirkleException("There are no more tiles in the bag.");
        }

        Tile[] randomTiles = new Tile[Math.min(n, tiles.size())];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int upperbound = tiles.size();
            int intRandom = random.nextInt(upperbound);
            Tile randomTile = tiles.remove(intRandom);
            randomTiles[i] = randomTile;
        }
        return randomTiles;
    }

    /**
     * Get the number of tiles currently in the bag.
     *
     * @return The number of tiles in the bag.
     */
    public int size() {
        return tiles.size();
    }
}
