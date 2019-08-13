package com.scwang.smartrefresh.header;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.scwang.smart.refresh.header.storehouse.StoreHouseBarItem;
import com.scwang.smart.refresh.header.storehouse.StoreHousePath;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.SmartUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * StoreHouseHeader
 * Created by SCWANG on 2017/5/31.
 * from https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
 */
public class StoreHouseHeader extends com.scwang.smart.refresh.header.StoreHouseHeader {

    //<editor-fold desc="View">
    public StoreHouseHeader(Context context) {
        this(context, null);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}