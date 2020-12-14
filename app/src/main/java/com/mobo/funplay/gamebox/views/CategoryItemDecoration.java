package com.mobo.funplay.gamebox.views;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : jzhou
 * time   : 2019/11/15
 * desc   : 列表item间距设置
 * version: 1.0
 */
public class CategoryItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing; //间距

    public CategoryItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = spacing;
            outRect.right = spacing;
        } else {
            outRect.right = spacing;
        }
    }
}