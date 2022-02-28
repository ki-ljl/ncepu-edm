package com.example.NCEPU.Utils;

import android.widget.LinearLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.NCEPU.R;

public class ItemView extends LinearLayout {
    private boolean isShowBottomLine = true;
    private boolean isShowLeftIcon = true;
    private boolean isShowRightArrow = true;
    private ImageView leftIcon;//列表左侧的图标
    private TextView leftTitle;//左侧的标题
    private TextView rightDesc;//右侧描述
    private ImageView rightArrow;//右侧图标
    private ImageView bottomLine;//底部下划线图标
    private RelativeLayout rootView;//整体item的view

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //添加布局文件
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_layout, null);
        addView(view);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        //找到控件
        leftIcon = findViewById(R.id.left_icon);
        leftTitle = findViewById(R.id.left_title);
        rightDesc = findViewById(R.id.right_desc);
        rightArrow = findViewById(R.id.right_arrow);
        bottomLine = findViewById(R.id.bottom_line);
        rootView= findViewById(R.id.root_item);
        //设置控件属性
        isShowBottomLine = ta.getBoolean(R.styleable.ItemView_show_bottom_line, true);//得到是否显示底部下划线属性
        isShowLeftIcon = ta.getBoolean(R.styleable.ItemView_show_left_icon, true);//得到是否显示左侧图标属性标识
        isShowRightArrow = ta.getBoolean(R.styleable.ItemView_show_right_arrow, true);//得到是否显示右侧图标属性标识

        leftIcon.setBackground(ta.getDrawable(R.styleable.ItemView_left_icon));//设置左侧图标
        leftIcon.setVisibility(isShowLeftIcon ? View.VISIBLE : View.INVISIBLE);//设置左侧箭头图标是否显示

        leftTitle.setText(ta.getString(R.styleable.ItemView_left_text));//设置左侧标题文字
        rightDesc.setText(ta.getString(R.styleable.ItemView_right_text));//设置右侧文字描述

        rightArrow.setVisibility(isShowRightArrow ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
        bottomLine.setVisibility(isShowBottomLine ? View.VISIBLE : View.INVISIBLE);//设置底部图标是否显示

        //设置点击事件
        //给整个item设置点击事件
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClick(rightDesc.getText().toString());
            }
        });

        //给最右侧的小箭头设置点击事件
        rightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClick(rightDesc.getText().toString());
            }
        });

        ta.recycle();
    }



    //设置左侧图标
    public void setLeftIcon(int value) {
        Drawable drawable=getResources().getDrawable(value);
        leftIcon.setBackground(drawable);
    }

    //设置左侧标题文字
    public void setLeftTitle(String value) {
        leftTitle.setText(value);
    }

    //获取右侧描述文字
    public String getRightDesc() { return rightDesc.getText().toString(); }


    //设置右侧描述文字
    public void setRightDesc(String value) {
        rightDesc.setText(value);
    }
    //设置右侧箭头
    public void setShowRightArrow(boolean value) {
        rightArrow.setVisibility(value ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
    }

    //设置是否显示下画线
    public void setShowBottomLine(boolean value) {
        bottomLine.setVisibility(value ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
    }

    public interface itemClickListener{
        void itemClick(String text);
    }

    private itemClickListener listener;

    //向外暴漏接口
    public void setItemClickListener(itemClickListener listener){
        this.listener=listener;
    }

}

