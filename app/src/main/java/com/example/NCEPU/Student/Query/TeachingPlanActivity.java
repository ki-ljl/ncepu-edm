package com.example.NCEPU.Student.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Student.Predict.RFActivity;
import com.example.NCEPU.Utils.Plan;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.adapter.PlanAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class TeachingPlanActivity extends AppCompatActivity {

    private ExpandableListView expand_lv_plan;
    private LinearLayout ly_back_plan;
    private ImageButton ib_back_plan;
    private TextView tv_info_plan;

    public ProgressDialog progressDialog = null;
    public Handler handler = null;
    public List<Plan> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching_plan);
        initViews();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void initViews() {
        expand_lv_plan = findViewById(R.id.expand_lv_plan);
        ly_back_plan = findViewById(R.id.ly_back_plan);
        ib_back_plan = findViewById(R.id.ib_back_plan);
//        tv_info_plan = findViewById(R.id.tv_info_plan);

        ib_back_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setHeight();
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back_plan.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back_plan.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ib_back_plan.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        ib_back_plan.setLayoutParams(linearParams);

    }

    private void initData() {
        progressDialog = new ProgressDialog(TeachingPlanActivity.this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在加载...");
        progressDialog.setIcon(R.drawable.running);
        new Thread(){
            public void run(){
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                list = connectJWGL.getLessonPlan();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            expand_lv_plan.setAdapter(new PlanAdapter(TeachingPlanActivity.this, list));
                            ShowDialogUtil.closeProgressDialog();
                        }
                    });
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
                ToastUtil.showMessage(TeachingPlanActivity.this, "加载完成!");
            }
        };
        progressDialog.show();
    }
}