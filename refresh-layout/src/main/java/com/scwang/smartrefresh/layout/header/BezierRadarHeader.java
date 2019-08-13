package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * 贝塞尔曲线类雷达风格刷新组件
 * Created by scwang on 2017/5/28.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class BezierRadarHeader extends com.scwang.smart.refresh.header.BezierRadarHeader implements RefreshHeader {

    //<editor-fold desc="FrameLayout">
    public BezierRadarHeader(Context context) {
        this(context,null);
    }

    public BezierRadarHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>

    @Override
    public BezierRadarHeader setPrimaryColor(int color) {
        super.setPrimaryColor(color);
        return this;
    }

    @Override
    public BezierRadarHeader setAccentColor(int color) {
        super.setAccentColor(color);
        return this;
    }

    @Override
    public BezierRadarHeader setPrimaryColorId(int colorId) {
        super.setPrimaryColorId(colorId);
        return this;
    }

    @Override
    public BezierRadarHeader setAccentColorId(int colorId) {
        super.setAccentColorId(colorId);
        return this;
    }

    @Override
    public BezierRadarHeader setEnableHorizontalDrag(boolean enable) {
        super.setEnableHorizontalDrag(enable);
        return this;
    }
}
