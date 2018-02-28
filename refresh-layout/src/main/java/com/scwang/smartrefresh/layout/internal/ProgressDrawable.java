package com.scwang.smartrefresh.layout.internal;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.animation.LinearInterpolator;

/**
 * 旋转动画
 * Created by SCWANG on 2017/6/16.
 */

public class ProgressDrawable extends PaintDrawable implements Animatable {

    private int mWidth = 0;
    private int mHeight = 0;
    private int mProgressDegree = 0;
    private ValueAnimator mValueAnimator;
    private Path mPath = new Path();

    public ProgressDrawable() {
        mValueAnimator = ValueAnimator.ofInt(30, 3600);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mProgressDegree = 30 * (value / 30);
                ProgressDrawable.super.invalidateSelf();
            }
        });
        mValueAnimator.setDuration(10000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    //<editor-fold desc="Drawable">
    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = super.getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        final int r = Math.max(1, width / 20);

        if (mWidth != width || mHeight != height) {
            mPath.reset();
            mPath.addCircle(width - r, height / 2, r, Path.Direction.CW);
            mPath.addRect(width - 5 * r, height / 2 - r, width - r, height / 2 + r, Path.Direction.CW);
            mPath.addCircle(width - 5 * r, height / 2, r, Path.Direction.CW);
            mWidth = width;
            mHeight = height;
        }

        canvas.save();
        canvas.rotate(mProgressDegree, (width) / 2, (height) / 2);
        for (int i = 0; i < 12; i++) {
            mPaint.setAlpha((i+5) * 0x11);
            canvas.rotate(30, (width) / 2, (height) / 2);
            canvas.drawPath(mPath, mPaint);
        }
        canvas.restore();
    }

    //</editor-fold>

    @Override
    public void start() {
        if (!mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
    }

    @Override
    public void stop() {
        if (mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        return mValueAnimator.isRunning();
    }

}
