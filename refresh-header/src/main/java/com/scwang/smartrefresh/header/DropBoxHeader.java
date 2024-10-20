package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * DropBoxRefresh
 * Created by scwang on 2017/6/24.
 * design <a href="https://dribbble.com/shots/3470499-DropBox-Refresh">...</a>
 */
public class DropBoxHeader extends com.scwang.smart.refresh.header.DropBoxHeader implements com.scwang.smart.refresh.layout.api.RefreshHeader {

    //<editor-fold desc="View">
    public DropBoxHeader(Context context) {
        this(context, null);
    }

    public DropBoxHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
