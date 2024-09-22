package g61453.qwirkle.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {
    private Bag bag = Bag.getInstance();

    @Test
    void allTest() {
        constructor_the_size_of_bag_should_be_108();
        get_random_tiles_enough_tiles_in_bag_should_not_throw_Qwirkle_Exception();
        get_random_tiles_negative_number_should_throw_Qwirkle_Exception();
        get_random_tiles_no_more_tiles_in_bag_should_throw_Qwirkle_Exception();
    }

    void constructor_the_size_of_bag_should_be_108() {
        assertEquals(108, bag.size());
    }

    void get_random_tiles_enough_tiles_in_bag_should_not_throw_Qwirkle_Exception() {
        int n = 5;
        Tile[] randomTiles = assertDoesNotThrow(() -> bag.getRandomTiles(n));
        assertNotNull(randomTiles);
        assertEquals(n, randomTiles.length);
        for (int i = 0; i < n; i++) {
            assertNotNull(randomTiles[i]);
        }
    }

    void get_random_tiles_negative_number_should_throw_Qwirkle_Exception() {
        QwirkleException q = assertThrows(QwirkleException.class, () -> bag.getRandomTiles(-5));
        String expectedMsg = "The number of tiles must be strictly positive.";
        assertEquals(expectedMsg, q.getMessage());
    }

    void get_random_tiles_no_more_tiles_in_bag_should_throw_Qwirkle_Exception() {
        assertDoesNotThrow(() -> bag.getRandomTiles(bag.size()));
        QwirkleException q = assertThrows(QwirkleException.class, () -> bag.getRandomTiles(10));
        String expectedMsg = "There are no more tiles in the bag.";
        assertEquals(expectedMsg, q.getMessage());
    }
}