package com.example.NCEPU.Student.Query;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.MyGridView;
import com.example.NCEPU.Utils.MySQLUtil;
import com.example.NCEPU.adapter.GridViewSortAdapter;
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

public class SingleSubjectMajorActivity extends AppCompatActivity {

    private Button query;
    private MySQLUtil mySQLUtil;
    private AutoCompleteTextView completeTextView;
    private ImageButton back;
    private MyGridView sort;
    PieChart pieChart;
    ArrayList<String> course = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_subject_major);
        query = findViewById(R.id.btn_query_major);
        completeTextView = findViewById(R.id.auto_comp_major);
        sort = findViewById(R.id.gv_sort);
        pieChart = findViewById(R.id.pie_chart_major);
        back = findViewById(R.id.ib_back_major);
        back.setOnClickListener(v -> onBackPressed());
        findCourse();  //课程名称
        String[] courses = course.toArray(new String[course.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.auto_list, courses);
        completeTextView.setAdapter(adapter);
        query.setOnClickListener(v -> {
            Query();
        });
    }

    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     * @param field  保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getSharedPreferences("single-analyze", Context.MODE_PRIVATE);
        String longhistory = sp.getString(field, "nothing");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Query() {
        //开始查询
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String major = sharedPreferences.getString("major", "");
        String id = sharedPreferences.getString("id", "");
        String course_name = completeTextView.getText().toString();
        if(course_name.equals("")) {
            ToastUtil.showMessage(this, "请选择课程!");
        }else {
            Thread thread = new Thread(() -> {
                MySQLUtil mySQLUtil = new MySQLUtil(SingleSubjectMajorActivity.this);
                mySQLUtil.getConnection("cce-18");
                ArrayList<String> res = null;
                res = mySQLUtil.getRank(course_name, major, id, false);
                if(res == null) {
                    runOnUiThread(() -> ToastUtil.showMessage(SingleSubjectMajorActivity.this, "该课程不存在!"));
                }else {
                    ArrayList<String> finalRes = res;
                    runOnUiThread(() -> {
                        initViews(finalRes, course_name, major);
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

    private void initViews(ArrayList<String> res, String course_name, String major) {
        //开始展示
        String text = "班级前五\n";
        ArrayList<String> array = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            array.add(res.get(i));
        }
        String avg = res.get(10);
        String[] x = array.toArray(new String[array.size()]);
        GridViewSortAdapter gridViewSortAdapter = new GridViewSortAdapter(this, x);
        sort.setAdapter(gridViewSortAdapter);
        float[] data = {0, 0, 0, 0, 0};
        for(int i = 0; i < 5; i++) {
            data[i] = Float.parseFloat(res.get(11 + i));
        }
        initPieChartView(data, course_name, major, avg);
    }

    private void initPieChartView(float[] data, String text1, String text2, String avg) {

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
        pieDataSet.setValueTextSize(11f);
        pieDataSet.setDrawValues(true);

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
        pieChart.setCenterTextSize(18);
        pieChart.setCenterTextColor(Color.parseColor("#3CC4C4"));
        pieChart.setEntryLabelTextSize(0);
        pieChart.animateXY(1000, 1000);
        Description description = new Description();
        description.setTextSize(18);
        description.setTextColor(Color.parseColor("#4DB38A"));
        description.setText("平均:" + avg);
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
                    ToastUtil.showMessage(SingleSubjectMajorActivity.this, name.get(0) + "占比" + rate.get(0));
                }else if(pieEntries.get(1).getValue() == e.getY()) {
                    ToastUtil.showMessage(SingleSubjectMajorActivity.this, name.get(1) + "占比" + rate.get(1));
                }else if(pieEntries.get(2).getValue() == e.getY()) {
                    ToastUtil.showMessage(SingleSubjectMajorActivity.this, name.get(2) + "占比" + rate.get(2));
                }else if(pieEntries.get(3).getValue() == e.getY()) {
                    ToastUtil.showMessage(SingleSubjectMajorActivity.this, name.get(3) + "占比" + rate.get(3));
                }else if(pieEntries.get(4).getValue() == e.getY()) {
                    ToastUtil.showMessage(SingleSubjectMajorActivity.this, name.get(4) + "占比" + rate.get(4));
                }
//                ToastUtil.showMessage(GradeRateActivity.this, e.getData().toString());
//                ToastUtil.showMessage(GradeRateActivity.this, e.getData().toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.setExtraBottomOffset(20);
        pieChart.invalidate();
    }


    private void findCourse() {
        Thread thread = new Thread(() -> {
            mySQLUtil = new MySQLUtil(SingleSubjectMajorActivity.this);
            mySQLUtil.getConnection("cce-18");
            course = mySQLUtil.getCourseName("final_exam_info");
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}