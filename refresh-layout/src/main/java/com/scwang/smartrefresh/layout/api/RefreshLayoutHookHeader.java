package com.scwang.smartrefresh.layout.api;

/**
 * 刷新布局 Hook 用于动画功能复杂的 Header
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshLayoutHookHeader extends RefreshLayoutHook{
    void onHookFinishRefresh(SuperMethod method, RefreshLayout layout);
}
