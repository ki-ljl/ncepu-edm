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
import com.example.NCEPU.Utils.Exam;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.ShareItem;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.adapter.ExamAdapter;

import java.util.ArrayList;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener{


    private LinearLayout ly_back_exam;
    private ImageButton ib_back_exam;
    private ExpandableListView expand_lv_exam;

    private LinearLayout ly_multiply_exam;
    private Button btn_query_exam;

    private TextView tv_info_exam;


    private Button schoolYear, semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initViews();
        setListeners();
        setHeight();
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back_exam.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back_exam.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ib_back_exam.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        ib_back_exam.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ly_multiply_exam.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_multiply_exam.setLayoutParams(linearParams);

        //button=0.0589=48dp
        linearParams = (LinearLayout.LayoutParams)schoolYear.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        schoolYear.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)semester.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        semester.setLayoutParams(linearParams);

        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)btn_query_exam.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        btn_query_exam.setLayoutParams(linearParams);
    }

    private void initViews() {
        ly_back_exam = findViewById(R.id.ly_back_exam);
        ib_back_exam = findViewById(R.id.ib_back_exam);
        ly_multiply_exam = findViewById(R.id.ly_multiply_exam);
        expand_lv_exam = findViewById(R.id.expand_lv_exam);
        btn_query_exam =  findViewById(R.id.btn_query_exam);
//        tv_info_exam = findViewById(R.id.tv_info_exam);

        schoolYear = findViewById(R.id.school_year_exam);
        semester = findViewById(R.id.semester_exam);

        ib_back_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setListeners() {
        schoolYear.setOnClickListener(this);
        semester.setOnClickListener(this);

        btn_query_exam.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_exam:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, schoolYear)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("24-25", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("23-24", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("22-23", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("21-22", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("20-21", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("19-20", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("18-19", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("17-18", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("16-17", Color.BLACK, 0xffEEEEFF))
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

            case R.id.semester_exam:
                @SuppressLint("WrongConstant") final FlipShareView shareSemester = new FlipShareView.Builder(this, semester)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("1", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2", Color.BLACK, 0xffEEEEFF))
                        .setSeparateLineColor(0x60000000).setItemDuration(0)
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


            case R.id.btn_query_exam:
                String year = schoolYear.getText().toString();
                year = "20" + year;
                String semester_ = semester.getText().toString();
                if(year.equals("学年")) {
                    ToastUtil.showMessage(this,"请选择学年！");
                }else if(semester_.equals("学期")) {
                    ToastUtil.showMessage(this, "请选择学期！");
                }else {
                    //开始查询考试信息并显示
                    try {
                        if(year.equals("全部")) {
                            year = "";
                        }
                        if(semester_.equals("全部")) {
                            semester_ = "";
                        }
                        ArrayList<Exam> list = connectJWGL.getExamInformation(year, semester_);
                        if (list.size() == 0) {
                            ToastUtil.showMessage(this, "没有查到记录！");
                        }else {
                            for(int i = 0; i < list.size(); ++i) {
                                list.get(i).setXn(year);
                                list.get(i).setXq(semester_);
                            }
                            expand_lv_exam.setAdapter(new ExamAdapter(this, list));
                            ShowDialogUtil.closeProgressDialog();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}