package com.scwang.smartrefresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.SizeObserver;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * CircleRefresh
 * @link https://github.com/tuesda/CircleRefreshLayout
 * Created by zhanglei on 15/7/18.
 */
public class CircleHeader extends View implements RefreshHeader, SizeObserver {

    //<editor-fold desc="Field">

    private Path mPath;
    private Paint mBackPaint;
    private Paint mFrontPaint;
    private Paint mOuterPaint;
    private float mWaveHeight;
    private float mHeadHeight;
    private float mSpringRatio;


    private RefreshState mState;
    private float mBollY;//弹出球体的Y坐标
    private boolean mShowBoll;//是否显示中心球体
    private boolean mShowBollTail;//是否显示球体拖拽的尾巴
    private int mBollRadius;//球体半径


    //</editor-fold>

    //<editor-fold desc="View">

    public CircleHeader(Context context) {
        this(context, null, 0);
    }

    public CircleHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mOuterPaint.setStrokeWidth(5);
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

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        drawWave(canvas, viewWidth, viewHeight);
        drawSpringUp(canvas, viewWidth);
        drawBoll(canvas, viewWidth);
    }

    private void drawBoll(Canvas canvas, int viewWidth) {
        if (mShowBoll) {
            canvas.drawCircle(viewWidth / 2, mBollY, mBollRadius, mFrontPaint);

            drawBollTail(canvas, viewWidth);
        }
    }

    private void drawBollTail(Canvas canvas, int viewWidth) {
        if (mShowBollTail) {
            final float bottom = mHeadHeight;
            final float fraction = (mHeadHeight + mWaveHeight) / mHeadHeight;
            final float bezierw = (viewWidth / 2 + (mBollRadius * 3 / 4) * (1 - fraction));
            final float starty = mBollY + mBollRadius * fraction;
            final float startx = viewWidth / 2 + (float) Math.sqrt(mBollRadius * mBollRadius * (1 - fraction * fraction));
            final float bezier1x = (viewWidth / 2 + (mBollRadius * 3 / 4) * (1 - fraction));
            final float bezier2x = bezierw + mBollRadius / 2;

            mPath.reset();
            mPath.moveTo(startx, starty);
            mPath.quadTo(bezier1x, bottom, bezier2x, bottom);
            mPath.lineTo(viewWidth - bezier2x, bottom);
            mPath.quadTo(viewWidth - bezier1x, bottom, viewWidth - startx, starty);
            canvas.drawPath(mPath, mFrontPaint);
        }
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
            mBollY = mHeadHeight + mWaveHeight;
            if (mSpringRatio < 0.9) {
                mPath.reset();
                mPath.moveTo(leftX, mBollY);
                mPath.quadTo(viewWidth / 2, mBollY - mBollRadius * mSpringRatio * 2,
                        viewWidth - leftX, mBollY);
                canvas.drawPath(mPath, mFrontPaint);
            } else {
//                canvas.drawArc(new RectF(viewWidth / 2 - radus, mBollY - radus, viewWidth / 2 + radus, curH + radus),
//                        180, 180, true, mFrontPaint);
                canvas.drawCircle(viewWidth / 2, mBollY, mBollRadius, mFrontPaint);
            }
        }
    }

    //</editor-fold>



    //<editor-fold desc="SizeObserver">
    @Override
    public void onSizeDefined(RefreshLayout layout, int height, int extendHeight) {
        layout.setOnRefreshListener(null);
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

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
    public void onStateChanged(RefreshState state) {
        mState = state;
    }

    @Override
    public void startAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mHeadHeight = headHeight;
        mBollRadius = headHeight / 6;
        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        float reboundHeight = Math.min(mWaveHeight * 0.9f, mHeadHeight / 2);
        ValueAnimator waveAnimator = ValueAnimator.ofFloat(
                mWaveHeight, 0,
                -(reboundHeight*1.0f),0,
                -(reboundHeight*0.6f),0,
                -(reboundHeight*0.3f),0
        );
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float speed = 0;
            float springRatio = 0;
            int springstatus = 0;//0 还没开始弹起 1 向上弹起 2 在弹起的最高点停住
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                if (springstatus == 0 && mWaveHeight <= 0) {
                    springstatus = 1;
                    speed = Math.abs(curValue - mWaveHeight);
                }
                if (springstatus == 1) {
                    springRatio = -mWaveHeight / reboundHeight;
                    if (springRatio >= mSpringRatio) {
                        mSpringRatio = springRatio;
                        speed = Math.abs(curValue - mWaveHeight);
                    } else {
                        springstatus = 2;
                        mSpringRatio = 0;
                        mShowBoll = true;
                        mShowBollTail = true;
                    }
                }
                if (springstatus == 2) {
                    if (mBollY > mHeadHeight / 2) {
                        mBollY = Math.max(mHeadHeight / 2, mBollY - speed);
                    }
                }
                if (mShowBollTail && curValue < mWaveHeight) {
                    mShowBollTail = false;
                    startBollAnimator();
                }
                mWaveHeight = curValue;
                CircleHeader.this.invalidate();
            }
        });
        waveAnimator.setInterpolator(interpolator);
        waveAnimator.setDuration(3000);
        waveAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout.finisRefresh();
            }
        });
        waveAnimator.start();
    }

    private void startBollAnimator() {

    }

    @Override
    public void onFinish(RefreshLayout layout) {
    }

    @Override
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