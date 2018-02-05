package com.scwang.smartrefresh.layout.internal;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * 箭头图像
 * Created by SCWANG on 2018/2/5.
 */

public class ArrowDrawable extends Drawable {

    private int mWidth = 0;
    private int mHeight = 0;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();

    public ArrowDrawable() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xff11bbff);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        if (mWidth != width || mHeight != height) {
            int lineWidth = width * 30 / 225;
            mPath.reset();

            float vector1 = (float) (lineWidth * Math.sin(Math.PI/4));
            float vector2 = (float) (lineWidth / Math.sin(Math.PI/4));
            mPath.moveTo(width / 2, height);
            mPath.lineTo(0, height / 2);
            mPath.lineTo(vector1, height / 2 - vector1);
            mPath.lineTo(width / 2 - lineWidth / 2, height - vector2 - lineWidth / 2);
            mPath.lineTo(width / 2 - lineWidth / 2, 0);
            mPath.lineTo(width / 2 + lineWidth / 2, 0);
            mPath.lineTo(width / 2 + lineWidth / 2, height - vector2 - lineWidth / 2);
            mPath.lineTo(width - vector1, height / 2 - vector1);
            mPath.lineTo(width, height / 2);
            mPath.close();

            mWidth = width;
            mHeight = height;
        }
        canvas.drawPath(mPath, mPaint);
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
