package dataBase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class fetchCourseHandler {

    private static fetchCourseHandler single_instance=null;
    private FirefoxDriver driver;

    public static fetchCourseHandler getInstance()
    {
        if (single_instance == null)
            single_instance = new fetchCourseHandler();
        return single_instance;
    }
    private fetchCourseHandler() {
        System.setProperty("webdriver.gecko.driver",  System.getProperty("user.dir")+"/lib/geckodriver");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        driver = new FirefoxDriver(options);
        driver.get("https://bgu4u.bgu.ac.il/pls/scwp/!app.gate?app=ann");
        driver.switchTo().frame(0);
        WebElement frame=driver.findElementByXPath("/html/body/center/d1iv/table[2]/tbody/tr/td/table/tbody/tr[2]/td[2]/div/a[2]");
        //driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS) ;
        frame.click();
    }


    public void addCourse(String courseNumber) {
        WebElement courseName = driver.findElementByXPath("//*[@id=\"oc_course_name\"]");
        courseName.clear();
        courseName.sendKeys("' '");
        WebElement courseNumb = driver.findElementByXPath("//*[@id=\"on_course\"]");
        courseNumb.clear();
        String[] digits = courseNumber.split("\\.");
        courseNumb.sendKeys(digits[2]);
        driver.findElementByXPath("//*[@id=\"GOPAGE2\"]").click();//click search.
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS) ;

        WebElement t=driver.findElementByXPath("//td[contains(text(),'"+courseNumber+"')]");
        WebElement row=t.findElement(By.xpath(".."));
        row.findElement(By.tagName("a")).click();
        Select language=new Select(driver.findElementById("languagesSelect"));
        language.selectByValue("en");

        ///get the course info: each row is a seesion. each table is a course.
        String name=driver.findElementByXPath("/html/body/center/d1iv/table[2]/tbody/tr/td/table/tbody/tr[2]/td[2]/ul/li[2]/p[2]").getText();
        WebElement courseTable=driver.findElementByXPath("/html/body/center/d1iv/table[2]/tbody/tr/td/table/tbody/tr[2]/td[2]/table[1]");
        List<WebElement> tableRows = courseTable.findElements(By.tagName("tr"));
        Course course=new Course(name);
        String lectureId = "";
        for(WebElement courseInfo:tableRows) {
            List<WebElement> cells = courseInfo.findElements(By.tagName("td"));
            try {
                if (cells.get(1).getText().equals("Lecture"))
                    lectureId = cells.get(0).getText();
                Session session = CreateSessionFromCourseTable(cells, name, lectureId);
                if (session != null) {
                    if (session.getType().equals("Lecture")) course.addLecture(session);
                    else if (session.getType().equals("Exercise")) course.addExercise(session);
                    else if (session.getType().equals("Laboratory")) course.addLab(session);
                }
            }catch(Exception e){continue;}
        }
        myCourses.getInstance().addCourse(course);
        myCourses.getInstance().setLastAdded(name);
        returnToMainScreen();

    }

    private static Session CreateSessionFromCourseTable(List<WebElement> cells,String courseName,String lectureId) {
        String id=cells.get(0).getText();
        String type=cells.get(1).getText();
        Set<String> hours=new HashSet<>();
        String courseTime=cells.get(3).getText();
        if(courseTime.equals("")) return null;
        String[] temp=courseTime.split("\n");
        for(String s: temp){
            hours.add(s);
        }
        return new Session(hours,id,courseName,type,lectureId);
    }
    private void returnToMainScreen(){
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
    }
}
