package com.mobo.funplay.gamebox.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;

import java.util.List;

/**
 * @Description: H5游戏收藏适配器
 * @Author: ydli
 * @CreateDate:
 */
public class GameCollectionAdapter extends BaseRecyclerAdapter<GameItemBean, GameCollectionAdapter.ItemViewHolder> {
    private Activity mActivity;

    public GameCollectionAdapter(Activity activity) {
        mActivity = activity;
    }

    public void reset(List<GameItemBean> gameItemBean) {
        if (gameItemBean != null) {
            super.reset(gameItemBean);
        }
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
        return getItems() == null ? 0 : getItems().size();
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

            tvMore.setVisibility(View.GONE);
            ivH5.setVisibility(View.GONE);
        }

        private void bindViewData(int position) {
            ivImage.setTag(R.id.iv_image, position);

            GameItemBean gameItemBean = getItem(position);
            if (gameItemBean == null) {
                return;
            }

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
            //直接跳转游戏详情页
            GamePreviewActivity.newStart(mActivity, getItem(position), Constants.GAME_COLLECTION);
        }
    }
}
