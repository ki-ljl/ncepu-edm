/**
 * Date   : 2021/2/4 19:32
 * Author : KI
 * File   : TimeUtils_1
 * Desc   : delay_1
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.Utils;

public class TimeUtils_1 {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 6000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}

