package com.example.NCEPU.Student.Query;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.MajorSortProgressView;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.MySQLUtil;
import com.example.NCEPU.Utils.ShareItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;


public class MajorGPASortActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ly_back;
    private ImageButton ib_back;

    private LinearLayout ly_multiply;
    private Button btn_query;

    private TextView tv_info;
    private Button schoolYear, semester, course;
    private MajorSortProgressView progressView;

    private double totalGPA = 5.0;
    private double currentGPA = 0;
    private int majorRank = 0;
    private long speed = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_g_p_a_sort);
        initViews();
        initProgressView();
        setListeners();
    }

    private void initViews() {
        ly_back = findViewById(R.id.ly_back_major_sort);
        ib_back = findViewById(R.id.ib_back_major_sort);
        ly_multiply = findViewById(R.id.ly_multiply_major_sort);
        btn_query = findViewById(R.id.btn_query_major_sort);
//        tv_info = findViewById(R.id.tv_info_major_sort);
        schoolYear = findViewById(R.id.school_year_major_sort);
        semester = findViewById(R.id.semester_major_sort);
        course = findViewById(R.id.course_major_sort);
        progressView = findViewById(R.id.circleProgress_major_sort);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setHeight();
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
        progressView.setMajorRank(majorRank);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_major_sort:
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

            case R.id.semester_major_sort:
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

            case R.id.course_major_sort:
                @SuppressLint("WrongConstant") final FlipShareView shareCourse = new FlipShareView.Builder(this, course, 89)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修+实践", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修+专选", Color.BLACK, 0xffEEEEFF))
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

            case R.id.btn_query_major_sort:
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
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                    String idMyself = sharedPreferences.getString("id", "");
                    String major = sharedPreferences.getString("major", "");
                    String finalYear = year;
                    String finalSemester_ = semester_;
                    Thread thread = new Thread(() -> {
                        MySQLUtil mySQLUtil = new MySQLUtil(this);
                        mySQLUtil.getConnection("cce-18");
                        ArrayList<String> res = null;
                        res = mySQLUtil.getMajorGpaRank(idMyself, major, finalYear, finalSemester_, course_nature);
                        if(res == null) {
                            runOnUiThread(() -> {
                                ToastUtil.showMessage(getApplicationContext(), "暂无成绩信息!");
                                currentGPA = 0;
                                majorRank = 0;
                                initProgressView();
                            });
                        }else {
                            ArrayList<String> finalRes = res;
                            runOnUiThread(() -> {
                                currentGPA = Double.parseDouble(finalRes.get(0));
                                majorRank = Integer.parseInt(finalRes.get(1));
                                initProgressView();
                            });
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}