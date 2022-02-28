package com.example.NCEPU.Student.Predict;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.NCEPU.MainActivity;
import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Grade;
import com.example.NCEPU.Utils.ListInfoUtil;
import com.example.NCEPU.Utils.MyListView;
import com.example.NCEPU.Utils.PredictUtil;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.adapter.PredictAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.NCEPU.MainActivity.connectJWGL;
import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class DecisionTreeActivity extends AppCompatActivity {

    private ImageButton back;
    private LinearLayout ly_back;
    private ListView listView;
    private Button query;
    private PopupWindow popupWindow;
    public ProgressDialog progressDialog;
    public boolean flag = false;
    public Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_tree);
        initViews();
        setHeight();
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        back.setLayoutParams(linearParams);

        //查询按钮=48dp=0.0744
        linearParams = (LinearLayout.LayoutParams)query.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0744));
        query.setLayoutParams(linearParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    void initPython(){
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    ArrayList<Integer> callPythonCode(String file, String model, int []lis, int row, int col) {
        //要返回四个整数值
        Python py = Python.getInstance();
        ArrayList<Integer> res = new ArrayList<>();
        for(int i = 1; i <= 4; i++) {
            PyObject pyObject = py.getModule(file + "/" + file).callAttr("test", model + "_" + i + ".pkl", lis, row, col);
            List<PyObject> list = pyObject.asList();
            res.add(list.get(0).toInt());
        }
        return res;
    }

    private void initViews() {
        initPython();
        ly_back = findViewById(R.id.ly_back_decision);
        back = findViewById(R.id.ib_back_decision);
        back.setOnClickListener(v -> onBackPressed());
        listView = findViewById(R.id.list_decision);
        query = findViewById(R.id.btn_query_decision);
        query.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(DecisionTreeActivity.this);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在加载...");
            progressDialog.setIcon(R.drawable.running);
            new Thread(){
                public void run(){
                    try{
                        runOnUiThread(() -> createList());
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
                    System.out.println("哈哈哈哈哈哈");
                    progressDialog.dismiss();
                    ToastUtil.showMessage(DecisionTreeActivity.this, "预测完成!");
                }
            };
            progressDialog.show();
        });

    }

    private String contains(ArrayList<String> courses, String course) {
        for(String cour : courses) {
            if(cour.contains(course) && !cour.contains("实验")) {
                return cour;
            }
        }
        return "";
    }

    private int convert(String x) {
        float score = Float.parseFloat(x.trim());
        if(score >= 4.0) {
            return 1;
        }else if(score >= 3.0) {
            return 2;
        }else if(score >= 2.0) {
            return 3;
        }else if(score >= 1.0) {
            return 4;
        }else {
            return 5;
        }
    }

    private String getChar(int level) {
        if(level == 1) {
            return "A";
        }else if(level == 2) {
            return "B";
        }else if(level == 3) {
            return "C";
        }else if(level == 4) {
            return "D";
        }else {
            return "E";
        }
    }

    private String getFinalRes(ArrayList<Integer> res) {
        if(res.get(0) == 1) {
            return "A";
        }else if(res.get(1) == 1) {
            return "B";
        }else if(res.get(2) == 1) {
            return "C";
        }else if(res.get(3) == 1) {
            return "D";
        }else {
            return "E";
        }
    }

    private ArrayList<String> getListInfo(ArrayList<Integer> pre) {
        ArrayList<String> res = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            if(pre.get(i) == 1) {
                res.add("是");
            }else {
                res.add("否");
            }
        }
        return res;
    }

    private void createList() {
        ArrayList<ListInfoUtil> listInfoUtils = new ArrayList<>();
        //先获取所有成绩信息
        ArrayList<Grade> grades_list = null;
        ArrayList<String> courses = new ArrayList<>();
        try {
            grades_list = connectJWGL.getStudentGrade("", "", "全部");
            for(Grade grade : grades_list) {
                courses.add(grade.getCourse_name());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PredictUtil> list = new ArrayList<>();
        //1.高数1->高数2
        ListInfoUtil listInfoUtil = new ListInfoUtil();
        PredictUtil predictUtil = new PredictUtil();
        predictUtil.setId("00");
        //既有有高数1的成绩又有高数2成绩
        if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1)));
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("高等数学B(2)-" + charLevel2)));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_to_gao2", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:97.06%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:87.25%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:81.37%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:77.45%");
            listInfoUtil.setFinalRes("高等数学B(2)-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("高等数学B(2)-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1)));
            //高数2
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("暂无数据")));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_to_gao2", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:97.06%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:87.25%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:81.37%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:77.45%");
            listInfoUtil.setFinalRes("高等数学B(2)-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("高等数学B(2)-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") == "" && contains(courses, "高等数学B(2)") == "") {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高数1无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("高数2无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //2.高数1, 高数2->线性代数
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("01");
        //三门成绩都有
        if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "线性代数") != "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2)));
            //线代
            int index3 = courses.indexOf(contains(courses, "线性代数"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("线性代数-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_to_linear", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:91.18%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:84.31%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:75.50%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:85.30%");
            listInfoUtil.setFinalRes("线性代数-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("线性代数-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "线性代数") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("待验证")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_to_linear", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:91.18%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:84.31%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:75.50%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:85.30%");
            listInfoUtil.setFinalRes("线性代数-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("线性代数-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") == "" && contains(courses, "线性代数") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高数2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("线代无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高数1, 2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("线代无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //3.高数1, 高数2->概率论
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("02");
        //三门成绩都有
        if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "概率论") != "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2)));
            //概率论
            int index3 = courses.indexOf(contains(courses, "概率论"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_to_pro", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:65.69%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:45.19%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:62.75%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:100.00%");
            listInfoUtil.setFinalRes("概率论-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("概率论-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "概率论") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("待验证")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_to_pro", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:65.69%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:45.19%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:62.75%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:100.00%");
            listInfoUtil.setFinalRes("概率论-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("概率论-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高数2暂无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高数1, 2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //4.高数1, 高数2, 线代->概率论
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("03");
        //四门成绩都有
        if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "线性代数") != "" &&contains(courses, "概率论") != "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            //线代
            int index3 = courses.indexOf(contains(courses, "线性代数"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2, "线性代数-" + charLevel3)));
            //概率论
            int index4 = courses.indexOf(contains(courses, "概率论"));
            String mark4 = grades_list.get(index4).getGpa();
            int level4 = convert(mark4);
            String charLevel4 = getChar(level4);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论-" + charLevel4)));
            //预测
            int []data = {level1, level2, level3};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_linear_to_pro", data, 1, 3);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:69.61%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:44.12%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:62.75%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:98.04%");
            listInfoUtil.setFinalRes("概率论-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("概率论-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "线性代数") != "" &&contains(courses, "概率论") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            //线代
            int index3 = courses.indexOf(contains(courses, "线性代数"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2, "线性代数-" + charLevel3)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("待验证")));
            //预测
            int []data = {level1, level2, level3};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_linear_to_pro", data, 1, 3);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:69.61%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:44.12%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:62.75%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:98.04%");
            listInfoUtil.setFinalRes("概率论-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("概率论-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "线性代数") == "" &&contains(courses, "概率论") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2, "线性代数暂无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论无数据")));
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高数2无数据", "线性代数暂无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论无数据")));
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高数1, 2, 线性代数无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("概率论无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //5.C语言->Java
        listInfoUtil = new ListInfoUtil();
        predictUtil = new PredictUtil();
        predictUtil.setId("04");
        //既有有高数1的成绩又有高数2成绩
        if(contains(courses, "高级语言程序") != "" && contains(courses, "面向对象") != "") {
            //C语言
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计(C)-" + charLevel1)));
            //Java
            int index2 = courses.indexOf(contains(courses, "面向对象"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("面向对象的程序设计-" + charLevel2)));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "c_to_java", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:67.89%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:71.53%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:74.45%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:93.43%");
            listInfoUtil.setFinalRes("面向对象的程序设计-" + predictRes);
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("面向对象的程序设计-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高级语言程序") != "" && contains(courses, "面向对象") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计(C)-" + charLevel1)));
            //高数2
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("暂无数据")));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "c_to_java", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:67.89%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:71.53%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:74.45%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:93.43%");
            listInfoUtil.setFinalRes("面向对象的程序设计-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("面向对象的程序设计-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高级语言程序") == "" && contains(courses, "面向对象") == "") {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("C语言无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("面向对象无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);


        //6.C语言->数据结构
        listInfoUtil = new ListInfoUtil();
        predictUtil = new PredictUtil();
        predictUtil.setId("05");
        //既有有高数1的成绩又有高数2成绩
        if(contains(courses, "高级语言程序") != "" && contains(courses, "数据结构") != "") {
            //C语言
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计(C)-" + charLevel1)));
            //Java
            int index2 = courses.indexOf(contains(courses, "数据结构"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("数据结构与算法-" + charLevel2)));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "c_to_ds", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:77.68%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:74.68%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:73.40%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:81.12%");
            listInfoUtil.setFinalRes("数据结构与算法-" + predictRes);
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("数据结构与算法-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高级语言程序") != "" && contains(courses, "数据结构") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计(C)-" + charLevel1)));
            //高数2
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("暂无数据")));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "c_to_ds", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:77.68%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:74.68%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:73.40%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:81.12%");
            listInfoUtil.setFinalRes("数据结构与算法-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("数据结构与算法-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高级语言程序") == "" && contains(courses, "数据结构") == "") {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("C语言无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("数据结构无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //7.C语言, Java->汇编
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("06");
        //三门成绩都有
        if(contains(courses, "高级语言程序") != "" && contains(courses, "面向对象") != "" && contains(courses, "汇编") != "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "面向对象"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计(C)-" + charLevel1, "面向对象的程序设计-" + charLevel2)));
            //概率论
            int index3 = courses.indexOf(contains(courses, "汇编"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("汇编语言-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "c_java_to_assembly", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:76.47%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:76.47%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:77.31%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:100.00%");
            listInfoUtil.setFinalRes("汇编语言-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("汇编语言-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高级语言程序") != "" && contains(courses, "面向对象") != "" && contains(courses, "汇编") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "面向对象"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计(C)-" + charLevel1, "面向对象的程序设计-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("暂无数据")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "c_java_to_assembly", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:76.47%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:76.47%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:77.31%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:100.00%");
            listInfoUtil.setFinalRes("汇编语言-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("汇编语言-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高级语言程序") != "" && contains(courses, "面向对象") == "" && contains(courses, "汇编") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高级语言程序"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高级语言程序设计-" + charLevel1, "java无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("Java无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("C语言, java无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("汇编无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //8.电路1->电路2
        listInfoUtil = new ListInfoUtil();
        predictUtil = new PredictUtil();
        predictUtil.setId("07");
        //既有有高数1的成绩又有高数2成绩
        if(contains(courses, "电路理论B(1)") != "" && contains(courses, "电路理论B(2)") != "") {
            //电路1
            int index1 = courses.indexOf(contains(courses, "电路理论B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路理论B(1)-" + charLevel1)));
            //电路2
            int index2 = courses.indexOf(contains(courses, "电路理论B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("电路理论B(2)-" + charLevel2)));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "dian1_to_dian2", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:75.88%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:67.98%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:77.19%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:88.60%");
            listInfoUtil.setFinalRes("电路理论B(2)-" + predictRes);
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("电路理论B(2)-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "电路理论B(1)") != "" && contains(courses, "电路理论B(2)") == "") {
            //电路1
            int index1 = courses.indexOf(contains(courses, "电路理论B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路理论B(1)-" + charLevel1)));
            //电路2
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("暂无数据")));
            //预测
            int []data = {level1};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "dian1_to_dian2", data, 1, 1);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:75.88%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:67.98%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:77.19%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:88.60%");
            listInfoUtil.setFinalRes("电路理论B(2)-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("电路理论B(2)-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "电路理论B(1)") == "" && contains(courses, "电路理论B(2)") == "") {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路1无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("电路2无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路1无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("电路2无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //9.电路1, 电路2->模电
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("08");
        //三门成绩都有
        if(contains(courses, "电路理论B(1)") != "" && contains(courses, "电路理论B(2)") != "" && contains(courses, "模拟电子技术基础") != "") {
            //电路1
            int index1 = courses.indexOf(contains(courses, "电路理论B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //电路2
            int index2 = courses.indexOf(contains(courses, "电路理论B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路理论B(1)-" + charLevel1, "电路理论B(2)-" + charLevel2)));
            //模电
            int index3 = courses.indexOf(contains(courses, "模拟电子"));
            String mark3 = grades_list.get(index3).getGpa();
            System.out.println("模电成绩为:" + mark3);
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("模拟电子技术-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "dian1_dian2_to_mo", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:74.63%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:70.65%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:81.59%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:92.04%");
            listInfoUtil.setFinalRes("模拟电子技术-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("模拟电子技术-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "电路理论B(1)") != "" && contains(courses, "电路理论B(2)") != "" && contains(courses, "模拟电子") == "") {
            //电路1
            int index1 = courses.indexOf(contains(courses, "电路理论B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //电路2
            int index2 = courses.indexOf(contains(courses, "电路理论B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路理论B(1)-" + charLevel1, "电路理论B(2)-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("暂无数据")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "dian1_dian2_to_mo", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:76.12%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:66.17%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:80.60%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:92.04%");
            listInfoUtil.setFinalRes("模拟电子技术-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("模拟电子技术-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "电路理论B(1)") != "" && contains(courses, "电路理论B(2)") == "" && contains(courses, "模拟电子") == "") {
            //电路1
            int index1 = courses.indexOf(contains(courses, "电路理论B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路理论B(1)-" + charLevel1, "电路2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("模电无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "电路理论B(1)") != "" && contains(courses, "电路理论B(2)") == "" && contains(courses, "模拟电子") != "") {
            //电路1
            int index1 = courses.indexOf(contains(courses, "电路理论B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路理论B(1)-" + charLevel1, "电路理论B(2)无数据")));
            //模电
            int index3 = courses.indexOf(contains(courses, "模拟电子"));
            String mark3 = grades_list.get(index3).getGpa();
            System.out.println("模电成绩为:" + mark3);
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("模拟电子技术-" + charLevel3)));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("电路1, 2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("模电无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //10.高数1, 高数2->复变函数
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("09");
        //三门成绩都有
        if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "复变") != "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2)));
            //线代
            int index3 = courses.indexOf(contains(courses, "复变"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("复变函数-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_to_fubian", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:99.56%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:91.59%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:77.43%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:90.71%");
            listInfoUtil.setFinalRes("复变函数-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("复变函数-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") != "" && contains(courses, "复变") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "高等数学B(2)"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高等数学B(2)-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("待验证")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "gao1_gao2_to_fubian", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:99.56%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:91.59%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:77.43%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:90.71%");
            listInfoUtil.setFinalRes("复变函数-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("复变函数-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "高等数学B(1)") != "" && contains(courses, "高等数学B(2)") == "" && contains(courses, "复变") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "高等数学B(1)"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高等数学B(1)-" + charLevel1, "高数2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("复变无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("高数1, 2无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("复变无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //11.数据库, 汇编->操作系统A
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("10");
        //三门成绩都有
        if(contains(courses, "数据库原理") != "" && contains(courses, "汇编") != "" && contains(courses, "操作系统") != "") {
            //数据库
            int index1 = courses.indexOf(contains(courses, "数据库原理"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "汇编"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("数据库原理-" + charLevel1, "汇编语言-" + charLevel2)));
            //概率论
            int index3 = courses.indexOf(contains(courses, "操作系统"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("操作系统A-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "database_assembly_to_os", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:84.30%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:67.77%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:70.25%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:93.39%");
            listInfoUtil.setFinalRes("操作系统A-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("操作系统A-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "数据库原理") != "" && contains(courses, "汇编") != "" && contains(courses, "操作系统") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "数据库原理"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //高数2
            int index2 = courses.indexOf(contains(courses, "汇编"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("数据库原理-" + charLevel1, "汇编语言-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("待验证")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "database_assembly_to_os", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:84.30%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:67.77%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:70.25%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:93.39%");
            listInfoUtil.setFinalRes("操作系统A-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("操作系统A-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "数据库原理") != "" && contains(courses, "汇编") == "") {
            //高数1
            int index1 = courses.indexOf(contains(courses, "数据库原理"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("数据库原理-" + charLevel1, "汇编语言暂无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("操作系统A无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("数据库原理, 汇编语言无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("操作系统A无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);

        //12.汇编, 操作系统A->计算机网络
        predictUtil  =new PredictUtil();
        listInfoUtil = new ListInfoUtil();
        predictUtil.setId("11");
        //三门成绩都有
        if(contains(courses, "汇编") != "" && contains(courses, "操作系统A") != "" && contains(courses, "计算机网络") != "") {
            //汇编
            int index1 = courses.indexOf(contains(courses, "汇编"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //操作系统A
            int index2 = courses.indexOf(contains(courses, "操作系统A"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("汇编语言-" + charLevel1, "操作系统A-" + charLevel2)));
            //计算机网络
            int index3 = courses.indexOf(contains(courses, "计算机网络"));
            String mark3 = grades_list.get(index3).getGpa();
            int level3 = convert(mark3);
            String charLevel3 = getChar(level3);
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("计算机网络-" + charLevel3)));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "assembly_os_to_net", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:91.50%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:67.97%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:73.20%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:93.46%");
            listInfoUtil.setFinalRes("计算机网络-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("计算机网络-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "汇编") != "" && contains(courses, "操作系统A") != "" && contains(courses, "计算机网络") == "") {
            //汇编
            int index1 = courses.indexOf(contains(courses, "汇编"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            //操作系统A
            int index2 = courses.indexOf(contains(courses, "操作系统A"));
            String mark2 = grades_list.get(index2).getGpa();
            int level2 = convert(mark2);
            String charLevel2 = getChar(level2);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("汇编语言-" + charLevel1, "操作系统A-" + charLevel2)));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("待验证")));
            //预测
            int []data = {level1, level2};
            ArrayList<Integer> res = callPythonCode("DecisionTree", "assembly_os_to_net", data, 1, 2);
            System.out.println("预测结果为:" + res);
            String predictRes = getFinalRes(res);
            //显示信息
            ArrayList<String> info = getListInfo(res);
            listInfoUtil.setIs_90(info.get(0) + "\n模型准确率:91.50%");
            listInfoUtil.setIs_80(info.get(1) + "\n模型准确率:67.97%");
            listInfoUtil.setIs_70(info.get(2) + "\n模型准确率:73.20%");
            listInfoUtil.setIs_60(info.get(3) + "\n模型准确率:93.46%");
            listInfoUtil.setFinalRes("计算机网络-" + predictRes);

            predictUtil.setPredict(new ArrayList<>(Arrays.asList("计算机网络-" + predictRes)));
            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else if(contains(courses, "汇编") != "" && contains(courses, "操作系统A") == "") {
            //汇编
            int index1 = courses.indexOf(contains(courses, "汇编"));
            String mark1 = grades_list.get(index1).getGpa();
            int level1 = convert(mark1);
            String charLevel1 = getChar(level1);
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("汇编语言-" + charLevel1, "操作系统A暂无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("计算机网络无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }else {
            predictUtil.setPre_item(new ArrayList<>(Arrays.asList("汇编语言, 操作系统A无数据")));
            predictUtil.setBack_item(new ArrayList<>(Arrays.asList("计算机网络无数据")));
            predictUtil.setPredict(new ArrayList<>(Arrays.asList("暂无数据")));
            predictUtil.setState("暂无数据");
            //显示信息
            listInfoUtil.setIs_90("暂无数据");
            listInfoUtil.setIs_80("暂无数据");
            listInfoUtil.setIs_70("暂无数据");
            listInfoUtil.setIs_60("暂无数据");
            listInfoUtil.setFinalRes("暂无数据");

            SpannableStringBuilder builder = new SpannableStringBuilder("点击查看");
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            predictUtil.setState(builder.toString());
        }
        list.add(predictUtil);
        listInfoUtils.add(listInfoUtil);


        //显示
        PredictAdapter predictAdapter = new PredictAdapter(this, list);
        listView.setAdapter(predictAdapter);
        //设置点击事件
        listView.setOnItemClickListener((parent, view, position, id) -> {
            View view_pop = getLayoutInflater().inflate(R.layout.layout_pop,null);
            TextView textView_90 = view_pop.findViewById(R.id.tv_90);
            TextView textView_80 = view_pop.findViewById(R.id.tv_80);
            TextView textView_70 = view_pop.findViewById(R.id.tv_70);
            TextView textView_60 = view_pop.findViewById(R.id.tv_60);
            TextView textView_final = view_pop.findViewById(R.id.tv_final_res);
            SpannableStringBuilder builder = new SpannableStringBuilder("是否90以上:" + listInfoUtils.get(position).getIs_90());
            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_90.setText(builder);
            builder = new SpannableStringBuilder("是否80以上:" + listInfoUtils.get(position).getIs_80());
            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_80.setText(builder);
            builder = new SpannableStringBuilder("是否70以上:" + listInfoUtils.get(position).getIs_70());
            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_70.setText(builder);
            builder = new SpannableStringBuilder("是否60以上:" + listInfoUtils.get(position).getIs_60());
            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_60.setText(builder);
            builder = new SpannableStringBuilder("最终预测结果:" + listInfoUtils.get(position).getFinalRes());
            builder.setSpan(new ForegroundColorSpan(Color.RED), 7, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_final.setText(builder);
            textView_90.setOnClickListener(v -> {
                popupWindow.dismiss();
            });
            popupWindow=new PopupWindow(view_pop, view.getWidth() / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(view);
        });
    }
}