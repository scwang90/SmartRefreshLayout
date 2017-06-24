package com.scwang.smartrefresh.header;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * DropboxRefresh
 * https://dribbble.com/shots/3470499-Dropbox-Refresh
 * Created by SCWANG on 2017/6/24.
 */

public class DropboxHeader extends View implements RefreshHeader {

    private Path mPath;
    private Paint mPaint;
    private int mAccentColor;
    private int mHeaderHeight;

    //<editor-fold desc="View">
    public DropboxHeader(Context context) {
        this(context, null);
    }

    public DropboxHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropboxHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DropboxHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mAccentColor = 0xff6ea9ff;
        setBackgroundColor(0xff283645);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        final int width = getWidth();
        final int height = getHeight();
        Path path = generateBoxBodyPath(width, height);
        mPaint.setColor(ColorUtils.compositeColors(0x55000000, mAccentColor));
        canvas.drawPath(path, mPaint);
    }

    private Path generateBoxBodyPath(int width, int height) {
        mPath.reset();
        final int sideLength = mHeaderHeight / 6;
        final int margin = sideLength / 2;
        final int centerX = width / 2;
        final int boxBottom = height - margin;
        final int boxTop = boxBottom - 2 * sideLength;
        final int boxLeft = centerX - (int)(sideLength * Math.sin(60));
        final int boxCenter = boxBottom - sideLength;
        final int boxCenterTop = boxTop + sideLength / 2;
        final int boxCenterBottom = boxBottom - sideLength / 2;
        final int boxRight = width - boxLeft;

        mPath.moveTo(boxLeft, boxCenterBottom);
        mPath.lineTo(boxCenter, boxBottom);
        mPath.lineTo(boxRight, boxCenterBottom);
        mPath.lineTo(boxRight, boxCenterTop);
        mPath.lineTo(boxCenter, boxTop);
        mPath.lineTo(boxLeft, boxCenterTop);
        mPath.lineTo(boxLeft, boxCenterBottom);
        mPath.close();
        return mPath;
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mHeaderHeight = height;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public void onFinish(RefreshLayout layout) {

    }
    //</editor-fold>
}
