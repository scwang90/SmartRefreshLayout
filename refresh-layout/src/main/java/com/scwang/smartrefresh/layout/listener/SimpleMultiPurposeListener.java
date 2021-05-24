package com.scwang.smartrefresh.layout.listener;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

/**
 * 多功能监听器
 * Created by scwang on 2017/5/26.
 */
public class SimpleMultiPurposeListener implements OnMultiPurposeListener, OnMultiListener {

    private RefreshLayout refreshLayout;
    private OnMultiPurposeListener listener;

    public SimpleMultiPurposeListener() {

    }

    public SimpleMultiPurposeListener(OnMultiPurposeListener listener, RefreshLayout refreshLayout) {
        this.listener = listener;
        this.refreshLayout = refreshLayout;
    }

    @Override
    public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
        if (listener != null) {
            listener.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
        }
    }

    @Override
    public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
        if (listener != null) {
            listener.onHeaderReleased(header, headerHeight, maxDragHeight);
        }
    }

    @Override
    public void onHeaderStartAnimator(RefreshHeader header, int footerHeight, int maxDragHeight) {
        if (listener != null) {
            listener.onHeaderStartAnimator(header, footerHeight, maxDragHeight);
        }
    }

    @Override
    public void onHeaderFinish(RefreshHeader header, boolean success) {
        if (listener != null) {
            listener.onHeaderFinish(header, success);
        }
    }

    @Override
    public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
        if (listener != null) {
            listener.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight);
        }
    }

    @Override
    public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {
        if (listener != null) {
            listener.onFooterReleased(footer, footerHeight, maxDragHeight);
        }
    }

    @Override
    public void onFooterStartAnimator(RefreshFooter footer, int headerHeight, int maxDragHeight) {
        if (listener != null) {
            listener.onFooterStartAnimator(footer, headerHeight, maxDragHeight);
        }
    }

    @Override
    public void onFooterFinish(RefreshFooter footer, boolean success) {
        if (listener != null) {
            listener.onFooterFinish(footer, success);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (listener != null) {
            listener.onRefresh(refreshLayout);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (listener != null) {
            listener.onLoadMore(refreshLayout);
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (listener != null) {
            listener.onStateChanged(refreshLayout, oldState, newState);
        }
    }

    @Override
    public void onHeaderMoving(com.scwang.smart.refresh.layout.api.RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
        if (refreshLayout != null) {
            onHeaderMoving(refreshLayout.getRefreshHeader(), isDragging, percent, offset, headerHeight, maxDragHeight);
        }
    }

    @Override
    public void onHeaderReleased(com.scwang.smart.refresh.layout.api.RefreshHeader header, int headerHeight, int maxDragHeight) {
        if (refreshLayout != null) {
            onHeaderReleased(refreshLayout.getRefreshHeader(), headerHeight, maxDragHeight);
        }
    }

    @Override
    public void onHeaderStartAnimator(com.scwang.smart.refresh.layout.api.RefreshHeader header, int headerHeight, int maxDragHeight) {
        if (refreshLayout != null) {
            onHeaderStartAnimator(refreshLayout.getRefreshHeader(), headerHeight, maxDragHeight);
        }
    }

    @Override
    public void onHeaderFinish(com.scwang.smart.refresh.layout.api.RefreshHeader header, boolean success) {
        if (refreshLayout != null) {
            onHeaderFinish(refreshLayout.getRefreshHeader(), success);
        }
    }

    @Override
    public void onFooterMoving(com.scwang.smart.refresh.layout.api.RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
        if (refreshLayout != null) {
            onFooterMoving(refreshLayout.getRefreshFooter(), isDragging, percent, offset, footerHeight, maxDragHeight);
        }
    }

    @Override
    public void onFooterReleased(com.scwang.smart.refresh.layout.api.RefreshFooter footer, int footerHeight, int maxDragHeight) {
        if (refreshLayout != null) {
            onFooterReleased(refreshLayout.getRefreshFooter(), footerHeight, maxDragHeight);
        }
    }

    @Override
    public void onFooterStartAnimator(com.scwang.smart.refresh.layout.api.RefreshFooter footer, int footerHeight, int maxDragHeight) {
        if (refreshLayout != null) {
            onFooterStartAnimator(refreshLayout.getRefreshFooter(), footerHeight, maxDragHeight);
        }
    }

    @Override
    public void onFooterFinish(com.scwang.smart.refresh.layout.api.RefreshFooter footer, boolean success) {
        if (refreshLayout != null) {
            onFooterFinish(refreshLayout.getRefreshFooter(), success);
        }
    }

    @Override
    public void onLoadMore(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
        if (this.refreshLayout != null) {
            onLoadMore(this.refreshLayout);
        }
    }

    @Override
    public void onRefresh(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
        if (this.refreshLayout != null) {
            onRefresh(this.refreshLayout);
        }
    }

    @Override
    public void onStateChanged(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout, @NonNull com.scwang.smart.refresh.layout.constant.RefreshState oldState, @NonNull com.scwang.smart.refresh.layout.constant.RefreshState newState) {
        if (this.refreshLayout != null) {
            onStateChanged(this.refreshLayout, RefreshState.from(oldState), RefreshState.from(newState));
        }
    }
}
