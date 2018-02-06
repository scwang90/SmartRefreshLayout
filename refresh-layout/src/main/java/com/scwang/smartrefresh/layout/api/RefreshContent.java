package com.scwang.smartrefresh.layout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.RestrictTo;
import android.view.MotionEvent;
import android.view.View;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static android.support.annotation.RestrictTo.Scope.SUBCLASSES;

/**
 * 刷新内容组件
 * Created by SCWANG on 2017/5/26.
 */
@RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
public interface RefreshContent {
    void moveSpinner(int spinner);
    boolean canRefresh();
    boolean canLoadMore();

    View getView();
    View getScrollableView();

    void onActionDown(MotionEvent e);
    void onActionUpOrCancel();

    void fling(int velocity);
    void setUpComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter);
    void onInitialHeaderAndFooter(int headerHeight, int footerHeight);
    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    void setEnableLoadMoreWhenContentNotFull(boolean enable);

    AnimatorUpdateListener scrollContentWhenFinished(int spinner);
}
