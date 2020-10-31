package org.openjfx.dataBase;

import java.util.HashSet;
import java.util.Set;

public class Course {
    Set<Session> lectures;
    Set<Session> exercises;
    Set<Session> labs;
    String name;
    String courseNumber;


    public Course(String name,String courseNumber){
        lectures=new HashSet<>();
        exercises=new HashSet<>();
        labs=new HashSet<>();
        this.name=name;
        this.courseNumber=courseNumber;

    }
    public String getName(){return this.name;}
    public Set<Session> getLectures() {
        return lectures;
    }
    public String getCourseNumber(){return this.courseNumber;}
    public Set<Session> getExercises() {
        return exercises;
    }

    public Set<Session> getLabs() {
        return labs;
    }
    public void addLecture(Session s){
        lectures.add(s);
    }
    public void addExercise(Session s){
        exercises.add(s);
    }
    public void addLab(Session s){
        labs.add(s);
    }
}

