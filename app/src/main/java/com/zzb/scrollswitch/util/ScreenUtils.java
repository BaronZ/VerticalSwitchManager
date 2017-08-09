package com.zzb.scrollswitch.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by ZZB on 2017/8/9.
 */

public class ScreenUtils {

    public static int getScreenHeightWithoutStatusBar(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels - getStatusBarHeight(context);
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
