package com.scwang.smart.refresh.header;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.util.SmartUtil;

/**
 * CircleRefresh
 * Created by scwang on 2018/7/18.
 * from https://github.com/tuesda/CircleRefreshLayout
 */
public class BezierCircleHeader extends SimpleComponent implements RefreshHeader {

    //<editor-fold desc="Field">
    protected Path mPath;
    protected Paint mBackPaint;
    protected Paint mFrontPaint;
    protected Paint mOuterPaint;
    protected int mHeight;
    protected float mWaveHeight;
    protected float mHeadHeight;
    protected float mSpringRatio;
    protected float mFinishRatio;

    protected float mBollY;//弹出球体的Y坐标
    protected float mBollRadius;//球体半径
    protected boolean mShowOuter;
    protected boolean mShowBoll;//是否显示中心球体
    protected boolean mShowBollTail;//是否显示球体拖拽的尾巴

    protected int mRefreshStop = 90;
    protected int mRefreshStart = 90;
    protected boolean mOuterIsStart = true;

    protected static final int TARGET_DEGREE = 270;
    protected boolean mWavePulling = false;
    protected RefreshKernel mKernel;
    //</editor-fold>

    //<editor-fold desc="View">
    public BezierCircleHeader(Context context) {
        this(context, null);
    }

    public BezierCircleHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        mSpinnerStyle = SpinnerStyle.FixedBehind;
        final View thisView = this;
        thisView.setMinimumHeight(SmartUtil.dp2px(100));
        mBackPaint = new Paint();
        mBackPaint.setColor(0xff11bbff);
        mBackPaint.setAntiAlias(true);
        mFrontPaint = new Paint();
        mFrontPaint.setColor(0xffffffff);
        mFrontPaint.setAntiAlias(true);
        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(0xffffffff);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(SmartUtil.dp2px(2f));
        mPath = new Path();
    }
    //</editor-fold>

    //<editor-fold desc="Draw">
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int viewWidth = thisView.getWidth();
        final int viewHeight = mHeight;//thisView.getHeight();
        //noinspection EqualsBetweenInconvertibleTypes
        final boolean footer = mKernel != null && (this.equals(mKernel.getRefreshLayout().getRefreshFooter()));

        if (footer) {
            canvas.save();
            canvas.translate(0, thisView.getHeight());
            canvas.scale(1, -1);
        }

        if (thisView.isInEditMode()) {
            mShowBoll = true;
            mShowOuter = true;
            mHeadHeight = viewHeight;
            mRefreshStop = 270;
            mBollY = mHeadHeight / 2;
            mBollRadius = mHeadHeight / 6;
        }

        drawWave(canvas, viewWidth, viewHeight);
        drawSpringUp(canvas, viewWidth);
        drawBoll(canvas, viewWidth);
        drawOuter(canvas, viewWidth);
        drawFinish(canvas, viewWidth);

        if (footer) {
            canvas.restore();
        }

        super.dispatchDraw(canvas);
    }

    protected void drawWave(Canvas canvas, int viewWidth, int viewHeight) {
        float baseHeight = Math.min(mHeadHeight, viewHeight);
        if (mWaveHeight != 0) {
            mPath.reset();
            mPath.lineTo(viewWidth, 0);
            mPath.lineTo(viewWidth, baseHeight);
            mPath.quadTo(viewWidth / 2f, baseHeight + mWaveHeight * 2, 0, baseHeight);
            mPath.close();
            canvas.drawPath(mPath, mBackPaint);
        } else {
            canvas.drawRect(0, 0, viewWidth, baseHeight, mBackPaint);
        }
    }

    protected void drawSpringUp(Canvas canvas, int viewWidth) {
        if (mSpringRatio > 0) {
            float leftX = (viewWidth / 2f - 4 * mBollRadius + mSpringRatio * 3 * mBollRadius);
            if (mSpringRatio < 0.9) {
                mPath.reset();
                mPath.moveTo(leftX, mBollY);
                mPath.quadTo(viewWidth / 2f, mBollY - mBollRadius * mSpringRatio * 2,
                        viewWidth - leftX, mBollY);
                canvas.drawPath(mPath, mFrontPaint);
            } else {
                canvas.drawCircle(viewWidth / 2f, mBollY, mBollRadius, mFrontPaint);
            }
        }
    }

    protected void drawBoll(Canvas canvas, int viewWidth) {
        if (mShowBoll) {
            canvas.drawCircle(viewWidth / 2f, mBollY, mBollRadius, mFrontPaint);

            drawBollTail(canvas, viewWidth, (mHeadHeight + mWaveHeight) / mHeadHeight);
        }
    }

    protected void drawBollTail(Canvas canvas, int viewWidth, float fraction) {
        if (mShowBollTail) {
            final float bottom = mHeadHeight + mWaveHeight;
            final float startY = mBollY + mBollRadius * fraction / 2;
            final float startX = viewWidth / 2f + (float) Math.sqrt(mBollRadius * mBollRadius * (1 - fraction * fraction / 4));
            final float bezier1x = (viewWidth / 2f + (mBollRadius * 3 / 4) * (1 - fraction));
            final float bezier2x = bezier1x + mBollRadius;

            mPath.reset();
            mPath.moveTo(startX, startY);
            mPath.quadTo(bezier1x, bottom, bezier2x, bottom);
            mPath.lineTo(viewWidth - bezier2x, bottom);
            mPath.quadTo(viewWidth - bezier1x, bottom, viewWidth - startX, startY);
            canvas.drawPath(mPath, mFrontPaint);
        }
    }

    protected void drawOuter(Canvas canvas, int viewWidth) {
        if (mShowOuter) {
            float outerR = mBollRadius + mOuterPaint.getStrokeWidth() * 2;

            mRefreshStart += mOuterIsStart ? 3 : 10;
            mRefreshStop += mOuterIsStart ? 10 : 3;
            mRefreshStart = mRefreshStart % 360;
            mRefreshStop = mRefreshStop % 360;

            int swipe = mRefreshStop - mRefreshStart;
            swipe = swipe < 0 ? swipe + 360 : swipe;

            canvas.drawArc(new RectF(viewWidth / 2f - outerR, mBollY - outerR, viewWidth / 2f + outerR, mBollY + outerR),
                    mRefreshStart, swipe, false, mOuterPaint);
            if (swipe >= TARGET_DEGREE) {
                mOuterIsStart = false;
            } else if (swipe <= 10) {
                mOuterIsStart = true;
            }
            final View thisView = this;
            thisView.invalidate();
        }

    }

    protected void drawFinish(Canvas canvas, int viewWidth) {
        if (mFinishRatio > 0) {
            int beforeColor = mOuterPaint.getColor();
            if (mFinishRatio < 0.3) {
                canvas.drawCircle(viewWidth / 2f, mBollY, mBollRadius, mFrontPaint);
                int outerR = (int) (mBollRadius + mOuterPaint.getStrokeWidth() * 2 * (1+mFinishRatio / 0.3f));
                int afterColor = ColorUtils.setAlphaComponent(beforeColor, (int) (0xff * (1 - mFinishRatio / 0.3f)));
                mOuterPaint.setColor(afterColor);
                canvas.drawArc(new RectF(viewWidth / 2f - outerR, mBollY - outerR, viewWidth / 2f + outerR, mBollY + outerR),
                        0, 360, false, mOuterPaint);
            }
            mOuterPaint.setColor(beforeColor);

            if (mFinishRatio >= 0.3 && mFinishRatio < 0.7) {
                float fraction = (mFinishRatio - 0.3f) / 0.4f;
                mBollY = (int) (mHeadHeight / 2 + (mHeadHeight - mHeadHeight / 2) * fraction);
                canvas.drawCircle(viewWidth / 2f, mBollY, mBollRadius, mFrontPaint);
                if (mBollY >= mHeadHeight - mBollRadius * 2) {
                    mShowBollTail = true;
                    drawBollTail(canvas, viewWidth, fraction);
                }
                mShowBollTail = false;
            }

            if (mFinishRatio >= 0.7 && mFinishRatio <= 1) {
                float fraction = (mFinishRatio - 0.7f) / 0.3f;
                int leftX = (int) (viewWidth / 2f - mBollRadius - 2 * mBollRadius * fraction);
                mPath.reset();
                mPath.moveTo(leftX, mHeadHeight);
                mPath.quadTo(viewWidth / 2f, mHeadHeight - (mBollRadius * (1 - fraction)),
                        viewWidth - leftX, mHeadHeight);
                canvas.drawPath(mPath, mFrontPaint);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mKernel = kernel;
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        mHeight = offset;
        if (isDragging || mWavePulling) {
            mWavePulling = true;
            mHeadHeight = height;
            mWaveHeight = Math.max(offset - height, 0) * .8f;
        }
        this.invalidate();
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mWavePulling = false;
        mHeadHeight = height;
        mBollRadius = height / 6f;
        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        final float reboundHeight = Math.min(mWaveHeight * 0.8f, mHeadHeight / 2);
        ValueAnimator waveAnimator = ValueAnimator.ofFloat(
                mWaveHeight, 0,
                -(reboundHeight*1.0f),0,
                -(reboundHeight*0.4f),0
        );
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float speed = 0;
            float springBollY;
            float springRatio = 0;
            int status = 0;//0 还没开始弹起 1 向上弹起 2 在弹起的最高点停住
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                if (status == 0 && curValue <= 0) {
                    status = 1;
                    speed = Math.abs(curValue - mWaveHeight);
                }
                if (status == 1) {
                    springRatio = -curValue / reboundHeight;
                    if (springRatio >= mSpringRatio) {
                        mSpringRatio = springRatio;
                        mBollY = mHeadHeight + curValue;
                        speed = Math.abs(curValue - mWaveHeight);
                    } else {
                        status = 2;
                        mSpringRatio = 0;
                        mShowBoll = true;
                        mShowBollTail = true;
                        springBollY = mBollY;
                    }
                }
                if (status == 2) {
                    if (mBollY > mHeadHeight / 2) {
                        mBollY = Math.max(mHeadHeight / 2, mBollY - speed);
                        float bally = animation.getAnimatedFraction() * (mHeadHeight / 2 - springBollY) + springBollY;
                        if (mBollY > bally) {
                            mBollY = bally;
                        }
                    }
                }
                if (mShowBollTail && curValue < mWaveHeight) {
                    mShowOuter = true;
                    mShowBollTail = false;
                    mOuterIsStart = true;
                    mRefreshStart = 90;
                    mRefreshStop = 90;
                }
                if (!mWavePulling) {
                    mWaveHeight = curValue;
                    final View thisView = BezierCircleHeader.this;
                    thisView.invalidate();
                }
            }
        });
        waveAnimator.setInterpolator(interpolator);
        waveAnimator.setDuration(1000);
        waveAnimator.start();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mShowBoll = false;
        mShowOuter = false;
        final int DURATION_FINISH = 800; //动画时长
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final View thisView = BezierCircleHeader.this;
                mFinishRatio = (float) animation.getAnimatedValue();
                thisView.invalidate();
            }
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(DURATION_FINISH);
        animator.start();
        return DURATION_FINISH;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            mBackPaint.setColor(colors[0]);
            if (colors.length > 1) {
                mFrontPaint.setColor(colors[1]);
                mOuterPaint.setColor(colors[1]);
            }
        }
    }

//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return SpinnerStyle.Scale;
//    }
    //</editor-fold>
}