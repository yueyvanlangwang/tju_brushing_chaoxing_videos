import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.List;
public class Main {
    public static void main(String[] args) throws Exception{
        EdgeDriver driver;
        System.setProperty("webdriver.edge.driver", "src/main/resources/msedgedriver.exe");
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        driver.get("https://passport2.chaoxing.com/login?");

        WebElement ph = driver.findElement(By.xpath("//*[@id=\"phone\"]"));
        //填入超星账号，例如ph.sendKeys("123123123");
        ph.sendKeys("18947617481");

        WebElement pa = driver.findElement(By.xpath("//*[@id=\"pwd\"]"));
        //填入超星密码，例如pa.sendKeys("123123123");
        pa.sendKeys("aw/\\154'q");
        WebElement logbt = driver.findElement(By.xpath("//*[@id=\"loginBtn\"]"));
        logbt.click();
        Thread.sleep(1000);

        //填入想要刷的课程的knowledge-all界面网址
        //形如driver.get("https://tsjy.chaoxing.com/plaza/knowledge-all?...")
        driver.get("https://tsjy.chaoxing.com/plaza/knowledge-all?classifyId=987539191&courseId=252557203&personId=273799278&classId=121303602&userId=239760581");
//        driver.get("");
        Thread.sleep(1000);
        driver.findElement(By.partialLinkText("全部")).click();
        Thread.sleep(1000);
        Double sum = 0.0;
        while (true) {
            //视频总播放超过850分钟后停止程序
            if (sum > 850) {
                break;
            }
            WebElement knowledgeList = driver.findElement(By.id("knowledgeList"));
            List<WebElement> list = knowledgeList.findElements(By.className("list"));
            for (WebElement knowledge : list) {
                knowledge.click();
                Thread.sleep(1000);
                Object[] windowHandles = driver.getWindowHandles().toArray();
                driver.switchTo().window((String) windowHandles[1]);
                driver.switchTo().frame("iframe");
                List<WebElement> fs = driver.findElements(By.cssSelector("#ext-gen1050 > iframe"));
                List<WebElement> states = driver.findElements(By.id("ext-gen1051"));
                int num = fs.size();
                System.out.println(num+"  "+states.size());
                driver.switchTo().defaultContent();
                for (int i = 0; i < num; i++,driver.switchTo().defaultContent()) {
                    driver.switchTo().frame("iframe");
                    System.out.println(states.get(i).getAttribute("aria-label"));
                    if(states.get(i).getAttribute("aria-label").equals("任务点已完成")){
                        continue;
                    }
                    WebElement f=fs.get(i);
                    driver.switchTo().frame(f);
                    WebElement b = driver.findElement(By.cssSelector("#video > button"));
                    b.click();
                    Thread.sleep(1000);
                    WebElement v = driver.findElement(By.tagName("video"));
                    System.out.println(v);
                    Object result = driver.executeScript("return arguments[0].duration;", v);
                    Double tt;
                    if (result instanceof Long) {
                        tt = ((Long) result).doubleValue();
                    } else {
                        tt = (Double) result;
                    }
                    System.out.println(tt);
                    result = driver.executeScript("return arguments[0].currentTime;", v);
                    Double ct;
                    if (result instanceof Long) {
                        ct = ((Long) result).doubleValue();
                    } else {
                        ct = (Double) result;
                    }
                    System.out.println(ct);
                    Thread.sleep((int) ((tt - ct) * 1000));
                    sum += tt - ct;
                }
                driver.close();
                driver.switchTo().window((String) windowHandles[0]);
            }
            driver.findElement(By.linkText("下一页")).click();
            Thread.sleep(1000);
        }
        driver.quit();
    }
}
