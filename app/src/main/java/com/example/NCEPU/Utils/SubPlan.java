/**
 * Date   : 2021/2/1 20:33
 * Author : KI
 * File   : SubPlan
 * Desc   : subplan
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.Utils;

public class SubPlan {
    private String course_num;
    private String course_name;
    private String course_nature;
    private String credit;   //学分
    private String year;
    private String semester;

    public String getCourse_num() {
        return course_num;
    }

    public void setCourse_num(String course_num) {
        this.course_num = course_num;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getCourse_nature() {
        return course_nature;
    }

    public void setCourse_nature(String course_nature) {
        this.course_nature = course_nature;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
