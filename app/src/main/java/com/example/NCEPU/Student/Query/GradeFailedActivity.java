package com.example.NCEPU.Student.Query;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.GradeFailed;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.FlipShareView;
import com.example.NCEPU.Utils.MySQLUtil;
import com.example.NCEPU.Utils.ShareItem;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.adapter.GradeFailedAdapter;

import java.sql.SQLException;
import java.util.List;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class GradeFailedActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton ib_back;

    private Button btn_query;
    private LinearLayout ly_back, ly_multiply;
    private ExpandableListView expandableListView;

    private Button schoolYear, semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_failed);
        initViews();
        setListeners();
        setHeight();
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ib_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        ib_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ly_multiply.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_multiply.setLayoutParams(linearParams);

        //button=0.0589=48dp
        linearParams = (LinearLayout.LayoutParams)schoolYear.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        schoolYear.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)semester.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0589));
        semester.setLayoutParams(linearParams);
        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)btn_query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        btn_query.setLayoutParams(linearParams);
    }

    private void initViews() {
        ib_back = findViewById(R.id.ib_back_failed);
        btn_query = findViewById(R.id.btn_query_failed);
        ly_back = findViewById(R.id.ly_back_failed);
        ly_multiply = findViewById(R.id.ly_multiply_failed);
        expandableListView = findViewById(R.id.expand_lv_failed);
        schoolYear = findViewById(R.id.school_year_failed);
        semester = findViewById(R.id.semester_failed);

        ib_back.setOnClickListener(v -> onBackPressed());
    }

    private void setListeners() {
        schoolYear.setOnClickListener(this);
        semester.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.school_year_failed:
                @SuppressLint("WrongConstant") final FlipShareView shareYear = new FlipShareView.Builder(this, schoolYear)
                        .addItem(new ShareItem("2024-2025", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2023-2024", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2022-2023", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2021-2022", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2020-2021", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2019-2020", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2018-2019", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2017-2018", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2016-2017", Color.BLACK, 0xffEEEEFF))
                        .setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareYear.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        schoolYear.setText(shareYear.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;

            case R.id.semester_failed:
                @SuppressLint("WrongConstant") final FlipShareView shareSemester = new FlipShareView.Builder(this, semester)
                        .addItem(new ShareItem("1", Color.BLACK, 0xffEEEEFF))
                        .addItem(new ShareItem("2", Color.BLACK, 0xffEEEEFF))
                        .setSeparateLineColor(0x60000000).setItemDuration(0)
                        .setAnimType(FlipShareView.AUTOFILL_TYPE_LIST)
                        .create();
                shareSemester.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        semester.setText(shareSemester.mItemList.get(position).title);
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;


            case R.id.btn_query_failed:
                String year = schoolYear.getText().toString();
                String semester_ = semester.getText().toString();
                if(year.equals("学年")) {
                    ToastUtil.showMessage(this,"请选择学年！");
                }else if(semester_.equals("学期")) {
                    ToastUtil.showMessage(this, "请选择学期！");
                }else {
                    //
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                    String major = sharedPreferences.getString("major", "");
                    Thread thread = new Thread(() -> {
                        MySQLUtil mySQLUtil = new MySQLUtil(this);
                        mySQLUtil.getConnection("cce-18");
                        List<GradeFailed> list = null;
                        try {
                            list = mySQLUtil.getFailed(year, semester_, major);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        List<GradeFailed> finalList = list;
                        if(finalList == null) {
                            runOnUiThread(() -> {
                                ToastUtil.showMessage(this, "本学期没有挂科情况!");
                            });
                        }else {
                            runOnUiThread(() -> {
                                GradeFailedAdapter adapter = new GradeFailedAdapter(this, finalList);
                                expandableListView.setAdapter(adapter);
                                ShowDialogUtil.closeProgressDialog();
                            });
                        }

                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}