package dataBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Course {
    Set<Session> lectures;
    Set<Session> exercises;
    Set<Session> labs;
    String name;

//    public Course(Set<Session> lectures, Set<Session> exercises, Set<Session> labs,String name) {
//        this.lectures = lectures;
//        this.exercises = exercises;
//        this.labs = labs;
//        this.name=name;
//    }
    public Course(String name){
        lectures=new HashSet<>();
        exercises=new HashSet<>();
        labs=new HashSet<>();
        this.name=name;

    }
    public String getName(){return this.name;}
    public Set<Session> getLectures() {
        return lectures;
    }

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

