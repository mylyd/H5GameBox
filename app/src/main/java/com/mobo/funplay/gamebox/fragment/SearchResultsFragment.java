package com.mobo.funplay.gamebox.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.GameCategoryListAdapter;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.bean.SearchBean;
import com.mobo.funplay.gamebox.bean.SearchItem;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.ConfirmDialog;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.utils.SystemUtils;
import com.mobo.funplay.gamebox.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 游戏搜索结果页
 */
public class SearchResultsFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static String IS_USER_FIRST = "is_user_first";
    private RecyclerView recyclerView;
    private LinearLayout mLoading;
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayout mLoadFail;
    private GameCategoryListAdapter adapter;
    private boolean isUserFirst = true;
    private BroadcastReceiver mReceiver;
    private String search = "ball";
    private TextView tvRemind;
    private ConfirmDialog confirmDialog;
    private int position;
    private TextView mLoadFailContent;
    private TextView mLoadFailTitle;
    private ImageView mLoadFailImg;

    /**
     * @param isUser 是否是初次加载
     * @return
     */
    public static SearchResultsFragment newInstance(boolean isUser) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_USER_FIRST, isUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_results;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isUserFirst = bundle.getBoolean(IS_USER_FIRST);
        }
        boolean hasSd = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (hasSd) {
            initView();
            initData();
            registerBroadcastReceiver(new registerBroadcastReceiver());
        }
    }

    private class registerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            search = intent.getStringExtra(Constants.SEARCH_RESULTS_BROADCAST_VALUE);
            searchRequestData(search);
        }
    }

    private void initView() {
        tvRemind = findViewById(R.id.tv_remind);
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
        confirmDialog = new ConfirmDialog(getContext());
    }

    private void initData() {
        adapter = new GameCategoryListAdapter(getActivity(), Constants.GAME_SEARCH_RESULT);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        initClickListener();
    }

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

    private void searchRequestData(String searchStr) {
        setTextVisibility(false);
        adapter.getDataListClear();

        if (SystemUtils.isNetworkAvailable(getContext())) {
            mLoading.setVisibility(View.VISIBLE);
            mLoadFail.setVisibility(View.GONE);
            mLoadFailImg.setImageResource(R.drawable.load_failed_pic);
            mLoadFailTitle.setText(R.string.no_result);
            mLoadFailContent.setText(R.string.get_feed_failed);
            requestData(searchStr);
        } else {
            mLoading.setVisibility(View.GONE);
            mLoadFail.setVisibility(View.VISIBLE);
            mLoadFailTitle.setText(R.string.no_internet);
            mLoadFailContent.setText(R.string.get_internet_failed);
            mLoadFail.setVisibility(View.VISIBLE);
        }
    }

    private void requestData(String searchStr) {
        if (getContext() == null) {
            return;
        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", Utils.getPackageName(getContext()));
        queryParams.put("versionCode", Utils.getVersionCode(getContext()));
        queryParams.put("pageSize", Integer.toString(Constants.PAGE_MAX));
        queryParams.put("page", Integer.toString(1));
        queryParams.put("words", searchStr);
        queryParams.put("w_type", Constants.WALLPAPER_GAME);

        RetrofitManager.INSTANCE.getRequest().getSearch(queryParams).enqueue(new CommonCallback<SearchBean>() {
            @Override
            public void onResponse(SearchBean response) {
                if (response == null) {
                    return;
                }
                setRvListData(response.getData());
            }

            @Override
            public void onFailure(Throwable throwable, boolean b) {
                mSwipeLayout.setEnabled(true);
                mSwipeLayout.setRefreshing(false);
                setFailureData();
            }
        });
    }

    private void setRvListData(SearchItem data) {

        mLoading.setVisibility(View.GONE);
        mLoadFail.setVisibility(View.GONE);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(false);

        //此处data有可能位空，需要换成空集合，防止执行刷新逻辑
        if (data == null) {
            tvRemind.post(() -> setTextVisibility(true));
            return;
        }

        if (data.getIs_match() != 1) {
            tvRemind.post(() -> {
                setTextVisibility(true);
                setTextContent(data.getMsg());
            });
            return;
        }

        setTextVisibility(false);

        List<GameItemBean> gameCollect = SPManager.getInstance().getGameCollectList();
        if (gameCollect != null && gameCollect.size() != 0) {
            for (int i = 0; i < data.getWallpapers().size(); i++) {
                for (int j = 0; j < gameCollect.size(); j++) {
                    if (data.getWallpapers().get(i).getId() == gameCollect.get(j).getId()) {
                        data.getWallpapers().get(i).setCollect(true);
                    }
                }
            }
        }
        adapter.reset(data.getWallpapers());
    }

    private void setFailureData() {
        if (mLoading.getVisibility() == View.VISIBLE) {
            mLoadFail.setVisibility(View.VISIBLE);
        } else {
            mLoadFail.setVisibility(View.GONE);
        }
        mLoading.setVisibility(View.GONE);
    }

    private void setTextVisibility(boolean visibility) {
        tvRemind.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void setTextContent(String s) {
        if (tvRemind != null) {
            tvRemind.setText(s);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reload:
                mLoading.setVisibility(View.VISIBLE);
                mLoadFail.setVisibility(View.GONE);
                searchRequestData(search);
                break;
        }
    }


    /**
     * 注册订阅成功广播接收器
     * 所有加载广告的页面都需要注册[订阅成功广播接收器],订阅成功时隐藏原生广告和横幅广告，显示插页广告和视频广告时需要判断是否订阅
     *
     * @param receiver
     */
    private void registerBroadcastReceiver(BroadcastReceiver receiver) {
        mReceiver = receiver;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SEARCH_RESULTS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onRefresh() {
        searchRequestData(search);
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
