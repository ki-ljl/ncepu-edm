package com.example.NCEPU.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    String []names;
    int []images;

    public GridViewAdapter(Context context, String []names, int []images) {
        this.context = context;
        this.names = names;
        this.images = images;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid, null);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);
        float screenWidth = sharedPreferences.getInt("width", 0);
        pagerHeight -= 60;
        float gridViewHeight = pagerHeight / 20 * 7;
        ImageView iv = view.findViewById(R.id.iv_grid);
        TextView tv = view.findViewById(R.id.tv_grid);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.height = dip2px(context, (gridViewHeight - 46) / 2);
//        ToastUtil.showMessage(context, (gridViewHeight+""));
//        linearParams.width = (int)screenWidth / 4;
        iv.setLayoutParams(linearParams);
        iv.setImageResource(images[position]);
        tv.setText(names[position]);
        return view;
    }
}
