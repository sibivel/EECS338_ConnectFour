package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try{
            int rows = 5;
            int cols = 6;
            int wincount = 4;
            String aiMode = "Basic";
            if(args.length == 1){
                aiMode = args[0];
            }else if(args.length == 3){
                aiMode = args[0];
                rows = Integer.parseInt(args[1]);
                cols = Integer.parseInt(args[2]);
            }else if(args.length == 4){
                aiMode = args[0];
                rows = Integer.parseInt(args[1]);
                cols = Integer.parseInt(args[2]);
                wincount = Integer.parseInt(args[3]);
            }


            GameBoard board = new GameBoard(rows,cols,wincount);
            Scanner in = new Scanner(System.in);
            boolean playing = true;
            GameAI ai;
            switch (aiMode){
                case "Basic": ai = new AlphaBetaAIBasicThreading();
                    break;
                case "None": ai = new AlphaBetaAINoThreading();
                    break;
                case "Full": ai = new AlphaBetaAIFullThreading();
                    break;
                default: throw new Exception("Can't recognize this Ai Mode");

            }
            while(board != null && board.getWinner() == 0){
                board.printBoard();
                if(playing){
                    System.out.println("Make a move:");
                    board = board.makeMove(in.nextInt());
                }else{
                    long startTime = System.nanoTime();
                    board = board.makeMove(ai.makeMove(board));
                    long endTime   = System.nanoTime();
                    long totalTime = endTime - startTime;
                    System.out.println((((double)totalTime)/1000000000) + " seconds");
                }

                playing = !playing;
            }
            System.out.println("Game Over");
            if(board != null)
                board.printBoard();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
