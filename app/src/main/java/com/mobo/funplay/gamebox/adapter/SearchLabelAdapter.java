package com.mobo.funplay.gamebox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.manager.SPManager;

import java.util.ArrayList;

/**
 * @author : ydli
 * @time : 20-6-23 下午4:50
 * @description 搜索词条适配器
 */
public class SearchLabelAdapter extends RecyclerView.Adapter<SearchLabelAdapter.ViewHolder> {
    private OnViewItemClick onViewItemClick;
    private ArrayList<String> searchBeans = new ArrayList<>();
    private Context context;
    private boolean search;

    public SearchLabelAdapter(Context context, boolean isSearch) {
        this.search = isSearch;
        this.context = context;
    }

    public void update(ArrayList<String> list, boolean isAppend) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (!isAppend) {
            searchBeans.clear();
        }
        searchBeans.addAll(list);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        delete("deleteAll", true);
    }

    public void delete(String str) {
        delete(str, false);
    }

    public void delete(String str, boolean isAll) {
        if (searchBeans == null || searchBeans.isEmpty()) {
            return;
        }
        if (isAll) {
            searchBeans.removeAll(searchBeans);
        } else {
            searchBeans.remove(str);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return searchBeans == null ? 0 : searchBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView historyLabel;
        private ImageView close;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyLabel = itemView.findViewById(R.id.history_label);
            close = itemView.findViewById(R.id.iv_close);
            close.setOnClickListener(this::onClose);
            itemView.setOnClickListener(this::onItem);
        }

        private void bind(int position) {
            itemView.setTag(position);
            close.setTag(R.id.iv_close, position);
            if (searchBeans == null) {
                return;
            }
            close.setVisibility(search ? View.GONE : View.VISIBLE);
            historyLabel.setText(searchBeans.get(position));
        }

        public void onClose(View v) {
            int position = (int) v.getTag(R.id.iv_close);
            SPManager.deleteLabel(searchBeans.get(position));
            delete(searchBeans.get(position));
            onViewItemClick.onItemCloseOnClick(position, searchBeans);
        }

        public void onItem(View v) {
            int position = (int) v.getTag();
            onViewItemClick.onItemViewOnClick(position, searchBeans.get(position));
        }
    }


    public interface OnViewItemClick {
        void onItemViewOnClick(int position, String item);

        void onItemCloseOnClick(int position, ArrayList<String> item);
    }

    public void setOnItemClickListener(OnViewItemClick itemClickListener) {
        this.onViewItemClick = itemClickListener;
    }

}
