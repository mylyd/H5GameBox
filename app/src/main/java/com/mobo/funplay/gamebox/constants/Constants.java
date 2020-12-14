package com.mobo.funplay.gamebox.constants;

import android.Manifest;

import com.mobo.funplay.gamebox.R;

/**
 * @Description: 全局变量
 * @Author: jzhou
 * @CreateDate: 19-8-28 下午6:33
 */
public class Constants {
    public static final long ONE_DAY_TIME = 24 * 60 * 60 * 1000L;

    /*游戏分类list页面 每个分类展示游戏数量*/
    public static final int GAME_MAX_ITEM_COUNT = 6;

    public static final int PAGE_SIZE = 18;
    public static final int PAGE_MAX = 100;

    //请求类型
    public static final String WALLPAPER_GAME = "game";

    //游戏分类界面预置数据 key
    public static final String LOCAL_DATA_MY_GAME_LIST = "LOCAL_DATA_MY_GAME_LIST";
    //游戏界面预置数据 key
    public static final String LOCAL_DATA_GAME_HOT_LIST = "LOCAL_DATA_GAME_HOT_LIST";
    public static final String GAME_HOT_LIST = "GAME_HOT_LIST";
    //游戏分类跳转分类详情 key
    public static final String CATEGORY_GAME_LIST_BEAN = "category_game_list_bean";
    public static final String CATEGORY_GAME_LIST_IS_PREVIEW = "category_game_list_is_preview";
    public static final String CATEGORY_GAME_LIST_POSITION = "category_game_list_position";
    public static final String SORT_TYPE_HEAT = "hot";

    //webView 下滑存储数据key
    public static String WEB_VIEW_GAME_DATA_LIST = "web_view_game_data_list";

    //游戏收藏 key
    public static final String DATA_COLLECT_GAME_LIST = "data_collect_game_list";
    public static final String DATA_LABEL_SEARCH_LIST = "DATA_LABEL_SEARCH_LIST";
    public static final String DATA_LABEL_SEARCH_LIST_SIZE = "DATA_LABEL_SEARCH_LIST_SIZE";
    public static final String DATA_COLLECT_GAME_LIST_NUM = "data_collect_game_list_num";

    //搜索结果广播key
    public static final String SEARCH_RESULTS_BROADCAST_ACTION = "search_results_broadcast_action";
    public static final String SEARCH_RESULTS_BROADCAST_VALUE = "search_results_broadcast_value";

    //push推送key
    public static final String KEY_LOCAL_PUSH_INTERVAL_DATA = "local_push_interval_data";
    public static final String KEY_LOCAL_PUSH_MESSAGE_DATA = "local_push_message_data";

    public static String PUSH_TIME_KEY = "push_time_key";

    //本地推送资源key
    public static final String LOCAL_GAME_LIST_PUSH_ITEM = "local_game_list_push_item";

    public static final String game_url_woso = "coldchick.xyz";
    public static final String game_url_woso_home = "https://a.coldchick.xyz/";
    public static final String game_url_yingxian = "acgame.net";
    public static final String game_url_yingxian_home = "https://top.acgame.net/main/ac716";

    //more
    public static String game_url = "https://a.coldchick.xyz/";

    //判断在收藏页面没有数据回跳时跳转到主页hot game 页面
    public static boolean INTENT_COLLECTION = false;

    //界面类型
    public static String GAME_HOME = "game_home";
    public static String GAME_CATEGORY = "game_category";
    public static String GAME_SEARCH_RESULT = "game_search_result";
    public static String GAME_CATE_DETAIL = "game_cate_detail";
    public static String GAME_MINE = "game_mine";
    public static String GAME_COLLECTION = "game_collection";
    public static String GAME_BANNER = "game_banner";
    public static String GAME_ICON = "game_icon";
    public static String GAME_HOT = "game_hot";
    public static String GAME_PUSH = "game_push";

}
