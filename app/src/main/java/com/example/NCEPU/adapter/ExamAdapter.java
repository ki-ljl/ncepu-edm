package com.example.NCEPU.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Exam;

import java.util.List;




public class ExamAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Exam> list;

    public ExamAdapter(Context context, List<Exam> list) {
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
            view = View.inflate(context, R.layout.group_exam, null);
            groupHolder = new GroupHolder();
            groupHolder.tv_course_name_exam= view.findViewById(R.id.tv_course_name_exam); //课程名
            groupHolder.tv_exam_place = view.findViewById(R.id.tv_exam_place);   //考试地点
            groupHolder.tv_exam_time = view.findViewById(R.id.tv_exam_time);
            view.setTag(groupHolder);
        } else {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        }
        groupHolder.tv_course_name_exam.setText(list.get(i).getCourse_name());
        groupHolder.tv_exam_place.setText(list.get(i).getExam_place());
        groupHolder.tv_exam_time.setText(list.get(i).getExam_time());
        return view;
    }


    //取得显示给定分组给定子位置的数据用的视图
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        ChildHolder childHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.child_exam, null);
            childHolder = new ChildHolder();
            childHolder.grade_class_exam = view.findViewById(R.id.grade_class_exam);
            childHolder.tv_xn_exam = view.findViewById(R.id.tv_xn_exam);
            childHolder.tv_xq_exam =  view.findViewById(R.id.tv_xq_exam);
            childHolder.tv_course_code_exam =  view.findViewById(R.id.tv_course_code_exam);
            childHolder.tv_class_comp_exam =  view.findViewById(R.id.tv_class_comp_exam);
            childHolder.tv_school_time_exam =  view.findViewById(R.id.tv_school_time_exam);
            childHolder.tv_major_exam =  view.findViewById(R.id.tv_major_exam);;
            childHolder.tv_campus_exam = view.findViewById(R.id.tv_campus_exam);
            childHolder.tv_teacher_exam = view.findViewById(R.id.tv_teacher_exam);

            view.setTag(childHolder);

        } else {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        }
        childHolder.grade_class_exam.setText("年级:" + list.get(i).getGrade_class());
        childHolder.tv_xn_exam.setText("学年:" + list.get(i).getXn());
        childHolder.tv_xq_exam.setText("学期:" + list.get(i).getXq());
        childHolder.tv_course_code_exam.setText("课程代码:" + list.get(i).getCourse_code());
        childHolder.tv_class_comp_exam.setText("教学班组成:" + list.get(i).getClass_comp());
        childHolder.tv_school_time_exam.setText("上课时间:" + list.get(i).getSchool_time());
        childHolder.tv_major_exam.setText("专业:" + list.get(i).getMajor());
        childHolder.tv_campus_exam.setText("考试校区:" + list.get(i).getCampus());
        childHolder.tv_teacher_exam.setText("任课教师:" + list.get(i).getTeacher());


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView tv_course_name_exam;
        TextView tv_exam_place;
        TextView tv_exam_time;
    }

    static class ChildHolder {
        TextView grade_class_exam;
        TextView tv_xn_exam;
        TextView tv_xq_exam;
        TextView tv_course_code_exam;
        TextView tv_class_comp_exam;
        TextView tv_school_time_exam;
        TextView tv_major_exam;
        TextView tv_campus_exam;
        TextView tv_teacher_exam;
    }
}

