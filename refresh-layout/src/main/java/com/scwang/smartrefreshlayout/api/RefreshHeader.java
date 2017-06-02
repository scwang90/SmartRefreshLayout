package com.scwang.smartrefreshlayout.api;

/**
 * 刷新头部
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshHeader extends RefreshInternal {
    void onPullingDown(int offset, int headHeight, int extendHeight);
    void onReleasing(int offset, int headHeight, int extendHeight);
    void startAnimator(int headHeight, int extendHeight);
}
