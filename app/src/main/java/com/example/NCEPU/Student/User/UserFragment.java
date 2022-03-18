package com.example.NCEPU.Student.User;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.NCEPU.MainActivity;
import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;
import com.example.NCEPU.Utils.ItemView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;


public class UserFragment extends Fragment {

    private ImageView mHBack;
    private ImageView mHHead;
    private ImageView mUserLine;
    private TextView mUserName;
    private TextView mUserVal;

    private ItemView mDept;
    private ItemView mSex;
    private ItemView mSignName;
    private ItemView mVersion;
    private ItemView mLogout;
    private ItemView mUserId;
    private ItemView mMajor;
    private ItemView mYear;
    private ItemView mCSDN;
    private ItemView mGithub;
    private ItemView mWechat;

    public static String stu_name, stu_class, stu_id, stu_dept;
    public static String stu_sex, stu_major, stu_year;

    private SharedPreferences sharedPreferences;


    @Nullable
    @Override

    //类似于Activity里面的setContentView();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHBack = view.findViewById(R.id.h_back);
        mHHead =  view.findViewById(R.id.h_head);
        mUserLine =  view.findViewById(R.id.user_line);
        mUserName =  view.findViewById(R.id.user_name);
        mUserVal =  view.findViewById(R.id.user_class);
        //下面item控件
        mDept =  view.findViewById(R.id.dept);
        mUserId = view.findViewById(R.id.user_id);
        mMajor = view.findViewById(R.id.user_major);
        mYear = view.findViewById(R.id.user_year);
        mSex =  view.findViewById(R.id.sex);
//        mSignName =  view.findViewById(R.id.signName);
        mCSDN = view.findViewById(R.id.csdn);
        mLogout =  view.findViewById(R.id.logout);
//        mVersion = view.findViewById(R.id.version);
        mGithub = view.findViewById(R.id.github);
        mWechat = view.findViewById(R.id.wechat);

        // 初始化
        sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        stu_name = sharedPreferences.getString("name", "");
        stu_class = sharedPreferences.getString("class", "");
        stu_id = sharedPreferences.getString("stu_id", "");
        stu_dept = sharedPreferences.getString("dept", "");
        stu_sex = sharedPreferences.getString("sex", "");
        stu_major = sharedPreferences.getString("major", "");
        stu_year = sharedPreferences.getString("year", "");

        mUserName.setText(stu_name);
        mUserVal.setText(stu_class);
        setData();
        setHeight();

//        mUserVal.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
////                ToastUtil.showMessage(getContext(), "getMeasuredHeight="+px2dip(getActivity(), mUserVal.getMeasuredWidth()));
//        });
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 645);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)mHBack.getLayoutParams();
        relativeParams.height = dip2px(getContext(), (float) (pagerHeight * 0.30387));
        mHBack.setLayoutParams(relativeParams);
        relativeParams = (RelativeLayout.LayoutParams)mHHead.getLayoutParams();
        relativeParams.height = dip2px(getContext(), (float) (pagerHeight * 0.15));
        relativeParams.width = dip2px(getContext(), (float) (pagerHeight * 0.15));
        mHHead.setLayoutParams(relativeParams);
        relativeParams = (RelativeLayout.LayoutParams)mUserName.getLayoutParams();
        relativeParams.height = dip2px(getContext(), (float) (pagerHeight * 0.0356));
        mUserName.setLayoutParams(relativeParams);
        relativeParams = (RelativeLayout.LayoutParams)mUserVal.getLayoutParams();
        relativeParams.height = dip2px(getContext(), (float) (pagerHeight * 0.0356));
        mUserVal.setLayoutParams(relativeParams);
        relativeParams = (RelativeLayout.LayoutParams)mUserLine.getLayoutParams();
        relativeParams.height = dip2px(getContext(), (float) (pagerHeight * 0.0356 + 2));
        mUserLine.setLayoutParams(relativeParams);

        //下方item
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)mDept.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mDept.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mUserId.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mUserId.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mMajor.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mMajor.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mYear.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mYear.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mSex.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mSex.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mGithub.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mGithub.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mCSDN.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mCSDN.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mLogout.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mLogout.setLayoutParams(linearParams);
        linearParams = (LinearLayout.LayoutParams)mWechat.getLayoutParams();
        linearParams.height = dip2px(getContext(), (float) (pagerHeight * 0.07777));
        mWechat.setLayoutParams(linearParams);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            return;
        }
        if(110 == requestCode) {
            String mod_sign = data.getExtras().getString("mod_sign");
            mSignName.setRightDesc(mod_sign);
        }
        if(1 == requestCode) {
            Uri uri = data.getData();
            ContentResolver cr = getActivity().getContentResolver();
            //获取图片
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,bos);
                String Base64 = android.util.Base64.encodeToString(bos.toByteArray(), android.util.Base64.DEFAULT);
                editor.putString("user_icon",Base64);
                editor.commit();
                //设置背景磨砂效果
                /*Glide.with(this).load(bitmap)
                        .transform(new CircleCrop())
                        .transform(new BlurTransformation(25,2))
                        .into(mHBack);*/
                //设置圆形图像
                Glide.with(this).load(bitmap)
                        .transform(new CircleCrop())
                        //.apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(mHHead);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            ToastUtil.showMessage(getContext(), "检查到您手机没有安装微信，请安装后使用该功能");
        }
    }

    private void setData() {
        //数据初始化
        //学院
        mDept.setShowBottomLine(true);
        mDept.setShowRightArrow(false);
        mDept.setRightDesc(stu_dept);

        //专业
        mMajor.setShowBottomLine(true);
        mMajor.setShowRightArrow(false);
        mMajor.setRightDesc(stu_major);

        //入学年份
        mYear.setShowBottomLine(true);
        mYear.setShowRightArrow(false);
        mYear.setRightDesc(stu_year);

        //学号
        mUserId.setShowBottomLine(true);
        mUserId.setShowRightArrow(false);
        mUserId.setRightDesc(stu_id);

        //性别
        mSex.setShowBottomLine(true);
        mSex.setShowRightArrow(false);
        if(stu_sex.equals("男")) {
            mSex.setLeftIcon(R.drawable.ic_sex_man);
        }
        mSex.setRightDesc(stu_sex);

        //CSDN
        mCSDN.setShowBottomLine(true);
        mCSDN.setShowRightArrow(true);

        //个性签名
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        String sign = sharedPreferences.getString("sign", "");
//        mSignName.setRightDesc(sign);

        //更新
//        mVersion.setRightDesc("V1.0");

        mHHead.setOnClickListener(v -> {
            Intent intent=new Intent();
            intent.setType("image/*");
            //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
            //类型的内容给你选择
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        });

        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("user_info",Context.MODE_PRIVATE);
        String bt = sharedPreferences1.getString("user_icon", "");
        if(!bt.equals("")) {
            ByteArrayInputStream bis = new ByteArrayInputStream(android.util.Base64.decode(bt.getBytes(), android.util.Base64.DEFAULT));
            Drawable drawable = Drawable.createFromStream(bis,"");
            /*Glide.with(this).load(drawable)
                    //.transform(new CircleCrop())
                    //.transform(new BlurTransformation(25,2))
                    .into(mHBack);*/
            //设置圆形图像
            Glide.with(this).load(drawable)
                    .transform(new CircleCrop())
                    //.apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(mHHead);
        }else {
            /*Glide.with(this).load(R.drawable.head_2)
                    //.transform(new CircleCrop())
                    //.transform(new BlurTransformation(25,2))
                    .into(mHBack);*/
            //设置圆形图像
            Glide.with(this).load(R.drawable.head_3)
                    .transform(new CircleCrop())
                    //.apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(mHHead);
        }
        //设置背景磨砂效果


        //设置用户名整个item的点击事件
        mDept.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        });


        //设置用户名整个item的点击事件

        //学院
        mDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), mDept.getRightDesc(), Toast.LENGTH_SHORT).show();
            }
        });

        mCSDN.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Intent intent = new Intent(getActivity(), CSDNActivity.class);
                startActivity(intent);
            }
        });

        mGithub.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Intent intent = new Intent(getActivity(), GitHubActivity.class);
                startActivity(intent);
            }
        });

        mWechat.setItemClickListener(new ItemView.itemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void itemClick(String text) {
                Context context = getContext();
                ClipboardManager manager =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("message","KI的算法杂记");
                manager.setPrimaryClip(clipData);
//                ToastUtil.showMessage(getContext(), "公众号名称复制成功!");
                //跳转至公众号
                try {
                    AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                    builder1.setTitle("提示").setMessage("公众号名称复制成功，需要为您跳转至微信吗？")
                            .setIcon(R.drawable.icon_exit)
                            .setPositiveButton("需要", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getWechatApi();
                                }
                            }).setNeutralButton("不需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //性别
        mSex.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                ToastUtil.showMessage(getContext(), mSex.getRightDesc());
            }
        });

        //专业
        mMajor.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                ToastUtil.showMessage(getActivity(), mMajor.getRightDesc());
            }
        });

        //学号
        mUserId.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                ToastUtil.showMessage(getActivity(), mUserId.getRightDesc());
            }
        });

        //入学年份
        mYear.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                ToastUtil.showMessage(getActivity(), mYear.getRightDesc());
            }
        });


        //退出登录
        mLogout.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                try {
                    AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                    builder1.setTitle("提示").setMessage("退出登录后会清除所有信息，下次进入时需要重新登录，确认退出吗？")
                            .setIcon(R.drawable.icon_exit)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //修改个性签名
//        mSignName.setItemClickListener(new ItemView.itemClickListener() {
//            @Override
//            public void itemClick(String text) {
//                Intent intent = new Intent(getActivity(), SignatureActivity.class);
//                Bundle bundle = new Bundle();
//                String signature = mSignName.getRightDesc();
//                bundle.putString("signature",signature);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, 110);
//            }
//        });
//
//        mVersion.setItemClickListener(text -> ToastUtil.showMessage(getContext(), "暂无新版本发布！"));

    }
}
