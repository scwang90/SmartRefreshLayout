package com.scwang.smartrefresh.layout.impl;

import android.graphics.PointF;
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
    public PointF mActionEvent;
    public ScrollBoundaryDecider boundary;
    public boolean mEnableLoadMoreWhenContentNotFull = true;

//    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary){
//        this.boundary = boundary;
//    }

//    void setActionEvent(MotionEvent event) {
//        //event 在没有必要时候会被设置为 null
//        mActionEvent = event;
//    }
//
//    public void setEnableLoadMoreWhenContentNotFull(boolean enable) {
//        mEnableLoadMoreWhenContentNotFull = enable;
//    }
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    @Override
    public boolean canRefresh(View content) {
        if (boundary != null) {
            return boundary.canRefresh(content);
        }
        //mActionEvent == null 时 canRefresh 不会动态递归搜索
        return ScrollBoundaryUtil.canRefresh(content, mActionEvent);
    }

    @Override
    public boolean canLoadMore(View content) {
        if (boundary != null) {
            return boundary.canLoadMore(content);
        }
//        if (mEnableLoadMoreWhenContentNotFull) {
//            //mActionEvent == null 时 canScrollDown 不会动态递归搜索
//            return !ScrollBoundaryUtil.canScrollDown(content, mActionEvent);
//        }
        //mActionEvent == null 时 canLoadMore 不会动态递归搜索
        return ScrollBoundaryUtil.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
    }
    //</editor-fold>
}
