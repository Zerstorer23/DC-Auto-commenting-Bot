package Markov;

import Markov.Vocabularies.Vocab;

import java.util.Stack;

import static Markov.Bots.Bot.ngramCut;
import static Markov.Decoder.vocabArrayList;
import static Markov.Decoder.vocabHash;

public class Predict {
    //PREDICT//

    public static int isReachable(String keyword) {
        int depth = keyword.length() - ngramCut;
        int occurence = 0;
        int level = 0;
        if(keyword.length()<=ngramCut) return -2;
        String buffer = keyword.substring(0, ngramCut);
        Vocab node;
        String target;
        System.out.println("pos: "+(level+ngramCut)+" / length: "+keyword.length()+" / depth: "+depth);
        while (level < depth && occurence != -1) {
            System.out.println("Buffer: "+buffer);
            if (vocabHash.containsKey(buffer)) {
                System.out.println("pos: "+(level+ngramCut)+" / length: "+keyword.length()+" / depth: "+depth);
                System.out.println("---> "+buffer+" is found");
                node = vocabArrayList.get(vocabHash.get(buffer));
                level++;
                if (node.priority > occurence) occurence = node.priority;
       //         if(level >keyword.length()) return occurence;
                target = String.valueOf(keyword.charAt(level+ngramCut-1));
                System.out.println("target: "+target);
                if(!node.frequency.containsKey(target)) return -1;
                buffer = (buffer+target).substring(1);
            } else {
                occurence = -1;
            }

        }
        return occurence;
    }

    public static String stringConversion(String sentence) {
        //문장의 모든 부분을 비슷한 키워드로 변경
        String[] token = sentence.split(" ");

        for (int x = 0; x < token.length; x++) {
            System.out.println("가공중: " + token[x]);
            if (!vocabHash.containsKey(token[x])) {
                String word = findMostSimilar(token[x]);
                token[x] = word;
                System.out.println("    ┗>" + token[x]);
            } else {
                System.out.println("    = 해쉬에 존재: " + token[x]);
            }
        }
        //Replaced
        StringBuilder result = new StringBuilder(token[0]);
        for (int i = 1; i < token.length; i++) result.append(" ").append(token[i]);
        return result.toString();
    }

    private static String findMostSimilar(String key) {
        //word to Refined word
        //비슷한 키워드 검색

        int index = -1;
        double maxSim = -1;
        for (int i = 0; i < vocabArrayList.size(); i++) {
            double similarity = Scroller.distanceChecker.score(key, vocabArrayList.get(i).word);
            if (similarity > maxSim) {
                index = i;
                maxSim = similarity;
            }
        }
        System.out.println("가장 비슷한 키워드 : " + vocabArrayList.get(index).word + " 해쉬존재 " + vocabHash.containsKey(vocabArrayList.get(index).word));
        return vocabArrayList.get(index).word;
    }

    public static String getRandomKey() {
        int index = (int) (Math.random() * vocabArrayList.size());
        return vocabArrayList.get(index).word;
    }

    public static String printStack(Vocab ju) {
        Stack<Vocab> printStack = new Stack<>();
        Vocab path = ju.parent;
        StringBuilder space = new StringBuilder();
        int c = ju.level;
        while (c > 0) {
            //    System.out.println("Level. "+path.level);
            printStack.push(path);
            path = path.parent;
            c--;
        }
        // printStack.push(path);
        //Start printing out as specified
        while (!printStack.isEmpty()) {
            String temp = printStack.pop().word;
            space.append(temp).append(" ");
        }
        System.out.println(space);
        return space.toString();
    }

    public static String printStack_ngram(Vocab ju) {
        Stack<Vocab> printStack = new Stack<>();
        Vocab path = ju.parent;
        StringBuilder space = new StringBuilder();
        int c = ju.level;
        while (c > 0) {
            //    System.out.println("Level. "+path.level);
            printStack.push(path);
            path = path.parent;
            c--;
        }
        // printStack.push(path);
        //Start printing out as specified
        String temp = printStack.pop().word;
        space.append(temp);
        while (!printStack.isEmpty()) {
            String word = printStack.pop().word;
            temp = String.valueOf(word.charAt(word.length() - 1));
//            System.out.println("PRINT "+temp);
            space.append(temp);
        }
        System.out.println(space);
//        space = space.replaceAll("\\[EOL\\]","");
        return space.toString();
    }
}
