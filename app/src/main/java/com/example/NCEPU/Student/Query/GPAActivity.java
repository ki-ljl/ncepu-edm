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

    private void initProgressView() {
        progressView.setTotalScore(totalGPA);
        /**
         * 进度条从0到指定数字的动画
         * 除了startAnimator1()方法中用的ValueAnimator.ofInt()，我们还有
         * ofFloat()、ofObject()这些生成器的方法;
         * 我们可以通过ofObject()去实现自定义的数值生成器
         */
        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) currentGPA);
        animator.setDuration(speed);
        /**
         *  Interpolators  插值器，用来控制具体数值的变化规律
         *  LinearInterpolator  线性
         */
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                /**
                 * 通过这样一个监听事件，我们就可以获取
                 * 到ValueAnimator每一步所产生的值。
                 *
                 * 通过调用getAnimatedValue()获取到每个时间因子所产生的Value。
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
        if(nature.equals("全部")) {
            return list;
        }
        ArrayList<String> list_nature = new ArrayList<>();
        if(nature.length() > 2) {
            if(nature.equals("除去校选")) {
                list_nature.add("必修课");
                list_nature.add("实践课");
                list_nature.add("专选课");
            }else if(nature.equals("必修-体育")) {
                list_nature.add("必修课");
            }else {
                String []natures = nature.split("\\+");
                for(int i = 0; i < natures.length; i++) {
                    list_nature.add(natures[i] + "课");
                }
            }
        }else {
            list_nature.add(nature + "课");
        }
        Iterator<Grade> it = list.iterator();
        while(it.hasNext()) {
            Grade grade = it.next();
            String nature_ = grade.getCourse_nature();
            if(!list_nature.contains(nature_)) {
                it.remove();
            }
        }
        if(nature.equals("必修-体育")) {
            Iterator<Grade> its = list.iterator();
            while(its.hasNext()) {
                Grade grade = its.next();
                String name = grade.getCourse_name();
                if(name.contains("体育")) {
                    System.out.println("体育删除");
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

            case R.id.course_gpa:
                @SuppressLint("WrongConstant") final FlipShareView shareCourse = new FlipShareView.Builder(this, course, 89)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修+实践", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修+专选", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修-体育", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("除去校选", Color.BLACK, 0xffEEEEFF))
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
                String semester_ = semester.getText().toString();
                String course_nature = course.getText().toString();
                if(year.equals("学年")) {
                    ToastUtil.showMessage(this, "请选择学年！");
                }else if(semester_.equals("学期")) {
                    ToastUtil.showMessage(this, "请选择学期！");
                }else if(course_nature.equals("查询性质")) {
                    ToastUtil.showMessage(this, "请选择查询性质！");
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
                        ArrayList<Grade> list = connectJWGL.getStudentGrade(year, semester_, "全部");
                        if (list.size() == 0) {
                            ToastUtil.showMessage(this, "没有查到记录！");
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