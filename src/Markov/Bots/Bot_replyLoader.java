package Markov.Bots;

import Markov.Objects.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static Markov.Bots.Bot_Haruhi_scr.exceptionGallery;
import static Markov.Bots.Bot_Haruhi_scr.leaveMessage;
import static Markov.Bots.Bot_Haruhi_scr.useFireFox;
import static Markov.Bots.Bot_nickChanger.Lexington;
import static Markov.Bots.Bot_nickChanger.currName;
import static Markov.Scroller.*;
import static Markov.pageList.foundNames;

public class Bot_replyLoader extends Bot {
    //"haruhiism";
    public boolean isReady = false;
    private WebDriver subDriver;
    private static String targetTitle = "unknown";
    public static Bot Langley = new Bot_replyLoader();
    public static Queue<Page> replyList = new LinkedList<>();

    @Override
    public void readSetting(String fileName) {

    }

    public static void setTargetTitle(String title) {
        targetTitle = title;
    }

    public static void main(String[] args) throws InterruptedException {
        Langley.stay();
    }

    public void stay() throws InterruptedException {
        if (useFireFox) {
            initFireFox();
        } else {
            initBrowser();
        }
        //   if(leaveMessage) logIn();
        System.out.println("레인져 부팅 완료");
        isReady = true;
    }

    @Override
    public void logIn() throws InterruptedException {
        subDriver.get("https://dcid.dcinside.com/join/login.php?s_url=http%3A%2F%2Fgall.dcinside.com%2Fmgallery%2Fboard%2Flists%2F%3Fid%3Dblhx");
        System.out.println("댓글 드라이버 로그인 대기중");
        subDriver.findElement(By.id("id")).sendKeys("hmshood439");
        subDriver.findElement(By.id("pw")).sendKeys("gally886");
        Thread.sleep(1000);
        subDriver.findElement(By.id("pw")).sendKeys(Keys.RETURN);
        Thread.sleep(500);
        System.out.println("로그인 성공");
    }

    @Override
    public void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
       /* if (initVPN) {
            options.addExtensions(new File("extension_6_2_5_0.crx"));
            options.addExtensions(new File("extension_2_9_2_0.crx")); //AdGuard
        }*/

        subDriver = new ChromeDriver(options);
        subDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정

    }

    public void giveAnswer() throws AWTException, InterruptedException {


    }

    public void connectTo(String URL) {
        try {
            System.out.println("[댓글로더] connected to " + URL);
            subDriver.get(URL);  //접속할 사이트
        } catch (UnhandledAlertException e) {
            subDriver.switchTo().alert().accept();
            System.out.println("Selenium RE connected to " + URL);
            subDriver.get(URL);  //접속할 사이트
        }
    }

    public void copyReplies(String commURL) throws NoSuchElementException, InterruptedException, IOException {
        // while(!replyList.isEmpty())replyList.poll();
        commURL = eCommentView(commURL);
        if (leaveMessage) {
            while (!isReady) {
                System.out.println("메세지남기기 준비 안됨, 2초 대기");
                Thread.sleep(2000);
            }
        }
        connectTo(commURL);
        setUp();
        crawl_replies();
        if (leaveMessage) {
            Thread leaveMsg = new Thread(() -> {
                try {
                    isReady = false;
                    driver.findElement(By.id("name")).sendKeys(botName);
                    driver.findElement(By.id("password")).sendKeys("fear");
                    String text = "스즈미야 하루히갤러리에 와주세요.";
                    driver.findElement(By.id("memo")).sendKeys(text);
                    Thread.sleep(100);
                    driver.findElement(By.id("memo")).sendKeys(Keys.RETURN);
                    isReady = true;
                    //  driver.findElement(By.By.id("re_member_write")).click();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            leaveMsg.start();
        }
        //Check if correct page was loaded
        checkPageisComment();


        while (!replyList.isEmpty()) {
            Page yuki = replyList.poll();
            if (!currName.equals(yuki.writer)) {
                ((Bot_nickChanger) Lexington).changeName(yuki.writer);
            }
            System.out.println("댓글 작성: " + yuki.title);
            writeComment(yuki.title);
            if (!replyList.isEmpty()) Thread.sleep(2750);
        }
        System.out.println("댓글 작성 완료, 다음 글로");
    }

    private void checkPageisComment() {
        String myURL = "http://gall.dcinside.com/mgallery/board/write/?id=" + exceptionGallery;
        System.out.println(driver.getCurrentUrl());
        try {
            if (driver.getCurrentUrl().contains("write")) {
                System.out.println("URL 수정 필요");
                if (major) {
                    mainPage = "http://gall.dcinside.com/board/lists/?id=" + exceptionGallery;
                } else {
                    mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + exceptionGallery;//ani1_new1
                }
                System.out.println("Main page = " + mainPage);
                if (targetTitle.length() > 10) targetTitle = targetTitle.substring(0, 10);
                Document doc = Jsoup.connect(mainPage).maxBodySize(0).get();

                System.out.println("Target = " + targetTitle);
                Elements table1 = doc.select("tr[class=tb]");
                //System.out.println("Frequency: "+nums.size()+" "+links.size()+" "+recommends.size()+" ");
                for (int i = 0; i < table1.size(); i++) {
                    Element haruhi = table1.get(i).select("td[class=t_subject]").select("a").first();
                    String title = haruhi.text();
                    if (title.length() > 10) title = title.substring(0, 10);
                    System.out.println("->" + title);
                    if (title.equals(targetTitle)) {
                        String link = "http://gall.dcinside.com";
                        String tempLink = haruhi.attr("href");
                        link = link + extractCommentView(tempLink);
                        driver.get(link);
                        System.out.println(link + " 로 이동");
                        break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeComment(String text) throws InterruptedException {
        //사라토가 사용
        if (driver.getCurrentUrl().equals("http://gall.dcinside.com/mgallery/board/write/?id=haruhiism")) {
            //TODO maybe press a button
        }
        try {
            driver.findElement(By.id("memo")).sendKeys(text);
            Thread.sleep(100);
            driver.findElement(By.id("memo")).sendKeys(Keys.RETURN);
            //  driver.findElement(By.By.id("re_member_write")).click();
        } catch (WebDriverException e) {

        } catch (Exception e) {
            System.out.println("오류 감지");
            e.printStackTrace();

        }
    }

    private String eCommentView(String link) {
        String[] token = link.split("/");
        token[token.length - 2] = "comment_view";
        String empty = token[0];
        for (int x = 1; x < token.length; x++) { //0 fo major?
            empty = empty + "/" + token[x];
        }
        return empty;
    }

    private void setUp() {
        String html = subDriver.getPageSource().toString();
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("a.html"),
                    StandardCharsets.UTF_8), true);
            pw.write(html);
            pw.close();
        } catch (FileNotFoundException e) {

        }
    }

    private void crawl_replies() throws IOException {
        //페이지의HTML소스를 저장하는 기능.
        File input = new File("a.html");
        Document doc = Jsoup.parse(input, "UTF-8", subDriver.getCurrentUrl());
        //   Document doc = Jsoup.connect(URL).maxBodySize(0).get();
        // System.out.println();
        Elements replies = doc.select("tr[class=reply_line]");
        for (int i = 0; i < replies.size(); i++) {
            String content = replies.get(i).select("td[class=reply]").first().ownText();
            String writer = replies.get(i).select("td[class=user user_layer]").first().attr("user_name");
            System.out.println(i + ". " + content + " / by " + writer);

            if (content.length() > 0) {
                Page yuki = new Page(content, content);
                yuki.writer = writer;
                replyList.add(yuki);
            }
            if (replyList.size() >= 4) break;//최대 4개만
        }
    }

}
