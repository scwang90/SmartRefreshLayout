package com.scwang.smartrefreshheader.drop;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 水滴下拉头
 * Created by SCWANG on 2017/6/4.
 */

public class DropHeader extends ViewGroup implements RefreshHeader {

    /**
     * 落ちる前の回転の最大のAngle値
     */
    private static final float MAX_PROGRESS_ROTATION_RATE = 0.8f;

    private enum VERTICAL_DRAG_THRESHOLD {
        FIRST(0.1f), SECOND(0.16f + FIRST.val), THIRD(0.5f + FIRST.val);

        final float val;

        VERTICAL_DRAG_THRESHOLD(float val) {
            this.val = val;
        }
    }
    private enum EVENT_PHASE {
        WAITING, BEGINNING, APPEARING, EXPANDING, DROPPING;
    }

    //<editor-fold desc="DropHeader">
    private EVENT_PHASE mEventPhase = EVENT_PHASE.WAITING;

    private WaveView mWaveView;
    private ProgressAnimationImageView mCircleView;

    public DropHeader(Context context) {
        this(context, null);
    }

    public DropHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        addView(mWaveView = new WaveView(context));
        addView(mCircleView = new ProgressAnimationImageView(getContext()));
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        params.height = -3;
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
    }


    //</editor-fold>

    //<editor-fold desc="WaveSwipe">
    /**
     * @param colors セットするColor達
     */
    public void setColorSchemeColors(int... colors) {
        mCircleView.setProgressColorSchemeColors(colors);
    }

    private void setEventPhase(EVENT_PHASE eventPhase) {
        mEventPhase = eventPhase;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mCircleView.showArrow(true);
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }

        mCircleView.scaleWithKeepingAspectRatio(1f);
        mCircleView.makeProgressTransparent();

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

        if (firstBounds < VERTICAL_DRAG_THRESHOLD.FIRST.val) {
            // draw a wave and not draw a circle
            mWaveView.beginPhase(firstBounds);
            setEventPhase(EVENT_PHASE.BEGINNING);
        } else if (firstBounds < VERTICAL_DRAG_THRESHOLD.SECOND.val) {
            // draw a circle with a wave
            mWaveView.appearPhase(firstBounds, secondBounds);
            setEventPhase(EVENT_PHASE.APPEARING);
        } else if (firstBounds < VERTICAL_DRAG_THRESHOLD.THIRD.val) {
            // draw a circle with expanding a wave
            mWaveView.expandPhase(firstBounds, secondBounds, finalBounds);
            setEventPhase(EVENT_PHASE.EXPANDING);
        } else {
            // stop to draw a wave and drop a circle
//            onDropPhase();
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        if (mEventPhase != EVENT_PHASE.WAITING) {
            onPullingDown(percent, offset, headHeight, extendHeight);
        }
    }

    @Override
    public void startAnimator(int headHeight, int extendHeight) {
        mWaveView.animationDropCircle();
        mWaveView.startDropAnimation();
        mCircleView.makeProgressTransparent();
        mCircleView.startProgress();
        setEventPhase(EVENT_PHASE.WAITING);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 0);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> mCircleView.setTranslationY(
                mWaveView.getCurrentCircleCenterY() + mCircleView.getHeight() / 2.f));
        animator.start();
    }

    @Override
    public void onStateChanged(RefreshState state) {

    }

    @Override
    public void onFinish() {
        Animation scaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mCircleView.scaleWithKeepingAspectRatio(1 - interpolatedTime);
            }
        };
        scaleDownAnimation.setDuration(200);
        mCircleView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                mCircleView.stopProgress();
                mCircleView.setVisibility(View.GONE);
                mCircleView.makeProgressTransparent();
                mWaveView.startDisappearCircleAnimation();
            }
        });
        mCircleView.clearAnimation();
        mCircleView.startAnimation(scaleDownAnimation);
    }

    @Override
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
        return SpinnerStyle.FixedFront;
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
            mProgress = new MaterialProgressDrawable(context, DropHeader.this);

            if (DisplayUtil.isOver600dp(getContext())) { // Make the progress be big
                mProgress.updateSizes(MaterialProgressDrawable.LARGE);
            }
            initialize();
        }

        private void initialize() {
            setImageDrawable(null);

            mProgress.setBackgroundColor(Color.TRANSPARENT);

            setImageDrawable(mProgress);
            setVisibility(View.GONE);
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
            ViewCompat.setScaleX(this, scale);
            ViewCompat.setScaleY(this, scale);
        }
    }
    //</editor-fold>
}
