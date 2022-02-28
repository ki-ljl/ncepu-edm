package com.example.NCEPU.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.NCEPU.Student.Predict.PredictFragment;
import com.example.NCEPU.Student.Query.QueryFragment;
import com.example.NCEPU.Student.User.UserFragment;
import com.example.NCEPU.StudentMainActivity;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 3;

    private PredictFragment predictFragment;
    private QueryFragment queryFragment;
    private UserFragment userFragment;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        predictFragment=new PredictFragment();
        queryFragment=new QueryFragment();
        userFragment=new UserFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case StudentMainActivity.PAGE_ONE:
                fragment = queryFragment;
                break;
            case StudentMainActivity.PAGE_TWO:
                fragment = predictFragment;
                break;
            case StudentMainActivity.PAGE_THREE:
                fragment = userFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}
