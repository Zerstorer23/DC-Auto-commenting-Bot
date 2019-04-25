package Markov.Bots;

import Markov.Objects.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static Markov.Bots.Bot_Haruhi_scr.useFireFox;
import static Markov.Decoder.getReader;
import static Markov.Scroller.*;
import static Markov.Tests.thread2.test;

public class Bot_MobileCombination extends Bot {
    private boolean countMode = false;
    private int count = 0;
    private String[] replyPreset = {"스트라이크 위치스 신작 방영중","그러냐", "그러게", "ㅋㅋㅋ","ㄹㅇ" };

    public static void main(String[] args) {
        gallID = "blhx";
        fileToRead = "network-blhx+nGram.txt";//"network-haruhi.txt";
        botName = "사라토가";

        //CRAWL options
        major = true; //메이져 갤러리
        seconds = 5; // 페이지 스크롤간 간격
        delay = 5; //댓글사이의 간격 -- 4.5
        yudong = true; //유동닉 사용 여부
        writePost = false; //30개마다 글 작성
        skipRate = 2;
        botNameMode = 2;
        //1=use mine 2=use rand bot preset 3= steal user name
    }

    public void stay() {
        if (!yudong) {
            logIn();
        }
        do {
            try {
                crawler.scrollMain();
                giveAnswer();
                Thread.sleep(seconds * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);

    }


    public void giveAnswer() throws InterruptedException {
        while (!instances.empty()) {
            long starttime = System.currentTimeMillis();
            Page haruhi = instances.pop();
            if (countMode) {
                count++;
                switch (count) {
                    case 1:
                        System.out.print("Count: " + count);
                        break;
                    default:
                        System.out.print(" " + count);
                }
                if (count == skipRate) {
                    System.out.println(" ");
                }
            }
            //Check censoring
            boolean skip = lookedID.contains(haruhi.link);
            if (skip) continue;
            lookedID.add(haruhi.link);
            if (haruhi.title.length() <= 0 || haruhi.writer.equals(botName)) continue;
            if (countMode) {
                if (count < skipRate) {
                    continue;
                }
            }
            try {
                if (!test) connectTo(haruhi.link);
            } catch (UnhandledAlertException e) {
                System.out.println("Unhandled Alert. Close");
                driver.switchTo().alert().accept();
                connectTo(haruhi.link);
            } catch (TimeoutException e) {
                System.out.println("Timeout, Skip this page");
                driver.quit();
                initBrowser();
                if (!yudong) {
                    logIn();
                }
                instances = null;
                instances = new Stack();
                continue;
            } catch (Exception e) {
                continue;
            }

            double replyType = Math.random();
            String out;
            if (replyType <= 0.33) {
                out = makePreset();
                sendKey(out);
                System.out.println(haruhi.title + "\n -> " + out);
                count = 0;
            } else {
                out = makeSentence(haruhi);
                if (out != null) {
                    sendKey(out);
                    System.out.println(haruhi.title + "\n -> " + out);
                    count = 0;
                } else {
                    out = makePreset();
                    sendKey(out);
                    count = 0;
                }
            }
            long endtime = System.currentTimeMillis();
            long remainTime = endtime - starttime;
            if (remainTime > 5000) remainTime = 5000;
            remainTime += 256;
            System.out.println("{" + (remainTime / 1000) + "}_link_ id: " + haruhi.link);
            Thread.sleep(remainTime);
        }
    }

    public void connectTo(String URL) throws TimeoutException {
        System.out.println(" ");
        System.out.println("Selenium connected to " + URL);
        driver.get(URL);  //접속할 사이트
        writeName();
    }

    private String makePreset() {
        int rand = (int) (Math.random() * replyPreset.length);
        StringBuilder base = new StringBuilder(replyPreset[rand]);
        rand = (int) (Math.random() * 3);
        for (int i = 0; i < rand; i++) {
            base.append("ㅋ");
        }
        if (rand == 1) base.append("ㅋ");
        return base.toString();
    }

    @Override
    public void readSetting(String fileName) {
        try {
            BufferedReader sc = getReader(fileName);
            String temp = "";
            while (sc.ready()) {
                temp = sc.readLine();
                String[] token = temp.split(",");
                String head = token[0];
                String in = token[1];
                System.out.println(head + " = " + in);
                switch (head) {
                    case "gallID":
                        gallID = in;
                        break;
                    case "fileToRead":
                        fileToRead = in;
                        break;
                    case "botName":
                        botName = in;
                        break;
                    case "major":
                        major = Boolean.parseBoolean(in);
                        break;
                    case "seconds":
                        seconds = Integer.parseInt(in);
                        break;
                    case "yudong":
                        yudong = Boolean.parseBoolean(in);
                        break;
                    case "writePost":
                        writePost = Boolean.parseBoolean(in);
                        break;
                    case "skipRate":
                        skipRate = Integer.parseInt(in);
                        break;
                    case "botNameMode":
                        botNameMode = Integer.parseInt(in);
                        break;
                    case "useFireFox":
                        useFireFox = Boolean.parseBoolean(in);
                        break;
                    case "ngramCut":
                        ngramCut = Integer.parseInt(in);
                        break;
                    case "countMode":
                        countMode = Boolean.parseBoolean(in);
                        break;
                    case "word":
                        replyPreset[0] = in;
                        break;
                    default:
                        break;
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading " + fileName + " DONE!");
    }

    public void logIn() {
        try {
            driver.get("http://m.dcinside.com/auth/login?r_url=http://m.dcinside.com/");
            System.out.println("로그인 대기중");
            driver.findElement(By.id("user_id")).sendKeys("hmshood439");
            driver.findElement(By.id("user_pw")).sendKeys("gally886");
            Thread.sleep(500);
            driver.findElement(By.id("user_pw")).sendKeys(Keys.RETURN);
            //  driver.findElement(By.cssSelector("button[type=submit]")).click();
            Thread.sleep(1000);
            System.out.println("로그인 성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendKey(String text) {
        if (dontPost) return;
        try {
            System.out.println("Writing reply " + text);
            System.out.println("Finding key...");
            WebElement comment = driver.findElement(By.id("comment_memo"));
            comment.sendKeys(text);
            WebElement element = driver.findElement(By.cssSelector("button[class=btn-comment-write]"));
            WebDriverWait wait = new WebDriverWait(driver, 10);   // wait for 5 seconds
            wait.until(ExpectedConditions.elementToBeClickable(element));
            Actions actions = new Actions(driver);
            try {
                actions.moveToElement(element).perform();
                boolean success = true;
              //  do {
                    Thread.sleep(1000);
                    element = driver.findElement(By.cssSelector("button[class=btn-comment-write]"));
                    actions.moveToElement(element).perform();
                    JavascriptExecutor executor = (JavascriptExecutor)driver;
                    executor.executeScript("arguments[0].click();", element);
                    System.out.println("clicked");
               //     success = commentSuccessful(text);
             //   } while (!success);
            } catch (MoveTargetOutOfBoundsException e) {
                System.out.println("스크롤 200 ↓");
                System.out.println(e.getLocalizedMessage());
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,200)");
                element.click();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
            System.out.println("Clicked.");
        } catch (WebDriverException e) {
            e.printStackTrace();
        }
    }

    public boolean commentSuccessful(String myKey) {
        Document doc = Jsoup.parse(driver.getPageSource());
        Element commentBox = doc.select("ul[class=all-comment-lst]").first();
        if (commentBox == null) {
            System.out.println("글상자 없음");
            return false;
        }
        Elements comments = doc.select("li[class=comment]");
        for (Element cmt : comments) {
            Element txtBox = cmt.select("p").first();
            if (txtBox == null) continue;
            String txt = txtBox.text();
            System.out.println(txt);
            if (txt.contains(myKey)) return true;
        }
        System.out.println("커멘트 실패 확인");
        return false;
    }

    public void writeName() {
        if (yudong) {
            System.out.println("Write name called");
            String nn = getBotName();
            //   driver.findElement(By.cssSelector("input[placeholder=닉네임]")).sendKeys(nn);
            driver.findElement(By.id("comment_nick")).clear();
            driver.findElement(By.id("comment_pw")).clear();
            driver.findElement(By.id("comment_nick")).sendKeys(nn);
            driver.findElement(By.id("comment_pw")).sendKeys("fear");
            nameFilled = true;
        }
    }

    public void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "Nexus 5");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation);
        options.addExtensions(new File("extension_2_9_2_0.crx")); //AdGuard
        driver = new ChromeDriver(options);
//     driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정

    }


}
