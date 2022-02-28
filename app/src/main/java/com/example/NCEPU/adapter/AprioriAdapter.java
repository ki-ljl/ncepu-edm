package com.example.NCEPU.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Apriori;
import com.example.NCEPU.Utils.Grade;

import java.util.ArrayList;
import java.util.List;

public class AprioriAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Apriori> list;

    public AprioriAdapter(Context context, List<Apriori> list) {
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
            view = View.inflate(context, R.layout.group_apriori, null);
            groupHolder = new GroupHolder();
            groupHolder.tv_rule = view.findViewById(R.id.tv_rule); //课程名
            groupHolder.tv_conf = view.findViewById(R.id.tv_conf);   //成绩
            view.setTag(groupHolder);
        } else {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        }
        String rule = "";
        for(int t = 0; t < list.get(i).getAntecedents().size(); t++) {
            String x = list.get(i).getAntecedents().get(t);
            if(t != list.get(i).getAntecedents().size() - 1) {
                rule += (x + ", ");
            }else {
                rule += x;
            }
        }
        rule += "\n---> ";
        for(int t = 0; t < list.get(i).getConsequents().size(); t++) {
            String x = list.get(i).getConsequents().get(t);
            if(t != list.get(i).getConsequents().size() - 1) {
                rule += (x + ", ");
            }else {
                rule += x;
            }
        }
        groupHolder.tv_rule.setText(rule);
        groupHolder.tv_conf.setText("置信度: " + list.get(i).getConf());
        return view;
    }


    //取得显示给定分组给定子位置的数据用的视图
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        ChildHolder childHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.child_apriori, null);
            childHolder = new ChildHolder();
            childHolder.tv_font = view.findViewById(R.id.tv_font);
            childHolder.tv_back =  view.findViewById(R.id.tv_back);
            childHolder.tv_state =  view.findViewById(R.id.tv_state);
            view.setTag(childHolder);
        } else {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        }
        ArrayList<String> fonts = list.get(i).getPreCourses();
        String font = "前项:\n";
        for(String x : fonts) {
            if(!x.contains("-")) {
                font += (x + "\n");
            }else {
                font += (x.replace("-", ": ") + "\n");
            }
        }
        ArrayList<String> backs = list.get(i).getBackCourses();
        String back = "后项:\n";
        for(String x : backs) {
            if(!x.contains("-")) {
                back += (x + "\n");
            }else {
                back += (x.replace("-", ": ") + "\n");
            }
        }
        childHolder.tv_font.setText(font);
        childHolder.tv_back.setText(back);
        childHolder.tv_state.setText("状态: " + list.get(i).getState());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView tv_rule;
        TextView tv_conf;
    }

    static class ChildHolder {
        TextView tv_font;
        TextView tv_back;
        TextView tv_state;
    }
}

