/**
 * Date   : 2021/2/1 21:11
 * Author : KI
 * File   : GridViewPlanAdapter
 * Desc   : plan
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;

public class GridViewPlanAdapter extends BaseAdapter {
    private Context context;
    String []names;

    public GridViewPlanAdapter(Context context, String []names) {
        this.context = context;
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_plan, null);
        TextView tv = view.findViewById(R.id.tv_grid_plan);
        tv.setText(names[position]);
        return view;
    }
}


