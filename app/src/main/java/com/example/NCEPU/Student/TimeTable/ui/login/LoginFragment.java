package com.example.NCEPU.Student.TimeTable.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.NCEPU.R;
import com.example.NCEPU.Student.TimeTable.bean.Course;
import com.example.NCEPU.Student.TimeTable.colleges.base.College;
import com.example.NCEPU.Student.TimeTable.colleges.base.CollegeFactory;
import com.example.NCEPU.Student.TimeTable.TimeTableActivity;
import com.example.NCEPU.Student.TimeTable.util.Config;
import com.example.NCEPU.Student.TimeTable.util.HttpUtils;
import com.example.NCEPU.Student.TimeTable.util.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private College college;

    private EditText mAccountEt;
    private EditText mPwEt;
    private EditText mRandomCodeEt;
    private ImageView mRandomCodeIv;
    private Button mLoginBtn;
    private ProgressBar mProgressBar;

    private Handler mHandler = new Handler();

    private boolean judgeFlag = true;//判断网络是否可用的循环退出标志，方便结束线程

    public static final String EXTRA_UPDATE_TIMETABLE = "update_timetable";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PWD = "pwd";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        college = CollegeFactory.createCollege(Config.getCollegeName());

        OkHttpUtils.setFollowRedirects(college.getFollowRedirects());
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login,menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRandomCodeImg();
        judgeConnected();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (college.isLogin()) {
                    final String[] strings = college.getTermOptions();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showSelectDialog(strings);
                        }
                    });
                }

            }
        }).start();
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
                                        getActivity(),
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

    /**
     * 初始化
     */
    private void init(View view) {
        TextView collegeName = view.findViewById(R.id.tv_college_name);
        collegeName.setText(college.getCollegeName());
        mRandomCodeIv = view.findViewById(R.id.iv_random_code);
        mLoginBtn = view.findViewById(R.id.btn_login);
        mProgressBar = view.findViewById(R.id.loading);
        mAccountEt = view.findViewById(R.id.et_account);
        mPwEt = view.findViewById(R.id.et_password);

        mRandomCodeEt = view.findViewById(R.id.et_random_code);
        mRandomCodeEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(college.getRandomCodeMaxLength()),});
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInput();
                String account = mAccountEt.getText().toString();
                String pw = mPwEt.getText().toString();
                String randomCode = mRandomCodeEt.getText().toString();

                if (pw.isEmpty() || account.isEmpty() || randomCode.isEmpty()) {
                    Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    setLoading(true);
                    login(account, pw, randomCode);
                }

            }
        });

        mRandomCodeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRandomCodeImg();
            }
        });
        readAccountFromLocal();
    }

    private void saveAccountToLocal(String account, String pwd) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCOUNT, account);
        editor.putString(KEY_PWD, pwd);
        editor.apply();
    }

    private void readAccountFromLocal() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("account", Context.MODE_PRIVATE);
        mAccountEt.setText(sharedPreferences.getString(KEY_ACCOUNT, ""));
        mPwEt.setText(sharedPreferences.getString(KEY_PWD, ""));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Objects.requireNonNull(getActivity(), "login activity can't be null").finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通知主界面更新
     */
    private void setUpdateResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATE_TIMETABLE, true);
        Objects.requireNonNull(getActivity()).setResult(Activity.RESULT_OK, intent);
    }

    /**
     * 显示学期选择对话框
     *
     * @param termOptions 学期选项
     */
    private void showSelectDialog(String[] termOptions) {
        if (termOptions == null || termOptions.length == 0) {
            Toast.makeText(getActivity(), "无法获取学期选项", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<String> items = new ArrayList<>();
        for (String s : termOptions) {
            if (!s.isEmpty())
                items.add(s);
        }
        OptionsPickerView mOptionsPv = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setLoading(true);
                getCourses(items.get(options1));
            }
        }).build();

        mOptionsPv.setTitleText("选择学期");

        mOptionsPv.setNPicker(items, null, null);
        mOptionsPv.setSelectOptions(0);
        mOptionsPv.show();

    }

    /**
     * 获取课程表中的课程
     *
     * @param term
     */
    private void getCourses(final String term) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Course> list = college.getCourses(term);
                final boolean success = list != null;
                if (success&&list.size()>0) {
                    Collections.sort(list);//按星期和上课时间排序
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        if (success) {
                            if(list.size()==0){
                                Toast.makeText(getActivity(), "该学期没有课程", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), "导入成功", Toast.LENGTH_SHORT).show();
                            }
                            TimeTableActivity.sCourseList = list;
                            setUpdateResult();
                            judgeFlag = false;
                            Activity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        }else {
                            Toast.makeText(getActivity(), "导入失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();


    }

    /**
     * 设置是否进入加载状态
     *
     * @param b
     */
    private void setLoading(boolean b) {

        mLoginBtn.setEnabled(!b);
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

    }

    /**
     * 隐藏键盘
     */
    private void hideInput() {
        Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View v = activity.getWindow().peekDecorView();
            if (null != v && imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }

    }
    /**
     * 登录
     *
     * @param account
     * @param pw
     * @param randomCode String 验证码
     */
    private void login(final String account, final String pw, final String randomCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isLogin = college.login(account, pw, randomCode);
                final String[] termOptions = college.getTermOptions();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        if (isLogin) {
                            saveAccountToLocal(mAccountEt.getText().toString(), mPwEt.getText().toString());
                            showSelectDialog(termOptions);
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    "账户或密码或验证码错误，登陆失败。",
                                    Toast.LENGTH_SHORT).show();
                            setRandomCodeImg();
                        }

                    }
                });
            }
        }).start();

    }

    /**
     * 从登录页面下载并加载验证码
     */
    private void setRandomCodeImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = college.getRandomCodeImg(getContext().getExternalCacheDir().getAbsolutePath());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap == null) {
                            mRandomCodeIv.setImageDrawable(getContext().getDrawable(R.drawable.ic_error_red_24dp));
                        } else {
                            mRandomCodeIv.setImageBitmap(bitmap);
                        }

                    }
                });
            }
        }).start();

    }
}
