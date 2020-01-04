package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * WaterDropHeader
 * Created by scwang on 2017/5/31.
 * from https://github.com/THEONE10211024/WaterDropListView
 */
public class WaterDropHeader extends com.scwang.smart.refresh.header.WaterDropHeader implements RefreshHeader {

    //<editor-fold desc="ViewGroup">
    public WaterDropHeader(Context context) {
        this(context, null);
    }

    public WaterDropHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}