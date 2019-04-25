package Markov;

import Markov.Vocabularies.Vocab;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static Markov.Scroller.EOL;
import static Markov.pageList.*;

public class Decoder {

    public static HashMap<String, Integer> vocabHash = new HashMap<>(); //단어, 위치 인덱스
    public static ArrayList<Vocab> vocabArrayList = new ArrayList<Vocab>();
    public static ArrayList<String> sentences = new ArrayList<>();
    public static String[] namesBot = {
            "랭글리", "사라토가", "렉싱턴", "레인져", "요크타운", "호넷", "와스프"
    };

    public static HashMap<String, Double> sortHashMapByValues(HashMap<String, Double> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapValues);
        Collections.reverse(mapKeys);
        HashMap<String, Double> sortedMap =
                new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            double val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                double comp1 = passedMap.get(key);
                double comp2 = val;

                if (comp1 == comp2) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public static void initialise() {
        df.setMaximumFractionDigits(2);
        df.setGroupingUsed(false);
        vocabHash = new HashMap<>();
        vocabArrayList = new ArrayList<>();
        Vocab yuki = new Vocab(Scroller.EOL, 0);
        yuki.position = 0.0;
        yuki.priority = 1;
        // decodeID++;
        vocabArrayList.add(yuki);
        vocabHash.put(yuki.word, 0);
        System.out.println("Initialisation finished");
    }

    public static void writeToFile(String fileName) {
        // Name + UserInfo + CVList+
        try {
            PrintWriter pw = new PrintWriter(new File(fileName));
            for (int i = 0; i < vocabArrayList.size(); i++) {
                System.out.println("Writing... " + i);
                Vocab haruhi = vocabArrayList.get(i);
//HEADER
                String content = haruhi.word + "," + haruhi.position + "," + haruhi.priority;
                String tail = "";
                //TAILS
                int limit = 10;
                int c = 0;
                for (Map.Entry<String, Double> entry : haruhi.frequency.entrySet()) {
                    String arcTo = entry.getKey();
                    if (arcTo.length() > 0) {
                        double arcWeight = entry.getValue();
                        tail = tail + "/" + arcTo + "," + arcWeight;
                    }
                    c++;
                    if (c > limit) break;
                }
                content = content + tail + "\n";
                pw.write(content);
            }
            pw.close();
        } catch (FileNotFoundException e) {

        }
        System.out.println("done!");
    }

    public static void readFile(String fileName) {
        //완성된 FSM 네트워크 로드
        //vocabArray와 Hash 리셋. 두번 부르려면 combine 사용
        initialise();
        long time = System.currentTimeMillis();
        try {
            BufferedReader sc = getReader(fileName);
            String temp = "";
            int prg = 0;
            while (sc.ready()) {
                temp = sc.readLine();
                  if(prg%1000 == 0)System.out.println(prg);
                  prg++;
                String[] token = temp.split("/");
                String[] entry = token[0].split(",");
                Vocab haruhi = new Vocab(entry[0], Double.parseDouble(entry[1]));
                haruhi.priority = Integer.parseInt(entry[2]);
                //   System.out.println(temp);
                for (int i = 1; i < token.length; i++) {
                    entry = token[i].split(",");
                    String indexTo = entry[0];
                    double freq = Double.parseDouble(entry[1]);
                    haruhi.setArc(indexTo, freq);
                }
                vocabHash.put(haruhi.word, vocabArrayList.size());
                vocabArrayList.add(haruhi);
             }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        time = System.currentTimeMillis() - time;
        System.out.println("VocabHash Size = " + vocabHash.size());
        System.out.println("VocabArrayList Size = " + vocabArrayList.size());
        System.out.println("Took " + (double) time / 1000 + " seconds");
    }

    public static void combineFile(String fileName) {
        //완성된 FSM 네트워크 로드, 중복처리, 합성, buildNetwork와 양립불가.
        // reafile후 사용, 이후 writeFile
        long time = System.currentTimeMillis();
        System.out.println("VocabHash Size = " + vocabHash.size());
        System.out.println("vocabArrayList Size = " + vocabArrayList.size());
        try {
            BufferedReader sc = getReader(fileName);
            String temp = "";
            int prg = 0;
            while ((temp = sc.readLine()) != null) {
                String[] token = temp.split("/");
                String[] entry = token[0].split(",");
                Vocab haruhi = new Vocab(entry[0], Double.parseDouble(entry[1]));
                haruhi.priority = Integer.parseInt(entry[2]);
                for (int i = 1; i < token.length; i++) {
                    entry = token[i].split(",");
                    String indexTo = entry[0];
                    double freq = Double.parseDouble(entry[1]);
                    haruhi.setArc(indexTo, freq);
                }

                if (vocabHash.containsKey(entry[0])) {
                    //중복, 합성필요
                    int index = vocabHash.get(entry[0]);
                    vocabArrayList.get(index).combineArc(haruhi);
                } else {
                    vocabArrayList.add(haruhi);
                }
                System.out.println("Combining... " + prg);
                prg++;
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        time = System.currentTimeMillis() - time;
        System.out.println("VocabHash Size = " + vocabHash.size());
        System.out.println("VocabArrayList Size = " + vocabArrayList.size());
        System.out.println("Took " + (double) time / 1000 + " seconds");
    }

    public static void readKey() {
        // Name + UserInfo + CVList+
        try {
            BufferedReader sc = getReader("keywords.txt");
            String temp = "";
            while ((temp = sc.readLine()) != null) {
                keywords.add(temp);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading File DONE! Key Size = " + keywords.size());
    }

    public static int vocab_Contains(String word) {
        for (int i = 0; i < vocabArrayList.size(); i++) {
            if (vocabArrayList.get(i).word.equals(word)) return i;
        }
        return -1;
    }

    public static void buildNetwork(String fileName) {
        //RawFile에서 네트워크 생성, 중복확인을 위한 Hash사용
        try {
            BufferedReader sc = getReader(fileName);
            String temp = "";
            int prg = 0;
            while ((temp = sc.readLine()) != null) {
                System.out.println("Reading ... " + prg);
                prg++;
                //   if(prg>all)break;
                temp = removeChars(temp).toLowerCase();
                sentences.add(temp);
            }
            sc.close();
            //ReWrite

            for (int j = 0; j < sentences.size(); j++) {
                System.out.println("Building header ... " + j);
                String title = sentences.get(j);
                String token[] = title.split(" ");
                for (int i = 0; i < token.length; i++) {
                    //타이틀 단어 순회
                    String instance = token[i];
                    if (vocabHash.containsKey(instance)) {
                        int index = vocabHash.get(instance);
                        Vocab haruhi = vocabArrayList.get(index);
                        double totalPosition = haruhi.position * haruhi.priority;
                        vocabArrayList.get(index).priority++;
                        vocabArrayList.get(index).position = (totalPosition + i) / (vocabArrayList.get(index).priority);
                    } else {
                        Vocab haruhi = new Vocab(instance, i);
                        vocabHash.put(instance, vocabArrayList.size());
                        vocabArrayList.add(haruhi);
                        haruhi.priority = 1;
                    }
                }
            }

            buildFSM();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading File DONE! Markov.Vocabularies.Vocab Size = " + vocabArrayList.size());
    }

    public static void buildFSM() {
        for (int j = 0; j < sentences.size(); j++) {
            System.out.println("Building ... " + j);
//전처리
            String token[] = sentences.get(j).split(" ");
//단어순회
            for (int i = 0; i < token.length; i++) {
                String instance = token[i];
                int index = vocabHash.get(instance);

                String targetIndex = EOL;
                if (i < token.length - 1) {
                    targetIndex = token[i + 1];
                }
                vocabArrayList.get(index).addArc(targetIndex);
            }
        }
    }

    public static PrintWriter getPrinter(String fileName) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName),
                    StandardCharsets.UTF_8), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pw;
    }

    public static BufferedReader getReader(String fileName) {
        BufferedReader sc = null;
        try {
            sc = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), "UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sc;
    }
}
