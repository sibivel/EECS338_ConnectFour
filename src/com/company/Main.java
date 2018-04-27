package com.company;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        System.out.println((char)27 + "[31m" + "ERROR MESSAGE IN RED hello");
//        int[][] board = new int [][]{{1,0,1},
//                {2,0,1},{2,0,2}};
//        new GameBoard(board, 1, null).printBoard();

//        GameBoard board = new GameBoard();
//        Random rnd = new Random();
//        while(board != null){
//            board.printBoard();
//            board = board.makeMove(rnd.nextInt(7));
//            if(board.getWinner() != 0){
//                board.printBoard();
//                System.out.println(board.getWinner());
//                return;
//            }
//        }


        GameBoard board = new GameBoard(3,3,3);
        Scanner in = new Scanner(System.in);
        boolean playing = true;
        GameAI ai = new TreeSplitAI();
        while(board != null && board.getWinner() == 0){
            board.printBoard();
            if(playing){
                board = board.makeMove(in.nextInt());
            }else{
                board = board.makeMove(ai.makeMove(board));
            }
//            board = board.makeMove(ai.makeMove(board));

            playing = !playing;
        }
        if(board != null)
            board.printBoard();

    }
}
