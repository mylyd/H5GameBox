package com.mobo.funplay.gamebox.manager;

import android.content.Context;

import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.interfaces.ListCallback;
import com.mobo.funplay.gamebox.utils.SystemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : ydli
 * @time : 20-7-23 下午12:13
 * @description 本地推送
 */
public class LocalGamePushManager {
    private static LocalGamePushManager localGamePushManager;
    //push 抽样数据条数
    public static int ITEM_PUSH = 4;

    public static LocalGamePushManager getInstance() {
        if (localGamePushManager == null) {
            synchronized (LocalGamePushManager.class) {
                if (localGamePushManager == null) {
                    localGamePushManager = new LocalGamePushManager();
                }
            }
        }
        return localGamePushManager;
    }

    /**
     * 返回从数据列表中随机的一个值
     */
    public static List<GameItemBean> getLocalPush() {
        List<GameItemBean> bean = SPManager.getInstance().getGameLocalPushList();
        if (bean == null || bean.size() == 0) {
            return null;
        }
        return bean;
    }

    public void addLocalPush(List<GameItemBean> data) {
        SPManager.getInstance().putGameLocalPushList(data);
    }

    public void Request(Context context) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", context.getPackageName());
        queryParams.put("versionCode", String.valueOf(SystemUtils.getVersionCode(context)));
        queryParams.put("pageSize", Integer.toString(ITEM_PUSH));
        queryParams.put("page", Integer.toString(1));
        queryParams.put("w_type", Constants.WALLPAPER_GAME);

        RetrofitManager.INSTANCE.getRequest().getGameHome(queryParams).enqueue(new ListCallback<GameItemBean>() {
            @Override
            public void onResponse(List<GameItemBean> response) {
                if (response == null) {
                    return;
                }
                addLocalPush(response);
            }

            @Override
            public void onFailure(Throwable throwable, boolean isServerUnavailable) {

            }
        });
    }

}
