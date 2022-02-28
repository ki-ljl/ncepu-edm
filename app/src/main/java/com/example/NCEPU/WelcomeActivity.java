package com.example.NCEPU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.NCEPU.JWUtils.ConnectJWGL;
import com.example.NCEPU.Utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.NCEPU.StudentMainActivity.px2dip;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    public static String stu_name, stu_class, stu_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    public int getStatusBarHeight() {

        Resources resources = this.getResources();

        int resourceId = resources.getIdentifier("status_bar_height","dimen","android");

        int height = resources.getDimensionPixelSize(resourceId);

        return px2dip(this, height);  //24dp

    }

    public void init() {
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        width = px2dip(this, width); //360dp
        height = px2dip(this, height); //720dp
        int status_bar_height = getStatusBarHeight();
        int pagerHeight = height - status_bar_height - 50;
        if(sharedPreferences.getInt("pager", 0) == 0) {
            editor.putInt("pager", pagerHeight);
            editor.commit();
        }
        if(sharedPreferences.getString("id", null) == null) {
            ToastUtil.showMessage(this,"请先进行登录！");
            final Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
            Timer timer=new Timer();
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            };
            timer.schedule(timerTask,2000);
        }else {
            try {
                Timer timer=new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        String _id = sharedPreferences.getString("id", "");
                        String password = sharedPreferences.getString("in_net", "");
                        String password1 = sharedPreferences.getString("jw_password", "");
                        stu_class = sharedPreferences.getString("class", "");
                        stu_id = sharedPreferences.getString("id", "");
                        stu_name = sharedPreferences.getString("name", "");
                        try {
                            MainActivity.connectJWGL = new ConnectJWGL(_id, password, password1, WelcomeActivity.this);
                            MainActivity.connectJWGL.init();
                            MainActivity.connectJWGL.beginLogin();
                            String cookies = MainActivity.connectJWGL.cookies.toString();
                            String cookies_in = MainActivity.connectJWGL.cookies_innet.toString();
                            sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cookies", cookies);
                            editor.putString("cookies_in", cookies_in);
                            Intent intent=new Intent(WelcomeActivity.this,StudentMainActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showMessage(WelcomeActivity.this, "内网连接超时,请检查网络是否正常连接或内网是否能正常访问!");
                                    new Handler().postDelayed(() -> {
                                        Intent home = new Intent(Intent.ACTION_MAIN);
                                        home.addCategory(Intent.CATEGORY_HOME);
                                        startActivity(home);
                                    }, 3000);
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                };
                timer.schedule(timerTask,1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
