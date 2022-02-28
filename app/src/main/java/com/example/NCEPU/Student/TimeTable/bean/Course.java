package com.example.NCEPU.Student.TimeTable.bean;



import androidx.annotation.NonNull;

public class Course implements Cloneable,Comparable<Course>{
    private String name;//课程名
    private String teacher;//教授名字
    private int classLength =0;//课程时长
    private int classStart =-1;//课程开始节数
    private String classRoom;//上课地点
    private int weekOfTerm=-1;//开始上课的周,用二进制后25位表示是否为本周
    private int dayOfWeek =0;//在周几上课 值1-7

    public int getClassStart() {
        return classStart;
    }

    public void setClassStart(int classStart) {
        this.classStart = classStart;
    }

    public int getWeekOfTerm() {
        return weekOfTerm;
    }

    public void setWeekOfTerm(int weekOfTerm) {
        this.weekOfTerm = weekOfTerm;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public int getClassLength() {
        return classLength;
    }

    public void setClassLength(int classLength) {
        if(classLength <=0)
            classLength =1;
        else if(classLength >12)
            classLength =12;
        this.classLength = classLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int compareTo(Course course) {
        int i=this.getDayOfWeek()-course.getDayOfWeek();//首先比较星期
        if(i==0)//星期相同比较开始上课的时间
        {
            return this.getClassStart()-course.getClassStart();
        }else {
            return i;
        }
    }

}
