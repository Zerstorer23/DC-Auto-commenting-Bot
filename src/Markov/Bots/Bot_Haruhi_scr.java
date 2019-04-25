package Markov.Bots;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import static Markov.Bots.Bot_nickChanger.Lexington;
import static Markov.Bots.Bot_replyLoader.Langley;
import static Markov.Decoder.getReader;
import static Markov.Scroller.*;
import static Markov.Scroller.Saratoga;

public class Bot_Haruhi_scr extends Bot {
    Stack<String> links = new Stack<>();
    ArrayList<String> fingerprints = new ArrayList<>();
    ArrayList<String> exceptNick = new ArrayList<>();
    ArrayList<String> exceptGall = new ArrayList<>();

    public static String searchURL = ".ED.95.98.EB.A3.A8.ED.9E.88";
    //            ".EB.B0.9C.ED.81.90.EB.A6.AC.EC.95.84"; 발큐리아
    // ;하루히
    public static String exceptionGallery = "haruhiism";
    //"haruhiism";

    public static int maxPage = 1; //첫 스크롤에 모을 페이지
    private boolean firstRun = true;
    public static boolean customName = false;// Use botname or writer's name?
    public static boolean leaveFootnote = false;
    public static boolean useFireFox = false;
    public static int postDelay = 38;
    public static boolean useNickChanger = true;
    public static boolean useReplyCopy = false;
    public static boolean cutTooLong = true;
    public static boolean leaveMessage = false;

    public static void main(String[] args) throws InterruptedException {
        //Writer option
        major = false; //메이져 갤러리
        yudong = false; //유동닉 사용 여부
        botName = "0x04";
        exceptionGallery = "haruhiism";
        searchURL = "하루히";
        maxPage = 2; //첫 스크롤에 모을 페이지
        customName = false;
        leaveFootnote = false;
        useFireFox = false;
        useNickChanger = true;
        useReplyCopy = true;
        cutTooLong = true;
        leaveMessage = true;
        postDelay = 6;
        ////
        Saratoga = new Bot_Haruhi_scr();
        System.out.println("Start programme");
        Saratoga.stay();
    }


    public void stay() throws InterruptedException {
        boolean stat = true;
        load("fingerprints.txt");
        readBlackList();
        if (useNickChanger) {
            Thread runLexington = new Thread(() -> {
                try {
                    Lexington.stay();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            runLexington.start();
        }
        if (useReplyCopy) {
            Thread runRanger = new Thread(() -> {
                try {
                    Langley.stay();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            runRanger.start();
        }


        if (useFireFox) {
            initFireFox();
        } else {
            initBrowser();
        }
        if (!yudong) logIn();

        do {
            try {
                if (useNickChanger) {
                    while (!((Bot_nickChanger) Lexington).isReady) {
                        System.out.println("렉싱턴 준비 안됨, 1초 대기");
                        Thread.sleep(1000);
                    }
                }
                if (useReplyCopy) {
                    while (!((Bot_replyLoader) Langley).isReady) {
                        System.out.println("레인져 준비 안됨, 1초 대기");
                        Thread.sleep(1000);
                    }
                }

                System.out.println("페이지 리로드");
                readPosts();
                copyPost();
                if (firstRun) {
                    firstRun = false;
                    maxPage = 1;
                }
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (stat);

    }

    public void readPosts() throws IOException {
        links.clear();
        for (int p = 1; p <= maxPage; p++) {
            Document doc = Jsoup.connect("http://search.dcinside.com/post/p/" + p + "/q/" + searchURL).maxBodySize(0).get();
            Elements table1 = doc.select("div[class=thumb_txt]");
            for (int i = 0; i < table1.size(); i++) {
                String link = table1.get(i).select("a[class=lnk_tit]").attr("href");
                String gallSrc = table1.get(i).select("a[class=lnk_gallery]").text();
                //  System.out.println("Number of classes: " + table1.size());
                //       String[] token  = link.split("/");
                //    String fp = token[token.length-1];
                if (!containsGall(link)) {
                    if (!isDuplicate(link)) {
                        //   System.out.println(i+". "+link + " is not duplicate");
                        links.add(link);
                    } else {
                        //     System.out.println(i + ". " + link + " is duplicate");
                    }
                }
            }
        }
    }

    public boolean isDuplicate(String link) {
        //True if contains
        return fingerprints.contains(link);
        /*for (int i = 0; i < fingerprints.size(); i++) {
            String target = fingerprints.get(i);
            if (target.equals(link))
                return true;
        }
        return false;*/
    }

    public void giveAnswer() throws AWTException, InterruptedException {


    }

    public void copyPost() throws IOException, InterruptedException {
        while (!links.empty()) {
            String link = links.pop();
            System.out.println("남은 포스트 = " + links.size());
            // 1. Parse Informations
            System.out.println("Connecting to ... " + link);
            try {
                Document post = Jsoup.connect(link).maxBodySize(0).get();
                Element nick = post.select("span[class=user_nick_nm]").first();
                String name = nick.text();
                boolean skip = containsNick(name);
                if (!skip) {
                    if (useNickChanger) {
                        String writer = post.select("span[class=user_nick_nm]").first().text();
                        System.out.println("Writer : " + writer);
                        ((Bot_nickChanger) Lexington).changeName(writer);
                    }

                    Element em1 = post.select("dl[class=wt_subject]").first().select("dd").first();
                    String title = em1.text();
                    System.out.println("Title : " + title);

                    String date = post.select("div[class=w_top_right]").first().select("li").first().text();
                    System.out.println("Date : " + date);

                    String body = post.select("div[class=s_write]").first().select("table").first().select("tr").first().select("td").first().toString();
                    String bodyHTML = body.substring(4,body.length()-5);
                    if(!bodyHTML.contains("<")){
                        bodyHTML="<p><br></p>"+bodyHTML;
                    }
                    System.out.println(bodyHTML);

                    // 3. Write content
                    writePost(title, name, bodyHTML, link, date);
                    fingerprints.add(link);
                    if (!firstRun) {
                        while (fingerprints.size() > 450) fingerprints.remove(0);
                    }
                   save("fingerprints.txt");

                    //4. Write replies
                    if (useReplyCopy) {
                        try {
                            Alert alert = driver.switchTo().alert();
                            System.out.println("[스크롤] 팝업 감지");
                            System.out.println(alert.getText());
                            alert.accept();
                            Saratoga.connectTo(driver.getCurrentUrl());
                        } catch (NoAlertPresentException ex) {
                        }
                        try {
                            ((Bot_replyLoader) Langley).copyReplies(link);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    System.out.println(name+"은 블랙리스트, 스킵" + link);
                }
            } catch (HttpStatusException e) {
                e.printStackTrace();
                System.out.println("Skip this. " + link);
            }
            Thread.sleep(postDelay * 1000);
        }

    }

    public void connectTo(String URL) {
        try {
            System.out.println("[하루히 스크롤] connected to " + URL);
            driver.get(URL);  //접속할 사이트
        } catch (UnhandledAlertException e) {
            driver.switchTo().alert().accept();
            System.out.println("Selenium RE connected to " + URL);
            driver.get(URL);  //접속할 사이트
        }
    }

    @Override
    public void readSetting(String fileName) {
        try {
            BufferedReader sc = null;
            sc = getReader(fileName);
            String temp = "";
            while (sc.ready()) {
                temp = sc.readLine();
                String[] token = temp.split(",");
                String head = token[0];
                String in = token[1];
                System.out.println(head + " = " + in);
                if (head.equals("botName")) {
                    botName = in;
                    System.out.println("봇 이름: " + botName);
                } else if (head.equals("major")) {
                    major = Boolean.parseBoolean(in);
                } else if (head.equals("yudong")) {
                    yudong = Boolean.parseBoolean(in);
                } else if (head.equals("exceptionGallery")) {
                    exceptionGallery = in;
                } else if (head.equals("searchURL")) {
                    searchURL = in;
                } else if (head.equals("maxPage")) {
                    maxPage = Integer.parseInt(in);
                } else if (head.equals("customName")) {
                    customName = Boolean.parseBoolean(in);
                } else if (head.equals("leaveFootnote")) {
                    leaveFootnote = Boolean.parseBoolean(in);
                } else if (head.equals("useFireFox")) {
                    useFireFox = Boolean.parseBoolean(in);
                } else if (head.equals("useNickChanger")) {
                    useNickChanger = Boolean.parseBoolean(in);
                } else if (head.equals("useReplyCopy")) {
                    useReplyCopy = Boolean.parseBoolean(in);
                } else if (head.equals("cutTooLong")) {
                    cutTooLong = Boolean.parseBoolean(in);
                } else if (head.equals("leaveMessage")) {
                    leaveMessage = Boolean.parseBoolean(in);
                } else if (head.equals("postDelay")) {
                    postDelay = Integer.parseInt(in);
                } else if (head.equals("initVPN")) {
                    //initVPN = Boolean.parseBoolean(in);
                }

            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading " + fileName + " DONE!");
    }

    public void writePost(String title, String nn, String bodyHTML, String link, String date) throws InterruptedException {
        String writeURL = "http://gall.dcinside.com/mgallery/board/write/?id=" + exceptionGallery;
        if (major) writeURL = "http://gall.dcinside.com/board/write/?id=" + exceptionGallery;
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ex) {
        }
        connectTo(writeURL);  //접속할 사이트
        if (yudong) {
            if (customName) nn = getBotName();
            driver.findElement(By.id("name")).sendKeys(nn);
            driver.findElement(By.id("password")).sendKeys("fear");
        }

        WebElement header = driver.findElement(By.cssSelector("input[name=subject]"));

        header.sendKeys(title);

        Thread.sleep(1000);
        driver.switchTo().defaultContent();
        driver.findElement(By.cssSelector("a[title='에디터 타입']")).click();
        Thread.sleep(1000);
        if (useFireFox) {// TODO
        }
        WebElement bodyFrame = driver.findElement(By.cssSelector("iframe[name=tx_canvas_wysiwyg]"));
        driver.switchTo().frame(bodyFrame);
        WebElement body = driver.findElement(By.tagName("body"));
        String content = bodyHTML;
        System.out.println("Size: " + content.length());
        if (content.length() > 2000 && cutTooLong) {
            content = content.substring(0, 999);
        }
        body.sendKeys(content);

        if (leaveFootnote) {
            String slicer = "<p><br>=====</p>\n";
            body.sendKeys(slicer);
            String footnote =
                    "<p></p>\n" +
                            "<p><u><span style=\"color: rgb(0, 85, 255);\">\uFEFF</span><a href=\"" + link + "\" target=\"_blank\" class=\"tx-link\"><span style=\"color: rgb(0, 85, 255);\">원본 갤러리로 이동</span></a></u>\uFEFF</p>";
            body.sendKeys(footnote);
            String dateNote =
                    "<p></p>\n" +
                            "<p>원본 작성 일시:&nbsp;" + date + "</p>";
            body.sendKeys(dateNote);
        }
        driver.switchTo().defaultContent();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("input[type=image]")).sendKeys(Keys.RETURN);
        Thread.sleep(100);
        driver.findElement(By.cssSelector("input[type=image]")).sendKeys(Keys.RETURN);
        Bot_replyLoader.setTargetTitle(title);
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            System.out.println(alert.getText() + " Alert is Displayed");
        } catch (NoAlertPresentException ex) {
            System.out.println("Alert is NOT Displayed");
        }
    }

    public void save(String fileName) throws FileNotFoundException {
        // Name + UserInfo + CVList+
        try {
            PrintWriter pw = new PrintWriter(new File(fileName));
            pw.write("???\n");
            System.out.println("Saving fingerprints...");
            for (int i = 0; i < fingerprints.size(); i++) {
                if (!fingerprints.get(i).equals("???")) {
                    String content = fingerprints.get(i) + "\n";
                    pw.write(content);
                }
            }
            pw.close();
        } catch (FileNotFoundException e) {

        }
        System.out.println("done!");
    }

    public void load(String fileName) {
        fingerprints = new ArrayList<>();
        try {
            BufferedReader sc = null;
            sc = getReader(fileName);
            String temp = "";
            while (sc.ready()) {
                temp = sc.readLine();
                //        System.out.println("Add fingerprint " + temp);
                fingerprints.add(temp);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading " + fileName + " DONE!");
    }

    public void readBlackList() {
        exceptGall = new ArrayList<>();
        exceptNick = new ArrayList<>();
        try {
            BufferedReader sc = null;
            sc = getReader("blacklist.txt");
            String temp = "";
            boolean galleryMode = true;
            while (sc.ready()) {
                temp = sc.readLine();
                if (temp.equals("##")) {
                    System.out.println("갤러리 읽기모드");
                    galleryMode = true;
                } else if (temp.equals("//")) {
                    System.out.println("닉네임 읽기모드");
                    galleryMode = false;
                } else {
                    if (galleryMode) {
                        exceptGall.add(temp);
                        System.out.println("갤러리에 추가 : " + temp);
                    } else {
                        exceptNick.add(temp);
                        System.out.println("닉네임에 추가 : " + temp);
                    }
                }
            }
            if (!exceptGall.contains(exceptionGallery)) exceptGall.add(exceptionGallery);
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading DONE!");
    }

    public boolean containsNick(String nick) {
        for (int i = 0; i < exceptNick.size(); i++) {
            if (nick.contains(exceptNick.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean containsGall(String gall) {
        for (int i = 0; i < exceptGall.size(); i++) {
            if (gall.contains(exceptGall.get(i))) {
                return true;
            }
        }
        return false;
    }
}
