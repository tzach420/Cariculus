package org.openjfx.dataBase;

import javafx.util.Pair;

import java.util.*;

public class myCourses {

    private static myCourses single_instance=null;
    private ArrayList<Course> courseList;
    private ArrayList<Set<Session>> allCombinations;
    private ArrayList<Set<Session>> filteredResults;
    private String lastAdded;
    private Map<String, Pair<Integer,Integer>> position;
    private double maxSize=4000000;
    private boolean flag=true;


    /**
     * implemented as single instance.
     * @return
     */
    public static myCourses getInstance()
    {
        if (single_instance == null)
            single_instance = new myCourses();
        return single_instance;
    }

    /**
     * constructor.
     */
    private myCourses(){
        courseList=new ArrayList<>();
        allCombinations=new ArrayList<>();
        position=new HashMap<>();
        filteredResults=new ArrayList<>();
        initMap("Sun",1);
        initMap("Mon",2);
        initMap("Tue",3);
        initMap("Wed",4);
        initMap("Thu",5);
        initMap("Fri",6);

    }


    /**
     * Check if a course exists in the database.
     * @param courseNumber- the course number.
     * @return true/false.
     */
    public boolean contains(String courseNumber){
        for(Course course: courseList){
            if(course.getCourseNumber().equals(courseNumber))
                return true;
        }
        return false;
    }

    public void setFilteredResults(ArrayList<Set<Session>> filteredResults) {
        this.filteredResults = filteredResults;
    }

    public ArrayList<Set<Session>> getFilteredResults() {
        return filteredResults;
    }

    public ArrayList<Set<Session>> getAllCombinations() {
        return allCombinations;
    }

    /**
     * Assign each cell on the display board a unique id.
     * @param day
     * @param colIndex
     */
    private void initMap(String day,int colIndex){
        position.put(day+" 08",new Pair(colIndex,1));
        position.put(day+" 09",new Pair(colIndex,2));
        position.put(day+" 10",new Pair(colIndex,3));
        position.put(day+" 11",new Pair(colIndex,4));
        position.put(day+" 12",new Pair(colIndex,5));
        position.put(day+" 13",new Pair(colIndex,6));
        position.put(day+" 14",new Pair(colIndex,7));
        position.put(day+" 15",new Pair(colIndex,8));
        position.put(day+" 16",new Pair(colIndex,9));
        position.put(day+" 17",new Pair(colIndex,10));
        position.put(day+" 18",new Pair(colIndex,11));
        position.put(day+" 19",new Pair(colIndex,12));
        position.put(day+" 20",new Pair(colIndex,13));
    }

    /**
     * saves the name of the last course that was added to the database.
     * @param s-course name.
     */
    public void setLastAdded(String s){lastAdded=s;}
    public String getLastAdded(){return lastAdded;}
    public void addCourse(Course toAdd){
        courseList.add(toAdd);
    }
    public void removeCourse(String toRemove){
        for(Course c:courseList){
            if(c.getName().equals(toRemove)){
                courseList.remove(c);
                break;
            }
        }
    }

    /**
     * Given a time returns the cell position on the display board that matches that time.
     * @param time
     * @return
     */
    public Pair<Integer,Integer> getSessionPosition(String time){
        return position.get(time);
    }

    /**
     * Generates all the possible curriculum options. Called when the user press the create button.
     */
    public void createCariculum(){
          Set<Session> s = new HashSet<>();
          allCombinations.clear();
          RunAlgo(0, s, createModel());
          filteredResults = allCombinations;//check later if need to adjust.
    }

    public ArrayList<Set<Session>> createModel(){//input for the algorithm.
        ArrayList<Set<Session>> model=new ArrayList<>();
        for(Course course: courseList){
            if(!course.getLectures().isEmpty())
                model.add(course.getLectures());
            if(!course.getExercises().isEmpty())
                model.add(course.getExercises());
            if(!course.getLabs().isEmpty())
                model.add(course.getLabs());
        }
        return model;
    }
    /**
     * This functions creates all the possible curriculums, and saves them in ArrayList "result"
     * @param i - the current index of the course on the ArrayList courses.
     * @param s - a possible curriculum.
     * @param courses - ArrayList of courses, each index is a course that has to be taken this semester.
     */
    public void RunAlgo(int i,Set<Session> s, ArrayList<Set<Session>> courses){
        System.out.println(i);
        if(i==courses.size()) {
            Set<Session> temp= copySet(s);
            if(allCombinations.size()>maxSize) flag=false;
            allCombinations.add(temp);
            System.out.println(allCombinations.size());
            return;
        }
            Set<Session> current = courses.get(i);
            for (Session x : current) {
                if (isGoodMatch(x, s)) {
                    s.add(x);
                    if(flag)
                        RunAlgo(i + 1, s, courses);
                    s.remove(x);
                }
            }

    }
    /**
     * This function copy set of sessions.
     * @param s - set to be copied.
     * @return copy of set s.
     */
    public  Set<Session> copySet (Set<Session> s){
        Set<Session> temp= new HashSet<>();
        for( Session x:s) {
            temp.add(x);
        }
        return temp;
    }
    /**
     * This function check if it's possible to add session x to curriculum s.
     * @param x- session to position.
     * @param s- possible curriculum.
     * @return true if possible, false otherwise.
     */
    public boolean isGoodMatch(Session x, Set<Session> s) {
        for(Session y:s) {
            Set<String> hours=sessionHours(y);
            for(String id: sessionHours(x))
                if(hours.contains(id)) return false;
        }
        return true;
    }
    private Set<String> sessionHours(Session s){
        Set<String> toReturn=new HashSet<>();
        for(String hour:s.getHours()){
            String day=hour.substring(0,3);
            int start=Integer.parseInt(hour.substring(4,6));
            int end=Integer.parseInt(hour.substring(12,14));
            while(start<end){
                toReturn.add(day+Integer.toString(start));
                start++;
            }
        }
        return toReturn;
    }

}
