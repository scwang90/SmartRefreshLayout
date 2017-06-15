package com.scwang.smartrefresh.layout.api;

/**
 * 刷新布局 Hook 用于动画功能复杂的 Header 和 Footer
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshLayoutHook {
    interface SuperMethod {
        void execute(Object... args);
    }
    void onHookFinisRefresh(SuperMethod method, RefreshLayout layout, int delayed);
}
