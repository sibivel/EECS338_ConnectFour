package com.company;

import java.util.Scanner;

public class TimeTest {

    public static void main(String[] args) {
        try{
            int rows = 4;
            int cols = 5;
            int wincount = 4;

            GameBoard board = new GameBoard(rows,cols,wincount);
            Scanner in = new Scanner(System.in);
            boolean playing = true;
            GameAI full = new AlphaBetaAIFullThreading();
            GameAI base = new AlphaBetaAIBasicThreading();
            GameAI none = new AlphaBetaAINoThreading();
            board = board.makeMove(0);

            long startTime;
            long endTime;
            long totalTime;
//            long startTime = System.nanoTime();
//            board.makeMove(full.makeMove(board));
//            long endTime   = System.nanoTime();
//            long totalTime = endTime - startTime;
//            System.out.println("Full takes " + (((double)totalTime)/1000000000) + " seconds");


            startTime = System.nanoTime();
            board.makeMove(base.makeMove(board));
            endTime   = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("Base takes " + (((double)totalTime)/1000000000) + " seconds");

            startTime = System.nanoTime();
            board.makeMove(none.makeMove(board));
            endTime   = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("None takes " + (((double)totalTime)/1000000000) + " seconds");


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
