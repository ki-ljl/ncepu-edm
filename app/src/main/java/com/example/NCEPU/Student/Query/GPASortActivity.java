package com.example.NCEPU.Student.Query;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
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
import com.example.NCEPU.Utils.GPASortProgressView;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.MySQLUtil;
import com.example.NCEPU.Utils.ShareItem;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class GPASortActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout ly_back;
    private ImageButton ib_back;

    private LinearLayout ly_multiply;
    private Button btn_query;

    private TextView tv_info;
    private Button schoolYear, semester, course;
    private GPASortProgressView progressView;
    private SharedPreferences sharedPreferences = null;
    private TextView tv_gpa;

    private int totalRank = 250;
    private int currentRank = 0;
    private long speed = 1000;

    private float self_gpa = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_a_sort);
        initViews();
        initProgressView();
        setListeners();
    }

    private void initViews() {
        ly_back = findViewById(R.id.ly_back_gpa_sort);
        ib_back = findViewById(R.id.ib_back_gpa_sort);
        ly_multiply = findViewById(R.id.ly_multiply_gpa_sort);
        btn_query = findViewById(R.id.btn_query_gpa_sort);
//        tv_info = findViewById(R.id.tv_info_gpa_sort);
        schoolYear = findViewById(R.id.school_year_gpa_sort);
        semester = findViewById(R.id.semester_gpa_sort);
        course = findViewById(R.id.course_gpa_sort);
        progressView = findViewById(R.id.circleProgress_gpa_sort);
        tv_gpa = findViewById(R.id.tv_gpa);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        //获取专业总人数
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String major = sharedPreferences.getString("major", "");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MySQLUtil mySQLUtil = new MySQLUtil(GPASortActivity.this);
                mySQLUtil.getConnection("cce-18");
                totalRank = mySQLUtil.getNum(major);
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {
        schoolYear.setOnClickListener(this);
        semester.setOnClickListener(this);
        course.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }

    private void initProgressView() {

        progressView.setTotalRank(totalRank);
        /**
         * 进度条从0到指定数字的动画
         * 除了startAnimator1()方法中用的ValueAnimator.ofInt()，我们还有
         * ofFloat()、ofObject()这些生成器的方法;
         * 我们可以通过ofObject()去实现自定义的数值生成器
         */
        ValueAnimator animator = ValueAnimator.ofInt(0, currentRank);
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
//                float current = (float) valueAnimator.getAnimatedValue();
                progressView.setmCurrent(currentRank);
            }
        });
        animator.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_gpa_sort:
                final FlipShareView shareYear = new FlipShareView.Builder(this, schoolYear)
                        .addItem(new ShareItem("全部", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2021-2022", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2020-2021", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2019-2020", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2018-2019", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2017-2018", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2016-2017", Color.WHITE, 0xffcc9999))
                        .setBackgroundColor(0x60000000).setItemDuration(100)
                        .setAnimType(FlipShareView.TYPE_SLIDE)
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

            case R.id.semester_gpa_sort:
                final FlipShareView shareSemester = new FlipShareView.Builder(this, semester)
                        .addItem(new ShareItem("全部", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("1", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("2", Color.WHITE, 0xffcc9999))
                        .setSeparateLineColor(0x60000000).setItemDuration(100)
                        .setAnimType(FlipShareView.TYPE_SLIDE)
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

            case R.id.course_gpa_sort:
                final FlipShareView shareCourse = new FlipShareView.Builder(this, course, 89)
                        .addItem(new ShareItem("必修", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("必修+实践", Color.WHITE, 0xffcc9999))
                        .addItem(new ShareItem("必修-体育", Color.WHITE, 0xffcc9999))
                        .setItemDuration(100)
                        .setSeparateLineColor(Color.BLACK)
                        .setAnimType(FlipShareView.TYPE_SLIDE)
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

            case R.id.btn_query_gpa_sort:
                String year = schoolYear.getText().toString();
                String semester_ = semester.getText().toString();
                String course_nature = course.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        MySQLUtil mySQLUtil = new MySQLUtil(GPASortActivity.this);
                        mySQLUtil.getConnection("cce-18");
                        String self_id = sharedPreferences.getString("id", "");
                        String self_major = sharedPreferences.getString("major", "");
                        ArrayList<String> map = mySQLUtil.getGPARank(year, semester_, course_nature, self_id, self_major);
                        if(map == null) {
                            runOnUiThread(() -> {
                                String text = "GPA:0.00";
                                tv_gpa.setText(text);
                                ToastUtil.showMessage(GPASortActivity.this, "暂时无法查询！");
                            });
                        }else {
                            String rank = map.get(0);
                            String gpa = map.get(1);
                            currentRank = Integer.parseInt(rank);
                            self_gpa = Float.parseFloat(gpa);
                        }
                    }
                });
                try {
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initProgressView();
                DecimalFormat df   = new DecimalFormat(".00");
                String gpa_value = df.format(self_gpa);
                String text = "GPA:" + gpa_value;
                tv_gpa.setText(text);
                break;
        }
    }
}