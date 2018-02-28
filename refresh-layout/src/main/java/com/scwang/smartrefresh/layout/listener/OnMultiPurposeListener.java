package com.scwang.smartrefresh.layout.listener;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * 多功能监听器
 * Created by SCWANG on 2017/5/26.
 */

public interface OnMultiPurposeListener extends OnRefreshLoadMoreListener, OnStateChangedListener {
    /**
     * 手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     * @param header 头部
     * @param isDragging true 手指正在拖动 false 回弹动画
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+extendHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+extendHeight)
     * @param headerHeight 高度 HeaderHeight or FooterHeight
     * @param extendHeight 扩展高度  extendHeaderHeight or extendFooterHeight
     */
    void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int extendHeight);

//    void onHeaderPulling(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight);
//    void onHeaderReleasing(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight);
    void onHeaderReleased(RefreshHeader header, int headerHeight, int extendHeight);
    void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int extendHeight);
    void onHeaderFinish(RefreshHeader header, boolean success);

    /**
     * 手指拖动上拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     * @param footer 尾部
     * @param isDragging true 手指正在拖动 false 回弹动画
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+extendHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+extendHeight)
     * @param footerHeight 高度 HeaderHeight or FooterHeight
     * @param extendHeight 扩展高度  extendHeaderHeight or extendFooterHeight
     */
    void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int extendHeight);

//    void onFooterPulling(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight);
//    void onFooterReleasing(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight);
    void onFooterReleased(RefreshFooter footer, int footerHeight, int extendHeight);
    void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int extendHeight);
    void onFooterFinish(RefreshFooter footer, boolean success);
}
