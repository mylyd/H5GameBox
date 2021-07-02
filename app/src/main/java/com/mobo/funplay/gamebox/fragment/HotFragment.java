package com.mobo.funplay.gamebox.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.adapter.HotListAdapter;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.utils.AssetsUtil;

import java.util.List;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description Hot界面
 */
public class HotFragment extends BaseFragment {
    private RecyclerView mRecycleView;
    private HotListAdapter mAdapter;

    public static HotFragment newInstance() {
        return new HotFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        initActionBar();
        getTitle().setText(R.string.hot);
        getSearch().setVisibility(View.VISIBLE);
        forSearchIntent(this, getSearch());

        mRecycleView = findViewById(R.id.recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HotListAdapter();
        mRecycleView.setAdapter(mAdapter);

        loadHotData();

        mAdapter.update(SPManager.getInstance().getGameHotFragmentList());
        mAdapter.setOnItemClickListener((position, item) ->
                GamePreviewActivity.newStart(this, item, Constants.GAME_HOT));
    }

    private void loadHotData() {
        List<GameItemBean> hotBean = SPManager.getInstance().getGameHotFragmentList();
        if (hotBean != null && !hotBean.isEmpty()) {
            return;
        }
        hotBean = AssetsUtil.getGameHotFragment(getContext());
        if (hotBean == null || hotBean.isEmpty()) {
            return;
        }
        SPManager.getInstance().putGameHotFragmentList(hotBean);
    }
}
