package Markov.Bots;

import Markov.Objects.Page;
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

public class Bot_MobileLite extends Bot {
    public boolean countMode = false;
    int count = 0;

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
        boolean stat = true;
        if (!yudong) {
            logIn();
        }
        do {
            try {
                crawler.scrollMain();
                giveAnswer();
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (stat);

    }


    public void giveAnswer() throws InterruptedException {
        while (!instances.empty()) {
            Page haruhi = instances.pop();
            //Check censoring
            boolean skip = lookedID.contains(haruhi.link);
            //TODO 설정 봇 디텍션
            if (!skip && haruhi.title.length() > 0) {
                if (haruhi.writer.equals(botName)) skip = true;
                if (countMode) {
                    if (count <  skipRate) {
                        skip = true;
                    } else {
                        skip = false;
                    }
                }
                if (!skip) {
                    try {
                        if (!test) connectTo(haruhi.link);
                    } catch (UnhandledAlertException e) {
                        System.out.println("Unhandled Alert. Close");
                        driver.switchTo().alert().accept();
                        connectTo(haruhi.link);
                    } catch (TimeoutException e) {
                        System.out.println("Timeout, Skip this page");
                        driver.quit();
                        if (useFireFox) {
                            initFireFox();
                        } else {
                            initBrowser();
                        }
                        if (!yudong) {
                            logIn();
                        }
                        instances = null;
                        instances = new Stack();
                        skip = true; // SKips all the tasks to be done on this webpage
                        continue;
                    } catch (Exception e) {
                        skip = true;
                        continue;
                    }

                }
                String out = makeSentence(haruhi);
                if (out != null) {
                    sendKey(out);
                    System.out.println(haruhi.title + "\n -> " + out);
                    count = 0;
                } else {
                    System.out.println("Couldn't predict. Skip.");
                }
            }

        if (countMode) {
            count++;
            System.out.println("Count: " + count);
//TODO 5000 원래값
        }
        Thread.sleep(4500);
        lookedID.add(haruhi.link);
        System.out.println("link_ id: " + haruhi.link);
    }

}


    public boolean containArray(String[] pr, String wr) {
        for (int i = 0; i < pr.length; i++) {
            if (pr[i].equals(wr)) return true;
        }
        return false;
    }

    public void connectTo(String URL) throws TimeoutException {
        System.out.println(" ");
        System.out.println("Selenium connected to " + URL);
        driver.get(URL);  //접속할 사이트
        writeName();
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
                if (head.equals("gallID")) {
                    gallID = in;
                } else if (head.equals("fileToRead")) {
                    fileToRead = in;
                } else if (head.equals("botName")) {
                    botName = in;
                } else if (head.equals("major")) {
                    major = Boolean.parseBoolean(in);
                } else if (head.equals("seconds")) {
                    seconds = Integer.parseInt(in);
                } else if (head.equals("delay")) {
                    delay = Double.parseDouble(in);
                } else if (head.equals("yudong")) {
                    yudong = Boolean.parseBoolean(in);
                } else if (head.equals("writePost")) {
                    writePost = Boolean.parseBoolean(in);
                }  else if (head.equals("skipRate")) {
                    skipRate = Integer.parseInt(in);
                } else if (head.equals("botNameMode")) {
                    botNameMode = Integer.parseInt(in);
                } else if (head.equals("useFireFox")) {
                    useFireFox = Boolean.parseBoolean(in);
                } else if (head.equals("ngramCut")) {
                    ngramCut = Integer.parseInt(in);
                } else if (head.equals("countMode")) {
                    countMode = Boolean.parseBoolean(in);
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading " + fileName + " DONE!");
    }

    public String detectTricks() {
        return getWikiWisdom();
    }

    public boolean detectBotMention(String title) {
        if (title.contains("매크로")) return true;
        if (title.contains("인공지능")) return true;
        if (title.contains("알파고")) return true;
        if (title.contains("봇")) return true;
        if (title.contains("옴닉")) return true;
        return false;
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


    public void sendKey(String text) throws InterruptedException {
        if (dontPost) return;
        try {
            System.out.println("Writing reply " + text);
            System.out.println("Finding key...");
            WebElement comment = driver.findElement(By.id("comment_memo"));
            comment.sendKeys(text);
            Thread.sleep(500);
            WebElement element = driver.findElement(By.cssSelector("button[class=btn-comment-write]"));
            WebDriverWait wait = new WebDriverWait(driver, 5);   // wait for 5 seconds
            wait.until(ExpectedConditions.elementToBeClickable(element));
            Actions actions = new Actions(driver);
            try {
                actions.moveToElement(element).perform();
                element.click();
                //   actions.moveToElement(element).click().perform();
            } catch (MoveTargetOutOfBoundsException e) {
                System.out.println(e.getLocalizedMessage());
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,200)");
                element.click();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,500)");
                element.click();
            }
            System.out.println("Clicked.");
        } catch (WebDriverException e) {
            e.printStackTrace();
        }
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
