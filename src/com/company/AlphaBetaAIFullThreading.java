package com.company;

import java.util.ArrayList;

public class AlphaBetaAIFullThreading extends GameAI {
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
//            Thread t = new Thread(){
//                @Override
//                public void run(){
                    Bound Alpha = new Bound();
                    Bound Beta = new Bound();
                    Alpha.setV(Double.NEGATIVE_INFINITY);
                    Beta.setV(Double.POSITIVE_INFINITY);
                    results[finalI] = alphabeta(nboard, (int)startDepth,Alpha , Beta, false, new ThreadMaintainer(16));
//                }
//            };
//            threads.add(t);
//            t.start();

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
//            System.out.println(results[i]);
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
    private double alphabeta(GameBoard gameBoard, final int depth, final Bound alpha, final Bound beta, boolean playing, final ThreadMaintainer maintainer){
        Double tvalue = terminalValue(gameBoard, depth, playing);
        if(tvalue != null){
            return tvalue;
        }
        final ArrayList<Thread> threads = new ArrayList<>();
        final Double[] results = new Double[gameBoard.getCols()];
        final Return ready = new Return(false);
        if(playing){
            for(int i = 0; i < gameBoard.getCols(); i++){
                results[i] = Double.NEGATIVE_INFINITY;
                final GameBoard child = gameBoard.makeMove(i);
                if(child == null){
                    continue;
                }

                final int finalI = i;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bound cAlpha = alpha.makeChild();
                        Bound cBeta = beta.makeChild();
                        double v = AlphaBetaAIFullThreading.this.alphabeta(child, depth - 1, cAlpha, cBeta, false, maintainer.makeChild());
                        results[finalI] = v;
                        if (alpha.getV() < v) {
                            alpha.setV(v);
                        }
//                alpha = Math.max(alpha, v);
                        if (beta.getV() <= alpha.getV()) {
                            synchronized (ready) {
//                            System.out.println("cutoff1");
                                ready.setV(true);
                                ready.notifyAll();
                            }
                        }
                    }
                });
                maintainer.startThread(t);
                threads.add(t);


            }
            for(Thread t: threads){
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            double max = Double.NEGATIVE_INFINITY;
            int arg = -1;
            for(int i = 0; i < gameBoard.getCols(); i++){
//            System.out.println(results[i]);
                if(results[i] > max){
                    max = results[i];
                    arg = i;
                }
            }
            maintainer.killThreads(Thread.currentThread());
            return max;
        }else{
            for(int i = 0; i < gameBoard.getCols(); i++){
                results[i] = Double.POSITIVE_INFINITY;
                final GameBoard child = gameBoard.makeMove(i);
                if(child == null){
                    continue;
                }
                final int finalI = i;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bound cAlpha = alpha.makeChild();
                        Bound cBeta = beta.makeChild();
                        double v = AlphaBetaAIFullThreading.this.alphabeta(child, depth - 1, cAlpha, cBeta, true, maintainer.makeChild());
                        results[finalI] = v;
                        if (beta.getV() > v) {
                            beta.setV(v);
                        }
                        if (beta.getV() <= alpha.getV()) {
                            //TODO kill threads;
                            synchronized (ready) {
//                            System.out.println("cutoff2");
                                ready.setV(true);
                                ready.notifyAll();
                            }
                        }
                    }
                });
                maintainer.startThread(t);
                threads.add(t);

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Thread t : threads) {
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (ready) {
                        ready.setV(true);
                        ready.notifyAll();
                    }
                }
            }).start();

            while(!ready.getV()){
                synchronized (ready){
                    try {
                        ready.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            double min = Double.POSITIVE_INFINITY;
            int arg = -1;
            for(int i = 0; i < gameBoard.getCols(); i++){
//            System.out.println(results[i]);
                if(results[i] < min){
                    min = results[i];
                    arg = i;
                }
            }
            maintainer.killThreads(Thread.currentThread());
            return min;
        }
//        synchronized (results){
//            try {
//                results.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


    }

    private class Bound{
        private double v;
        private ArrayList<Bound> children = new ArrayList<>();
        public Bound(){
            super();
        }
        public Bound(double v){
            this.v = v;
        }
        public synchronized double getV(){
            return v;
        }
        public synchronized void setV(double v){
            this.v = v;
            for(Bound b: children){
                b.setV(v);
            }
        }

        public synchronized Bound makeChild(){
            Bound b = new Bound(v);
            children.add(b);
            return b;

        }
    }

    private class Return{
        private boolean v;

        public Return(){
            super();
        }
        public Return(boolean v){
            this.v = v;
        }
        public synchronized boolean getV(){
            return v;
        }
        public synchronized void setV(boolean v){
            this.v = v;
        }
    }

    private class ThreadMaintainer{
        private int threadLimit;
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<ThreadMaintainer> maintainers = new ArrayList<>();
        public ThreadMaintainer(int threadLimit){
            this.threadLimit = threadLimit;
        }

        public synchronized Thread startThread(Thread t){
            t.start();
            threads.add(t);
            return t;
        }

        public synchronized void killThreads(Thread x){
            for(Thread t: threads){
                if(t.isAlive() && t != x){
                    t.stop();
                }
            }
            for(ThreadMaintainer child: maintainers){
                child.killThreads(x);
            }
        }

        public synchronized ThreadMaintainer makeChild(){
            ThreadMaintainer child = new ThreadMaintainer(threadLimit);
            maintainers.add(child);
            return child;
        }
    }


}
