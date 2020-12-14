package com.mobo.funplay.gamebox.views;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : jzhou
 * time   : 2019/11/15
 * desc   : 壁纸列表item间距设置
 * version: 1.0
 */
public class GridWallpaperDecoration extends RecyclerView.ItemDecoration {
    private int spacing; //间距

    public GridWallpaperDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // getSpanGroupIndex item位于第几行
        // getSpanSize item占用几个span
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager == null) {
            throw new IllegalStateException("GridLayoutManager is null.");
        }

        // 计算网格每行最多显示几个
        int spanCount = layoutManager.getSpanCount();
        int position = parent.getChildAdapterPosition(view);
        GridLayoutManager.SpanSizeLookup sizeLookup = layoutManager.getSpanSizeLookup();
        if (sizeLookup.getSpanSize(position) == spanCount) {
            outRect.left = spacing;
            outRect.right = spacing;
        } else {
            // 计算item位于该行的第几个
            int spanIndex = sizeLookup.getSpanIndex(position, spanCount);
            if (spanIndex == 0) {
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.bottom = spacing / 2;
                outRect.top = spacing / 2;
            } else {
                outRect.right = spacing;
                outRect.bottom = spacing / 2;
                outRect.top = spacing / 2;
            }
        }
    }
}