package com.scwang.smartrefreshlayout.listener;

import com.scwang.smartrefreshlayout.api.RefreshFooter;
import com.scwang.smartrefreshlayout.api.RefreshHeader;

/**
 * 多功能监听器
 * Created by SCWANG on 2017/5/26.
 */

public interface OnMultiPurposeListener extends OnRefreshLoadmoreListener, OnStateChangedListener {
    void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight);
    void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight);
    void onHeaderStartAnimator(RefreshHeader header, int bottomHeight, int extendHeight);
    void onHeaderFinish(RefreshHeader header);

    void onFooterPulling(RefreshFooter footer, float percent, int offset, int headHeight, int extendHeight);
    void onFooterReleasing(RefreshFooter footer, float percent, int offset, int headHeight, int extendHeight);
    void onFooterStartAnimator(RefreshFooter footer, int headHeight, int extendHeight);
    void onFooterFinish(RefreshFooter footer);
}
