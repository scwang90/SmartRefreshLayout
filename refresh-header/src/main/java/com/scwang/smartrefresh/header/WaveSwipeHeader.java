package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * 全屏水滴下拉头
 * Created by scwang on 2017/6/4.
 * from https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout
 */
public class WaveSwipeHeader extends com.scwang.smart.refresh.header.WaveSwipeHeader implements RefreshHeader {

    //<editor-fold desc="DropHeader">
    public WaveSwipeHeader(Context context) {
        this(context, null);
    }

    public WaveSwipeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
