package com.scwang.smartrefresh.layout.api;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 刷新内容组件
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshContent {
    void moveSpinner(int spinner);
    boolean canScrollUp();
    boolean canScrollDown();
    int getMeasuredWidth();
    int getMeasuredHeight();
    void measure(int widthSpec, int heightSpec);
    void layout(int left, int top, int right, int bottom);

    View getView();
    View getScrollableView();
    ViewGroup.LayoutParams getLayoutParams();

    void onActionDown(MotionEvent e);
    void onActionUpOrCancel(MotionEvent e);

    void setEnableAutoLoadmore(boolean enable, RefreshKernel kernel);

    void onLoadingFinish(int footerHeight);
}
