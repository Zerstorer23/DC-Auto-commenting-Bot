package Markov.Vocabularies;

import Markov.Decoder;
import Markov.Scroller;

import java.util.HashMap;
import java.util.Map;

import static Markov.Decoder.vocabHash;
import static Markov.Decoder.vocabArrayList;


public class Vocab {
    //COLLECTION MODE
    public String word;
    public double position;
    public int priority = 0;
    public HashMap<String, Double> frequency;

    //BFS MODE, needs reset all the time
    public int level = 0;
    public Vocab parent = null;
    public boolean containsKey = false;

    public Vocab() {
        this.word = "ROOT";
        frequency = new HashMap<>();
    }

    public Vocab(String tit, double pos) {
        this.word = tit;
        this.position = pos;
        this.level = 0;
        frequency = new HashMap<>();
    }

    public void addArc(String indexTo) {
        if (indexTo == null) return;
        if (frequency.containsKey(indexTo)) {
            //Exists
            frequency.replace(indexTo, frequency.get(indexTo) + 1);

        } else {
            //Add new key
            frequency.put(indexTo, 1.0);

        }
    }

    public void setArc(String indexTo, double freq) {
        frequency.put(indexTo, freq);
    }

    public void updateArc(String indexTo, double freq) {
        double prev = frequency.get(indexTo);
        frequency.replace(indexTo, prev + freq);
    }

    public void combineArc(Vocab haruhi) {
        double total = this.position * this.priority;
        double totalB = haruhi.position * haruhi.priority;
        this.priority = this.priority + haruhi.priority;
        this.position = (total + totalB) / this.priority;


        for (Map.Entry<String, Double> entry : haruhi.frequency.entrySet()) {
            String nextWord = entry.getKey();
            if (frequency.containsKey(nextWord)) {
                //Exists
                frequency.replace(nextWord, frequency.get(nextWord) + entry.getValue());
            } else {
                //Add new key
                frequency.put(nextWord, 1.0);
            }
        }
    }

    public double getSum() {
        double sum = 0;
        for (double value : frequency.values()) {
            sum = sum + value;
        }
        return sum;
    }

    public void sortMap() {
        HashMap<String, Double> temp = (HashMap<String, Double>) this.frequency.clone();
        this.frequency = Decoder.sortHashMapByValues(temp);
    }

    public void printMap() {
        System.out.println("========= " + this.word + " ============");
        for (Map.Entry<String, Double> entry : frequency.entrySet()) {
            String arcTo = entry.getKey();
            double arcWeight = entry.getValue();
            System.out.println("-> " + arcTo + ": " + arcWeight);
        }

    }

    public static Vocab findVocabByWord(String word) {
        if (vocabHash.containsKey(word)) {
            int index = vocabHash.get(word);
            System.out.println("Hash contains "+word+" at "+index);
            return vocabArrayList.get(index);
        }
       System.out.println("Hash does not contain ["+word+"]");
        return null;
    }

    public void printString() {
        System.out.println(this.word + ": " + this.priority + " at " + this.position);
    }
}
