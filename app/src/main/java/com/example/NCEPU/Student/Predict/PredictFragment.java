package com.example.NCEPU.Student.Predict;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.NCEPU.R;
import com.example.NCEPU.StudentMainActivity;
import com.example.NCEPU.Utils.GlideImageLoader;
import com.example.NCEPU.Utils.MyGridView;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.adapter.GridViewAdapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class PredictFragment extends Fragment {

    private Banner banner;
    private MyGridView aprGridView;
    private MyGridView preGridView;
    private String dept = "";
    private String gradeYear = "";
    @Nullable
    @Override

    //类似于Activity里面的setContentView();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_predict, container, false);
        initViews(view);
        initBanner(view);
        setHeight();
        onClick();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View v) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        dept = sharedPreferences.getString("dept", "");
        gradeYear  =sharedPreferences.getString("year", "");
        aprGridView = v.findViewById(R.id.gv_relate);
        String []namesApr = {"数理课", "政治课", "专业课"};
        int []imagesApr = {R.drawable.shuli, R.drawable.politics, R.drawable.major_course};
        GridViewAdapter gridViewAdapter1 = new GridViewAdapter(getActivity(), namesApr, imagesApr);
        aprGridView.setAdapter(gridViewAdapter1);

        preGridView = v.findViewById(R.id.gv_predict);
        String []namesPre = {"AdaBoost", "DecisionTree", "NaiveBayes", "RandomForest", "SVM", "KNN"};
        int []imagesPre = {R.drawable.adaboost, R.drawable.decision, R.drawable.bayes, R.drawable.random_forest,
                R.drawable.svm, R.drawable.knn};
        GridViewAdapter gridViewAdapter2 = new GridViewAdapter(getActivity(), namesPre, imagesPre);
        preGridView.setAdapter(gridViewAdapter2);
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 645);
//        ToastUtil.showMessage(getContext(), pagerHeight + "");
        pagerHeight -= 60;
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) banner.getLayoutParams();
        linearParams.height = dip2px(getContext(), pagerHeight / 20 * 6);
        banner.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams) aprGridView.getLayoutParams();
        linearParams.height = dip2px(getContext(), pagerHeight / 20 * 7);
        aprGridView.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams) preGridView.getLayoutParams();
        linearParams.height = dip2px(getContext(), pagerHeight / 20 * 7);
        preGridView.setLayoutParams(linearParams);
    }

    private void initBanner(View v) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.predict_dialog_4);
        banner = v.findViewById(R.id.banner_pre);
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

    private void onClick() {
        aprGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = null;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), ShuLiActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 1:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), PoliticsActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 2:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), MajorActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;
                }
            }
        });

        preGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = null;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), AdaBoostActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 1:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), DecisionTreeActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 2:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), NaiveBayesActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 3:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), RFActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 4:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), SVMActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;

                    case 5:
//                        if(dept.equals("控制与计算机工程学院")) {
//                            intent = new Intent(getActivity(), KNNActivity.class);
//                            startActivity(intent);
//                        }else {
//                            ToastUtil.showMessage(getContext(), "本功能仅对控制与计算机工程学院同学开放!");
//                        }
                        ToastUtil.showMessage(getContext(), "暂未开放!");
                        break;
                }
            }
        });
    }
}
