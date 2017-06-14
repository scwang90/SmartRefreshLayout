package com.scwang.smartrefresh.layout.api;

/**
 * 尺寸订阅接口
 * Created by SCWANG on 2017/6/1.
 */

public interface SizeObserver {
    void onSizeDefined(RefreshLayout layout, int height, int extendHeight);
}
