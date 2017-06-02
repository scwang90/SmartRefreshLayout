package com.scwang.smartrefreshlayout.api;

import android.support.annotation.NonNull;
import android.view.View;

import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.listener.OnStateChangedListener;


/**
 * 刷新内部组件
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshInternal extends OnStateChangedListener {
    void onFinish();
    void setPrimaryColors(int... colors);
    @NonNull
    View getView();
    SpinnerStyle getSpinnerStyle();
}
