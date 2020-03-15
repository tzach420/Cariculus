package Controllers;

import Fxmls.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import dataBase.Session;
import dataBase.myCourses;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class sideSceneController {

    private int displayIndex=1;
    @FXML
    private AnchorPane cariculum;
    @FXML
    private AnchorPane calenderSettings;
    @FXML
    private GridPane pane;

    @FXML
    private AnchorPane loading;
    @FXML
    private AnchorPane errorMsg;

    @FXML
    private JFXToggleButton sameGroup;

    @FXML
    private JFXToggleButton noWindows;

    @FXML
    private RadioButton sunday;

    @FXML
    private RadioButton monday;

    @FXML
    private RadioButton tuesday;

    @FXML
    private RadioButton wednesday;

    @FXML
    private RadioButton thursday;

    @FXML
    private RadioButton friday;

    @FXML
    private JFXButton createBtn;

    @FXML
    private JFXButton forwardBtn;

    @FXML
    private JFXButton prevBtn;

    @FXML
    private Text resultsText;

    @FXML
    private JFXButton filterBtn;
    @FXML
    private JFXButton errorMsgBtn;
    @FXML
    private ImageView returnBtn;
    @FXML
    private VBox settings;


    public void init() throws IOException {
        displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex--));
    }
    public void displayResult(Set<Session> input) throws IOException {
        clearPane(pane);
        for(Session session: input) {
            for (String hour : session.getHours()) {
                int rowSpan=getSessionTime(hour);
                Pair<Integer,Integer> position=myCourses.getInstance().getSessionPosition(hour.substring(0,6));
                Node node = FXMLLoader.load(getClass().getResource("../Fxmls/Course.fxml"));
                TextFlow t=(TextFlow) ((VBox)node).getChildren().get(0);
                Text text=new Text(session.toString());
                text.setStyle("-fx-font: 10 arial;");
                t.getChildren().add(text);
                if(session.getType().equals("Lecture"))
                    ((VBox)node).setStyle("-fx-background-color: #90EE90");
                else if(session.getType().equals("Laboratory"))
                    ((VBox)node).setStyle("-fx-background-color: #8B008B");
                pane.add(node, position.getKey(), position.getValue(), 1, rowSpan);
            }
        }

    }
    private int  getSessionTime(String time){ ;
        int start=Integer.parseInt(time.substring(4,6));
        int end=Integer.parseInt(time.substring(12,14));
        return end-start;
    }
    public void getPrevDisplay() throws IOException {
        displayIndex--;
        int numOfCariculums=myCourses.getInstance().getFilteredResults().size();
        if(displayIndex<1) displayIndex=numOfCariculums;
        resultsText.setText(displayIndex+"/"+myCourses.getInstance().getFilteredResults().size());
        displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex-1));
    }
    public void getNextDisplay() throws IOException {
        displayIndex++;
        int numOfCariculums=myCourses.getInstance().getFilteredResults().size();
        if(displayIndex>numOfCariculums) displayIndex=1;
        resultsText.setText(displayIndex+"/"+myCourses.getInstance().getFilteredResults().size());
        displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex-1));
    }
    private void clearPane(GridPane pane){
        ObservableList<Node> toRemove=  FXCollections.observableArrayList();
        for(Node n:pane.getChildren()) {
            if (n instanceof VBox)
                toRemove.add(n);
        }
        for(Node n:toRemove)
            pane.getChildren().remove(n);
        }
    public void setDisplayIndex(){
        displayIndex=1;
        resultsText.setText(displayIndex+"/"+myCourses.getInstance().getFilteredResults().size());
    }
    public void filter(){
        Main.sceneManager.getSideScene().setCursor(Cursor.WAIT); //Change cursor to hand
        loading.setVisible(true);
        Task task = new Task() {
            @Override
            public Void call() {
                filterResults();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            loading.setVisible(false);
            Main.sceneManager.getSideScene().setCursor(Cursor.DEFAULT);
            if(!myCourses.getInstance().getFilteredResults().isEmpty()){
                setDisplayIndex();
                try {
                    displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex-1));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else{
                errorMsg.setVisible(true);
                errorMsg.toFront();
                myCourses.getInstance().setFilteredResults(myCourses.getInstance().getAllCombinations());
            }

        });
        task.setOnFailed(e -> Main.sceneManager.getSideScene().setCursor(Cursor.DEFAULT));
        new Thread(task).start();
    }
    public void filterResults() {
        ArrayList<Set<Session>> toShow = new ArrayList<>();
        for (Set<Session> cariculum : myCourses.getInstance().getAllCombinations()) {
            if (satisfyAllConditions(cariculum))
                toShow.add(cariculum);
        }
        myCourses.getInstance().setFilteredResults(toShow);

    }


    public boolean satisfyAllConditions(Set<Session> session) {
        Set<String> sessionHours=getCourseHours(session);
        if(sunday.isSelected())
            if(!isDayOff("Sun",sessionHours)) return false;
        if(monday.isSelected())
            if(!isDayOff("Mon",sessionHours)) return false;
        if(tuesday.isSelected())
            if(!isDayOff("Tue",sessionHours)) return false;
        if(wednesday.isSelected())
            if(!isDayOff("Wed",sessionHours)) return false;
        if(thursday.isSelected())
            if(!isDayOff("Thu",sessionHours)) return false;
        if(friday.isSelected())
            if(!isDayOff("Fri",sessionHours)) return false;
        if(noWindows.isSelected())
            if(hasWindows(sessionHours)) return false;
        if(sameGroup.isSelected())
            if(!allCoursesFromSameGroup(session)) return false;
         return true;
    }
    public Set<String> getCourseHours(Set<Session> cariculum){
        Set<String> hours=new HashSet<>();
        for(Session session:cariculum){
            hours.addAll(session.getHours());
        }
        return hours;
    }
    public boolean isDayOff(String day,Set<String> hours){
        for(String hour:hours){
            if(hour.contains(day)) return false;
        }
        return true;
    }
    public boolean hasWindows(Set<String> sessionHours){
       if(hasWindowsOn("Sun",sessionHours)||hasWindowsOn("Mon",sessionHours)||hasWindowsOn("Tue",sessionHours)||
               hasWindowsOn("Wed",sessionHours)|| hasWindowsOn("Thu",sessionHours)|| hasWindowsOn("Fri",sessionHours))
           return true;
       return false;
    }

    private boolean hasWindowsOn(String day,Set<String> cariculum){
        Set<String> hours=new HashSet<>();
        ArrayList<Integer> sortedList=new ArrayList<>();
        for(String s:cariculum){
            if(s.contains(day))
                hours.add(s);
        }
        for(String s:hours){
            int start=Integer.parseInt(s.substring(4,6));
            int end=Integer.parseInt(s.substring(12,14));
            while(start<end){
                sortedList.add(start);
                start++;
            }
        }
        //List<Integer> newList = sortedList.stream().distinct().collect(Collectors.toList());//remove duplicates.
        Collections.sort(sortedList);
        for(int i=0;i<sortedList.size()-1;i++){
            if(sortedList.get(i+1)-sortedList.get(i)!=1) return true;
        }
        return false;
    }
    public boolean allCoursesFromSameGroup(Set<Session> cariculum){
        for(Session session: cariculum){
            if(session.getType().equals("Exercise")||session.getType().equals("Laboratory")){
                String lectureId=session.getLectureId();
                String name=session.getName();
                for(Session s:cariculum){
                    if(s.getName().equals(name)&&s.getType().equals("Lecture"))
                        if(!s.getId().equals(lectureId)) return false;
                }
            }
        }
        return true;
    }
    public void returnToMain(){
        Scene main=Main.sceneManager.getMainScene();
        try {
            Main.sceneManager.switchScene(main,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeErrorMsg(){
        errorMsg.setVisible(false);
        resetButtons();
    }
    private void resetButtons(){
        for(Node btn:settings.getChildren()){
            if(btn instanceof RadioButton)
                ((RadioButton)btn).setSelected(false);
            else if(btn instanceof JFXToggleButton)
                ((JFXToggleButton)btn).setSelected(false);
        }
    }
}


