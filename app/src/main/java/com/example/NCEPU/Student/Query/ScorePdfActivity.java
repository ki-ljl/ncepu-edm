package com.example.NCEPU.Student.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.NCEPU.MainActivity;
import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ShowDialogUtil;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.ZC;
import com.example.NCEPU.adapter.PdfAdapter;
import com.example.NCEPU.adapter.ZCAdapter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;


public class ScorePdfActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private LinearLayout ly_back;
    private TextView ncepu;
    private ListView listView;
    private TextView total_tv, major_tv, spec_tv, practice_tv, sch_tv, gpa_tv;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public ProgressDialog progressDialog = null;
    public Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_pdf);
        imageButton = findViewById(R.id.ib_back_pdf);
        imageButton.setOnClickListener(v -> {
            onBackPressed();
        });

        total_tv = findViewById(R.id.text_total_credit);
        major_tv = findViewById(R.id.text_major_credit);
        spec_tv = findViewById(R.id.text_spec_credit);
        practice_tv = findViewById(R.id.text_practice_credit);
        sch_tv = findViewById(R.id.text_sch_credit);
        gpa_tv = findViewById(R.id.text_avg);
        ly_back = findViewById(R.id.ly_back_pdf);
        ncepu = findViewById(R.id.pdf_info);

        setHeight();

        //初始化成绩
        listView = findViewById(R.id.total_grade_table);

        ///使用dialog
        progressDialog = new ProgressDialog(ScorePdfActivity.this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在加载...");
        progressDialog.setIcon(R.drawable.running);
        new Thread(){
            public void run(){
                try{
                    runOnUiThread(() -> {
                        List<Grade> list = null;
                        try {
                            list = MainActivity.connectJWGL.getStudentGrade("", "", "全部");
                            //计算已获得总学分，其中包括必修、专选、实践、校选以及平均学分绩
                            double total = 0, major = 0, spec = 0, practice = 0, sch = 0;
                            double sum = 0, credits = 0;
                            for(Grade grade : list) {
                                String credit = grade.getCredit();
                                String nature = grade.getCourse_nature();
                                String gpa = grade.getGpa();
                                String gradeNature = grade.getGrade_nature();
                                double temp = Double.parseDouble(credit);

                                if(Double.parseDouble(gpa) < 1) {
                                    continue;
                                }
                                total += temp;
                                if(nature.equals("必修课")) {
                                    major += temp;
                                }else if(nature.equals("专选课")) {
                                    spec += temp;
                                }else if(nature.equals("实践课")) {
                                    practice += temp;
                                }else if(nature.equals("校选修课")) {
                                    sch += temp;
                                }
                                double mark = Double.parseDouble(gpa) * 10 + 50;
                                sum += temp * mark;
                                credits += temp;
                            }
                            double avg = sum / credits;
                            DecimalFormat df = new DecimalFormat("#.00");
                            String avgs = df.format(avg);
                            SpannableStringBuilder builder = new SpannableStringBuilder("总学分\n" + total);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 4, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            total_tv.setText(builder);
                            builder = new SpannableStringBuilder("必修\n" + major);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            major_tv.setText(builder);
                            builder = new SpannableStringBuilder("专选\n" + spec);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spec_tv.setText(builder);
                            builder = new SpannableStringBuilder("实践\n" + practice);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            practice_tv.setText(builder);
                            builder = new SpannableStringBuilder("校选\n" + sch);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            sch_tv.setText(builder);
                            builder = new SpannableStringBuilder("平均学分绩\n" + avgs);
                            builder.setSpan(new ForegroundColorSpan(Color.RED), 6, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            gpa_tv.setText(builder);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        PdfAdapter pdfAdapter = new PdfAdapter(ScorePdfActivity.this, list);
                        listView.setAdapter(pdfAdapter);
                        //设置点击事件
                        List<Grade> finalList = list;
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Grade grade = finalList.get(position);
                            ToastUtil.showMessage(ScorePdfActivity.this, grade.getCourse_name() + ":" + grade.getMark());
                        });
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
                ToastUtil.showMessage(ScorePdfActivity.this, "加载完成!");
            }
        };
        progressDialog.show();
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)imageButton.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        imageButton.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)ncepu.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0465));
        ncepu.setLayoutParams(linearParams);

    }

    @Override
    protected void onDestroy() {
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

    public Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;

        } catch (OutOfMemoryError ex) {

        }
        return null;

    }

    public Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if(bitmap!=null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float)newWidth)/width;
            float scaleHeight = ((float)newHeight)/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;

        }
        else{
            return null;
        }
    }
}