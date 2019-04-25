package Markov;

import Markov.Objects.Human;
import Markov.Vocabularies.Vocab;

import java.text.DecimalFormat;
import java.util.*;

import static Markov.Decoder.vocabArrayList;
import static Markov.Decoder.vocabHash;


public class pageList {
    public static DecimalFormat df = new DecimalFormat();
    static ArrayList<String> keywords = new ArrayList<String>();

    public static Human.HumanList targetNames = new Human.HumanList();
    public static ArrayList<String> foundNames = new ArrayList<>();



    /*public static void add(String title, int index) {
        if (!noDuplicate(index)) {
            return;
        }//Checks duplicate by article ID
        title = title.toLowerCase();
        sentenceList.add(title);
        title = removeChars(title);
        String token[] = title.split(" ");

        for (int i = 0; i < token.length; i++) {
            //타이틀 단어 순회
            String instance = token[i];
            if (instance.equals("")) {
                return;
            }//빈 제목
            instance = instance.toLowerCase();
            boolean exists = contains(instance);
            if (!exists) {
                Vocab haruhi = new Vocab(instance, decodeID);
                decodeID++;
                vocabArrayList.add(haruhi);
            }
        }
        duplicateList.add(index);
    }*/


    static String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

    public static String removeChars(String title) {
        title = title.replaceAll(match, " ");
        //    title = title.replace(".", " ");
        title = title.replace(",", "");
        title = title.replace("/", "");
        title = title.trim().replaceAll(" +", " ");
        //    title = title.replaceAll(" ", "");
        return title.toLowerCase();
    }


    public static void printFSM() {
        // Name + UserInfo + CVList+
        for (int i = 0; i < vocabArrayList.size(); i++) {
            Vocab haruhi = vocabArrayList.get(i);
            String content = haruhi.word + "," + i + "," + haruhi.priority;
            String tail = "";
            //TAILS
            for (Map.Entry<String, Double> entry : haruhi.frequency.entrySet()) {
                String arcTo = entry.getKey();
                double arcWeight = entry.getValue();
                tail = tail + "/" + arcTo + "," + arcWeight;
            }
            content = content + tail;
        }

    }


    public static boolean contains(String inst) {
        for (int i = 0; i < vocabArrayList.size(); i++) {
            if (vocabArrayList.get(i).word.equals(inst)) {
                return true;//this.vocabArrayList.get(i).index;
            }
        }
        return false;
    }


}

