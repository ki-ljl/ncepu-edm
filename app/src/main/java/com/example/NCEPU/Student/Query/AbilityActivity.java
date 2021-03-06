package com.example.NCEPU.Student.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Grade;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class AbilityActivity extends AppCompatActivity {

    private ImageButton back;
    private LinearLayout ly_back;
    private Button query;
    private RadarChart radarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability);
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

        //????????????=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        query.setLayoutParams(linearParams);
    }
    private void initViews() {
        ly_back = findViewById(R.id.ly_back_radar);
        back = findViewById(R.id.ib_back_radar);
        back.setOnClickListener(v -> {
            onBackPressed();
        });

        radarChart = findViewById(R.id.ability_radar);

        query = findViewById(R.id.btn_query_radar);
        query.setOnClickListener(v -> {
            showRadar();
        });
    }

    private void showRadar() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String dept = sharedPreferences.getString("dept", "");
        try {
            ArrayList<Grade> list = connectJWGL.getStudentGrade("", "", "??????");
            //?????????????????????????????????????????????????????????????????????
            ArrayList<Float> data = new ArrayList<>();
            ArrayList<Float> sums = new ArrayList<>();
            ArrayList<Float> credits = new ArrayList<>();
            for(int i = 0; i < 6; i++) {
                sums.add((float) 0);
                credits.add((float) 0);
            }
            for(Grade grade : list) {
                String college = grade.getCollege();
                String nature = grade.getCourse_nature();
                float credit = Float.parseFloat(grade.getCredit());
                float gpa = Float.parseFloat(grade.getGpa());
                //?????????
                if(college.equals("????????????")) {
                    sums.set(0, sums.get(0) + gpa * credit);
                    credits.set(0, credits.get(0) + credit);
                }
                //?????????
                if(college.equals("???????????????")) {
                    sums.set(1, sums.get(1) + gpa * credit);
                    credits.set(1, credits.get(1) + credit);
                }

                //??????
                if(nature.equals("?????????")) {
                    sums.set(2, sums.get(2) + gpa * credit);
                    credits.set(2, credits.get(2) + credit);
                }

                //??????
                if(college.equals("?????????????????????")) {
                    sums.set(3, sums.get(3) + gpa * credit);
                    credits.set(3, credits.get(3) + credit);
                }

                //??????
                if(college.equals("???????????????")) {
                    sums.set(4, sums.get(4) + gpa * credit);
                    credits.set(4, credits.get(4) + credit);
                }

                //?????????
                if(college.equals(dept)) {
                    sums.set(5, sums.get(5) + gpa * credit);
                    credits.set(5, credits.get(5) + credit);
                }

            }

            for(int i = 0; i < 6; i++) {
                float avg = (sums.get(i) / credits.get(i)) * 10 + 50;
                avg = (float)(Math.round(avg * 100)) / 100;
                System.out.println(avg);
                data.add(avg);
            }
            initRadar(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRadar(ArrayList<Float> data) {
        ArrayList<RadarEntry> list = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            list.add(new RadarEntry(data.get(i)));
        }
        RadarDataSet radarDataSet = new RadarDataSet(list,"????????????");
        // ??????????????????
        radarDataSet.setColor(Color.parseColor("#EB9CAC"));
       // ????????????????????????
        radarDataSet.setFillColor(Color.parseColor("#EB9CAC"));
//        radarDataSet.setFillColor(ColorTemplate.VORDIPLOM_COLORS[0]);
       // ????????????????????????
        radarDataSet.setDrawFilled(true);
       // ??????????????????
        radarDataSet.setLineWidth(2f);
        RadarData radarData=new RadarData(radarDataSet);
        radarData.setValueTextSize(10f);
        radarChart.setData(radarData);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setValueFormatter((v, axisBase) -> {
            if (v == 0){
                return "??????";
            }
            if (v == 1){
                return "??????";
            }
            if (v == 2){
                return "??????";
            }
            if (v == 3){
                return "??????";
            }
            if (v == 4){
                return "??????";
            }
            return "??????";
        });

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setDrawLabels(false);

        //legend
        Legend l = radarChart.getLegend();
       // ??????????????????
       // l.setTypeface(tf);
       // ??????X??????
        l.setXEntrySpace(2f);
       // ??????Y??????
        l.setYEntrySpace(1f);
        l.setTextSize(14f);
        l.setTextColor(Color.parseColor("#2B6FD5")); //????????????
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        Description description = new Description();
        description.setText("");
        radarChart.setDescription(description);

        //Y????????????????????????????????????????????????????????????Y????????????
        radarChart.getYAxis().setAxisMinimum(0);
        radarChart.getYAxis().setAxisMaximum(100);
        radarChart.animateXY(1000, 1000);
        radarChart.invalidate();
    }
}