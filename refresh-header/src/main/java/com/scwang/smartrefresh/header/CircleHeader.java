package com.scwang.smartrefresh.header;

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
    private float mPercent;
    private float mWaveHeight;
    private float mHeadHeight;
    private float mSpringDelta;


    private RefreshState mState;


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

        deawSpringUp(canvas, viewWidth);
    }

    private void drawWave(Canvas canvas, int viewWidth, int viewHeight) {
        float baseHeight = Math.min(mHeadHeight, viewHeight);
        mPath.reset();
        mPath.lineTo(viewWidth, 0);
        mPath.lineTo(viewWidth, baseHeight);
        mPath.quadTo(viewWidth / 2, baseHeight + mWaveHeight * 2, 0, baseHeight);
        mPath.close();
        canvas.drawPath(mPath, mBackPaint);
    }

    private void deawSpringUp(Canvas canvas, int viewWidth) {
        float radus = mHeadHeight / 6;
        if (mSpringDelta > 0) {
            int leftX = (int) (viewWidth / 2 - 2 * radus + getSprRatio() * radus);
            mPath.reset();
            mPath.moveTo(leftX, curH);
            mPath.quadTo(mWidth / 2, curH - mRadius * getSprRatio() * 2,
                    mWidth - leftX, curH);
            canvas.drawPath(mPath, mBallPaint);
        }
    }

    //</editor-fold>



    //<editor-fold desc="SizeObserver">
    @Override
    public void onSizeDefined(RefreshLayout layout, int height, int extendHeight) {
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mPercent = percent;
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
        ValueAnimator waveAnimator = ValueAnimator.ofFloat(
                mWaveHeight, 0,
                -(mHeadHeight*0.5f),0,
                -(mHeadHeight*0.3f),0,
                -(mWaveHeight*0.1f),0);
        waveAnimator.addUpdateListener(animation -> {
            mWaveHeight = (float)animation.getAnimatedValue();
            invalidate();
        });
        waveAnimator.setInterpolator(new DecelerateInterpolator());
        waveAnimator.setDuration(1000);
        waveAnimator.start();

        ValueAnimator springAinAnimator = ValueAnimator.ofFloat(mHeadHeight, mHeadHeight / 2);
        springAinAnimator.addUpdateListener(animation -> {
            mSpringDelta = (float)animation.getAnimatedValue();
        });
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