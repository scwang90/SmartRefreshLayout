package com.scwang.smartrefresh.layout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

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

    void setUpComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter);
    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    void setEnableLoadMoreWhenContentNotFull(boolean enable);

    void moveSpinner(int spinner, int headerTranslationViewId, int footerTranslationViewId);

    boolean canRefresh();
    boolean canLoadMore();

    AnimatorUpdateListener scrollContentWhenFinished(int spinner);
}
