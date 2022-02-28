package com.example.NCEPU.Student.Predict;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Apriori;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.adapter.AprioriAdapter;

import java.util.ArrayList;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class PoliticsActivity extends AppCompatActivity {

    private ImageButton back;
    private LinearLayout ly_back;
    private Button query;
    private ExpandableListView expandableListView;
    private TextView textView;
    private ProgressDialog progressDialog = null;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politics);
        initViews();
        setHeight();
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        back.setLayoutParams(linearParams);

        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        query.setLayoutParams(linearParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void initViews() {
        ly_back = findViewById(R.id.ly_back_politics);
        back = findViewById(R.id.ib_back_politics);
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        expandableListView = findViewById(R.id.expand_politics);
        textView = findViewById(R.id.politics_show);
        query = findViewById(R.id.btn_query_politics);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(PoliticsActivity.this);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在生成...");
                progressDialog.setIcon(R.drawable.running);
                new Thread(){
                    public void run(){
                        try{
                            runOnUiThread(() -> initData());
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();//线程启动
                handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        progressDialog.dismiss();
                        ToastUtil.showMessage(PoliticsActivity.this, "加载完成!");
                    }
                };
                progressDialog.show();
            }
        });
    }

    private String convert(String x) {
        float score = Float.parseFloat(x.trim());
        if(score >= 4.0) {
            return "A";
        }else if(score >= 3.0) {
            return "B";
        }else if(score >= 2.0) {
            return "C";
        }else if(score >= 1.0) {
            return "D";
        }else {
            return "E";
        }
    }

    private String contains(ArrayList<String> courses, String course) {
        for(String cour : courses) {
            if(cour.contains(course)) {
                return cour;
            }
        }
        return "";
    }

    private void initData() {
        ArrayList<Apriori> list = new ArrayList<>();
        try {
            ArrayList<Grade> grades_list = connectJWGL.getStudentGrade("", "", "全部");
            ArrayList<String> courses = new ArrayList<>();
            for(Grade grade : grades_list) {
                courses.add(grade.getCourse_name());
            }
            //添加规则
            //规则1:思想道德-A--->马克思-A, conf = 77.40%
            Apriori apriori = new Apriori();
            ArrayList<String> antecedents = new ArrayList<>();
            antecedents.add("思想道德-A");
            ArrayList<String> consequents = new ArrayList<>();
            consequents.add("马克思-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("77.40%");
            //寻找成绩
            ArrayList<String> preCourses = new ArrayList<>();
            ArrayList<String> backCourses = new ArrayList<>();
            String mark;
            String convert;
            int index;
            boolean flag1 = false, flag2 = false;
            boolean []check = {false, false, false, false, false, false, false, false};
            if(contains(courses, "思想道德") != "") {
                index = courses.indexOf(contains(courses, "思想道德"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "思想道德" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "思想道德暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "马克思") != "") {
                index = courses.indexOf(contains(courses, "马克思"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[1] = true;
                }
                String x = "马克思" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "马克思暂无数据";
                backCourses.add(x);
            }
            if(flag1 && flag2) {  //前后都没有
                apriori.setState("暂无");
                apriori.setSuggestion("暂无");
            }else if(!flag1 && flag2) {//前有后没有
                if(check[0]) {
                    apriori.setState("待验证");
                }else {
                    apriori.setState("无法验证");
                }
            }else if(!flag1 && !flag2) {  //前后都有
                apriori.setState("已验证");
            }else {   //后有前没有
                apriori.setState("无法验证");
            }
            flag1 = false; flag2 = false; check[0] = false; check[1] = false;
            apriori.setPreCourses(preCourses);
            apriori.setBackCourses(backCourses);
            list.add(apriori);

            //规则2:近现代史-A, 马克思-B--->毛概-A, conf = 87.4%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("近现代史-A");
            antecedents.add("马克思-B");
            consequents = new ArrayList<>();
            consequents.add("毛概-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("87.40%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "近现代史") != "") {
                index = courses.indexOf(contains(courses, "近现代史"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "近现代史" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "近现代史暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "马克思") != "") {
                index = courses.indexOf(contains(courses, "马克思"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "马克思" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("B")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "马克思暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "毛泽东") != "") {
                index = courses.indexOf(contains(courses, "毛泽东"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "毛概" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "毛概暂无数据";
                backCourses.add(x);
            }
            if(flag1 && flag2) {  //前后都没有
                apriori.setState("暂无");
            }else if(!flag1 && flag2) {//前有后没有
                if(check[0] && check[1]) {
                    apriori.setState("待验证");
                }else {
                    apriori.setState("无法验证");
                }
            }else if(!flag1 && !flag2) {  //前后都有
                apriori.setState("已验证");
            }else {   //后有前没有
                apriori.setState("无法验证");
            }
            flag1 = false; flag2 = false; check[0] = false; check[1] = false; check[2] = false;
            apriori.setPreCourses(preCourses);
            apriori.setBackCourses(backCourses);
            list.add(apriori);

            //规则3:近现代史-B--->毛概-B, conf = 70.32%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("近现代史-B");
            consequents = new ArrayList<>();
            consequents.add("毛概-B");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("70.32%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "近现代史") != "") {
                index = courses.indexOf(contains(courses, "近现代史"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("B")) {
                    check[0] = true;
                }
                String x = "近现代史" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "近现代史暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "毛泽东") != "") {
                index = courses.indexOf(contains(courses, "毛泽东"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("B")) {
                    check[1] = true;
                }
                String x = "毛概" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "毛概暂无数据";
                backCourses.add(x);
            }
            if(flag1 && flag2) {  //前后都没有
                apriori.setState("暂无");
                apriori.setSuggestion("暂无");
            }else if(!flag1 && flag2) {//前有后没有
                if(check[0]) {
                    apriori.setState("待验证");
                }else {
                    apriori.setState("无法验证");
                }
            }else if(!flag1 && !flag2) {  //前后都有
                apriori.setState("已验证");
            }else {   //后有前没有
                apriori.setState("无法验证");
            }
            flag1 = false; flag2 = false; check[0] = false; check[1] = false;
            apriori.setPreCourses(preCourses);
            apriori.setBackCourses(backCourses);
            list.add(apriori);

            //规则4:近现代史-C--->毛概-C, conf = 68.36%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("近现代史-C");
            consequents = new ArrayList<>();
            consequents.add("毛概-C");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("68.36%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "近现代史") != "") {
                index = courses.indexOf(contains(courses, "近现代史"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[0] = true;
                }
                String x = "近现代史" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "近现代史暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "毛泽东") != "") {
                index = courses.indexOf(contains(courses, "毛泽东"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[1] = true;
                }
                String x = "毛概" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "毛概暂无数据";
                backCourses.add(x);
            }
            if(flag1 && flag2) {  //前后都没有
                apriori.setState("暂无");
                apriori.setSuggestion("暂无");
            }else if(!flag1 && flag2) {//前有后没有
                if(check[0]) {
                    apriori.setState("待验证");
                }else {
                    apriori.setState("无法验证");
                }
            }else if(!flag1 && !flag2) {  //前后都有
                apriori.setState("已验证");
            }else {   //后有前没有
                apriori.setState("无法验证");
            }
            flag1 = false; flag2 = false; check[0] = false; check[1] = false;
            apriori.setPreCourses(preCourses);
            apriori.setBackCourses(backCourses);
            list.add(apriori);

            //数据显示
            AprioriAdapter aprioriAdapter = new AprioriAdapter(this, list);
            expandableListView.setAdapter(aprioriAdapter);
            ShowDialogUtil.closeProgressDialog();
            String s = "对于关联规则:思想道德-A---> 马克思-A\n" +
                    "置信度:77.40%, 其解释为:\n" +
                    "在思想道德-A的情况下有77.40%的可能马克思-A.";
            textView.setText(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}