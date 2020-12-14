package com.mobo.funplay.gamebox.manager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.mobo.funplay.gamebox.bean.ConfigBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author : ydli
 * @time : 20-7-23 下午4:15
 * @description 全局接口
 */
public class GlobalConfigurationManager {
    private static GlobalConfigurationManager globalConfigurationManager;

    public static GlobalConfigurationManager getInstance() {
        if (globalConfigurationManager == null) {
            synchronized (GlobalConfigurationManager.class) {
                if (globalConfigurationManager == null) {
                    globalConfigurationManager = new GlobalConfigurationManager();
                }
            }
        }
        return globalConfigurationManager;
    }

    public void requestConfig(Context context) {
        //TODO 全局配置接口
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", context.getPackageName());
        queryParams.put("language", Locale.getDefault().getLanguage());
        queryParams.put("country", Locale.getDefault().getCountry());
        queryParams.put("versionCode", Utils.getVersionCode(context));
        queryParams.put("sdkVersion", String.valueOf(Build.VERSION.SDK_INT));
        RetrofitManager.INSTANCE.getRequest().getConfigRequest(queryParams)
                .enqueue(new CommonCallback<ConfigBean>() {
                    @Override
                    public void onResponse(ConfigBean response) {
                        if (response.getData().getCode() == 100) {
                            Constants.game_url = response.getData().getConfig().getHome_game_link();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, boolean isServerUnavailable) {
                        Log.d("onFailure", "onFailure: ");
                    }
                });
    }
}
