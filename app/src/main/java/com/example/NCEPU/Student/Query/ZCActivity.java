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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Student.Predict.RFActivity;
import com.example.NCEPU.Student.Query.QueryFragment;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.Utils.TimeUtils_1;
import com.example.NCEPU.Utils.ZC;
import com.example.NCEPU.adapter.ZCAdapter;

import java.io.IOException;
import java.util.List;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class ZCActivity extends AppCompatActivity {

    private ExpandableListView expand_lv_zc;
    private LinearLayout ly_back;
    private ImageButton ib_back_zc;
    private TextView tv_info_zc;
    private Button btn;

    public ProgressDialog progressDialog = null;
    public Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_c);
        initViews();
        setHeight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ib_back_zc.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        ib_back_zc.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)btn.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        btn.setLayoutParams(linearParams);

    }

    private void initViews() {
        expand_lv_zc = findViewById(R.id.expand_lv_zc);
        ly_back = findViewById(R.id.ly_back_zc);
        ib_back_zc = findViewById(R.id.ib_back_zc);
//        tv_info_zc = findViewById(R.id.tv_info_zc);
        btn = findViewById(R.id.btn_query_zc);

        ib_back_zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///使用dialog
                progressDialog = new ProgressDialog(ZCActivity.this);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在加载...");
                progressDialog.setIcon(R.drawable.running);
                new Thread(){
                    public void run(){
                        try{
                            try {
                                List<ZC> list = QueryFragment.connectSZHD.getZC();
                                if (list.size() == 0) {
                                    ToastUtil.showMessage(v.getContext(), "没有查到记录！");
                                }else {
                                    runOnUiThread(() -> {
                                        expand_lv_zc.setAdapter(new ZCAdapter(v.getContext(), list));
                                        ShowDialogUtil.closeProgressDialog();
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                        ToastUtil.showMessage(ZCActivity.this, "加载完成!");
                    }
                };
                progressDialog.show();
            }
        });
    }
}