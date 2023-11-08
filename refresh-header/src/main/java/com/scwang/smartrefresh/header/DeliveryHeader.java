package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * Refresh your delivery!
 * Created by scwang on 2017/6/25.
 * design <a href="https://dribbble.com/shots/2753803-Refresh-your-delivery">...</a>
 */
public class DeliveryHeader extends com.scwang.smart.refresh.header.DeliveryHeader implements RefreshHeader {

    //<editor-fold desc="View">
    public DeliveryHeader(Context context) {
        this(context, null);
    }

    public DeliveryHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
