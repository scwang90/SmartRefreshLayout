package com.scwang.smartrefresh.layout.api;

import android.view.View;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

public interface RefreshScrollBoundary {
    /**
     * 内容是否可以下拉滚动
     * @param content 内容视图
     * @return false 将会触发下拉刷新
     */
    boolean canPullDown(View content);
    /**
     * 内容是否可以上拉滚动
     * @param content 内容视图
     * @return false 将会触发加载更多
     */
    boolean canPullUp(View content);
}
