package com.company;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class TreeSplitAI extends GameAI {
    private volatile HashMap<Integer, Double> Alpha;
    private volatile HashMap<Integer, Double> Beta;
    private volatile int numThreads = 0;
    private final int threadLimit = 16;
    private final Object alphaBetaLock = new Object();
//    private final Object threadCount


    @Override
    public int makeMove(GameBoard gameBoard){
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
            Alpha = new HashMap<>();
            Beta = new HashMap<>();
            results[i] = treeSplit2(nboard, (int)startDepth, new Double[]{Double.NEGATIVE_INFINITY},new Double[]{Double.POSITIVE_INFINITY},new Object(), false);
        }

        double max = Double.NEGATIVE_INFINITY;
        int arg = -1;
        for(int i = 0; i < gameBoard.getCols(); i++){
            System.out.println(results[i]);
            if(results[i] > max){
                max = results[i];
                arg = i;
            }
        }
        return arg;
    }

    private double treeSplit(GameBoard gameBoard, int depth, double alpha, double beta, boolean playing){
        Double tvalue = terminalValue(gameBoard, depth, playing);
        if(tvalue != null){
            return tvalue;
        }
        synchronized (alphaBetaLock){
            if(!Alpha.containsKey(depth)){
                Alpha.put(depth, alpha);
            }
            if(!Beta.containsKey(depth)){
                Beta.put(depth, beta);
            }
        }
        Object returnLock = new Object();
        int[] counter = new int[1];

        ArrayList<Thread> threads = new ArrayList<>();
        double[] results = new double[gameBoard.getCols()];
        for(int i = 0; i < gameBoard.getCols(); i++){
            results[i] = Double.NEGATIVE_INFINITY;
            GameBoard child = gameBoard.makeMove(i);
            if(child == null){
                synchronized(returnLock){
                    counter[0]++;
                    if(counter[0] >= gameBoard.getCols()){
                        returnLock.notify();
                    }
                }
                continue;
            }
            final int finalI = i;
            Thread t = new Thread(){
                @Override
                public void run(){
                    numThreads++;
                    double v = Double.NEGATIVE_INFINITY;
                    double a;
                    double b;
                    synchronized (alphaBetaLock){
                        a = -Beta.get(depth);
                        b = -Alpha.get(depth);
                    }
                    v = treeSplit(child, depth-1, a, b, !playing);
                    results[finalI] = v;
                    synchronized (alphaBetaLock){
                        if(v > Alpha.get(depth)){
                            Alpha.put(depth,v);
                        }
                        synchronized (returnLock){
                            if(Alpha.get(depth) > Beta.get(depth)){
                                counter[0] = gameBoard.getCols()+1;
                                System.out.println("notify");
                                returnLock.notify();
                            }
                            counter[0]++;
                            if(counter[0] >= gameBoard.getCols()){
                                returnLock.notify();
                            }
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
        synchronized (returnLock){
            if(counter[0] < gameBoard.getCols()){
                try {
                    System.out.println("waiting");
                    returnLock.wait();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for(Thread t: threads){
            t.stop();
        }
        double max = Double.NEGATIVE_INFINITY;
        int arg = -1;
        for(int i = 0; i < gameBoard.getCols(); i++){
            if(results[i] > max){
                max = results[i];
                arg = i;
            }
        }
        return max;
//        return returnValue[0];
//        System.out.println("waiting");
//        while(Alpha.get(depth) < Beta.get(depth)){
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("done waiting");
//        for(Thread t: threads){
//            t.stop();
//        }
//        return Alpha.get(depth);
    }

    private double treeSplit2(GameBoard gameBoard, int depth, Double[] pAlpha, Double[] pBeta, Object parentUpdate, boolean playing){
        Double tvalue = terminalValue(gameBoard, depth, playing);
        if(tvalue != null){
            synchronized (parentUpdate){
                parentUpdate.notifyAll();
                return tvalue;
            }
        }
        Double[] cAlpha = new Double[1];
        Double[] cBeta = new Double[1];
        Double[] alpha =new Double[]{pAlpha[0]};
        Double[] beta = new Double[]{pBeta[0]};
        Integer[] counter = new Integer[]{0};
        Object childUpdate = new Object();
        Boolean[] returnReady = new Boolean[1];
        returnReady[0] = false;
        ArrayList<Thread> threads = new ArrayList<>();
        double[] results = new double[gameBoard.getCols()];
        for(int i = 0; i < gameBoard.getCols(); i++){
            results[i] = Double.NEGATIVE_INFINITY;
            GameBoard child = gameBoard.makeMove(i);
            if(child == null){
                counter[0] += 1;
                if(counter[0] >= gameBoard.getCols()){
                    synchronized (parentUpdate){
                        returnReady[0] = true;
                        parentUpdate.notifyAll();
                    }
                }
                continue;
            }
            final int finalI = i;
            Thread t = new Thread() {
                @Override
                public void run() {
                    cBeta[0] = -alpha[0];
                    cAlpha[0] = -beta[0];
                    results[finalI] = treeSplit2(child, depth+1, cAlpha, cBeta, childUpdate, !playing);
                    if(results[finalI] > pAlpha[0]){
                        alpha[0] = results[finalI];
                        synchronized (childUpdate){
                            cBeta[0] = -alpha[0];
                            childUpdate.notifyAll();
                        }
                    }
                    if(alpha[0] > beta[0]){
                        //TODO terminate slaves
                        synchronized (parentUpdate){
                            returnReady[0] = true;
                            parentUpdate.notifyAll();
                        }

                    }
                    counter[0] += 1;
                    if(counter[0] >= gameBoard.getCols()){
                        synchronized (parentUpdate){
                            returnReady[0] = true;
                            parentUpdate.notifyAll();
                        }
                    }


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
        synchronized (parentUpdate){
            try {
                if(!returnReady[0]){
//                    parentUpdate.wait();
                    alpha[0] = pAlpha[0];
                    beta[0] = pBeta[0];
                    synchronized (childUpdate){
                        cBeta[0] = -alpha[0];
                        cAlpha[0] = -beta[0];
                        childUpdate.notifyAll();
                    }
                }else{
                    double max = Double.NEGATIVE_INFINITY;
                    int arg = -1;
                    for(int i = 0; i < gameBoard.getCols(); i++){
                        if(results[i] > max){
                            max = results[i];
                            arg = i;
                        }
                    }
                    return max;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("i broke");
        return -1;
    }
}
