package com.mobo.funplay.gamebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.SearchActivity;
import com.mobo.funplay.gamebox.adapter.SearchLabelAdapter;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.ConfirmDialog;
import com.mobo.funplay.gamebox.manager.FlowLayoutManager;
import com.mobo.funplay.gamebox.manager.SPManager;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 游戏搜索标签
 */
public class SearchLabelFragment extends BaseFragment {
    private RecyclerView recycleHistory, recycleSearch, recycleLocal;
    private String[] search = {"2048", "draw", "ball", "basketball", "love",
            "snake", "jump", "online", "zombie", "escape",
            "fly", "airplane", "war"};
    private ArrayList<String> localList = new ArrayList<>();
    private ImageView mItemDelete;
    private FrameLayout mLayoutHistory;
    private ConfirmDialog confirmDialog;
    private SearchLabelAdapter localAdapter, historyAdapter;

    public static SearchLabelFragment newInstance() {
        return new SearchLabelFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_history;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean hasSd = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (hasSd) {
            initView();
            initData();
            setClickListener();
        }
    }

    private void initView() {
        if (!localList.isEmpty()) {
            localList.clear();
        }
        localList = new ArrayList<>(Arrays.asList(search));
        //用户搜索 词条
        recycleHistory = findViewById(R.id.recycle_history);
        //本地预置 词条
        recycleLocal = findViewById(R.id.recycle_history_local);
        confirmDialog = new ConfirmDialog(getContext());

        mLayoutHistory = findViewById(R.id.layout_delete);
        mItemDelete = findViewById(R.id.item_delete);
        mItemDelete.setOnClickListener(view -> {
            if (confirmDialog != null && !confirmDialog.isShowing()) {
                confirmDialog.show();
            }
        });

        localAdapter = new SearchLabelAdapter(getContext(), true);
        historyAdapter = new SearchLabelAdapter(getContext(), false);

    }

    private void initData() {
        recycleLocal.setLayoutManager(new FlowLayoutManager(getContext(), true));
        recycleLocal.setAdapter(localAdapter);
        recycleHistory.setLayoutManager(new FlowLayoutManager(getContext(), false));
        recycleHistory.setAdapter(historyAdapter);

        //本地词条
        localAdapter.update(localList, false);

        //搜索词条
        ArrayList<String> historyBean = SPManager.getInstance().getLabelList();
        if (historyBean == null) {
            mLayoutHistory.setVisibility(View.GONE);
        } else {
            historyAdapter.update(historyBean, false);
        }
    }

    private void setClickListener() {
        confirmDialog.setOnConfirmListener(() -> {
            if (mLayoutHistory.getVisibility() == View.VISIBLE) {
                mLayoutHistory.setVisibility(View.GONE);
            }
            SPManager.getInstance().putLabelList(null);
            historyAdapter.deleteAll();
        });

        //预置标签
        localAdapter.setOnItemClickListener(new SearchLabelAdapter.OnViewItemClick() {
            @Override
            public void onItemViewOnClick(int position, String item) {
                ((SearchActivity) getActivity()).setEditText(item);
                //进行搜索请求
                registerIntent(item);
                ((SearchActivity) getActivity()).setCurrentItem(1);
            }

            @Override
            public void onItemCloseOnClick(int position, ArrayList<String> item) {

            }
        });

        //历史标签
        historyAdapter.setOnItemClickListener(new SearchLabelAdapter.OnViewItemClick() {
            @Override
            public void onItemViewOnClick(int position, String item) {
                ((SearchActivity) getActivity()).setEditText(item);
                //进行搜索请求
                registerIntent(item);
                ((SearchActivity) getActivity()).setCurrentItem(1);
            }

            @Override
            public void onItemCloseOnClick(int position, ArrayList<String> item) {
                if (item.size() == 0 && mLayoutHistory.getVisibility() == View.VISIBLE) {
                    mLayoutHistory.setVisibility(View.GONE);
                }
            }
        });
    }

    private void registerIntent(String str) {
        Intent intent = new Intent(Constants.SEARCH_RESULTS_BROADCAST_ACTION);
        intent.putExtra(Constants.SEARCH_RESULTS_BROADCAST_VALUE, str);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && SPManager.getInstance().getLabelList() != null && mLayoutHistory != null) {
            mLayoutHistory.setVisibility(View.VISIBLE);
            historyAdapter.update(SPManager.getInstance().getLabelList(), false);
        }
    }
}
