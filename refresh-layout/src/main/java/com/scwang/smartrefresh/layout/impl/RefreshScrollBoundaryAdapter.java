package com.scwang.smartrefresh.layout.impl;

import android.view.MotionEvent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshScrollBoundary;
import com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

public class RefreshScrollBoundaryAdapter implements RefreshScrollBoundary {

    //<editor-fold desc="Internal">
    private MotionEvent mActionEvent;
    private RefreshScrollBoundary boundary;

    void setRefreshScrollBoundary(RefreshScrollBoundary boundary){
        this.boundary = boundary;
    }

    void setActionEvent(MotionEvent event) {
        mActionEvent = event;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshScrollBoundary">
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
        return ScrollBoundaryUtil.canLoadmore(content, mActionEvent);
    }
    //</editor-fold>
}
