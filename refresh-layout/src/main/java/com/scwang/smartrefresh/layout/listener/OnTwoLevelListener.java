package com.scwang.smartrefresh.layout.listener;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * 二级刷新监听器
 * Created by SCWANG on 2017/5/26.
 */

public interface OnTwoLevelListener {
    /**
     * 二级刷新触发
     * @param refreshLayout 刷新布局
     * @return true 将会展开二楼状态 false 关闭刷新
     */
    boolean onTwoLevel(RefreshLayout refreshLayout);
}
