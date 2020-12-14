package com.mobo.funplay.gamebox.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 全局字体样式修改
 */
public class FontsUtils {
    private static Typeface cmtTypeFace;

    /**
     * 非中文内容使用此字体样式（设计需求）设置字体样式
     */
    public static void setCMTFonts(Context context, TextView textView) {
        textView.setTypeface(getCMTTypeface(context));// 设置字体样式
    }

    /**
     * 非中文内容使用此字体样式（设计需求） 获取Typeface
     */
    public static Typeface getCMTTypeface(Context context) {
        if (cmtTypeFace == null) {
            cmtTypeFace = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Aileron-Light.otf");// 根据路径得到Typeface
        }
        return cmtTypeFace;
    }

    /**
     * 设置全局字体样式
     *
     * @param context 上下文
     */
    public static void setAppTypeface(Context context) {
        try {
            //SERIF 对应是在style里面的 <item name="android:typeface">serif</item>
            Field field = Typeface.class.getDeclaredField("SERIF");
            field.setAccessible(true);
            field.set(null, getCMTTypeface(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
