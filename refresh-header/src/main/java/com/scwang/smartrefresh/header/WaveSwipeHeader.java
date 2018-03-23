package com.scwang.smartrefresh.header;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.scwang.smartrefresh.header.internal.MaterialProgressDrawable;
import com.scwang.smartrefresh.header.waveswipe.WaveView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 水滴下拉头
 * Created by SCWANG on 2017/6/4.
 * from https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout
 */
@SuppressWarnings("ALL")
public class WaveSwipeHeader extends InternalAbstract implements RefreshHeader {

    /**
     * 落ちる前の回転の最大のAngle値
     */
    protected static final float MAX_PROGRESS_ROTATION_RATE = 0.8f;

    protected enum VERTICAL_DRAG_THRESHOLD {
        FIRST(0.1f), SECOND(0.16f + FIRST.val), THIRD(0.5f + FIRST.val);
//        FIRST(0.2f), SECOND(0.26f + FIRST.val), THIRD(0.7f + FIRST.val);
        final float val;
        VERTICAL_DRAG_THRESHOLD(float val) {
            this.val = val;
        }
    }

    //<editor-fold desc="DropHeader">
    protected WaveView mWaveView;
    protected RefreshState mState;
    protected MaterialProgressDrawable mProgress;
    protected ProgressAnimationImageView mCircleView;
    protected float mLastFirstBounds;

    public WaveSwipeHeader(Context context) {
        this(context, null);
    }

    public WaveSwipeHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSwipeHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSpinnerStyle = SpinnerStyle.MatchLayout;

        final ViewGroup thisGroup = this;
        thisGroup.addView(mWaveView = new WaveView(context));
        thisGroup.addView(mCircleView = new ProgressAnimationImageView(context));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveSwipeHeader);

        int primaryColor = ta.getColor(R.styleable.WaveSwipeHeader_wshPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.WaveSwipeHeader_wshAccentColor, 0);
        if (primaryColor != 0) {
            mWaveView.setWaveColor(primaryColor);
        }
        if (accentColor != 0) {
            mProgress.setColorSchemeColors(accentColor);
        } else {
            mProgress.setColorSchemeColors(0xffffffff);
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
        super.setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec));
        final View waveView = mWaveView;
        final View cricleView = mCircleView;
        final Drawable progress = mProgress;
        final int circleDiameter = progress.getIntrinsicWidth();
        final int spec = MeasureSpec.makeMeasureSpec(circleDiameter, MeasureSpec.EXACTLY);
        cricleView.measure(spec, spec);
        waveView.measure(makeMeasureSpec(getSize(widthMeasureSpec), EXACTLY),makeMeasureSpec(getSize(heightMeasureSpec), EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View thisView = this;
        final View waveView = mWaveView;
        final View cricleView = mCircleView;
        waveView.layout(0, 0, thisView.getMeasuredWidth(), thisView.getMeasuredHeight());

        final int thisWidth = thisView.getMeasuredWidth();
        final int circleWidth = cricleView.getMeasuredWidth();
        final int circleHeight = cricleView.getMeasuredHeight();
        cricleView.layout((thisWidth - circleWidth) / 2, -circleHeight , (thisWidth + circleWidth) / 2, 0);

        if (thisView.isInEditMode()) {
            onMoving(true, 0.99f, DensityUtil.dp2px(99), DensityUtil.dp2px(100), DensityUtil.dp2px(100));
//            onPulling(0.99f, DensityUtil.dp2px(99), DensityUtil.dp2px(100), DensityUtil.dp2px(100));
        }
    }


    //</editor-fold>

    //<editor-fold desc="WaveSwipe">
    /**
     * @param colors セットするColor達
     */
    public void setColorSchemeColors(int... colors) {
        mProgress.setColorSchemeColors(colors);
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (isDragging) {
            if (mState == RefreshState.Refreshing) {
                return;
            }
            final View cricleView = mCircleView;

            float dragPercent = Math.min(1f, percent);
            float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;

            // 0f...2f
            float tensionSlingshotPercent =
                    (percent > 3f) ? 2f : (percent > 1f) ? percent - 1f : 0;
            float tensionPercent = (4f - tensionSlingshotPercent) * tensionSlingshotPercent / 8f;

            if (percent < 1f) {
                float strokeStart = adjustedPercent * .8f;
                mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ROTATION_RATE, strokeStart));
                mProgress.setArrowScale(Math.min(1f, adjustedPercent));
            }

            float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
            mProgress.setProgressRotation(rotation);
            cricleView.setTranslationY(mWaveView.getCurrentCircleCenterY());

            final View thisView = this;
            float seed = 1f * offset / Math.min(thisView.getMeasuredWidth(), thisView.getMeasuredHeight());
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
    }

//    @Override
//    public void onPulling(float percent, int offset, int height, int maxDragHeight) {
//
//        if (mState == RefreshState.Refreshing) {
//            return;
//        }
//
//        float dragPercent = Math.min(1f, percent);
//        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
//
//        // 0f...2f
//        float tensionSlingshotPercent =
//                (percent > 3f) ? 2f : (percent > 1f) ? percent - 1f : 0;
//        float tensionPercent = (4f - tensionSlingshotPercent) * tensionSlingshotPercent / 8f;
//
//        if (percent < 1f) {
//            float strokeStart = adjustedPercent * .8f;
//            mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ROTATION_RATE, strokeStart));
//            mProgress.setArrowScale(Math.min(1f, adjustedPercent));
//        }
//
//        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
//        mProgress.setProgressRotation(rotation);
//        mCircleView.setTranslationY(mWaveView.getCurrentCircleCenterY());
//
//        float seed = 1f * offset / Math.min(getMeasuredWidth(), getMeasuredHeight());
//        float firstBounds = seed * (5f - 2 * seed) / 3.5f;
//        float secondBounds = firstBounds - VERTICAL_DRAG_THRESHOLD.FIRST.val;
//        float finalBounds = (firstBounds - VERTICAL_DRAG_THRESHOLD.SECOND.val) / 5;
//        mLastFirstBounds = firstBounds;
//
//        if (firstBounds < VERTICAL_DRAG_THRESHOLD.FIRST.val) {
//            // draw a wave and not draw a circle
//            mWaveView.beginPhase(firstBounds);
//        } else if (firstBounds < VERTICAL_DRAG_THRESHOLD.SECOND.val) {
//            // draw a circle with a wave
//            mWaveView.appearPhase(firstBounds, secondBounds);
//        } else /*if (firstBounds < VERTICAL_DRAG_THRESHOLD.THIRD.val)*/ {
//            // draw a circle with expanding a wave
//            mWaveView.expandPhase(firstBounds, secondBounds, finalBounds);
////        } else {
////            // stop to draw a wave and drop a circle
////            onDropPhase();
//        }
//    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        mLastFirstBounds = 0;
        mWaveView.animationDropCircle();
        mProgress.setAlpha(0xff);
        mProgress.start();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 0);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final View cricleView = mCircleView;
                cricleView.setTranslationY(
                        mWaveView.getCurrentCircleCenterY() + cricleView.getHeight() / 2.f);
            }
        });
        animator.start();
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View cricleView = mCircleView;
        mState = newState;
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mProgress.showArrow(true);
                cricleView.setScaleX(1f);
                cricleView.setScaleY(1f);
                mProgress.setAlpha(0xff);
                break;
            case PullDownCanceled:
                mProgress.showArrow(false);
                mProgress.setProgressRotation(0);
                mProgress.setStartEndTrim(0f, 0f);
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
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        final View cricleView = mCircleView;
        Animation scaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                cricleView.setScaleX(1 - interpolatedTime);
                cricleView.setScaleY(1 - interpolatedTime);
            }
        };
        scaleDownAnimation.setDuration(200);
        mCircleView.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                mProgress.stop();
                mProgress.setAlpha(0xff);
                mWaveView.startDisappearCircleAnimation();
            }
        });
        cricleView.clearAnimation();
        cricleView.startAnimation(scaleDownAnimation);
        return 0;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            mWaveView.setWaveColor(colors[0]);
            if (colors.length > 1) {
                mProgress.setColorSchemeColors(colors[1]);
            }
        }
    }
//
//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return SpinnerStyle.MatchLayout;
//    }
    //</editor-fold>

    //<editor-fold desc="ProgressAnimationImageView">
    /**
     * 現在の向きが600dpを超えているかどうか
     *
     * @return 600dpを超えているかどうか
     */
    public static boolean isOver600dp() {
//        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density >= 600;
    }
    /**
     * Custom view has progress drawable.
     * Some features of MaterialProgressDrawable are decorated.
     *
     * @author jmatsu
     */
    protected class ProgressAnimationImageView extends ImageView {

        /**
         * AnimationのStartとEnd時にListenerにアレする
         */
        protected Animation.AnimationListener mListener;

        /**
         * @param listener {@link android.view.animation.Animation.AnimationListener}
         */
        public void setAnimationListener(Animation.AnimationListener listener) {
            mListener = listener;
        }

        /**
         * ViewのAnimationのStart時にセットされたListenerの {@link android.view.animation.Animation.AnimationListener#onAnimationStart(Animation)}
         * を呼ぶ
         */
        @Override public void onAnimationStart() {
            super.onAnimationStart();
            if (mListener != null) {
                mListener.onAnimationStart(getAnimation());
            }
        }

        /**
         * ViewのAnimationのEnd時にセットされたListenerの {@link android.view.animation.Animation.AnimationListener#onAnimationEnd(Animation)}
         * (Animation)} を呼ぶ
         */
        @Override public void onAnimationEnd() {
            super.onAnimationEnd();
            if (mListener != null) {
                mListener.onAnimationEnd(getAnimation());
            }
        }
        /**
         * Constructor
         * {@inheritDoc}
         */
        public ProgressAnimationImageView(Context context) {
            super(context);
            mProgress = new MaterialProgressDrawable(WaveSwipeHeader.this);
            mProgress.setBackgroundColor(Color.TRANSPARENT);
            if (isOver600dp()) { // Make the progress be big
                mProgress.updateSizes(MaterialProgressDrawable.LARGE);
            }
            super.setImageDrawable(mProgress);
        }

        public void setProgressColorSchemeColorsFromResource(@IdRes int... resources) {
            final View thisView = this;
            final Resources res = thisView.getResources();
            final int[] colorRes = new int[resources.length];

            for (int i = 0; i < resources.length; i++) {
                colorRes[i] = res.getColor(resources[i]);
            }

            setColorSchemeColors(colorRes);
        }

    }
    //</editor-fold>
}
