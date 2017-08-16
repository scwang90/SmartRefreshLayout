package com.scwang.smartrefresh.header;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * CircleRefresh
 * https://github.com/tuesda/CircleRefreshLayout
 * Created by zhanglei on 15/7/18.
 */
public class CircleHeader extends View implements RefreshHeader {

    //<editor-fold desc="Field">

    private static final int DURATION_FINISH = 800; //动画时长

    private Path mPath;
    private Paint mBackPaint;
    private Paint mFrontPaint;
    private Paint mOuterPaint;
    private float mWaveHeight;
    private float mHeadHeight;
    private float mSpringRatio;
    private float mFinishRatio;

    private RefreshState mState;
    private float mBollY;//弹出球体的Y坐标
    private boolean mShowBoll;//是否显示中心球体
    private boolean mShowBollTail;//是否显示球体拖拽的尾巴
    private boolean mShowOuter;
    private float mBollRadius;//球体半径

    private int mRefreshStop = 90;
    private int mRefreshStart = 90;
    private boolean mOuterIsStart = true;

    private static final int TARGET_DEGREE = 270;

    //</editor-fold>

    //<editor-fold desc="View">

    public CircleHeader(Context context) {
        super(context, null, 0);
        initView(context, null);
    }

    public CircleHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView(context, attrs);
    }

    public CircleHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setMinimumHeight(DensityUtil.dp2px(100));
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
        mOuterPaint.setStrokeWidth(DensityUtil.dp2px(2f));
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    //</editor-fold>

    //<editor-fold desc="Draw">
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            mShowBoll = true;
            mShowOuter = true;
            mHeadHeight = getHeight();
            mRefreshStop = 270;
            mBollY = mHeadHeight / 2;
            mBollRadius = mHeadHeight / 6;
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        drawWave(canvas, viewWidth, viewHeight);
        drawSpringUp(canvas, viewWidth);
        drawBoll(canvas, viewWidth);
        drawOuter(canvas, viewWidth);
        drawFinish(canvas, viewWidth);
    }

    private void drawWave(Canvas canvas, int viewWidth, int viewHeight) {
        float baseHeight = Math.min(mHeadHeight, viewHeight);
        if (mWaveHeight != 0) {
            mPath.reset();
            mPath.lineTo(viewWidth, 0);
            mPath.lineTo(viewWidth, baseHeight);
            mPath.quadTo(viewWidth / 2, baseHeight + mWaveHeight * 2, 0, baseHeight);
            mPath.close();
            canvas.drawPath(mPath, mBackPaint);
        } else {
            canvas.drawRect(0, 0, viewWidth, baseHeight, mBackPaint);
        }
    }

    private void drawSpringUp(Canvas canvas, int viewWidth) {
        if (mSpringRatio > 0) {
            float leftX = (viewWidth / 2 - 4 * mBollRadius + mSpringRatio * 3 * mBollRadius);
            if (mSpringRatio < 0.9) {
                mPath.reset();
                mPath.moveTo(leftX, mBollY);
                mPath.quadTo(viewWidth / 2, mBollY - mBollRadius * mSpringRatio * 2,
                        viewWidth - leftX, mBollY);
                canvas.drawPath(mPath, mFrontPaint);
            } else {
                canvas.drawCircle(viewWidth / 2, mBollY, mBollRadius, mFrontPaint);
            }
        }
    }

    private void drawBoll(Canvas canvas, int viewWidth) {
        if (mShowBoll) {
            canvas.drawCircle(viewWidth / 2, mBollY, mBollRadius, mFrontPaint);

            drawBollTail(canvas, viewWidth, (mHeadHeight + mWaveHeight) / mHeadHeight);
        }
    }

    private void drawBollTail(Canvas canvas, int viewWidth, float fraction) {
        if (mShowBollTail) {
            final float bottom = mHeadHeight + mWaveHeight;
            final float starty = mBollY + mBollRadius * fraction / 2;
            final float startx = viewWidth / 2 + (float) Math.sqrt(mBollRadius * mBollRadius * (1 - fraction * fraction / 4));
            final float bezier1x = (viewWidth / 2 + (mBollRadius * 3 / 4) * (1 - fraction));
            final float bezier2x = bezier1x + mBollRadius;

            mPath.reset();
            mPath.moveTo(startx, starty);
            mPath.quadTo(bezier1x, bottom, bezier2x, bottom);
            mPath.lineTo(viewWidth - bezier2x, bottom);
            mPath.quadTo(viewWidth - bezier1x, bottom, viewWidth - startx, starty);
            canvas.drawPath(mPath, mFrontPaint);
        }
    }

    private void drawOuter(Canvas canvas, int viewWidth) {
        if (mShowOuter) {
            float outerR = mBollRadius + mOuterPaint.getStrokeWidth() * 2;

            mRefreshStart += mOuterIsStart ? 3 : 10;
            mRefreshStop += mOuterIsStart ? 10 : 3;
            mRefreshStart = mRefreshStart % 360;
            mRefreshStop = mRefreshStop % 360;

            int swipe = mRefreshStop - mRefreshStart;
            swipe = swipe < 0 ? swipe + 360 : swipe;

            canvas.drawArc(new RectF(viewWidth / 2 - outerR, mBollY - outerR, viewWidth / 2 + outerR, mBollY + outerR),
                    mRefreshStart, swipe, false, mOuterPaint);
            if (swipe >= TARGET_DEGREE) {
                mOuterIsStart = false;
            } else if (swipe <= 10) {
                mOuterIsStart = true;
            }
            invalidate();
        }

    }

    private void drawFinish(Canvas canvas, int viewWidth) {
        if (mFinishRatio > 0) {
            int beforeColor = mOuterPaint.getColor();
            if (mFinishRatio < 0.3) {
                canvas.drawCircle(viewWidth / 2, mBollY, mBollRadius, mFrontPaint);
                int outerR = (int) (mBollRadius + mOuterPaint.getStrokeWidth() * 2 * (1+mFinishRatio / 0.3f));
                int afterColor = Color.argb((int) (0xff * (1 - mFinishRatio / 0.3f)), Color.red(beforeColor),
                        Color.green(beforeColor), Color.blue(beforeColor));
                mOuterPaint.setColor(afterColor);
                canvas.drawArc(new RectF(viewWidth / 2 - outerR, mBollY - outerR, viewWidth / 2 + outerR, mBollY + outerR),
                        0, 360, false, mOuterPaint);
            }
            mOuterPaint.setColor(beforeColor);


            if (mFinishRatio >= 0.3 && mFinishRatio < 0.7) {
                float fraction = (mFinishRatio - 0.3f) / 0.4f;
                mBollY = (int) (mHeadHeight / 2 + (mHeadHeight - mHeadHeight / 2) * fraction);
                canvas.drawCircle(viewWidth / 2, mBollY, mBollRadius, mFrontPaint);
                if (mBollY >= mHeadHeight - mBollRadius * 2) {
                    mShowBollTail = true;
                    drawBollTail(canvas, viewWidth, fraction);
                }
                mShowBollTail = false;
            }

            if (mFinishRatio >= 0.7 && mFinishRatio <= 1) {
                float fraction = (mFinishRatio - 0.7f) / 0.3f;
                int leftX = (int) (viewWidth / 2 - mBollRadius - 2 * mBollRadius * fraction);
                mPath.reset();
                mPath.moveTo(leftX, mHeadHeight);
                mPath.quadTo(viewWidth / 2, mHeadHeight - (mBollRadius * (1 - fraction)),
                        viewWidth - leftX, mHeadHeight);
                canvas.drawPath(mPath, mFrontPaint);
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mHeadHeight = headHeight;
        mWaveHeight = Math.max(offset - headHeight, 0) * .8f;
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        if (mState != RefreshState.Refreshing) {
            onPullingDown(percent, offset, headHeight, extendHeight);
        }
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mState = newState;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mHeadHeight = headHeight;
        mBollRadius = headHeight / 6;
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
            int springstatus = 0;//0 还没开始弹起 1 向上弹起 2 在弹起的最高点停住
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                if (springstatus == 0 && curValue <= 0) {
                    springstatus = 1;
                    speed = Math.abs(curValue - mWaveHeight);
                }
                if (springstatus == 1) {
                    springRatio = -curValue / reboundHeight;
                    if (springRatio >= mSpringRatio) {
                        mSpringRatio = springRatio;
                        mBollY = mHeadHeight + curValue;
                        speed = Math.abs(curValue - mWaveHeight);
                    } else {
                        springstatus = 2;
                        mSpringRatio = 0;
                        mShowBoll = true;
                        mShowBollTail = true;
                        springBollY = mBollY;
                    }
                }
                if (springstatus == 2) {
                    if (mBollY > mHeadHeight / 2) {
                        mBollY = Math.max(mHeadHeight / 2, mBollY - speed);
                        float bolly = animation.getAnimatedFraction() * (mHeadHeight / 2 - springBollY) + springBollY;
                        if (mBollY > bolly) {
                            mBollY = bolly;
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
                mWaveHeight = curValue;
                CircleHeader.this.invalidate();
            }
        });
        waveAnimator.setInterpolator(interpolator);
        waveAnimator.setDuration(1000);
        waveAnimator.start();
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mShowOuter = false;
        mShowBoll = false;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFinishRatio = (float) animation.getAnimatedValue();
                CircleHeader.this.invalidate();
            }
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(DURATION_FINISH);
        animator.start();
        return DURATION_FINISH;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            mBackPaint.setColor(colors[0]);
            if (colors.length > 1) {
                mFrontPaint.setColor(colors[1]);
                mOuterPaint.setColor(colors[1]);
            }
        }
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
    //</editor-fold>
}