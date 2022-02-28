package com.example.NCEPU.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.GradeFailed;
import com.example.NCEPU.Utils.ToastUtil;
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


public class GradeFailedAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<GradeFailed> list;

    public GradeFailedAdapter(Context context, List<GradeFailed> list) {
        this.context = context;
        this.list = list;

    }


    @Override
    //获取分组个数
    public int getGroupCount() {
        return list.size();
    }

    @Override
    //分组中子选项个数为1
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    //获取指定分组数据
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    //获取指定子选项数据
    public Object getChild(int i, int j) {
        return null;
    }

    @Override
    //获取指定分组的id
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        GroupHolder groupHolder;
        if (convertView == null) {
            //父布局
            view = View.inflate(context, R.layout.group_failed, null);
            groupHolder = new GroupHolder();
            groupHolder.tv_course_name = view.findViewById(R.id.tv_course_name_failed); //课程名
            groupHolder.tv_show = view.findViewById(R.id.tv_show_number);
            view.setTag(groupHolder);
        } else {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        }
        groupHolder.tv_course_name.setText(list.get(i).getCourseName());
        String num = list.get(i).getNum() + "/" + list.get(i).getTotalNum();
        String text = "挂科人数:" + num;
        groupHolder.tv_show.setText(text);
        return view;
    }


    //取得显示给定分组给定子位置的数据用的视图
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        ChildHolder childHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.child_failed, null);
            childHolder = new ChildHolder();
            childHolder.pieChart = view.findViewById(R.id.pie_chart_failed);
            view.setTag(childHolder);

        } else {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        }
        //根据数据对pieChart进行初始化
        ArrayList<Integer> data = list.get(i).getData();
        ArrayList<String> name = new ArrayList<>(Arrays.asList("55-60", "50-55", "45-50","40-45", "40以下"));
        List<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<String> rate = new ArrayList<>();
        //计算百分比
        for(int m = 0; m < 5; m++) {
            System.out.println("当前x = " + data.get(m) / (data.get(0) + data.get(1) + data.get(2) + data.get(3) + data.get(4)) * 100);
            DecimalFormat df = new DecimalFormat("0.00%");
            String decimal = df.format((float)data.get(m) / (data.get(0) + data.get(1) + data.get(2) + data.get(3) + data.get(4)));
            PieEntry pieEntry = new PieEntry((float)data.get(m) / (data.get(0) + data.get(1) + data.get(2) + data.get(3) + data.get(4)) * 100, name.get(m));
            pieEntries.add(pieEntry);
            rate.add(decimal);
        }

        for(PieEntry pieEntry : pieEntries) {
            System.out.println("hh" + pieEntry.getValue());
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
        childHolder.pieChart.setData(pieData);
        childHolder.pieChart.setDrawCenterText(true);
        childHolder.pieChart.setCenterText(list.get(i).getCourseName());
        childHolder.pieChart.setCenterTextSize(20);
        childHolder.pieChart.setCenterTextColor(Color.parseColor("#3CC4C4"));
        childHolder.pieChart.setEntryLabelTextSize(0);
        Description description = new Description();
        description.setText("");
        childHolder.pieChart.setDescription(description);
        Legend legend = childHolder.pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(12);
        //legend.setXEntrySpace(12);
        childHolder.pieChart.setDragDecelerationFrictionCoef(0.95f);
        childHolder.pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(e == null) {
                    return;
                }
                if(pieEntries.get(0).getValue() == e.getY()) {
                    ToastUtil.showMessage(context, name.get(0) + "占比" + rate.get(0));
                }else if(pieEntries.get(1).getValue() == e.getY()) {
                    ToastUtil.showMessage(context, name.get(1) + "占比" + rate.get(1));
                }else if(pieEntries.get(2).getValue() == e.getY()) {
                    ToastUtil.showMessage(context, name.get(2) + "占比" + rate.get(2));
                }else if(pieEntries.get(3).getValue() == e.getY()) {
                    ToastUtil.showMessage(context, name.get(3) + "占比" + rate.get(3));
                }else if(pieEntries.get(4).getValue() == e.getY()) {
                    ToastUtil.showMessage(context, name.get(4) + "占比" + rate.get(4));
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        childHolder.pieChart.animateXY(1000, 1000);
        childHolder.pieChart.setExtraBottomOffset(20);
        childHolder.pieChart.invalidate();
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView tv_course_name;
        TextView tv_show;
    }

    static class ChildHolder {
        PieChart pieChart;
    }
}

