package com.example.NCEPU.Student.TimeTable.colleges.base;

import com.example.NCEPU.Student.TimeTable.colleges.CSUCollege;
import com.example.NCEPU.Student.TimeTable.colleges.ShmtuCollege;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollegeFactory {
    private static List<String> collegeNameList = new ArrayList<>();
    static {
        collegeNameList.add(CSUCollege.NAME);
        collegeNameList.add(ShmtuCollege.NAME);
        sortCollegeNameList();//升序排序
    }

    public static List<String> getCollegeNameList() {
        return collegeNameList;
    }
    private static void sortCollegeNameList(){
        Collections.sort(collegeNameList);
    }

    public static College createCollege(String collegeName) {
        if (collegeName == null) {
            return null;
        }
        else if (collegeName.equals(CSUCollege.NAME)) {
            return new CSUCollege();
        } else if (collegeName.equals(ShmtuCollege.NAME)) {
            return new ShmtuCollege();
        }
        else {
            return null;
        }
    }
}
