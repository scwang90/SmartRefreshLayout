package com.scwang.smartrefresh.layout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 刷新内容组件
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshContent {
    void moveSpinner(int spinner);
    boolean canRefresh();
    boolean canLoadmore();
    int getMeasuredWidth();
    int getMeasuredHeight();
    void measure(int widthSpec, int heightSpec);
    void layout(int left, int top, int right, int bottom);

    View getView();
    View getScrollableView();
    ViewGroup.LayoutParams getLayoutParams();

    void onActionDown(MotionEvent e);
    void onActionUpOrCancel();

    void setupComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter);
    void onInitialHeaderAndFooter(int headerHeight, int footerHeight);
    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary);
    void setEnableLoadmoreWhenContentNotFull(boolean enable);

    AnimatorUpdateListener onLoadingFinish(RefreshKernel kernel, int footerHeight, int startDelay, int reboundDuration);
}
