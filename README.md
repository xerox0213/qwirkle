# Qwirkle Console Game

## Description
Qwirkle is a Java adaptation of the popular board game. Playable in the console, it allows 2 or more players to compete by placing tiles on a 91x91 grid. Each player has 6 tiles and must play according to the original game rules. Players can save and restore games at any time.

## Game rules
This project follows the original rules of the Qwirkle board game. For a complete overview of the rules, please refer to [this link](https://upload.snakesandlattes.com/rules/q/Qwirkle.pdf).

## Features
- Tile placement on a 91x91 grid with rule validation.
- Intuitive commands to play, pass turns, save, or restore a game.
- Save system with backups stored in the `backups` folder, allowing games to be resumed later.
- Tile management: each player starts with 6 tiles, replaced after each turn if tiles are still available in the bag.

## Game Commands

- **Play the first tile(s)**: `f [<direction>] <i1> [<i2>]`  
  Place one or more tiles starting from the center of the grid (45, 45) in the specified direction.
  
- **Play a single tile**: `o <row> <col> <i>`  
  Place a tile at a specific position (row, column).

- **Play a line of tiles**: `l <row> <col> <direction> <i1> [<i2>]`  
  Place a series of tiles starting from a given position in the specified direction.

- **Play plic-ploc**: `m <row1> <col1> <i1> [<row2> <col2> <i2>]`  
  Place multiple tiles at different positions, as long as they are placed on the same line.

- **Pass a turn**: `p`  
  Skip your turn if no valid moves are available.

- **Save the game**: `s <filename>`  
  Save the current game under a specified filename.

- **Quit the game**: `q`  
  Quit the game session.

## Game Saving and Restoring
At the start of each game, players can choose to:
- Restore a saved game by selecting a file from the `backups` folder.
- Start a new game by entering player names (minimum of 2 players).

## Requirements
- **Java 17**
- **Maven** for dependency management.

## Installation and Execution

1. Clone the repository:
   ```bash
   git clone https://github.com/xerox0213/qwirkle.git
   ```

2. Navigate to the project directory:
   ```bash
   cd qwirkle
   ```

3. Compile and run the project with Maven:
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="g61453.qwirkle.App.App"
   ```

   The main entry point is located in the `g61453.qwirkle.App` package and is named `App`.

## Contributors
Developed by Nasreddine (myself).
