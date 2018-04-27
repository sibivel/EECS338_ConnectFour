package com.company;

import java.util.ArrayList;

public class GameAI {

    private double startDepth = 100;

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
    public int makeMove(GameBoard gameBoard){
        int rows = gameBoard.getBoard().length;

        if (rows == 0)
            throw new NullPointerException();

        int cols = gameBoard.getBoard()[0].length;
        /* this array will be shared between all threads */
        double[] results = new double[cols];
        ArrayList<Thread> threads = new ArrayList<>();
        for(int i = 0; i<cols; i++){
            results[i] = Double.NEGATIVE_INFINITY;
            GameBoard nboard = gameBoard.makeMove(i);
            if(nboard == null){
                continue;
            }
            int winner = nboard.getWinner();
            if(winner != 0){
                return i;
            }
            int finalI = i;
            Thread t = new Thread(){
                @Override
                public void run(){
                    results[finalI] = alphabeta(nboard, (int)startDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);;
                }
            };
            threads.add(t);
            t.start();

        }
        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double max = Double.NEGATIVE_INFINITY;
        int arg = -1;
        for(int i = 0; i < cols; i++){
            if(results[i] > max){
                max = results[i];
                arg = i;
            }
        }
        return arg;


    }

    /**
     * helper function that does the actual planning for the game using alpha beta.
     * @param gameBoard this node's gameBoard
     * @param depth depth of planning
     * @param alpha
     * @param beta
     * @param playing whether it is the Ai's turn this ply.
     * @return the heuristic value of this game position.
     */
    private double alphabeta(GameBoard gameBoard, int depth, double alpha, double beta, boolean playing){
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
       if(playing){
           double v = Double.NEGATIVE_INFINITY;
           for(int i = 0; i < gameBoard.getCols(); i++){
               GameBoard child = gameBoard.makeMove(i);
               if(child == null){
                   continue;
               }
               v = Math.max(v, alphabeta(child, depth-1, alpha, beta, false));
               alpha = Math.max(alpha, v);
               if(beta <= alpha)
                   break;
           }
           return v;
       }else{
           double v = Double.POSITIVE_INFINITY;
           for(int i = 0; i < gameBoard.getCols(); i++){
               GameBoard child = gameBoard.makeMove(i);
               if(child == null){
                   continue;
               }
               v = Math.min(v, alphabeta(child, depth-1, alpha, beta, true));
               beta = Math.min(beta, v);
               if(beta <= alpha){
                   break;
               }
           }
           return v;
       }
    }

    /**
     * Heuristic function
     * @param w Win status, 1 for win, -1 for loss, 0 for tie.
     * @param depth how many moves it took to get to this position.
     * @return value of the position.
     */
    private double h(int w, int depth){
        if(w > 0){
            return 10 * w - Math.abs((startDepth - depth)/(startDepth));
        }else{
            return 10 * w + Math.abs((startDepth - depth)/(startDepth));
        }

    }

}
