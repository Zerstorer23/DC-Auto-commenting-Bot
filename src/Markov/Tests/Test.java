package Markov.Tests;

import Markov.Bots.Bot_Haruhi_scr;

import static Markov.Bots.Bot.ngramCut;
import static Markov.Scroller.Saratoga;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Saratoga = new Bot_Haruhi_scr();
        ((Bot_Haruhi_scr) Saratoga).readBlackList();
        String processed = "abcde";
        String overhead = "";
        if (processed.length() > ngramCut) {
            int extra = processed.length() - ngramCut;
            overhead = processed.substring(0, extra);
            processed = processed.substring(extra, processed.length());

        }

        String out = overhead + processed;
        System.out.println(out);

    }
}
