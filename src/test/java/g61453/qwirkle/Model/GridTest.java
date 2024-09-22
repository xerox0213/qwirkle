package g61453.qwirkle.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static g61453.qwirkle.Model.Direction.*;
import static g61453.qwirkle.Model.Shape.*;
import static g61453.qwirkle.Model.Color.*;

class GridTest {

    TileAtPosition createTAP(int row, int col, Color color, Shape shape) {
        Tile tile = new Tile(color, shape);
        return new TileAtPosition(row, col, tile);
    }

    private Grid grid;

    @BeforeEach
    void setupGrid() {
        grid = new Grid();
    }

    @Test
    void firstAdd_one_tile() {
        Tile tile = new Tile(BLUE, CROSS);
        assertDoesNotThrow(() -> grid.firstAdd(RIGHT, tile));
        assertEquals(tile, grid.get(45, 45));
    }

    @Test
    void firstAdd_cannot_be_called_twice() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, DIAMOND);
        grid.firstAdd(UP, tile1, tile2);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.firstAdd(DOWN, tile1, tile2));
        String expectedMsg = "Utilize this method strictly during the very first turn of the game!";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void add_one_tile_must_be_called_second() {
        Tile tile = new Tile(RED, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 45, tile));
        String expectedMsd = "Method not to be used during the initial turn of the game!";
        assertEquals(expectedMsd, q.getMessage());
        assertNull(grid.get(45, 45));
    }

    @Test
    void add_one_tile_chosen_cell_outside_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        grid.firstAdd(UP, tile1);
        Tile tile2 = new Tile(RED, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(99, 46, tile2));
        String expectedMsg = "The chosen cell is located outside the grid.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(99, 46));
    }

    @Test
    void add_one_tile_chosen_cell_already_occupied() {
        Tile tile1 = new Tile(RED, CROSS);
        grid.firstAdd(UP, tile1);
        Tile tile2 = new Tile(RED, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 45, tile2));
        String expectedMsg = "The chosen cell is already occupied by a tile.";
        assertEquals(expectedMsg, q.getMessage());
        assertEquals(tile1, grid.get(45, 45));
    }

    @Test
    void add_one_tile_chosen_cell_not_connected_to_existing_tile_on_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        grid.firstAdd(UP, tile1);
        Tile tile2 = new Tile(RED, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(48, 74, tile2));
        String expectedMsg = "Prohibited action: Tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(48, 74));
    }

    @Test
    void add_one_tile_duplicate_tile() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 46, tile3));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(45, 46));
    }

    @Test
    void add_one_tile_no_common_attribute() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, ROUND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 46, tile3));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(45, 46));
    }

    @Test
    void add_line_of_tile_must_be_called_second() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 45, UP, tile1, tile2));
        String expectedMsg = "Method not to be used during the initial turn of the game!";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(45, 45));
        assertNull(grid.get(44, 45));
    }

    @Test
    void add_line_of_tile_chosen_cell_outside_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        Tile tile4 = new Tile(GREEN, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(98, 45, UP, tile3, tile4));
        String expectedMsg = "The chosen cell is located outside the grid.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void add_line_of_tile_chosen_cell_is_already_occupied() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        Tile tile4 = new Tile(GREEN, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(44, 45, DOWN, tile3, tile4));
        String expectedMsg = "The chosen cell is already occupied by a tile.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(44, 45));
    }

    @Test
    void add_line_of_tile_not_connected_to_existing_tile_on_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        Tile tile4 = new Tile(GREEN, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(89, 45, DOWN, tile3, tile4));
        String expectedMsg = "Prohibited action: First tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(89, 45));
        assertNull(grid.get(90, 45));
    }

    @Test
    void add_line_of_tile_duplicate_tile() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        Tile tile4 = new Tile(RED, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(44, 45, UP, tile3, tile4));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(44, 45));
        assertNull(grid.get(43, 45));
    }

    @Test
    void add_line_of_tile_no_common_attribute() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        Tile tile4 = new Tile(YELLOW, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(44, 45, UP, tile3, tile4));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(44, 45));
        assertNull(grid.get(43, 45));
    }

    @Test
    void add_line_of_tile_valid_line_of_tile_does_not_throw_qwirkle_exception() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        Tile tile4 = new Tile(GREEN, CROSS);
        assertDoesNotThrow(() -> grid.add(44, 45, UP, tile3, tile4));
        assertEquals(tile3, grid.get(44, 45));
        assertEquals(tile4, grid.get(43, 45));
    }

    @Test
    void add_line_of_tile_contains_one_tile_does_not_throw_qwirkle_exception() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, SQUARE);
        grid.firstAdd(LEFT, tile1, tile2);
        Tile tile3 = new Tile(BLUE, CROSS);
        assertDoesNotThrow(() -> grid.add(44, 45, UP, tile3));
        assertEquals(tile3, grid.get(44, 45));
    }

    @Test
    void add_line_of_tile_at_position_must_be_called_second() {
        TileAtPosition tap1 = createTAP(45, 45, RED, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1));
        String expectedMsg = "Method not to be used during the initial turn of the game!";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
    }

    @Test
    void add_line_of_tile_at_position_not_align() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, BLUE, CROSS);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(43, 44, YELLOW, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "Prohibited: Tiles are not aligned.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
    }

    @Test
    void add_line_of_tile_at_position_tiles_use_a_same_case() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, BLUE, CROSS);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(44, 45, YELLOW, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "Prohibited: Tiles sharing the same cell.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
    }

    @Test
    void add_line_of_tile_at_position_tiles_tap_do_not_go_in_same_direction() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, BLUE, CROSS);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(45, 45, YELLOW, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "You have to keep the same direction.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertEquals(tile1, grid.get(45, 45));
    }

    @Test
    void add_line_of_tile_at_position_duplicate_tap() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, BLUE, CROSS);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(42, 45, BLUE, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
    }

    @Test
    void add_line_of_tile_at_position_no_common_carac() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, YELLOW, STAR);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(42, 45, BLUE, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
    }

    @Test
    void add_line_of_tile_at_the_length_of_line_must_be_maximum_six() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, YELLOW, STAR);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(42, 45, RED, CROSS);
        TileAtPosition tap4 = createTAP(41, 45, BLUE, CROSS);
        TileAtPosition tap5 = createTAP(40, 45, PURPLE, CROSS);
        TileAtPosition tap6 = createTAP(39, 45, ORANGE, CROSS);
        TileAtPosition tap7 = createTAP(38, 45, YELLOW, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3, tap4, tap5, tap6, tap7));
        String expectedMsg = "Maximum of six tiles allowed on a line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
        assertNull(grid.get(tap4.row(), tap4.col()));
        assertNull(grid.get(tap5.row(), tap5.col()));
        assertNull(grid.get(tap6.row(), tap6.col()));
        assertNull(grid.get(tap7.row(), tap7.col()));
    }

    @Test
    void add_line_of_tile_at_position_taps_must_connect_to_prev_tile_or_existing_tile_on_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(44, 45, YELLOW, STAR);
        TileAtPosition tap2 = createTAP(43, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(41, 45, BLUE, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "Prohibited action: Tiles must connect to existing line or tile of your hand.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
    }

    @Test
    void add_line_of_tile_at_position_first_tap_must_connect_to_existing_tile_on_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(RED, STAR);
        grid.firstAdd(LEFT, tile1, tile2);
        TileAtPosition tap1 = createTAP(43, 45, YELLOW, STAR);
        TileAtPosition tap2 = createTAP(42, 45, GREEN, CROSS);
        TileAtPosition tap3 = createTAP(41, 45, BLUE, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        String expectedMsg = "Prohibited action: First tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
        assertNull(grid.get(tap1.row(), tap1.col()));
        assertNull(grid.get(tap2.row(), tap2.col()));
        assertNull(grid.get(tap3.row(), tap3.col()));
    }


    @Test
    void rules_sonia_a_valid_line() {
        Tile t1 = new Tile(RED, ROUND);
        Tile t2 = new Tile(RED, DIAMOND);
        Tile t3 = new Tile(RED, PLUS);
        Tile[] tiles = {t1, t2, t3};
        assertDoesNotThrow(() -> grid.firstAdd(UP, tiles));
        for (int i = 0; i < tiles.length; i++) {
            int row = 45 + i * UP.getDeltaRow();
            int col = 45 + i * UP.getDeltaCol();
            assertEquals(tiles[i], grid.get(row, col));
        }
    }

    @Test
    void rules_sonia_a_adapted_to_fail_duplicate_tiles() {
        Tile t1 = new Tile(RED, ROUND);
        Tile t2 = new Tile(RED, DIAMOND);
        Tile t3 = new Tile(RED, DIAMOND);
        Tile[] tiles = {t1, t2, t3};
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.firstAdd(UP, tiles));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
        for (int i = 0; i < tiles.length; i++) {
            int row = 45 + i * UP.getDeltaRow();
            int col = 45 + i * UP.getDeltaCol();
            assertNull(grid.get(row, col));
        }
    }

    @Test
    void rules_sonia_a_adapted_to_fail_tiles_do_not_have_common_attribute() {
        Tile t1 = new Tile(RED, ROUND);
        Tile t2 = new Tile(BLUE, PLUS);
        Tile t3 = new Tile(RED, DIAMOND);
        Tile[] tiles = {t1, t2, t3};
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.firstAdd(UP, tiles));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
        for (int i = 0; i < tiles.length; i++) {
            int row = 45 + i * UP.getDeltaRow();
            int col = 45 + i * UP.getDeltaCol();
            assertNull(grid.get(row, col));
        }
    }

    @Test
    void rules_sonia_a_adapted_to_fail_more_than_six_tiles() {
        Tile t1 = new Tile(RED, ROUND);
        Tile t2 = new Tile(RED, PLUS);
        Tile t3 = new Tile(RED, CROSS);
        Tile t4 = new Tile(RED, DIAMOND);
        Tile t5 = new Tile(RED, SQUARE);
        Tile t6 = new Tile(RED, STAR);
        Tile t7 = new Tile(RED, DIAMOND);
        Tile[] tiles = {t1, t2, t3, t4, t5, t6, t7};
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.firstAdd(UP, tiles));
        String expectedMsg = "Maximum of six tiles allowed on a line.";
        assertEquals(expectedMsg, q.getMessage());
        for (int i = 0; i < tiles.length; i++) {
            int row = 45 + i * UP.getDeltaRow();
            int col = 45 + i * UP.getDeltaCol();
            assertNull(grid.get(row, col));
        }
    }

    @Test
    void rules_sonia_a_adapted_to_fail_empty_line() {
        Tile[] tiles = new Tile[0];
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.firstAdd(UP, tiles));
        assertNull(grid.get(45, 45));
        String expectedMsg = "At least one tile required on a line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_cedric_b_valid_line() {
        rules_sonia_a_valid_line();
        Tile t1 = new Tile(RED, SQUARE);
        Tile t2 = new Tile(BLUE, SQUARE);
        Tile t3 = new Tile(PURPLE, SQUARE);
        assertDoesNotThrow(() -> grid.add(46, 45, RIGHT, t1, t2, t3));
        assertEquals(t1, grid.get(46, 45));
        assertEquals(t2, grid.get(46, 46));
        assertEquals(t3, grid.get(46, 47));
    }

    @Test
    void rules_cedric_b_adapted_adapted_to_fail_duplicate_tiles() {
        rules_sonia_a_valid_line();
        Tile t1 = new Tile(RED, SQUARE);
        Tile t2 = new Tile(BLUE, SQUARE);
        Tile t3 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(46, 45, RIGHT, t1, t2, t3));
        assertNull(grid.get(46, 45));
        assertNull(grid.get(46, 46));
        assertNull(grid.get(46, 47));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());

    }

    @Test
    void rules_cedric_b_adapted_adapted_to_fail_no_common_attribute() {
        rules_sonia_a_valid_line();
        Tile t1 = new Tile(RED, SQUARE);
        Tile t2 = new Tile(BLUE, SQUARE);
        Tile t3 = new Tile(YELLOW, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(46, 45, RIGHT, t1, t2, t3));
        assertNull(grid.get(46, 45));
        assertNull(grid.get(46, 46));
        assertNull(grid.get(46, 47));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_c_valid_line() {
        rules_cedric_b_valid_line();
        Tile t1 = new Tile(BLUE, ROUND);
        assertDoesNotThrow(() -> grid.add(45, 46, t1));
        assertEquals(t1, grid.get(45, 46));
    }

    @Test
    void rules_elvire_c_adapted_to_fail_no_common_attribute() {
        rules_cedric_b_valid_line();
        Tile t1 = new Tile(YELLOW, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 46, t1));
        assertNull(grid.get(45, 46));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_c_adapted_to_fail_duplicate_tile() {
        rules_cedric_b_valid_line();
        Tile t1 = new Tile(BLUE, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(45, 46, t1));
        assertNull(grid.get(45, 46));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_d_valid_line() {
        rules_elvire_c_valid_line();
        Tile t1 = new Tile(GREEN, PLUS);
        Tile t2 = new Tile(GREEN, DIAMOND);
        assertDoesNotThrow(() -> grid.add(43, 44, DOWN, t1, t2));
        assertEquals(t1, grid.get(43, 44));
        assertEquals(t2, grid.get(44, 44));
    }

    @Test
    void rules_vincent_d_adapted_to_fail_no_common_attribute() {
        rules_elvire_c_valid_line();
        Tile t1 = new Tile(GREEN, PLUS);
        Tile t2 = new Tile(GREEN, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(43, 44, DOWN, t1, t2));
        assertNull(grid.get(43, 44));
        assertNull(grid.get(44, 44));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_d_adapted_to_fail_duplicate_tiles() {
        rules_elvire_c_valid_line();
        Tile t1 = new Tile(RED, PLUS);
        Tile t2 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(43, 44, DOWN, t1, t2));
        assertNull(grid.get(43, 44));
        assertNull(grid.get(44, 44));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_e_valid() {
        rules_vincent_d_valid_line();
        TileAtPosition tap1 = createTAP(42, 44, GREEN, STAR);
        TileAtPosition tap2 = createTAP(45, 44, GREEN, ROUND);
        assertDoesNotThrow(() -> grid.add(tap1, tap2));
        assertEquals(tap1.tile(), grid.get(42, 44));
        assertEquals(tap2.tile(), grid.get(45, 44));
    }

    @Test
    void rules_sonia_e_adapted_to_fail_no_common_attribute() {
        rules_vincent_d_valid_line();
        TileAtPosition tap1 = createTAP(42, 44, GREEN, STAR);
        TileAtPosition tap2 = createTAP(45, 44, PURPLE, ROUND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2));
        assertNull(grid.get(42, 44));
        assertNull(grid.get(45, 44));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_e_adapted_to_fail_taps_do_not_keep_same_direction() {
        rules_vincent_d_valid_line();
        TileAtPosition tap1 = createTAP(42, 44, GREEN, STAR);
        TileAtPosition tap2 = createTAP(45, 44, GREEN, PLUS);
        TileAtPosition tap3 = createTAP(41, 44, GREEN, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        assertNull(grid.get(42, 44));
        assertNull(grid.get(45, 44));
        assertNull(grid.get(41, 44));
        String expectedMsg = "You have to keep the same direction.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_e_adapted_to_fail_taps_not_align() {
        rules_vincent_d_valid_line();
        TileAtPosition tap1 = createTAP(42, 44, GREEN, STAR);
        TileAtPosition tap2 = createTAP(45, 44, GREEN, PLUS);
        TileAtPosition tap3 = createTAP(46, 48, GREEN, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        assertNull(grid.get(42, 44));
        assertNull(grid.get(45, 44));
        assertNull(grid.get(46, 48));
        String expectedMsg = "Prohibited: Tiles are not aligned.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_e_adapted_to_fail_taps_use_same_case() {
        rules_vincent_d_valid_line();
        TileAtPosition tap1 = createTAP(42, 44, GREEN, STAR);
        TileAtPosition tap2 = createTAP(45, 44, GREEN, PLUS);
        TileAtPosition tap3 = createTAP(42, 44, GREEN, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(tap1, tap2, tap3));
        assertNull(grid.get(42, 44));
        assertNull(grid.get(45, 44));
        assertNull(grid.get(46, 48));
        String expectedMsg = "Prohibited: Tiles sharing the same cell.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_cedric_f_valid_line() {
        rules_sonia_e_valid();
        Tile tile1 = new Tile(ORANGE, SQUARE);
        Tile tile2 = new Tile(RED, SQUARE);
        assertDoesNotThrow(() -> grid.add(46, 48, DOWN, tile1, tile2));
        assertEquals(tile1, grid.get(46, 48));
        assertEquals(tile2, grid.get(47, 48));
    }

    @Test
    void rules_cedric_f_adapted_to_fail_duplicate_tiles() {
        rules_sonia_e_valid();
        Tile tile1 = new Tile(ORANGE, SQUARE);
        Tile tile2 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(46, 48, RIGHT, tile1, tile2));
        assertNull(grid.get(46, 48));
        assertNull(grid.get(47, 48));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_cedric_f_adapted_to_fail_no_common_carac() {
        rules_sonia_e_valid();
        Tile tile1 = new Tile(ORANGE, SQUARE);
        Tile tile2 = new Tile(BLUE, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(46, 48, DOWN, tile1, tile2));
        assertNull(grid.get(46, 48));
        assertNull(grid.get(47, 48));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_g_valid_line() {
        rules_cedric_f_valid_line();
        Tile tile1 = new Tile(YELLOW, STAR);
        Tile tile2 = new Tile(ORANGE, STAR);
        assertDoesNotThrow(() -> grid.add(42, 43, LEFT, tile1, tile2));
        assertEquals(tile1, grid.get(42, 43));
        assertEquals(tile2, grid.get(42, 42));
    }

    @Test
    void rules_elvire_g_adapted_to_fail_duplicate_tiles() {
        rules_cedric_f_valid_line();
        Tile tile1 = new Tile(YELLOW, STAR);
        Tile tile2 = new Tile(GREEN, STAR);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(42, 43, LEFT, tile1, tile2));
        assertNull(grid.get(42, 43));
        assertNull(grid.get(42, 42));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_g_adapted_to_fail_no_common_carac() {
        rules_cedric_f_valid_line();
        Tile tile1 = new Tile(YELLOW, STAR);
        Tile tile2 = new Tile(GREEN, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(42, 43, LEFT, tile1, tile2));
        assertNull(grid.get(42, 43));
        assertNull(grid.get(42, 42));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_g_adapted_to_fail_first_tile_must_connect_to_line() {
        rules_cedric_f_valid_line();
        Tile tile1 = new Tile(YELLOW, STAR);
        Tile tile2 = new Tile(GREEN, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(42, 42, LEFT, tile1, tile2));
        assertNull(grid.get(42, 42));
        assertNull(grid.get(42, 41));
        String expectedMsg = "Prohibited action: First tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_h_valid_line() {
        rules_elvire_g_valid_line();
        Tile tile1 = new Tile(ORANGE, CROSS);
        Tile tile2 = new Tile(ORANGE, DIAMOND);
        assertDoesNotThrow(() -> grid.add(43, 42, DOWN, tile1, tile2));
        assertEquals(tile1, grid.get(43, 42));
        assertEquals(tile2, grid.get(44, 42));
    }

    @Test
    void rules_vincent_h_adapted_to_fail_duplicate_tile() {
        rules_elvire_g_valid_line();
        Tile tile1 = new Tile(ORANGE, CROSS);
        Tile tile2 = new Tile(ORANGE, STAR);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(43, 42, DOWN, tile1, tile2));
        assertNull(grid.get(43, 42));
        assertNull(grid.get(44, 42));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_h_adapted_to_fail_no_common_attribute() {
        rules_elvire_g_valid_line();
        Tile tile1 = new Tile(ORANGE, CROSS);
        Tile tile2 = new Tile(BLUE, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(43, 42, DOWN, tile1, tile2));
        assertNull(grid.get(43, 42));
        assertNull(grid.get(44, 42));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_h_adapted_to_fail_first_tile_must_be_connected_to_line() {
        rules_elvire_g_valid_line();
        Tile tile1 = new Tile(ORANGE, CROSS);
        Tile tile2 = new Tile(BLUE, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(43, 41, DOWN, tile1, tile2));
        assertNull(grid.get(43, 41));
        assertNull(grid.get(44, 41));
        String expectedMsg = "Prohibited action: First tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_i_valid_line() {
        rules_vincent_h_valid_line();
        Tile tile1 = new Tile(YELLOW, DIAMOND);
        Tile tile2 = new Tile(YELLOW, ROUND);
        assertDoesNotThrow(() -> grid.add(44, 43, DOWN, tile1, tile2));
        assertEquals(tile1, grid.get(44, 43));
        assertEquals(tile2, grid.get(45, 43));
    }

    @Test
    void rules_sonia_i_adapted_to_fail_duplicate_tile() {
        rules_vincent_h_valid_line();
        Tile tile1 = new Tile(YELLOW, DIAMOND);
        Tile tile2 = new Tile(RED, ROUND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(44, 43, DOWN, tile1, tile2));
        assertNull(grid.get(44, 43));
        assertNull(grid.get(45, 43));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_i_adapted_to_fail_no_common_attribute() {
        rules_vincent_h_valid_line();
        Tile tile1 = new Tile(YELLOW, DIAMOND);
        Tile tile2 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(44, 43, DOWN, tile1, tile2));
        assertNull(grid.get(44, 43));
        assertNull(grid.get(45, 43));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_sonia_i_adapted_to_fail_first_tile_must_be_connected_to_line() {
        rules_vincent_h_valid_line();
        Tile tile1 = new Tile(YELLOW, DIAMOND);
        Tile tile2 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(78, 43, DOWN, tile1, tile2));
        assertNull(grid.get(78, 43));
        assertNull(grid.get(79, 43));
        String expectedMsg = "Prohibited action: First tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_cedric_j_valid_line() {
        rules_sonia_i_valid_line();
        Tile tile1 = new Tile(RED, STAR);
        assertDoesNotThrow(() -> grid.add(42, 45, tile1));
        assertEquals(tile1, grid.get(42, 45));
    }

    @Test
    void rules_cedric_j_adapted_to_fail_no_common_attribute() {
        rules_sonia_i_valid_line();
        Tile tile1 = new Tile(ORANGE, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(42, 45, tile1));
        assertNull(grid.get(42, 45));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_cedric_j_adapted_to_fail_must_be_connected_to_a_line() {
        rules_sonia_i_valid_line();
        Tile tile1 = new Tile(ORANGE, STAR);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(75, 45, tile1));
        assertNull(grid.get(75, 45));
        String expectedMsg = "Prohibited action: Tile must connect to existing line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_k_valid_line() {
        rules_cedric_j_valid_line();
        Tile tile1 = new Tile(BLUE, CROSS);
        Tile tile2 = new Tile(RED, CROSS);
        Tile tile3 = new Tile(ORANGE, CROSS);
        assertDoesNotThrow(() -> grid.add(47, 46, LEFT, tile1, tile2, tile3));
        assertEquals(tile1, grid.get(47, 46));
        assertEquals(tile2, grid.get(47, 45));
        assertEquals(tile3, grid.get(47, 44));
    }

    @Test
    void rules_elvire_k_adapted_to_fail_no_common_attribute() {
        rules_cedric_j_valid_line();
        Tile tile1 = new Tile(BLUE, CROSS);
        Tile tile2 = new Tile(GREEN, CROSS);
        Tile tile3 = new Tile(ORANGE, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(47, 46, LEFT, tile1, tile2, tile3));
        assertNull(grid.get(47, 46));
        assertNull(grid.get(47, 45));
        assertNull(grid.get(47, 44));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_elvire_k_adapted_to_fail_duplicate_tiles() {
        rules_cedric_j_valid_line();
        Tile tile1 = new Tile(BLUE, CROSS);
        Tile tile2 = new Tile(RED, CROSS);
        Tile tile3 = new Tile(BLUE, CROSS);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(47, 46, LEFT, tile1, tile2, tile3));
        assertNull(grid.get(47, 46));
        assertNull(grid.get(47, 45));
        assertNull(grid.get(47, 44));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_l_valid_line() {
        rules_elvire_k_valid_line();
        Tile tile1 = new Tile(YELLOW, SQUARE);
        Tile tile2 = new Tile(BLUE, SQUARE);
        assertDoesNotThrow(() -> grid.add(46, 49, DOWN, tile1, tile2));
        assertEquals(tile1, grid.get(46, 49));
        assertEquals(tile2, grid.get(47, 49));
    }

    @Test
    void rules_vincent_adapted_to_fail_no_common_attribute() {
        rules_elvire_k_valid_line();
        Tile tile1 = new Tile(YELLOW, SQUARE);
        Tile tile2 = new Tile(GREEN, DIAMOND);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(46, 49, DOWN, tile1, tile2));
        assertNull(grid.get(46, 49));
        assertNull(grid.get(47, 49));
        String expectedMsg = "Tiles have no shared attributes.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void rules_vincent_adapted_to_fail_duplicate_tiles() {
        rules_elvire_k_valid_line();
        Tile tile1 = new Tile(YELLOW, SQUARE);
        Tile tile2 = new Tile(RED, SQUARE);
        QwirkleException q = assertThrows(QwirkleException.class, () -> grid.add(46, 49, DOWN, tile1, tile2));
        assertNull(grid.get(46, 49));
        assertNull(grid.get(47, 49));
        String expectedMsg = "Duplicate tiles found on a single line.";
        assertEquals(expectedMsg, q.getMessage());
    }

    @Test
    void can_get_tile_outside_virtual_grid() {
        assertDoesNotThrow(() -> grid.get(98, 42));
        assertNull(grid.get(98, 42));
    }

    @Test
    void can_get_tile_inside_virtual_grid() {
        Tile tile1 = new Tile(RED, CROSS);
        Tile tile2 = new Tile(BLUE, CROSS);
        Tile tile3 = new Tile(GREEN, CROSS);
        Direction direction = LEFT;
        Tile[] tiles = {tile1, tile2, tile3};
        grid.firstAdd(direction, tiles);
        for (int i = 0; i < tiles.length; i++) {
            int row = 45 + i * direction.getDeltaRow();
            int col = 45 + i * direction.getDeltaCol();
            assertEquals(tiles[i], grid.get(row, col));
        }
    }

    @Test
    void rules_sonia_a_valid_line_score_should_be_3() {
        Tile t1 = new Tile(RED, ROUND);
        Tile t2 = new Tile(RED, DIAMOND);
        Tile t3 = new Tile(RED, PLUS);
        Tile[] tiles = {t1, t2, t3};
        assertEquals(3, grid.firstAdd(UP, tiles));
    }

    @Test
    void rules_cedric_b_valid_line_score_should_be_7() {
        rules_sonia_a_valid_line();
        Tile t1 = new Tile(RED, SQUARE);
        Tile t2 = new Tile(BLUE, SQUARE);
        Tile t3 = new Tile(PURPLE, SQUARE);
        assertEquals(7, grid.add(46, 45, RIGHT, t1, t2, t3));
    }

    @Test
    void rules_elvire_c_valid_line_score_should_be_4() {
        rules_cedric_b_valid_line();
        Tile t1 = new Tile(BLUE, ROUND);
        assertEquals(4, grid.add(45, 46, t1));
    }

    @Test
    void rules_vincent_d_valid_line_score_should_be_6() {
        rules_elvire_c_valid_line();
        Tile t1 = new Tile(GREEN, PLUS);
        Tile t2 = new Tile(GREEN, DIAMOND);
        assertEquals(6, grid.add(43, 44, DOWN, t1, t2));
    }

    @Test
    void rules_sonia_e_valid_score_should_be_7() {
        rules_vincent_d_valid_line();
        TileAtPosition tap1 = createTAP(42, 44, GREEN, STAR);
        TileAtPosition tap2 = createTAP(45, 44, GREEN, ROUND);
        assertEquals(7, grid.add(tap1, tap2));
    }

    @Test
    void rules_cedric_f_valid_line_score_should_be_6() {
        rules_sonia_e_valid();
        Tile tile1 = new Tile(ORANGE, SQUARE);
        Tile tile2 = new Tile(RED, SQUARE);
        assertEquals(6, grid.add(46, 48, DOWN, tile1, tile2));
    }

    @Test
    void rules_elvire_g_valid_line_score_should_be_3() {
        rules_cedric_f_valid_line();
        Tile tile1 = new Tile(YELLOW, STAR);
        Tile tile2 = new Tile(ORANGE, STAR);
        assertEquals(3, grid.add(42, 43, LEFT, tile1, tile2));
    }

    @Test
    void rules_vincent_h_valid_line_score_should_be_3() {
        rules_elvire_g_valid_line();
        Tile tile1 = new Tile(ORANGE, CROSS);
        Tile tile2 = new Tile(ORANGE, DIAMOND);
        assertEquals(3, grid.add(43, 42, DOWN, tile1, tile2));
    }

    @Test
    void rules_sonia_i_valid_line_score_should_be_10() {
        rules_vincent_h_valid_line();
        Tile tile1 = new Tile(YELLOW, DIAMOND);
        Tile tile2 = new Tile(YELLOW, ROUND);
        assertEquals(10, grid.add(44, 43, DOWN, tile1, tile2));
    }

    @Test
    void rules_cedric_j_valid_line_score_should_be_9() {
        rules_sonia_i_valid_line();
        Tile tile1 = new Tile(RED, STAR);
        assertEquals(9, grid.add(42, 45, tile1));
    }

    @Test
    void rules_elvire_k_valid_line_score_should_be_18() {
        rules_cedric_j_valid_line();
        Tile tile1 = new Tile(BLUE, CROSS);
        Tile tile2 = new Tile(RED, CROSS);
        Tile tile3 = new Tile(ORANGE, CROSS);
        assertEquals(18, grid.add(47, 46, LEFT, tile1, tile2, tile3));
    }

    @Test
    void rules_vincent_l_valid_line_score_should_be_9() {
        rules_elvire_k_valid_line();
        Tile tile1 = new Tile(YELLOW, SQUARE);
        Tile tile2 = new Tile(BLUE, SQUARE);
        assertEquals(9, grid.add(46, 49, DOWN, tile1, tile2));
    }

    @Test
    void isEmpty_must_be_true_at_first_round() {
        assertTrue(grid.isEmpty());
    }

    @Test
    void isEmpty_must_be_false_after_first_round() {
        Tile tile = new Tile(ORANGE, CROSS);
        grid.firstAdd(UP, tile);
        assertFalse(grid.isEmpty());
    }
}