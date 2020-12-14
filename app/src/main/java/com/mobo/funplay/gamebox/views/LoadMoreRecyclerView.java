package com.mobo.funplay.gamebox.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.mobo.funplay.gamebox.R;

/**
 * 支持上拉加载更多的RecyclerView
 */
public class LoadMoreRecyclerView extends RecyclerView {
    public static final int DEF_PAGE_SIZE = 20;
    //item 类型
    public final static int TYPE_HEADER = 1001;//头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 1002;//底部--往往是loading_more
    public final static int TYPE_FULL_ITEM = 1003;//需要沾满一行的特殊item

    /**
     * 是否允许加载更多
     */
    private boolean isLoadMoreEnable = false;
    /**
     * 加载更多View是否可见
     */
    private boolean isLoadMoreVisible = false;
    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口
     */
    private boolean isLoadingMore = false;
    /**
     * 已经加载了全部数据
     */
    private boolean hasLoadAll = false;
    /**
     * 加载失败了
     */
    private boolean isLoadError = false;
    /**
     * 标记加载更多的position
     */
    private int mLoadMorePosition;
    /**
     * 自定义实现了头部和底部加载更多的adapter
     */
    private LoadMoreAdapter mAutoLoadAdapter;
    /**
     * 加载更多的监听-业务需要实现加载数据
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    private OnScrollChangedListener mOnScrollChangedListener;

    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mOnLoadMoreListener && isLoadMoreEnable
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最后一条--及加载更多
     * </pre>
     */
    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (mOnLoadMoreListener != null && !hasLoadAll && isLoadMoreEnable && !isLoadingMore && isScrollingToLoadMoreView(dy)) {
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition == mAutoLoadAdapter.getItemCount() - 1) {
                mLoadMorePosition = lastVisiblePosition;
                isLoadError = false;
                isLoadingMore = true;
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    /**
     * RecyclerView正序显示时是上拉加载更多
     * 倒序显示时是下拉加载更多
     *
     * @param dy
     * @return
     */
    private boolean isScrollingToLoadMoreView(int dy) {
        if (getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) getLayoutManager()).getReverseLayout()) {
                return dy < 0;
            }
        }
        return dy > 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollStateChanged(this, state);
        }
    }

    public void setStaggeredGridLayoutManager(int spanCount, int orientation) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        setLayoutManager(layoutManager);
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
        setAutoLoadMoreEnable(true);
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    /**
     * 加载更多监听
     */
    public interface OnLoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }

    /**
     * 滑动监听
     */
    public interface OnScrollChangedListener {
        /**
         * 滑动状态变化
         */
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mAutoLoadAdapter = new LoadMoreAdapter(adapter);
        }
        super.swapAdapter(mAutoLoadAdapter, true);
    }

    /**
     * 获取第一条展示的位置
     *
     * @return int
     */
    public int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param loadMoreEnable
     */
    public void setAutoLoadMoreEnable(boolean loadMoreEnable) {
        isLoadMoreEnable = loadMoreEnable;
    }

    /**
     * 通知更多的数据已经加载完成
     */
    public void loadMoreComplete() {
        isLoadingMore = false;
        isLoadMoreVisible = true;
        notifyDataSetChanged();
    }

//    /**
//     * 通知更多的数据已经加载完成
//     *
//     * @param itemCount 新增个数
//     */
//    public void loadMoreComplete(int itemCount) {
//        loadMoreComplete(itemCount, DEF_PAGE_SIZE);
//    }

    /**
     * 通知更多的数据已经加载完成
     *
     * @param itemCount 新增个数
     * @param pageSize  每页个数
     */
    public void loadMoreComplete(int itemCount, int pageSize) {
        isLoadingMore = false;
        isLoadMoreVisible = true;
        int startPosition = mAutoLoadAdapter.getInternalItemCount() - itemCount;
        if (startPosition < pageSize) {
            //当前是第一页数据必须全部刷新全部(因为可能是下拉刷新操作，页面上已经有了多页数据，而最新数据只是第一页)
            mAutoLoadAdapter.notifyDataSetChanged();
        } else {
            //加载更多布局也要刷新
            mAutoLoadAdapter.notifyItemChanged(mLoadMorePosition);
            startPosition = mAutoLoadAdapter.getExternalPosition(startPosition);
            mAutoLoadAdapter.notifyItemRangeInserted(startPosition, itemCount);
        }
    }

    /**
     * 通知已经加载完全部数据
     */
    public void setHasLoadAll(boolean hasLoadAll) {
        this.hasLoadAll = hasLoadAll;
    }

    public boolean hasLoadAll() {
        return hasLoadAll;
    }

    /**
     * 通知是否加载失败
     */
    public void setLoadError(boolean loadError) {
        isLoadingMore = false;
        isLoadError = loadError;
        if (isLoadMoreEnable) {
            mAutoLoadAdapter.notifyItemChanged(mLoadMorePosition);
        }
    }

    /**
     * 刷新列表数据
     */
    public void notifyDataSetChanged() {
        mAutoLoadAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新item数据
     */
    public void notifyItemInserted(int position) {
        position = mAutoLoadAdapter.getExternalPosition(position);
        mAutoLoadAdapter.notifyItemInserted(position);
    }

    /**
     * 刷新item数据
     */
    public void notifyItemChanged(int position) {
        position = mAutoLoadAdapter.getExternalPosition(position);
        mAutoLoadAdapter.notifyItemChanged(position);
    }

    /**
     * 删除item数据
     */
    public void notifyItemRemoved(int position) {
        position = mAutoLoadAdapter.getExternalPosition(position);
        mAutoLoadAdapter.notifyItemRemoved(position);
    }

    public void addHeaderView(View headerView) {
        mAutoLoadAdapter.addHeaderView(headerView);
    }

    public void removeHeaderView() {
        mAutoLoadAdapter.removeHeaderView();
    }

    /**
     * 加载更多Adapter
     */
    public class LoadMoreAdapter extends Adapter<ViewHolder> {
        //业务数据adapter
        private Adapter mInternalAdapter;
        private View mHeaderView;

        public LoadMoreAdapter(Adapter adapter) {
            mInternalAdapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new HeaderViewHolder(mHeaderView);
            } else if (viewType == TYPE_FOOTER) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_load_more, parent, false);
                return new LoadMoreViewHolder(itemView);
            } else {
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        /**
         * 计算mAutoLoadAdapter实际position
         *
         * @param position
         * @returno
         */
        private int getExternalPosition(int position) {
            if (mHeaderView != null) {
                position = position + 1;
            }
            return position;
        }

        /**
         * 计算mInternalAdapter实际position
         *
         * @param position
         * @returno
         */
        private int getInternalPosition(int position) {
            if (mHeaderView != null) {
                position = position - 1;
            }
            return position;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == TYPE_HEADER) {
            } else if (type == TYPE_FOOTER) {
                ((LoadMoreViewHolder) holder).bindViewData();
            } else {
                mInternalAdapter.onBindViewHolder(holder, getInternalPosition(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeaderView != null && position == 0) {
                return TYPE_HEADER;
            } else if (position == getItemCount() - 1 && isLoadMoreEnable) {
                return TYPE_FOOTER;
            } else {
                return mInternalAdapter.getItemViewType(getInternalPosition(position));
            }
        }

        /**
         * 需要计算上加载更多
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (mHeaderView != null) count++;
            if (isLoadMoreEnable) count++;
            return count;
        }

        /**
         * 计算mInternalAdapter item数量
         *
         * @return
         */
        public int getInternalItemCount() {
            return mInternalAdapter.getItemCount();
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                int viewType = getItemViewType(holder.getLayoutPosition());
                if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER) {
                    //解决瀑布流header/footer不能占满一行的bug
                    ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
                }
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mInternalAdapter.onAttachedToRecyclerView(recyclerView);

            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        //解决GridLayout中header/footer不能占满一行的bug
                        int viewType = getItemViewType(position);
                        if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER || viewType == TYPE_FULL_ITEM) {
                            return gridLayoutManager.getSpanCount();
                        }
                        return 1;
                    }
                });
            }
        }

        public void addHeaderView(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        public void removeHeaderView() {
            mHeaderView = null;
        }

        public class HeaderViewHolder extends ViewHolder {

            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class LoadMoreViewHolder extends ViewHolder implements OnClickListener {
            public ProgressBar mLoading;
            public ImageView mLoadFail;
            public TextView mText;
            public View mHasLoadAll;

            public LoadMoreViewHolder(View itemView) {
                super(itemView);
                mLoading = itemView.findViewById(R.id.pb_loading);
                mLoadFail = itemView.findViewById(R.id.iv_load_fail);
                mText = itemView.findViewById(R.id.tv_text);
                mHasLoadAll = itemView.findViewById(R.id.tv_has_load_all);
                itemView.setOnClickListener(this);
            }

            public void bindViewData() {
                if (isLoadMoreVisible && !hasLoadAll) {
                    itemView.setVisibility(VISIBLE);
                    if (isLoadError) {
                        mHasLoadAll.setVisibility(GONE);
                        mLoading.setVisibility(GONE);
                        mLoadFail.setVisibility(VISIBLE);
                        mText.setVisibility(VISIBLE);
                        mText.setText(R.string.load_more_fail);
                    } else {
                        mHasLoadAll.setVisibility(GONE);
                        mLoadFail.setVisibility(GONE);
                        mLoading.setVisibility(VISIBLE);
                        mText.setVisibility(VISIBLE);
                        mText.setText(R.string.is_loading_more);
                    }
                } else {
                    itemView.setVisibility(GONE);
                }
            }

            @Override
            public void onClick(View v) {
                if (isLoadError) {
                    isLoadError = false;
                    isLoadingMore = true;
                    mText.setText(R.string.is_loading_more);
                    mLoading.setVisibility(VISIBLE);
                    mLoadFail.setVisibility(GONE);
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    }
}