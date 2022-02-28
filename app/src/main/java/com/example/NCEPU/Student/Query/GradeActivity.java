package com.example.NCEPU.Student.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShareItem;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.adapter.GradeAdapter;

import java.util.ArrayList;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;
import static com.example.NCEPU.StudentMainActivity.px2dip;

public class GradeActivity extends AppCompatActivity implements View.OnClickListener{


    private LinearLayout ly_back;
    private ImageButton ib_back;
    private ExpandableListView expand_lv;

    private LinearLayout ly_multiply;
    private Button btn_query;

    private TextView tv_info;


    private Button schoolYear, semester, course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        initViews();
        setListeners();
        setHeight();
    }

    private void initViews() {
        ly_back = findViewById(R.id.ly_back);
        ib_back = findViewById(R.id.ib_back);
        ly_multiply = findViewById(R.id.ly_multiply);
        expand_lv = findViewById(R.id.expand_lv);
        btn_query =  findViewById(R.id.btn_query);
//        tv_info = findViewById(R.id.tv_info);

        schoolYear = findViewById(R.id.school_year);
        semester = findViewById(R.id.semester);
        course = findViewById(R.id.course);

        ib_back.setOnClickListener(v -> onBackPressed());
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ib_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        ib_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ly_multiply.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_multiply.setLayoutParams(linearParams);

        //button=0.0589=48dp
        linearParams = (LinearLayout.LayoutParams)schoolYear.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        schoolYear.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)semester.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        semester.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)course.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        course.setLayoutParams(linearParams);

        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)btn_query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        btn_query.setLayoutParams(linearParams);
    }

    private void setListeners() {
        schoolYear.setOnClickListener(this);
        semester.setOnClickListener(this);
        course.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, schoolYear)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2024-2025", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2023-2024", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2022-2023", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2021-2022", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2020-2021", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2019-2020", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2018-2019", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2017-2018", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2016-2017", Color.BLACK, 0xffEEEEFF))
//                        .setBackgroundColor(0xffccc)
                        .setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareYear.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        schoolYear.setText(shareYear.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.semester:
                @SuppressLint("WrongConstant") final FlipShareView shareSemester = new FlipShareView.Builder(this, semester)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("1", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2", Color.BLACK, 0xffEEEEFF))
//                        .setSeparateLineColor(0x60000000)
                        .setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareSemester.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        semester.setText(shareSemester.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.course:
                @SuppressLint("WrongConstant") final FlipShareView shareCourse = new FlipShareView.Builder(this, course)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("实践课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("校选修课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("专选课", Color.BLACK, 0xffEEEEFF))
                        //.setBackgroundColor(0x60000000)
                        .setItemDuration(0)
//                        .setSeparateLineColor(Color.BLACK)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareCourse.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        course.setText(shareCourse.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.btn_query:
                String year = schoolYear.getText().toString();
                String semester_ = semester.getText().toString();
                String course_nature = course.getText().toString();
//                File file = new File("D:/schedule.txt");
//                try {
//                    if(file.createNewFile()) {
//                        System.out.println("hhh");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                if(year.equals("学年")) {
                    ToastUtil.showMessage(this, "请选择学年！");
                }else if(semester_.equals("学期")) {
                    ToastUtil.showMessage(this, "请选择学期！");
                }else if(course_nature.equals("课程性质")) {
                    ToastUtil.showMessage(this, "请选择课程性质！");
                }else {
                    //开始查询成绩并显示
                    try {
                        if(year.equals("全部")) {
                            year = "";
                        }else {
                            year = year.substring(0, 4);
                        }
                        if(semester_.equals("全部")) {
                            semester_ = "";
                        }
                        ArrayList<Grade> list = connectJWGL.getStudentGrade(year, semester_, course_nature);
                        if (list.size() == 0) {
                            ToastUtil.showMessage(this, "没有查到记录！");
                        }else {
                            for(int i = 0; i < list.size(); ++i) {
                                list.get(i).setXn(year);
                                list.get(i).setXq(semester_);
                            }
                            expand_lv.setAdapter(new GradeAdapter(this, list));
                            ShowDialogUtil.closeProgressDialog();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}