package com.example.NCEPU.Student.TimeTable.ui.config;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.NCEPU.R;
import com.example.NCEPU.Student.TimeTable.util.CalendarReminderUtils;
import com.example.NCEPU.Student.TimeTable.util.Config;
import com.example.NCEPU.Student.TimeTable.util.FileUtils;
import com.example.NCEPU.Student.TimeTable.util.Utils;

import java.io.File;

public class ConfigActivity extends AppCompatActivity{

    private TextView mAlphaTextView;
    private CardView mCardView;
    private ImageView mBgImageView;

    private static final int REQUEST_CODE_SYSTEM_PIC = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 2;// 动态申请存储权限标识
    private static final int REQUEST_CODE_PHOTO_CUT = 3;
    public static final String EXTRA_UPDATE_BG = "update_bg";
    public static final String BG_NAME = "bg.jpg";

    public static String sPath;

    private float mAlpha = Config.getCardViewAlpha();
    private int mBgId = Config.getBgId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        SeekBar seekBar = findViewById(R.id.alpha_seekBar);

        mAlphaTextView = findViewById(R.id.tv_alpha);
        mCardView = findViewById(R.id.cv_config_alpha);

        mBgImageView = findViewById(R.id.iv_bg_config);


        initGridView();


        setCardViewAlpha();

        sPath = getExternalFilesDir(null).getAbsolutePath() + File.separator + "pictures";
        initActionBar();

        int value = (int) (Config.getCardViewAlpha() * 100);
        String s = value + "%";
        mAlphaTextView.setText(s);

        seekBar.setProgress(value - 10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value = i + 10;
                String str = value + "%";
                mAlphaTextView.setText(str);
                mAlpha = value / 100.0f;
                mCardView.setAlpha(mAlpha);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        Button cleanBtn=findViewById(R.id.btn_delete_calendar_event);
//        cleanBtn.setOnClickListener(v -> CalendarReminderUtils.deleteCalendarEvent(ConfigActivity.this
//                ,CalendarReminderUtils.DESCRIPTION));
//        cleanBtn.setOnClickListener(view -> {
//            CalendarReminderUtils.deleteCalendarEvent(ConfigActivity.this
//                    ,CalendarReminderUtils.DESCRIPTION);
//        });
        Utils.setBackGround(this,mBgImageView);
    }
    private void initGridView() {
        GridView gridView=findViewById(R.id.gv_bg_select);
        final BgBtnAdapter bgBtnAdapter=new BgBtnAdapter(this);
        bgBtnAdapter.bgIdList.add(R.drawable.camera_logo);
        //bgBtnAdapter.bgIdList.add(R.drawable.bg_x);
//        bgBtnAdapter.bgIdList.add(R.drawable.bg_gradient);
        bgBtnAdapter.bgIdList.add(R.drawable.btn_bg_2);
//        bgBtnAdapter.bgIdList.add(R.drawable.btn_bg_3);
//        bgBtnAdapter.bgIdList.add(R.drawable.btn_bg_4);

        final int[] bgId=new int[]{
                R.color.background_color_white,
                R.drawable.btn_bg_2,
                R.drawable.btn_bg_4,
                R.drawable.bg_3,
                R.drawable.bg_4
        };
        gridView.setAdapter(bgBtnAdapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            if(i==0) {
                userSelectBg();
            }
            else {
                showBgConfirmDialog(bgId[i]);
            }
//                showBgConfirmDialog(bgId[i]);
        });
    }

    /**
     * 打开系统相册选取图片
     */
    private void userSelectBg() {
        //申请权限
        requestStoragePermission();
        //打开图库选取图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SYSTEM_PIC);//打开系统相册
    }

    /**
     * 显示背景确认对话框
     * @param id
     */
    private void showBgConfirmDialog(final int id) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否将其设为背景图片")
                .create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mBgId != id) {
                    showUserSelectBg(id);
                }

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置CardView 透明度
     */
    private void setCardViewAlpha() {
        float alpha = Config.getCardViewAlpha();
        mCardView.setAlpha(alpha);
    }

    /**
     * 菜单栏
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isConfigChange()) {
                final AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否保存设置?").create();
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveConfig();
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            } else {
                finish();
            }

        } else if (id == R.id.menu_apply) {
            if (isConfigChange()) {
                saveConfig();
                Toast.makeText(this, "应用成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "应用成功", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, "设置未发生改变", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @return 设置是否改变
     */
    private boolean isConfigChange() {
        return (mBgId != Config.getBgId() || mAlpha != Config.getCardViewAlpha());
    }

    /**
     * 保存设置
     */
    private void saveConfig() {
        Config.setCardViewAlpha(mAlpha);
        Config.setBgId(mBgId);
        Config.saveSharedPreferences(this);

        setUpdateResult();
    }

    /**
     * 通知MainActivity更新背景图片
     */
    private void setUpdateResult()
    {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATE_BG, true);
        setResult(RESULT_OK, intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SYSTEM_PIC) {
                if (data != null) {
                    Uri imgUri = data.getData();
                    String path = FileUtils.getPath(this, imgUri);
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                    BitmapFactory.decodeFile(path,options);
                    int height = options.outHeight;
                    int width = options.outWidth;

                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    float ratio=(float) dm.heightPixels/dm.widthPixels;
                    if(height>dm.heightPixels||width>dm.widthPixels) {
                        startPhotoCrop(imgUri,dm.heightPixels,dm.widthPixels);
                    }
                    else if ((float) height / width == ratio) {//如果图片比例与屏幕比例相同，直接复制图片
                        FileUtils.fileCopy(path, sPath + File.separator + BG_NAME);
                        showUserSelectBg(0);
                    } else {
                        startPhotoCrop(imgUri, height,Math.round(height/ratio));
                    }

                }
            } else if (requestCode == REQUEST_CODE_PHOTO_CUT) {
                //预览图片改变效果,设置不会保存到本地
                showUserSelectBg(0);
            }
        }
    }
    public void showUserSelectBg(int id)
    {
        mBgId = id;//当为0时,读取自定义背景
        Utils.refreshBg(this,id);
        Utils.setBackGround(this,mBgImageView, mBgId);
        if(id==0)
            setUpdateResult();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意，执行相应操作
                Log.e("TAG", "用户已经同意了存储权限");
            } else {
                Toast.makeText(this, "需要访问本地图片才能完成背景图片的设置", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestStoragePermission() {

        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG", "开始" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            // 拥有权限，可以执行涉及到存储权限的操作
            Log.e("TAG", "你已经授权了该组权限");
        } else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "向用户申请该组权限");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
        }

    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.menu_config);
    }

    /**
     * 以屏幕分辨率比例裁剪图片
     *
     * @param uri 图片uri
     * @param height 图片高度,用于设置裁剪后的高度
     */
    public void startPhotoCrop(Uri uri, int height,int width) {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        // crop为true是设置在开启的intent中设置显示的view可以剪裁

        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例

        intent.putExtra("aspectX", 9);

        intent.putExtra("aspectY", 16);


        // outputX,outputY 是剪裁图片的宽高

        intent.putExtra("outputX", width);

        intent.putExtra("outputY", height);

        //设置了true的话直接返回bitmap，可能会很占内存

        intent.putExtra("return-data", false);

        //设置输出的格式

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        File file = new File(sPath, BG_NAME);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs())
                return;
        }
        //Log.d("uri",Uri.fromFile(file).getPath());

        //设置输出的地址

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        //不启用人脸识别

        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, REQUEST_CODE_PHOTO_CUT);
        

    }
}
