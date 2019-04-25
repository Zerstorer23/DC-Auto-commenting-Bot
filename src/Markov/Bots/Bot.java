package Markov.Bots;

import Markov.Objects.Human;
import Markov.Objects.Page;
import Markov.Scroller;
import Markov.Vocabularies.Vocab;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import scala.collection.Seq;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static Markov.Bots.Bot_Haruhi_scr.useFireFox;
import static Markov.Decoder.*;
import static Markov.Predict.*;
import static Markov.Scroller.*;
import static Markov.Vocabularies.Vocab.findVocabByWord;
import static Markov.pageList.foundNames;

public abstract class Bot {
    static WebDriver driver;
    public static int ngramCut = 4;
    static ArrayList<String> didyouKnow = new ArrayList<>();
    static Human.HumanList Humans = new Human.HumanList();
    boolean nameFilled = false;

    public static String generateName(int length) {
        RandomStringGenerator randomStringGenerator =
                new RandomStringGenerator.Builder()
                        .withinRange('0', 'z')
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .build();
        return randomStringGenerator.generate(length);
    }

    static boolean checkFingerPrint(String link) {
        return !lookedID.contains(link);
    }

    public abstract void stay() throws InterruptedException;

    public abstract void giveAnswer() throws AWTException, InterruptedException;

    public abstract void connectTo(String URL);

    public abstract void readSetting(String fileName);

    static boolean dontPost = false;

    public void setTestSetting() {
        gallID = "blhx";
        fileToRead = "network-blhx+nGram4.txt";
        major = false;
        yudong = true;
        useFireFox = false;
        ngramCut = 4;
        skipRate = 0;
        dontPost = true;
        System.out.println("Setting test DONE!");
    }

    public String getBotName() {
        String nn = botName;
        switch (botNameMode) {
            case 1:
                nn = botName;//+ " 改";
                break;
            case 2:
                int len = (int) (Math.random() * namesBot.length);
                nn = namesBot[len];// + " 改";
                break;
            case 3:
                if (foundNames.size() > 5) {
                    int ind = (int) (Math.random() * foundNames.size());
                    nn = foundNames.get(ind);
                }
                break;
            default:
                nn = botName + " 改";
                break;
        }
        System.out.println("Selected name: " + nn);
        return nn;
    }

    private String getRandom(String a) {
        String[] token = a.split(" ");
        int ind = 0;
        if (token.length > 2) {
            ind = (int) (Math.random() * 3);
            System.out.println(ind);
        }
        System.out.println("추출된 단어: " + token[ind]);
        return token[ind];
    }

    private String getRefined(String a) {
        //String to Word
        System.out.println("추출시작");
        long time = System.currentTimeMillis();
        // Tokenize
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(a);
        List<KoreanTokenJava> list = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
        ArrayList<String> nouns = new ArrayList<>();

        for (KoreanTokenJava aList : list) {
            //   System.out.println(list.get(i).toString());
            if (aList.toString().contains("Noun")) nouns.add(aList.getText());
        }
        if (nouns.size() == 0) return getRandom(a);
        int ind = (int) (Math.random() * nouns.size());
        time = System.currentTimeMillis() - time;
        double elapsed = (double) time / 1000;
        System.out.println("(봇)추출된 단어: " + nouns.get(ind) + " time:" + elapsed);
        return nouns.get(ind);
        // [한국어(Noun: 0, 3), 를(Josa: 3, 1), 처리(Noun: 5, 2), 하는(Verb(하다): 7, 2), 예시(Noun: 10, 2),
        // 입니다(Adjective(이다): 12, 3), ㅋㅋㅋ(KoreanParticle: 15, 3), #한국어(Hashtag: 19, 4)]
    }

    public String selectWord(String key) {
        //word to Refined word
        //비슷한 키워드 검색
        //키워드 id 호출
        int index = -1;
        double maxFreq = -1;
        for (int i = 0; i < vocabArrayList.size(); i++) {
            Vocab haruhi = vocabArrayList.get(i);
            if (haruhi.word.contains(key) && haruhi.priority > maxFreq) {
                index = i;
                maxFreq = haruhi.priority;
            }
        }
        if (index < 0) {
            double[] scores = new double[vocabArrayList.size()];
            for (int i = 0; i < scores.length; i++) {
                double similarity = Scroller.distanceChecker.score(key, vocabArrayList.get(i).word);
                scores[i] = similarity;
            }
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] > maxFreq) {
                    index = i;
                    maxFreq = scores[i];
                }
            }
        }
        System.out.println("Found:[" + vocabArrayList.get(index).word + "] at " + index + " with " + maxFreq);
        return vocabArrayList.get(index).word;
    }

    String predict_ngram(String key) { //sort as you go
        Stack<Vocab> Frontier = new Stack<>();
        int expanded = 0; // Total number of expanded nodes
        boolean foundGoal = false; // Found Goal?
        System.out.println("Predicting " + key);
        //root start mode
        Vocab root = findVocabByWord(key);
        assert root != null;
        root.parent = null;
        root.level = 0;
        Vocab lastLooked = new Vocab();
        //Push initial state
        Frontier.add(root);
        //Start search
        int depth;
        while (!Frontier.isEmpty() && !foundGoal) {
            // Pop node to expand from Frontier
            Vocab temp = Frontier.pop();
            depth = temp.level;
            expanded++;
            temp.sortMap();
         /*     int c = 1;
          for (Map.Entry<String, Double> entry : temp.frequency.entrySet()) {
          //      System.out.println(c+"."+entry.getKey()+": "+entry.getValue());
                c++;
                if(c>10){break;}
            }*/
            double sum = temp.getSum();
            double lowerBound = 0;
            for (Map.Entry<String, Double> entry : temp.frequency.entrySet()) {
                lowerBound = (lowerBound + entry.getValue());
                double chance = lowerBound / sum; //say 0.3
                //        System.out.println(lowerBound+"/ "+sum+" = "+chance);
                double rand = Math.random(); //say 0.75
                //     System.out.println((int) (chance * 100) + "% chance > " + (int) (rand * 100) + "% ?");
                if (rand < chance) {
                    // if(curr>maxLimit)break;
                    String arcTo = entry.getKey();
                    //가나다+라 -> 나다라
                    //가나 다+라 -> 나 다라
                    String modString;
                    // System.out.println(temp.word + "->" + arcTo);
                    //   if (temp.word.contains(" ") && temp.word.length()<4 && temp.word.charAt(0)!=' ') {
                    //          modString = (temp.word + arcTo);
                    //     } else {
                    modString = (temp.word + arcTo).substring(1);
                    //       }
                    boolean limit = false;
                    Vocab haruhi;
                    if (arcTo.equals(EOL)) {
                        haruhi = new Vocab(modString, temp.priority);
                        limit = true;
                    } else {
                        System.out.println("Finding " + temp.word + " --> " + arcTo);
                        haruhi = findVocabByWord(modString);
                    }
                    assert haruhi != null;
                    haruhi.parent = temp;
                    haruhi.level = haruhi.parent.level + 1;
                    lastLooked = haruhi;
                    if (depth >= 100) limit = true;
                    if (limit) {
                        System.out.println("Expanded Nodes: " + expanded);
                        //Terminate search
                        foundGoal = true;
                        return printStack_ngram(haruhi);
                    } else {
                        Frontier.add(haruhi);
                    }
                    break;
                }
            }

        }
        //If search finished without finding a goal,
        if (!foundGoal) {
            printStack(lastLooked);
            System.out.println("NOT REACHABLE: " + key);
            System.out.println("Expanded Nodes: " + expanded);
            String newKey = getRandomKey();
            System.out.println("New Key: " + newKey);
            return predict_ngram(newKey);//("연상 실패: " + key);
        }
        return "예상치 못한일";
    }

    public void logIn() throws InterruptedException {
        try {
            driver.get("https://dcid.dcinside.com/join/login.php?s_url=http%3A%2F%2Fgall.dcinside.com%2Fmgallery%2Fboard%2Flists%2F%3Fid%3Dblhx");
            System.out.println("로그인 대기중");
            driver.findElement(By.id("id")).sendKeys("hmshood439");
            driver.findElement(By.id("pw")).sendKeys("gally886");
            Thread.sleep(100);
            driver.findElement(By.id("pw")).sendKeys(Keys.RETURN);
            Thread.sleep(1000);
            System.out.println("로그인 성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendKey(String text) throws InterruptedException {
        try {
            System.out.println("Writing reply " + text);
            driver.findElement(By.cssSelector("textarea[id^='memo_']")).sendKeys(text);
            Thread.sleep(500);
            driver.findElement(By.cssSelector("textarea[id^='memo_']")).sendKeys(Keys.RETURN);
            driver.findElement(By.cssSelector("button[class=\'btn_blue small repley_add\']")).click();
        } catch (WebDriverException ignored) {

        }
    }

    public void writeName() {
        if (yudong) {
            System.out.println("Write name called");
            String nn = getBotName();
            //   driver.findElement(By.cssSelector("input[placeholder=닉네임]")).sendKeys(nn);
            driver.findElement(By.cssSelector("input[id^='name']")).sendKeys(nn);
            driver.findElement(By.cssSelector("input[id^='password']")).sendKeys("fear");
        }
    }

    public void initFireFox() {
        //System.setProperty("webdriver.chrome.driver","chromedriver.exe");
        driver = null;


        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "logs.txt");
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("AI");
        myprofile.setPreference("http.response.timeout", 30);
        String user_agent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";
        myprofile.setPreference("general.useragent.override", user_agent);
        //  myprofile.setPreference("dom.max_script_run_time", 30);
        //   myprofile.setPreference("permissions.default.image", 1); //1 default, 3 blocks thirdparty 2 blocks all
        FirefoxOptions dc = new FirefoxOptions();
        dc.setCapability(FirefoxDriver.PROFILE, myprofile);
        driver = new FirefoxDriver(dc);
        //  driver.get("http://www.google.com/");
    }


    public void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        //   Map<String, String> mobileEmulation = new HashMap<>();
        //   mobileEmulation.put("deviceName", "Nexus 5");
        ChromeOptions options = new ChromeOptions();
        //      options.setExperimentalOption("mobileEmulation", mobileEmulation);
        driver = new ChromeDriver(options);
//     driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정
    }

    String getRefined(String a, boolean largest) {
        //String to Word
        System.out.println("추출시작, Largest: " + largest);
        long time = System.currentTimeMillis();
        // Tokenize
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(a);
        List<KoreanTokenJava> list = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
        ArrayList<String> nouns = new ArrayList<>();

        for (KoreanTokenJava aList : list) {
            if (aList.toString().contains("Noun")) nouns.add(aList.getText());
        }
        if (nouns.size() == 0) return getRandom(a);
        System.out.print("Found nouns: ");
        for (String noun : nouns) {
            System.out.print(noun + " ");
        }
        System.out.println(" ");

        //1. Got list of nouns
        int ind = (int) (Math.random() * nouns.size());
        String maxNoun = "아";
        int occurence = 0;
        if (largest) {
            for (String noun : nouns) {
                int point = isReachable(noun);
                if (point == -2) {
                    //  System.out.println(nouns.get(i) + " was too short.");
                    Vocab nnnn = selectVocab(noun);
                    assert nnnn != null;
                    if (nnnn.priority > occurence) {
                        maxNoun = nnnn.word;
                        occurence = nnnn.priority;
                    }
                } else if (point > occurence) {
                    System.out.println(noun + " was reachable and better. " + point);
                    occurence = point;
                    maxNoun = noun;
                } else {
                    System.out.println(noun + " was reachable but lower. " + point);
                }
            }
        } else {
            occurence = Integer.MAX_VALUE;
            for (String noun : nouns) {
                int point = isReachable(noun);
                if (point == -2) {
                    System.out.println(noun + " was too short.");
                    Vocab nnnn = selectVocab(noun);
                    if (nnnn != null) {
                        if (nnnn.priority < occurence) {
                            maxNoun = nnnn.word;
                            occurence = nnnn.priority;
                        }
                    }
                } else if (point < occurence) {
                    System.out.println(noun + " was reachable and better. " + point);
                    occurence = point;
                    maxNoun = noun;
                } else {
                    System.out.println(noun + " was reachable but higher. " + point);
                }
            }
        }
        if (occurence == 0 || occurence == Integer.MAX_VALUE) maxNoun = nouns.get(ind);
        time = System.currentTimeMillis() - time;
        double elapsed = (double) time / 1000;
        System.out.println("추출된 단어: " + maxNoun + " time:" + elapsed);
        return maxNoun;
    }

    private Vocab selectVocab(String key) {
        //word to Refined word
        //비슷한 키워드 검색
        //키워드 id 호출
        int index = -1;
        double maxFreq = -1;
        for (int i = 0; i < vocabArrayList.size(); i++) {
            Vocab haruhi = vocabArrayList.get(i);
            if (haruhi.word.contains(key) && haruhi.priority > maxFreq) {
                index = i;
                maxFreq = haruhi.priority;
            }
        }

        if (index >= 0) {
            System.out.println("    Select w Found:[" + vocabArrayList.get(index).word + "] at " + index + " with occurence " + maxFreq);
            return vocabArrayList.get(index);
        } else {
            System.out.println("  Couldn't find any similar word  ");
            return null;
        }
    }

    public String getWikiWisdom() {
        int rand = (int) (Math.random() * didyouKnow.size());
        return didyouKnow.get(rand);
    }

    String makeSentence(Page haruhi) {
        System.out.println(haruhi.title);
        String processed = getRefined(haruhi.title, false);//getFirst(haruhi.title);
        String overhead = "";
        if (processed.length() > ngramCut) {
            int extra = processed.length() - ngramCut;
            overhead = processed.substring(0, extra);
            processed = processed.substring(extra);
        }
        if (vocabHash.containsKey(processed)) {
            String out = overhead + predict_ngram(processed);
            if (out.split(" ")[0].equals(EOL)) {
                return null;
            }
            return out;
        } else {
            return null;
        }
    }

}
