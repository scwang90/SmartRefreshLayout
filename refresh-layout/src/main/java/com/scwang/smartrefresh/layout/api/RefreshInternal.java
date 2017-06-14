package com.scwang.smartrefresh.layout.api;

import android.support.annotation.NonNull;
import android.view.View;

import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnStateChangedListener;


/**
 * 刷新内部组件
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshInternal extends OnStateChangedListener {
    void onFinish(RefreshLayout layout);
    void setPrimaryColors(int... colors);
    @NonNull
    View getView();
    SpinnerStyle getSpinnerStyle();
}
