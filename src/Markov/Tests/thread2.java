package Markov.Tests;

import Markov.Bots.Bot_MobileCombination;
import Markov.Bots.Bot_MobileLite;

import java.util.Scanner;

import static Markov.Bots.Bot_Haruhi_scr.useFireFox;
import static Markov.Scroller.*;

public class thread2 {
    public static boolean test = false;


    public static void main(String[] args) throws InterruptedException {
        Saratoga = new Bot_MobileCombination();
        System.out.println("Thread 2");
        Scanner sc = new Scanner(System.in);
        String file = "replyBotSetting.txt";//sc.next();
        if (!test) {
            Saratoga.readSetting(file);
        } else {
            Saratoga.setTestSetting();
        }

        System.out.println("Start programme");
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID;
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID;//ani1_new1
        }
        if (useFireFox) {
            if (!test) Saratoga.initFireFox();
        } else {
            if (!test) Saratoga.initBrowser();
        }
        //  test();
        scrollOffline();
        Saratoga.stay();

    }
}
