package com.mobo.funplay.gamebox.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jzhou
 * time   : 2019/12/03
 * desc   : 列表数据适配器基类,实现数据的增删改查等基本操作
 * version: 1.0
 */
public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> mList;
    protected OnItemClickListener<T> mOnItemClickListener;

    public BaseRecyclerAdapter() {
        mList = new ArrayList<>();
    }

    public BaseRecyclerAdapter(List<T> list) {
        mList = list;
    }

    public List<T> getItems() {
        return mList;
    }

    public void reset(List<T> list) {
        if (list != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addFirst(T t) {
        mList.add(0, t);
        notifyItemInserted(0);
    }

    public void add(T t) {
        if (mList.add(t)) {
            notifyDataSetChanged();
        }
    }

    public void addAllFirst(List<T> list) {
        if (mList.addAll(0, list)) {
            notifyDataSetChanged();
        }
    }

    public void addAll(List<T> list) {
        if (mList.addAll(list)) {
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            int position = (int) v.getTag();
            T data = getItem(position);
            if (data == null) return;
            mOnItemClickListener.onItemClick(position, getItem(position));
        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        /**
         * @param position 显示在RecyclerView中位置
         * @param data     数据
         */
        void onItemClick(int position, T data);
    }
}
