package Markov.Tests;

import Markov.Bots.Bot_Haruhi_scr;
import Markov.Bots.Bot_MobileLite;

import java.util.Scanner;

import static Markov.Bots.Bot.ngramCut;
import static Markov.Bots.Bot_Haruhi_scr.useFireFox;
import static Markov.Predict.isReachable;
import static Markov.Scroller.*;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        Saratoga = new Bot_MobileLite();
        System.out.println("Thread 2");
        //System.out.println("Enter setting file name");
        Scanner sc = new Scanner(System.in);
        fileToRead="network-kancolle+nGram.txt";
        //  test();
        scrollOffline();
        while(true){
            System.out.println("Input String");
            String input =sc.next();
            input = input.replaceAll("_"," ");
            int out = isReachable(input);
            System.out.println("OUT: "+out);
        }
    }
}
