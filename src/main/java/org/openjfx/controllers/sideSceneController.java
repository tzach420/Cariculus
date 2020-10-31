package org.openjfx.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import org.openjfx.App;
import org.openjfx.dataBase.Session;
import org.openjfx.dataBase.myCourses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class sideSceneController {


    private int displayIndex=1;

    @FXML
    private GridPane pane;

    @FXML
    private AnchorPane loading;
    @FXML
    private AnchorPane errorMsg;

    @FXML
    private RadioButton sameGroup;

    @FXML
    private RadioButton noWindows;

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
    private Button createBtn;

    @FXML
    private Text resultsText;

    @FXML
    private VBox settings;


    public void init() throws IOException {
        displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex-1));
    }

    /**
     * Displays a possible cariculum to the user.
     * @param input
     * @throws IOException
     */
    public void displayResult(Set<Session> input) throws IOException {
        clearPane(pane);
        for(Session session: input) {
            for (String hour : session.getHours()) {
                int rowSpan=getSessionTime(hour);
                Pair<Integer,Integer> position=myCourses.getInstance().getSessionPosition(hour.substring(0,6));
                Node node = FXMLLoader.load(App.class.getResource("item.fxml"));
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

    /**
     * Given a session time returns how long it takes.
     * @param time (example- wed: 12:00-17:00)
     * @return session hours (3)
     */
    private int  getSessionTime(String time){ ;
        int start=Integer.parseInt(time.substring(4,6));
        int end=Integer.parseInt(time.substring(12,14));
        return end-start;
    }

    /**
     * Displays the previous curriculum option to the user.
     * @throws IOException
     */
    public void getPrevDisplay() throws IOException {
        displayIndex--;
        int numOfCariculums=myCourses.getInstance().getFilteredResults().size();
        if(displayIndex<1) displayIndex=numOfCariculums;
        resultsText.setText(displayIndex+"/"+myCourses.getInstance().getFilteredResults().size());
        displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex-1));
    }

    /**
     * Displays the next curriculum option to the user.
     * @throws IOException
     */
    public void getNextDisplay() throws IOException {
        displayIndex++;
        int numOfCariculums=myCourses.getInstance().getFilteredResults().size();
        if(displayIndex>numOfCariculums) displayIndex=1;
        resultsText.setText(displayIndex+"/"+myCourses.getInstance().getFilteredResults().size());
        displayResult(myCourses.getInstance().getFilteredResults().get(displayIndex-1));
    }

    /**
     * Clears the display board. Called when the user wishes to get next/prev option.
     * @param pane- the display board.
     */
    private void clearPane(GridPane pane){
        ObservableList<Node> toRemove=  FXCollections.observableArrayList();
        for(Node n:pane.getChildren()) {
            if (n instanceof VBox)
                toRemove.add(n);
        }
        for(Node n:toRemove)
            pane.getChildren().remove(n);
    }

    /**
     * Resets the display index. Called when user wishes to filter.
     */
    public void setDisplayIndex(){
        displayIndex=1;
        resultsText.setText(displayIndex+"/"+myCourses.getInstance().getFilteredResults().size());
    }

    /**
     * Displays the user filtered results.
     */
    public void filter(){
        App.sceneManager.getSideScene().setCursor(Cursor.WAIT); //Change cursor to hand
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
            App.sceneManager.getSideScene().setCursor(Cursor.DEFAULT);
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
        task.setOnFailed(e -> App.sceneManager.getSideScene().setCursor(Cursor.DEFAULT));
        new Thread(task).start();
    }

    public void filterResults() {
        ArrayList<Set<Session>> toShow = new ArrayList<>();
        for (Set<Session> curriculum : myCourses.getInstance().getAllCombinations()) {
            if (satisfyAllConditions(curriculum))
                toShow.add(curriculum);
        }
        myCourses.getInstance().setFilteredResults(toShow);

    }

    /**
     * Checks if a possible curriculum satisfy all the filter options.
     * @param session- a possible curriculum.
     * @return true/false.
     */
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

    /**
     * Get all the hours from a possible curriculum.
     * @param curriculum- a possilbe curriculum.
     * @return
     */
    public Set<String> getCourseHours(Set<Session> curriculum){
        Set<String> hours=new HashSet<>();
        for(Session session:curriculum){
            hours.addAll(session.getHours());
        }
        return hours;
    }

    /**
     * Checks if there are no courses on a specific day.
     * @param day- the day we want to check.
     * @param hours- all the hours of a possible curriculum.
     * @return true/false.
     */
    public boolean isDayOff(String day,Set<String> hours){
        for(String hour:hours){
            if(hour.contains(day)) return false;
        }
        return true;
    }

    /**
     * Checks if there are no windows on a possible curriculum.
     * @param sessionHours-  all the hours of a possible curriculum.
     * @return true/false.
     */
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
        Collections.sort(sortedList);
        for(int i=0;i<sortedList.size()-1;i++){
            if(sortedList.get(i+1)-sortedList.get(i)!=1) return true;
        }
        return false;
    }

    /**
     * Checks if all the courses and exercises of a possible curriculum  are from the same group
     * @param curriculum- a possible curriculum.
     * @return true/false.
     */
    public boolean allCoursesFromSameGroup(Set<Session> curriculum){
        for(Session session: curriculum){
            if(session.getType().equals("Exercise")||session.getType().equals("Laboratory")){
                String lectureId=session.getLectureId();
                String name=session.getName();
                for(Session s:curriculum){
                    if(s.getName().equals(name)&&s.getType().equals("Lecture"))
                        if(!s.getId().equals(lectureId)) return false;
                }
            }
        }
        return true;
    }

    /**
     * returns to the main screen. Called when the user press on the back arrow.
     */
    public void returnToMain(){
        Scene main=App.sceneManager.getMainScene();
        try {
            App.sceneManager.switchScene(main,0);
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
        }
    }
}

