package com.mobo.funplay.gamebox.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.bean.BannerBean;
import com.mobo.funplay.gamebox.bean.BannerItem;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.utils.Utils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoaderInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 轮播图
 */
public class BannerFragment extends BaseFragment {
    private Banner mBannerView;
    //请求失败默认展示4个
    private static final int BANNER_MAX_NUM_FAIL = 4;
    private BannerBean mBean;

    public static BannerFragment newInstance() {
        return new BannerFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mBannerView = findViewById(R.id.banner_view);
        request();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_banner_layout;
    }

    private void startBanner(List<BannerItem> bannerItems) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity().isDestroyed()) {
                return;
            }
        }
        if (bannerItems.isEmpty()) {
            return;
        }
        //设置指示器位置（指示器居右）
        mBannerView.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载器
        mBannerView.setImageLoader(new ImageLoaderInterface() {
            @Override
            public void displayImage(Context context, Object path, View imageView) {
                BannerItem item = (BannerItem) path;
                ViewGroup viewGroup = (ViewGroup) imageView;
                ImageView img = viewGroup.findViewById(R.id.image_view);
                ImageView bannerIcon = viewGroup.findViewById(R.id.banner_icon);
                TextView bannerTitle = viewGroup.findViewById(R.id.banner_item_title);
                TextView bannerDesc = viewGroup.findViewById(R.id.banner_item_description);
                TextView bannerAction = viewGroup.findViewById(R.id.banner_action);
                viewGroup.findViewById(R.id.fl_banner).setOnClickListener(v -> {
                });
                Glide.with(imageView).load(item.banner == null ? R.drawable.item_banner_default : item.banner).
                        placeholder(R.drawable.item_banner_default).into(img);

                Glide.with(imageView).load(item.banner == null ? R.drawable.item_banner_default : item.banner).
                        placeholder(R.drawable.item_banner_default).into(bannerIcon);
                bannerTitle.setText(item.name);

                bannerAction.setOnClickListener(v -> {
                    GameItemBean gameItemBean = new GameItemBean(item.link, item.banner, "", item.id);
                    GamePreviewActivity.newStart(BannerFragment.this, gameItemBean, Constants.GAME_BANNER);
                });
                viewGroup.setOnClickListener(v -> {
                    GameItemBean gameItemBean = new GameItemBean(item.link, item.banner, "", item.id);
                    GamePreviewActivity.newStart(BannerFragment.this, gameItemBean, Constants.GAME_BANNER);
                });
            }

            @Override
            public View createImageView(Context context) {
                return LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            }
        });
        //设置图片集合
        mBannerView.setImages(bannerItems.subList(0, bannerItems.size()));
        //设置自动轮播，默认为true
        mBannerView.isAutoPlay(true);
        //设置轮播时间
        mBannerView.setDelayTime(5000);
        //banner设置方法全部调用完毕时最后调用
        mBannerView.start();
    }

    @SuppressLint("DefaultLocale")
    private void request() {
        HashMap<String, String> map = new HashMap<>();
        map.put("packageName", Utils.getPackageName(getContext()));
        map.put("versionCode", Utils.getVersionCode(getContext()));
        map.put("w_type", Constants.WALLPAPER_GAME);
        //map.put("pageSize", Integer.toString(BANNER_MAX_NUM));
        RetrofitManager.INSTANCE.getRequest().getBanner(map).enqueue(new CommonCallback<BannerBean>() {
            @Override
            public void onResponse(BannerBean response) {
                if (response == null) {
                    return;
                }
                mBean = response;
                startBanner(mBean.getData());
            }

            @Override
            public void onFailure(Throwable t, boolean isServerUnavailable) {
                List<BannerItem> items = SPManager.getInstance().getBanner();
                if (items == null || items.isEmpty()) {
                    //可以去加载本地预置图片
                    items = new ArrayList<>();
                    for (int i = 0; i < BANNER_MAX_NUM_FAIL; i++) {
                        items.add(new BannerItem());
                    }
                    SPManager.getInstance().setBanner(items);
                }
                mBean = new BannerBean();
                mBean.setData(items);
                startBanner(items);
            }
        });
    }


}
