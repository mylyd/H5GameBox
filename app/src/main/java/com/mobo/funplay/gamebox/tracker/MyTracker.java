package com.mobo.funplay.gamebox.tracker;

/**
 * @author : ydli
 * @description track字段
 * @time : 14:50
 */
public interface MyTracker {
    //展示页内容
    String show_game_page_ = "show_game_page_";
    String show_category_page_ = "show_category_page_";
    String show_searchresult_page_ = "show_searchresult_page_";
    String show_catedetail_page_ = "show_catedetail_page_";
    String show_mine_page_ = "show_mine_page_";
    String show_collection_page_ = "show_collection_page_";

    //点击一个页面的游戏
    String click_game_img = "click_game_img";
    String click_category_img = "click_category_img";
    String click_searchresult_img = "click_searchresult_img";
    String click_catedetail_img = "click_catedetail_img";
    String click_collection_img = "click_collection_img";

    //点击一个游戏的名称
    String click_game_img_ = "click_game_img_";
    String click_category_img_ = "click_category_img_";
    String click_searchresult_img_ = "click_searchresult_img_";
    String click_catedetail_img_ = "click_catedetail_img_";
    String click_collection_img_ = "click_collection_img_";

    //点击收藏某个页面游戏的名称
    String click_game_collect_ = "click_game_collect_";
    String click_searchresult_collect_ = "click_searchresult_collect_";
    String click_catedetail_collect_ = "click_catedetail_collect_";

    //点击轮播图
    String click_banner_ = "click_banner_";

    //点击某一个功能
    String click_search = "click_search";
    String click_mycollection = "click_mycollection";
    String click_rateus = "click_rateus";
    String click_share = "click_share";
    String click_aboutus = "click_aboutus";
    String click_category_more = "click_category_more";

    //push实验
    //展示第一次push
    String PUSH_FRIST_SHOW = "push_frist_show";
    //点击第一次的push
    String PUSH_FRIST_CLICK = "push_frist_click";
    //展示第二次的push
    String PUSH_SECOND_SHOW = "push_second_show";
    //点击第二次的push
    String PUSH_SECOND_CLICK = "push_second_click";
    //展示第三次，和第三次以后的push
    String PUSH_THIRD_SHOW = "push_third_show";
    //点击第三次和第三次以后的push
    String PUSH_THIRD_CLICK = "push_third_click";

    //游戏推荐弹窗展示
    String show_suggest_img = "show_suggest_img";
    //点击游戏推荐弹窗的游戏
    String click_suggest_img = "click_suggest_img";
    //点击Game页和分类页右上角图标
    String click_little_icon = "click_little_icon";
    //展示hot页面
    String show_hot_page_1 = "show_hot_page_1";
    //点击hot页面的图片id
    String click_hot_img_ = "click_hot_img_";

    //搜索详情页
    //游戏详情页点击搜索框
    String gamedetail_click_searchbar = "gamedetail_click_searchbar";
    //游戏详情页点击分享功能
    String gamedetail_click_share = "gamedetail_click_share";
    //游戏详情页向下面滑动 展示出see more按钮
    String gamedetail_slide_down = "gamedetail_slide_down";
    //游戏详情页点击跳转到下一个详情页的按钮
    String gamedetail_click_nextbotton = "gamedetail_click_nextbotton";
    //游戏详情页展示
    String gamedetail_show = "gamedetail_show";
    //游戏详情页点击开始游戏
    String gamedetail_click_open = "gamedetail_click_open";
    //游戏详情页点击返回
    String gamedetail_click_back = "gamedetail_click_back";

}
