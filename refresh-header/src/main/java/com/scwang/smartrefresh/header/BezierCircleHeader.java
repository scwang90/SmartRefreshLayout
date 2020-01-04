package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * CircleRefresh
 * Created by scwang on 2018/7/18.
 * from https://github.com/tuesda/CircleRefreshLayout
 */
public class BezierCircleHeader extends com.scwang.smart.refresh.header.BezierCircleHeader implements RefreshHeader {

    //<editor-fold desc="View">
    public BezierCircleHeader(Context context) {
        this(context, null);
    }

    public BezierCircleHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}