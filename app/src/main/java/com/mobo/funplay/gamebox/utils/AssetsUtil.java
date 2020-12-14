package com.mobo.funplay.gamebox.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.bean.push.LocalPushConfig;

import java.io.InputStream;
import java.util.List;

/**
 * @Description:
 * @Author: jzhou
 * @CreateDate: 19-8-15 下午9:36
 */
public class AssetsUtil {

    //从assets 文件夹中获取文件并读取数据
    public static String getJson(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getAssets().open(fileName);
            //获取文件的字节数
            int length = in.available();
            //创建byte数组
            byte[] buffer = new byte[length];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取GameHot预置数据信息
     *
     * @return
     */
    public static List<GameItemBean> getGameHome(Context context) {
        String json = getJson(context, "game_home_list.json");
        return new Gson().fromJson(json, new TypeToken<List<GameItemBean>>() {
        }.getType());
    }

    /**
     * 获取GameCategory预置数据信息
     *
     * @return
     */
    public static List<GameBean> getGameCategory(Context context) {
        String json = getJson(context, "game_category_list.json");
        return new Gson().fromJson(json, new TypeToken<List<GameBean>>() {
        }.getType());
    }

    /**
     * 获取无广告GameHot预置数据信息
     *
     * @return
     */
    public static List<GameItemBean> getGameHomeNoAds(Context context) {
        String json = getJson(context, "game_home_no_ads_list.json");
        return new Gson().fromJson(json, new TypeToken<List<GameItemBean>>() {
        }.getType());
    }

    /**
     * 获取无广告GameCategory预置数据信息
     *
     * @return
     */
    public static List<GameBean> getGameCategoryNoAds(Context context) {
        String json = getJson(context, "game_category_no_ads_list.json");
        return new Gson().fromJson(json, new TypeToken<List<GameBean>>() {
        }.getType());
    }

    /**
     * 获取push本地数据信息
     *
     * @return
     */
    public static LocalPushConfig.DataBean getLocalPushList(Context context) {
        String json = getJson(context, "local_push_list.json");
        return new Gson().fromJson(json, LocalPushConfig.DataBean.class);
    }

    /**
     * 获取hot数据信息
     *
     * @return
     */
    public static List<GameItemBean> getGameHotFragment(Context context) {
        String json = getJson(context, "game_hot.json");
        return new Gson().fromJson(json, new TypeToken<List<GameItemBean>>() {
        }.getType());
    }

}
