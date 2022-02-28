/**
 * Date   : 2021/2/25 21:12
 * Author : KI
 * File   : PredictAdapter
 * Desc   : tableadapter
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.PredictUtil;

import java.util.ArrayList;
import java.util.List;


public class PdfAdapter extends BaseAdapter {

    private List<Grade> list;
    private LayoutInflater inflater;

    public PdfAdapter(Context context, List<Grade> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(list != null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Grade grade = (Grade) this.getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.pdf_list_item, null);
            viewHolder.course_name = convertView.findViewById(R.id.text_course_name);
            viewHolder.nature = convertView.findViewById(R.id.text_course_nature);
            viewHolder.year_semester = convertView.findViewById(R.id.text_year_semester);
            viewHolder.credit = convertView.findViewById(R.id.text_credit);
            viewHolder.mark = convertView.findViewById(R.id.text_grade);
            viewHolder.gpa = convertView.findViewById(R.id.text_gpa);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.course_name.setText(grade.getCourse_name());
        viewHolder.nature.setText(grade.getCourse_nature());
        viewHolder.year_semester.setText(grade.getXn() + "/" + grade.getXq());
        viewHolder.credit.setText(grade.getCredit());
        viewHolder.mark.setText(grade.getMark());
        viewHolder.gpa.setText(grade.getGpa());

        return convertView;
    }

    public static class ViewHolder{
        public TextView course_name;
        public TextView nature;
        public TextView year_semester;
        public TextView credit;
        public TextView mark;
        public TextView gpa;
    }

}
