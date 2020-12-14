package com.mobo.funplay.gamebox.manager;

import com.mobo.funplay.gamebox.bean.GameItemBean;

import java.util.List;
import java.util.Random;

/**
 * @author : ydli
 * @time : 20-7-29 下午12:03
 * @description WebView界面下滑加载更多帮助类
 */
public class WebViewSeeMoreManager {

    /**
     * 取出需要切换的数据，同时做内容非空判断
     *
     * @return
     */
    public static List<GameItemBean> getSeeMoreData() {
        List<GameItemBean> bean = SPManager.getInstance().getGameWebViewList();
        if (bean.isEmpty()) {
            return null;
        }
        return bean;
    }

    /**
     * 获取需要取值的随机数
     *
     * @param length
     * @return
     */
    public static int getRandomIndex(int length) {
        return new Random().nextInt(length);
    }

    /**
     * 筛选出数据
     *
     * @return
     */
    public static GameItemBean getSeeMoreNext() {
        List<GameItemBean> bean = getSeeMoreData();
        if (bean == null) {
            return null;
        }
        return bean.get(getRandomIndex(bean.size()));
    }

    /**
     * 筛选出重复数据
     *
     * @return
     */
    public static GameItemBean getWebSeeViewFilter(String url) {
        GameItemBean itemBean = getSeeMoreNext();
        if (itemBean == null) {
            return null;
        }
        if (url.equals(itemBean.getLink())) {
            itemBean = getSeeMoreNext();
        }
        return itemBean;
    }

}
