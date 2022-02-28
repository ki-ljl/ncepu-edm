package com.example.NCEPU.Student.TimeTable.ui.editcourse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.NCEPU.R;
import com.example.NCEPU.Student.TimeTable.bean.Course;
import com.example.NCEPU.Student.TimeTable.TimeTableActivity;
import com.example.NCEPU.Student.TimeTable.util.FileUtils;
import com.example.NCEPU.Student.TimeTable.util.Utils;


import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_UPDATE_TIMETABLE = "update_timetable";
    private static List<String> sWeekItems;
    private List<String> sStartItems;
    private List<String> sEndItems;

    private TextView mClassNumTextView;
    private EditText mNameEditText;
    private EditText mClassRoomEditText;
    private TextView mWeekOfTermTextView;
    private EditText mTeacherEditText;
    private OptionsPickerView pvOptions;

    private Course mCourse;

    public static final String EXTRA_COURSE_INDEX = "course_index";
    public static final String EXTRA_Day_OF_WEEK = "day_of_week";
    public static final String EXTRA_CLASS_START = "class_start";

    /**
     * 保存在MainActivity.WeekOfTerm中的索引值
     */
    private int mIndex;

    private int mClassStart;
    private int mClassEnd;
    private int mDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mClassNumTextView = findViewById(R.id.tv_class_num);
        mNameEditText = findViewById(R.id.name_editText);
        mClassRoomEditText = findViewById(R.id.et_class_room);
        mWeekOfTermTextView = findViewById(R.id.tv_week_of_term);
        mTeacherEditText = findViewById(R.id.et_teacher);
        setData();

        setActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            mIndex = intent.getIntExtra(EXTRA_COURSE_INDEX, -1);
            mDayOfWeek = intent.getIntExtra(EXTRA_Day_OF_WEEK, 0);
            mClassStart = intent.getIntExtra(EXTRA_CLASS_START, 0);
        }
        if (mIndex != -1) {
            try {
                mCourse = (Course) TimeTableActivity.sCourseList.get(mIndex).clone();
                mClassStart = mCourse.getClassStart();
                mClassEnd = mClassStart + mCourse.getClassLength() - 1;
                mDayOfWeek = mCourse.getDayOfWeek();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            setDefaultValue();
        } else {
            if (mDayOfWeek != 0) {
                mClassEnd = mClassStart + 1;
                mClassNumTextView.setText(
                        String.format(getString(R.string.schedule_section),
                                sWeekItems.get(mDayOfWeek - 1), mClassStart, mClassEnd));
            } else {
                mDayOfWeek = 1;
                mClassStart = 1;
                mClassEnd = 1;
            }

            mCourse = new Course();
        }
        setCardViewAlpha();
        ImageView imageView = findViewById(R.id.iv_bg_edit);

        Utils.setBackGround(this, imageView);

        mClassNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInput();//关闭软键盘防止挡住选择控件
                int start = mCourse.getClassStart();
                if (start == -1)
                    initOptionsPicker();
                else
                    initOptionsPicker();
            }
        });
        mWeekOfTermTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final WeekOfTermSelectDialog dialog = new WeekOfTermSelectDialog(EditActivity.this, mCourse.getWeekOfTerm());

                System.out.println("first:" + String.valueOf(mCourse.getWeekOfTerm()));

                dialog.setPositiveBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCourse.setWeekOfTerm(dialog.getWeekOfTerm());
                        System.out.println("last:" + String.valueOf(mCourse.getWeekOfTerm()));
                        mWeekOfTermTextView.setText(Utils.getFormatStringFromWeekOfTerm(mCourse.getWeekOfTerm()));
                        dialog.dismiss();
                    }
                });
                dialog.setNativeBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    /**
     * 设置CardView透明度
     */
    private void setCardViewAlpha() {
        CardView cardView = findViewById(R.id.cv_edit_1);
        Utils.setCardViewAlpha(cardView);
        cardView = findViewById(R.id.cv_edit_2);
        Utils.setCardViewAlpha(cardView);
    }

    /**
     * 通知界面更新
     */
    private void setUpdateResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATE_TIMETABLE, true);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 从界面中读取课程信息
     *
     * @return 是否读取成功
     */
    private boolean setCourseFromView() {
        String name = mNameEditText.getText().toString();
        String classroom = mClassRoomEditText.getText().toString();
        String teacher = mTeacherEditText.getText().toString();
        if (name.isEmpty() || classroom.isEmpty() || teacher.isEmpty() ||
                mDayOfWeek == 0 || mClassStart == 0 || mClassEnd == 0) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        mCourse.setName(name);
        mCourse.setClassRoom(classroom);
        mCourse.setClassStart(mClassStart);
        mCourse.setDayOfWeek(mDayOfWeek);
        mCourse.setClassLength(mClassEnd - mClassStart + 1);

        mCourse.setTeacher(teacher);
        return true;
    }

    /**
     * 保存课程信息到本地文件
     */
    private void saveCourse() {
        if (setCourseFromView()) {
            if (mIndex == -1) {
                TimeTableActivity.sCourseList.add(getInsertIndex(), mCourse);
            } else {
                TimeTableActivity.sCourseList.set(mIndex, mCourse);
            }
            new FileUtils<List<Course>>().saveToJson(this, TimeTableActivity.sCourseList, FileUtils.TIMETABLE_FILE_NAME);
            setUpdateResult();
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return 得到新增课程应该插入到List的位置
     * 按照一周中课程开始时间排序
     */
    private int getInsertIndex() {//按上课顺序插入
        List<Course> courseList = TimeTableActivity.sCourseList;
        int dayOfWeek = mCourse.getDayOfWeek();
        int class_start = mCourse.getClassStart();
        int size = courseList.size();
        int i;
        for (i = 0; i < size; i++) {
            Course course = courseList.get(i);
            if (dayOfWeek == course.getDayOfWeek() && class_start < course.getClassStart())
                break;
        }
        return i;
    }

    /**
     * 隐藏键盘
     */
    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 初始化ActionBar
     */
    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.course_edit);
    }

    /**
     * 设置TextView的初始值
     */
    private void setDefaultValue() {
        mTeacherEditText.setText(mCourse.getTeacher());
        mNameEditText.setText(mCourse.getName());
        int weekOption = Utils.getWeekOptionFromWeekOfTerm(mCourse.getWeekOfTerm());


        mWeekOfTermTextView.setText(Utils.getFormatStringFromWeekOfTerm(mCourse.getWeekOfTerm()));

        mClassRoomEditText.setText(mCourse.getClassRoom());

        int class_start = mCourse.getClassStart();
        int class_end = class_start + mCourse.getClassLength() - 1;
        String week = sWeekItems.get(mCourse.getDayOfWeek() - 1);

        mClassNumTextView.setText(
                String.format(getString(R.string.schedule_section), week, class_start, class_end));
    }

    /**
     * 菜单栏
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定要退出吗?")
                    .create();

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //在退出之前结束dialog,再退出,否则退出会很慢
                            alertDialog.dismiss();
                            finish();

                        }
                    });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });

            alertDialog.show();

        } else if (id == R.id.menu_save) {
            if (setCourseFromView()) {
                final AlertDialog alertDialog = initAlertDialog("提示", "是否保存内容?", "取消");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveCourse();

                            }
                        });
                alertDialog.show();
            }
        }

//        boolean Return false to allow normal menu processing to  proceed, true to consume it here.
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog initAlertDialog(String title, String message, String cancleBtnText) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancleBtnText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        return alertDialog;
    }

    /**
     * 初始化节数选择对话框的星期，开始节数，节数节数列表
     */
    private void setData() {
        sWeekItems = new ArrayList<>();

        sWeekItems.add("周一");
        sWeekItems.add("周二");
        sWeekItems.add("周三");
        sWeekItems.add("周四");
        sWeekItems.add("周五");
        sWeekItems.add("周六");
        sWeekItems.add("周日");

        sStartItems = new ArrayList<>();
        sEndItems = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("到");
        for (int i = 1; i <= 12; i++) {
            stringBuilder.append(i);
            sStartItems.add(String.valueOf(i));
            sEndItems.add(stringBuilder.toString());
            stringBuilder.delete(1, stringBuilder.length());

        }
    }

    /**
     * 初始化选择对话框
     */
    private void initOptionsPicker() {

        int options1 = mDayOfWeek - 1;
        int options2 = mClassStart - 1;
        int options3 = mClassEnd - 1;

        final String str = "%s %d-%d节";
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String str = sWeekItems.get(options1) + " " + (options2 + 1) + "-" + (options3 + 1) + "节";
                mClassNumTextView.setText(str);
                //保存节数信息
                mDayOfWeek = options1 + 1;
                mClassStart = options2 + 1;
                mClassEnd = options3 + 1;

            }
        })
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String str = sWeekItems.get(options1) + " " + (options2 + 1) + "-" + (options3 + 1) + "节";

                        pvOptions.setTitleText(str);
                        if (options3 < options2) {
                            pvOptions.setSelectOptions(options1, options2, options2 + 1);
                        }
                    }
                })
                .build();
        if (pvOptions != null) {
            pvOptions.setNPicker(sWeekItems, sStartItems, sEndItems);
            pvOptions.setSelectOptions(options1, options2, options3);
            pvOptions.setTitleText("选择上课节数");
            pvOptions.show();
        }

    }
}
