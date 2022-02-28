package com.example.NCEPU.Utils;

import java.util.ArrayList;
import java.util.List;

public class CourseUtil {
    private String name;//课程名
    private String teacher;//教授名字
    private int classLength =0;//课程时长
    private int classStart =-1;//课程开始节数
    private String classRoom;//上课地点
    private List<Boolean> weekOfTerm = new ArrayList<>(25);//上课周数
    private int dayOfWeek =0;//在周几上课 值1-7


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getClassLength() {
        return this.classLength;
    }

    public void setClassLength(int classLength) {
        this.classLength = classLength;
    }

    public int getClassStart() {
        return this.classStart;
    }

    public void setClassStart(int classStart) {
        this.classStart = classStart;
    }

    public String getClassRoom() {
        return this.classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public List<Boolean> getWeekOfTerm() {
        return this.weekOfTerm;
    }

    public void setWeekOfTerm(List<Boolean> weekOfTerm) {
        for(int i = 0; i < 25; i++) {
            this.weekOfTerm.add(false);
        }
        for(int i = 0; i < weekOfTerm.size(); i++) {
            this.weekOfTerm.set(i, weekOfTerm.get(i));
        }
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


}
