package com.mobo.funplay.gamebox.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.manager.SPManager;

/**
 * @Description: 游戏分类详情页适配器
 * @Author: ydli
 * @CreateDate: 2020-06-24 15：24
 */
public class GameCategoryListAdapter extends BaseNativeAdAdapter<GameItemBean> {
    private Activity mActivity;
    private String type;
    private OnItemLikeClick onItemLikeClick;
    private ImageView imageView;


    public interface OnItemLikeClick {
        void onItemViewLikeOnClick(int position, GameItemBean item);
    }

    public void setOnItemClickListener(OnItemLikeClick listener) {
        this.onItemLikeClick = listener;
    }

    public GameCategoryListAdapter(Activity activity, String type) {
        super();
        this.mActivity = activity;
        this.type = type;
    }

    @Override
    public ItemViewHolder onCreateNativeAdViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ad, parent, false);
        return new NativeAdViewHolder(view);
    }

    @Override
    public int getNativeAdInterval() {
        return getDataList().size();
    }

    @Override
    public ItemViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_category_list, parent, false);
        return new WallpaperViewHolder(view);
    }

    public class WallpaperViewHolder extends ItemViewHolder {
        private ImageView ivThumb, ivCollect;
        private TextView tvName, tvDescription;

        private WallpaperViewHolder(View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            ivCollect = itemView.findViewById(R.id.iv_collect);
            ivCollect.setOnClickListener(this::CollectOnClick);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        public void bindViewHolder(int position) {
            ivCollect.setTag(R.id.iv_collect, position);
            GameItemBean bean = getData(position);
            if (bean == null) {
                return;
            }

            ivCollect.setSelected(bean.isCollect());

            itemView.setOnClickListener(view -> GamePreviewActivity.newStart(mActivity, bean, type));

            tvName.setText(bean.getName());
            tvDescription.setText(bean.getDesc());

            Glide.with(itemView.getContext()).load(bean.getThumbnail()).into(ivThumb);
        }

        public void CollectOnClick(View v) {
            int position = (int) v.getTag(R.id.iv_collect);
            GameItemBean bean = getData(position);
            if (bean == null) {
                return;
            }
            imageView = (ImageView) v;
            onItemLikeClick.onItemViewLikeOnClick(position, bean);
        }
    }

    public void setLike(int position) {
        GameItemBean bean = getData(position);
        if (bean == null) {
            return;
        }

        if (bean.isCollect()) {
            imageView.setSelected(false);
            bean.setCollect(false);
            SPManager.deleteGameCollectBean(bean);
        } else {
            imageView.setSelected(true);
            bean.setCollect(true);
            SPManager.addGameCollectBean(bean);
            int likeNum = SPManager.getInstance().getInt(Constants.DATA_COLLECT_GAME_LIST_NUM, 0);
            SPManager.getInstance().setInt(Constants.DATA_COLLECT_GAME_LIST_NUM, likeNum + 1);
        }
        notifyItemChanged(position);
    }
}
