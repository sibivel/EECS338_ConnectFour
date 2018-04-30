package com.company;

public class GameBoard {


    private int rows, cols;

    private int turn = 1;
    private int[][] board;
    private int winner = 0;
    private Move move = null;

    private static int winCount = 4;

    /**multiple constructors allows GameBoards to be created from other gameboards
     * as well as of different sizes
     */
    public GameBoard(int rows, int cols){
        this.rows = rows;
        this.cols = cols;

        this.board = new int[rows][cols];

    }

    public GameBoard(int rows, int cols, int winCount){
        this(rows, cols);
        this.winCount = winCount;
    }

    public GameBoard(){
        this(6,7);
    }

    public GameBoard(int[][] board, int turn, Move move){
        this.rows = board.length;
        this.cols = board[0].length;
        this.board = board;
        this.turn = turn;
        this.move = move;
        this.winner = checkStatus();
    }

    /**
     * function to call to make a move.
     * @param col where to make the move.
     * @return new GameBoard where the move has been made.
     */
    public GameBoard makeMove(int col){
        int [][] nboard = cloneBoard(this.board);
        int row = 0;
        if(col < 0 || col >= cols){
            return null;
        }
        while(row < rows){
           if(nboard[row][col] == 0){
               nboard[row][col] = turn;
               return new GameBoard(nboard, nextTurn(), new Move(row, col));
           }
           row++;
        }
        return null;

    }

    public int getWinner(){
        return winner;
    }

    /**check status of if anyone has won the game.
     *
     * @return 0 if no on has won. 1 if player 1 won, 2 if player 2 won.
     */
    private int checkStatus(){
        //player who might have won (otehr player):
        int player = nextTurn();
        //check row:
        int count = 0;
        for(int i=0; i < cols; i++){
            if(board[move.row][i] == player){
                count++;
                if(count == winCount){
                    return player;
                }
            }else{
                count = 0;
            }
        }

        //check column
        count = 0;
        for(int i=0; i < rows; i++){
            if(board[i][move.col] == player){
                count++;
                if(count == winCount){
                    return player;
                }
            }else{
                count = 0;
            }
        }

        //check diagonals
        count = 0;
        int r = (move.row < move.col) ? 0 : (move.row - move.col);
        int c = (move.row < move.col) ? (move.col - move.row) : 0;
        while(r < rows && c < cols){
            if(board[r][c] == player){
                count++;
                if(count == winCount){
                    return player;
                }
            }else{
                count = 0;
            }
            r++;
            c++;
        }

        count = 0;
        r = (rows - 1 - move.row < move.col) ? rows-1 : (move.row + move.col);
        c = (rows - 1 - move.row < move.col) ? (move.col - (rows -1 - move.row)) : 0;
        while(r >= 0 && c < cols){
            if(board[r][c] == player){
                count++;
                if(count == winCount){
                    return player;
                }
            }else{
                count = 0;
            }
            r--;
            c++;
        }

        return 0;
    }

    public int[][] getBoard(){
        return this.board;
    }

    private int nextTurn(){
        return (turn == 1) ? 2 : 1;
    }

    private static int[][] cloneBoard(int[][] board){
        if (board == null)
            return null;
        int[][] result = new int[board.length][];
        for (int r = 0; r < board.length; r++) {
            result[r] = board[r].clone();
        }
        return result;
    }

    /**
     * class to represent a single move.
     */
    public static class Move{
        public int row;
        public int col;
        public Move(int row, int col){
            this.row = row;
            this.col = col;
        }
    }

    /**
     * prints this GameBoard in color to System.out.
     */
    public void printBoard(){
        for(int i = rows-1; i >= 0; i--){
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < cols; j++){
                switch (board[i][j]){
                    case 1: sb.append(ANSI_RED + " O ");
                        break;
                    case 2: sb.append(ANSI_GREEN + " X ");
                        break;
                    default: sb.append(ANSI_RESET + " _ ");
                        break;
                }
            }
            System.out.println(sb.toString());
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < cols; i++){
            sb.append(" " + i + " ");
        }
        System.out.println(ANSI_RESET + sb.toString());
        System.out.println();
    }

    public int getTurn() {
        return turn;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_GREEN = "\u001B[32m";





}
