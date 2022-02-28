package com.example.NCEPU.Utils;


public class Exam {
    private String xn;
    private String xq;
    private String course_code;
    private String course_name;
    private String class_name;  //教学班名称
    private String class_comp;  //教学班组成
    private String teacher; //老师
    private String school_time;   //上课时间
    private String class_place;  //上课地点
    private String exam_time;  //考试时间
    private String exam_place;  //考试地点
    private String major;  //专业
    private String campus;  //校区
    private String grade_class;  //年级

    public String getXn() {
        return xn;
    }

    public void setXn(String xn) {
        this.xn = xn;
    }

    public String getXq() {
        return xq;
    }

    public void setXq(String xq) {
        this.xq = xq;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_comp() {
        return class_comp;
    }

    public void setClass_comp(String class_comp) {
        this.class_comp = class_comp;
    }

    public String getSchool_time() {
        return school_time;
    }

    public void setSchool_time(String school_time) {
        this.school_time = school_time;
    }

    public String getClass_place() {
        return class_place;
    }

    public void setClass_place(String class_place) {
        this.class_place = class_place;
    }

    public String getExam_time() {
        return exam_time;
    }

    public void setExam_time(String exam_time) {
        this.exam_time = exam_time;
    }

    public String getExam_place() {
        return exam_place;
    }

    public void setExam_place(String exam_place) {
        this.exam_place = exam_place;
    }

    public String getTeacher() { return teacher; }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getGrade_class() {
        return grade_class;
    }

    public void setGrade_class(String grade_class) {
        this.grade_class = grade_class;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "xn='" + xn + '\'' +
                ", xq='" + xq + '\'' +
                ", course_code='" + course_code + '\'' +
                ", course_name='" + course_name + '\'' +
                ", class_name='" + class_name + '\'' +
                ", class_comp='" + class_comp + '\'' +
                ", teacher='" + teacher + '\'' +
                ", school_time='" + school_time + '\'' +
                ", class_place='" + class_place + '\'' +
                ", exam_time='" + exam_time + '\'' +
                ", exam_place='" + exam_place + '\'' +
                ", major='" + major + '\'' +
                ", campus='" + campus + '\'' +
                ", grade_class='" + grade_class + '\'' +
                '}';
    }
}

