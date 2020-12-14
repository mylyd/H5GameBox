package com.mobo.funplay.gamebox.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonElement;
import com.mobo.funplay.gamebox.MyApp;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.helper.DefImageHelper;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.interfaces.InterfaceRequest;
import com.mobo.funplay.gamebox.manager.RetrofitManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.http.Url;

public final class Utils {
    private Utils() {
    }

    /**
     * 获取android唯一识别码
     *
     * @param context 上下文
     * @return 返回唯一识别码
     */
    public static String getAndroidId(Context context) {
        String androidId = "null";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
        }

        if (androidId == null || "".equals(androidId)) {
            androidId = "null";
        }

        return getStringMD5(androidId);
    }

    /**
     * 对字符串进行加密
     *
     * @param plainText 待加密字符串
     * @return 加密后字符串
     */
    public static String getStringMD5(String plainText) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
        } catch (Exception e) {
            return null;
        }
        return encodeHex(md.digest());

    }

    /**
     * 二进制加密为16进制转换
     *
     * @param data 待转换二进制
     * @return 加密后字符串
     */
    public static String encodeHex(byte[] data) {
        if (data == null) {
            return null;
        }
        final String HEXES = "0123456789abcdef";
        int len = data.length;
        StringBuilder hex = new StringBuilder(len * 2);
        for (int i = 0; i < len; ++i) {
            hex.append(HEXES.charAt((data[i] & 0xF0) >>> 4));
            hex.append(HEXES.charAt((data[i] & 0x0F)));
        }
        return hex.toString();
    }

    //版本名
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context);
        if (info == null) {
            return "";
        }
        return info.versionName;
    }

    //版本号
    public static String getVersionCode(Context context) {
        PackageInfo info = getPackageInfo(context);
        if (info == null) {
            return null;
        }
        return String.valueOf(info.versionCode);
    }

    //获取app版本信息
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPackageName(Context context) {
        if (context != null) {
            return context.getPackageName();
        }
        return "";
    }

    public static String getCurrentCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    public static String getCurrentLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static String getChannelId(Context context) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                String channelId =
                        appInfo.metaData.get("CYOU_CHANNEL").toString();
                if (channelId != null) {
                    return channelId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 从drawable中获取图片
     *
     * @param drawable
     * @return
     */
    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable == null || drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap result = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        drawable.draw(canvas);
        return result;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, int resourceId) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (context.getResources().getDimension(resourceId) * scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param fragment
     * @param dipValue
     * @return
     */
    public static int dip2px(Fragment fragment, float dipValue) {
        final float scale = fragment.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 提交反馈信息
     *
     * @param email
     * @param suggestion
     */
    public static void requestFeedBack(Context context, String email, String suggestion, CommonCallback<JsonElement> commonCallback) {
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("pkgname", Utils.getPackageName(context));
        paramMap.put("version", getVersionName(context));
        paramMap.put("vercode", getVersionCode(context));
        paramMap.put("did", getAndroidId(context));
        paramMap.put("deviceModel", android.os.Build.MODEL);
        paramMap.put("os", android.os.Build.VERSION.RELEASE);
        paramMap.put("language", getCurrentLanguage());
        paramMap.put("country", getCurrentCountry());
        paramMap.put("channelId", getChannelId(context));
        paramMap.put("resolution", String.valueOf(getWindowHeight(context)));
        paramMap.put("cpu", android.os.Build.BOARD);
        paramMap.put("email", email);
        paramMap.put("message", suggestion);

        RetrofitManager.INSTANCE.getRequest().postFeedBack(paramMap).enqueue(commonCallback);
    }


    // 检查x，y坐标是不是在view内
    public static boolean checkTouchInView(View view, float x, float y) {
        if (view == null) {
            return false;
        }
        if (view.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] local = new int[2];
        view.getLocationOnScreen(local);
        RectF rect = new RectF(local[0], local[1], local[0] + view.getWidth(), local[1] + view.getHeight());
        return rect.contains(x, y);
    }

    // 检查x，y坐标是不是在view内
    public static boolean checkTouchInView(View view, MotionEvent event) {
        if (view == null) {
            return false;
        }
        if (view.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] local = new int[2];
        view.getLocationOnScreen(local);
        RectF rect = new RectF(local[0], local[1], local[0] + view.getWidth(), local[1] + view.getHeight());
        return rect.contains(event.getRawX(), event.getRawY());
    }

    public static ColorDrawable getColorDrawable(Context context, int resId) {
        return new ColorDrawable(context.getResources().getColor(resId));
    }

    public static void setSize(View view, int width, int height) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = width;
            params.height = height;
        } else {
            params = new ViewGroup.LayoutParams(width, height);
        }
        view.setLayoutParams(params);
    }

    /**
     * 在固定大小的居中png资源
     *
     * @param context
     * @param resId
     * @param width
     * @param height
     * @return
     */
    public static Drawable getDefDrawable(Context context, int resId, int width, int height) {
        BitmapDrawable result = DefImageHelper.get(resId, width, height);
        if (result != null) {
            MoboLogger.debug("DefImageHelper", "get cache def image");
            return result;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#fff0f0f0"));
        Drawable drawable = context.getResources().getDrawable(resId);
        if (drawable instanceof BitmapDrawable) {
            Matrix matrix = new Matrix();
            matrix.postTranslate((width - drawable.getIntrinsicWidth()) / 2f, (height - drawable.getIntrinsicHeight()) / 2f);
            canvas.drawBitmap(((BitmapDrawable) drawable).getBitmap(), matrix, null);
        }

        result = new BitmapDrawable(bitmap);
        DefImageHelper.put(result, resId, width, height);
        return result;
    }

    //首字母转小写
    public static String toUpperCaseFirstOne(String s) {
        if (!TextUtils.isEmpty(s) && !Character.isUpperCase(s.charAt(0))) {
            s = (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
        return s;
    }

    public static Context getContext(Object object) {
        Context result = null;
        if (object instanceof Activity) {
            result = (Activity) object;
        } else if (object instanceof View) {
            result = ((View) object).getContext();
        } else if (object instanceof Fragment) {
            result = ((Fragment) object).getContext();
        } else if (object instanceof android.app.Fragment) {
            result = ((android.app.Fragment) object).getActivity();
        }

        return result != null ? result : MyApp.getInstance();
    }

    public static String getJson(Context context, String fileName) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
        }
        return stringBuilder.toString();
    }

}
