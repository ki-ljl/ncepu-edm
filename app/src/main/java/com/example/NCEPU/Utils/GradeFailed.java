/**
 * Date   : 2021/2/17 22:36
 * Author : KI
 * File   : GradeFailed
 * Desc   : GradeFailed
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.Utils;

import java.util.ArrayList;

public class GradeFailed {
    private String courseName;
    private String courseNature;
    private int num;
    private int totalNum;
    private ArrayList<Integer> data;

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseNature(String courseNature) {
        this.courseNature = courseNature;
    }

    public String getCourseNature() {
        return courseNature;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setData(ArrayList<Integer> data) {
        this.data = data;
    }

    public ArrayList<Integer> getData() {
        return data;
    }
}
