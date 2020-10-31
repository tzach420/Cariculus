package org.openjfx;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openjfx.dataBase.Course;
import org.openjfx.dataBase.Session;
import org.openjfx.dataBase.myCourses;
import org.openqa.selenium.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class fetchCourseHandler {

    private static fetchCourseHandler single_instance=null;
    private WebDriver driver;

    /**
     * implemented as single instance.
     * @return a single instance of the class.
     */
    public static fetchCourseHandler getInstance()
    {
        if (single_instance == null)
            single_instance = new fetchCourseHandler();
        return single_instance;
    }
    private fetchCourseHandler() {
        try {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            options.setHeadless(true);
            driver = new FirefoxDriver(options);
        }catch(Exception e){

            System.out.println("Please install firefox.");
        }
        driver.get("https://bgu4u.bgu.ac.il/pls/scwp/!app.gate?app=ann");
        driver.switchTo().frame(0);
        WebElement frame=driver.findElement(By.xpath("/html/body/center/d1iv/table[2]/tbody/tr/td/table/tbody/tr[2]/td[2]/div/a[2]"));
        frame.click();
    }

    /**
     * Fetching the course details from the university database and add it to the course list.
     * @param courseNumber
     */
    public void addCourse(String courseNumber) {
        WebElement courseName = driver.findElement(By.xpath("//*[@id=\"oc_course_name\"]"));
        courseName.clear();
        courseName.sendKeys("' '");
        WebElement courseNumb = driver.findElement(By.xpath("//*[@id=\"on_course\"]"));
        courseNumb.clear();
        String[] digits = courseNumber.split("\\.");
        courseNumb.sendKeys(digits[2]);
        driver.findElement(By.xpath("//*[@id=\"GOPAGE2\"]")).click();//click search.
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS) ;

        WebElement t=driver.findElement(By.xpath("//td[contains(text(),'"+courseNumber+"')]"));
        WebElement row=t.findElement(By.xpath(".."));
        row.findElement(By.tagName("a")).click();
        Select language=new Select(driver.findElement(By.id("languagesSelect")));
        language.selectByValue("en");

        ///get the course info: each row is a seesion. each table is a course.
        String name=driver.findElement(By.xpath("/html/body/center/d1iv/table[2]/tbody/tr/td/table/tbody/tr[2]/td[2]/ul/li[2]/p[2]")).getText();
        WebElement courseTable=driver.findElement(By.xpath("/html/body/center/d1iv/table[2]/tbody/tr/td/table/tbody/tr[2]/td[2]/table[1]"));
        List<WebElement> tableRows = courseTable.findElements(By.tagName("tr"));
        Course course=new Course(name,courseNumber);
        String lectureId = "";
        for(int i=1; i<tableRows.size();i++) {
            WebElement courseInfo=tableRows.get(i);
            List<WebElement> cells = courseInfo.findElements(By.tagName("td"));
            if(i!=tableRows.size()-1 && courseIsSplit(tableRows,i+1)) {
                i++;
                WebElement toAdd=tableRows.get(i).findElement(By.tagName("td"));
                System.out.println(toAdd.getAttribute("class"));
                cells.add(cells.size(),toAdd );
                i++;
            }
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

    /**
     * Creates a session corresponding to the course details on the webpage.
     * @param cells
     * @param courseName
     * @param lectureId
     * @return
     */
    private static Session CreateSessionFromCourseTable(List<WebElement> cells,String courseName,String lectureId) { //FIXME - THE PROBLEM IS HERE. NEED TO RE-THINK HOW TO GENERATE THE HOURS, THEY CHANGED THE WEB-FORMAT.
        String id=cells.get(0).getText();
        String type=cells.get(1).getText();
        Set<String> hours=new HashSet<>();
        String courseTime=cells.get(3).getText();
        if(cells.size()>5) courseTime=courseTime+"\n"+cells.get(cells.size()-1).getText();
        if(courseTime.equals("")) return null;
        String[] temp=courseTime.split("\n");
        for(String s: temp){
            if(s.contains("Hours"))
                hours.add(s.substring(7));
        }
        return new Session(hours,id,courseName,type,lectureId);
    }

    /**
     * Returns to the main screen in the browser for further searches.
     */
    private void returnToMainScreen(){
        driver.navigate().back();
        driver.navigate().back();
        driver.navigate().back();
    }

    /**
     * Some courses have multiple line representation on the web. This function help to detect it.
     * @param list
     * @param i
     * @return
     */
    private boolean courseIsSplit(List<WebElement> list,int i){
        WebElement row=list.get(i);
        List<WebElement> cols = row.findElements(By.tagName("td"));
        if(cols.size()==1)
            try{
                cols.get(0).findElement(By.tagName("div"));
            }catch(Exception e){return true;}
        return false;
    }
}
