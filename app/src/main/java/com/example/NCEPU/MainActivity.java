package com.example.NCEPU;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.NCEPU.JWUtils.ConnectJWGL;
import com.example.NCEPU.Utils.JellyInterpolator;
import com.example.NCEPU.Utils.MyDatabaseHelper;
import com.example.NCEPU.Utils.TimeUtils;
import com.example.NCEPU.Utils.ToastUtil;

import java.util.Map;

import static com.example.NCEPU.StudentMainActivity.px2dip;


public class MainActivity extends Activity implements OnClickListener {

    public static ConnectJWGL connectJWGL;

    //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    private MyDatabaseHelper myDatabaseHelper;
    private TextView mBtnLogin;
    private EditText textId,textPassword, textPassword1;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mPsw;
    private RadioGroup radioGroup;
//    private RadioButton radioButton_Student, radioButton_Teacher;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ProgressDialog progressDialog = null;
    public Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
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

    private void initView() {
        mBtnLogin = findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = findViewById(R.id.input_layout_name);
        mPsw =  findViewById(R.id.input_layout_psw);
        //mSignUp = findViewById(R.id.signup);
        textId = findViewById(R.id.usersid);
        textPassword = findViewById(R.id.password);
        textPassword1 = findViewById(R.id.password1);
//        radioGroup = findViewById(R.id.rg_Identity);
//        radioButton_Student = findViewById(R.id.radio_stu);
//        radioButton_Teacher = findViewById(R.id.radio_tea);
        progressBar = findViewById(R.id.progressbar_log_in);
       // mSignUp.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public int getStatusBarHeight() {

        Resources resources = this.getResources();

        int resourceId = resources.getIdentifier("status_bar_height","dimen","android");

        int height = resources.getDimensionPixelSize(resourceId);

        return px2dip(this, height);  //24dp

    }

    private void login(String _id, String password1, String password) {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        width = px2dip(this, width); //360dp
        height = px2dip(this, height); //720dp
        int status_bar_height = getStatusBarHeight();
        int pagerHeight = height - status_bar_height - 50;
        try {
            connectJWGL = new ConnectJWGL(_id, password1, password, MainActivity.this);
            //内网登录密码错误，登录失败
            if(connectJWGL.init() == 0) {
                runOnUiThread(() -> ToastUtil.showMessage(MainActivity.this, "内网密码错误！"));
            }else {
                if(MainActivity.connectJWGL.beginLogin()) {
                    Map<String, String> info_map = connectJWGL.getStudentInformation();
                    editor.putInt("width", width);
                    editor.putInt("height", height);
                    editor.putInt("pager", pagerHeight);
                    editor.putString("id", _id);
                    editor.putString("in_net", password1);
                    editor.putString("sign", "Hungry And Humble");
                    editor.putString("jw_password", password);
                    editor.putString("name", info_map.get("name"));
                    editor.putString("stu_id", info_map.get("id"));
                    editor.putString("class", info_map.get("class"));
                    editor.putString("major", info_map.get("major"));
                    editor.putString("sex", info_map.get("sex"));
                    editor.putString("dept", info_map.get("dept"));
                    editor.putString("year", info_map.get("year"));
                    String cookies = connectJWGL.cookies.toString();
                    String cookies_in = connectJWGL.cookies_innet.toString();
                    editor.putString("cookies", cookies);
                    editor.putString("cookies_in", cookies_in);
                    editor.commit();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.VISIBLE);
                        mWidth = mBtnLogin.getMeasuredWidth();
                        mHeight = mBtnLogin.getMeasuredHeight();
                        mName.setVisibility(View.INVISIBLE);
                        mPsw.setVisibility(View.INVISIBLE);
                        //mSignUp.setVisibility(View.INVISIBLE);
                        inputAnimator(mInputLayout, mWidth, mHeight);
                        Intent intent1=new Intent(MainActivity.this,StudentMainActivity.class);
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(intent1);
                    });
                    runOnUiThread(() -> ToastUtil.showMessage(MainActivity.this, "登录成功！"));

                }else {
                    runOnUiThread(() -> ToastUtil.showMessage(MainActivity.this, "教务系统密码错误！"));
                }
            }

        } catch (Exception e) {
            runOnUiThread(() -> ToastUtil.showMessage(MainActivity.this, "内网连接超时,请检查网络是否正常连接或内网是否能正常访问!"));
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_login:
                if(!TimeUtils.isFastClick()) {
                    ToastUtil.showMessage(this, "正在处理,请不要频繁点击!");
                    break;
                }
                String _id=textId.getText().toString();
                String password=textPassword.getText().toString();
                String password1 = textPassword1.getText().toString();
                if(_id.equals("")) {
                    ToastUtil.showMessage(MainActivity.this,"学号不能为空！");
                }else if(password.equals("")) {
                    ToastUtil.showMessage(MainActivity.this,"教务系统密码不能为空！");
                }else if(password1.equals("")) {
                    ToastUtil.showMessage(MainActivity.this,"内网密码不能为空！");
                }else {
                    //使用Dialog
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("提示");
                    progressDialog.setMessage("正在登录...");
                    progressDialog.setIcon(R.drawable.running);
                    new Thread(){
                        public void run(){
                            try{
                                login(_id, password1, password);
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();//线程启动
                    handler = new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            progressDialog.dismiss();
                        }
                    };
                    progressDialog.show();
                }
                break;
        }
    }


    private void inputAnimator(final View view, float w, float h) {

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recovery();
                    }
                }, 3000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }

    private void recovery() {
        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f,1f );
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }
}
