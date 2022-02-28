package com.example.NCEPU.Student.TimeTable.bean;

import androidx.annotation.NonNull;

public class Time implements Cloneable{
    private String start="";
    private String end="";

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @NonNull
    @Override
    public String toString() {

        return start.isEmpty()?"":start+" - "+end;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
