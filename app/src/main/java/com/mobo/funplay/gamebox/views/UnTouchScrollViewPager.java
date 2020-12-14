package com.mobo.funplay.gamebox.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 不可以通过触摸左右滑动的ViewPager，setCurrentItem(m)会有滑动效果
 * Created by Administrator on 2017/3/29.
 */
public class UnTouchScrollViewPager extends ViewPager {
    private boolean onEvent = false;

    public UnTouchScrollViewPager(Context context) {
        super(context);
    }

    public UnTouchScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return onEvent && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onEvent && super.onInterceptTouchEvent(ev);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    /**
     * 设置其是否能滑动换页
     * @param event false 不能换页， true 可以滑动换页
     */
    public void setOnEvent(boolean event) {
        this.onEvent = event;
    }
}
