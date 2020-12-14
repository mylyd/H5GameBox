package com.mobo.funplay.gamebox.activity;


import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.GameCollectionAdapter;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.tracker.MyTracker;

import java.util.List;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 我的游戏收藏
 */
public class MyCollectionActivity extends BaseActivity {

    private LinearLayout layoutEmpty;
    private RecyclerView recyclerView;
    private GameCollectionAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void onCreate() {
        track(MyTracker.click_mycollection);
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.my_collection);
        layoutEmpty = findViewById(R.id.layout_empty);
        recyclerView = findViewById(R.id.recycle_collection);
        adapter = new GameCollectionAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.tv_confirm).setOnClickListener(view -> {
            Constants.INTENT_COLLECTION = true;
            finish();
        });
    }

    @Override
    protected void initData() {
        List<GameItemBean> beans = SPManager.getInstance().getGameCollectList();
        if (beans != null && beans.size() != 0) {
            adapter.reset(beans);
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            track(MyTracker.show_collection_page_, 1);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public static void newStart(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), MyCollectionActivity.class);
        fragment.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
