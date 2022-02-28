package com.example.NCEPU.Student.Query;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.NCEPU.JWUtils.ConnectSZHD;
import com.example.NCEPU.R;
import com.example.NCEPU.Student.TimeTable.TimeTableActivity;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.GlideImageLoader;
import com.example.NCEPU.Utils.MyGridView;
import com.example.NCEPU.Utils.TimeUtils_2;
import com.example.NCEPU.adapter.GridViewAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;


public class QueryFragment extends Fragment {

    public static ConnectSZHD connectSZHD;
    public static boolean flag = false;
    public String year = "";
    private MyGridView eduGridView;
    private MyGridView visionGridView;
    private Banner banner;
    private String dept = "";
    private String gradeYear = "";
    private ImageView imageView;

    private CollapsingToolbarLayout layout;
    private LinearLayout x;

    public ProgressDialog progressDialog = null;
    public Handler handler = null;

    //定位
    private static final int REQUEST_CODE_GPS = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Nullable
    @Override

    //类似于Activity里面的setContentView();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query,container,false);
        initViews(view);
        initBanner(view);
        setHeight();
        onClick();
//        startLocation();
        return view;
    }

    public void startLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        // https://developer.android.google.cn/guide/components/intents-filters?hl=zh-cn#ExampleSend
        // https://developer.android.google.cn/reference/android/content/Intent?hl=zh-cn#resolveActivity(android.content.pm.PackageManager)
        // 判断是否有合适的应用能够处理该 Intent，并且可以安全调用 startActivity()。
        startActivityForResult(intent, REQUEST_CODE_GPS);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GPS) {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Toast.makeText(MainActivity.this, "用户打开定位服务", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(MainActivity.this, "用户关闭定位服务", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void initViews(View v) {
        layout = v.findViewById(R.id.full_match);
        x = v.findViewById(R.id.info);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        dept = sharedPreferences.getString("dept", "");
        gradeYear  =sharedPreferences.getString("year", "");
        eduGridView = v.findViewById(R.id.gv_education);
        String []namesEdu = {"成绩查询", "课表查询", "考试安排", "GPA", "培养方案", "成绩总表", "综合测评"};
        int []imagesEdu = {R.drawable.query_mark, R.drawable.query_timetable, R.drawable.query_exam, R.drawable.query_credit,
                R.drawable.query_cult, R.drawable.grade_total_1, R.drawable.query_zc};
        GridViewAdapter gridViewAdapter1 = new GridViewAdapter(getActivity(), namesEdu, imagesEdu);
        eduGridView.setAdapter(gridViewAdapter1);
        visionGridView = v.findViewById(R.id.gv_vision);
        String []namesVis = {"成绩占比", "成绩比较", "GPA走势", "单科分析", "专业排名", "挂科分析", "单科排名", "个人分析"};
        int []imagesVis = {R.drawable.grade_rate, R.drawable.grade_pk, R.drawable.grade_tend, R.drawable.single_major_2,
        R.drawable.major_sort, R.drawable.grade_loss, R.drawable.query_single_sort, R.drawable.radar};
        GridViewAdapter gridViewAdapter2 = new GridViewAdapter(getActivity(), namesVis, imagesVis);
        visionGridView.setAdapter(gridViewAdapter2);
        eduGridView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                ToastUtil.showMessage(getContext(), "hh="+px2dip(getActivity(), eduGridView.getMeasuredHeight()));
            }
        });
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 645);
        pagerHeight -= 60;
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) banner.getLayoutParams();
        linearParams.height = dip2px(getContext(), pagerHeight / 20 * 6);
        banner.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams) eduGridView.getLayoutParams();
        linearParams.height = dip2px(getContext(), pagerHeight / 20 * 7);
        eduGridView.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams) visionGridView.getLayoutParams();
        linearParams.height = dip2px(getContext(), pagerHeight / 20 * 7);
        visionGridView.setLayoutParams(linearParams);
    }

    //初始化banner
    private void initBanner(View v) {
        ArrayList<Integer> list = new ArrayList<>();
        //list.add(R.drawable.ncepu_1);
        //list.add(R.id.tv_info_gpa);
        list.add(R.drawable.ncepu_2);
        list.add(R.drawable.ncepu_3);
        list.add(R.drawable.ncepu_4);
        list.add(R.drawable.ncepu_5);
        list.add(R.drawable.ncepu_6);
//        list.add(R.drawable.ncepu_7);
//        list.add(R.drawable.ncepu_8);
        banner = v.findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(list);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
//        banner.setOnBannerListener(new OnBannerListener() {
//            @Override
//            public void OnBannerClick(int position) {
//                ToastUtil.showMessage(getActivity(), "该图片不包括内容！");
//
//            }
//        });
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    private void firstLoginSZHD() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog,null);
        EditText sz_password=view1.findViewById(R.id.sz_password);
        Button btnLogin = view1.findViewById(R.id.btn_login_sz);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用dialog
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在登录...");
                progressDialog.setIcon(R.drawable.running);
                new Thread(){
                    public void run(){
                        try{
                            String password = sz_password.getText().toString();  //数字华电密码
//                            password = password.replace(" ", "");
                            String id = sharedPreferences.getString("id", "");
                            try {
                                connectSZHD = new ConnectSZHD(getContext(), id, password);
                                if(connectSZHD.login()) {
                                    //存入密码
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    String pass = sharedPreferences.getString("id", "");
                                    pass += "_szhd";
                                    editor.putString(pass, password);
                                    editor.commit();
                                    Intent intent = new Intent(getActivity(), ZCActivity.class);
                                    startActivity(intent);
                                }else {
                                    getActivity().runOnUiThread(() -> ToastUtil.showMessage(getContext(), "密码错误！"));
                                }
                            } catch (Exception e) {
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
                    }
                };
                progressDialog.show();
            }
        });
        builder.setTitle("请先登录!").setView(view1).show();
    }

    private void onClick() {
        eduGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = null;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), GradeActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        intent = new Intent(getActivity(), TimeTableActivity.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(getActivity(), ExamActivity.class);
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(getActivity(), GPAActivity.class);
                        startActivity(intent);
                        break;

                    case 4:
                        //使用ProgressDialog
                        if(TimeUtils_2.isFastClick()) {
                            intent = new Intent(getActivity(), TeachingPlanActivity.class);
                            startActivity(intent);
                        }else {
                            ToastUtil.showMessage(getContext(), "正在处理,请不要频繁点击!");
                        }
                        break;

                    case 5:
                        intent = new Intent(getActivity(), ScorePdfActivity.class);
                        startActivity(intent);
                        break;

                    case 6:
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;
//                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//                        String pass = sharedPreferences.getString("id", "");
//                        pass += "_szhd";
//                        if(sharedPreferences.getString(pass, null) == null) {
//                            firstLoginSZHD();
//                        }else {
//                            //已经登录过，直接读取密码，使用ProgressDialog
//                            progressDialog = new ProgressDialog(getActivity());
//                            progressDialog.setTitle("提示");
//                            progressDialog.setMessage("正在登录...");
//                            progressDialog.setIcon(R.drawable.running);
//                            new Thread(){
//                                public void run(){
//                                    try{
//                                        String num = sharedPreferences.getString("id", null);
//                                        String password = sharedPreferences.getString(num + "_szhd", null);
//                                        try {
//                                            connectSZHD = new ConnectSZHD(getContext(), num, password);
//                                            connectSZHD.login();
//                                            flag = true;
//                                            intent = new Intent(getActivity(), ZCActivity.class);
//                                            startActivity(intent);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        Message msg = new Message();
//                                        msg.what = 1;
//                                        handler.sendMessage(msg);
//                                    }catch(Exception e){
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }.start();//线程启动
//                            handler = new Handler(){
//                                @Override
//                                public void handleMessage(@NonNull Message msg) {
//                                    super.handleMessage(msg);
//                                    progressDialog.dismiss();
//                                    ToastUtil.showMessage(getActivity(), "登录成功!");
//                                }
//                            };
//                            progressDialog.show();
//                        }
//                        break;

//                    case 7:
//                        intent = new Intent(getActivity(), TeachingEvaluationActivity.class);
//                        startActivity(intent);
//                        break;
                }
            }
        });

        //数据可视化
        visionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = null;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), GradeRateActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), GradeGpaActivity.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(getActivity(), TendencyActivity.class);
                        startActivity(intent);
                        break;

                    case 3:
//                        if(dept.equals("控制与计算机工程学院") && gradeYear.equals("2018")) {
//                            intent = new Intent(getActivity(), SingleSubjectMajorActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院2018级同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 4:
//                        if(dept.equals("控制与计算机工程学院") && gradeYear.equals("2018")) {
//                            intent = new Intent(getActivity(), MajorGPASortActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院2018级同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 5:
//                        if(dept.equals("控制与计算机工程学院") && gradeYear.equals("2018")) {
//                            intent = new Intent(getActivity(), GradeFailedActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院2018级同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 6:
//                        if(dept.equals("控制与计算机工程学院") && gradeYear.equals("2018")) {
//                            intent = new Intent(getActivity(), SingleSubjectActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院2018级同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 7:
                        intent = new Intent(getActivity(), AbilityActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

}
