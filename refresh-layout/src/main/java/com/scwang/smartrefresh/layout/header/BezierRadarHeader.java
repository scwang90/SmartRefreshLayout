package com.scwang.smartrefresh.layout.header;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.scwang.smartrefresh.layout.util.DesignUtil;

/**
 * 贝塞尔曲线类雷达风格刷新组件
 * Created by lcodecore on 2016/10/2.
 */

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class BezierRadarHeader extends InternalAbstract implements RefreshHeader {

    //<editor-fold desc="属性字段">
    protected int mAccentColor;
    protected int mPrimaryColor;
    protected boolean mManualPrimaryColor;
    protected boolean mManualAccentColor;
    protected boolean mWavePulling;
    protected boolean mEnableHorizontalDrag = false;

    protected Path mPath;
    protected Paint mPaint;
    protected int mWaveTop;
    protected int mWaveHeight;
    protected int mWaveOffsetX = -1;

    protected float mDotAlpha;
    protected float mDotFraction;
    protected float mDotRadius;
    protected float mRippleRadius;

    protected int mRadarAngle = 0;
    protected float mRadarRadius = 0;
    protected float mRadarCircle = 0;
    protected float mRadarScale = 0;
    protected AnimatorSet mAnimatorSet;
//    protected ValueAnimator mRadarAnimator;
    protected RectF mRadarRect = new RectF(0,0,0,0);
    //</editor-fold>

    //<editor-fold desc="FrameLayout">
    public BezierRadarHeader(Context context) {
        this(context,null);
    }

    public BezierRadarHeader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BezierRadarHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DensityUtil density = new DensityUtil();

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mDotRadius = density.dip2px(7);
        mRadarRadius = density.dip2px(20);
        mRadarCircle = density.dip2px(7);
        mPaint.setStrokeWidth(density.dip2px(3));

        setMinimumHeight(density.dip2px(100));

        if (isInEditMode()) {
            mWaveTop = 1000;
            mRadarScale = 1;
            mRadarAngle = 270;
        } else {
            mRadarScale = 0;
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BezierRadarHeader);

        mEnableHorizontalDrag = ta.getBoolean(R.styleable.BezierRadarHeader_srlEnableHorizontalDrag, mEnableHorizontalDrag);
        setAccentColor(ta.getColor(R.styleable.BezierRadarHeader_srlAccentColor, 0xFFffffff));
        setPrimaryColor(ta.getColor(R.styleable.BezierRadarHeader_srlPrimaryColor, 0xFF222222));
        mManualAccentColor = ta.hasValue(R.styleable.BezierRadarHeader_srlAccentColor);
        mManualPrimaryColor = ta.hasValue(R.styleable.BezierRadarHeader_srlPrimaryColor);

        ta.recycle();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        if (mRadarAnimator != null) {
//            mRadarAnimator.removeAllListeners();
//            mRadarAnimator.removeAllUpdateListeners();
//            mRadarAnimator.end();
//            mRadarAnimator = null;
//        }
        if (mAnimatorSet != null) {
            mAnimatorSet.removeAllListeners();
            mAnimatorSet.end();
            mAnimatorSet = null;
        }
    }

    //</editor-fold>

    //<editor-fold desc="绘制方法 - draw">
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int width = getWidth(), height = getHeight();
        drawWave(canvas, width);
        drawDot(canvas, width, height);
        drawRadar(canvas, width, height);
        drawRipple(canvas, width, height);
        super.dispatchDraw(canvas);
    }

    protected void drawWave(Canvas canvas, int width) {
        //重置画笔
        mPath.reset();
        //绘制贝塞尔曲线
        mPath.lineTo(0, mWaveTop);
        mPath.quadTo(mWaveOffsetX >= 0 ? (mWaveOffsetX) : width / 2, mWaveTop + mWaveHeight, width, mWaveTop);
        mPath.lineTo(width, 0);
        mPaint.setColor(mPrimaryColor);
        canvas.drawPath(mPath, mPaint);
    }

    protected void drawDot(Canvas canvas, int width, int height) {
        if (mDotAlpha > 0) {
            mPaint.setColor(mAccentColor);
            final int num = 7;
            float x = DensityUtil.px2dp(height);
            float wide = (width / num) * mDotFraction -((mDotFraction >1)?((mDotFraction -1)*(width / num)/ mDotFraction):0);//y1 = t*(w/n)-(t>1)*((t-1)*(w/n)/t)
            float high = height - ((mDotFraction >1)?((mDotFraction -1)*height/2/ mDotFraction):0);//y2 = x - (t>1)*((t-1)*x/t);
            for (int i = 0 ; i < num; i++) {
                float index = 1f + i - (1f + num) / 2;//y3 = (x + 1) - (n + 1)/2; 居中 index 变量：0 1 2 3 4 结果： -2 -1 0 1 2
                float alpha = 255 * (1 - (2 * (Math.abs(index) / num)));//y4 = m * ( 1 - 2 * abs(y3) / n); 横向 alpha 差
                mPaint.setAlpha((int) (mDotAlpha * alpha * (1d - 1d / Math.pow((x / 800d + 1d), 15))));//y5 = y4 * (1-1/((x/800+1)^15));竖直 alpha 差
                float radius = mDotRadius * (1-1/((x/10+1)));//y6 = mDotRadius*(1-1/(x/10+1));半径
                canvas.drawCircle(width / 2- radius/2 + wide * index , high / 2, radius, mPaint);
            }
            mPaint.setAlpha(255);
        }
    }

    protected void drawRadar(Canvas canvas, int width, int height) {
        if (/*mRadarAnimator != null*/mAnimatorSet != null || isInEditMode()) {
            float radius = mRadarRadius * mRadarScale;
            float circle = mRadarCircle * mRadarScale;

            mPaint.setColor(mAccentColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2, height / 2, radius, mPaint);

            mPaint.setStyle(Paint.Style.STROKE);//设置为空心
            canvas.drawCircle(width / 2, height / 2, radius + circle, mPaint);

            mPaint.setColor(mPrimaryColor & 0x00ffffff | 0x55000000);
            mPaint.setStyle(Paint.Style.FILL);
            mRadarRect.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);
            canvas.drawArc(mRadarRect, 270, mRadarAngle, true, mPaint);

            radius += circle;
            mPaint.setStyle(Paint.Style.STROKE);
            mRadarRect.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);
            canvas.drawArc(mRadarRect, 270, mRadarAngle, false, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
        }
    }


    protected void drawRipple(Canvas canvas, int width, int height) {
        if (mRippleRadius > 0) {
            mPaint.setColor(mAccentColor);
            canvas.drawCircle(width / 2, height / 2, mRippleRadius, mPaint);
        }
    }
    //</editor-fold>

    //<editor-fold desc="刷新接口 - RefreshHeader">

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        mWavePulling = true;
        mWaveTop = Math.min(height, offset);
        mWaveHeight = (int) (1.9f * Math.max(0, offset - height));
        mDotFraction = percent;
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        if (mWavePulling) {
            onPulling(percent, offset, height, extendHeight);
        }
    }

    @Override
    public void onReleased(@NonNull final RefreshLayout refreshLayout, int height, int extendHeight) {
        mWaveTop = height;
        mWavePulling = false;

        ValueAnimator mRadarAnimator = ValueAnimator.ofInt(0,360);
        mRadarAnimator.setDuration(720);
        mRadarAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRadarAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRadarAnimator.addUpdateListener(new AnimatorUpdater(PROPERTY_RADAR_ANGLE));

        Interpolator interpolatorDecelerate = new DecelerateInterpolator();
        //圆点消失动画
        ValueAnimator animatorDotAlpha = ValueAnimator.ofFloat(1, 0);
        animatorDotAlpha.setInterpolator(interpolatorDecelerate);
        animatorDotAlpha.addUpdateListener(new AnimatorUpdater(PROPERTY_DOT_ALPHA));
        //雷达出现动画
        ValueAnimator animatorRadarScale = ValueAnimator.ofFloat(0, 1);
        animatorDotAlpha.setInterpolator(interpolatorDecelerate);
        animatorRadarScale.addUpdateListener(new AnimatorUpdater(PROPERTY_RADAR_SCALE));
        //连续动画集
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorDotAlpha, animatorRadarScale, mRadarAnimator);
        animatorSet.start();
        //贝塞尔弹性动画
        ValueAnimator animatorWave = ValueAnimator.ofInt(
                mWaveHeight, 0,
                -(int)(mWaveHeight *0.8f),0,
                -(int)(mWaveHeight *0.4f),0);
        animatorWave.addUpdateListener(new AnimatorUpdater(PROPERTY_WAVE_HEIGHT));
        animatorWave.setInterpolator(interpolatorDecelerate);
        animatorWave.setDuration(800);
        animatorWave.start();

        mAnimatorSet = animatorSet;
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
//        if (mRadarAnimator != null) {
//            mRadarAnimator.end();
//            mRadarAnimator = null;
//        }
        if (mAnimatorSet != null) {
            mAnimatorSet.removeAllListeners();
            mAnimatorSet.end();
            mAnimatorSet = null;
        }

        final int duration = 400;
        final int width = getWidth(), height = getHeight();
        final float bigRadius = (float) (Math.sqrt(width * width + height * height));
        ValueAnimator animator = ValueAnimator.ofFloat(0, bigRadius);
        animator.setDuration(duration);
        animator.addUpdateListener(new AnimatorUpdater(PROPERTY_RIPPLE_RADIUS));
        animator.start();
        return duration;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                mDotAlpha = 1;
                mRadarScale = 0;
                mRippleRadius = 0;
                break;
        }
    }

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0 && !mManualPrimaryColor) {
            setPrimaryColor(colors[0]);
            mManualPrimaryColor = false;
        }
        if (colors.length > 1 && !mManualAccentColor) {
            setAccentColor(colors[1]);
            mManualAccentColor = false;
        }
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return mEnableHorizontalDrag;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        mWaveOffsetX = offsetX;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            invalidate();
        }
    }
    //</editor-fold>

    //<editor-fold desc="开放接口 - API">
    public BezierRadarHeader setPrimaryColor(@ColorInt int color) {
        mPrimaryColor = color;
        mManualPrimaryColor = true;
        return this;
    }

    public BezierRadarHeader setAccentColor(@ColorInt int color) {
        mAccentColor = color;
        mManualAccentColor = true;
        return this;
    }

    public BezierRadarHeader setPrimaryColorId(@ColorRes int colorId) {
        setPrimaryColor(DesignUtil.getColor(getContext(), colorId));
        return this;
    }

    public BezierRadarHeader setAccentColorId(@ColorRes int colorId) {
        setAccentColor(DesignUtil.getColor(getContext(), colorId));
        return this;
    }

    public BezierRadarHeader setEnableHorizontalDrag(boolean enable) {
        this.mEnableHorizontalDrag = enable;
        if (!enable) {
            mWaveOffsetX = -1;
        }
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="动画更新">
    protected static final byte PROPERTY_RADAR_SCALE = 0;
    protected static final byte PROPERTY_WAVE_HEIGHT = 1;
    protected static final byte PROPERTY_DOT_ALPHA = 2;
    protected static final byte PROPERTY_RIPPLE_RADIUS = 3;
    protected static final byte PROPERTY_RADAR_ANGLE = 4;

    protected class AnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {

        byte propertyName;
        AnimatorUpdater(byte name) {
            this.propertyName = name;
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (PROPERTY_RADAR_SCALE == propertyName) {
                mRadarScale = (float) animation.getAnimatedValue();
            } else if (PROPERTY_WAVE_HEIGHT == propertyName) {
                if (mWavePulling) {
                    animation.cancel();
                    return;
                }
                mWaveHeight = (int) animation.getAnimatedValue() / 2;
            } else if (PROPERTY_DOT_ALPHA == propertyName) {
                mDotAlpha = (float) animation.getAnimatedValue();
            } else if (PROPERTY_RIPPLE_RADIUS == propertyName) {
                mRippleRadius = (float) animation.getAnimatedValue();
            } else if (PROPERTY_RADAR_ANGLE == propertyName) {
                mRadarAngle = (int) animation.getAnimatedValue();
            }
            invalidate();
        }
    }
    //</editor-fold>
}
