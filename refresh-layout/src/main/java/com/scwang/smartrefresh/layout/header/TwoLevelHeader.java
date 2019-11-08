package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.OnTwoLevelListener;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * 二级刷新
 * Created by scwang on 2017/5/26.
 */
@SuppressWarnings("UnusedReturnValue")
public class TwoLevelHeader extends com.scwang.smart.refresh.header.TwoLevelHeader implements RefreshHeader {


    //<editor-fold desc="构造方法">
    public TwoLevelHeader(Context context) {
        this(context, null);
    }

    public TwoLevelHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>

    public TwoLevelHeader setOnTwoLevelListener(final OnTwoLevelListener listener) {
        super.setOnTwoLevelListener(new com.scwang.smart.refresh.header.listener.OnTwoLevelListener() {
            @Override
            public boolean onTwoLevel(@NonNull com.scwang.smart.refresh.layout.api.RefreshLayout refreshLayout) {
                return listener.onTwoLevel((RefreshLayout) refreshLayout);
            }
        });
        return this;
    }

    @Override
    public TwoLevelHeader setRefreshHeader(com.scwang.smart.refresh.layout.api.RefreshHeader header) {
        super.setRefreshHeader(header);
        return this;
    }

    @Override
    public TwoLevelHeader setRefreshHeader(com.scwang.smart.refresh.layout.api.RefreshHeader header, int width, int height) {
        super.setRefreshHeader(header, width, height);
        return this;
    }

    @Override
    public TwoLevelHeader setMaxRate(float rate) {
        super.setMaxRate(rate);
        return this;
    }

    @Override
    public TwoLevelHeader setEnablePullToCloseTwoLevel(boolean enabled) {
        super.setEnablePullToCloseTwoLevel(enabled);
        return this;
    }

    @Override
    public TwoLevelHeader setFloorRate(float rate) {
        super.setFloorRate(rate);
        return this;
    }

    @Override
    public TwoLevelHeader setRefreshRate(float rate) {
        super.setRefreshRate(rate);
        return this;
    }

    @Override
    public TwoLevelHeader setEnableTwoLevel(boolean enabled) {
        super.setEnableTwoLevel(enabled);
        return this;
    }

    @Override
    public TwoLevelHeader setFloorDuration(int duration) {
        super.setFloorDuration(duration);
        return this;
    }
}