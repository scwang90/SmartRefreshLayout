package com.scwang.smart.refresh.layout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider;

/**
 * 刷新内容组件
 * Created by scwang on 2017/5/26.
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
