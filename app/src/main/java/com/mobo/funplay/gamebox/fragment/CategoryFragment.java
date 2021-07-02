package com.mobo.funplay.gamebox.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.GameCategoryAdapter;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.interfaces.ListCallback;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.utils.AssetsUtil;
import com.mobo.funplay.gamebox.utils.SystemUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 游戏分类页面
 */
public class CategoryFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private GameCategoryAdapter gameAdapter;
    private LinearLayout mLoading;
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayout mLoadFail;
    private boolean isLoadFromLocal;
    private ImageView mLoadFailImg;
    private TextView mLoadFailTitle;
    private TextView mLoadFailContent;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_categroy;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean hasSd = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (hasSd) {
            initView();
            initData();
        }
    }

    private void initView() {
        initActionBar();
        getTitle().setText(R.string.category);
        getGameIcon().setVisibility(View.VISIBLE);
        forGameHomeUrlListener(this, getGameIcon());

        recyclerView = findViewById(R.id.rv_game);
        mSwipeLayout = findViewById(R.id.refresh_layout);
        mSwipeLayout.setColorSchemeResources(R.color.refresh_bar_scheme);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setEnabled(false);
        mLoading = findViewById(R.id.ll_loading);
        mLoadFail = findViewById(R.id.ll_fail);
        mLoadFailImg = findViewById(R.id.fail_img);
        mLoadFailTitle = findViewById(R.id.fail_title);
        mLoadFailContent = findViewById(R.id.fail_content);

        findViewById(R.id.tv_reload).setOnClickListener(this);
    }

    private void initData() {
        gameAdapter = new GameCategoryAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gameAdapter);

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
            mLoadFailTitle.setText(R.string.no_internet);
            mLoadFailContent.setText(R.string.get_internet_failed);
            mLoadFail.setVisibility(View.VISIBLE);
            mLoadFail.setVisibility(View.VISIBLE);
        }
    }

    private void requestData() {
        if (getContext() == null) {
            return;
        }

        if (!GrayStatus.game_ads_value) {
            //替换成无广告游戏
            setRvListData(AssetsUtil.getGameCategoryNoAds(getContext()));
            return;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", getContext().getPackageName());
        queryParams.put("versionCode", String.valueOf(SystemUtils.getVersionCode(getContext())));
        queryParams.put("pageSize", Integer.toString(Constants.PAGE_SIZE));
        queryParams.put("limit", Integer.toString(Constants.GAME_MAX_ITEM_COUNT));
        queryParams.put("w_type", Constants.WALLPAPER_GAME);

        RetrofitManager.INSTANCE.getRequest().getCategoryGame(queryParams).enqueue(new ListCallback<GameBean>() {
            @Override
            public void onResponse(List<GameBean> response) {
                if (response == null) {
                    return;
                }
                //请求成功时保存缓存到本地
                saveDataToLocal(response);
                setRvListData(response);

            }

            @Override
            public void onFailure(Throwable throwable, boolean b) {
                setFailureData();
            }
        });
    }

    private void setRvListData(List<GameBean> data) {
        //此处data有可能位空，需要换成空集合，防止执行刷新逻辑
        if (data == null) {
            data = Collections.emptyList();
        }

        mLoading.setVisibility(View.GONE);
        mLoadFail.setVisibility(View.GONE);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(false);
        gameAdapter.reset(data);
    }

    private void setFailureData() {
        List<GameBean> data = SPManager.getInstance().getGameCategoryList();
        if (data == null || data.isEmpty()) {
            mSwipeLayout.setEnabled(true);
            mSwipeLayout.setRefreshing(false);
            if (mLoading.getVisibility() == View.VISIBLE) {
                mLoadFail.setVisibility(View.VISIBLE);
            } else {
                mLoadFail.setVisibility(View.GONE);
            }
            mLoading.setVisibility(View.GONE);
        } else {
            setRvListData(data);
        }
    }

    /**
     * 初始化本地壁纸数据集
     */
    private void initLocalGameList() {
        //只初始化一次预置数据
        List<GameBean> data = SPManager.getInstance().getGameCategoryList();
        if (data != null && !data.isEmpty()) {
            return;
        }

        List<GameBean> gameList = AssetsUtil.getGameCategory(getContext());
        if (gameList == null || gameList.isEmpty()) {
            return;
        }

        SPManager.getInstance().putGameCategoryList(gameList);
    }

    /**
     * 请求成功时保存缓存到本地
     *
     * @param data
     */
    private void saveDataToLocal(List<GameBean> data) {
        // 将新增数据添加到sp中, 第一次加载失败后分页加载只加载本地数据
        List<GameBean> local = SPManager.getInstance().getGameCategoryList();
        if (local == null || local.isEmpty() || data == null || data.isEmpty()) return;
        for (GameBean item : data) {
            int index = local.indexOf(item);
            if (index > -1) {
                local.set(index, item);
            } else {
                local.add(item);
            }
        }
        SPManager.getInstance().putGameCategoryList(local);
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
    public void onRefresh() {
        requestData();
    }
}
