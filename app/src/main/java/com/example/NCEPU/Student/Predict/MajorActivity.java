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

public class MajorActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_major);
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
        ly_back = findViewById(R.id.ly_back_majors);
        back = findViewById(R.id.ib_back_majors);
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        expandableListView = findViewById(R.id.expand_majors);
        textView = findViewById(R.id.majors_show);
        query = findViewById(R.id.btn_query_majors);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MajorActivity.this);
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
                        ToastUtil.showMessage(MajorActivity.this, "加载完成!");
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
            //规则0:计算机组成原理-B--->计算机体系结构-B, conf = 85.88%
            Apriori apriori = new Apriori();
            ArrayList<String> antecedents = new ArrayList<>();
            antecedents.add("计算机组成原理-B");
            ArrayList<String> consequents = new ArrayList<>();
            consequents.add("计算机体系结构-B");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("85.88%");
            //寻找成绩
            ArrayList<String> preCourses = new ArrayList<>();
            ArrayList<String> backCourses = new ArrayList<>();
            String mark;
            String convert;
            int index;
            boolean flag1 = false, flag2 = false;
            boolean []check = {false, false, false, false, false, false, false, false};
            if(contains(courses, "计算机组成原理") != "") {
                index = courses.indexOf(contains(courses, "计算机组成原理"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("B")) {
                    check[0] = true;
                }
                String x = "计算机组成原理" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "计算机组成原理暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "计算机体系结构") != "") {
                index = courses.indexOf(contains(courses, "计算机体系结构"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("B")) {
                    check[1] = true;
                }
                String x = "计算机体系结构" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "计算机体系结构暂无数据";
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

            //规则1:自动控制理论-A, 运筹学-A--->过程控制-A, conf = 91.03%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("自动控制理论-A");
            antecedents.add("运筹学-A");
            consequents = new ArrayList<>();
            consequents.add("过程控制-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("91.03%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "自动控制理论") != "") {
                index = courses.indexOf(contains(courses, "自动控制理论"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "自动控制理论" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "自动控制理论暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "运筹学") != "") {
                index = courses.indexOf(contains(courses, "运筹学"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "运筹学" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "运筹学暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "过程控制") != "") {
                index = courses.indexOf(contains(courses, "过程控制"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "过程控制" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "过程控制暂无数据";
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


            //规则2:现代控制-A, 高等数学B(2)-A--->信号分析-A, conf = 90.27%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("大现代控制-A");
            antecedents.add("高等数学B(2)-A");
            consequents = new ArrayList<>();
            consequents.add("信号分析-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("90.27%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "现代控制") != "") {
                index = courses.indexOf(contains(courses, "现代控制"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "现代控制" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "现代控制暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "高等数学B(2)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(2)"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "高等数学B(2)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "高等数学B(2)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "信号分析") != "") {
                index = courses.indexOf(contains(courses, "信号分析"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "信号分析" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "信号分析暂无数据";
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

            //规则3:复变-A, 运筹学-A--->现代控制-A, conf = 89.66%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("复变函数-A");
            antecedents.add("运筹学-A");
            consequents = new ArrayList<>();
            consequents.add("现代控制-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("89.66%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "复变函数") != "") {
                index = courses.indexOf(contains(courses, "复变函数"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "复变函数" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "复变函数暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "运筹学") != "") {
                index = courses.indexOf(contains(courses, "运筹学"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "运筹学" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "运筹学暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "现代控制") != "") {
                index = courses.indexOf(contains(courses, "现代控制"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "现代控制" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "现代控制暂无数据";
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


            //规则4:复变-A, 运筹学-A--->模拟电子技术-A, conf = 89.66%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("复变函数-A");
            antecedents.add("运筹学-A");
            consequents = new ArrayList<>();
            consequents.add("模拟电子技术-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("89.66%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "复变函数") != "") {
                index = courses.indexOf(contains(courses, "复变函数"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "复变函数" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "复变函数暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "运筹学") != "") {
                index = courses.indexOf(contains(courses, "运筹学"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "运筹学" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "运筹学暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "模拟电子技术") != "") {
                index = courses.indexOf(contains(courses, "模拟电子技术"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "模拟电子技术" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "模拟电子技术暂无数据";
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

            //规则5:运筹学-A, 概率论-A--->过程控制-A, conf = 89.66%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("复变函数-A");
            antecedents.add("概率论与数理统计-A");
            consequents = new ArrayList<>();
            consequents.add("过程控制-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("89.66%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "复变函数") != "") {
                index = courses.indexOf(contains(courses, "复变函数"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "复变函数" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "复变函数暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "概率论") != "") {
                index = courses.indexOf(contains(courses, "概率论"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "概率论与数理统计" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "概率论与数理统计暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "过程控制") != "") {
                index = courses.indexOf(contains(courses, "过程控制"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "过程控制" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "过程控制暂无数据";
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

            //规则6:电路理论B(2)-A, 软件工程-A--->操作系统A-A, conf = 89.29%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("电路理论B(2)-A");
            antecedents.add("软件工程-A");
            consequents = new ArrayList<>();
            consequents.add("操作系统A-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("89.26%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "电路理论B(2)") != "") {
                index = courses.indexOf(contains(courses, "电路理论B(2)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "电路理论B(2)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "电路理论B(2)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "软件工程") != "") {
                index = courses.indexOf(contains(courses, "软件工程"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "软件工程" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "软件工程暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "操作系统A暂无数据";
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

            //规则7:热工理论-A, 高等数学B(1)-B--->模拟电子技术-A, conf = 83.33%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("热工理论-A");
            antecedents.add("高等数学B(1)-B");
            consequents = new ArrayList<>();
            consequents.add("模拟电子技术-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("89.33%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "热工理论") != "") {
                index = courses.indexOf(contains(courses, "热工理论"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "热工理论" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "热工理论暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "高等数学B(1)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(1)"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "高等数学B(1)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("B")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "高等数学B(1)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "模拟电子技术") != "") {
                index = courses.indexOf(contains(courses, "模拟电子技术"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "模拟电子技术" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "模拟电子技术暂无数据";
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

            //规则8:操作系统A-A, 编译-A--->软件工程-A, conf = 65.55%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("操作系统A-A");
            antecedents.add("编译技术-A");
            consequents = new ArrayList<>();
            consequents.add("软件工程-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("65.55%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "操作系统A暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "编译") != "") {
                index = courses.indexOf(contains(courses, "编译"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "编译技术" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "编译技术暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "软件工程") != "") {
                index = courses.indexOf(contains(courses, "软件工程"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "软件工程" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "软件工程暂无数据";
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

            //规则9:汇编语言-A, 计算机网络-A--->操作系统A-A, conf = 65.66%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("汇编语言-A");
            antecedents.add("计算机网络-A");
            consequents = new ArrayList<>();
            consequents.add("操作系统A-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("65.66%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "汇编") != "") {
                index = courses.indexOf(contains(courses, "汇编"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "汇编语言" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "汇编语言暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "计算机网络") != "") {
                index = courses.indexOf(contains(courses, "计算机网络"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "计算机网络" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "计算机网络暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "操作系统A暂无数据";
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

            //规则10:高等数学B(2)-B, 面向对象的程序设计-A--->操作系统A-A, conf = 62.62%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("高等数学B(2)-B");
            antecedents.add("面向对象的程序设计-A");
            consequents = new ArrayList<>();
            consequents.add("操作系统A-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("62.62%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "高等数学B(2)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(2)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("B")) {
                    check[0] = true;
                }
                String x = "高等数学B(2)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "高等数学B(2)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "面向对象") != "") {
                index = courses.indexOf(contains(courses, "面向对象"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "面向对象的程序设计" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "面向对象的程序设计暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "操作系统A暂无数据";
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

            //规则11:数据库原理-A, 汇编语言-A--->操作系统A-A, conf = 62.41%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("数据库原理-A");
            antecedents.add("汇编语言-A");
            consequents = new ArrayList<>();
            consequents.add("操作系统A-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("62.41%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "数据库原理") != "") {
                index = courses.indexOf(contains(courses, "数据库原理"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "数据库原理" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "数据库原理暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "汇编") != "") {
                index = courses.indexOf(contains(courses, "汇编"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "汇编语言" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "汇编语言暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "操作系统A暂无数据";
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

            //规则12:概率论与数理统计-A, 编译技术-A--->操作系统A-A, conf = 62.37%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("概率论与数理统计-A");
            antecedents.add("编译技术-A");
            consequents = new ArrayList<>();
            consequents.add("操作系统A-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("62.37%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "概率论与数理统计") != "") {
                index = courses.indexOf(contains(courses, "概率论与数理统计"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "概率论与数理统计" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "概率论与数理统计暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "编译") != "") {
                index = courses.indexOf(contains(courses, "编译"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "编译技术" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "编译技术暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "操作系统A暂无数据";
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

            //规则13:操作系统A-A, 汇编语言-A--->计算机网络-A, conf = 60.19%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("操作系统A-A");
            antecedents.add("汇编语言-A");
            consequents = new ArrayList<>();
            consequents.add("计算机网络-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("60.19%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "操作系统A暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "汇编") != "") {
                index = courses.indexOf(contains(courses, "汇编"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "汇编语言" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "汇编语言暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "计算机网络") != "") {
                index = courses.indexOf(contains(courses, "计算机网络"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "计算机网络" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "计算机网络暂无数据";
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

            //规则14:操作系统A-A, 数据库原理-A--->计算机网络-A, conf = 61.11%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("操作系统A-A");
            antecedents.add("汇编语言-A");
            consequents = new ArrayList<>();
            consequents.add("计算机网络-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("61.11%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "操作系统A") != "") {
                index = courses.indexOf(contains(courses, "操作系统A"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "操作系统A" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "操作系统A暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "数据库原理") != "") {
                index = courses.indexOf(contains(courses, "数据库原理"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "数据库原理" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("A")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "数据库原理暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "计算机网络") != "") {
                index = courses.indexOf(contains(courses, "计算机网络"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[2] = true;
                }
                String x = "计算机网络" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "计算机网络暂无数据";
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

            //数据显示
            AprioriAdapter aprioriAdapter = new AprioriAdapter(this, list);
            expandableListView.setAdapter(aprioriAdapter);
            ShowDialogUtil.closeProgressDialog();
            String s = "对于关联规则:操作系统A-A, 编译技术-A ---> 软件工程-A\n" +
                    "置信度:65.55%, 其解释为:\n" +
                    "在操作系统A-A, 编译技术-A的情况下有65.55%的可能软件工程-A.";
            textView.setText(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}