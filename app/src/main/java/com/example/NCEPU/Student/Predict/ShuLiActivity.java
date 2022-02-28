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

public class ShuLiActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_shuli);
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
        ly_back = findViewById(R.id.ly_back_apriori);
        back = findViewById(R.id.ib_back_apriori);
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        expandableListView = findViewById(R.id.expand_shuli);
        textView = findViewById(R.id.shuli_show);
        query = findViewById(R.id.btn_query_shuli);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ShuLiActivity.this);
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
                        ToastUtil.showMessage(ShuLiActivity.this, "加载完成!");
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
            //规则1:离散数学-C--->线性代数-C, conf = 60.10%
            Apriori apriori = new Apriori();
            ArrayList<String> antecedents = new ArrayList<>();
            antecedents.add("离散数学-C");
            ArrayList<String> consequents = new ArrayList<>();
            consequents.add("线性代数-C");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("60.10%");
            //寻找成绩
            ArrayList<String> preCourses = new ArrayList<>();
            ArrayList<String> backCourses = new ArrayList<>();
            String mark;
            String convert;
            int index;
            boolean flag1 = false, flag2 = false;
            boolean []check = {false, false, false, false, false, false, false, false};
            if(contains(courses, "离散数学") != "") {
                index = courses.indexOf(contains(courses, "离散数学"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[0] = true;
                }
                String x = "离散数学" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "离散数学暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "线性代数") != "") {
                index = courses.indexOf(contains(courses, "线性代数"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[1] = true;
                }
                String x = "线性代数" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "线性代数暂无数据";
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

            //规则2:大学物理(1)-D, 线性代数-C--->概率论-D, conf = 77.83%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("大学物理(1)-D");
            antecedents.add("线性代数-C");
            consequents = new ArrayList<>();
            consequents.add("概率论与数理统计-D");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("77.83%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "大学物理(1)") != "") {
                index = courses.indexOf(contains(courses, "大学物理(1)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("D")) {
                    check[0] = true;
                }
                String x = "大学物理(1)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "大学物理(1)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "线性代数") != "") {
                index = courses.indexOf(contains(courses, "线性代数"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "线性代数" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("C")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "线性代数暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "概率论与数理统计") != "") {
                index = courses.indexOf(contains(courses, "概率论与数理统计"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("D")) {
                    check[2] = true;
                }
                String x = "概率论与数理统计" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "概率论与数理统计暂无数据";
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

            //规则3:高等数学B(2)-A--->概率论-A, conf = 61.10%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("高等数学B(2)-A");
            consequents = new ArrayList<>();
            consequents.add("概率论与数理统计-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("61.10%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "高等数学B(2)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(2)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "高等数学B(2)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "高等数学B(2)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "概率论") != "") {
                index = courses.indexOf(contains(courses, "概率论"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[1] = true;
                }
                String x = "概率论与数理统计" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "概率论与数理统计暂无数据";
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

            //规则4:大学物理(1)-A--->大学物理(2)-A, conf = 68.75%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("大学物理(1)-A");
            consequents = new ArrayList<>();
            consequents.add("大学物理(2)-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("68.75%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "大学物理(1)") != "") {
                index = courses.indexOf(contains(courses, "大学物理(1)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "大学物理(1)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "大学物理(1)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "大学物理(2)") != "") {
                index = courses.indexOf(contains(courses, "大学物理(2)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[1] = true;
                }
                String x = "大学物理(2)" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "大学物理(2)暂无数据";
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

            //规则5:离散数学-C--->高等数学B(1)-C, conf = 61.54%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("离散数学-C");
            consequents = new ArrayList<>();
            consequents.add("高等数学B(1)-C");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("61.54%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "离散数学") != "") {
                index = courses.indexOf(contains(courses, "离散数学"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[0] = true;
                }
                String x = "离散数学" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "离散数学暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "高等数学B(1)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(1)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[1] = true;
                }
                String x = "高等数学B(1)" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "高等数学B(1)暂无数据";
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

            //规则6:高等数学B(2)-A--->复变函数-A, conf = 62.64%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("高等数学B(2)-A");
            consequents = new ArrayList<>();
            consequents.add("复变函数-A");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("62.64%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "高等数学B(2)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(2)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "高等数学B(2)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "高等数学B(2)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "复变函数") != "") {
                index = courses.indexOf(contains(courses, "复变函数"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[1] = true;
                }
                String x = "复变函数" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "复变函数暂无数据";
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

            //规则7:高等数学B(1)-D, 高等数学B(2)-C--->概率论与数理统计-C, conf = 60.00%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("高等数学B(1)-D");
            antecedents.add("高等数学B(2)-C");
            consequents = new ArrayList<>();
            consequents.add("概率论与数理统计-C");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("60.00%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "高等数学B(1)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(1)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("D")) {
                    check[0] = true;
                }
                String x = "高等数学B(1)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "高等数学B(1)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "高等数学B(2)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(2)"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "高等数学B(2)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("C")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "高等数学B(2)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "概率论与数理统计") != "") {
                index = courses.indexOf(contains(courses, "概率论与数理统计"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[2] = true;
                }
                String x = "概率论与数理统计" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "概率论与数理统计暂无数据";
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

            //规则8:离散数学-A, 高等数学B(1)-C--->高等数学B(2)-C, conf = 60.00%
            apriori = new Apriori();
            antecedents = new ArrayList<>();
            antecedents.add("离散数学-A");
            antecedents.add("高等数学B(1)-C");
            consequents = new ArrayList<>();
            consequents.add("高等数学B(2)-C");
            apriori.setAntecedents(antecedents);
            apriori.setConsequents(consequents);
            apriori.setConf("60.00%");
            //寻找成绩
            preCourses = new ArrayList<>();
            backCourses = new ArrayList<>();
            if(contains(courses, "离散数学") != "") {
                index = courses.indexOf(contains(courses, "离散数学"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("A")) {
                    check[0] = true;
                }
                String x = "离散数学" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
            }else {
                flag1 = true;
                String x = "离散数学暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "高等数学B(1)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(1)"));
                mark = grades_list.get(index).getGpa();
                convert = convert(mark);
                String grade = grades_list.get(index).getMark();
                String x = "高等数学B(1)" + "-" + convert + "(" + grade + ")";
                preCourses.add(x);
                if(convert.equals("C")) {
                    check[1] = true;
                }
            }else {
                flag1 = true;
                String x = "高等数学B(1)暂无数据";
                preCourses.add(x);
            }
            if(contains(courses, "高等数学B(2)") != "") {
                index = courses.indexOf(contains(courses, "高等数学B(2)"));
                mark = grades_list.get(index).getGpa();
                String grade = grades_list.get(index).getMark();
                convert = convert(mark);
                if(convert.equals("C")) {
                    check[2] = true;
                }
                String x = "高等数学B(2)" + "-" + convert + "(" + grade + ")";
                backCourses.add(x);
            }else {
                flag2 = true;
                String x = "高等数学B(2)暂无数据";
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
            String s = "对于关联规则:离散数学-C ---> 线性代数-C\n" +
                    "置信度:60.10%, 其解释为:\n" +
                    "在离散数学-C的情况下有60.10%的可能线性代数-C.";
            textView.setText(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}