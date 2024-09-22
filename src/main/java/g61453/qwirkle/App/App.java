package g61453.qwirkle.App;

import g61453.qwirkle.Model.*;
import g61453.qwirkle.View.View;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main application class for the Qwirkle game.
 * This class handles user input, game setup, and interaction with the Game and View classes.
 */
public class App {
    private static Game game;
    private static final Pattern PATTERN_PLAY_ONE_TILE = Pattern.compile("^o( [0-9]{1,2}){2} [0-6]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_PLAY_LINE = Pattern.compile("^l( [0-9]{1,2}){2} [lrud]( [0-6])+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_PLAY_PLIC_PLOC = Pattern.compile("^m(( [0-9]{1,2}){2} [0-6])+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_PLAY_FIRST = Pattern.compile("^f(( [lrud])? [0-6]| [lrud]( [0-6]){2,})$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_PASS = Pattern.compile("^p$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_QUIT = Pattern.compile("^q$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_SAVE_GAME = Pattern.compile("^s [a-zA-Z0-9^<>:;,?\"*_]+$", Pattern.CASE_INSENSITIVE);
    private static final String RED_BOLD = "\033[1;31m";
    private static final String BOLD = "\033[1m";
    private static final String RESET = "\033[0m";

    private App() {
    }

    /**
     * Get the names of players from user input.
     *
     * @param scanner The scanner to read user input from.
     * @return A list of player names.
     */
    private static List<String> getNameOfPlayers(Scanner scanner) {
        String[] names;
        boolean firstTry = true;
        do {
            if (firstTry) {
                System.out.print("Enter the names of the players followed by a space : ");
                firstTry = false;
            } else {
                System.out.print("Minimum two players please, try again : ");
            }
            String namePlayers = scanner.nextLine();
            names = namePlayers.split(" ");

        } while (names.length < 2);

        return Arrays.asList(names);
    }

    /**
     * Create a new game with the provided player names.
     *
     * @param namesPlayers The list of player names.
     * @return The created Game instance.
     */
    private static Game createAGame(List<String> namesPlayers) {
        System.out.println("Creating a new game ...");
        Game generatedGame = new Game(namesPlayers);
        System.out.println("Game created !");
        return generatedGame;
    }

    /**
     * Decode and execute a user command.
     *
     * @param command The user command to decode.
     */
    private static void decodeCommand(String command) {
        if (getMatcherPlayOneTile(command).find()) {
            handlePlayOneTile(command);
        } else if (getMatcherPlayLine(command).find()) {
            handlePlayLine(command);
        } else if (getMatcherPlayPlicPloc(command).find()) {
            handlePlayPlicPloc(command);
        } else if (getMatcherPlayFirst(command).find()) {
            handlePlayFirst(command);
        } else if (getMatcherPass(command).find()) {
            handlePass();
        } else if (getMatcherQuit(command).find()) {
            handleQuit();
        } else if (getMatcherSaveGame(command).find()) {
            handleSaveGame(command);
        } else {
            handleCommandNotFound();
        }
    }

    /**
     * Handle the "play one tile" command by extracting operands and playing the tile.
     *
     * @param command The user command to handle.
     */
    private static void handlePlayOneTile(String command) {
        String[] operands = getOperandsCommand(command);
        int row = convertStrToInt(operands[0]);
        int col = convertStrToInt(operands[1]);
        int index = convertStrToInt(operands[2]);
        game.play(row, col, index);
    }

    /**
     * Handle the "play line" command by extracting operands and playing the tiles in a line.
     *
     * @param command The user command to handle.
     */
    private static void handlePlayLine(String command) {
        String[] operands = getOperandsCommand(command);
        int row = convertStrToInt(operands[0]);
        int col = convertStrToInt(operands[1]);
        Direction d = getDirection(operands[2]);
        int[] indexes = convertOperandsToInteger(operands, 3);
        game.play(row, col, d, indexes);
    }

    /**
     * Handle the "play plic-ploc" command by extracting operands and playing the tiles.
     *
     * @param command The user command to handle.
     */
    private static void handlePlayPlicPloc(String command) {
        String[] operands = getOperandsCommand(command);
        int[] intArr = convertOperandsToInteger(operands, 0);
        game.play(intArr);
    }

    /**
     * Handle the "play first" command by extracting operands and playing the first tiles.
     *
     * @param command The user command to handle.
     */
    private static void handlePlayFirst(String command) {
        String[] operands = getOperandsCommand(command);
        if (operands[0].equals("l") || operands[0].equals("r") || operands[0].equals("u") || operands[0].equals("d")) {
            Direction d = getDirection(operands[0]);
            int[] indexes = convertOperandsToInteger(operands, 1);
            game.first(d, indexes);
        } else {
            Direction d = Direction.UP;
            int index = convertStrToInt(operands[0]);
            game.first(d, index);
        }
    }

    /**
     * Handle the "pass" command by passing the current player's turn.
     */
    private static void handlePass() {
        game.pass();
    }

    /**
     * Handle the "quit" command by terminating the game.
     */
    private static void handleQuit() {
        System.out.println("Game terminated.");
        System.exit(0);
    }

    /**
     * Handle the "save game" command by saving the current game state.
     *
     * @param command The user command to handle.
     */
    private static void handleSaveGame(String command) {
        String fileName = getOperandsCommand(command)[0];
        try {
            game.write(fileName);
            System.out.println("Saved game.");
        } catch (QwirkleException e) {
            System.out.println(RED_BOLD + e.getMessage() + RESET);
        }
    }

    /**
     * Handle the restoration of a game from a file.
     *
     * @param fileName The name of the file to restore the game from.
     */
    private static void handleRestoreGame(String fileName) {
        try {
            game = Game.getFromFile(fileName);
            System.out.println("Charged game.");
        } catch (QwirkleException e) {
            System.out.println(RED_BOLD + e.getMessage() + RESET);
            System.exit(-1);
        }
    }

    /**
     * Handle the case where the user command is not recognized.
     */
    private static void handleCommandNotFound() {
        System.out.println(RED_BOLD + "This command does not exist, please try again." + RESET);
    }

    /**
     * Get the operands from a user command.
     *
     * @param command The user command to extract operands from.
     * @return An array of operands extracted from the command.
     */
    private static String[] getOperandsCommand(String command) {
        return command.substring(2).split(" ");
    }

    /**
     * Get a matcher for the "play one tile" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "play one tile" pattern.
     */
    private static Matcher getMatcherPlayOneTile(String command) {
        return PATTERN_PLAY_ONE_TILE.matcher(command);
    }

    /**
     * Get a matcher for the "play line" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "play line" pattern.
     */
    private static Matcher getMatcherPlayLine(String command) {
        return PATTERN_PLAY_LINE.matcher(command);
    }

    /**
     * Get a matcher for the "play plic-ploc" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "play plic-ploc" pattern.
     */
    private static Matcher getMatcherPlayPlicPloc(String command) {
        return PATTERN_PLAY_PLIC_PLOC.matcher(command);
    }

    /**
     * Get a matcher for the "play first" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "play first" pattern.
     */
    private static Matcher getMatcherPlayFirst(String command) {
        return PATTERN_PLAY_FIRST.matcher(command);
    }

    /**
     * Get a matcher for the "pass" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "pass" pattern.
     */
    private static Matcher getMatcherPass(String command) {
        return PATTERN_PASS.matcher(command);
    }

    /**
     * Get a matcher for the "quit" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "quit" pattern.
     */
    private static Matcher getMatcherQuit(String command) {
        return PATTERN_QUIT.matcher(command);
    }

    /**
     * Get a matcher for the "save game" command pattern.
     *
     * @param command The user command to match.
     * @return A matcher for the "save game" pattern.
     */
    private static Matcher getMatcherSaveGame(String command) {
        return PATTERN_SAVE_GAME.matcher(command);
    }

    /**
     * Convert an array of string operands to an array of integers.
     *
     * @param strArr     The array of string operands.
     * @param beginIndex The starting index for conversion.
     * @return An array of integers converted from the string operands.
     */
    private static int[] convertOperandsToInteger(String[] strArr, int beginIndex) {
        int[] intArr = new int[strArr.length - beginIndex];
        int i = 0;
        while (beginIndex < strArr.length) {
            int value = convertStrToInt(strArr[beginIndex]);
            intArr[i] = value;
            i++;
            beginIndex++;
        }
        return intArr;
    }

    /**
     * Convert a string to an integer.
     *
     * @param value The string value to convert.
     * @return The integer value converted from the string.
     */
    private static int convertStrToInt(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Get the Direction enum value from a string representation.
     *
     * @param d The string representation of the direction.
     * @return The corresponding Direction enum value.
     */
    private static Direction getDirection(String d) {
        return switch (d) {
            case "l" -> Direction.LEFT;
            case "r" -> Direction.RIGHT;
            case "u" -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    /**
     * Prompt the user whether they want to restore a previously saved game.
     *
     * @param scanner The scanner to read user input from.
     * @return `true` if the user chooses to restore a game, `false` otherwise.
     */
    private static boolean askRestoreGame(Scanner scanner) {
        char r;
        do {
            System.out.print("Would you like to restore a game? (Y/N): ");
            r = scanner.next().charAt(0);
            r = Character.toUpperCase(r);
        }
        while (r != 'Y' && r != 'N');
        scanner.nextLine();

        if (r == 'Y') {
            String directoryName = "backups" + File.separator;
            File directory = new File(directoryName);
            File[] listOfFiles = directory.listFiles();
            Path file;
            String fileName;
            boolean firstTry = true;

            if (listOfFiles == null) {
                System.out.println("No game parts to restore.");
                return false;
            }

            do {
                for (File listOfFile : listOfFiles) {
                    System.out.println(BOLD + "File: " + listOfFile.getName() + RESET);
                }
                if (firstTry) {
                    System.out.print("Enter the name of a file listed in the selection: ");
                    firstTry = false;
                } else {
                    System.out.print("The file does not exist, please select a file listed in the selection: ");
                }
                fileName = scanner.nextLine();
                file = Paths.get(directoryName + fileName);
            } while (!Files.exists(file));
            handleRestoreGame(fileName);
            return true;
        }

        return false;
    }

    /**
     * Main method to start the Qwirkle game application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (!askRestoreGame(scanner)) {
            List<String> namesPlayers = getNameOfPlayers(scanner);
            game = createAGame(namesPlayers);
        }

        View.displayHelp();
        while (true) {
            String nameCurrPlayer = game.getCurrentPlayerName();
            List<Tile> handCurrPlayer = game.getCurrentPlayerHand();
            int scoreCurrPlayer = game.getCurrentPlayerScore();
            View.display(nameCurrPlayer, handCurrPlayer, scoreCurrPlayer);
            View.display(game.getGrid());
            System.out.print("Enter a command: ");
            String command = scanner.nextLine();
            decodeCommand(command);
            if (game.isOver()) {
                Player winner = game.getWinner();
                View.displayWinner(winner);
                handleQuit();
            }
        }
    }
}
