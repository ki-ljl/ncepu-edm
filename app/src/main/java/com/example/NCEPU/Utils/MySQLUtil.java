package com.example.NCEPU.Utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MySQLUtil {
    Context mContext;
    Connection connection = null;
    public MySQLUtil(Context context) {
        mContext = context;
    }

    //判断编码方式
    public String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    public Connection getConnection(String name) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://cdb-qpcioxsd.gz.tencentcdb.com:10171/" + name + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, "root", "*****");
            if(!connection.isClosed()) {
                System.out.println("连接成功");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //获取课程名字，方便查询
    public ArrayList<String> getCourseName(String tableName) {
        ArrayList<String> course = new ArrayList<>();
        String sql = "select CourseName from " + tableName;
        System.out.println(sql);
        try {
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(sql);
            while(res.next()) {
                String string = res.getString("CourseName");
                if(!course.contains(string)) {
                    course.add(string);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return course;
    }

//    public void getMajorSort(String year, String semester, String nature, String major) {
//        String sqlString = "select Sno, Mark, Gpa from final_exam_info where Major = " + major +
//                "Years = " + year + "Semester = " + semester;
//    }
    //根据课程名称以及专业id获取排名
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getRank(String courseString, String majorString, String stu_num, boolean flag) {
        String sqlString = "SELECT Sno, Mark FROM final_exam_info"
                + " WHERE CourseName='"+courseString+"' and Sno in (SELECT Sno FROM stu_info WHERE Major='"+majorString+"')";
        float score = (float) 120;
        //String sqlString = new String(sqlStrings.getBytes("GB2312"), "UTF-8");
        System.out.println(sqlString);
        ArrayList<Float> scores = new ArrayList<>();
        ArrayList<Integer> ranks = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sqlString);
            while(set.next()) {
                String sno = set.getString(1);
                String temp = set.getString(2);
                System.out.println("hh" + sno + " " + temp);
                scores.add(Float.parseFloat(temp));
                //sno = new String(sno.getBytes("ISO-8859-1"),"UTF-8");
                //stu_num = new String(stu_num.getBytes("ISO-8859-1"),"UTF-8");
                if(stu_num.equals(sno.trim())) {
                    System.out.println("jjj");
                    score = Float.parseFloat(temp);
                }
            }
            System.out.println(scores);
            //排序
            Collections.sort(scores, (o1, o2) -> {
                if (o1.compareTo(o2) > 0) {
                    return -1;
                }
                if (o1.compareTo(o2) < 0) {
                    return 1;
                }
                return 0;
            });
            for(Float i : scores) {
                int count = (int) (scores.stream().filter(integer -> integer > i).count() + 1);
                ranks.add(count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(score);
        if(score == (float)120) {
            return null;
        }else {
            //获得排名以及自己的分数
            if(flag) {
                int rank = ranks.get(scores.indexOf(score));
                ArrayList<String> res = new ArrayList<>();
                res.add(String.valueOf(rank));
                res.add(String.valueOf(score));
                return res;
            }else {
                //获得前五名以及班级平均分以及自己的排名以及比例
                ArrayList<String> res = new ArrayList<>();
                for(int i = 0; i < 5; i++) {  //前五名的分数以及排名
                    res.add(String.valueOf(ranks.get(i)));
                    res.add(String.valueOf(scores.get(i)));
                }
                //算平均分
                float sum  = 0;
                for(int i = 0; i < scores.size(); i++) {
                    sum += scores.get(i);
                }
                float av = sum / scores.size();
                String avg = String.format("%.3f", av);
                res.add(avg);  //平均分
                //算比例画图
                int[] data = {0, 0, 0, 0, 0};
                for(int i = 0; i < scores.size(); i++) {
                    if(scores.get(i) > 90) {
                        data[0]++;
                    }else if(scores.get(i) > 80) {
                        data[1]++;
                    }else if(scores.get(i) > 70) {
                        data[2]++;
                    }else if(scores.get(i) > 60) {
                        data[3]++;
                    }else {
                        data[4]++;
                    }
                }
                for(int i = 0; i < 5; i++) {
                    res.add(String.valueOf(data[i]));
                }
                return res;
            }
        }
    }

    public int getNum(String major) {
        String sql = "select count(*) from stu_info where Major='"+major+"'";
        try {
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(sql);
            res.next();
            return Integer.parseInt(res.getString("count(*)"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取GPA排名
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getGPARank(String year, String semester, String nature, String self_id, String major) {
        ArrayList<String> map = new ArrayList<>();
        ArrayList<String> stu_list = new ArrayList<>();
        ArrayList<Float> gpa_list = new ArrayList<>();
        //保存成绩信息
        ArrayList<GPAUtil> gpaUtils = new ArrayList<>();
        ArrayList<StuGPAUtil> stuGPAUtils = new ArrayList<>();
        //查询体育课的Code
        ArrayList<String> pe_code = new ArrayList<>();
        String sql_code = "select Ccode from course_info_2018_2019 WHERE Cdept='体育教学部'";
        //查询实践课的Code
        ArrayList<String> p_code = new ArrayList<>();
        String sql_p = "select Ccode from course_info_2018_2019 WHERE Cnature='实践课'";
        float self_gap = 0;
        //查询本专业所有人的学号并保存
        String stu_string = "select Sno from stu_info where Major='"+major+"'";
        try {
            Statement statement = connection.createStatement();
            //选择了必修-体育就查询下体育课的code
            if(nature.equals("必修-体育")) {
                ResultSet set_code = statement.executeQuery(sql_code);
                while(set_code.next()) {
                    pe_code.add(set_code.getString("Ccode"));
                }
            }
            if(nature.equals("必修+实践")) {
                ResultSet set_code = statement.executeQuery(sql_p);
                while(set_code.next()) {
                    p_code.add(set_code.getString("Ccode"));
                }
            }
//            System.out.println(sql_code);
//            System.out.println(sql_p);
            //查询本专业学生的学号
            ResultSet set = statement.executeQuery(stu_string);
            while(set.next()) {
                String stuString = set.getString("Sno");
                if(stuString.contains("1201")) {
                    stu_list.add(stuString);
                }
            }
            //获得每个学生的成绩信息,这里可以稍作改进
            for(int i = 0; i < stu_list.size(); i++) {
                String str = stu_list.get(i);  //学号
                //先获取必修+实践,里面也包含了体育课
                String sql = "select GPA, Ccode, Credit from exam_info_test where "
                        + "Sno='"+str+"' and Cnature in ('必修课', '实践课') and `Year`='"+year+"' and Semester='"+semester+"'";
                ResultSet course = statement.executeQuery(sql);
                if(course == null) {
                    return null;
                }
                GPAUtil util = new GPAUtil();
                while(course.next()) {
                    //先选出所有课
                    String gpa = course.getString("GPA");
                    String credit = course.getString("Credit");
                    String code = course.getString("Ccode");
                    util.setGPA(gpa);
                    util.setCredit(credit);
                    util.setCode(code);
                    //一个学生的所有成绩信息
                    gpaUtils.add(util);
                }
                StuGPAUtil stuGPAUtil = new StuGPAUtil();
                //所有学生的成绩新
                stuGPAUtil.setStu_id(str);
                stuGPAUtil.setUtil(gpaUtils);
                stuGPAUtils.add(stuGPAUtil);
            }
            //根据情况算GPA
            if(nature.equals("必修")) {
                //对每一个学生
                for(int i = 0; i < stuGPAUtils.size(); i++) {
                    float sumCredit = 0;
                    float sum = 0;
                    ArrayList<GPAUtil> gpaUtils1 = stuGPAUtils.get(i).getUtil();
                    String id = stuGPAUtils.get(i).getStu_id();
                    //计算每一门课
                    for(int j = 0; j < gpaUtils1.size(); i++) {
                        String code = gpaUtils1.get(j).getCode();
                        //实践课就跳过
                        if(p_code.contains(code)) {
                            continue;
                        }
                        String credit = gpaUtils1.get(j).getCredit();
                        String gpa = gpaUtils1.get(j).getGPA();
                        float gpa_temp = Float.parseFloat(gpa);
                        float credit_temp = Float.parseFloat(credit);
                        sum += gpa_temp * credit_temp;
                        sumCredit += credit_temp;
                    }
                    float final_gpa = sum / sumCredit;
                    if(id.trim().equals(self_id.trim())) {
                        self_gap = final_gpa;
                    }
                    gpa_list.add(final_gpa);
                }
            }else if(nature.equals("必修+实践")) {
                //对每一个学生
                for(int i = 0; i < stuGPAUtils.size(); i++) {
                    float sumCredit = 0;
                    float sum = 0;
                    ArrayList<GPAUtil> gpaUtils1 = stuGPAUtils.get(i).getUtil();
                    String id = stuGPAUtils.get(i).getStu_id();
                    //计算每一门课
                    for(int j = 0; j < gpaUtils1.size(); i++) {
                        String code = gpaUtils1.get(j).getCode();
                        String credit = gpaUtils1.get(j).getCredit();
                        String gpa = gpaUtils1.get(j).getGPA();
                        float gpa_temp = Float.parseFloat(gpa);
                        float credit_temp = Float.parseFloat(credit);
                        sum += gpa_temp * credit_temp;
                        sumCredit += credit_temp;
                    }
                    float final_gpa = sum / sumCredit;
                    if(id.trim().equals(self_id.trim())) {
                        self_gap = final_gpa;
                    }
                    gpa_list.add(final_gpa);
                }
            }else {
                //对每一个学生
                for(int i = 0; i < stuGPAUtils.size(); i++) {
                    float sumCredit = 0;
                    float sum = 0;
                    ArrayList<GPAUtil> gpaUtils1 = stuGPAUtils.get(i).getUtil();
                    String id = stuGPAUtils.get(i).getStu_id();
                    //计算每一门课
                    for(int j = 0; j < gpaUtils1.size(); i++) {
                        String code = gpaUtils1.get(j).getCode();
                        //实践课和体育课就跳过
                        if(p_code.contains(code) || pe_code.contains(code)) {
                            continue;
                        }
                        String credit = gpaUtils1.get(j).getCredit();
                        String gpa = gpaUtils1.get(j).getGPA();
                        float gpa_temp = Float.parseFloat(gpa);
                        float credit_temp = Float.parseFloat(credit);
                        sum += gpa_temp * credit_temp;
                        sumCredit += credit_temp;
                    }
                    float final_gpa = sum / sumCredit;
                    if(id.trim().equals(self_id.trim())) {
                        self_gap = final_gpa;
                    }
                    gpa_list.add(final_gpa);
                }
            }
            //排序
            Collections.sort(gpa_list, new Comparator<Float>() {
                @Override
                public int compare(Float o1, Float o2) {
                    if (o1.compareTo(o2) > 0) {
                        return -1;
                    }
                    if (o1.compareTo(o2) < 0) {
                        return 1;
                    }
                    return 0;
                }
            });
            ArrayList<Integer> ranks = new ArrayList<Integer>();
            for(Float i : gpa_list) {
                int count = (int)gpa_list.stream().filter(integer -> integer > i).count() + 1;
                ranks.add(count);
            }
            int rank = ranks.get(gpa_list.indexOf(self_gap));
            map.add(String.valueOf(rank));
            map.add(String.valueOf(self_gap));

        } catch (SQLException e) {
            e.printStackTrace();
        }  //加载驱动
        return map;
    }

    //根据学期学年课程性质查询自己的gpa排名
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getMajorGpaRank(String idMyself, String majorString, String year, String semester, String nature) {
        ArrayList<String> res = new ArrayList<>();
        String sqlString = "";
        if(year.equals("全部")) {
            if(semester.equals("全部")) {
                if(nature.equals("全部")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'";
                }else if(nature.equals("必修")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and CourseNature = " + "'" + "必修课" + "'";
                }else if(nature.equals("必修+实践")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'实践课')";
                }else if(nature.equals("必修+专选")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'专选课')";
                }
            }else {
                if(nature.equals("全部")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Semester = "
                            + "'" + semester + "'";
                }else if(nature.equals("必修")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Semester = "
                            + "'" + semester + "'" + " and CourseNature = " + "'" + "必修课" + "'";
                }else if(nature.equals("必修+实践")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Semester = "
                            + "'" + semester + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'实践课')";
                }else if(nature.equals("必修+专选")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Semester = "
                            + "'" + semester + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'专选课')";
                }
            }
        }else {
            if(semester.equals("全部")) {
                if(nature.equals("全部")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'";
                }else if(nature.equals("必修")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and CourseNature = " + "'" + "必修课" + "'";
                }else if(nature.equals("必修+实践")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'实践课')";
                }else if(nature.equals("必修+专选")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'专选课')";
                }
            }else {
                if(nature.equals("全部")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and Semester = "
                            + "'" + semester + "'";
                }else if(nature.equals("必修")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and Semester = "
                            + "'" + semester + "'" + " and CourseNature = " + "'" + "必修课" + "'";
                }else if(nature.equals("必修+实践")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and Semester = "
                            + "'" + semester + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'实践课')";
                }else if(nature.equals("必修+专选")) {
                    sqlString = "select Sno, Mark, Gpa, Credit from final_exam_info where Major = '"
                            + majorString + "'" + " and Years = " + "'" + year + "'" + " and Semester = "
                            + "'" + semester + "'" + " and (CourseNature = " + "'" + "必修课" + "'"
                            + " or CourseNature = " + "'专选课')";
                }
            }
        }
        System.out.println(sqlString);
        String sqlNum = "SELECT Sno FROM stu_info WHERE Major = " + "'" + majorString + "'";
        System.out.println(sqlNum);
        ArrayList<String> numArrayList = new ArrayList<>();
        try {
            //查询有哪些人
            Statement statement1 = connection.createStatement();
            ResultSet set1 = statement1.executeQuery(sqlNum);
            while(set1.next()) {
                numArrayList.add(set1.getString(1));
                //System.out.println(set1.getString(1));
            }
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sqlString);
            ArrayList<String[]> data = new ArrayList<>();
            while(set.next()) {
                String []xStrings = new String[3];
                xStrings[0] = set.getString(1);
                xStrings[1] = set.getString(3);
                xStrings[2] = set.getString(4);
                data.add(xStrings);
                //String stuString = set.getString("Sno") + " " + set.getString("Mark");
                System.out.println(set.getString(1) + " " +
                        set.getString(2) + " " + set.getString(3) + " " +
                        set.getString(4));
            }
            if(data.isEmpty()) {
                return null;
            }
            //开始计算每个人的GPA
            String gpaMyself = "";
            ArrayList<Double> gpaList = new ArrayList<>();
            ArrayList<String[]> gpaArrayList = new ArrayList<>();
            for(int i = 0; i < data.size(); i++) {
                String id = data.get(i)[0];
                double sum = 0;
                double credits = 0;
                while(i < data.size() && data.get(i)[0].equals(id)) {
                    double credit = Double.parseDouble(data.get(i)[2].trim());
                    double gpa = Double.parseDouble(data.get(i)[1].trim());
                    sum += credit * gpa;
                    credits += credit;
                    i++;
                }
                String []temp = new String[2];
                temp[0] = id;
                DecimalFormat dfDecimalFormat = new DecimalFormat("0.000");
                gpaList.add(Double.parseDouble(dfDecimalFormat.format(sum / credits)));
                temp[1] = dfDecimalFormat.format(sum / credits);
                if(id.trim().equals(idMyself)) {
                    gpaMyself = temp[1];
                }
                i--;
                gpaArrayList.add(temp);
            }
            System.out.println(gpaMyself);
            for(String []e : gpaArrayList) {
                System.out.println(e[0] + " " + e[1]);
            }
            //对gpaList排序
            Collections.sort(gpaList, (o1, o2) -> {
                if (o1.compareTo(o2) > 0) {
                    return -1;
                }
                if (o1.compareTo(o2) < 0) {
                    return 1;
                }
                return 0;
            });
            ArrayList<Integer> ranks = new ArrayList<Integer>();
            for(Double i : gpaList) {
                int count = (int) (gpaList.stream().filter(integer -> integer > i).count() + 1);
                ranks.add(count);
            }
            res.add(gpaMyself.trim());
            Double x = Double.parseDouble(gpaMyself.trim());
            res.add(String.valueOf(ranks.get(gpaList.indexOf(x))));
            System.out.println(ranks.get(gpaList.indexOf(x)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<GradeFailed> getFailed(String year, String semester, String major) throws SQLException {
        List<GradeFailed> list = new ArrayList<>();
        String sql = "SELECT CourseName, Mark, CourseNature FROM final_exam_info WHERE Major = " + "'" + major + "'" +
                " AND Years = " + "'" + year + "'" + " AND Semester = " + "'" + semester + "'" + " AND " +
                "CourseClass <> '体育课-学评教体育课' ORDER BY CourseName";
        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(sql);
        ArrayList<String[]> tempList = new ArrayList<>();
        while (set.next()) {
            String[] temp = new String[3];
            temp[0] = set.getString(1);
            temp[1] = set.getString(2);
            temp[2] = set.getString(3);
            tempList.add(temp);
        }
        boolean flag = false;
        for(int i = 0; i < tempList.size(); i++) {
            int total = 1;
            int num = 0;
            ArrayList<Integer> data = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
            String courseName = tempList.get(i)[0];
            String courseNature = tempList.get(i)[2];
            while(i + 1 < tempList.size() && courseName.equals(tempList.get(i + 1)[0])) {
                total++;
                String mark = tempList.get(i)[1];
                double score = Double.parseDouble(mark.trim());
                if(score < 60 && score >= 55) {
                    data.set(0, data.get(0) + 1);
                }else if(score < 55 && score >= 50) {
                    data.set(1,  data.get(1) + 1);
                }else if(score < 50 && score >= 45) {
                    data.set(2, data.get(2) + 1);
                }else if(score < 45 && score >= 40) {
                    data.set(3, data.get(3) + 1);
                }else if(score < 40) {
                    data.set(4, data.get(4) + 1);
                }
                i++;
            }
            for(int j = 0; j < 5; j++) {
                num += data.get(j);
            }
            if(num != 0) {
                flag = true;
                GradeFailed failed = new GradeFailed();
                failed.setCourseName(courseName);
                failed.setCourseNature(courseNature);
                failed.setNum(num);
                failed.setTotalNum(total);
                failed.setData(data);
                list.add(failed);
            }
            System.out.println(courseName + " " + courseNature + " " + num + "/" + total);
        }
        if(flag) {
            return list;
        }else {
            return null;
        }
    }
}

