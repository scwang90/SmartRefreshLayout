package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * Material 主题下拉头
 * Created by scwang on 2017/6/2.
 */
public class MaterialHeader extends com.scwang.smart.refresh.header.MaterialHeader implements RefreshHeader {

    //<editor-fold desc="MaterialHeader">
    public MaterialHeader(Context context) {
        this(context, null);
    }

    public MaterialHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
