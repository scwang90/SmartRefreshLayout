package com.scwang.smartrefresh.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.scwang.smartrefresh.header.internal.MaterialProgressDrawable;
import com.scwang.smartrefresh.header.material.CircleImageView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.View.MeasureSpec.getSize;

/**
 * Material 主题下拉头
 * Created by SCWANG on 2017/6/2.
 */
@SuppressWarnings("unused")
public class MaterialHeader extends InternalAbstract implements RefreshHeader {

    // Maps to ProgressBar.Large style
    public static final int SIZE_LARGE = 0;
    // Maps to ProgressBar default style
    public static final int SIZE_DEFAULT = 1;

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final float MAX_PROGRESS_ANGLE = .8f;
    @VisibleForTesting
    private static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    private static final int CIRCLE_DIAMETER_LARGE = 56;

    private boolean mFinished;
    private int mCircleDiameter;
    private CircleImageView mCircleView;
    private MaterialProgressDrawable mProgress;

    /**
     * 贝塞尔背景
     */
    private int mWaveHeight;
    private int mHeadHeight;
    private Path mBezierPath;
    private Paint mBezierPaint;
    private boolean mShowBezierWave = false;
    private RefreshState mState;

    //<editor-fold desc="MaterialHeader">
    public MaterialHeader(Context context) {
        this(context, null);
    }

    public MaterialHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setMinimumHeight(DensityUtil.dp2px(100));

        mProgress = new MaterialProgressDrawable(context, this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mProgress.setAlpha(255);
        mProgress.setColorSchemeColors(0xff0099cc,0xffff4444,0xff669900,0xffaa66cc,0xffff8800);
        mCircleView = new CircleImageView(context,CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        addView(mCircleView);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);

        mBezierPath = new Path();
        mBezierPaint = new Paint();
        mBezierPaint.setAntiAlias(true);
        mBezierPaint.setStyle(Paint.Style.FILL);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaterialHeader);
        mShowBezierWave = ta.getBoolean(R.styleable.MaterialHeader_mhShowBezierWave, mShowBezierWave);
        mBezierPaint.setColor(ta.getColor(R.styleable.MaterialHeader_mhPrimaryColor, 0xff11bbff));
        if (ta.hasValue(R.styleable.MaterialHeader_mhShadowRadius)) {
            int radius = ta.getDimensionPixelOffset(R.styleable.MaterialHeader_mhShadowRadius, 0);
            int color = ta.getColor(R.styleable.MaterialHeader_mhShadowColor, 0xff000000);
            mBezierPaint.setShadowLayer(radius, 0, 0, color);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        ta.recycle();

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec));
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
//        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
//                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            return;
        }
        final int width = getMeasuredWidth();
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();

        if (isInEditMode() && mHeadHeight > 0) {
            int circleTop = mHeadHeight - circleHeight / 2;
            mCircleView.layout((width / 2 - circleWidth / 2), circleTop,
                    (width / 2 + circleWidth / 2), circleTop + circleHeight);

            mProgress.showArrow(true);
            mProgress.setStartEndTrim(0f, MAX_PROGRESS_ANGLE);
            mProgress.setArrowScale(1);
            mCircleView.setAlpha(1f);
            mCircleView.setVisibility(VISIBLE);
        } else {
            mCircleView.layout((width / 2 - circleWidth / 2), -mCircleDiameter,
                    (width / 2 + circleWidth / 2), circleHeight - mCircleDiameter);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mShowBezierWave) {
            //重置画笔
            mBezierPath.reset();
            mBezierPath.lineTo(0, mHeadHeight);
            //绘制贝塞尔曲线
            mBezierPath.quadTo(getMeasuredWidth() / 2, mHeadHeight + mWaveHeight * 1.9f, getMeasuredWidth(), mHeadHeight);
            mBezierPath.lineTo(getMeasuredWidth(), 0);
            canvas.drawPath(mBezierPath, mBezierPaint);
        }
        super.dispatchDraw(canvas);
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        if (!mShowBezierWave) {
            kernel.requestDefaultHeaderTranslationContent(false);
        }
        if (isInEditMode()) {
            mWaveHeight = mHeadHeight = height / 2;
        }
    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        if (mShowBezierWave) {
            mHeadHeight = Math.min(offset, height);
            mWaveHeight = Math.max(0, offset - height);
            postInvalidate();
        }

        if (mState != RefreshState.Refreshing) {
            float originalDragPercent = 1f * offset / height;

            float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
            float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
            float extraOS = Math.abs(offset) - height;
            float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, (float) height * 2)
                    / (float) height);
            float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                    (tensionSlingshotPercent / 4), 2)) * 2f;
            float strokeStart = adjustedPercent * .8f;
            mProgress.showArrow(true);
            mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
            mProgress.setArrowScale(Math.min(1f, adjustedPercent));

            float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
            mProgress.setProgressRotation(rotation);
            mCircleView.setAlpha(Math.min(1f, originalDragPercent*2));
        }

        float targetY = offset / 2 + mCircleDiameter / 2;
        mCircleView.setTranslationY(Math.min(offset, targetY));//setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        if (!mProgress.isRunning() && !mFinished) {
            onPulling(percent, offset, height, extendHeight);
        } else {
            if (mShowBezierWave) {
                mHeadHeight = Math.min(offset, height);
                mWaveHeight = Math.max(0, offset - height);
                postInvalidate();
            }
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int extendHeight) {
        mProgress.start();
        if ((int) mCircleView.getTranslationY() != height / 2 + mCircleDiameter / 2) {
            mCircleView.animate().translationY(height / 2 + mCircleDiameter / 2);
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        mState = newState;
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mFinished = false;
                mCircleView.setVisibility(VISIBLE);
                mCircleView.setScaleX(1);
                mCircleView.setScaleY(1);
                break;
            case ReleaseToRefresh:
                break;
            case Refreshing:
                break;
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mProgress.stop();
        mCircleView.animate().scaleX(0).scaleY(0);
        mFinished = true;
        return 0;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            mBezierPaint.setColor(colors[0]);
        }
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.MatchLayout;
    }
    //</editor-fold>


    //<editor-fold desc="API">

    /**
     * 设置 ColorScheme
     * @param colors ColorScheme
     * @return MaterialHeader
     */
    public MaterialHeader setColorSchemeColors(int... colors) {
        mProgress.setColorSchemeColors(colors);
        return this;
    }

    /**
     * 设置大小尺寸
     * @param size One of DEFAULT, or LARGE.
     * @return MaterialHeader
     */
    public MaterialHeader setSize(int size) {
        if (size != SIZE_LARGE && size != SIZE_DEFAULT) {
            return this;
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (size == SIZE_LARGE) {
            mCircleDiameter = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
        } else {
            mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null);
        mProgress.updateSizes(size);
        mCircleView.setImageDrawable(mProgress);
        return this;
    }

    /**
     * 是否显示贝塞尔图形
     * @param show 是否显示
     * @return MaterialHeader
     */
    public MaterialHeader setShowBezierWave(boolean show) {
        this.mShowBezierWave = show;
        return this;
    }

    //</editor-fold>
}
