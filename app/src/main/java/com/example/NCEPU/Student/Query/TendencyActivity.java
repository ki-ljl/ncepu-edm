package com.example.NCEPU.Student.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShareItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class TendencyActivity extends AppCompatActivity implements View.OnClickListener{

    private ComboLineColumnChartView chartView;
    private ImageButton button;
    private Button nature, query;
    private LinearLayout ly_back, ly_multiply;

//    String[] date = {"大一上","大一下","大二上","大二下","大三上","大三下"};//X轴的标注
//    double[] score= {90.8, 91.0, 92.0, 93.0, 94.0, 96.0};//图表的数据点
//    private List<PointValue> mPointValues = new ArrayList<PointValue>(); //坐标点
//    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();  //横坐标显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);
        chartView = findViewById(R.id.combine_chart);
        nature = findViewById(R.id.nature_tend);
        query = findViewById(R.id.btn_query_tend);
        button = findViewById(R.id.ib_back_tend);
        ly_multiply = findViewById(R.id.ly_multiply_tend);
        ly_back = findViewById(R.id.ly_back_tend);
        setListeners();
        button.setOnClickListener(new View.OnClickListener() {
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

        linearParams = (LinearLayout.LayoutParams)button.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        button.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ly_multiply.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_multiply.setLayoutParams(linearParams);

        //button=0.0589=48dp
        linearParams = (LinearLayout.LayoutParams)nature.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        nature.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        query.setLayoutParams(linearParams);
    }

    private void setListeners() {
        nature.setOnClickListener(this);
        query.setOnClickListener(this);
    }

     public void show(ArrayList<Grade> list) {

//        for(Grade e : list) {
//            System.out.println(e.getCourse_name() + " " + e.getXn());
//        }

        //先算算有几个学期来着
         ArrayList<String> semesterList = new ArrayList<>();
         for(int i = 0; i <list.size(); i++) {
             String xString = list.get(i).getXn() + "-" + list.get(i).getXq();
             if(!semesterList.contains(xString)) {
                 semesterList.add(xString);
             }
         }

         String[] data = {"大一上","大一下","大二上","大二下","大三上","大三下", "大四上", "大四下"};//X轴的标注
         ArrayList<String> xValues = new ArrayList<>();
         for(int i = 0; i < semesterList.size(); i++) {
             //System.out.println(data[i]);
             xValues.add(data[i]);  //横坐标
         }

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

         //计算每个学期的gpa
         ArrayList<Float> gpaList = new ArrayList<>();
         for(int i = 0; i < semesterList.size(); i++) {
             String xn = semesterList.get(i).substring(0, 9);
             String xq = semesterList.get(i).substring(10);
             double sum = 0;
             double credit = 0;
             for(int j = 0; j < list.size(); j++) {
                 if(list.get(j).getXn().equals(xn) && list.get(j).getXq().equals(xq)) {
                     double credits = Double.parseDouble(list.get(j).getCredit());
                     double marks = Double.parseDouble(list.get(j).getGpa()) * 10 + 50;
                     credit += credits;
                     sum += credits * marks;
                 }
             }
             gpaList.add((float)(sum / credit));
         }

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
        List<Line> dataLine = initDataLine(xValues, gpaList);
        LineChartData lineCharData = initLineCharData(dataLine);
        lineCharData.setLines(dataLine);
        comboLineColumnChartData.setLineChartData(lineCharData);

        //为组合图设置柱形图数据
        List<Column> dataColumn = initColumnLine(xValues, gpaList);
        ColumnChartData columnChartData = initColumnCharData(dataColumn, xValues);
        columnChartData.setColumns(dataColumn);
        comboLineColumnChartData.setColumnChartData(columnChartData);

        comboLineColumnChartData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
        comboLineColumnChartData.setValueLabelTextSize(25);// 设置数据文字大小
        comboLineColumnChartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);

        List<AxisValue> axisValues = new ArrayList<>();

        int numLines = xValues.size();
        for (int i = 0; i < numLines; ++i) {
            axisValues.add(new AxisValue(i).setLabel(xValues.get(i)));
        }
        axisX.setValues(axisValues);
        axisX.setTextSize(14);
        //axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.BLACK);
        axisY.setTextColor(Color.BLACK);
        axisY.setTextSize(14);

        comboLineColumnChartData.setAxisYLeft(axisY);
        comboLineColumnChartData.setAxisXBottom(axisX);
        comboLineColumnChartData.setValueLabelTextSize(14);
        //comboLineColumnChartData.setAxisYRight(axisYRight);//设置右边显示的轴
        //comboLineColumnChartData.setAxisXTop(axisXTop);//设置顶部显示的轴
        chartView.setComboLineColumnChartData(comboLineColumnChartData);//为组合图添加数据
        Viewport v = chartView.getMaximumViewport();//设置ｙ轴的长度
        v.top = 108;
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
    public static List<Line> initDataLine(ArrayList<String> xValues, ArrayList<Float> gpaList) {
        List<AxisValue> axisValues = new ArrayList<>();
        List<Line> lineList = new ArrayList<>();
        List<PointValue> pointValueList = new ArrayList<>();

        int numLines = xValues.size();
        for (int i = 0; i < numLines; ++i) {
            pointValueList.add(new PointValue(i, gpaList.get(i)));
            axisValues.add(new AxisValue(i).setLabel(xValues.get(i)));
        }

        Line line = new Line(pointValueList);

        line.setColor(Color.RED);
        line.setShape(ValueShape.CIRCLE);
        line.setPointRadius(4);
        line.setHasLabelsOnlyForSelected(true);
        lineList.add(line);

        return lineList;
    }

    //定义方法设置柱状图中数据
    public ColumnChartData initColumnCharData(List<Column> dataLine, ArrayList<String> xValues) {
        List<AxisValue> axisValues = new ArrayList<>();

        int numLines = xValues.size();
        for (int i = 0; i < numLines; ++i) {
            axisValues.add(new AxisValue(i).setLabel(xValues.get(i)));
        }
        ColumnChartData columnData = new ColumnChartData(dataLine);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true)
                .setTextColor(Color.BLACK));
        columnData.setAxisYLeft(new Axis().setHasLines(true)
                .setTextColor(Color.BLACK).setMaxLabelChars(2));
        columnData.setValueLabelTextSize(10);
        // Set selection mode to keep selected month column highlighted.
        chartView.setValueSelectionEnabled(true);
        chartView.setZoomType(ZoomType.HORIZONTAL);
        chartView.setAlpha(0.8f);

        Axis axisX = new Axis(axisValues);//设置横坐标柱子下面的分类
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("学期");    //设置横轴名称
        axisY.setName("gpa");    //设置竖轴名称
        columnData.setAxisXBottom(axisX); //设置横轴
        columnData.setAxisYLeft(axisY);   //设置竖轴

        return columnData;
    }

    //定义方法设置柱状图中数据
    public static List<Column> initColumnLine(ArrayList<String> xValues, ArrayList<Float> gpaList) {
        List<Column> list = new ArrayList<>();
        List<SubcolumnValue> subColumnValueList;
        ArrayList<AxisValue> axisValues = new ArrayList<>();
        int numSubColumns = 1;
        int numColumns = gpaList.size();
        for (int i = 0; i < numColumns; ++i) {
            subColumnValueList = new ArrayList<>();
            for (int j = 0; j < numSubColumns; ++j) {
                subColumnValueList.add(new SubcolumnValue(gpaList.get(i),
                        ChartUtils.pickColor()));
            }
            // 点击柱状图就展示数据量
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(2);
            axisValues.add(new AxisValue(i).setLabel(xValues.get(i)));
            list.add(new Column(subColumnValueList).setHasLabels(true).setFormatter(chartValueFormatter));
            //list.add(new Column(subColumnValueList).setHasLabelsOnlyForSelected(true));
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nature_tend:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, nature)
                        .addItem(new ShareItem("全部", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修+实践", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修+专选", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("必修-体育", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("除去校选", Color.BLACK, 0xffEEEEFF))
                        .setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareYear.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        nature.setText(shareYear.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.btn_query_tend:
                try {
                    String natureString = nature.getText().toString();
                    if(natureString.equals("查询性质")) {
                        ToastUtil.showMessage(this, "请选择查询性质!");
                    }else {
                        ArrayList<Grade> list = connectJWGL.getStudentGrade("", "", "全部");
                        list = cut(list, natureString);
                        show(list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
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
}