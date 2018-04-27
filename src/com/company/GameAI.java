package com.company;

import java.util.ArrayList;

public abstract class GameAI {

    protected double startDepth = 100;

    /**
     * Constructor for a game playing ai.
     * @param startDepth specify how much to search befor making a move. a negative depth will plan the whole game tree.
     */
    public GameAI(int startDepth){
        this.startDepth = startDepth;
    }

    public GameAI(){
        super();
    }

    /**
     * function to call in order to find the best move.
     * @param gameBoard GameBoard to make the move on.
     * @return column number of the best move.
     */
    public abstract int makeMove(GameBoard gameBoard);

    protected Double terminalValue(GameBoard gameBoard, int depth, boolean playing){
        if(gameBoard == null){
            return Double.NEGATIVE_INFINITY;
        }
        if(depth == 0 || gameBoard.getWinner() != 0){
            int winner = gameBoard.getWinner();
            if(winner != 0){
                if(playing){
                    return h(-1, depth);
                }else{
                    return h(1, depth);
                }
            }
            return h(0, depth);
        }
        boolean noMoves = true;
        for(int i = 0; i < gameBoard.getCols(); i++) {
            GameBoard child = gameBoard.makeMove(i);
            if (child != null) {
                noMoves = false;
            }
        }
        if(noMoves){
            return h(0, depth);
        }
        return null;
    }

    /**
     * Heuristic function
     * @param w Win status, 1 for win, -1 for loss, 0 for tie.
     * @param depth how many moves it took to get to this position.
     * @return value of the position.
     */
    protected double h(int w, int depth){
        if(w > 0){
            return 10 * w - Math.abs((startDepth - depth)/(startDepth));
        }else{
            return 10 * w + Math.abs((startDepth - depth)/(startDepth));
        }

    }

}
