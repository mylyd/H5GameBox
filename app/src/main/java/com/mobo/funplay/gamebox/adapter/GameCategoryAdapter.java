package com.mobo.funplay.gamebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GameCategoryListActivity;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.utils.SystemUtils;
import com.mobo.funplay.gamebox.views.CategoryItemDecoration;

/**
 * @Description: H5游戏分类数据适配器
 * @Author: ydli
 * @CreateDate: 19-12-06 下午3:00
 */
public class GameCategoryAdapter extends BaseNativeAdAdapter<GameBean> {
    private Fragment mFragment;
    private int mMargin;
    private boolean isVipUser;

    public GameCategoryAdapter(Fragment fragment) {
        super();
        mFragment = fragment;
        mMargin = SystemUtils.dip2px(mFragment, 8);
    }

    @Override
    public ItemViewHolder onCreateNativeAdViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ad, parent, false);
        return new NativeAdViewHolder(view);
    }

    @Override
    public int getNativeAdInterval() {
        return 4;
    }

    @Override
    public ItemViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_category, parent, false);
        return new WallpaperViewHolder(view);
    }

    public class WallpaperViewHolder extends ItemViewHolder {
        private TextView tvType;
        private TextView tvName;
        private TextView tvMore;
        private ImageView ivLabel;
        private RecyclerView mRecyclerView;
        private LinearLayoutManager mLayoutManager;
        private GameCategoryItemAdapter mImageAdapter;

        private WallpaperViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_type);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMore = itemView.findViewById(R.id.tv_more);
            ivLabel = itemView.findViewById(R.id.iv_label);

            mRecyclerView = itemView.findViewById(R.id.recycler_view);
            tvMore.setOnClickListener(this::onClick);

            mLayoutManager = new LinearLayoutManager(mFragment.getContext());
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mImageAdapter = new GameCategoryItemAdapter(isVipUser, mFragment, false);

            mRecyclerView.addItemDecoration(new CategoryItemDecoration(SystemUtils.dip2px(mFragment, 16)));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mImageAdapter);
        }

        public void bindViewHolder(int position) {
            tvMore.setTag(position);
            GameBean item = getDataByItemPosition(position);
            if (item == null)
                return;

            mImageAdapter.reset(item);
            tvName.setText(item.getCate_name());
        }

        /**
         * 根据类型跳转到不同的页面
         *
         * @param v
         */
        public void onClick(View v) {
            int position = (int) v.getTag();
            GameBean item = getDataByItemPosition(position);
            if (item == null)
                return;
            track(MyTracker.click_category_more);
            //More 跳转
            GameCategoryListActivity.newStart(mFragment, item, false, position);
        }
    }
}
