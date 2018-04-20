package com.company;

public class GameBoard {

    private int rows, cols;

    private int turn = 1;
    private int[][] board;
    private String winner = null;
    private Move move = null;

    public GameBoard(int rows, int cols){
        this.rows = rows;
        this.cols = cols;

        this.board = new int[rows][cols];

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
    }

    public GameBoard makeMove(int col){
        int [][] nboard = cloneBoard(this.board);
        int row = 0;
        while(row < rows){
           if(nboard[row][col] == 0){
               nboard[row][col] = turn;
               return new GameBoard(nboard, nextTurn(), new Move(row, col));
           }
           row++;
        }
        return null;

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

    private static class Move{
        public int row;
        public int col;
        public Move(int row, int col){
            this.row = row;
            this.col = col;
        }
    }

    public void printBoard(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                switch (board[i][j]){
                    case 0: sb.append(" _ ");
                        break;
                    case 1: sb.append(" "+(char)27 );
                }
            }
        }
    }




}
