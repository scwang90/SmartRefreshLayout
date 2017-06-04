package com.scwang.smartrefreshlayout.api;

/**
 * 尺寸订阅接口
 * Created by SCWANG on 2017/6/1.
 */

public interface SizeObserver {
    void onSizeDefined(int height, int extendHeight);
}
