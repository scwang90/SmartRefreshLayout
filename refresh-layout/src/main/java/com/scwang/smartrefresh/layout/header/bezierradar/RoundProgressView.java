package com.scwang.smartrefresh.layout.header.bezierradar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.scwang.smartrefresh.layout.util.DensityUtil;


/**
 * 中心圆形加载进度视图
 * Created by Administrator on 2015/8/27.
 */
public class RoundProgressView extends View {

    private Paint mPaint;
    private Paint mPaintR;
    private int endAngle = 0;
    private int startAngle = 270;
    private int mRadius = 0;
    private int mOutsideCircle = 0;
    private RectF mRect = new RectF(0,0,0,0);
    public ValueAnimator mAnimator;

    public RoundProgressView(Context context) {
        super(context);

        mPaint = new Paint();
        mPaintR = new Paint();
        mPaint.setAntiAlias(true);
        mPaintR.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaintR.setColor(0x55000000);

        DensityUtil density = new DensityUtil();
        mRadius = density.dip2px(20);
        mOutsideCircle = density.dip2px(7);
        mPaint.setStrokeWidth(density.dip2px(3));
        mPaintR.setStrokeWidth(density.dip2px(3));

        mAnimator = ValueAnimator.ofInt(0,360);
        mAnimator.setDuration(720);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                endAngle = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    public void setBackColor(@ColorInt int backColor) {
        mPaintR.setColor(backColor&0x00ffffff|0x55000000);
    }

    public void setFrontColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        if (isInEditMode()) {
            startAngle = 0;
            endAngle = 270;
        }

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mRadius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);//设置为空心
        canvas.drawCircle(width / 2, height / 2, mRadius + mOutsideCircle, mPaint);

        mPaintR.setStyle(Paint.Style.FILL);
        mRect.set(width/2- mRadius, height/2- mRadius, width/2+ mRadius, height/2+ mRadius);
        canvas.drawArc(mRect, startAngle, endAngle, true, mPaintR);

        mRadius += mOutsideCircle;
        mPaintR.setStyle(Paint.Style.STROKE);
        mRect.set(width/2- mRadius, height/2- mRadius, width/2+ mRadius, height/2+ mRadius);
        canvas.drawArc(mRect, startAngle, endAngle, false, mPaintR);
        mRadius -= mOutsideCircle;
    }

}
