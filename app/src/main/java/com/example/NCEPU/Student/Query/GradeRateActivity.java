package com.example.NCEPU.Student.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShareItem;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class GradeRateActivity extends AppCompatActivity implements View.OnClickListener {

    //private PieChartView mPieChartView;
    private Button btn_query;
    private Button school_year, term, nature;
    private ImageButton ib_back;
    private LinearLayout ly_back, ly_multiply;
    private PieChart pieChart;

    //private int[] colors = {Color.pi,R.color.PIE_ORANGE,R.color.PIE_YELLOW,R.color.PIE_GREEN,R.color.PIE_BLUE};//颜色集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_rate);
        init();
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
        linearParams = (LinearLayout.LayoutParams)school_year.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        school_year.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)term.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        term.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)nature.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        nature.setLayoutParams(linearParams);

        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)btn_query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        btn_query.setLayoutParams(linearParams);
    }

    public void init() {
        pieChart = findViewById(R.id.pie_chart);
        pieChart.setNoDataText("No chart data available.");
        btn_query = findViewById(R.id.btn_query_rate);
        school_year = findViewById(R.id.school_year_rate);
        term = findViewById(R.id.semester_rate);
        nature = findViewById(R.id.nature_rate);
        ib_back = findViewById(R.id.ib_back_rate);
        ly_back = findViewById(R.id.ly_back_rate);
        ly_multiply = findViewById(R.id.ly_multiply_rate);
        //String[] label = {"90-100", "80-90", "70-80", "60-70", "60以下"};
        ib_back.setOnClickListener(v -> onBackPressed());

//        float[] data = new float[]{10.0f, 10.0f, 10.0f, 10.0f, 10.0f};
        btn_query.setOnClickListener(this);
        school_year.setOnClickListener(this);
        term.setOnClickListener(this);
        nature.setOnClickListener(this);
    }

    private void initPieChartView(float[] data, String text1, String text2) {

//        String[] name = new String[]{"优秀", "良好", "中等","及格", "不及格"};
        ArrayList<String> name = new ArrayList<>(Arrays.asList("优秀", "良好", "中等","及格", "不及格"));
        List<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<String> rate = new ArrayList<>();
        //计算百分比
        for(int i = 0; i < 5; i++) {
            DecimalFormat df = new DecimalFormat("0.00%");
            String decimal = df.format(data[i] / (data[0] + data[1] + data[2] + data[3] + data[4]));
            PieEntry pieEntry = new PieEntry(data[i] / (data[0] + data[1] + data[2] + data[3] + data[4]) * 100, name.get(i));
            pieEntries.add(pieEntry);
            rate.add(decimal);
            //name[i] = name[i] + decimal;
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#6BE61A"));
        colors.add(Color.parseColor("#4474BB"));
        colors.add(Color.parseColor("#AA7755"));
        colors.add(Color.parseColor("#BB5C44"));
        colors.add(Color.parseColor("#E61A1A"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setSliceSpace(1f); //设置个饼状图之间的距离
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setValueTextColor(colors.get(2));
        pieDataSet.setDrawValues(true);

        //这一段代码就是实现加一个横线然后将模块的数据放在外面的效果
        /////////////////////////////////////////
        pieDataSet.setValueLinePart1OffsetPercentage(80.f);
        pieDataSet.setValueLinePart1Length(0.5f);
        pieDataSet.setValueLinePart2Length(0.2f);
        pieDataSet.setValueLineColor(colors.get(4));
        //当值显示在界面外面的时候是否允许改变量行长度
        pieDataSet.setValueLineVariableLength(false);
        //设置线的宽度
        pieDataSet.setValueLineWidth(2);
        //设置项X值拿出去
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //设置将Y轴的值拿出去
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(text1 + "\n" + text2);
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextColor(Color.parseColor("#3CC4C4"));
        pieChart.setEntryLabelTextSize(0);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(12);
        //legend.setXEntrySpace(12);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(e == null) {
                    return;
                }
                if(pieEntries.get(0).getValue() == e.getY()) {
                    Toast.makeText(GradeRateActivity.this, "占比" + rate.get(0), Toast.LENGTH_SHORT).show();
//                    ToastUtil.showMessage(GradeRateActivity.this, "占比" + rate.get(0));
                }else if(pieEntries.get(1).getValue() == e.getY()) {
                   Toast.makeText(GradeRateActivity.this, "占比" + rate.get(1), Toast.LENGTH_SHORT).show();
//                    ToastUtil.showMessage(GradeRateActivity.this, "占比" + rate.get(1));
                }else if(pieEntries.get(2).getValue() == e.getY()) {
                    Toast.makeText(GradeRateActivity.this, "占比" + rate.get(2), Toast.LENGTH_SHORT).show();
//                    ToastUtil.showMessage(GradeRateActivity.this, "占比" + rate.get(2));
                }else if(pieEntries.get(3).getValue() == e.getY()) {
                    Toast.makeText(GradeRateActivity.this, "占比" + rate.get(3), Toast.LENGTH_SHORT).show();
//                    ToastUtil.showMessage(GradeRateActivity.this, "占比" + rate.get(3));
                }else if(pieEntries.get(4).getValue() == e.getY()) {
                    Toast.makeText(GradeRateActivity.this, "占比" + rate.get(4), Toast.LENGTH_SHORT).show();
//                    ToastUtil.showMessage(GradeRateActivity.this, "占比" + rate.get(4));
                }
            }

            @Override
            public void onNothingSelected() {
                ToastUtil.showMessage(GradeRateActivity.this, "请不要频繁点击!");
            }
        });
        pieChart.animateXY(1000, 1000);
        pieChart.setExtraBottomOffset(20);
        pieChart.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_rate:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, school_year)
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
                        school_year.setText(shareYear.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.semester_rate:
                @SuppressLint("WrongConstant") final FlipShareView shareSemester = new FlipShareView.Builder(this, term)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("1", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2", Color.BLACK, 0xffEEEEFF))
                        .setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareSemester.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        term.setText(shareSemester.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.nature_rate:
                @SuppressLint("WrongConstant") final FlipShareView shareCourse = new FlipShareView.Builder(this, nature)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("实践课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("校选修课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("专选课", Color.BLACK, 0xffEEEEFF))
                        .setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareCourse.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        nature.setText(shareCourse.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.btn_query_rate:
                String year = school_year.getText().toString();
                String semester_ = term.getText().toString();
                String course_nature = nature.getText().toString();
                String text1 = year + "-" + semester_;
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
                            float []data = {0, 0, 0, 0, 0};
                            for(int i = 0; i < list.size(); i++) {
                                String gpa = list.get(i).getGpa();
                                float value = Float.parseFloat(gpa) * 10 + 50;
                                if(value >= 90) {
                                    data[0]++;
                                }else if(value >= 80) {
                                    data[1]++;
                                }else if(value >= 70) {
                                    data[2]++;
                                }else if(value >= 60) {
                                    data[3]++;
                                }else {
                                    data[4]++;
                                }
                            }
//                            String[] name = new String[]{"优秀", "良好", "中等","及格", "不及格"};
//                            mPieChartView.setData(data, name);
//                            mPieChartView.startAnimation(2000);
                            initPieChartView(data, text1, course_nature);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}