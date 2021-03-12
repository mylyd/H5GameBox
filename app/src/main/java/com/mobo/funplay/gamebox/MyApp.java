package com.mobo.funplay.gamebox;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.mobo.funplay.gamebox.bean.GrayItem;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.manager.GlobalConfigurationManager;
import com.mobo.funplay.gamebox.manager.LocalGamePushManager;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.push.LocalPushManager;
import com.mobo.funplay.gamebox.tracker.FacebookTracker;
import com.mobo.funplay.gamebox.tracker.FirebaseTracker;
import com.mobo.funplay.gamebox.utils.MD5Util;
import com.mobo.funplay.gamebox.utils.SystemUtils;
import com.mobo.funplay.gamebox.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * @author : create by zq
 * @time : 19-7-5  15:47
 */
public class MyApp extends MultiDexApplication {
    private static final String TAG = "MyApp";

    private static MyApp instance;

    private static boolean hadLaunched;

    int width, height;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 暂时只有admob广告
        //AdManager.getInstance().initContext(this).initAdmob(getString(R.string.gms_app_id)).updateStrategy(Utils.getJson(this, "ad.json"));

        initScreenSize();

        requestGray();
        LocalPushManager.getInstance().requestConfig(this);
        LocalGamePushManager.getInstance().Request(this);
        GlobalConfigurationManager.getInstance().requestConfig(this);
        //SecurityMgr.init(this);
    }


    public static MyApp getInstance() {
        return instance;
    }

    public static void setHadLaunched(boolean b) {
        hadLaunched = b;
    }

    public static boolean getHadLaunched() {
        return hadLaunched;
    }

    /**
     * 获取屏幕宽高；为feed流中图片提供合适的大小
     */
    private void initScreenSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void requestGray() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("did", MD5Util.getStringMD5(Utils.getAndroidId(this)));
        if (Locale.getDefault().toString().equals("zh_CN_#Hans")) {
            queryParams.put("lc", "zh_CN");
        } else {
            queryParams.put("lc", Locale.getDefault().toString());
        }
        queryParams.put("pn", getPackageName());
        queryParams.put("appvc", String.valueOf(SystemUtils.getVersionCode(this)));
        queryParams.put("appvn", SystemUtils.getVersionName(this));
        queryParams.put("os", "android");
        queryParams.put("chn", "ofw");
        queryParams.put("avn", String.valueOf(Build.VERSION.SDK_INT));

        RetrofitManager.INSTANCE.getRequest().getSwitchConfig(queryParams).enqueue(new CommonCallback<GrayItem>() {
            @Override
            public void onResponse(GrayItem response) {
                if (response.getData() != null) {
                    List<GrayItem.DataBean> list = response.getData();
                    for (GrayItem.DataBean bean : list) {
                        Log.d(TAG, bean.getTag() + "：" + bean.isStatus());
                        switch (bean.getTag()) {
                            case "game_ads_value":
                                GrayStatus.game_ads_value = bean.isStatus();
                                break;
                            case "push_H5":
                                GrayStatus.push_H5 = bean.isStatus();
                                break;
                            case "game_recommend":
                                GrayStatus.game_recommend = bean.isStatus();
                                break;
                            case "game_icon_show":
                                GrayStatus.game_icon_show = bean.isStatus();
                                break;
                            case "tab_hot":
                                GrayStatus.tab_hot = bean.isStatus();
                                break;
                            case "main_tab_slide":
                                GrayStatus.main_tab_slide = bean.isStatus();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, boolean isServerUnavailable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

}
