package com.example.NCEPU.Student.TimeTable.colleges;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.example.NCEPU.Student.TimeTable.MyApplication;
import com.example.NCEPU.Student.TimeTable.bean.Course;
import com.example.NCEPU.Student.TimeTable.colleges.base.College;
import com.example.NCEPU.Student.TimeTable.util.ExcelUtils;
import com.example.NCEPU.Student.TimeTable.util.OkHttpUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;


public class CSUCollege implements College {
    public static final String NAME = "中南大学";

    private static final String BASE_URL = "http://csujwc.its.csu.edu.cn";
    private static final String SESS_URL = BASE_URL + "/Logon.do?method=logon&flag=sess";
    private static final String LOGIN_URL = BASE_URL + "/Logon.do?method=logon";
    private static final String RANDOM_CODE_URL = BASE_URL + "/verifycode.servlet";
    private static final String INDEX_URL = BASE_URL + "/jsxsd/framework/xsMain.jsp";
    private static final String TIMETABLE_EXCEL_URL = BASE_URL + "/jsxsd/xskb/xskb_print.do?xnxq01id=%s&zc=";
    private String[] termOptions;


    @Override
    public String getCollegeName() {
        return NAME;
    }

    @Override
    public boolean login(String account, String pw, String RandomCode) {
        String encoded = encode(account, pw);
        //String data = "view=0&useDogCode=&encoded=" + encoded + "&RANDOMCODE=" + RandomCode;

        FormBody form = new FormBody.Builder()
                .add("view", "0")
                .add("useDogCode", "")
                .add("encoded", encoded)
                .add("RANDOMCODE", RandomCode)
                .build();
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(form)
                .build();
        try {
            String result = OkHttpUtils.downloadText(request);
            if (!TextUtils.isEmpty(result)) {
                Document doc = Jsoup.parse(result);
                if (doc.title().equals("学生个人中心")) {
                    Element e = doc.select("select[id=xnxq01id]").first();
                    Elements es = e.children();
                    termOptions = new String[es.size()];
                    int i = 0;
                    for (Element element : es) {
                        termOptions[i++] = element.text();
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isLogin() {

        String result = OkHttpUtils.downloadText(INDEX_URL);
        if (!TextUtils.isEmpty(result)) {
            Document doc = Jsoup.parse(result);
            if (doc.title().equals("学生个人中心")) {
                Element e = doc.select("select[id=xnxq01id]").first();
                Elements es = e.children();
                termOptions = new String[es.size()];
                int i = 0;
                for (Element element : es) {
                    termOptions[i++] = element.text();
                }
                return true;
            }
        }
        return false;
    }

    private String encode(String account, String pw) {
        String result = OkHttpUtils.downloadText(SESS_URL);

        if (TextUtils.isEmpty(result))
            return "";
        String[] strings = result.split("#");
        String scode = strings[0];
        String sxh = strings[1];
        String code = account + "%%%" + pw;
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            if (i < 50) {
                encoded.append(code.substring(i, i + 1));
                int value = Integer.parseInt(sxh.substring(i, i + 1));
                encoded.append(scode.substring(0, value));
                scode = scode.substring(value);
            } else {
                encoded.append(code.substring(i));
                i = code.length();
            }
        }
        return encoded.toString();
    }


    @Override
    public Bitmap getRandomCodeImg(String dirPath) {

        String name = "random.jpg";
        if (OkHttpUtils.downloadToLocal(RANDOM_CODE_URL, dirPath, name)) {
            return BitmapFactory.decodeFile(dirPath + File.separator + name);
        } else {
            return null;
        }
    }

    @Override
    public List<Course> getCourses(String term) {
        String strUrl = String.format(TIMETABLE_EXCEL_URL, term);
        FormBody form = new FormBody.Builder()
                .add("xnxq01id", term)
                .add("zc", "")
                .build();
        Request request = new Request.Builder()
                .url(strUrl)
                .post(form)
                .build();

        String path = MyApplication.getApplication().getFilesDir().getAbsolutePath();
        String name = "timetable.xls";
        Log.d("excel", path + "/" + name);

        if (OkHttpUtils.downloadToLocal(request, path, name)) {
            File file = new File(path, name);
            if (file.exists()) {
                List<Course> list = ExcelUtils.handleExcel(file.getAbsolutePath(), 4, 2, new ExcelUtils.HandleResult() {
                    @Override
                    public Course handle(String courseStr, int row, int col) {
                        Course course = ExcelUtils.getCourseFromString(courseStr);
                        if (course == null)
                            return null;
                        course.setDayOfWeek(col == 1 ? 7 : col - 1);
                        course.setClassStart(row * 2 - 1);
                        return course;
                    }
                });
                file.delete();
                return list;
            }
        }
        return null;

    }

    @Override
    public String[] getTermOptions() {
        return termOptions;
    }

    @Override
    public boolean getFollowRedirects() {
        return true;
    }

    @Override
    public int getRandomCodeMaxLength() {
        return 4;
    }
}
