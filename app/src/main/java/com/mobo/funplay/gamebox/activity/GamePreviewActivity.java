package com.mobo.funplay.gamebox.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.WebViewSearchDialog;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.manager.WebViewSeeMoreManager;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.utils.ShareUtil;


/**
 * @Description: H5游戏详情页
 * @Author: ydli
 * @CreateDate: 19-12-06 下午3:00
 */
public class GamePreviewActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "GameActivity";
    public static final String GAME_KEY = "GAME_KEY";
    public static final String TYPE = "type";
    private WebView webView;
    private Intent intent;
    private LinearLayout mLoadFail;
    private FrameLayout layoutLoading;
    private GameItemBean bean;
    //当前网页url
    private String game_url = "";
    private ImageView imgWebViewBack;
    private ImageView imgWebViewGoAlong;
    private WebViewSearchDialog webViewSearchDialog;
    private boolean isFirstHome = true;
    private LinearLayout webBottomBrowser;
    private TextView seeMore;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_preview;
    }

    @Override
    protected void onCreate() {
        setStatusTextColor(this, true);
    }

    @Override
    protected void initView() {
        Glide.with(this).load(R.drawable.gif_loading).into((ImageView) findViewById(R.id.iv_load_game));
        webView = findViewById(R.id.web_view);
        mLoadFail = findViewById(R.id.ll_fail);
        layoutLoading = findViewById(R.id.layout_loading);
        findViewById(R.id.tv_reload).setOnClickListener(this);
        webView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        webView.setBackgroundResource(R.color.colorPrimary);

        webBottomBrowser = findViewById(R.id.web_bottom_item);
        findViewById(R.id.web_back).setOnClickListener(this);
        findViewById(R.id.web_go_along).setOnClickListener(this);
        findViewById(R.id.web_search).setOnClickListener(this);
        findViewById(R.id.web_home).setOnClickListener(this);
        findViewById(R.id.web_refresh).setOnClickListener(this);
        imgWebViewBack = findViewById(R.id.web_img_back);
        imgWebViewGoAlong = findViewById(R.id.web_img_go_along);
        seeMore = findViewById(R.id.see_more);
        seeMore.setOnClickListener(this);

        webViewSearchDialog = new WebViewSearchDialog(this);


        intent = getIntent();
        bean = intent.getParcelableExtra(GAME_KEY);
        if (bean == null) {
            return;
        }
        String type = intent.getStringExtra(TYPE);
        if (TextUtils.isEmpty(type)) {
            return;
        }
        trackTag(type, bean.getId());
    }

    @Override
    protected void initData() {
        initWebView();
        setWebView();
    }

    @SuppressLint("NewApi")
    private void setWebView() {
        webView.loadUrl(bean.getLink());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //开始加载时回调
                if (layoutLoading.getVisibility() == View.GONE) {
                    layoutLoading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                game_url = url;
                Log.d(TAG, "onPageCommitVisible: " + url);

                webToBackIcon(webView.canGoBack(), webView.canGoForward());
                track(MyTracker.gamedetail_show);
                //加载成功时回调
                if (layoutLoading.getVisibility() == View.VISIBLE) {
                    layoutLoading.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    webBottomBrowser.setVisibility(View.VISIBLE);
                }
                if (seeMore.getVisibility() == View.VISIBLE) {
                    seeMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (layoutLoading.getVisibility() == View.VISIBLE && mLoadFail.getVisibility() == View.GONE) {
                    mLoadFail.setVisibility(View.VISIBLE);
                    layoutLoading.setVisibility(View.GONE);
                }
            }
        });

        //TODO 搜索框返回经过筛选的uri进行请求
        webViewSearchDialog.setOnSearchClickListener(uri -> {
            if (webView != null) {
                webView.loadUrl(uri);
            }
        });

        webView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //WebView的总高度
            float webViewContentHeight = webView.getContentHeight() * webView.getScale();
            //WebView的现高度
            float webViewCurrentHeight = (webView.getHeight() + webView.getScrollY());

            if ((webViewContentHeight - webViewCurrentHeight) < 5.0 && seeMore.getVisibility() == View.GONE) {
                //已经处于底端
                if (game_url.contains(Constants.game_url_woso) || game_url.contains(Constants.game_url_yingxian)) {
                    if (!game_url.equals(Constants.game_url_woso_home) && !game_url.equals(Constants.game_url_yingxian_home)) {
                        //当处于游戏主页时，直接返回大到应用主页
                        track(MyTracker.gamedetail_slide_down);
                        seeMore.setVisibility(View.VISIBLE);
                    }
                }
            } else if (webViewContentHeight - webViewCurrentHeight > 60 && seeMore.getVisibility() == View.VISIBLE) {
                seeMore.setVisibility(View.GONE);
            }
        });
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        //打开js支持
        webSettings.setJavaScriptEnabled(true);
        //加入辅助处理js，避免一些js的函数会失效，如alert()方法
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //TODO 处理facebook分享
                facebookShare(game_url);
                result.cancel();
                return true;
            }
        });
        //加入缓存清除机制，避免H5页面变动，不能及时在app端显示出来
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //自适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    private void facebookShare(String uri) {
        track(MyTracker.gamedetail_click_share);
        ShareUtil.share(this, getString(R.string.share_content));
    }

    private void trackTag(String type, int id) {
        if (type.equals(Constants.GAME_BANNER)) {
            track(MyTracker.click_banner_, id);
        } else if (type.equals(Constants.GAME_COLLECTION)) {
            track(MyTracker.click_collection_img_, id);
            track(MyTracker.click_collection_img);
        } else if (type.equals(Constants.GAME_CATE_DETAIL)) {
            track(MyTracker.click_catedetail_img_, id);
            track(MyTracker.click_catedetail_img);
        } else if (type.equals(Constants.GAME_SEARCH_RESULT)) {
            track(MyTracker.click_searchresult_img_, id);
            track(MyTracker.click_searchresult_img);
        } else if (type.equals(Constants.GAME_CATEGORY)) {
            track(MyTracker.click_category_img_, id);
            track(MyTracker.click_category_img);
        } else if (type.equals(Constants.GAME_HOME)) {
            track(MyTracker.click_game_img_, id);
            track(MyTracker.click_game_img);
        } else if (type.equals(Constants.GAME_ICON)) {

        } else if (type.equals(Constants.GAME_HOT)) {

        } else if (type.equals(Constants.GAME_PUSH)) {

        }
    }

    /**
     * 切换前进后退按钮图标
     *
     * @param back
     * @param goAlong
     */
    private void webToBackIcon(boolean back, boolean goAlong) {
        imgWebViewBack.setImageResource(back ? R.drawable.web_view_back : R.drawable.web_view_back_no);
        imgWebViewGoAlong.setImageResource(goAlong ? R.drawable.web_view_go_along : R.drawable.web_view_go_along_no);
    }

    /**
     * @param fragment 上下文
     * @param bean
     * @param type
     */
    public static void newStart(Fragment fragment, GameItemBean bean, String type) {
        Intent intent = new Intent(fragment.getContext(), GamePreviewActivity.class);
        intent.putExtra(GAME_KEY, bean);
        intent.putExtra(TYPE, type);
        fragment.startActivity(intent);
    }

    /**
     * @param activity
     * @param bean
     */
    public static void newStart(Activity activity, GameItemBean bean, String type) {
        Intent intent = new Intent(activity, GamePreviewActivity.class);
        intent.putExtra(GAME_KEY, bean);
        intent.putExtra(TYPE, type);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reload:
                layoutLoading.setVisibility(View.VISIBLE);
                mLoadFail.setVisibility(View.GONE);
                webView.loadUrl(bean.getLink());
                break;
            case R.id.web_back:
                //TODO 回退
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case R.id.web_go_along:
                //TODO 前进
                if (webView != null && webView.canGoForward()) {
                    webView.goForward();
                }
                break;
            case R.id.web_search:
                //TODO 打开搜索
                if (webViewSearchDialog != null && !webViewSearchDialog.isShowing()) {
                    webViewSearchDialog.show();
                    track(MyTracker.gamedetail_click_searchbar);
                }
                break;
            case R.id.web_home:
                //TODO 主页
                if (game_url.contains(Constants.game_url_woso)) {
                    webView.loadUrl(Constants.game_url_woso_home);
                } else if (game_url.contains(Constants.game_url_yingxian)) {
                    webView.loadUrl(Constants.game_url_yingxian_home);
                }
                break;
            case R.id.web_refresh:
                //TODO 刷新
                if (webView != null) {
                    webView.reload();
                }
                break;
            case R.id.see_more:
                //TODO see more
                GameItemBean bean = WebViewSeeMoreManager.getWebSeeViewFilter(game_url);
                if (bean == null || webView == null) {
                    return;
                }
                webView.loadUrl(bean.getLink());
                track(MyTracker.gamedetail_click_nextbotton);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!GrayStatus.game_ads_value) {
            //替换成无广告游戏
            super.onBackPressed();
            return;
        }
        if (webView == null) {
            super.onBackPressed();
        }
        //TODO 返回逻辑
        // 1.判断是否处于游戏界面，如果不处于直接返回到应用主页；
        // 2.如果处于游戏界面，判断时候在游戏主页，如果处于游戏主页则直接返回到应用主页
        // 3.如果不是处于游戏主页，则先返回到对应的游戏主页，再次点击则返回到应用主页（如果点击了回退键第二次处于这种情况时，则直接返回到应用主页）

        if (game_url.contains(Constants.game_url_woso) || game_url.contains(Constants.game_url_yingxian)) {
            if (game_url.equals(Constants.game_url_woso_home) || game_url.equals(Constants.game_url_yingxian_home)) {
                //当处于游戏主页时，直接返回大到应用主页
                track(MyTracker.gamedetail_click_back);
                super.onBackPressed();
            } else if (isFirstHome) {
                //如果不处于主页，第一次点击返回按钮返回到主页，如果点击了回退返回了，在点击返回按钮时，直接返回到应用界面
                if (game_url.contains(Constants.game_url_woso)) {
                    webView.loadUrl(Constants.game_url_woso_home);
                } else if (game_url.contains(Constants.game_url_yingxian)) {
                    webView.loadUrl(Constants.game_url_yingxian_home);
                }
                isFirstHome = false;
            } else {
                track(MyTracker.gamedetail_click_back);
                super.onBackPressed();
            }
        } else {
            //当链接处于搜索内容且不是游戏内容时，直接返回到应用页面
            track(MyTracker.gamedetail_click_back);
            super.onBackPressed();
        }
    }
}
