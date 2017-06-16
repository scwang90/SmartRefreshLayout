package com.scwang.smartrefresh.layout.api;

/**
 * 刷新布局 Hook 用于动画功能复杂的 Footer
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshLayoutHookFooter extends RefreshLayoutHook{
    void onHookFinishLoadmore(SuperMethod method, RefreshLayout layout);
}
