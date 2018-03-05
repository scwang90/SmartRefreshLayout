package com.scwang.smartrefresh.layout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.NonNull;
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
public interface RefreshContent {

    @NonNull
    View getView();
    @NonNull
    View getScrollableView();

    void onActionDown(MotionEvent e);
//    void onActionUpOrCancel();

    void setUpComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter);
//    void onInitialHeaderAndFooter(int headerHeight, int footerHeight);
    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    void setEnableLoadMoreWhenContentNotFull(boolean enable);

    void moveSpinner(int spinner);

    boolean canRefresh();
    boolean canLoadMore();

    AnimatorUpdateListener scrollContentWhenFinished(int spinner);
}
