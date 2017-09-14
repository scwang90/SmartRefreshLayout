package com.scwang.smartrefresh.layout.impl;

import android.view.MotionEvent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryDeciderAdapter implements ScrollBoundaryDecider {

    //<editor-fold desc="Internal">
    protected MotionEvent mActionEvent;
    protected ScrollBoundaryDecider boundary;
    protected boolean mEnableLoadmoreWhenContentNotFull;

    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary){
        this.boundary = boundary;
    }

    void setActionEvent(MotionEvent event) {
        mActionEvent = event;
    }
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    @Override
    public boolean canRefresh(View content) {
        if (boundary != null) {
            return boundary.canRefresh(content);
        }
        return ScrollBoundaryUtil.canRefresh(content, mActionEvent);
    }

    @Override
    public boolean canLoadmore(View content) {
        if (boundary != null) {
            return boundary.canLoadmore(content);
        }
        if (mEnableLoadmoreWhenContentNotFull) {
            return !ScrollBoundaryUtil.canScrollDown(content, mActionEvent);
        }
        return ScrollBoundaryUtil.canLoadmore(content, mActionEvent);
    }

    public void setEnableLoadmoreWhenContentNotFull(boolean enable) {
        mEnableLoadmoreWhenContentNotFull = enable;
    }
    //</editor-fold>
}
