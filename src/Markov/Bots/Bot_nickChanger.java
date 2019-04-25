package Markov.Bots;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Bot_nickChanger extends Bot {
    //"haruhiism";
    public boolean isReady = false;
    private WebDriver subDriver;
    public static Bot Lexington = new Bot_nickChanger();
    public static String currName = "사라토가";

    public static void main(String[] args) throws InterruptedException {
        Lexington.stay();
        ((Bot_nickChanger) Lexington).changeName("테스트");
    }

    @Override
    public void readSetting(String fileName) {

    }

    @Override
    public void logIn() throws InterruptedException {
        subDriver.get("https://dcid.dcinside.com/join/login.php?s_url=http%3A%2F%2Fgall.dcinside.com%2Fmgallery%2Fboard%2Flists%2F%3Fid%3Dblhx");
        System.out.println("닉네임 드라이버 로그인 대기중");
        subDriver.findElement(By.id("id")).sendKeys("hmshood439");
        subDriver.findElement(By.id("pw")).sendKeys("gally886");
        Thread.sleep(1000);
        subDriver.findElement(By.id("pw")).sendKeys(Keys.RETURN);
        Thread.sleep(500);
        System.out.println("로그인 성공");
    }

    public void stay() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); //크롬 드라이버 파일 경로설정

        ChromeOptions options = new ChromeOptions();
        // options.addExtensions(new File("extension_6_2_5_0.crx"));
        // options.addExtensions(new File("extension_2_9_2_0.crx")); //AdGuard
        subDriver = new ChromeDriver(options);
        subDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        logIn();
        System.out.println("로그인 성공");
        subDriver.get("http://gallog.dcinside.com/hmshood439");
        System.out.println("연결 완료");
        isReady = true;
    }

    public void giveAnswer() throws AWTException, InterruptedException {


    }

    @Override
    public void initBrowser() {
        System.out.println("Initiating Chrome Driver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:/Chrome Driver/User Data");
        //   options.addArguments("--start-maximized");
        //options.addArguments("profile-directory=Profile 2");
        //  driver=null;
        //  options.addExtensions(new File("extension_6_2_5_0.crx"));
        // options.addExtensions(new File("extension_2_9_2_0.crx")); //AdGuard

        subDriver = new ChromeDriver(options);
//     driver = new ChromeDriver(capabilities);
        subDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //응답시간 5초설정
        //    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); //응답시간 5초설정
    }

    public void connectTo(String URL) {
        try {
            System.out.println("Selenium connected to " + URL);
            subDriver.get(URL);  //접속할 사이트
        } catch (UnhandledAlertException e) {
            subDriver.switchTo().alert().accept();
            System.out.println("Selenium RE connected to " + URL);
            subDriver.get(URL);  //접속할 사이트
        }
    }

    public void changeName(String name) throws NoSuchElementException, InterruptedException {
        WebElement openNick = subDriver.findElement(By.xpath("//img[@src='http://wstatic.dcinside.com/gallery/skin/gallog/total.gif']"));
        openNick.click();
        Thread.sleep(1000);
        subDriver.findElement(By.id("txtNickName")).clear();
        subDriver.findElement(By.id("txtNickName")).sendKeys(name);
        WebElement okay = subDriver.findElement(By.xpath("//img[@src='http://wstatic.dcinside.com/gallery/skin/gallog/edit-01.gif']"));
        okay.click();
        currName = name;
        System.out.println("닉네임 변경됨 : " + name);
    }

}
