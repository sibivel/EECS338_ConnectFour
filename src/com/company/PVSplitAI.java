package com.company;

import java.util.ArrayList;

public class PVSplitAI extends GameAI {
    /**
     * function to call in order to find the best move.
     * @param gameBoard GameBoard to make the move on.
     * @return column number of the best move.
     */
    public int makeMove(GameBoard gameBoard){
        int rows = gameBoard.getBoard().length;

        if (rows == 0)
            throw new NullPointerException();

        int cols = gameBoard.getBoard()[0].length;
        /* this array will be shared between all threads */
        double[] results = new double[cols];
        for(int i = 0; i<cols; i++) {

        }

        return 0;
    }

    private double pvsplit(GameBoard gameBoard, int depth, double alpha, double beta, boolean playing){
        Double tvalue = terminalValue(gameBoard, depth, playing);
        if(tvalue != null){
            return tvalue;
        }

        //find first son.
        int son = 0;
        while(son < gameBoard.getCols()){
            GameBoard child = gameBoard.makeMove(son);
            if(child != null){
                break;
            }
            son++;
        }

        return 0;
    }
}
