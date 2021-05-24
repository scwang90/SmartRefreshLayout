package com.scwang.smartrefresh.layout.api;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;


/**
 * 刷新内部组件
 * Created by scwang on 2017/5/26.
 */
public interface RefreshInternal extends RefreshComponent {

    /**
     * 获取变换方式 {@link SpinnerStyle} 必须返回 非空
     * @return 变换方式
     */
    @NonNull
    SpinnerStyle getSpinnerStyle();
}
