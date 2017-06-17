package com.scwang.smartrefresh.layout.api;

/**
 * 尺寸订阅接口
 * Created by SCWANG on 2017/6/1.
 */

public interface SizeObserver {
    /**
     * 尺寸定义完成
     * @param kernel RefreshKernel
     * @param height HeaderHeight or FooterHeight
     * @param extendHeight extendHeaderHeight or extendFooterHeight
     */
    void onSizeDefined(RefreshKernel kernel, int height, int extendHeight);
}
