package com.example.NCEPU.adapter;

import androidx.viewpager.widget.PagerAdapter;


import android.view.View;
import android.view.ViewGroup;

import com.example.NCEPU.base.BasePager;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapter extends PagerAdapter {

    List<BasePager> pagerList = new ArrayList<>();

    public ViewPagerAdapter(List<BasePager> list) {
        pagerList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePager pager = pagerList.get(position);
        View view = pager.rootView;
        if (!pager.isInitData()) {
            pager.initData();
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagerList.get(position).rootView);
    }

    @Override
    public int getCount() {
        return pagerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

