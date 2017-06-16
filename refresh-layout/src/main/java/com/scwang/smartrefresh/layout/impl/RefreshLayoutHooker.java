package com.scwang.smartrefresh.layout.impl;

import com.scwang.smartrefresh.layout.api.RefreshLayoutHook;

/**
 * 刷新布局 Hook 用于动画功能复杂的 Header 和 Footer
 * Created by SCWANG on 2017/5/26.
 */

public class RefreshLayoutHooker implements RefreshLayoutHook {
    /**
     * 默认同意被取代
     * @param hook 新的 Hook
     */
    @Override
    public boolean isAgreeDisplace(RefreshLayoutHook hook) {
        return true;
    }

    /**
     * 在已经存在 Hook 的时候 被拒绝替代
     * @param hook 发出拒绝的
     */
    @Override
    public void onRefuseDisplace(RefreshLayoutHook hook) {

    }
}
