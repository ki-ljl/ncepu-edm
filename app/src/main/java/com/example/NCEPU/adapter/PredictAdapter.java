/**
 * Date   : 2021/2/25 21:12
 * Author : KI
 * File   : PredictAdapter
 * Desc   : tableadapter
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.PredictUtil;


public class PredictAdapter extends BaseAdapter {

    private List<PredictUtil> list;
    private LayoutInflater inflater;

    public PredictAdapter(Context context, List<PredictUtil> list){
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

        PredictUtil predictUtil = (PredictUtil) this.getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder.id = convertView.findViewById(R.id.text_id);
            viewHolder.pre_item = convertView.findViewById(R.id.text_pre_items);
            viewHolder.back_item = convertView.findViewById(R.id.text_back_items);
            viewHolder.predict = convertView.findViewById(R.id.text_pre);
            viewHolder.state = convertView.findViewById(R.id.text_state);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.id.setText(predictUtil.getId());
        ArrayList<String> pre_items = predictUtil.getPre_item();
        String pre = "";
        for(int t = 0; t < pre_items.size(); t++) {
            String x = pre_items.get(t);
            if(t != pre_items.size() - 1) {
                pre += (x + ",\n");
            }else {
                pre += x;
            }
        }
        viewHolder.pre_item.setText(pre);

        ArrayList<String> back_items = predictUtil.getBack_item();
        String back = "";
        for(int t = 0; t < back_items.size(); t++) {
            String x = back_items.get(t);
            if(t != back_items.size() - 1) {
                back += (x + ", ");
            }else {
                back += x;
            }
        }
        viewHolder.back_item.setText(back);

        ArrayList<String> predicts = predictUtil.getPredict();
        String predict = "";
        for(int t = 0; t < predicts.size(); t++) {
            String x = predicts.get(t);
            if(t != predicts.size() - 1) {
                predict += (x + ", ");
            }else {
                predict += x;
            }
        }
        viewHolder.predict.setText(predict);
        SpannableStringBuilder builder = new SpannableStringBuilder(predictUtil.getState());
        builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.state.setText(builder);

        return convertView;
    }

    public static class ViewHolder{
        public TextView id;
        public TextView pre_item;
        public TextView back_item;
        public TextView predict;
        public TextView state;
    }

}
