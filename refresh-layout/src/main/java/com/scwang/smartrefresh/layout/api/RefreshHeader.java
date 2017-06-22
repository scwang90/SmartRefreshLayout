package com.scwang.smartrefresh.layout.api;

/**
 * 刷新头部
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshHeader extends RefreshInternal {
    void onPullingDown(float percent, int offset, int headHeight, int extendHeight);
    void onReleasing(float percent, int offset, int headHeight, int extendHeight);
}
