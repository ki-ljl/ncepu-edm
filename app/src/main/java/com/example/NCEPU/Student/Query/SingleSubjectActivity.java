package com.example.NCEPU.Student.Query;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.MySQLUtil;
import com.example.NCEPU.Utils.ProgressView;

import java.util.ArrayList;
import java.util.List;


public class SingleSubjectActivity extends AppCompatActivity {

    private TextView rankTextView;
    private boolean flag = false;

    private ProgressView view;
    private SharedPreferences sharedPreferences;
    /**
     * 总成绩
     */
    private int totalScore = 100;
    /**
     * 当前成绩
     */
    private int currentScore = 0;
    /**
     * 转动的速度
     */
    private long speed = 1000;

    private List<String> listSpeeds;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView completeTextView;
    ArrayList<String> course = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_subject);
        findCourse();
        init();
        initView(false);

        completeTextView.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = completeTextView.getCompoundDrawables()[2];
                if(drawable==null) {
                    return false;
                }
                if(event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > completeTextView.getWidth()
                        - completeTextView.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    //接下来就是点击事件，执行数据库查询并显示
                    String currentCourse = completeTextView.getText().toString();
                    findRank(currentCourse);
                }
                return false;
            }
        });

    }

    private void init() {
        rankTextView=findViewById(R.id.rank);
        view = findViewById(R.id.circleProgress);
        completeTextView = findViewById(R.id.auto_comp);
        String[] courses = course.toArray(new String[course.size()]);
        adapter = new ArrayAdapter<>(this, R.layout.auto_list, courses);
        completeTextView.setAdapter(adapter);
    }

    private void initView(boolean flag) {
        view.setTotalScore(totalScore);
        /**
         * 进度条从0到指定数字的动画
         * 除了startAnimator1()方法中用的ValueAnimator.ofInt()，我们还有
         * ofFloat()、ofObject()这些生成器的方法;
         * 我们可以通过ofObject()去实现自定义的数值生成器
         */
        ValueAnimator animator = ValueAnimator.ofFloat(0, currentScore);
        animator.setDuration(speed);
        /**
         *  Interpolators  插值器，用来控制具体数值的变化规律
         *  LinearInterpolator  线性
         */
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                /**
                 * 通过这样一个监听事件，我们就可以获取
                 * 到ValueAnimator每一步所产生的值。
                 *
                 * 通过调用getAnimatedValue()获取到每个时间因子所产生的Value。
                 * */
                float current = (float) valueAnimator.getAnimatedValue();
                if(flag) {
                    view.setmCurrent((int)current);
                }else {
                    view.setmCurrent(0);
                }
            }
        });
        animator.start();

    }


    /**
     * 初始化course
     */
    private void findCourse() {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                MySQLUtil mySQLUtil = new MySQLUtil(SingleSubjectActivity.this);
                mySQLUtil.getConnection("cce-18");
                course = mySQLUtil.getCourseName("final_exam_info");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findRank(String course_name) {
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String major = sharedPreferences.getString("major", "");
        String id = sharedPreferences.getString("id", "");
        Thread thread = new Thread(() -> {
            MySQLUtil mySQLUtil = new MySQLUtil(SingleSubjectActivity.this);
            mySQLUtil.getConnection("cce-18");
            ArrayList<String> res = null;
            res = mySQLUtil.getRank(course_name, major, id, true);
            if(res == null) {
                runOnUiThread(() -> {
                    String hint = "排名：";
                    currentScore = 0;
                    rankTextView.setText(hint);
                    ToastUtil.showMessage(SingleSubjectActivity.this, "该课程不存在");
                });
            }else {
                String rank = res.get(0);
                String score = res.get(1);
                String hint="排名："+rank;
                flag = true;
                currentScore = (int)Float.parseFloat(score);
                rankTextView.setText(hint);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initView(flag);
        flag = false;
    }
}
