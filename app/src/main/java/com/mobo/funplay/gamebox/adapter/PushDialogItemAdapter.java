package com.mobo.funplay.gamebox.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.tracker.FacebookTracker;
import com.mobo.funplay.gamebox.tracker.FirebaseTracker;
import com.mobo.funplay.gamebox.tracker.MyTracker;

import java.util.List;

/**
 * @author : ydli
 * @time : 20-7-23 下午6:17
 * @description dialog push game item adapter
 */
public class PushDialogItemAdapter extends RecyclerView.Adapter<PushDialogItemAdapter.Holder> {
    private List<GameItemBean> beans;
    private Activity activity;

    public PushDialogItemAdapter(Activity activity, List<GameItemBean> beans) {
        this.beans = beans;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_push_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return beans == null ? 0 : beans.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.game_img);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            itemView.setTag(position);
            if (beans == null) {
                return;
            }
            GameItemBean item = beans.get(position);
            if (item == null) {
                return;
            }
            Glide.with(activity).load(item.getThumbnail()).placeholder(R.drawable.default_thumb)
                    .centerCrop().into(imageView);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            GameItemBean item = beans.get(position);
            if (item == null) {
                return;
            }
            GamePreviewActivity.newStart(activity, item, Constants.GAME_PUSH);
            FirebaseTracker.getInstance().track(MyTracker.click_suggest_img);
            FacebookTracker.getInstance().track(MyTracker.click_suggest_img);
        }
    }
}
