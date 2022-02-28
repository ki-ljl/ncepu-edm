package com.example.NCEPU.base;

import android.content.Context;
import android.view.View;

/**
 * Created by LLL on 2018/2/25 0025.
 */

public abstract class BasePager {

    public final Context context;

    public View rootView;

    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();

    /**
     * 当子页面初始化数据，联网请求或者绑定数据的时候需要重写该方法
     */
    public void initData(){
        isInitData = true;
    }

    public boolean isInitData(){
        return isInitData;
    }


}

