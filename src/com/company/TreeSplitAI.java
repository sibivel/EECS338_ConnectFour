package com.company;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class TreeSplitAI extends GameAI {
    private volatile HashMap<Integer, Double> Alpha;
    private volatile HashMap<Integer, Double> Beta;
    private volatile int numThreads = 0;
    private final int threadLimit = 16;
    private final Object alphaBetaLock = new Object();;


    @Override
    public int makeMove(GameBoard gameBoard){
        Alpha = new HashMap<>();
        Beta = new HashMap<>();
        double[] results = new double[gameBoard.getCols()];
        ArrayList<Thread> threads = new ArrayList<>();
        for(int i = 0; i<gameBoard.getCols(); i++){
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
                    numThreads++;
                    results[finalI] = treeSplit(nboard, (int)startDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);;
                    numThreads--;
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
        for(int i = 0; i < gameBoard.getCols(); i++){
            if(results[i] > max){
                max = results[i];
                arg = i;
            }
        }
        return arg;
    }

    private double treeSplit(GameBoard gameBoard, int depth, double alpha, double beta, boolean playing){
        Double tvalue = terminalValue(gameBoard, depth, playing);
        if(!Alpha.containsKey(depth)){
            Alpha.put(depth, alpha);
        }
        if(!Beta.containsKey(depth)){
            Beta.put(depth, beta);
        }
        if(tvalue != null){
            return tvalue;
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for(int i = 0; i < gameBoard.getCols(); i++){
            GameBoard child = gameBoard.makeMove(i);
            if(child == null){
                continue;
            }
            Thread t = new Thread(){
                @Override
                public void run(){
                    numThreads++;
                    double v = Double.NEGATIVE_INFINITY;
                    v = treeSplit(child, depth-1, -Beta.get(depth), -Alpha.get(depth), false);
                    synchronized (alphaBetaLock){
                        if(v > Alpha.get(depth)){
                            Alpha.put(depth,v);
                        }
                    }

                    numThreads--;
                }
            };
            threads.add(t);
//            System.out.println("wating");
//            while(numThreads > threadLimit){
//                try {
//                    sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            t.start();
        }
        System.out.println("waiting");
        while(Alpha.get(depth) < Beta.get(depth)){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("done waiting");
        for(Thread t: threads){
            t.stop();
        }
        return Alpha.get(depth);
    }
}
