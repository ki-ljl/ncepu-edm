package com.example.NCEPU.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ZC;

import java.util.List;




public class ZCAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<ZC> list;

    public ZCAdapter(Context context, List<ZC> list) {
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
            view = View.inflate(context, R.layout.group_zc, null);
            groupHolder = new GroupHolder();
            groupHolder.zc_year = view.findViewById(R.id.zc_year); //学年
            view.setTag(groupHolder);
        } else {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        }
        groupHolder.zc_year.setText(list.get(i).getYear());
        return view;
    }


    //取得显示给定分组给定子位置的数据用的视图
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        ChildHolder childHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.child_zc, null);
            childHolder = new ChildHolder();
            childHolder.zc_fj = view.findViewById(R.id.zc_fj);
            childHolder.zc_grade =  view.findViewById(R.id.zc_grade);
            childHolder.zc_pe =  view.findViewById(R.id.zc_pe);
            childHolder.zc_sort_class =  view.findViewById(R.id.zc_sort_class);
            childHolder.zc_sort_major =  view.findViewById(R.id.zc_sort_major);
            childHolder.zc_sx =  view.findViewById(R.id.zc_sx);;
            childHolder.zc_total = view.findViewById(R.id.zc_total);

            view.setTag(childHolder);

        } else {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        }
        childHolder.zc_sx.setText("思想品德分:" + list.get(i).getSx());
        childHolder.zc_grade.setText("成绩分:" + list.get(i).getGrade());
        childHolder.zc_pe.setText("体育分:" + list.get(i).getPe());
        childHolder.zc_fj.setText("附加分:" + list.get(i).getFj());
        childHolder.zc_total.setText("总分:" + list.get(i).getTotal());
        childHolder.zc_sort_class.setText("班级排名:" + list.get(i).getClass_sort());
        childHolder.zc_sort_major.setText("专业排名:" + list.get(i).getMajor_sort());


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView zc_year;
    }

    static class ChildHolder {
        TextView zc_sx;
        TextView zc_grade;
        TextView zc_pe;
        TextView zc_fj;
        TextView zc_total;
        TextView zc_sort_class;
        TextView zc_sort_major;
    }
}

