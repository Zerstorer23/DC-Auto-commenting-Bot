package Markov.Bots;

import java.util.Scanner;

import static Markov.Bots.Bot.ngramCut;
import static Markov.Decoder.*;
import static Markov.Scroller.*;

public class PredictionMachine {

    public static void main(String[] args) {
        fileToRead = "data/network-blhx+ngram4.txt";//"network-haruhi.txt";
        Bot_MobileLite Lexington = new Bot_MobileLite();
        ngramCut = 4;
        System.out.println("Start programme");
        readFile(fileToRead);
        System.out.println(vocabHash.containsKey(EOL));
        System.out.println("    #Hash: " + vocabHash.size());
        System.out.println("    =Array: " + vocabArrayList.size());

        System.out.println("Ready for input: ");
        Scanner scin = new Scanner(System.in);
        String input = scin.next();
        boolean stat = true;
        do {
            if (input != "QUIT") {
                input = input.replaceAll("_", " ");

                ///---------
                System.out.println(input);
                String processed = Lexington.getRefined(input, false);//getFirst(haruhi.title);
                String overhead = "";
                if (processed.length() > ngramCut) {
                    int extra = processed.length() - ngramCut;
                    System.out.println("Processed = "+processed+" / extra: "+extra);
                    overhead = processed.substring(0, extra);
                    processed = processed.substring(extra, processed.length());
                }
                String predicted = Lexington.predict_ngram(processed);
                System.out.println(overhead+" + "+predicted);
                String out = overhead + predicted;
                System.out.println(out);


                ///-----------

                input = scin.next();
            } else {
                stat = false;
            }
        } while (stat);
        System.exit(32);
    }
}
