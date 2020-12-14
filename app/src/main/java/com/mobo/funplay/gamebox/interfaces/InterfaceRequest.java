package com.mobo.funplay.gamebox.interfaces;


import com.google.gson.JsonElement;
import com.mobo.funplay.gamebox.bean.BannerBean;
import com.mobo.funplay.gamebox.bean.ConfigBean;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.bean.GrayItem;
import com.mobo.funplay.gamebox.bean.SearchBean;
import com.mobo.funplay.gamebox.bean.push.LocalPushConfig;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * @author : create by zq
 * @time : 19-4-30  15:47
 */
public interface InterfaceRequest {
    String HOST = "http://api.u-launcher.com/";
    // String HOST = "http://172.18.0.1:8888/";

    @GET("http://mobotoolpush.moboapps.io/ipo/api/gray/status")
    Call<GrayItem> getSwitchConfig(@QueryMap Map<String, String> params);

    @POST("client/v3/user/feedback.json")
    Call<JsonElement> postFeedBack(@Body Map<String, String> params);

    @GET("client/v3/wallpaper_reco/category_summary.json")
    Call<ListResponse<GameBean>> getCategoryGame(@QueryMap Map<String, String> params);

    @GET("client/v3/wallpaper_reco/hot_list.json")
    Call<ListResponse<GameItemBean>> getGameHome(@QueryMap Map<String, String> params);

    @GET("client/v3/wallpaper_reco/category_list.json")
    Call<ListResponse<GameItemBean>> getCategoryListGame(@QueryMap Map<String, String> params);

    @GET("/client/v3/wallpaper_reco/banner.json")
    Call<BannerBean> getBanner(@QueryMap Map<String, String> params);

    @GET("/client/v3/wallpaper_reco/search.json")
    Call<SearchBean> getSearch(@QueryMap Map<String, String> params);

    @GET("client/v3/push/push_regularly.json")
    Call<LocalPushConfig> getLocalPushConfigRequest(@QueryMap Map<String, String> params);

    //线上全局配置接口
    @GET("/client/v2/resource/config.json?")
    Call<ConfigBean> getConfigRequest(@QueryMap Map<String, String> params);
}