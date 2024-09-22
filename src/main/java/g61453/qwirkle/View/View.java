package g61453.qwirkle.View;

import g61453.qwirkle.Model.*;

import java.util.List;
import java.util.Objects;

/**
 * The View class provides methods for displaying the game state and information to the user.
 */
public class View {
    private static final String BOLD = "\033[1m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String RED = "\033[0;31m";
    private static final String RED_BOLD = "\033[1;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\u001B[38;5;226m";
    private static final String BLUE = "\033[0;34m";
    private static final String BLUE_BOLD = "\033[1;34m";
    private static final String PURPLE = "\033[0;35m";
    private static final String RESET = "\033[0m";
    private static final String SQUARE = "[]";
    private static final String ROUND = "O";
    private static final String CROSS = "X";
    private static final String STAR = "*";
    private static final String DIAMOND = "<>";
    private static final String PLUS = "+";

    private View() {
    }

    /**
     * Display the game grid to the user.
     *
     * @param grid The GridView representing the game grid.
     */
    public static void display(GridView grid) {
        int minRow = getMinRow(grid);
        int minCol = getMinCol(grid);
        int maxRow = getMaxRow(grid);
        int maxCol = getMaxCol(grid);

        if (minRow == -1 || minCol == -1 || maxRow == -1 || maxCol == -1) {
            return;
        }

        for (int row = minRow; row <= maxRow + 1; row++) {
            if (row == maxRow + 1) {
                System.out.print("    ");
            } else {
                System.out.print(row + " " + "|");
            }
            for (int col = minCol; col <= maxCol; col++) {
                if (row == maxRow + 1) {
                    System.out.print(" " + col);
                } else {
                    Tile tile = grid.get(row, col);
                    if (tile == null) {
                        System.out.print("   ");
                    } else {
                        String shape = getShape(tile);
                        String color = getColor(tile);
                        if (Objects.equals(shape, SQUARE) || Objects.equals(shape, DIAMOND)) {
                            System.out.print(color + " " + shape + RESET);
                        } else {
                            System.out.print(color + "  " + shape + RESET);
                        }
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * Displays the current player's turn and their hand.
     *
     * @param playerName The name of the current player.
     * @param playerHand The hand of the current player as a formatted string.
     */
    public static void display(String playerName, List<Tile> playerHand, int playerScore) {
        System.out.print(BLUE_BOLD + playerName + "'s Turn " + "(" + playerScore + " points)" + ":" + RESET);
        for (int i = 0; i < playerHand.size(); i++) {
            Tile tile = playerHand.get(i);
            String shape = getShape(tile);
            String color = getColor(tile);
            System.out.print(color + "  " + shape + RESET + " " + "(" + i + ")");
        }
        System.out.println();
    }

    /**
     * Displays the winner of the game.
     */
    public static void displayWinner(Player player) {
        System.out.println("The winner is: " + player.getName() + " with " + player.getScore() + " points");
    }

    /**
     * Display the game's help information to the user.
     */
    public static void displayHelp() {
        System.out.println(BOLD + "Q W I R K L E");
        System.out.println("Qwirkle command:");
        System.out.println("- play 1 tile : o <row> <col> <i>");
        System.out.println("- play line: l <row> <col> <direction> <i1> [<i2>]");
        System.out.println("- play plic-ploc : m <row1> <col1> <i1> [<row2> <col2> <i2>]");
        System.out.println("- play first : f [<direction>] <i1> [<i2>]");
        System.out.println("- pass : p");
        System.out.println("- save game : s <fileName>");
        System.out.println("- quit : q");
        System.out.println("    i : index in list of tiles");
        System.out.println("    d : direction in l (left), r (right), u (up), d(down)" + RESET);
    }

    /**
     * Display an error message to the user.
     *
     * @param errorMsg The error message to display.
     */
    public static void displayError(String errorMsg) {
        System.out.println(RED_BOLD + "Error: " + errorMsg + RESET);
    }

    /**
     * Get the minimum column index with a tile in the GridView.
     *
     * @param grid The GridView to search for tiles.
     * @return The minimum column index with a tile, or -1 if no tiles are found.
     */
    private static int getMinCol(GridView grid) {
        for (int col = 0; col < 91; col++) {
            for (int row = 0; row < 91; row++) {
                if (grid.get(row, col) != null) {
                    return col;
                }
            }
        }
        return -1;
    }

    /**
     * Get the minimum row index with a tile in the GridView.
     *
     * @param grid The GridView to search for tiles.
     * @return The minimum row index with a tile, or -1 if no tiles are found.
     */
    private static int getMinRow(GridView grid) {
        for (int row = 0; row < 91; row++) {
            for (int col = 0; col < 91; col++) {
                if (grid.get(row, col) != null) {
                    return row;
                }
            }
        }
        return -1;
    }

    /**
     * Get the maximum column index with a tile in the GridView.
     *
     * @param grid The GridView to search for tiles.
     * @return The maximum column index with a tile, or -1 if no tiles are found.
     */
    private static int getMaxCol(GridView grid) {
        for (int col = 90; col > -1; col--) {
            for (int row = 0; row < 91; row++) {
                if (grid.get(row, col) != null) {
                    return col;
                }
            }
        }
        return -1;
    }

    /**
     * Get the maximum row index with a tile in the GridView.
     *
     * @param grid The GridView to search for tiles.
     * @return The maximum row index with a tile, or -1 if no tiles are found.
     */
    private static int getMaxRow(GridView grid) {
        for (int row = 90; row > -1; row--) {
            for (int col = 0; col < 91; col++) {
                if (grid.get(row, col) != null) {
                    return row;
                }
            }
        }
        return -1;
    }

    /**
     * Get the representation of the shape of a tile.
     *
     * @param tile The tile whose shape to retrieve.
     * @return The ASCII representation of the tile's shape.
     */
    private static String getShape(Tile tile) {
        return switch (tile.shape().name()) {
            case "SQUARE" -> SQUARE;
            case "ROUND" -> ROUND;
            case "DIAMOND" -> DIAMOND;
            case "CROSS" -> CROSS;
            case "STAR" -> STAR;
            default -> PLUS;
        };
    }

    /**
     * Get the ANSI color code for the color of a tile.
     *
     * @param tile The tile whose color to retrieve.
     * @return The ANSI color code for the tile's color.
     */
    private static String getColor(Tile tile) {
        return switch (tile.color().name()) {
            case "BLUE" -> BLUE;
            case "RED" -> RED;
            case "PURPLE" -> PURPLE;
            case "GREEN" -> GREEN;
            case "YELLOW" -> YELLOW;
            default -> ORANGE;
        };
    }
}
