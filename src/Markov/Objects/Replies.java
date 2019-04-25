package Markov.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import static Markov.Decoder.getReader;
import static Markov.Decoder.vocabArrayList;

public class Replies {
    static ArrayList<Replies> listOfPerson = new ArrayList<>();
    String personality = "";
    ArrayList<String> lines = new ArrayList<>();

    public Replies(String personality) {
        this.personality = personality;
    }

    public static void readReplies() {
        // Name + UserInfo + CVList+
        try {
            BufferedReader sc = null;
            sc = getReader("인사말.txt");
            String temp = "";
            int linesToAddAt = -1;
            while ((temp = sc.readLine()) != null) {
               // System.out.println(temp);
                if(temp.contains("#")){
                    Replies set = new Replies(temp.substring(1));
                    listOfPerson.add(set);
                    linesToAddAt++;
                }else{
                    listOfPerson.get(linesToAddAt).lines.add(temp);
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading File DONE! Markov.Vocabularies.Vocab Size = " + vocabArrayList.size());
    }
}
