package com.mobo.funplay.gamebox.activity;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.GameCategoryListAdapter;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.ConfirmDialog;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.interfaces.ListCallback;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.utils.SystemUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameCategoryListActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private GameBean gameBean;
    private List<GameItemBean> gameItemBean;
    private GameCategoryListAdapter listAdapter;
    private RecyclerView recyclerView;
    private LinearLayout mLoading;
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayout mLoadFail;
    private boolean isPreview;
    private int position;
    private ConfirmDialog confirmDialog;
    private ImageView mLoadFailImg;
    private TextView mLoadFailTitle;
    private TextView mLoadFailContent;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_category_list;
    }

    @Override
    protected void onCreate() {
        gameBean = getIntent().getParcelableExtra(Constants.CATEGORY_GAME_LIST_BEAN);
        isPreview = getIntent().getBooleanExtra(Constants.CATEGORY_GAME_LIST_IS_PREVIEW, false);
        position = getIntent().getIntExtra(Constants.CATEGORY_GAME_LIST_POSITION, 0);
        if (gameBean == null) {
            return;
        }
        gameItemBean = gameBean.getItems();

        if (gameItemBean == null) {
            return;
        }
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mSwipeLayout = findViewById(R.id.refresh_layout);
        mSwipeLayout.setColorSchemeResources(R.color.refresh_bar_scheme);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setEnabled(false);
        recyclerView = findViewById(R.id.recycle_category_list);
        mLoading = findViewById(R.id.ll_loading);
        mLoadFail = findViewById(R.id.ll_fail);
        mLoadFailImg = findViewById(R.id.fail_img);
        mLoadFailTitle = findViewById(R.id.fail_title);
        mLoadFailContent = findViewById(R.id.fail_content);
        findViewById(R.id.tv_reload).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(gameBean.getCate_name());
        confirmDialog = new ConfirmDialog(this);
    }

    @Override
    protected void initData() {
        if (isPreview) {
            GamePreviewActivity.newStart(this, gameItemBean.get(position), Constants.GAME_CATEGORY);
        }
        listAdapter = new GameCategoryListAdapter(this, Constants.GAME_CATE_DETAIL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        initClickListener();

        if (SystemUtils.isNetworkAvailable(this)) {
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

    private void initClickListener() {
        listAdapter.setOnItemClickListener((int position, GameItemBean item) -> {
            int likeNum = SPManager.getInstance().getInt(Constants.DATA_COLLECT_GAME_LIST_NUM, 0);
            if (likeNum >= 2) {
                listAdapter.setLike(position);
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

        confirmDialog.setOnConfirmListener(() -> listAdapter.setLike(position));
    }

    private void requestData() {
        if (!GrayStatus.game_ads_value) {
            //替换成无广告游戏
            setRvListData(gameItemBean);
            return;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", getPackageName());
        queryParams.put("versionCode", String.valueOf(SystemUtils.getVersionCode(this)));
        queryParams.put("pageSize", Integer.toString(100));
        queryParams.put("w_type", Constants.WALLPAPER_GAME);
        queryParams.put("cate_id", Integer.toString(gameBean.getCate_id()));
        queryParams.put("sort", Constants.SORT_TYPE_HEAT);

        RetrofitManager.INSTANCE.getRequest().getCategoryListGame(queryParams).enqueue(new ListCallback<GameItemBean>() {

            @Override
            public void onResponse(List<GameItemBean> response) {
                if (response.size() == 0) {
                    setRvListData(gameItemBean);
                    return;
                }
                setRvListData(response);
            }

            @Override
            public void onFailure(Throwable t, boolean isServerUnavailable) {
                mSwipeLayout.setEnabled(true);
                mSwipeLayout.setRefreshing(false);
                setRvListData(gameItemBean);
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoadFail.setVisibility(View.VISIBLE);
                } else {
                    mLoadFail.setVisibility(View.GONE);
                }
                mLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setRvListData(List<GameItemBean> data) {
        //此处data有可能位空，需要换成空集合，防止执行刷新逻辑
        if (data == null) {
            data = Collections.emptyList();
        }

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

        mLoading.setVisibility(View.GONE);
        mLoadFail.setVisibility(View.GONE);
        mSwipeLayout.setEnabled(true);
        mSwipeLayout.setRefreshing(false);
        listAdapter.reset(data);
        track(MyTracker.show_catedetail_page_, 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_reload:
                mLoading.setVisibility(View.VISIBLE);
                mLoadFail.setVisibility(View.GONE);
                requestData();
                break;
        }
    }

    /**
     * @param fragment      上下文
     * @param gameBean
     * @param isGamePreview 时候是直接显示游戏详情页面
     * @param position
     */
    public static void newStart(Fragment fragment, GameBean gameBean, boolean isGamePreview, int position) {
        Intent intent = new Intent(fragment.getContext(), GameCategoryListActivity.class);
        intent.putExtra(Constants.CATEGORY_GAME_LIST_BEAN, gameBean);
        intent.putExtra(Constants.CATEGORY_GAME_LIST_IS_PREVIEW, isGamePreview);
        intent.putExtra(Constants.CATEGORY_GAME_LIST_POSITION, position);
        fragment.startActivity(intent);
    }

    /**
     * @param activity
     * @param gameBean
     * @param isGamePreview 时候是直接显示游戏详情页面
     * @param position
     */
    public static void newStart(Activity activity, GameBean gameBean, boolean isGamePreview, int position) {
        Intent intent = new Intent(activity, GameCategoryListActivity.class);
        intent.putExtra(Constants.CATEGORY_GAME_LIST_BEAN, gameBean);
        intent.putExtra(Constants.CATEGORY_GAME_LIST_IS_PREVIEW, isGamePreview);
        intent.putExtra(Constants.CATEGORY_GAME_LIST_POSITION, position);
        activity.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        requestData();
    }
}
