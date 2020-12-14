package com.mobo.funplay.gamebox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.views.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ydli
 * @time : 20-6-23 下午4:50
 * @description
 */
public class HotListAdapter extends RecyclerView.Adapter<HotListAdapter.ViewHolder> {
    private OnViewItemClick onViewItemClick;
    private List<GameItemBean> mDataList = new ArrayList<>();

    public HotListAdapter() {
    }

    public void update(List<GameItemBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (!mDataList.isEmpty()) {
            mDataList.clear();
        }
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RoundedImageView imageView;
        private ImageView bannerIcon;
        private TextView bannerTitle, bannerDesc, bannerAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
            bannerIcon = itemView.findViewById(R.id.banner_icon);
            bannerTitle = itemView.findViewById(R.id.banner_item_title);
            bannerDesc = itemView.findViewById(R.id.banner_item_description);
            bannerAction = itemView.findViewById(R.id.banner_action);
            //bannerAction.setOnClickListener(this);
            itemView.findViewById(R.id.fl_banner).setVisibility(View.GONE);

        }

        private void bind(int position) {
            itemView.setTag(position);
            if (mDataList == null) {
                return;
            }
            GameItemBean item = mDataList.get(position);
            if (item == null) {
                return;
            }
            Glide.with(itemView.getContext()).load(item.getThumbnail()).placeholder(R.drawable.default_thumb)
                    .centerCrop().into(imageView);

            Glide.with(imageView).load(item.getThumbnail() == null ? R.drawable.item_banner_default : item.getThumbnail()).
                    placeholder(R.drawable.item_banner_default).into(bannerIcon);
            bannerTitle.setText(item.getName());
            bannerDesc.setText(item.getDesc());
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            GameItemBean item = mDataList.get(position);
            if (item == null) {
                return;
            }
            onViewItemClick.onItemViewOnClick(position, item);
        }
    }


    public interface OnViewItemClick {
        void onItemViewOnClick(int position, GameItemBean item);
    }

    public void setOnItemClickListener(OnViewItemClick itemClickListener) {
        this.onViewItemClick = itemClickListener;
    }

}
