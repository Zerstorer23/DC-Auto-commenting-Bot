package Markov.Bots;

import Markov.Objects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import static Markov.Decoder.getReader;
import static Markov.Objects.Replies.readReplies;
import static Markov.Scroller.*;

public class Bot_PostMachine extends Bot {
    public static long timeFirstReply=0;
    static long timeLastReply = 0;
    int repliesTillPost = 30;
    int count = 0;

    @Override
    public void readSetting(String fileName) {

    }

    public void stay() throws InterruptedException {
        readWiki();
        readReplies();
        boolean stat = true;
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); //크롬 드라이버 파일 경로설정
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //   Scanner sc = new Scanner(System.in);
        //    sc.next();
        if (!yudong) {
            logIn();
        }
        do {
            try {
                System.out.println("페이지 리로드");
                crawler.scrollMain();
                giveAnswer();
                // writePost_animeCensor();
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
       if(count == 0){
           timeFirstReply = System.currentTimeMillis();
       }
        while (!instances.empty()) {
            Page haruhi = instances.pop();
            //Check censoring
            boolean skip = false;
            boolean notDuplicated = checkFingerPrint(haruhi.id);
            if(haruhi.writer.equals(botName))skip = true;
    //        boolean botMentioned =   detectBotMention(haruhi.title);
            if (notDuplicated && !skip) {
                count++;
                if (count == repliesTillPost) {
                    timeLastReply=System.currentTimeMillis();
                    //Write a post
                    writePost(haruhi.title);
                    count = 0;
                }
                lookedID.add(haruhi.id);
            }
        }
        System.out.println("Count : "+count+" / "+repliesTillPost);
    }

    public void connectTo(String URL) {
        System.out.println("Selenium connected to " + URL);
        driver.get(URL);  //접속할 사이트
        if (yudong) {
            int len = (int) (Math.random() * 3) + 4;
            String nn = botName;
           /* if (foundNames.size() > 5) {
                int ind = (int) (Math.random() * foundNames.size());
                nn = foundNames.get(ind);
            }*/
            driver.findElement(By.id("name")).sendKeys(nn);
            driver.findElement(By.id("password")).sendKeys("fear");
        }
    }

    public String detectTricks(String writer) {
        double p = 1+(1/(1-Math.exp(writer.length())));
        return writer + "군이 동정인것에 "+p*100+"% 확신";
    }

    public boolean detectBotMention(String title) {
        if (title.contains("댓글봇")) return true;
        if (title.contains("매크로")) return true;
        if (title.contains("인공지능")) return true;
        if (title.contains("깡파고")) return true;
        if (title.contains("깡트론")) return true;
        if (title.contains("알파고")) return true;
        if (title.contains("봇")) return true;
        return false;
    }

    public String returnReply(String writer) {
        return Humans.getHuman(writer).getReply();
    }

    public void writePost(String title) {
        driver.get("http://gall.dcinside.com/board/write/?id="+gallID);  //접속할 사이트
        if (yudong) {
            int len = (int) (Math.random() * 3) + 4;
            String nn = botName;
            driver.findElement(By.id("name")).sendKeys(nn);
            driver.findElement(By.id("password")).sendKeys("fear");
        }

        WebElement header = driver.findElement(By.cssSelector("input[name=subject]"));
        header.sendKeys("현 시각 이 갤러리의 흥갤지수");

        String wisdom = getWikiWisdom();
        System.out.println("Sending keys to body: "+wisdom);


        WebElement bodyFrame = driver.findElement(By.cssSelector("iframe[name=tx_canvas_wysiwyg]"));
        driver.switchTo().frame(bodyFrame);
        WebElement body = driver.findElement(By.tagName("body"));
        String content = "이 갤러리의 흥갤 지수는 "+getMangMeter()+"% 입니다. \n";
        body.sendKeys(content);
        body.sendKeys("오늘의 토막 정보 \n");
        body.sendKeys(wisdom);
        body.sendKeys("\n 감사합니다. 아리가또");
        driver.switchTo().defaultContent();

        driver.findElement(By.cssSelector("input[type=image]")).sendKeys(Keys.RETURN);
    }
    public String getMangMeter(){
        long timeDiff = timeLastReply - timeFirstReply;
        double minutes = timeDiff/1000/60;
        //30초에 하나
        double standardExpected = repliesTillPost/2;
        //15분이 걸린다. 짧을수록 흥갤. 길수록 망갤
        double mX =(standardExpected-minutes)/standardExpected;
       double sigmoid =(2*Math.exp(mX)-0.009*minutes)/(Math.exp(mX)+1);
       timeFirstReply=0;
       timeLastReply=0;
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(sigmoid*100);
    }


    public String getWikiWisdom() {
        int rand = (int)(Math.random()*didyouKnow.size());
        return didyouKnow.get(rand);
    }

    public static void readWiki() {
        // Name + UserInfo + CVList+
        try {
            BufferedReader sc = null;
            sc = getReader("Didyouknow.txt");
            String temp = "";
            while ((temp = sc.readLine()) != null) {
                didyouKnow.add(temp);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading Wiki done = " + didyouKnow.size());

    }
}
