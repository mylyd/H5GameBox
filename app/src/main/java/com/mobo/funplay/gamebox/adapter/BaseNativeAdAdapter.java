package com.mobo.funplay.gamebox.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobo.funplay.gamebox.views.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jzhou
 * time   : 2019/12/03
 * desc   : 内嵌广告的数据适配器
 * version: 1.0
 */
public abstract class BaseNativeAdAdapter<T> extends RecyclerView.Adapter<BaseNativeAdAdapter.ItemViewHolder> {
    protected final static int ITEM_NATIVE_AD = LoadMoreRecyclerView.TYPE_FULL_ITEM;
    protected final static int ITEM_DATA = 1;

    private boolean isAdInVisible;
    private List<T> mDataList;
    protected OnItemClickListener<T> mOnItemClickListener;

    public BaseNativeAdAdapter() {
        this.isAdInVisible = true;
        this.mDataList = new ArrayList<>();
    }

    public BaseNativeAdAdapter(boolean adInVisible, List<T> items) {
        this.isAdInVisible = adInVisible;
        this.mDataList = items;
    }

    public void setAdInVisible(boolean adInVisible) {
        isAdInVisible = adInVisible;
    }

    public boolean isAdInVisible() {
        return isAdInVisible;
    }

    @Override
    public int getItemViewType(int position) {
        //position==(interval+1)n-1时显示广告
        int interval = getNativeAdInterval();
        if (!isAdInVisible && (position + 1) % (interval + 1) == 0) {
            return ITEM_NATIVE_AD;
        }
        return ITEM_DATA;
    }

    /**
     * 判断当前是否是广告item
     *
     * @param position item位置
     * @return boolean
     */
    public boolean isNativeAdView(int position) {
        return ITEM_NATIVE_AD == getItemViewType(position);
    }

    /**
     * 壁纸加广告的总数量
     *
     * @return int
     */
    @Override
    public int getItemCount() {
        int count = mDataList.size();
        if (!isAdInVisible && count > 0) {
            return count + count / getNativeAdInterval();
        }

        return count;
    }

    /**
     * 默认每9个壁纸展示一个广告位，灰度打开则15个壁纸展示一个广告位
     *
     * @return
     */
    protected int getNativeAdInterval() {
        return 6;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_DATA:
                return onCreateDataViewHolder(parent, viewType);
            case ITEM_NATIVE_AD:
            default:
                return onCreateNativeAdViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindViewHolder(position);
    }

    public abstract ItemViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType);

    public abstract ItemViewHolder onCreateNativeAdViewHolder(ViewGroup parent, int viewType);

    public void reset(List<T> items) {
        if (items != null) {
            mDataList.clear();
            mDataList.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<T> items) {
        if (items != null && mDataList.addAll(items)) {
            notifyDataSetChanged();
        }
    }

    public void addTop(List<T> items) {
        if (items != null) {
            mDataList.addAll(0, items);
            notifyDataSetChanged();
        }
    }

    public void getDataListClear() {
        if (getDataList() != null) {
            mDataList.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 获取数据集合
     *
     * @return List<T>
     */
    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 计算数据显示在RecyclerView中的位置(包含广告)
     *
     * @param dataPosition 数据的实际中的位置
     * @return int
     */
    public int getItemPosition(int dataPosition) {
        if (isAdInVisible) return dataPosition;
        int interval = getNativeAdInterval();
        return dataPosition + dataPosition / interval;
    }

    /**
     * 计算数据在mItemList中的位置
     *
     * @param itemPosition 数据显示在RecyclerView中的位置
     * @return int
     */
    public int getDataPosition(int itemPosition) {
        if (isAdInVisible) return itemPosition;
        int interval = getNativeAdInterval();
        return itemPosition - itemPosition / (interval + 1);
    }

    /**
     * 根据RecyclerView中的位置获取壁纸
     *
     * @param itemPosition 数据显示在RecyclerView中的位置
     * @return T
     */
    public T getDataByItemPosition(int itemPosition) {
        return mDataList.get(getDataPosition(itemPosition));
    }

    /**
     * 根据数据集合中的实际位置获取数据
     *
     * @param dataPosition 集合中的位置
     * @return T
     */
    public T getData(int dataPosition) {
        return mDataList.get(dataPosition);
    }

    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            int position = (int) v.getTag();
            T data = getDataByItemPosition(position);
            if (data == null) return;
            mOnItemClickListener.onItemClick(position, getDataPosition(position), data);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        /**
         * @param itemPosition 显示在RecyclerView中位置
         * @param dataPosition 数据集合中的实际位置
         * @param data         数据
         */
        void onItemClick(int itemPosition, int dataPosition, T data);
    }

    public abstract static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindViewHolder(int position);
    }

    public class NativeAdViewHolder extends ItemViewHolder {

        public NativeAdViewHolder(View itemView) {
            super(itemView);
        }

        public void bindViewHolder(final int position) {

        }
    }
}
