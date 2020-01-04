package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * Taurus
 * Created by scwang on 2017/5/31.
 * from https://github.com/Yalantis/Taurus
 */
public class TaurusHeader extends com.scwang.smart.refresh.header.TaurusHeader implements RefreshHeader {

    //<editor-fold desc="View">
    public TaurusHeader(Context context) {
        this(context, null);
    }

    public TaurusHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
