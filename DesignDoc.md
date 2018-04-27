# Connect 4 with Multithreading
## Sibi Sengottuvel

### Files:
src/com/company has all of the .java files for this project. 
* Main.java - used to run the game. Prompts user for input, then takes turns with the user to play the game.
* GameBoard.java - Object used to represent the Game board on screen as well as in the Ai's planning. This has functions for making moves, checking the winner, as well as displaying the board on the command line (in color). Contains the array that maintains the board position.
* GameAI.java - an implementation of a connect 4 ai that uses Alpha-Beta pruning. The ai runs its planning in multiple threads to speed up the process. Each thread shares the "results" array, and after each thread is done, this class checks the result of each thread and decides on the best move. Right now the parallelization is only done at the beginning, but I would eventually like to make it happen at every ply where the alpha and beta variables are also synchronized.

### Running the project

out/artifacts/EECS338_jar/EECS338.jar is the runnable build of the file.

Run this file by running java -jar ./out/artifacts/EECS338_jar/EECS338.jar

### Usage

After starting the project, the game should display an empty board and wait for player input. The player makes a move by typing in the column number and pressing enter.

On the board displayed red 'O's will be the player, and blue 'X's will be the Ai.

The Ai should make a move after planning.

When the game reaches an terminal state (a win or tie), the program will exit.
Sample Game:
```
 _  _  _  _  _ 
 _  _  _  _  _ 
 _  _  _  _  _ 
 _  _  _  _  _ 
 0  1  2  3  4 

0
 _  _  _  _  _ 
 _  _  _  _  _ 
 _  _  _  _  _ 
 O  _  _  _  _ 
 0  1  2  3  4 

0
 _  _  _  _  _ 
 _  _  _  _  _ 
 _  _  _  _  _ 
 O  X  _  _  _ 
 0  1  2  3  4 

 _  _  _  _  _ 
 _  _  _  _  _ 
 O  _  _  _  _ 
 O  X  _  _  _ 
 0  1  2  3  4 

 _  _  _  _  _ 
 _  _  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  _ 
 0  1  2  3  4 

0
 _  _  _  _  _ 
 O  _  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  _ 
 0  1  2  3  4 

 X  _  _  _  _ 
 O  _  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  _ 
 0  1  2  3  4 

4
 X  _  _  _  _ 
 O  _  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  O 
 0  1  2  3  4 

 X  _  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  O 
 0  1  2  3  4 

2
 X  _  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  _ 
 O  X  O  _  O 
 0  1  2  3  4 

 X  X  _  _  _ 
 O  X  _  _  _ 
 O  X  _  _  _ 
 O  X  O  _  O 
 0  1  2  3  4 

 Process finished with exit code 0
```

Right now the game is smaller than a regular Connect 4 board to speed up the planning, but this can be modified later on with different arguments.


