package com.scwang.smartrefresh.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scwang.smartrefresh.header.internal.MaterialProgressDrawable;
import com.scwang.smartrefresh.header.material.CircleImageView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.scwang.smartrefresh.layout.util.SmartUtil;

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

    protected static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    protected static final float MAX_PROGRESS_ANGLE = .8f;
    @VisibleForTesting
    protected static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    protected static final int CIRCLE_DIAMETER_LARGE = 56;

    protected boolean mFinished;
    protected int mCircleDiameter;
    protected ImageView mCircleView;
    protected MaterialProgressDrawable mProgress;

    /**
     * 贝塞尔背景
     */
    protected int mWaveHeight;
    protected int mHeadHeight;
    protected Path mBezierPath;
    protected Paint mBezierPaint;
    protected boolean mShowBezierWave = false;
    protected RefreshState mState;

    //<editor-fold desc="MaterialHeader">
    public MaterialHeader(Context context) {
        this(context, null);
    }

    public MaterialHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSpinnerStyle = SpinnerStyle.MatchLayout;
        final View thisView = this;
        final ViewGroup thisGroup = this;
        thisView.setMinimumHeight(DensityUtil.dp2px(100));

        mProgress = new MaterialProgressDrawable(this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mProgress.setAlpha(255);
        mProgress.setColorSchemeColors(0xff0099cc,0xffff4444,0xff669900,0xffaa66cc,0xffff8800);
        mCircleView = new CircleImageView(context,CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        thisGroup.addView(mCircleView);

        final DisplayMetrics metrics = thisView.getResources().getDisplayMetrics();
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
            thisView.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        ta.recycle();

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec));
        final View circleView = mCircleView;
        circleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
//        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
//                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final ViewGroup thisGroup = this;
        if (thisGroup.getChildCount() == 0) {
            return;
        }
        final View thisView = this;
        final View circleView = mCircleView;
        final int width = thisView.getMeasuredWidth();
        int circleWidth = circleView.getMeasuredWidth();
        int circleHeight = circleView.getMeasuredHeight();

        if (thisView.isInEditMode() && mHeadHeight > 0) {
            int circleTop = mHeadHeight - circleHeight / 2;
            circleView.layout((width / 2 - circleWidth / 2), circleTop,
                    (width / 2 + circleWidth / 2), circleTop + circleHeight);

            mProgress.showArrow(true);
            mProgress.setStartEndTrim(0f, MAX_PROGRESS_ANGLE);
            mProgress.setArrowScale(1);
            circleView.setAlpha(1f);
            circleView.setVisibility(VISIBLE);
        } else {
            circleView.layout((width / 2 - circleWidth / 2), -mCircleDiameter,
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
            final View thisView = this;
            mBezierPath.quadTo(thisView.getMeasuredWidth() / 2, mHeadHeight + mWaveHeight * 1.9f, thisView.getMeasuredWidth(), mHeadHeight);
            mBezierPath.lineTo(thisView.getMeasuredWidth(), 0);
            canvas.drawPath(mBezierPath, mBezierPaint);
        }
        super.dispatchDraw(canvas);
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        final View thisView = this;
        if (!mShowBezierWave) {
            kernel.requestDefaultTranslationContentFor(this, false);
//            kernel.requestDefaultHeaderTranslationContent(false);
        }
        if (thisView.isInEditMode()) {
            mWaveHeight = mHeadHeight = height / 2;
        }
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (mShowBezierWave) {
            mHeadHeight = Math.min(offset, height);
            mWaveHeight = Math.max(0, offset - height);

            final View thisView = this;
            thisView.postInvalidate();
        }

        if (isDragging || (!mProgress.isRunning() && !mFinished)) {

            final View circleView = mCircleView;
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
                circleView.setAlpha(Math.min(1f, originalDragPercent * 2));
            }

            float targetY = offset / 2 + mCircleDiameter / 2;
            circleView.setTranslationY(Math.min(offset, targetY));//setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
        }
    }

//    @Override
//    public void onPulling(float percent, int offset, int height, int maxDragHeight) {
//        if (mShowBezierWave) {
//            mHeadHeight = Math.min(offset, height);
//            mWaveHeight = Math.max(0, offset - height);
//            postInvalidate();
//        }
//
//        if (mState != RefreshState.Refreshing) {
//            float originalDragPercent = 1f * offset / height;
//
//            float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
//            float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
//            float extraOS = Math.abs(offset) - height;
//            float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, (float) height * 2)
//                    / (float) height);
//            float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
//                    (tensionSlingshotPercent / 4), 2)) * 2f;
//            float strokeStart = adjustedPercent * .8f;
//            mProgress.showArrow(true);
//            mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
//            mProgress.setArrowScale(Math.min(1f, adjustedPercent));
//
//            float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
//            mProgress.setProgressRotation(rotation);
//            mCircleView.setAlpha(Math.min(1f, originalDragPercent*2));
//        }
//
//        float targetY = offset / 2 + mCircleDiameter / 2;
//        mCircleView.setTranslationY(Math.min(offset, targetY));//setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
//    }
//
//    @Override
//    public void onReleasing(float percent, int offset, int height, int maxDragHeight) {
//        if (!mProgress.isRunning() && !mFinished) {
//            onPulling(percent, offset, height, maxDragHeight);
//        } else {
//            if (mShowBezierWave) {
//                mHeadHeight = Math.min(offset, height);
//                mWaveHeight = Math.max(0, offset - height);
//                postInvalidate();
//            }
//        }
//    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        mProgress.start();
        final View circleView = mCircleView;
        if ((int) circleView.getTranslationY() != height / 2 + mCircleDiameter / 2) {
            circleView.animate().translationY(height / 2 + mCircleDiameter / 2);
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View circleView = mCircleView;
        mState = newState;
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mFinished = false;
                circleView.setVisibility(VISIBLE);
                circleView.setTranslationY(0);
                circleView.setScaleX(1);
                circleView.setScaleY(1);
                break;
            case ReleaseToRefresh:
                break;
            case Refreshing:
                break;
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        final View circleView = mCircleView;
        mProgress.stop();
        circleView.animate().scaleX(0).scaleY(0);
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

//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return SpinnerStyle.MatchLayout;
//    }
    //</editor-fold>


    //<editor-fold desc="API">

    /**
     * 设置 ColorScheme
     * @param colors ColorScheme
     * @return MaterialHeader
     */
    public MaterialHeader setColorSchemeColors(@ColorInt int... colors) {
        mProgress.setColorSchemeColors(colors);
        return this;
    }

    /**
     * 设置 ColorScheme
     * @param colorIds ColorSchemeResources
     * @return MaterialHeader
     */
    public MaterialHeader setColorSchemeResources(@ColorRes int... colorIds) {
        final View thisView = this;
        final Context context = thisView.getContext();
        int[] colors = new int[colorIds.length];
        for (int i = 0; i < colorIds.length; i++) {
            colors[i] = SmartUtil.getColor(context, colorIds[i]);
        }
        return setColorSchemeColors(colors);
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
        final View thisView = this;
        DisplayMetrics metrics = thisView.getResources().getDisplayMetrics();
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
