package com.mobo.funplay.gamebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.manager.SPManager;

/**
 * @Description: 主页Game游戏分类列表适配器
 * @Author: ydli
 * @CreateDate: 2020-06-28 下午9:57
 */
public class GameHomeAdapter extends BaseNativeAdAdapter<GameItemBean> {
    private Fragment mFragment;
    private boolean isVipUser;
    private OnItemLikeClick onItemLikeClick;
    private ImageView imageView;

    public interface OnItemLikeClick {
        void onItemViewLikeOnClick(int position, GameItemBean item);
    }

    public void setOnItemClickListener(OnItemLikeClick listener) {
        this.onItemLikeClick = listener;
    }


    public GameHomeAdapter(Fragment fragment) {
        super();
        mFragment = fragment;
    }

    @Override
    public ItemViewHolder onCreateNativeAdViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ad, parent, false);
        return new NativeAdViewHolder(view);
    }

    @Override
    public int getNativeAdInterval() {
        return 6;
    }

    @Override
    public ItemViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new WallpaperViewHolder(view);
    }

    public class WallpaperViewHolder extends ItemViewHolder {
        private ImageView ivImage, ivCollect;
        private TextView tvName, tvLike;

        private WallpaperViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_game);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLike = itemView.findViewById(R.id.tv_like);
            ivCollect = itemView.findViewById(R.id.iv_collect);
            ivCollect.setOnClickListener(this::onClick);
            itemView.setOnClickListener(this::onItemClick);
        }

        public void bindViewHolder(int position) {
            ivCollect.setTag(position);
            itemView.setTag(position);
            GameItemBean bean = getData(position);
            if (bean == null) {
                return;
            }
            ivCollect.setSelected(bean.isCollect());
            tvLike.setText(String.valueOf(bean.getCnt_like()));
            tvName.setText(bean.getName());
            Glide.with(itemView.getContext()).load(bean.getThumbnail()).placeholder(R.drawable.default_thumb).into(ivImage);
        }

        /**
         * 收藏
         *
         * @param view
         */
        public void onClick(View view) {
            int position = (int) view.getTag();
            GameItemBean bean = getData(position);
            if (bean == null) {
                return;
            }
            imageView = (ImageView) view;
            onItemLikeClick.onItemViewLikeOnClick(position, bean);
        }

        public void onItemClick(View view) {
            int position = (int) view.getTag();
            GameItemBean bean = getData(position);
            if (bean == null) {
                return;
            }
            GamePreviewActivity.newStart(mFragment, bean, Constants.GAME_HOME);
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
