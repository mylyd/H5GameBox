package com.mobo.funplay.gamebox.utils;

import android.text.TextUtils;
import android.util.Log;

import com.mobo.funplay.gamebox.BuildConfig;


/**
 * log打印控制类；
 * 1、当打印内容为空时，不打印任何log
 * 2、warn以下级别，release版本时，不打印
 * 3、暂时只有级别debug、error
 */
public class MoboLogger {

    // 不允许构造对象
    private MoboLogger() {
    }

    private static boolean sIsRelease = !BuildConfig.DEBUG;

    public static void debug(String tag, String content) {
        if (sIsRelease) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Log.d(tag, content);
    }

    public static void error(String tag, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Log.e(tag, content);
    }

    public static void warn(String tag, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Log.w(tag, content);
    }
}