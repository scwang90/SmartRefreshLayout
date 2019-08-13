package com.scwang.smartrefresh.layout.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smartrefresh.layout.api.RefreshInternal;

/**
 * Internal 初步实现
 * 实现 Header 和 Footer 时，继承 InternalAbstract 的话可以少写很多接口方法
 * Created by scwang on 2018/2/6.
 */
public abstract class InternalAbstract extends SimpleComponent {


    protected InternalAbstract(@NonNull View wrapped) {
        super(wrapped);
    }

    protected InternalAbstract(@NonNull View wrappedView, @Nullable RefreshInternal wrappedInternal) {
        super(wrappedView, wrappedInternal);
    }

    protected InternalAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
