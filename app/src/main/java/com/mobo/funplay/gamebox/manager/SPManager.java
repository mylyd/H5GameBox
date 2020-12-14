
package com.mobo.funplay.gamebox.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobo.funplay.gamebox.MyApp;
import com.mobo.funplay.gamebox.bean.BannerItem;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.bean.push.LocalPushMessage;
import com.mobo.funplay.gamebox.constants.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * author : jzhou
 * time   : 2019/11/22
 * desc   : SharedPreferences工具类
 * version: 1.0
 */
public class SPManager {

    private static SPManager sInstance;
    private SharedPreferences mSharedPreferences;

    public static final String BANNER = "BANNER";

    public static SPManager getInstance() {
        if (sInstance == null) {
            synchronized (SPManager.class) {
                if (sInstance == null)
                    sInstance = new SPManager(MyApp.getInstance());
            }
        }
        return sInstance;
    }

    private SPManager(Context context) {
        String sharedName = context.getPackageName() + "_preferences";
        mSharedPreferences = context.getSharedPreferences(sharedName, MODE_PRIVATE
                | Context.MODE_MULTI_PROCESS);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void setInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public void setLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public void setString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void setBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public Boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public void setList(String key, List<String> list) {
        String ret = new Gson().toJson(list);
        mSharedPreferences.edit().putString(key, ret).apply();
    }

    public List<String> getList(String key) {
        String ret = mSharedPreferences.getString(key, "");
        if (TextUtils.isEmpty(ret)) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(ret, new TypeToken<List<String>>() {
        }.getType());
    }

    /**
     * 获取已经解锁的id集合
     *
     * @param key
     * @return
     */
    public Set<Integer> getUnLockIds(String key) {
        String ret = mSharedPreferences.getString(key, "");
        if (TextUtils.isEmpty(ret)) {
            return Collections.emptySet();
        }
        return new Gson().fromJson(ret, new TypeToken<Set<Integer>>() {
        }.getType());
    }

    /**
     * 设置已经解锁的id集合
     *
     * @param key
     * @param ids
     */
    public void setUnLockIds(String key, Set<Integer> ids) {
        String ret = new Gson().toJson(ids);
        mSharedPreferences.edit().putString(key, ret).apply();
    }

    /**
     * banner 轮播图内置资源
     *
     * @param bannerItems
     */
    public void setBanner(List<BannerItem> bannerItems) {
        mSharedPreferences.edit().putString(BANNER, new Gson().toJson(bannerItems, new TypeToken<List<BannerItem>>() {
        }.getType())).apply();
    }

    public List<BannerItem> getBanner() {
        String str = mSharedPreferences.getString(BANNER, null);
        return new Gson().fromJson(str, new TypeToken<List<BannerItem>>() {
        }.getType());
    }

    /**
     * 存储游戏列表数据
     *
     * @param gameBeans
     */
    public void putGameHomeList(List<GameItemBean> gameBeans) {
        mSharedPreferences.edit().putString(Constants.LOCAL_DATA_GAME_HOT_LIST, new Gson().toJson(gameBeans, new TypeToken<List<GameItemBean>>() {
        }.getType())).apply();
    }

    /**
     * 获取游戏列表数据
     *
     * @return List<GameBean>
     */
    public List<GameItemBean> getGameHomeList() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.LOCAL_DATA_GAME_HOT_LIST, null);
        Type type = new TypeToken<List<GameItemBean>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 存储游戏分类列表数据
     *
     * @param gameBeans
     */
    public void putGameCategoryList(List<GameBean> gameBeans) {
        mSharedPreferences.edit().putString(Constants.LOCAL_DATA_MY_GAME_LIST, new Gson().toJson(gameBeans, new TypeToken<List<GameBean>>() {
        }.getType())).apply();
    }

    /**
     * 获取CategoryBean 分类列表数据
     *
     * @return List<GameBean>
     */
    public List<GameBean> getGameCategoryList() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.LOCAL_DATA_MY_GAME_LIST, null);
        Type type = new TypeToken<List<GameBean>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 存储游戏搜索标签数据
     *
     * @param gameBeans
     */
    public void putLabelList(ArrayList<String> gameBeans) {
        if (gameBeans == null) {
            mSharedPreferences.edit().putInt(Constants.DATA_LABEL_SEARCH_LIST_SIZE, 0).apply();
            return;
        }
        mSharedPreferences.edit().putInt(Constants.DATA_LABEL_SEARCH_LIST_SIZE, gameBeans.size()).apply();
        for (int i = 0; i < gameBeans.size(); i++) {
            mSharedPreferences.edit().putString(Constants.DATA_LABEL_SEARCH_LIST + i, gameBeans.get(i)).apply();
        }
    }

    /**
     * 获取游戏搜索标签数据
     *
     * @return List<GameBean>
     */
    public ArrayList<String> getLabelList() {
        int index = mSharedPreferences.getInt(Constants.DATA_LABEL_SEARCH_LIST_SIZE, 0);
        if (index == 0) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            arrayList.add(mSharedPreferences.getString(Constants.DATA_LABEL_SEARCH_LIST + i, null));
        }
        return arrayList;
    }

    /**
     * 新增收藏搜索标签数据
     */
    public static void addLabel(String searchBean) {
        ArrayList<String> searchBeans = SPManager.getInstance().getLabelList();
        if (searchBeans != null) {
            //去重
            if (searchBeans.contains(searchBean)) {
                return;
            }
        } else {
            searchBeans = new ArrayList<>();
        }
        searchBeans.add(searchBean);
        //反向排序
        Collections.reverse(searchBeans);
        SPManager.getInstance().putLabelList(searchBeans);
    }

    /**
     * 删除搜索标签数据
     */
    public static void deleteLabel(String searchBean) {
        ArrayList<String> searchBeans = SPManager.getInstance().getLabelList();
        if (searchBeans != null) {
            if (searchBeans.contains(searchBean)) {
                searchBeans.remove(searchBean);
            }
        } else {
            searchBeans = new ArrayList<>();
        }
        SPManager.getInstance().putLabelList(searchBeans);
    }

    /**
     * 存储游戏收藏列表数据
     *
     * @param gameBeans
     */
    public void putGameCollectList(List<GameItemBean> gameBeans) {
        mSharedPreferences.edit().putString(Constants.DATA_COLLECT_GAME_LIST, new Gson().toJson(gameBeans, new TypeToken<List<GameItemBean>>() {
        }.getType())).apply();
    }

    /**
     * 获取游戏收藏列表数据
     *
     * @return List<GameBean>
     */
    public List<GameItemBean> getGameCollectList() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.DATA_COLLECT_GAME_LIST, null);
        Type type = new TypeToken<List<GameItemBean>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 新增收藏列表数据
     */
    public static void addGameCollectBean(GameItemBean gameBean) {
        List<GameItemBean> gameCollectList = SPManager.getInstance().getGameCollectList();
        if (gameCollectList != null) {
            //去重
            gameCollectList.remove(gameBean);
        } else {
            gameCollectList = new ArrayList<>();
        }
        gameCollectList.add(gameBean);
        SPManager.getInstance().putGameCollectList(gameCollectList);
    }

    /**
     * 删除收藏列表数据
     */
    public static void deleteGameCollectBean(GameItemBean gameBean) {
        List<GameItemBean> gameCollectList = SPManager.getInstance().getGameCollectList();
        if (gameCollectList != null) {
            gameCollectList.remove(gameBean);
        } else {
            gameCollectList = new ArrayList<>();
        }
        SPManager.getInstance().putGameCollectList(gameCollectList);
    }

    /**
     * 存储LocalPush延时时间数据
     *
     * @param intervals
     */
    public void putLocalPushIntervalList(List<Integer> intervals) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String json = new Gson().toJson(intervals);
        editor.putString(Constants.KEY_LOCAL_PUSH_INTERVAL_DATA, json).apply();
    }

    /**
     * 获取LocalPush延时时间数据
     *
     * @return
     */
    public List<Integer> getLocalPushIntervalList() {
        String json = mSharedPreferences.getString(Constants.KEY_LOCAL_PUSH_INTERVAL_DATA, null);
        return new Gson().fromJson(json, new TypeToken<List<Integer>>() {
        }.getType());
    }

    /**
     * 存储LocalPushItem数据
     *
     * @param items
     */
    public void putLocalPushMessageList(List<LocalPushMessage> items) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String json = new Gson().toJson(items);
        editor.putString(Constants.KEY_LOCAL_PUSH_MESSAGE_DATA, json).apply();
    }

    /**
     * 获取LocalPushItem数据
     *
     * @return
     */
    public List<LocalPushMessage> getLocalPushMessageList() {
        String json = mSharedPreferences.getString(Constants.KEY_LOCAL_PUSH_MESSAGE_DATA, null);
        return new Gson().fromJson(json, new TypeToken<List<LocalPushMessage>>() {
        }.getType());
    }

    /**
     * 存储本地推送游戏列表数据
     *
     * @param gameBeans
     */
    public void putGameLocalPushList(List<GameItemBean> gameBeans) {
        mSharedPreferences.edit().putString(Constants.LOCAL_GAME_LIST_PUSH_ITEM, new Gson().toJson(gameBeans, new TypeToken<List<GameItemBean>>() {
        }.getType())).apply();
    }

    /**
     * 获取本地推送游戏列表数据
     *
     * @return List<GameBean>
     */
    public List<GameItemBean> getGameLocalPushList() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.LOCAL_GAME_LIST_PUSH_ITEM, null);
        Type type = new TypeToken<List<GameItemBean>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 获取Hot游戏列表数据
     *
     * @return List<GameBean>
     */
    public List<GameItemBean> getGameHotFragmentList() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.GAME_HOT_LIST, null);
        Type type = new TypeToken<List<GameItemBean>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 存储Hot游戏列表数据
     *
     * @param gameBeans
     */
    public void putGameHotFragmentList(List<GameItemBean> gameBeans) {
        mSharedPreferences.edit().putString(Constants.GAME_HOT_LIST, new Gson().toJson(gameBeans, new TypeToken<List<GameItemBean>>() {
        }.getType())).apply();
    }

    /**
     * 存储游戏列表数据
     *
     * @param gameBeans
     */
    public void putGameWebViewList(List<GameItemBean> gameBeans) {
        mSharedPreferences.edit().putString(Constants.WEB_VIEW_GAME_DATA_LIST, new Gson().toJson(gameBeans, new TypeToken<List<GameItemBean>>() {
        }.getType())).apply();
    }

    /**
     * 获取游戏列表数据
     *
     * @return List<GameBean>
     */
    public List<GameItemBean> getGameWebViewList() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.WEB_VIEW_GAME_DATA_LIST, null);
        Type type = new TypeToken<List<GameItemBean>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}
