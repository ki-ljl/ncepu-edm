package com.example.NCEPU.Utils;

import com.example.NCEPU.MainActivity;
import com.example.NCEPU.Student.TimeTable.bean.Course;
import com.example.NCEPU.Student.TimeTable.ui.editcourse.WeekOfTermSelectDialog;

import java.util.ArrayList;

public class TimeTableUtil {

    public static ArrayList<CourseUtil> getList(String year, int semester) throws Exception {
        //找到学年学期
        ArrayList<CourseUtil> list = MainActivity.connectJWGL.getStudentTimetable(year, semester);
        return list;
    }

    public static ArrayList<CourseUtil> getListStart() throws Exception {
        //找到学年学期
        ArrayList<CourseUtil> list = MainActivity.connectJWGL.getStudentTimetableInit();
        return list;
    }

    public static ArrayList<Course> getCourse(String year, int semester) throws Exception {
        ArrayList<Course> realList = new ArrayList<>();
        ArrayList<CourseUtil> list = getList(year, semester);
        for(int i = 0; i < list.size(); i++) {
            Course course = new Course();
            course.setClassLength(list.get(i).getClassLength());
            course.setClassRoom(list.get(i).getClassRoom());
            course.setClassStart(list.get(i).getClassStart());
            course.setDayOfWeek(list.get(i).getDayOfWeek());
            course.setName(list.get(i).getName());
            course.setTeacher(list.get(i).getTeacher());
            int weekOfTerm = WeekOfTermSelectDialog.getWeekOfTerm_(list.get(i).getWeekOfTerm());
            course.setWeekOfTerm(weekOfTerm);
            realList.add(course);
        }
        return realList;
    }


    public static ArrayList<Course> getCourseStart() throws Exception {
        ArrayList<Course> realList = new ArrayList<>();
        ArrayList<CourseUtil> list = getListStart();
        for(int i = 0; i < list.size(); i++) {
            Course course = new Course();
            course.setClassLength(list.get(i).getClassLength());
            course.setClassRoom(list.get(i).getClassRoom());
            course.setClassStart(list.get(i).getClassStart());
            course.setDayOfWeek(list.get(i).getDayOfWeek());
            course.setName(list.get(i).getName());
            course.setTeacher(list.get(i).getTeacher());
            int weekOfTerm = WeekOfTermSelectDialog.getWeekOfTerm_(list.get(i).getWeekOfTerm());
            course.setWeekOfTerm(weekOfTerm);
            realList.add(course);
        }
        return realList;
    }
}
