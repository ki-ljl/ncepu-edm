package com.example.NCEPU.Student.Query;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShareItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.util.ArrayList;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;
import static com.github.mikephil.charting.components.Legend.LegendPosition.BELOW_CHART_CENTER;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class GradeGpaActivity extends AppCompatActivity implements View.OnClickListener{



    //private ComboLineColumnChartView chartView;
    private BarChart barChart;
    private LinearLayout ly_back, ly_multiply;
    private ImageButton ib_back;

    private Button btn_query;

    private TextView tv_info;


    private Button year, semester, nature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_gpa);
        initViews();
        setListeners();
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
        linearParams = (LinearLayout.LayoutParams)year.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        year.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)semester.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        semester.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)nature.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        nature.setLayoutParams(linearParams);

        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)btn_query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        btn_query.setLayoutParams(linearParams);
    }

    private void initViews() {
        ly_back = findViewById(R.id.ly_back_gpa_grade);
        ib_back = findViewById(R.id.ib_back_gpa_grade);
        btn_query =  findViewById(R.id.btn_query_gpa_grade);
//        tv_info = findViewById(R.id.tv_info_gpa_grade);
        ly_multiply = findViewById(R.id.ly_multiply_gpa_grade);

        year = findViewById(R.id.school_year_gpa_grade);
        semester = findViewById(R.id.semester_gpa_grade);
        nature = findViewById(R.id.course_gpa_grade);
        btn_query = findViewById(R.id.btn_query_gpa_grade);
//        chartView = findViewById(R.id.column_chart);
        barChart = findViewById(R.id.bar_chart);

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setListeners() {
        year.setOnClickListener(this);
        semester.setOnClickListener(this);
        nature.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_gpa_grade:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, year)
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
                        year.setText(shareYear.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.semester_gpa_grade:
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

            case R.id.course_gpa_grade:
                @SuppressLint("WrongConstant") final FlipShareView shareCourse_2 = new FlipShareView.Builder(this, nature)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("实践课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("校选修课", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("专选课", Color.BLACK, 0xffEEEEFF))
                        //.setBackgroundColor(0x60000000)
                        .setItemDuration(0)
                        .setSeparateLineColor(Color.BLACK)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareCourse_2.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        nature.setText(shareCourse_2.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.btn_query_gpa_grade:
                String yearString = year.getText().toString();
                String semesterString = semester.getText().toString();
                String course_nature = nature.getText().toString();
                if(yearString.equals("学年")) {
                    ToastUtil.showMessage(this, "请选择学年！");
                }else if(semesterString.equals("学期")) {
                    ToastUtil.showMessage(this, "请选择学期！");
                }else if(course_nature.equals("查询性质")) {
                    ToastUtil.showMessage(this, "请选择课程性质！");
                }else {
                    //开始查询成绩并显示
                    try {
                        if(yearString.equals("全部")) {
                            yearString = "";
                        }else {
                            yearString = yearString.substring(0, 4);
                        }
                        if(semesterString.equals("全部")) {
                            semesterString = "";
                        }
                        ArrayList<Grade> list = connectJWGL.getStudentGrade(yearString, semesterString, course_nature);
                        if (list.size() == 0) {
                            ToastUtil.showMessage(this, "没有查到记录！");
                        }else {
                            showMPAndroid(list);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public ArrayList<Grade> convert(ArrayList<Grade> list, int year, int semester) {
        String year_ = year + "-" + (year + 1);
        String semester_ = String.valueOf(semester);
        for(int i = 0; i < list.size(); i++) {
            list.get(i).setXn(year_);
            list.get(i).setXq(semester_);
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Grade> getData(int year_start, int semester_start, int year_end, int semester_end, String nature) throws Exception {
        //2014-2015--2014-2015
        ArrayList<Grade> final_list = new ArrayList<>();
        if(year_start == year_end) {
            //2014-2015-1--2014-2015-1
            if (semester_start == semester_end) {
                ArrayList<Grade> list1 = connectJWGL.getStudentGrade(String.valueOf(year_start), String.valueOf(semester_start), nature);
                final_list = convert(list1, year_start, semester_start);
            }else {   //2014-2015-1--2014-2015-2
                ArrayList<Grade> list1 = connectJWGL.getStudentGrade(String.valueOf(year_start), "1", nature);
                list1 = convert(list1, year_start, 1);
                ArrayList<Grade> list2 = connectJWGL.getStudentGrade(String.valueOf(year_start), "2", nature);
                list2 = convert(list2, year_start, 2);
                for(int i = 0; i < list1.size(); i++) {
                    final_list.add(list1.get(i));
                }
                for(int i = 0; i < list2.size(); i++) {
                    final_list.add(list2.get(i));
                }
            }
        }else {   //2014---2018
            //处理2014
            if(semester_start == 1) {
                ArrayList<Grade> list1 = connectJWGL.getStudentGrade(String.valueOf(year_start), "1", nature);
                list1 = convert(list1, year_start, 1);
                ArrayList<Grade> list2 = connectJWGL.getStudentGrade(String.valueOf(year_start), "2", nature);
                list2 = convert(list2, year_start, 2);
                for(int j = 0; j < list1.size(); j++) {
                    final_list.add(list1.get(j));
                }
                for(int j = 0; j < list2.size(); j++) {
                    final_list.add(list2.get(j));
                }
            }else {
                ArrayList<Grade> list = connectJWGL.getStudentGrade(String.valueOf(year_start), "2", nature);
                list = convert(list, year_start, 2);
                for(int j = 0; j < list.size(); j++) {
                    final_list.add(list.get(j));
                }
            }
            //处理中间几年2015-2017
            for(int i = year_start + 1; i < year_end; i++) {
                ArrayList<Grade> list1 = connectJWGL.getStudentGrade(String.valueOf(i), "1", nature);
                list1 = convert(list1, i, 1);
                ArrayList<Grade> list2 = connectJWGL.getStudentGrade(String.valueOf(i), "2", nature);
                list2 = convert(list2, i, 2);
                for(int j = 0; j < list1.size(); j++) {
                    final_list.add(list1.get(j));
                }
                for(int j = 0; j < list2.size(); j++) {
                    final_list.add(list2.get(j));
                }
            }

            //处理最后一年
            if(semester_end == 2) {
                ArrayList<Grade> list1 = connectJWGL.getStudentGrade(String.valueOf(year_end), "1", nature);
                list1 = convert(list1, year_end, 1);
                ArrayList<Grade> list2 = connectJWGL.getStudentGrade(String.valueOf(year_end), "2", nature);
                list2 = convert(list2, year_end, 2);
                for(int j = 0; j < list1.size(); j++) {
                    final_list.add(list1.get(j));
                }
                for(int j = 0; j < list2.size(); j++) {
                    final_list.add(list2.get(j));
                }
            }else {
                ArrayList<Grade> list = connectJWGL.getStudentGrade(String.valueOf(year_end), "1", nature);
                list = convert(list, year_end, 1);
                for(int j = 0; j < list.size(); j++) {
                    final_list.add(list.get(j));
                }
            }

        }
        System.out.println("开始展示咯");
        //final_list就是最后需要展示的成绩表
        for(int i = 0; i < final_list.size(); i++) {
            System.out.println(final_list.get(i).getCourse_name() + " "
             + final_list.get(i).getXn() + " " + final_list.get(i).getXq());
        }
        showMPAndroid(final_list);
        return final_list;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showMPAndroid(ArrayList<Grade> list) {
        //list中有多个学期，按照学期来吧
        //先寻找有多少个学期，决定有多少个legend和Gpa
//        ArrayList<Integer> Sizes = new ArrayList<>();
//        ArrayList<String> sub = new ArrayList<>();
//        String beginXn = list.get(0).getXn();
//        String beginXq = list.get(0).getXq();
//        String endXn = list.get(list.size() - 1).getXn();
//        String endXq = list.get(list.size() - 1).getXq();
//        int startYear = Integer.parseInt(beginXn.substring(0, 4));
//        int endYear = Integer.parseInt(endXn.substring(0, 4));
//        int startSemester = Integer.parseInt(beginXq);
//        int endSemester = Integer.parseInt(endXq);
//        sub.add(beginXn + "-" + beginXq);
//        if(startSemester == 1) {
//            sub.add(beginXn + "-2");
//        }
//        for(int i = startYear + 1; i < endYear; i++) {
//            sub.add(i + "-" + (i + 1) + "-1");
//            sub.add(i + "-" + (i + 1) + "-2");
//        }
//        sub.add(endXn + "-1");
//        if(endSemester == 2) {
//            sub.add(endXn + "-2");
//        }
//        for(int i = 0; i < sub.size(); i++) {
//            Sizes.add(0);
//            for(int j = 0; j < list.size(); j++) {
//                String value = list.get(j).getXn() + "-" + list.get(j).getXq();
//                if(value.equals(sub.get(i))) {
//                    Sizes.set(i, Sizes.get(i) + 1);
//                }
//            }
//        }
//
//        for(int i = 0; i <Sizes.size(); i++) {
//            System.out.println(sub.get(i) + " " + Sizes.get(i));
//        }
        List<Float> grades = new ArrayList<>();
        List<Float> Gpa = new ArrayList<>();
        List<String> xList = new ArrayList<>();
        //删除掉重修成绩
        Iterator<Grade> it = list.iterator();
        while(it.hasNext()) {
            Grade grade = it.next();
            String nature = grade.getGrade_nature();
            if(!nature.equals("正常考试")) {
                it.remove();
            }
        }

        ArrayList<String> level = new ArrayList<>(Arrays.asList("优秀", "良好", "中等", "及格", "不及格"));
        ArrayList<String> mark = new ArrayList<>(Arrays.asList("95", "85", "75", "65", "30"));
        for(int i = 0; i < list.size(); i++) {
            if(level.contains(list.get(i).getMark())) {
                int index = level.indexOf(list.get(i).getMark());
                list.get(i).setMark(mark.get(index));
            }
        }

//        计算GPA/// 7 6 6
//        ArrayList<Float> gpaList = new ArrayList<>();
//        for(int i = 0; i < sub.size(); i++) {
//            int j = 0;
//            for(int k = 0; k < i; k++) {
//                j += Sizes.get(k);
//            }
////            System.out.println("j = " + j);
//            double sum = 0;
//            double x = 0;
//            for(int m = j; m < j + Sizes.get(i); m++) {
//                double credit = Double.parseDouble(list.get(m).getCredit());
//                double gpa = Double.parseDouble(list.get(m).getGpa()) * 10 + 50;
//                x += credit;
//                sum += credit * gpa;
//            }
//            gpaList.add((float) (sum / x));
//        }

        double sum = 0;
        double xx = 0;
        for(int m = 0; m < list.size(); m++) {
            double credit = Double.parseDouble(list.get(m).getCredit());
            double gpa = Double.parseDouble(list.get(m).getGpa()) * 10 + 50;
            xx += credit;
            sum += credit * gpa;
        }

        float gpa = (float) (sum / xx);



        for(int i = 0; i < list.size(); i++) {
            grades.add(Float.parseFloat(list.get(i).getMark()));
            Gpa.add(gpa);
            xList.add(list.get(i).getCourse_name());
            //xList.add("hhh");
        }

        //对x轴设置换行符
        for(int i = 0; i < list.size(); i++) {
            String x = xList.get(i);
            int len = x.length();
            if(len <= 5) {
                x = x + "\n" + " ";
            }else if(len <= 10) {
                x = x.substring(0, 5) + "\n" +  x.substring(5);
            }else if(len <= 15) {
                x = x.substring(0, 5) + "\n" + x.substring(5, 10) + "\n" + x.substring(10);
            }else {
                x = x.substring(0, 5) + "\n" + x.substring(5, 10) + "\n" + x.substring(10, 15)
                         + "\n" + x.substring(15);
            }
            xList.set(i, x);
        }
//        for(int i = 0; i < sub.size(); i++) {
//            for(int j = 0; j < Sizes.get(i); j++) {
//                Gpa.add(gpaList.get(i));
//            }
//        }

        //设置曲线整体的配置
        barChart.setNoDataText("暂无数据");
        //设置绘制动画
        barChart.animateXY(1000, 1000);
        //隐藏说明
        barChart.getDescription().setEnabled(false);

        //设置X轴
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f); //要设置，否则右侧还有部分图表未展示出来
        xAxis.setAxisMaximum(list.size()); //要设置，否则右侧还有部分图表未展示出来
        xAxis.setLabelCount(list.size(),false); //要设置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-0);
        xAxis.setTextSize(12f);
        //设置要不要X轴的网格，就是网格的竖线
        xAxis.setDrawGridLines(false);
        //将X轴的值显示在中央
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //用(int) Math.abs(value) % xList.size()防止越界
                return xList.get((int) Math.abs(value) % xList.size());
            }
        });

        //设置Y轴
        YAxis rightYAxis = barChart.getAxisRight();
        //隐藏右边Y轴
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = barChart.getAxisLeft();
        //设置网格为虚线
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);

        List<IBarDataSet> dataSets = new ArrayList<>();
        //设置成绩数据
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            entries.add(new BarEntry(i, grades.get(i)));
        }
        BarDataSet barDataSet = new BarDataSet(entries, "成绩");
        //设置柱形的颜色
        barDataSet.setValueTextSize(9f);
        barDataSet.setColor(Color.parseColor("#9CEBAC"));
        dataSets.add(barDataSet);

        //设置GPA数据
        List<BarEntry> entries2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            entries2.add(new BarEntry(i, Gpa.get(i)));
        }
        BarDataSet barDataSet2 = new BarDataSet(entries2, "平均学分绩");
        //设置柱形的颜色
        barDataSet2.setColor(Color.parseColor("#209C38"));
        barDataSet2.setValueTextSize(9f);
        dataSets.add(barDataSet2);

//        //设置另一组
//        List<BarEntry> entries3 = new ArrayList<>();
//        for(int i = 3; i < list.size(); i++) {
//            entries3.add(new BarEntry(i, 94.0f));
//        }
//        BarDataSet barDataSet3 = new BarDataSet(entries3, "学分绩");
//        //设置柱形的颜色
//        barDataSet3.setColor(Color.parseColor("#ff0000"));
//        dataSets.add(barDataSet3);

        BarData data = new BarData(dataSets);
        //关键
        /**
         * float groupSpace    //柱状图组之间的间距
         * float barSpace   //每条柱状图之间的间距  一组两个柱状图
         * float barWidth     //每条柱状图的宽度     一组两个柱状图
         * (barWidth + barSpace) * barAmount + groupSpace = 1.00
         * 3个数值 加起来 必须等于 1 即100% 按照百分比来计算 组间距 柱状图间距 柱状图宽度
         */
        int barAmount = dataSets.size(); //需要显示柱状图的类别 数量
        //设置组间距占比30% 每条柱状图宽度占比 70% /barAmount  柱状图间距占比 0%
        float groupSpace = 0.3f; //柱状图组之间的间距
        float barSpace = 0.05f;
        float barWidth = (1f - groupSpace) / barAmount - 0.05f;
        //设置柱状图宽度
        data.setBarWidth(barWidth);
        //(起始点、柱状图组间距、柱状图之间间距)
        data.groupBars(0f, groupSpace, barSpace);

        barChart.setData(data);
        barChart.setVisibleXRangeMaximum(4);
        barChart.setExtraBottomOffset(4 * 12f);
        barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(), barChart.getTransformer(YAxis.AxisDependency.LEFT)));

        Legend legend = barChart.getLegend();
        legend.setPosition(BELOW_CHART_CENTER);//设置图例的位置在图表的 左下角


    }
    public class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            for (int i = 0; i < line.length; i++) {
                float vOffset = i * mAxisLabelPaint.getTextSize();
                Utils.drawXAxisValue(c, line[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees);
            }
//            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
//            Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        }
    }



    /*public void show(ArrayList<Grade> list) {

        //删除掉重修成绩
        Iterator<Grade> it = list.iterator();
        while(it.hasNext()) {
            Grade grade = it.next();
            String nature = grade.getGrade_nature();
            if(!nature.equals("正常考试")) {
                it.remove();
            }
        }

        ArrayList<String> level = new ArrayList<>(Arrays.asList("优秀", "良好", "中等", "及格", "不及格"));
        ArrayList<String> mark = new ArrayList<>(Arrays.asList("95", "85", "75", "65", "30"));
        for(int i = 0; i < list.size(); i++) {
            if(level.contains(list.get(i).getMark())) {
                int index = level.indexOf(list.get(i).getMark());
                list.get(i).setMark(mark.get(index));
            }
        }

        //计算GPA
        double sum = 0;
        double x = 0;
        for(int i = 0; i < list.size(); i++) {
            double credit = Double.parseDouble(list.get(i).getCredit());
            double gpa = Double.parseDouble(list.get(i).getGpa()) * 10 + 50;
            x += credit;
            sum += credit * gpa;
        }

        float gpa = (float) (sum / x);
        //开始显示
        chartView.setZoomEnabled(true);//设置是否支持缩放
        //为图表设置值得触摸事件
        //设置值触摸侦听器，将触发图表顶部的变化。
        chartView.setOnValueTouchListener(new ComboLineColumnChartOnValueSelectListener() {
            @Override
            public void onColumnValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

            }

            @Override
            public void onPointValueSelected(int i, int i1, PointValue pointValue) {

            }

            @Override
            public void onValueDeselected() {

            }
        });
        //设置图表是否可以与用户互动
        chartView.setInteractive(true);
        //设置图表数据是否选中进行显示
        chartView.setValueSelectionEnabled(true);
        //定义组合数据对象
        ComboLineColumnChartData comboLineColumnChartData = new ComboLineColumnChartData();
        //为图表设置数据，数据类型为ComboLineColumnChartData
        chartView.setComboLineColumnChartData(comboLineColumnChartData);

        //为组合图设置折现图数据
        List<Line> dataLine = initDataLine(list, gpa);
        LineChartData lineCharData = initLineCharData(dataLine);
        lineCharData.setLines(dataLine);
        comboLineColumnChartData.setLineChartData(lineCharData);

        //为组合图设置柱形图数据
        List<Column> dataColumn = initColumnLine(list);
        ColumnChartData columnChartData = initColumnCharData(dataColumn, list);
        columnChartData.setColumns(dataColumn);
        comboLineColumnChartData.setColumnChartData(columnChartData);

        comboLineColumnChartData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
        comboLineColumnChartData.setValueLabelTextSize(25);// 设置数据文字大小
        comboLineColumnChartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);

        List<AxisValue> axisValues = new ArrayList<>();

        int numLines = list.size();
        for (int i = 0; i < numLines; ++i) {
            axisValues.add(new AxisValue(i).setLabel(list.get(i).getCourse_name()));
        }
        axisX.setValues(axisValues);
        axisX.setTextSize(10);
        axisX.setHasTiltedLabels(true);
        axisY.setTextColor(Color.BLACK);
        axisY.setTextColor(Color.BLACK);

        comboLineColumnChartData.setAxisYLeft(axisY);
        comboLineColumnChartData.setAxisXBottom(axisX);
        //comboLineColumnChartData.setAxisYRight(axisYRight);//设置右边显示的轴
        //comboLineColumnChartData.setAxisXTop(axisXTop);//设置顶部显示的轴
        chartView.setComboLineColumnChartData(comboLineColumnChartData);//为组合图添加数据

        Viewport v = chartView.getMaximumViewport();//设置ｙ轴的长度
        v.top = 112;
        chartView.setCurrentViewport(v);

        //通过left, top, right, bottom四边确定的一个矩形区域。
//用来控制柱形图视图窗口的缩放。
        Viewport viewport =new Viewport(0,  105, list.size() > 5 ? 5 : list.size(), 0);
        chartView.setCurrentViewport(viewport);
        chartView.moveTo(0, 0);

        //chartView.setMaximumViewport(viewport);
        chartView.setCurrentViewport(viewport);


    }

    //设置折线图,添加设置好的数据
    public static LineChartData initLineCharData(List<Line> dataLine) {
        LineChartData lineCharData = new LineChartData(dataLine);
        //初始化轴
        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("课程");
        axisY.setName("分数");
        lineCharData.setAxisYLeft(axisY);
        lineCharData.setAxisXBottom(axisX);
        return lineCharData;
    }

    //定义方法设置折线图中数据
    public static List<Line> initDataLine(ArrayList<Grade> list, float gpa) {
        List<AxisValue> axisValues = new ArrayList<>();
        List<Line> lineList = new ArrayList<>();
        List<PointValue> pointValueList = new ArrayList<>();

        int numLines = list.size();
        for (int i = 0; i < numLines; ++i) {
            pointValueList.add(new PointValue(i, gpa));
            axisValues.add(new AxisValue(i).setLabel(list.get(i).getCourse_name()));
        }

        Line line = new Line(pointValueList);

        line.setColor(Color.RED);
        line.setShape(ValueShape.CIRCLE);
        line.setPointRadius(1);
        line.setHasLabelsOnlyForSelected(true);
        lineList.add(line);

        return lineList;
    }

    //定义方法设置柱状图中数据
    public ColumnChartData initColumnCharData(List<Column> dataLine, ArrayList<Grade> list) {
        List<AxisValue> axisValues = new ArrayList<>();

        int numLines = list.size();
        for (int i = 0; i < numLines; ++i) {
            axisValues.add(new AxisValue(i).setLabel(list.get(i).getCourse_name()));
        }
        ColumnChartData columnData = new ColumnChartData(dataLine);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true)
                .setTextColor(Color.BLACK));
        columnData.setAxisYLeft(new Axis().setHasLines(true)
                .setTextColor(Color.BLACK).setMaxLabelChars(2));
        // Set selection mode to keep selected month column highlighted.
        chartView.setValueSelectionEnabled(true);
        chartView.setZoomType(ZoomType.HORIZONTAL);
        chartView.setAlpha(0.8f);

        Axis axisX = new Axis(axisValues);//设置横坐标柱子下面的分类
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("课程");    //设置横轴名称
        axisY.setName("分数");    //设置竖轴名称
        columnData.setAxisXBottom(axisX); //设置横轴
        columnData.setAxisYLeft(axisY);   //设置竖轴

        return columnData;
    }

    //定义方法设置柱状图中数据
    public static List<Column> initColumnLine(ArrayList<Grade> list2) {
        List<Column> list = new ArrayList<>();
        List<SubcolumnValue> subColumnValueList;
        ArrayList<AxisValue> axisValues = new ArrayList<AxisValue>();
        int numSubColumns = 1;
        int numColumns = list2.size();
        for (int i = 0; i < numColumns; ++i) {
            subColumnValueList = new ArrayList<>();
            for (int j = 0; j < numSubColumns; ++j) {
                subColumnValueList.add(new SubcolumnValue(Float.parseFloat(list2.get(i).getMark()),
                        ChartUtils.pickColor()));
            }
            // 点击柱状图就展示数据量
            axisValues.add(new AxisValue(i).setLabel(list2.get(i).getCourse_name()));
            list.add(new Column(subColumnValueList).setHasLabels(true));
            //list.add(new Column(subColumnValueList).setHasLabelsOnlyForSelected(true));
        }
        return list;
    }*/


}