package com.example.NCEPU;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.adapter.MyFragmentPagerAdapter;

public class StudentMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {

    private RadioGroup rg_tab_bar;
    private RadioButton rb_main;
    private RadioButton rb_query;
//    private RadioButton rb_predict;
    private RadioButton rb_user;
    private ViewPager vpager;

    private MyFragmentPagerAdapter mAdapter;

    public static int pagerHeight;

    //代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_query.setChecked(true);

    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public int getStatusBarHeight() {

        Resources resources = this.getResources();

        int resourceId = resources.getIdentifier("status_bar_height","dimen","android");

        int height = resources.getDimensionPixelSize(resourceId);

        return height;

    }

    private void bindViews() {
        rg_tab_bar =  findViewById(R.id.rg_tab_bar);
//        rb_main =  findViewById(R.id.rb_main);
        rb_query =  findViewById(R.id.rb_query);
//        rb_predict =  findViewById(R.id.rb_predict);
        rb_user =  findViewById(R.id.rb_user);
        rg_tab_bar.setOnCheckedChangeListener(this);

        vpager = findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);

//        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("pager", px2dip(StudentMainActivity.this, vpager.getMeasuredHeight()));
//        editor.commit();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

//        int width=metric.widthPixels; // 宽度（PX）
//        int height=metric.heightPixels; // 高度（PX）
//
//        float density = metric.density; // 密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi; // 密度DPI（120 / 160 / 240）
//        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
//        int screenWidth = (int) (width/density);//屏幕宽度(dp)
//        int screenHeight = (int)(height/density);//屏幕高度(dp)
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        height = px2dip(this, height);
        vpager.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            pagerHeight = px2dip(StudentMainActivity.this, vpager.getMeasuredHeight());
//            SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt("pager", px2dip(StudentMainActivity.this, vpager.getMeasuredHeight()));
//            editor.commit();
//            ToastUtil.showMessage(StudentMainActivity.this, "getMeasuredHeight="+px2dip(StudentMainActivity.this, vpager.getMeasuredHeight()) + " 状态栏" +
//                    "高度:" + px2dip(StudentMainActivity.this, getStatusBarHeight()) + " 底部按钮高度:" + px2dip(StudentMainActivity.this, rg_tab_bar.getMeasuredHeight()) + "屏幕高度:" + finalHeight);
        });
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_query:
                vpager.setCurrentItem(PAGE_ONE);
                break;
//            case R.id.rb_predict:
//                vpager.setCurrentItem(PAGE_TWO);
//                break;
            case R.id.rb_user:
                vpager.setCurrentItem(PAGE_TWO);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_query.setChecked(true);
                    break;
//                case PAGE_TWO:
//                    rb_predict.setChecked(true);
//                    break;
                case PAGE_TWO:
                    rb_user.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
