import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.List;
public class Main  {
    public static void main(String[] args) throws Exception{
        //如果你的edge不是135.0.3179.98正式版，将edge更新至此版本
        //或自行下载对应版本的EdgeWebDriver替换msedgedriver.exe文件
        EdgeDriver driver;
        System.setProperty("webdriver.edge.driver", "src/main/resources/msedgedriver.exe");
        driver = new EdgeDriver();

//        如果使用的是Chrome，改为使用这三行
//        并自行下载与你版本对应的ChromeWebDriver放到resources目录，同时完善下面的路径"src/main/resources/"
//        ChromeDriver driver;
//        System.setProperty("webdriver.chrome.driver", "src/main/resources/");
//        driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get("https://passport2.chaoxing.com/login?");

        WebElement ph = driver.findElement(By.xpath("//*[@id=\"phone\"]"));
        //填入超星账号，例如ph.sendKeys("123123123");
        ph.sendKeys("");

        WebElement pa = driver.findElement(By.xpath("//*[@id=\"pwd\"]"));
        //填入超星密码，例如pa.sendKeys("123123123");
        pa.sendKeys("");
        WebElement logbt = driver.findElement(By.xpath("//*[@id=\"loginBtn\"]"));
        logbt.click();
        Thread.sleep(1000);

        //填入想要刷的课程的knowledge-all界面网址
        //形如driver.get("https://tsjy.chaoxing.com/plaza/knowledge-all?...")
        driver.get("");
        Thread.sleep(1000);
        driver.findElement(By.partialLinkText("全部")).click();
        Thread.sleep(1000);
        Double sum = 0.0;
        while (true) {
            WebElement knowledgeList = driver.findElement(By.id("knowledgeList"));
            List<WebElement> list = knowledgeList.findElements(By.className("list"));
            for (WebElement knowledge : list) {
                driver.executeScript("arguments[0].scrollIntoView();", knowledge);
                Thread.sleep(1000);
                knowledge.click();
                Thread.sleep(1000);
                Object[] windowHandles = driver.getWindowHandles().toArray();
                driver.switchTo().window((String) windowHandles[1]);
                driver.switchTo().frame("iframe");
                List<WebElement> fs = driver.findElements(By.cssSelector("#ext-gen1050 > iframe"));
                List<WebElement> states = driver.findElements(By.id("ext-gen1051"));
                int num = fs.size();
                driver.switchTo().defaultContent();
                for (int i = 0; i < num; i++,driver.switchTo().defaultContent()) {
                    //视频总播放超过850分钟后停止程序
                    if (sum > 850*60) {
                        driver.quit();
                        return;
                    }
                    driver.switchTo().frame("iframe");
                    if(states.get(i).getAttribute("aria-label").equals("任务点已完成")){
                        continue;
                    }
                    WebElement f=fs.get(i);
                    driver.switchTo().frame(f);
                    WebElement b = driver.findElement(By.cssSelector("#video > button"));
                    b.click();
                    Thread.sleep(1000);
                    WebElement v = driver.findElement(By.tagName("video"));
                    Object result = driver.executeScript("return arguments[0].duration;", v);
                    Double tt;
                    if (result instanceof Long) {
                        tt = ((Long) result).doubleValue();
                    } else {
                        tt = (Double) result;
                    }
                    result = driver.executeScript("return arguments[0].currentTime;", v);
                    Double ct;
                    if (result instanceof Long) {
                        ct = ((Long) result).doubleValue();
                    } else {
                        ct = (Double) result;
                    }
                    Thread.sleep((int) ((tt - ct) * 1000));
                    sum += tt - ct ;
                }
                driver.close();
                driver.switchTo().window((String) windowHandles[0]);
            }
            driver.findElement(By.linkText("下一页")).click();
            Thread.sleep(1000);
        }
    }
}
