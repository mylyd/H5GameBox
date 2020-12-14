package com.mobo.funplay.gamebox.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import com.mobo.funplay.gamebox.MyApp;

import java.io.File;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * Created by dfwm on 2016/6/24.
 */
public class SystemUtils {

    public static void startWebView(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startWebView(Fragment fragment, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            fragment.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
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

    /**
     * 获取系统内存大小
     *
     * @param context
     * @return
     */
    public static long getTotalMem(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        // 单位Byte
        return info.totalMem;
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
        return androidId;
    }

//    public static String getDeviceId(Context context) {
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        return tm.getDeviceId();
//    }

    //加入白名单，防止手机休眠后app停止
    public static void requestIgnoringBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = Utils.getPackageName(context);
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //是否加入白名單
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

    public static boolean existSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static String getCachePath(Context context) {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = context.getExternalCacheDir();
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

//    /**
//     * 调用系统拨电话功能
//     *
//     * @param context
//     * @param tel
//     */
//    public static void call(Context context, String tel) {
//        Intent intent = new Intent(Intent.ACTION_CALL);
//        Uri data = Uri.parse("tel:" + tel);
//        intent.setData(data);
//        try {
//            context.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getLocalIpAddress1() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void hideSoftKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //隐藏软键盘
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        //切换软键盘的显示与隐藏
        //imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showSoftKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //显示软键盘
        //imm.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
        //切换软键盘的显示与隐藏
        imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 选择相册成功以后取出图片路径
     *
     * @param uri
     * @return
     */
    public static String getMediaImagePath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(column_index);
            cursor.close();
            return imagePath;
        }
        return null;
    }

    /**
     * 拍照成功以后取出图片路径
     *
     * @param uri
     * @return
     */
    public static String getCameraImagePath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getMediaImagePath(context, uri);
        } else {
            return uri.getPath();
        }
    }

//    /**
//     * 判断app是否在前台运行
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isRunningForeground(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        String currentPackageName = cn.getPackageName();
//        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName())) {
//            return true;
//        }
//        return false;
//    }

    public static boolean isImageFile(String filepath) {
        return filepath.endsWith(".jpg") || filepath.endsWith(".png") || filepath.endsWith(".jpeg")
                || filepath.endsWith(".bmp") || filepath.endsWith(".gif");
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpiHeight(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int totalHeight = getDpiHeight(context);

        int contentHeight = getWindowHeight(context);

        return totalHeight - contentHeight;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
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
     * 检测app是否被安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    //获取是否存在NavigationBar
    public static boolean isNavigationBarExist(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }



    /**
     * 检测是否支持陀螺仪
     */
    public static boolean isSensorSupport() {
        SensorManager sensorManager = (SensorManager) MyApp.getInstance().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        return sensor != null;
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
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 增加按钮动效
     *
     * @param v
     * @param scaleX
     * @param scaleY
     */
    public static void startRepeatScaleAnimation(final View v, final float scaleX, final float scaleY) {
        v.animate().cancel();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        v.animate().scaleX(scaleX).scaleY(scaleY).setInterpolator(interpolator).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!v.isEnabled()) return;
                if (v.getScaleX() == 1) {
                    v.animate().scaleX(scaleX).scaleY(scaleY).setInterpolator(interpolator).setListener(this).start();
                } else {
                    v.animate().scaleX(1).scaleY(1).setInterpolator(interpolator).setListener(this).start();
                }
            }
        }).start();
    }
}
