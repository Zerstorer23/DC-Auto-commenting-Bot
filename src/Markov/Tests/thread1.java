package Markov.Tests;
import Markov.Bots.Bot_MobileCombination;

import java.util.Scanner;

import static Markov.Bots.Bot_Haruhi_scr.useFireFox;
import static Markov.Scroller.*;

public class thread1 {

    // -Dfile.encoding=UTF-8
    public static void main(String[] args) throws InterruptedException {
        Saratoga = new Bot_MobileCombination();
        //System.out.println("Enter setting file name");
        Scanner sc = new Scanner(System.in);
        String file = "replyBotSetting.txt";//sc.next();
        Saratoga.readSetting(file);
        System.out.println("Start programme");
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID;
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID;//ani1_new1
        }
        if(useFireFox){
            Saratoga.initFireFox();
        }else{
            Saratoga.initBrowser();
        }
        //  test();
        scrollOffline();
        Saratoga.stay();

    }
}
