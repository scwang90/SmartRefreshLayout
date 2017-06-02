package com.scwang.smartrefreshlayout.api;

/**
 * 刷新底部
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshFooter extends RefreshInternal {
    void onPullingUp(int offset, int bottomHeight, int extendHeight);
    void onPullReleasing(int offset, int bottomHeight, int extendHeight);
    void startAnimator(int bottomHeight, int extendHeight);
}
