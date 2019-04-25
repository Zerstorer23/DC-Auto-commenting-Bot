package Markov;

import Markov.Bots.Bot;
import Markov.Bots.Bot_MobileCombination;
import Markov.Crawler.Crawler;
import Markov.Crawler.Crawler_DC;
import Markov.Objects.Page;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.api.StringDistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import static Markov.Decoder.readFile;


public class Scroller {
    public static String gallID = "idolmaster";
//MODE
    public static String fileToRead =  "network-idolmaster.txt";//"network-haruhi.txt";
    public static String botName = "사라토가";

    public static String mainPage;

    //CRAWL options
    public static boolean major = true; //메이져 갤러리
    public static int seconds = 5; // 페이지 스크롤간 간격
    public static double delay = 5; //댓글사이의 간격 -- 4.5
    public static boolean yudong = true; //유동닉 사용 여부
    public static boolean writePost = false; //30개마다 글 작성
    public static int skipRate = 3;
    public static int botNameMode = 2;
    //1=use mine 2=use rand bot preset 3= steal user name

    public static Bot Saratoga = new Bot_MobileCombination();
    public static Crawler crawler = new Crawler_DC();

    public final static String EOL = "[EOL]";
    static JaroWinkler jaro = new JaroWinkler();
    public static StringDistance distanceChecker = jaro.getDistance();
    public static Stack<Page> instances = new Stack<>();
    public static ArrayList<String> lookedID = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Start programme");
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID;
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID;//ani1_new1
        }
        scrollOffline();
        Saratoga.stay();
    }


    public static String extractCommentView(String link) {
        String[] token = link.split("/");
        if (major) {
            token[2] = "comment_view";
        } else {
            token[3] = "comment_view"; //Minor gall
        }
        String empty = "";
        for (int x = 1; x < token.length; x++) { //0 fo major?
            empty = empty + "/" + token[x];
        }
        return empty;
    }

    public static void scrollOffline() {
        readFile("data/" +fileToRead);
        //buildRoot();
    }

    public static void ps(String a) {
        System.out.println(a);
    }
}

