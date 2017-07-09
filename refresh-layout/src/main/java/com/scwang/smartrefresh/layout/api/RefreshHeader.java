package com.scwang.smartrefresh.layout.api;

/**
 * 刷新头部
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshHeader extends RefreshInternal {
    /**
     * 手指拖动下拉（会连续多次调用）
     * @param percent 下拉的百分比 值 = offset/headerHeight (0 - percent - (headerHeight+extendHeight) / headerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (headerHeight+extendHeight)
     * @param headerHeight Header的高度
     * @param extendHeight Header的扩展高度
     */
    void onPullingDown(float percent, int offset, int headerHeight, int extendHeight);

    /**
     * 手指释放之后的持续动画
     * @param percent 下拉的百分比 值 = offset/headerHeight (0 - percent - (headerHeight+extendHeight) / headerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (headerHeight+extendHeight)
     * @param headerHeight Header的高度
     * @param extendHeight Header的扩展高度
     */
    void onReleasing(float percent, int offset, int headerHeight, int extendHeight);
}
