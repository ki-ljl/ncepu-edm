package com.example.NCEPU.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Grade;

import java.util.List;

public class GradeAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Grade> list;

    public GradeAdapter(Context context, List<Grade> list) {
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
            view = View.inflate(context, R.layout.group_grade, null);
            groupHolder = new GroupHolder();
            groupHolder.tv_course_name = view.findViewById(R.id.tv_course_name); //课程名
            groupHolder.tv_mark = view.findViewById(R.id.tv_mark);   //成绩
            view.setTag(groupHolder);
        } else {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        }
        groupHolder.tv_course_name.setText(list.get(i).getCourse_name());
        groupHolder.tv_mark.setText(list.get(i).getMark());
        return view;
    }


    //取得显示给定分组给定子位置的数据用的视图
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        ChildHolder childHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.child_grade, null);
            childHolder = new ChildHolder();
            childHolder.tv_xn = view.findViewById(R.id.tv_xn);
            childHolder.tv_xq =  view.findViewById(R.id.tv_xq);
            childHolder.tv_course_code =  view.findViewById(R.id.tv_course_code);
            childHolder.tv_course_nature =  view.findViewById(R.id.tv_course_nature);
            childHolder.tv_ks_nature = view.findViewById(R.id.tv_ks_nature);
            childHolder.tv_credit =  view.findViewById(R.id.tv_credit);
            childHolder.tv_gpa =  view.findViewById(R.id.tv_gpa);;
            childHolder.tv_college = view.findViewById(R.id.tv_college);
            childHolder.tv_class = view.findViewById(R.id.tv_class);
            childHolder.tv_teacher = view.findViewById(R.id.tv_teacher);

            view.setTag(childHolder);

        } else {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        }
        childHolder.tv_xn.setText("学年:" + list.get(i).getXn());
        childHolder.tv_xq.setText("学期:" + list.get(i).getXq());
        childHolder.tv_course_code.setText("课程代码:" + list.get(i).getCourse_code());
        childHolder.tv_course_nature.setText("课程性质:" + list.get(i).getCourse_nature());
        childHolder.tv_ks_nature.setText("考试性质:" + list.get(i).getGrade_nature());
        childHolder.tv_credit.setText("学分:" + list.get(i).getCredit());
        childHolder.tv_gpa.setText("绩点:" + list.get(i).getGpa());
        childHolder.tv_college.setText("开课学院:" + list.get(i).getCollege());
        childHolder.tv_class.setText("教学班:" + list.get(i).getClass_());
        childHolder.tv_teacher.setText("任课教师:" + list.get(i).getTeacher());


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView tv_course_name;
        TextView tv_mark;
    }

    static class ChildHolder {
        TextView tv_xn;
        TextView tv_xq;
        TextView tv_course_code;
        TextView tv_course_nature;
        TextView tv_ks_nature;
        TextView tv_credit;
        TextView tv_gpa;
        TextView tv_college;
        TextView tv_class;
        TextView tv_teacher;
    }
}

