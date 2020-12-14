package com.mobo.funplay.gamebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GameCategoryListActivity;
import com.mobo.funplay.gamebox.bean.GameBean;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;

/**
 * @Description: H5游戏分类图片显示适配器
 * @Author: ydli
 * @CreateDate: 19-12-06 下午3:00
 */
public class GameCategoryItemAdapter extends BaseRecyclerAdapter<GameItemBean, GameCategoryItemAdapter.ItemViewHolder> {
    private Fragment mFragment;
    private GameBean mGameBean;
    private boolean isVipUser;

    public GameCategoryItemAdapter(boolean isVipUser, Fragment fragment, boolean category) {
        this.isVipUser = isVipUser;
        mFragment = fragment;
    }

    public void reset(GameBean category) {
        super.reset(category.getItems());
        this.mGameBean = category;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_category_image, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindViewData(position);
    }

    @Override
    public int getItemCount() {
        int c = super.getItemCount();
        return (c > Constants.GAME_MAX_ITEM_COUNT) ? Constants.GAME_MAX_ITEM_COUNT : c;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivH5;
        private TextView tvMore;
        private TextView tvName;
        private ImageView ivImage;

        private ItemViewHolder(View itemView) {
            super(itemView);
            ivH5 = itemView.findViewById(R.id.iv_h5);
            tvMore = itemView.findViewById(R.id.tv_more);
            tvName = itemView.findViewById(R.id.tv_name);
            ivImage = itemView.findViewById(R.id.iv_image);
            ivImage.setOnClickListener(this::onClick);
        }

        private void bindViewData(int position) {
            ivImage.setTag(R.id.iv_image, position);

            if (getItemCount() >= Constants.GAME_MAX_ITEM_COUNT && position == getItemCount() - 1) {
                tvMore.setVisibility(View.VISIBLE);
            } else {
                tvMore.setVisibility(View.GONE);
            }


            GameItemBean gameItemBean = getItem(position);
            if (null == gameItemBean)
                return;

            tvName.setText(gameItemBean.getName());
            Glide.with(itemView.getContext()).load(gameItemBean.getThumbnail())
                    .placeholder(R.drawable.default_thumb).into(ivImage);
        }

        /**
         * 根据类型跳转到不同的页面
         *
         * @param v
         */
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.iv_image);

            if (getItemCount() >= Constants.GAME_MAX_ITEM_COUNT && position == getItemCount() - 1) {
                //在跳转到游戏分类详情界面
                GameCategoryListActivity.newStart(mFragment, mGameBean, false, position);
            } else {
                //直接跳转游戏详情页
                GameCategoryListActivity.newStart(mFragment, mGameBean, true, position);
            }
        }
    }
}
