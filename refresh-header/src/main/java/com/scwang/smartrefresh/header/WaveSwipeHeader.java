package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 全屏水滴下拉头
 * Created by scwang on 2017/6/4.
 * from <a href="https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout">...</a>
 */
public class WaveSwipeHeader extends com.scwang.smart.refresh.header.WaveSwipeHeader implements com.scwang.smart.refresh.layout.api.RefreshHeader {

    //<editor-fold desc="DropHeader">
    public WaveSwipeHeader(Context context) {
        this(context, null);
    }

    public WaveSwipeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
