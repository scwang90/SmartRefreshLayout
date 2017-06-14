package com.scwang.smartrefresh.layout.listener;


import com.scwang.smartrefresh.layout.constant.RefreshState;

/**
 * 刷新状态改变监听器
 * Created by SCWANG on 2017/5/26.
 */

public interface OnStateChangedListener {
    void onStateChanged(RefreshState state);
}
