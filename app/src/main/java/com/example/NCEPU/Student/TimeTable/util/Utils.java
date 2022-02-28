package com.example.NCEPU.Student.TimeTable.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.example.NCEPU.Student.TimeTable.bean.Version;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 工具类：
 * 设置背景
 * <p>
 * 获取更新
 */
public class Utils {

    private static String PATH;
    private static final String BG_NAME = "bg.jpg";
    private static final String UPDATE_URL =
            "https://raw.githubusercontent.com/Potato-DiGua/Timetable/master/app/release/version.json";

    private static final String BASE_URL = "https://raw.githubusercontent.com/Potato-DiGua/Timetable/master/app/release/";
    private static Bitmap bgBitmap = null;

    public static final int SINGLE_DOUBLE_WEEK = 0;
    public static final int SINGLE_WEEK = 1;
    public static final int DOUBLE_WEEK = 2;
    public static final String[] WEEK_OPTIONS = new String[]{"周", "单周", "双周"};


    public static void setPATH(String PATH) {
        Utils.PATH = PATH;
    }

    /**
     * 设置背景图片
     *
     * @param context
     * @param imageView
     */
    public static void setBackGround(Context context, ImageView imageView) {
        setBackGround(context, imageView, Config.getBgId());
    }

    public static void setBackGround(Context context, ImageView imageView, int id) {

        if (bgBitmap == null) {
            refreshBg(context, id);
        }
        imageView.setImageBitmap(bgBitmap);

    }

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 计算1970年1月4号0时0分0秒(周一)至今有多少周
     * 用于更新周数，用一年周数的话跨年会产生问题
     *
     * @return
     */
    public static long getWeekNum() {
        //System.currentTimeMillis()返回的是1970年1月4号0时0分0秒距今多少毫秒
        long day = System.currentTimeMillis() / (1000 * 60 * 60 * 24) - 4;//减四为1月4日距今多少天
        //Log.d("weeknum",String.valueOf(day/7+1));
        return day / 7 + 1;
    }

    /**
     * 刷新背景
     *
     * @param context
     * @param id
     */
    public static void refreshBg(Context context, int id) {

        if (id == 0) {
            File file = new File(PATH, BG_NAME);
            if (file.exists()) {
                bgBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } else {
            bgBitmap = BitmapFactory.decodeResource(context.getResources(), id);
        }
    }


    /**
     * 获取app版本号以供更新
     *
     * @param context
     * @return
     */
    public static long getLocalVersionCode(Context context) {
        long localVersion = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                localVersion = packageInfo.getLongVersionCode();
            } else {
                localVersion = packageInfo.versionCode;
            }
            //Log.d("TAG", "当前版本号：" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 检查是否有新版本
     *
     * @param versionCode
     * @return
     */
    public static String checkUpdate(long versionCode) {
        String json=OkHttpUtils.downloadText(UPDATE_URL);
        if(!json.isEmpty())
        {
            Version version = new Gson().fromJson(json, Version.class);
            //Log.d("update","最新版本号"+version.getVersionCode());
            if (version.getVersionCode() > versionCode) {
                return BASE_URL + version.getReleaseName();
            }
        }
        return "";

    }

    /**
     * 设置CardView透明度
     *
     * @param cardView
     */
    public static void setCardViewAlpha(CardView cardView) {
        cardView.setAlpha(Config.getCardViewAlpha());
    }

    /**
     * 判断是单周、双周、还是周
     *
     * @param weekOfTerm
     * @return
     */
    public static int getWeekOptionFromWeekOfTerm(int weekOfTerm) {
        int singleWeek = 0x55555555;//二进制:0101,0101,0101,0101,0101,0101,0101,0101
        int doubleWeek = 0xaaaaaaaa;//二进制:1010,1010,1010,1010,1010,1010,1010,1010

        //如果总周数是偶数则互换，保证算法的正确性
        if (Config.getMaxWeekNum() % 2 == 0) {
            int temp = singleWeek;
            singleWeek = doubleWeek;
            doubleWeek = temp;
        }
        //快速判断是否有单周或者双周
        boolean hasSingleWeek = ((singleWeek & weekOfTerm) != 0);
        boolean hasDoubleWeek = ((doubleWeek & weekOfTerm) != 0);
        if (hasSingleWeek && hasDoubleWeek) {
            return SINGLE_DOUBLE_WEEK;
        } else if (hasSingleWeek) {
            return SINGLE_WEEK;
        } else if (hasDoubleWeek) {
            return DOUBLE_WEEK;
        } else {
            return -1;
        }

    }

    /**
     *
     * @param weekOfTerm
     * @return 获取格式为"1-9,19,20-25 [周]"的周数
     */
    public static String getFormatStringFromWeekOfTerm(int weekOfTerm) {
        return getStringFromWeekOfTerm(weekOfTerm) +
                " [" +
                WEEK_OPTIONS[getWeekOptionFromWeekOfTerm(weekOfTerm)] +
                "]";
    }

    /**
     * 生成1-18,19,25格式的周数
     *
     * @param weekOfTerm
     * @return
     */
    public static String getStringFromWeekOfTerm(int weekOfTerm) {
        if (weekOfTerm == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();

        int weekOptions = getWeekOptionFromWeekOfTerm(weekOfTerm);

        boolean week[] = new boolean[Config.getMaxWeekNum()];

        for (int i = Config.getMaxWeekNum() - 1; i >= 0; i--) {
            week[i] = ((weekOfTerm & 0x01) == 1);
            weekOfTerm = weekOfTerm >> 1;
        }
        String weekOptionsStr = "";

        int start = 1;
        int space = 2;

        switch (weekOptions) {
            case SINGLE_DOUBLE_WEEK:
                space = 1;
                break;
            case SINGLE_WEEK:
                break;
            case DOUBLE_WEEK:
                start = 2;
                break;
            default:
                return "error";

        }
        int count = 0;
        for (int i = start; i <= Config.getMaxWeekNum(); i += space) {
            if (week[i-1]) {
                if (count == 0) {
                    stringBuilder.append(i);
                }
                count += 1;
            } else {
                if (count == 1) {
                    stringBuilder.append(',');
                } else if (count > 1) {
                    stringBuilder.append('-');
                    stringBuilder.append(i-space);
                    stringBuilder.append(',');
                }
                count = 0;
            }
        }
        if (count > 1) {
            stringBuilder.append('-');
            int max=Config.getMaxWeekNum();
            if(start==1&&max%2==0){//单周
                max--;
            }else if(start==2&&max%2==1){//双周
                max--;
            }
            stringBuilder.append(max);
        }
        int len = stringBuilder.length() - 1;
        if (stringBuilder.charAt(len) == ',')
            stringBuilder.deleteCharAt(len);

        return stringBuilder.toString();
    }

    public static String formatTime(int time) {
        return time < 10 ? "0" + time : String.valueOf(time);
    }
    /**
     * 获取星期
     *
     * @return 返回1-7代表周几 周日为1
     */
    public static int getWeekOfDay() {

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
