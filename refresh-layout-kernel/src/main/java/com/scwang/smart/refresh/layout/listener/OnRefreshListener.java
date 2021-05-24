package com.scwang.smart.refresh.layout.listener;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * 刷新监听器
 * Created by scwang on 2017/5/26.
 */
public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayout refreshLayout);
}
