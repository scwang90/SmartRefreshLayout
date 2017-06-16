package com.scwang.smartrefresh.layout.api;

/**
 * 刷新布局 Hook 用于动画功能复杂的 Header 和 Footer
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshLayoutHook {

    /**
     * 是否同意被取代
     * @param hook 新的 Hook
     * @return true 同意并将被取代，反之不然
     */
    boolean isAgreeDisplace(RefreshLayoutHook hook);

    /**
     * 在已经存在 Hook 的时候 被拒绝替代
     * @param hook 发出拒绝的
     */
    void onRefuseDisplace(RefreshLayoutHook hook);

    interface SuperMethod {
        void invoke(Object... args);
    }
}
