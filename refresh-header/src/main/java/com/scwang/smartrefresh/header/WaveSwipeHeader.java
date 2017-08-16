package com.scwang.smartrefresh.header;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.scwang.smartrefresh.header.internal.MaterialProgressDrawable;
import com.scwang.smartrefresh.header.waveswipe.AnimationImageView;
import com.scwang.smartrefresh.header.waveswipe.DisplayUtil;
import com.scwang.smartrefresh.header.waveswipe.WaveView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 水滴下拉头
 * Created by SCWANG on 2017/6/4.
 */

public class WaveSwipeHeader extends ViewGroup implements RefreshHeader {

    /**
     * 落ちる前の回転の最大のAngle値
     */
    private static final float MAX_PROGRESS_ROTATION_RATE = 0.8f;

    private enum VERTICAL_DRAG_THRESHOLD {
        FIRST(0.1f), SECOND(0.16f + FIRST.val), THIRD(0.5f + FIRST.val);
//        FIRST(0.2f), SECOND(0.26f + FIRST.val), THIRD(0.7f + FIRST.val);
        final float val;
        VERTICAL_DRAG_THRESHOLD(float val) {
            this.val = val;
        }
    }

    //<editor-fold desc="DropHeader">
    private WaveView mWaveView;
    private RefreshState mState;
    private ProgressAnimationImageView mCircleView;
    private float mLastFirstBounds;

    public WaveSwipeHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public WaveSwipeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public WaveSwipeHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public WaveSwipeHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        addView(mWaveView = new WaveView(context));
        addView(mCircleView = new ProgressAnimationImageView(getContext()));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveSwipeHeader);

        int primaryColor = ta.getColor(R.styleable.WaveSwipeHeader_wshPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.WaveSwipeHeader_wshAccentColor, 0);
        if (primaryColor != 0) {
            mWaveView.setWaveColor(primaryColor);
        }
        if (accentColor != 0) {
            mCircleView.setProgressColorSchemeColors(accentColor);
        }
        if (ta.hasValue(R.styleable.WaveSwipeHeader_wshShadowRadius)) {
            int radius = ta.getDimensionPixelOffset(R.styleable.WaveSwipeHeader_wshShadowRadius, 0);
            int color = ta.getColor(R.styleable.WaveSwipeHeader_wshShadowColor, 0xff000000);
            mWaveView.setShadow(radius, color);
        }

        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec));
        mCircleView.measure();
        mWaveView.measure(makeMeasureSpec(getSize(widthMeasureSpec), EXACTLY),makeMeasureSpec(getSize(heightMeasureSpec), EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mWaveView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());

        final int thisWidth = getMeasuredWidth();
        final int circleWidth = mCircleView.getMeasuredWidth();
        final int circleHeight = mCircleView.getMeasuredHeight();
        mCircleView.layout((thisWidth - circleWidth) / 2, -circleHeight , (thisWidth + circleWidth) / 2, 0);

        if (isInEditMode()) {
            onPullingDown(0.99f, DensityUtil.dp2px(99), DensityUtil.dp2px(100), DensityUtil.dp2px(100));
        }
    }


    //</editor-fold>

    //<editor-fold desc="WaveSwipe">
    /**
     * @param colors セットするColor達
     */
    public void setColorSchemeColors(int... colors) {
        mCircleView.setProgressColorSchemeColors(colors);
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

        if (mState == RefreshState.Refreshing) {
            return;
        }

        float dragPercent = Math.min(1f, percent);
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;

        // 0f...2f
        float tensionSlingshotPercent =
                (percent > 3f) ? 2f : (percent > 1f) ? percent - 1f : 0;
        float tensionPercent = (4f - tensionSlingshotPercent) * tensionSlingshotPercent / 8f;

        if (percent < 1f) {
            float strokeStart = adjustedPercent * .8f;
            mCircleView.setProgressStartEndTrim(0f, Math.min(MAX_PROGRESS_ROTATION_RATE, strokeStart));
            mCircleView.setArrowScale(Math.min(1f, adjustedPercent));
        }

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mCircleView.setProgressRotation(rotation);
        mCircleView.setTranslationY(mWaveView.getCurrentCircleCenterY());

        float seed = 1f * offset / Math.min(getMeasuredWidth(), getMeasuredHeight());
        float firstBounds = seed * (5f - 2 * seed) / 3.5f;
        float secondBounds = firstBounds - VERTICAL_DRAG_THRESHOLD.FIRST.val;
        float finalBounds = (firstBounds - VERTICAL_DRAG_THRESHOLD.SECOND.val) / 5;
        mLastFirstBounds = firstBounds;

        if (firstBounds < VERTICAL_DRAG_THRESHOLD.FIRST.val) {
            // draw a wave and not draw a circle
            mWaveView.beginPhase(firstBounds);
        } else if (firstBounds < VERTICAL_DRAG_THRESHOLD.SECOND.val) {
            // draw a circle with a wave
            mWaveView.appearPhase(firstBounds, secondBounds);
        } else /*if (firstBounds < VERTICAL_DRAG_THRESHOLD.THIRD.val)*/ {
            // draw a circle with expanding a wave
            mWaveView.expandPhase(firstBounds, secondBounds, finalBounds);
//        } else {
//            // stop to draw a wave and drop a circle
//            onDropPhase();
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mLastFirstBounds = 0;
        mWaveView.animationDropCircle();
        mCircleView.makeProgressTransparent();
        mCircleView.startProgress();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 0);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCircleView.setTranslationY(
                        mWaveView.getCurrentCircleCenterY() + mCircleView.getHeight() / 2.f);
            }
        });
        animator.start();
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mState = newState;
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mCircleView.showArrow(true);
                mCircleView.scaleWithKeepingAspectRatio(1f);
                mCircleView.makeProgressTransparent();
                break;
            case PullDownCanceled:
                mCircleView.showArrow(false);
                mCircleView.setProgressRotation(0);
                mCircleView.setProgressStartEndTrim(0f, 0f);
                mWaveView.startWaveAnimation(mLastFirstBounds);
                mLastFirstBounds = 0;
                break;
            case ReleaseToRefresh:
                break;
            case Refreshing:
                break;
        }
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        Animation scaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mCircleView.scaleWithKeepingAspectRatio(1 - interpolatedTime);
            }
        };
        scaleDownAnimation.setDuration(200);
        mCircleView.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                mCircleView.stopProgress();
                mCircleView.makeProgressTransparent();
                mWaveView.startDisappearCircleAnimation();
            }
        });
        mCircleView.clearAnimation();
        mCircleView.startAnimation(scaleDownAnimation);
        return 0;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            mWaveView.setWaveColor(colors[0]);
            if (colors.length > 1) {
                mCircleView.setProgressColorSchemeColors(colors[1]);
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
        return SpinnerStyle.MatchLayout;
    }
    //</editor-fold>

    //<editor-fold desc="ProgressAnimationImageView">
    /**
     * Custom view has progress drawable.
     * Some features of MaterialProgressDrawable are decorated.
     *
     * @author jmatsu
     */
    private class ProgressAnimationImageView extends AnimationImageView {
        private final MaterialProgressDrawable mProgress;

        /**
         * Constructor
         * {@inheritDoc}
         */
        public ProgressAnimationImageView(Context context) {
            super(context);
            mProgress = new MaterialProgressDrawable(context, WaveSwipeHeader.this);
            mProgress.setBackgroundColor(Color.TRANSPARENT);
            if (DisplayUtil.isOver600dp(getContext())) { // Make the progress be big
                mProgress.updateSizes(MaterialProgressDrawable.LARGE);
            }
            setImageDrawable(mProgress);
        }

        public void measure() {
            final int circleDiameter = mProgress.getIntrinsicWidth();
            measure(makeMeasureSpecExactly(circleDiameter), makeMeasureSpecExactly(circleDiameter));
        }

        private int makeMeasureSpecExactly(int length) {
            return MeasureSpec.makeMeasureSpec(length, MeasureSpec.EXACTLY);
        }

        public void makeProgressTransparent() {
            mProgress.setAlpha(0xff);
        }

        public void showArrow(boolean show) {
            mProgress.showArrow(show);
        }

        public void setArrowScale(float scale) {
            mProgress.setArrowScale(scale);
        }

        public void setProgressAlpha(int alpha) {
            mProgress.setAlpha(alpha);
        }

        public void setProgressStartEndTrim(float startAngle, float endAngle) {
            mProgress.setStartEndTrim(startAngle, endAngle);
        }

        public void setProgressRotation(float rotation) {
            mProgress.setProgressRotation(rotation);
        }

        public void startProgress() {
            mProgress.start();
        }

        public void stopProgress() {
            mProgress.stop();
        }

        public void setProgressColorSchemeColors(@NonNull int... colors) {
            mProgress.setColorSchemeColors(colors);
        }

        public void setProgressColorSchemeColorsFromResource(@IdRes int... resources) {
            final Resources res = getResources();
            final int[] colorRes = new int[resources.length];

            for (int i = 0; i < resources.length; i++) {
                colorRes[i] = res.getColor(resources[i]);
            }

            setColorSchemeColors(colorRes);
        }

        public void scaleWithKeepingAspectRatio(float scale) {
            this.setScaleX(scale);
            this.setScaleY(scale);
        }
    }
    //</editor-fold>
}
