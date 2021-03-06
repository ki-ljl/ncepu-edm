package com.example.NCEPU.Student.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.GPAProgressView;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShareItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class GPAActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ly_back;
    private ImageButton ib_back;

    private LinearLayout ly_multiply;
    private Button btn_query;

    private TextView tv_info;
    private Button schoolYear, semester, course;
    private GPAProgressView progressView;

    private double totalGPA = 5.0;
    private double currentGPA = 0;
    private long speed = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_a);
        initViews();
        initProgressView();
        setListeners();
        setHeight();
    }

    private void initViews() {
        ly_back = findViewById(R.id.ly_back_gpa);
        ib_back = findViewById(R.id.ib_back_gpa);
        ly_multiply = findViewById(R.id.ly_multiply_gpa);
        btn_query = findViewById(R.id.btn_query_gpa);
//        tv_info = findViewById(R.id.tv_info_gpa);
        schoolYear = findViewById(R.id.school_year_gpa);
        semester = findViewById(R.id.semester_gpa);
        course = findViewById(R.id.course_gpa);
        progressView = findViewById(R.id.circleProgress_gpa);
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

        //????????????=48dp=0.0744
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

    private void initProgressView() {
        progressView.setTotalScore(totalGPA);
        /**
         * ????????????0????????????????????????
         * ??????startAnimator1()???????????????ValueAnimator.ofInt()???????????????
         * ofFloat()???ofObject()????????????????????????;
         * ??????????????????ofObject()????????????????????????????????????
         */
        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) currentGPA);
        animator.setDuration(speed);
        /**
         *  Interpolators  ???????????????????????????????????????????????????
         *  LinearInterpolator  ??????
         */
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                /**
                 * ??????????????????????????????????????????????????????
                 * ???ValueAnimator???????????????????????????
                 *
                 * ????????????getAnimatedValue()???????????????????????????????????????Value???
                 * */
                //float current = (float) valueAnimator.getAnimatedValue();
                DecimalFormat df   = new DecimalFormat(".00");
                progressView.setmCurrent(Double.parseDouble(df.format(currentGPA)));
            }
        });
        animator.start();

    }


    private double getGPA(ArrayList<Grade> list, String nature) {
        double total = 0.0;
        double total_num = 0.0;
        list = cut(list, nature);
        for(int i = 0; i < list.size(); i++) {
            double credit = Double.parseDouble(list.get(i).getCredit());
            double mark = Double.parseDouble(list.get(i).getGpa()) * 10 + 50;
            total_num += credit;
            total += credit * mark;
        }
        double gpa = (total / total_num - 50) / 10;
        return gpa;
    }


    public ArrayList<Grade> cut(ArrayList<Grade> list, String nature) {
        if(nature.equals("??????")) {
            return list;
        }
        ArrayList<String> list_nature = new ArrayList<>();
        if(nature.length() > 2) {
            if(nature.equals("????????????")) {
                list_nature.add("?????????");
                list_nature.add("?????????");
                list_nature.add("?????????");
            }else if(nature.equals("??????-??????")) {
                list_nature.add("?????????");
            }else {
                String []natures = nature.split("\\+");
                for(int i = 0; i < natures.length; i++) {
                    list_nature.add(natures[i] + "???");
                }
            }
        }else {
            list_nature.add(nature + "???");
        }
        Iterator<Grade> it = list.iterator();
        while(it.hasNext()) {
            Grade grade = it.next();
            String nature_ = grade.getCourse_nature();
            if(!list_nature.contains(nature_)) {
                it.remove();
            }
        }
        if(nature.equals("??????-??????")) {
            Iterator<Grade> its = list.iterator();
            while(its.hasNext()) {
                Grade grade = its.next();
                String name = grade.getCourse_name();
                if(name.contains("??????")) {
                    System.out.println("????????????");
                    its.remove();
                }
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_gpa:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, schoolYear)
                        .addItem(new ShareItem("??????", Color.BLACK, 0xffEEEEFF))
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

            case R.id.semester_gpa:
                @SuppressLint("WrongConstant") final FlipShareView shareSemester = new FlipShareView.Builder(this, semester)
                        .addItem(new ShareItem("??????", Color.BLACK, 0xffEEEEFF))
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

            case R.id.course_gpa:
                @SuppressLint("WrongConstant") final FlipShareView shareCourse = new FlipShareView.Builder(this, course, 89)
                        .addItem(new ShareItem("??????", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("??????", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("??????+??????", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("??????+??????", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("??????-??????", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("????????????", Color.BLACK, 0xffEEEEFF))
                        .setItemDuration(0)
                        .setSeparateLineColor(Color.BLACK)
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

            case R.id.btn_query_gpa:
                String year = schoolYear.getText().toString();
                year = "20" + year;
                String semester_ = semester.getText().toString();
                String course_nature = course.getText().toString();
                if(year.equals("??????")) {
                    ToastUtil.showMessage(this, "??????????????????");
                }else if(semester_.equals("??????")) {
                    ToastUtil.showMessage(this, "??????????????????");
                }else if(course_nature.equals("????????????")) {
                    ToastUtil.showMessage(this, "????????????????????????");
                }else {
                    //???????????????????????????
                    try {
                        if(year.equals("??????")) {
                            year = "";
                        }else {
                            year = year.substring(0, 4);
                        }
                        if(semester_.equals("??????")) {
                            semester_ = "";
                        }
                        ArrayList<Grade> list = connectJWGL.getStudentGrade(year, semester_, "??????");
                        if (list.size() == 0) {
                            ToastUtil.showMessage(this, "?????????????????????");
                        }else {
                            currentGPA = getGPA(list, course_nature);
                            initProgressView();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}