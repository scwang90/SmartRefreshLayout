package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.api.RefreshHeader;

import java.text.DateFormat;
import java.util.Date;

/**
 * 经典下拉头部-兼容【1.x】版本-包名位置
 * Created by scwang on 2017/5/28.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClassicsHeader extends com.scwang.smart.refresh.header.ClassicsHeader implements RefreshHeader {

    public ClassicsHeader(Context context) {
        super(context);
    }

    public ClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ClassicsHeader setLastUpdateTime(Date time) {
        super.setLastUpdateTime(time);
        return this;
    }

    @Override
    public ClassicsHeader setTimeFormat(DateFormat format) {
        super.setTimeFormat(format);
        return this;
    }

    @Override
    public ClassicsHeader setLastUpdateText(CharSequence text) {
        super.setLastUpdateText(text);
        return this;
    }

    @Override
    public ClassicsHeader setAccentColor(int accentColor) {
        super.setAccentColor(accentColor);
        return this;
    }

    @Override
    public ClassicsHeader setEnableLastTime(boolean enable) {
        super.setEnableLastTime(enable);
        return this;
    }

    @Override
    public ClassicsHeader setTextSizeTime(float size) {
        super.setTextSizeTime(size);
        return this;
    }

    @Override
    public ClassicsHeader setTextSizeTime(int unit, float size) {
        super.setTextSizeTime(unit, size);
        return this;
    }

    @Override
    public ClassicsHeader setTextTimeMarginTop(float dp) {
        super.setTextTimeMarginTop(dp);
        return this;
    }

    @Override
    public ClassicsHeader setTextTimeMarginTopPx(int px) {
        super.setTextTimeMarginTopPx(px);
        return this;
    }

    @Override
    public void setPrimaryColors(int... colors) {
        super.setPrimaryColors(colors);
    }

    @Override
    public ClassicsHeader setProgressBitmap(Bitmap bitmap) {
        super.setProgressBitmap(bitmap);
        return this;
    }

    @Override
    public ClassicsHeader setProgressDrawable(Drawable drawable) {
        super.setProgressDrawable(drawable);
        return this;
    }

    @Override
    public ClassicsHeader setProgressResource(int resId) {
        super.setProgressResource(resId);
        return this;
    }

    @Override
    public ClassicsHeader setArrowBitmap(Bitmap bitmap) {
        super.setArrowBitmap(bitmap);
        return this;
    }

    @Override
    public ClassicsHeader setArrowDrawable(Drawable drawable) {
        super.setArrowDrawable(drawable);
        return this;
    }

    @Override
    public ClassicsHeader setArrowResource(int resId) {
        super.setArrowResource(resId);
        return this;
    }

    @Override
    public ClassicsHeader setSpinnerStyle(SpinnerStyle style) {
        super.setSpinnerStyle(style);
        return this;
    }

    @Override
    public ClassicsHeader setPrimaryColor(int primaryColor) {
        super.setPrimaryColor(primaryColor);
        return this;
    }

    @Override
    public ClassicsHeader setPrimaryColorId(int colorId) {
        super.setPrimaryColorId(colorId);
        return this;
    }

    @Override
    public ClassicsHeader setAccentColorId(int colorId) {
        super.setAccentColorId(colorId);
        return this;
    }

    @Override
    public ClassicsHeader setFinishDuration(int delay) {
        super.setFinishDuration(delay);
        return this;
    }

    @Override
    public ClassicsHeader setTextSizeTitle(float size) {
        super.setTextSizeTitle(size);
        return this;
    }

    @Override
    public ClassicsHeader setTextSizeTitle(int unit, float size) {
        super.setTextSizeTitle(unit, size);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableMarginRight(float dp) {
        super.setDrawableMarginRight(dp);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableMarginRightPx(int px) {
        super.setDrawableMarginRightPx(px);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableSize(float dp) {
        super.setDrawableSize(dp);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableSizePx(int px) {
        super.setDrawableSizePx(px);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableArrowSize(float dp) {
        super.setDrawableArrowSize(dp);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableArrowSizePx(int px) {
        super.setDrawableArrowSizePx(px);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableProgressSize(float dp) {
        super.setDrawableProgressSize(dp);
        return this;
    }

    @Override
    public ClassicsHeader setDrawableProgressSizePx(int px) {
        super.setDrawableProgressSizePx(px);
        return this;
    }
}
