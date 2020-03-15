package dataBase;

import java.util.Set;

public class Session {
    Set<String> hours;
    String id;
    String name;
    String type;
    String building;//optional
    String lectureId;

    public String getLectureId() {
        return lectureId;
    }

    public Session(Set<String> hours, String id, String name, String type, String lectureId) {
        this.hours = hours;
        this.id = id;
        this.name = name;
        this.type=type;
        this.lectureId=lectureId;
    }

    public Set<String> getHours() {
        return hours;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBuilding() {
        return building;
    }

    public String getType() {
        return type;
    }
    public String toString(){
        String toRet="";
        toRet+="("+id+")"+name+"\n";
        return toRet;
    }
}
