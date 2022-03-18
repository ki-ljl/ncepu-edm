package com.example.NCEPU.JWUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.NCEPU.Utils.Exam;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.Plan;
import com.example.NCEPU.Utils.SubPlan;
import com.example.NCEPU.Utils.CourseUtil;
import com.example.NCEPU.Utils.ToastUtil;

//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.rendering.PDFRenderer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ConnectJWGL {


    public Map<String,String> cookies_innet;

    Context mContext;

    private final String url = "https://202-204-74-178.webvpn.ncepu.edu.cn";
    public Map<String,String> cookies = new HashMap<>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;
    private String stuNum;
    private String jw_Password;
    private String in_Password;

    public ConnectJWGL(String stuNum, String in_Password, String jw_Password, Context mContext) throws Exception {
        this.stuNum = stuNum;
        this.in_Password = in_Password;
        this.jw_Password = jw_Password;
        this.mContext = mContext;
        Login_Innet login_innet = new Login_Innet(stuNum, in_Password);
        cookies_innet = login_innet.in_net();
    }

    public int init() throws Exception{
        if(cookies_innet.containsKey("webvpn_username")) {
            getCsrftoken();
            getRSApublickey();
            System.out.println("内网密码正确");
            return 1;
        }else {
            System.out.println("内网密码错误");
            return 0;
        }
    }

    // 获取csrftoken和Cookies
    private void getCsrftoken(){
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            connection = Jsoup.connect(url+ "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime()).cookies(cookies_innet);
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
            connection.header("Connection", "keep-alive");
            response = connection.followRedirects(true).timeout(60000).execute();
//            response = connection.execute();
            cookies = response.cookies();
            //保存csrftoken
            document = Jsoup.parse(response.body());
            csrftoken = document.getElementById("csrftoken").val();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 获取公钥并加密密码
    public void getRSApublickey() throws Exception{

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connection = Jsoup.connect(url+ "/jwglxt/xtgl/login_getPublicKey.html?" +
                "time="+ new Date().getTime()).cookies(cookies_innet);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Connection", "keep-alive");
        //去
        response = connection.cookies(cookies).ignoreContentType(true).followRedirects(true).timeout(60000).execute();

        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        jw_Password = RSAEncoder.RSAEncrypt(jw_Password, B64.b64tohex(modulus), B64.b64tohex(exponent));

//        StrictMode.ThreadPolicy policy2=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy2);

        jw_Password = B64.hex2b64(jw_Password);
    }

    //登录
    public boolean beginLogin() throws Exception{

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connection = Jsoup.connect(url+ "/jwglxt/xtgl/login_slogin.html").cookies(cookies_innet);
        //connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Connection", "keep-alive");

        connection.data("csrftoken",csrftoken);
//        connection.data("language", "zh_CN");
        connection.data("yhm",stuNum);
        connection.data("mm", jw_Password);
        connection.data("mm", jw_Password);

        response = connection.cookies(cookies).ignoreContentType(true).followRedirects(true)
                .method(Connection.Method.POST).timeout(60000).execute();
//        response = connection.execute();
        System.out.println("登录后的cookies为:" + response.cookies().get("JSESSIONID"));
        Map<String, String> subCookies = new HashMap<>();
        subCookies.put("JSESSIONID", response.cookies().get("JSESSIONID"));
        System.out.println("subCookies为:" + subCookies);
        cookies = subCookies;
        document = Jsoup.parse(response.body());
//        System.out.println("登录后的界面为:" + document);
        if(document.getElementById("tips") == null){
            System.out.println("教务系统密码正确");
            return true;
        }else{
            System.out.println(document.getElementById("tips").text());
            return false;
        }
    }

    // 查询学生信息
    public Map<String, String> getStudentInformation() throws Exception {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        connection = Jsoup.connect(url+ "/jwglxt/xsxxxggl/xsxxwh_cxCkDgxsxx.html?gnmkdm=N100801&su="+ stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).ignoreContentType(true).followRedirects(true).timeout(60000).execute();
//        Log.d("dddddddddd", response.body());
        JSONObject jsonObject = JSON.parseObject(response.body());
        System.out.println(jsonObject);
        System.out.println("--- 基本信息 ---");
        System.out.println("学号：" + jsonObject.getString("xh_id"));
        System.out.println("性别：" + jsonObject.getString("xbm"));
        System.out.println("民族：" + jsonObject.getString("mzm"));
        System.out.println("学院：" + jsonObject.getString("jg_id"));
        System.out.println("班级：" + jsonObject.getString("bh_id"));
        System.out.println("专业：" + jsonObject.getString("zszyh_id"));
        System.out.println("状态：" + jsonObject.getString("xjztdm"));
        System.out.println("入学年份：" + jsonObject.getString("njdm_id"));
        System.out.println("证件号码：" + jsonObject.getString("zjhm"));
        System.out.println("政治面貌：" + jsonObject.getString("zzmmm"));
        Map<String, String> map = new HashMap<>();
        map.put("id", jsonObject.getString("xh_id"));
        map.put("class", jsonObject.getString("bh_id"));
        map.put("name", jsonObject.getString("xm"));
        map.put("major", jsonObject.getString("zyh_id"));
        map.put("sex", jsonObject.getString("xbm"));
        map.put("dept", jsonObject.getString("jg_id"));
        map.put("year", jsonObject.getString("njdm_id"));
        return map;
    }

    //获取考试信息
    public ArrayList<Exam> getExamInformation(String year, String term) throws Exception {
        Map<String, String> datas = new HashMap<>();
        if(!year.equals("")) {
            year = year.substring(0, 4);
        }
        datas.put("xnm", year);
        if(term.equals("")) {
            datas.put("xqm", "");
        }else {
            int term_ = Integer.parseInt(term);
            datas.put("xqm",String.valueOf(term_ * term_ * 3));
        }
        datas.put("ksmcdmb_id","");
        datas.put("kch","");
        datas.put("kc","");
        datas.put("ksrq","");
        datas.put("_search","fasle");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","150");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("time","0");

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connection = Jsoup.connect(url+ "/jwglxt/kwgl/kscx_cxXsksxxIndex.html?doType=query&gnmkdm=N358105&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).followRedirects(true).timeout(60000).execute();

        System.out.println(response.body());

        JSONObject jsonObject = JSON.parseObject(response.body());
        JSONArray examTable = JSON.parseArray(jsonObject.getString("items"));

        ArrayList<Exam> list = new ArrayList<>();

        for (Iterator iterator = examTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
//            System.out.println(lesson.getString("jxbmc") + " " +
//                    lesson.getString("kch") + " " +
//                    lesson.getString("jsxx") + " " +
//                    lesson.getString("cdmc") + " " +
//                    lesson.getString("kssj"));
            Exam exam = new Exam();
            String kch = lesson.getString("kch");  //课程代码
            exam.setCourse_code(kch);
            String course_name = lesson.getString("kcmc");  //课程名称
            exam.setCourse_name(course_name);
            String class_name = lesson.getString("jxbmc");  //教学班名称
            exam.setClass_name(class_name);
            String class_comp = lesson.getString("jxbzc");  //教学班组成
            exam.setClass_comp(class_comp);
            String teacher = lesson.getString("jsxx");  //老师
            exam.setTeacher(teacher);
            String school_time = lesson.getString("sksj");  //上课时间
            exam.setSchool_time(school_time);
            String class_place = lesson.getString("jxdd");   //上课地点
            exam.setClass_place(class_place);
            String exam_time = lesson.getString("kssj");  //考试时间
            exam.setExam_time(exam_time);
            String exam_place = lesson.getString("cdmc");  //考试地点
            exam.setExam_place(exam_place);
            String major = lesson.getString("zymc");  //专业
            exam.setMajor(major);
            String campus = lesson.getString("cdxqmc");  //考试校区
            exam.setCampus(campus);
            String grade_class = lesson.getString("njmc");  //年级
            exam.setGrade_class(grade_class);
            list.add(exam);
        }

        return list;

    }

    public int getIndex(String[] list, String res) {
        for(int i = 0; i < list.length; i++) {
            if(list[i].equals(res)) {
                return i + 1;
            }
        }
        return 0;
    }

    public ArrayList<CourseUtil> getStudentTimetableInit() throws Exception {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //先找到学年和学期
        String year = "2019";
        int term = 2;
        connection = Jsoup.connect(url+ "/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies_innet).cookies(cookies).execute();
        Map<String, String> cook = response.cookies();
        document = Jsoup.parse(response.body());
//        System.out.println(document);
        Elements lis = document.getElementsByAttributeValue("id", "xnm").select("option");
        for(Element element : lis) {
            if(element.attr("selected").equals("selected")) {
                year = element.text().substring(0, 4).replace(" ", "");
            }
        }
        Elements lis1 = document.getElementsByAttributeValue("id", "xqm").select("option");
        for(Element element : lis1) {
            if(element.attr("selected").equals("selected")) {
                term = Integer.parseInt(element.text().replace(" ", ""));
            }
        }
//        year = "2019"; term = 1;
        connection = Jsoup.connect(url+ "/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xnm",year);
        connection.data("xqm",String.valueOf(term * term * 3));
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).followRedirects(true).timeout(60000).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        ArrayList<CourseUtil> list = new ArrayList<>();
        if(jsonObject.get("kbList") == null){
//            System.out.println("暂时没有安排课程");
            return list;
        }
        String day[] = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        JSONArray timeTable = JSON.parseArray(jsonObject.getString("kbList"));
//        System.out.println(String.valueOf(year) + " -- " + String.valueOf(year + 1) + "学年 " + "第" + term + "学期");
        for (Iterator iterator = timeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
//            System.out.println(lesson.getString("xqjmc") + " " +
//                    lesson.getString("jc") + " " +
//                    lesson.getString("kcmc") + " " +
//                    lesson.getString("xm") + " " +
//                    lesson.getString("xqmc") + " " +
//                    lesson.getString("cdmc") + " " +
//                    lesson.getString("zcd"));
            CourseUtil courseUtil = new CourseUtil();
            String dayOfWeek = lesson.getString("xqjmc");  //周几上课
//            System.out.println(dayOfWeek);
//            System.out.println(getIndex(day, dayOfWeek));
            courseUtil.setDayOfWeek(getIndex(day, dayOfWeek));//1-7
            String timeOfDay = lesson.getString("jc");  //3-4节,最多11节课
            int _index = timeOfDay.indexOf("-");
            int begin = Integer.parseInt(timeOfDay.substring(0, _index));
            int end = Integer.parseInt(timeOfDay.substring(_index + 1, timeOfDay.length() - 1));
            courseUtil.setClassStart(begin);
            courseUtil.setClassLength(end - begin + 1);
            String name = lesson.getString("kcmc");  //课程名称
            courseUtil.setName(name);
            String teacher = lesson.getString("xm");
            courseUtil.setTeacher(teacher);
            String address = lesson.getString("xqmc") + lesson.getString("cdmc");
            courseUtil.setClassRoom(address);
            String week = lesson.getString("zcd");  //1-16周
            _index = week.indexOf("-");
            int week_begin = Integer.parseInt(week.substring(0, _index));
            int week_end = Integer.parseInt(week.substring(_index + 1, week.length() - 1));
            List<Boolean> weekOfTerm = new ArrayList<>(25);
            for(int i = 0; i < 25; i++) {
                weekOfTerm.add(false);
            }
            for(int i = week_begin - 1; i < week_end; i++) {
                weekOfTerm.set(i, true);
            }
            courseUtil.setWeekOfTerm(weekOfTerm);
            list.add(courseUtil);
        }
        return list;
    }

    // 获取课表信息
    public ArrayList<CourseUtil> getStudentTimetable(String year, int term) throws Exception {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //先找到学年和学期
//        String year = "2019";
//        int term = 2;
//        connection = Jsoup.connect(url+ "/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151");
//        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
//        response = connection.cookies(cookies_innet).cookies(cookies).execute();
//        Map<String, String> cook = response.cookies();
//        document = Jsoup.parse(response.body());
////        System.out.println(document);
//        Elements lis = document.getElementsByAttributeValue("id", "xnm").select("option");
//        for(Element element : lis) {
//            if(element.attr("selected").equals("selected")) {
//                year = element.text().substring(0, 4).replace(" ", "");
//            }
//        }
//        Elements lis1 = document.getElementsByAttributeValue("id", "xqm").select("option");
//        for(Element element : lis1) {
//            if(element.attr("selected").equals("selected")) {
//                term = Integer.parseInt(element.text().replace(" ", ""));
//            }
//        }
//        year = "2019"; term = 1;
        System.out.println(year + " " + term);
        connection = Jsoup.connect(url+ "/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xnm",year);
        connection.data("xqm",String.valueOf(term * term * 3));
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).followRedirects(true).timeout(60000).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        ArrayList<CourseUtil> list = new ArrayList<>();
        if(jsonObject.get("kbList") == null){
//            System.out.println("暂时没有安排课程");
            return list;
        }
        String day[] = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        JSONArray timeTable = JSON.parseArray(jsonObject.getString("kbList"));
//        System.out.println(String.valueOf(year) + " -- " + String.valueOf(year + 1) + "学年 " + "第" + term + "学期");
        for (Iterator iterator = timeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
//            System.out.println(lesson.getString("xqjmc") + " " +
//                    lesson.getString("jc") + " " +
//                    lesson.getString("kcmc") + " " +
//                    lesson.getString("xm") + " " +
//                    lesson.getString("xqmc") + " " +
//                    lesson.getString("cdmc") + " " +
//                    lesson.getString("zcd"));
            CourseUtil courseUtil = new CourseUtil();
            String dayOfWeek = lesson.getString("xqjmc");  //周几上课
//            System.out.println(dayOfWeek);
//            System.out.println(getIndex(day, dayOfWeek));
            courseUtil.setDayOfWeek(getIndex(day, dayOfWeek));//1-7
            String timeOfDay = lesson.getString("jc");  //3-4节,最多11节课
            int _index = timeOfDay.indexOf("-");
            int begin = Integer.parseInt(timeOfDay.substring(0, _index));
            int end = Integer.parseInt(timeOfDay.substring(_index + 1, timeOfDay.length() - 1));
            courseUtil.setClassStart(begin);
            courseUtil.setClassLength(end - begin + 1);
            String name = lesson.getString("kcmc");  //课程名称
            courseUtil.setName(name);
            String teacher = lesson.getString("xm");
            courseUtil.setTeacher(teacher);
            String address = lesson.getString("xqmc") + lesson.getString("cdmc");
            courseUtil.setClassRoom(address);
            String week = lesson.getString("zcd");  //1-16周
            _index = week.indexOf("-");
            int week_begin = Integer.parseInt(week.substring(0, _index));
            int week_end = Integer.parseInt(week.substring(_index + 1, week.length() - 1));
            List<Boolean> weekOfTerm = new ArrayList<>(25);
            for(int i = 0; i < 25; i++) {
                weekOfTerm.add(false);
            }
            for(int i = week_begin - 1; i < week_end; i++) {
                weekOfTerm.set(i, true);
            }
            courseUtil.setWeekOfTerm(weekOfTerm);
            list.add(courseUtil);
        }
        System.out.println("当前size为:" + list.size());
        return list;
    }

    // 获取成绩信息
    public ArrayList<Grade> getStudentGrade(String year , String term, String query_nature) throws Exception {
        Map<String,String> datas = new HashMap<>();
        datas.put("xnm", year);
        if(term.equals("")) {
            datas.put("xqm", "");
        }else {
            int term_ = Integer.parseInt(term);
            datas.put("xqm",String.valueOf(term_ * term_ * 3));
        }
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","150");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("queryModel.sortName","");
        datas.put("time","0");

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connection = Jsoup.connect(url+ "/jwglxt/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).followRedirects(true).timeout(60000).execute();
        connection = Jsoup.connect(url+ "/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).followRedirects(true).timeout(60000).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        ArrayList<Grade> list = new ArrayList<>();
        JSONArray gradeTable = JSON.parseArray(jsonObject.getString("items"));
        for (Iterator iterator = gradeTable.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            Grade grade = new Grade();
            grade.setCourse_code(lesson.getString("kch"));
            grade.setCourse_nature(lesson.getString("kcxzmc"));
            grade.setCourse_name(lesson.getString("kcmc"));
            grade.setCredit(lesson.getString("xf"));
            grade.setGpa(lesson.getString("jd"));
            grade.setMark(lesson.getString("cj"));
            grade.setCollege(lesson.getString("kkbmmc"));
            grade.setClass_(lesson.getString("jxbmc"));
            grade.setTeacher(lesson.getString("jsxm"));
            grade.setGrade_nature(lesson.getString("ksxz"));
            grade.setXn(lesson.getString("xnmmc"));
            grade.setXq(lesson.getString("xqmmc"));
            list.add(grade);
        }
        if(query_nature.equals("全部")) {
            return list;
        }
        //删除不是当前课程性质的课程
        Iterator<Grade> it = list.iterator();
        while(it.hasNext()) {
            Grade grade = it.next();
            String nature = grade.getCourse_nature();
            if(!nature.equals(query_nature)) {
                it.remove();
            }
        }
        return list;
    }

    public void logout() throws Exception {
        connection = Jsoup.connect(url+ "/jwglxt/logout");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).ignoreContentType(true).execute();
    }

    public List<Plan> getLessonPlan() throws IOException {

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //先寻找到被选中学院的值16
        String suburl = url + "/jwglxt/jxzxjhgl/jxzxjhck_cxJxzxjhckIndex.html?gnmkdm=N153540&layout=default&su=" + "120181080110";
        connection = Jsoup.connect(suburl);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Connection", "keep-alive");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.GET).execute();
        String jg_id = "";
        String njdm_id = "";
        Document doc11 = Jsoup.parse(response.body());
        //System.out.println(doc11);
        //找学年和年份，进而找到id
        Elements lis = doc11.getElementsByAttributeValue("id", "jg_id").select("option");
        for(Element element : lis) {
            if(element.attr("selected").equals("selected")) {
                jg_id = element.attr("value");
//                System.out.println(jg_id);
            }
        }

        Elements lis1 = doc11.getElementsByAttributeValue("id", "nj_cx").select("option");
        for(Element element : lis1) {
            if(element.attr("selected").equals("selected")) {
                njdm_id = element.attr("value");
//                System.out.println(njdm_id);
            }
        }

        //开始查询
        suburl = url + "/jwglxt/jxzxjhgl/jxzxjhck_cxJxzxjhckIndex.html?doType=query&gnmkdm=N153540&su=" + stuNum;
        connection = Jsoup.connect(suburl);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("Connection", "keep-alive");
        connection.data("jg_id", jg_id);
        connection.data("njdm_id", njdm_id);
        connection.data("dlbs", "");
        connection.data("zyh_id", "");
        connection.data("_search", "false");
        connection.data("nd", String.valueOf(new Date().getTime()));
        connection.data("queryModel.showCount", "15");
        connection.data("queryModel.currentPage", "1");
        connection.data("queryModel.sortName", "");
        connection.data("queryModel.sortOrder", "asc");
        connection.data("time", "1");
        response = connection.cookies(cookies_innet).cookies(cookies).ignoreContentType(true).method(Connection.Method.GET).execute();
//        System.out.println(response.body());
        JSONObject jsonObject = JSON.parseObject(response.body());
        JSONArray table = JSON.parseArray(jsonObject.getString("items"));
        //开始找jxzxjhxx_id
        String final_id = "";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String major = sharedPreferences.getString("major", "");
        for (Iterator iterator = table.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            if(lesson.getString("zymc").equals(major)) {
                final_id = lesson.getString("jxzxjhxx_id");
            }
//            System.out.println(lesson.getString("zymc") + " " +
//                    lesson.getString("jxzxjhxx_id"));
        }



        //获取最低要求学分和已修学分
        String time = String.valueOf(new Date().getTime());
        connection = Jsoup.connect(url + "/jwglxt/jxzxjhgl/jxzxjhck_cxJxzxjhxdyqIndex.html?jxzxjhxx_id=" + final_id + "&_=" + time + "&gnmkdm=N153540&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.GET).ignoreContentType(true).execute();
        String doc = response.body();
//        System.out.println(doc);

        //服气, xfyqjd_id也是动态变化的
        System.out.println(doc);
        int index11 = doc.indexOf("必修课&nbsp;最低要求学分");
        String sub1 = doc.substring(index11 - 200, index11 - 100);
        int index12 = sub1.indexOf("xfyqjd_id");
        String zhu = sub1.substring(index12 + 11, index12 + 43);
//        System.out.println(sub1.substring(index12 + 11, index12 + 43));

        int index21 = doc.indexOf("专选课&nbsp;最低要求学分");
        String sub2 = doc.substring(index21 - 200, index21 - 100);
        int index22 = sub2.indexOf("xfyqjd_id");
        String zhuan = sub2.substring(index22 + 11, index22 + 43);
//        System.out.println(sub2.substring(index22 + 11, index22 + 43));

        int index31 = doc.indexOf("实践课&nbsp;最低要求学分");
        String sub3 = doc.substring(index31 - 200, index31 - 100);
        int index32 = sub3.indexOf("xfyqjd_id");
        String shi = sub3.substring(index32 + 11, index32 + 43);
//        System.out.println(sub3.substring(index32 + 11, index32 + 43));



        ArrayList<Integer> num = new ArrayList<>();
        //寻找yqzdxf
        String doc1 = doc;
        int index1 = 0;
        while(index1 != -1) {
            index1 = doc1.indexOf("yqzdxf", index1 + 6);
            if(index1 != -1) {
                num.add(index1);
            }
        }
        //寻找jdkcxf
        String doc2 = doc;
        int index2 = 0;
        while(index2 != -1) {
            index2 = doc2.indexOf("jdkcxf", index2 + 6);
            if(index2 != -1) {
                num.add(index2);
            }
        }
        for(int v : num) {
//            System.out.println(v);
        }
        int[] final_index = {2, 4, 6, 10, 12, 14};
        ArrayList<String> credits = new ArrayList<>();
        for(int i = 0; i < final_index.length; i++) {
            int index = final_index[i];
            index = num.get(index);
            String x = doc.substring(index + 8, index + 15);
            String y = "";
            for(int j = 0; j < x.length(); j++) {
                if(Character.isDigit(x.charAt(j)) || x.charAt(j) == '.') {
                    y += String.valueOf(x.charAt(j));
                }else {
                    continue;
                }
            }
            credits.add(y);
        }

        List<Plan> data = new ArrayList<>();
        connection = Jsoup.connect(url + "/jwglxt/jxzxjhgl/jxzxjhxfyq_cxJxzxjhxfyqKcxx.html?gnmkdm=N153540&su=" +  stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xfyqjd_id", zhu);
        connection.data("jdkcsx", "1");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        JSONArray major1 = JSON.parseArray(response.body());

        Plan plan1 = new Plan();
        plan1.setTag("必修课");
        plan1.setMinCredit(credits.get(0));
        plan1.setCurrentCredit(credits.get(3));
        List<SubPlan> subPlans1 = new ArrayList<>();
        for (Iterator iterator = major1.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            SubPlan subPlan = new SubPlan();
            subPlan.setCourse_num(lesson.getString("KCH"));
            subPlan.setCourse_name(lesson.getString("KCMC"));
            subPlan.setCourse_nature(lesson.getString("KCXZMC"));
            subPlan.setCredit(lesson.getString("XF"));
            subPlan.setYear(lesson.getString("JYXDXNM"));
            subPlan.setSemester(lesson.getString("JYXDXQM"));
            subPlans1.add(subPlan);
            System.out.println(lesson.getString("KCH") + " " +
                    lesson.getString("KCMC") + " " +
                    lesson.getString("KCXZMC") + " " +
                    lesson.getString("XF") + " " +
                    lesson.getString("JYXDXNM") + " " +
                    lesson.getString("JYXDXQM"));
        }
        plan1.setPlans(subPlans1);
        data.add(plan1);


        connection = Jsoup.connect(url + "/jwglxt/jxzxjhgl/jxzxjhxfyq_cxJxzxjhxfyqKcxx.html?gnmkdm=N153540&su=" +  stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xfyqjd_id", zhuan);
        connection.data("jdkcsx", "1");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        //Document document = response.parse();
        JSONArray major2 = JSON.parseArray(response.body());

        Plan plan2 = new Plan();
        plan2.setTag("专选课");
        plan2.setMinCredit(credits.get(1));
        plan2.setCurrentCredit(credits.get(4));
        List<SubPlan> subPlans2 = new ArrayList<>();
        for (Iterator iterator = major2.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            SubPlan subPlan = new SubPlan();
            subPlan.setCourse_num(lesson.getString("KCH"));
            subPlan.setCourse_name(lesson.getString("KCMC"));
            subPlan.setCourse_nature(lesson.getString("KCXZMC"));
            subPlan.setCredit(lesson.getString("XF"));
            subPlan.setYear(lesson.getString("JYXDXNM"));
            subPlan.setSemester(lesson.getString("JYXDXQM"));
            subPlans2.add(subPlan);
            System.out.println(lesson.getString("KCH") + " " +
                    lesson.getString("KCMC") + " " +
                    lesson.getString("KCXZMC") + " " +
                    lesson.getString("XF") + " " +
                    lesson.getString("JYXDXNM") + " " +
                    lesson.getString("JYXDXQM"));
        }
        plan2.setPlans(subPlans2);
        data.add(plan2);


        connection = Jsoup.connect(url + "/jwglxt/jxzxjhgl/jxzxjhxfyq_cxJxzxjhxfyqKcxx.html?gnmkdm=N153540&su=" +  stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.data("xfyqjd_id", shi);
        connection.data("jdkcsx", "1");
        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        //Document document = response.parse();
        JSONArray major3 = JSON.parseArray(response.body());

        Plan plan3 = new Plan();
        plan3.setTag("实践课");
        plan3.setMinCredit(credits.get(2));
        plan3.setCurrentCredit(credits.get(5));
        List<SubPlan> subPlans3 = new ArrayList<>();
        for (Iterator iterator = major3.iterator(); iterator.hasNext();) {
            JSONObject lesson = (JSONObject) iterator.next();
            SubPlan subPlan = new SubPlan();
            subPlan.setCourse_num(lesson.getString("KCH"));
            subPlan.setCourse_name(lesson.getString("KCMC"));
            subPlan.setCourse_nature(lesson.getString("KCXZMC"));
            subPlan.setCredit(lesson.getString("XF"));
            subPlan.setYear(lesson.getString("JYXDXNM"));
            subPlan.setSemester(lesson.getString("JYXDXQM"));
            subPlans3.add(subPlan);
            System.out.println(lesson.getString("KCH") + " " +
                    lesson.getString("KCMC") + " " +
                    lesson.getString("KCXZMC") + " " +
                    lesson.getString("XF") + " " +
                    lesson.getString("JYXDXNM") + " " +
                    lesson.getString("JYXDXQM"));
        }
        plan3.setPlans(subPlans3);
        data.add(plan3);
        return data;
    }
}

