package com.example.NCEPU.Student.TimeTable.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.NCEPU.R;
import com.example.NCEPU.Student.TimeTable.colleges.base.CollegeFactory;
import com.example.NCEPU.Student.TimeTable.util.Config;
import com.example.NCEPU.Student.TimeTable.util.HttpUtils;
import com.example.NCEPU.Student.TimeTable.util.Utils;

public class LoginActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    private Handler mHandler = new Handler();
    private FragmentManager fragmentManager;
    private LoginFragment loginFragment=null;
    private ItemFragment itemFragment=null;
    private boolean judgeFlag = true;//判断网络是否可用的循环退出标志，方便结束线程

    public static final String EXTRA_UPDATE_TIMETABLE = "update_timetable";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        fragmentManager = getSupportFragmentManager();//初始化管理者
        String name = Config.getCollegeName();
        if (!name.isEmpty() && CollegeFactory.getCollegeNameList().contains(name)) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, getLoginFragment())
                    .commit();

        } else {
            //选择学校
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, getItemFragment())
                    .commit();
        }
    }

    public LoginFragment getLoginFragment() {
        return loginFragment==null?new LoginFragment():loginFragment;
    }

    public ItemFragment getItemFragment() {
        return itemFragment==null?ItemFragment.newInstance(1):itemFragment;
    }

    private void init() {
        setActionBar();
        ImageView bgIv = findViewById(R.id.iv_bg);
        Utils.setBackGround(this, bgIv);

        Config.readSelectCollege(this);

    }

    private void judgeConnected() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (judgeFlag) {
                    if (!HttpUtils.isNetworkConnected()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "当前网络不可用，请检查网络设置！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        try {
                            Thread.sleep(30000);//每30秒循环一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_login);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }else if(id==R.id.menu_select_college){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, getItemFragment())
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通知主界面更新
     */
    private void setUpdateResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATE_TIMETABLE, true);
        setResult(RESULT_OK, intent);
    }

    /**
     * 隐藏键盘
     */
    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v && imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onListFragmentInteraction(String item) {
        Config.setCollegeName(item);
        Config.saveSelectCollege(this);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, getLoginFragment())
                .commit();
    }
}
