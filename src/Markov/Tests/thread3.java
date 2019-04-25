package Markov.Tests;

import Markov.Bots.Bot_Haruhi_scr;

import static Markov.Bots.Bot_Haruhi_scr.*;
import static Markov.Scroller.*;

public class thread3 {
    public static void main(String[] args) throws InterruptedException {
        //Writer option
        ////
        Saratoga = new Bot_Haruhi_scr();
        Saratoga.readSetting("postcopySetting.txt");
        System.out.println("Start programme");
        Saratoga.stay();
    }
}
