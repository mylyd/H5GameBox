package com.mobo.funplay.gamebox.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.GameHomeAdapter;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.ConfirmDialog;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.interfaces.ListCallback;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.utils.AssetsUtil;
import com.mobo.funplay.gamebox.utils.SystemUtils;
import com.mobo.funplay.gamebox.views.LoadMoreRecyclerView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 游戏主页
 */
public class GameFragment extends BaseFragment implements View.OnClickListener, LoadMoreRecyclerView.OnLoadMoreListener {
    private static final String TAG = "GameFragment";
    private SwipeRefreshLayout refreshLayout;
    private LoadMoreRecyclerView recyclerView;
    private LinearLayout mLoading, mLoadFail;
    private GameHomeAdapter adapter;
    private int mIndex = 0;
    /*第一次加载失败，然后加载了本地预置数据*/
    private boolean isLoadFromLocal = false;
    private ConfirmDialog confirmDialog;
    private int position;
    //刷新后数据到顶部
    private boolean upDataShuffle = false;
    private ImageView mLoadFailImg;
    private TextView mLoadFailTitle;
    private TextView mLoadFailContent;
    private View headView;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_game;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initClickListener();
    }

    private void initView() {
        initActionBar();
        getTitle().setBackgroundResource(R.drawable.ic_home_game_title);
        getGameIcon().setVisibility(View.VISIBLE);
        getSearch().setVisibility(View.VISIBLE);
        forGameHomeUrlListener(this, getGameIcon());
        forSearchIntent(this, getSearch());

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.refresh_bar_scheme);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        refreshLayout.setEnabled(false);
        recyclerView = findViewById(R.id.recycler_view);
        mLoading = findViewById(R.id.ll_loading);
        mLoadFail = findViewById(R.id.ll_fail);
        mLoadFailImg = findViewById(R.id.fail_img);
        mLoadFailTitle = findViewById(R.id.fail_title);
        mLoadFailContent = findViewById(R.id.fail_content);
        findViewById(R.id.tv_reload).setOnClickListener(this);

        //处理SwipeRefreshLayout + CoordinatorLayout + AppBarLayout 滑动冲突
        ((AppBarLayout) findViewById(R.id.appBarLayout))
                .addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
                    refreshLayout.setEnabled(verticalOffset >= 0);
                });

        confirmDialog = new ConfirmDialog(getActivity());
    }

    private void initData() {
        adapter = new GameHomeAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreListener(this);

        //加载轮播图
        if (GrayStatus.game_ads_value) {
            headView = View.inflate(getContext(), R.layout.item_recycler_banner, null);
            recyclerView.addHeaderView(headView);
        }

        initLocalGameList();

        if (SystemUtils.isNetworkAvailable(getContext())) {
            mLoading.setVisibility(View.VISIBLE);
            mLoadFail.setVisibility(View.GONE);
            mLoadFailImg.setImageResource(R.drawable.load_failed_pic);
            mLoadFailTitle.setText(R.string.no_result);
            mLoadFailContent.setText(R.string.get_feed_failed);
            requestData();
        } else {
            mLoading.setVisibility(View.GONE);
            mLoadFailImg.setImageResource(R.drawable.network_bg);
            mLoadFailTitle.setText(R.string.no_internet);
            mLoadFailContent.setText(R.string.get_internet_failed);
            mLoadFail.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initClickListener() {
        adapter.setOnItemClickListener((int position, GameItemBean item) -> {
            int likeNum = SPManager.getInstance().getInt(Constants.DATA_COLLECT_GAME_LIST_NUM, 0);
            if (likeNum >= 2) {
                adapter.setLike(position);
                return;
            }
            if (confirmDialog != null && !confirmDialog.isShowing()) {
                this.position = position;
                confirmDialog.show();
                confirmDialog.setTitleText(getString(R.string.collect_game));
                confirmDialog.setContextText(getString(R.string.collect_game_content));
                confirmDialog.setConfirmText(getString(R.string.collection));
            }
        });

        confirmDialog.setOnConfirmListener(() -> adapter.setLike(position));

    }

    private void requestData() {
        if (getContext() == null) {
            return;
        }

        if (!GrayStatus.game_ads_value) {
            //替换成无广告游戏
            setRvListData(AssetsUtil.getGameHomeNoAds(getContext()));
            mIndex = 0;
            recyclerView.setHasLoadAll(true);
            return;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", getContext().getPackageName());
        queryParams.put("versionCode", String.valueOf(SystemUtils.getVersionCode(getContext())));
        queryParams.put("pageSize", Integer.toString(Constants.PAGE_SIZE));
        /*接口page参数是从1开始，app中全局保持统一从0开始，此处需要+1*/
        queryParams.put("page", Integer.toString(mIndex + 1));
        queryParams.put("w_type", Constants.WALLPAPER_GAME);

        RetrofitManager.INSTANCE.getRequest().getGameHome(queryParams).enqueue(new ListCallback<GameItemBean>() {
            @Override
            public void onResponse(List<GameItemBean> response) {
                if (response == null) {
                    return;
                }
                if (mIndex == 0) {
                    SPManager.getInstance().putGameWebViewList(response);
                }
                //请求成功时保存缓存到本地
                saveDataToLocal(response);
                setRvListData(response);
            }

            @Override
            public void onFailure(Throwable throwable, boolean isServerUnavailable) {
                setFailureData(isServerUnavailable);
            }
        });
    }

    private void setRvListData(List<GameItemBean> data) {
        //此处data有可能位空，需要换成空集合，防止执行刷新逻辑
        if (data == null) {
            data = Collections.emptyList();
        }
        //匹配是否已收藏
        List<GameItemBean> gameCollect = SPManager.getInstance().getGameCollectList();
        if (gameCollect != null && gameCollect.size() != 0) {
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < gameCollect.size(); j++) {
                    if (data.get(i).getId() == gameCollect.get(j).getId()) {
                        data.get(i).setCollect(true);
                    }
                }
            }
        }

        if (mIndex == 0) {
            mLoading.setVisibility(View.GONE);
            mLoadFail.setVisibility(View.GONE);
            refreshLayout.setEnabled(true);
            refreshLayout.setRefreshing(false);
            recyclerView.setAutoLoadMoreEnable(true);
            adapter.reset(data);
        } else {
            if (upDataShuffle) {
                upDataShuffle = false;
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(false);
                recyclerView.setAutoLoadMoreEnable(true);
                adapter.addTop(data);
                recyclerView.notifyDataSetChanged();
            } else {
                adapter.addAll(data);
            }
        }

        if (data.size() == 0) {
            recyclerView.setHasLoadAll(true);
        }

        recyclerView.loadMoreComplete(data.size(), Constants.PAGE_SIZE);

        if (data.size() > 0) {
            mIndex++;
            track(MyTracker.show_game_page_, mIndex);
        }
    }

    private void setFailureData(boolean isServerUnavailable) {
        if (mIndex == 0) {
            List<GameItemBean> data = null;
            if (isServerUnavailable) {
                data = loadDataFromLocal(mIndex);
            }
            if (CollectionUtils.isEmpty(data)) {
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(false);
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoadFail.setVisibility(View.VISIBLE);
                } else {
                    mLoadFail.setVisibility(View.GONE);
                }
                mLoading.setVisibility(View.GONE);
            } else {
                isLoadFromLocal = true;
                setRvListData(data);
            }
        } else {
            recyclerView.setLoadError(true);
        }
    }

    /**
     * 加载本地数据
     *
     * @param pageIndex
     * @return
     */
    private List<GameItemBean> loadDataFromLocal(int pageIndex) {
        List<GameItemBean> data = SPManager.getInstance().getGameHomeList();
        int fromIndex = Constants.PAGE_SIZE * pageIndex;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }
        int toIndex = fromIndex + Constants.PAGE_SIZE;
        if (toIndex > data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }

    /**
     * 初始化本地壁纸数据集
     */
    private void initLocalGameList() {
        //只初始化一次预置数据
        List<GameItemBean> data = SPManager.getInstance().getGameHomeList();
        if (!CollectionUtils.isEmpty(data)) {
            return;
        }

        List<GameItemBean> wallpaperList = AssetsUtil.getGameHome(getContext());
        if (CollectionUtils.isEmpty(wallpaperList)) {
            return;
        }

        SPManager.getInstance().putGameHomeList(wallpaperList);
    }

    /**
     * 请求成功时保存缓存到本地
     *
     * @param data
     */
    private void saveDataToLocal(List<GameItemBean> data) {
        // 将新增数据添加到sp中, 第一次加载失败后分页加载只加载本地数据
        List<GameItemBean> local = SPManager.getInstance().getGameHomeList();
        if (CollectionUtils.isEmpty(local) || CollectionUtils.isEmpty(data)) return;
        for (GameItemBean item : data) {
            int index = local.indexOf(item);
            if (index > -1) {
                local.set(index, item);
            } else {
                local.add(item);
            }
        }
        SPManager.getInstance().putGameHomeList(local);
    }

    private void onRefresh() {
        //刷新逻辑
        //mIndex = 0;
        upDataShuffle = true;
        isLoadFromLocal = false;
        recyclerView.setHasLoadAll(false);
        requestData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reload:
                mLoading.setVisibility(View.VISIBLE);
                mLoadFail.setVisibility(View.GONE);
                requestData();
                break;
        }
    }

    @Override
    public void onLoadMore() {
        if (isLoadFromLocal) {
            List<GameItemBean> data = loadDataFromLocal(mIndex);
            setRvListData(data);
        } else {
            requestData();
        }
    }

}
