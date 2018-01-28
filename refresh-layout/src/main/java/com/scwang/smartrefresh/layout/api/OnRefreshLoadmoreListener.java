package com.scwang.smartrefresh.layout.api;

import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * 刷新加载组合监听器
 * @deprecated 使用 {@link com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener} 代替
 * Created by SCWANG on 2017/5/26.
 */
@Deprecated
public interface OnRefreshLoadmoreListener extends OnRefreshListener, OnLoadmoreListener {
}
