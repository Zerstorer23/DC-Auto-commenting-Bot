package Markov.Bots;

import Markov.Objects.Page;
import org.openqa.selenium.*;

import java.awt.*;
import java.util.Stack;

import static Markov.Bots.Bot_Haruhi_scr.cutTooLong;
import static Markov.Decoder.namesBot;
import static Markov.Scroller.*;

public class Bot_ads extends Bot {
    public static long timeFirstReply = 0;
    public static boolean nameFilled = false;
    static long timeLastReply = 0;
    public String[] lines = {
            "그러게",
            "그러게 말이지",
            "그러게 말이야",
            "그러게?",
            "그런가?",
    };
    int repliesTillPost = 30;
    int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Bot_ads Lexington = new Bot_ads();
        gallID = "haruhiism";
        botName = "사라토가";
        //String [] names = {"故 아카기", "故 카가", "故 소류", "故 히류"};
        //  namesBot = names;

        //CRAWL options
        major = false; //메이져 갤러리
        yudong = true; //유동닉 사용 여부
        //1=use mine 2=use rand bot preset 3= steal user name

        Lexington.initBrowser();
        Lexington.writePost("테스트","내용을 입력하세요. 1 2 3 ");

    }

    public void stay() throws InterruptedException {
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
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (stat);

    }


    public void giveAnswer() throws AWTException, InterruptedException {
        if (count == 0) {
            timeFirstReply = System.currentTimeMillis();
        }
        while (!instances.empty()) {
            //      System.out.println("Instances size " + instances.size());
            Page haruhi = instances.pop();
            //Check censoring
            boolean skip = false;
            //TODO 설정 봇 디텍션
            if (haruhi.title.length() > 0) {
                if (haruhi.writer.equals(botName)) skip = true;
                if (!skip) {
                    try {
                        connectTo(haruhi.link);
                    } catch (UnhandledAlertException e) {
                        driver.switchTo().alert().accept();
                    } catch (TimeoutException e) {
                        System.out.println("Timeout, Skip this page");
                        driver.quit();
                        initFireFox();
                        instances = null;
                        instances = new Stack<>();
                        skip = true; // SKips all the tasks to be done on this webpage
                    } catch (NoSuchWindowException e) {
                        skip = true;
                    } catch (WebDriverException e) {
                        skip = true;
                    } catch (Exception e) {
                        skip = true;
                    }

                }
                if (!skip) {

                    String out;
                    int len = (int) (Math.random() * lines.length);
                    out = lines[len];
                    if (!nameFilled) writeName();
                    System.out.println("Writing... " + out);
                    sendKey(out);
                }
                count++;
                Thread.sleep(2000);
                lookedID.add(haruhi.id);
                System.out.println("Added to LookedID: " + haruhi.id);
            }
        }
    }

    public void connectTo(String URL) throws TimeoutException {
        System.out.println("Selenium connected to " + URL);
        nameFilled = false;
        driver.get(URL);  //접속할 사이트
        writeName();
    }

    @Override
    public void readSetting(String fileName) {

    }

    public void writeName() throws NoSuchElementException {
        if (yudong) {
            String nn;
            int len = (int) (Math.random() * namesBot.length);
            nn = namesBot[len];
            driver.findElement(By.id("name")).sendKeys(nn);
            driver.findElement(By.id("password")).sendKeys("fear");
            nameFilled = true;
        }

    }

    public void writePost(String title, String content) throws InterruptedException {
        String writeURL = "http://gall.dcinside.com/mgallery/board/write/?id=" + gallID;
        if (major) writeURL = "http://gall.dcinside.com/board/write/?id=" + gallID;
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ex) {

        }
        connectTo(writeURL);  //접속할 사이트
        if (yudong) {
            String nn = getBotName();
            driver.findElement(By.id("name")).sendKeys(nn);
            driver.findElement(By.id("password")).sendKeys("fear");
        }

        WebElement header = driver.findElement(By.cssSelector("input[name=subject]"));
        header.sendKeys(title);
        Thread.sleep(1000);
      //  driver.switchTo().defaultContent();
        driver.findElement(By.cssSelector("a[title='에디터 타입']")).click();
        Thread.sleep(1000);
       // WebElement bodyFrame = driver.findElement(By.cssSelector("iframe[name=tx_canvas_wysiwyg]"));
     //   driver.switchTo().frame(bodyFrame);
        WebElement body = driver.findElement(By.cssSelector("textarea[id=tx_canvas_source]"));
        System.out.println("Size: " + content.length());
        if (content.length() > 2000 && cutTooLong) {
            content = content.substring(0, 999);
        }
        body.sendKeys(content);
        body.sendKeys(Keys.TAB);
        Thread.sleep(100);
        body.sendKeys(Keys.TAB);
        Thread.sleep(100);
     //   body.sendKeys(Keys.RETURN);
        driver.switchTo().defaultContent();
        Thread.sleep(1000);
        WebElement submit =driver.findElement(By.cssSelector("button[class=btn_blue write]"));
      //  submit.click();
    //   submit.sendKeys(Keys.RETURN);
        Bot_replyLoader.setTargetTitle(title);
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            System.out.println(alert.getText() + " Alert is Displayed");
        } catch (NoAlertPresentException ex) {
            System.out.println("Alert is NOT Displayed");
        }
    }

}
