/*
 * Copyright (C) 2015 RECRUIT LIFESTYLE CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scwang.smartrefreshheader.drop;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/**
 * @author amyu
 */
public class WaveSwipeRefreshLayout extends ViewGroup
        implements ViewTreeObserver.OnPreDrawListener {

    private enum VERTICAL_DRAG_THRESHOLD {
        FIRST(0.1f), SECOND(0.16f + FIRST.val), THIRD(0.5f + FIRST.val);

        final float val;

        VERTICAL_DRAG_THRESHOLD(float val) {
            this.val = val;
        }
    }

    private enum STATE {
        REFRESHING, PENDING;
    }

    private enum EVENT_PHASE {
        WAITING, BEGINNING, APPEARING, EXPANDING, DROPPING;
    }

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private static final int INVALID_POINTER = -1;

    private static final float DRAGGING_WEIGHT = 0.5f;

    /**
     * 落ちる前の回転の最大のAngle値
     */
    private static final float MAX_PROGRESS_ROTATION_RATE = 0.8f;

    /**
     * {@link WaveSwipeRefreshLayout#mCircleView} が消えるときのDuration
     */
    private static final int SCALE_DOWN_DURATION = 200;

    /**
     * {@link WaveSwipeRefreshLayout.ProgressAnimationImageView#mProgress}
     * の回転が始まるまでのDuration
     */
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    /**
     * デフォルトのCircleのTargetの値
     */
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private View mTarget;

    /**
     * Refreshを通知するListener
     */
    private OnRefreshListener mListener;

    /**
     * リフレッシュ状態
     */
    private STATE mState = STATE.PENDING;

    private EVENT_PHASE mEventPhase = EVENT_PHASE.WAITING;

    /**
     * {@link WaveSwipeRefreshLayout#mAnimateToCorrectPosition } にセットするInterpolator
     */
    private final DecelerateInterpolator mDecelerateInterpolator;

    private ProgressAnimationImageView mCircleView;

    /**
     * 波のView
     */
    private WaveView mWaveView;

    /**
     * {@link WaveSwipeRefreshLayout#mListener} に通知するかどうかのFlag
     */
    private boolean mNotify;

    private boolean mIsManualRefresh = false;

    private float mFirstTouchDownPointY;

    private boolean mIsBeingDropped;

    private int mActivePointerId = INVALID_POINTER;

    private int mTopOffset;

    /**
     * Constructor
     * {@inheritDoc}
     */
    public WaveSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     * {@inheritDoc}
     */
    public WaveSwipeRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     * {@inheritDoc}
     */
    public WaveSwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getViewTreeObserver().addOnPreDrawListener(this);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        createProgressView();
        createWaveView();
    }

    private void createProgressView() {
        addView(mCircleView = new ProgressAnimationImageView(getContext()));
    }

    private void createWaveView() {
        mWaveView = new WaveView(getContext());
        addView(mWaveView, 0);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureTarget();

        //// Propagates measuring to child view and to circle view

        // Measures on child view with each directed length without padding
        mTarget.measure(
                makeMeasureSpecExactly(getMeasuredWidth() - (getPaddingLeft() + getPaddingRight())),
                makeMeasureSpecExactly(getMeasuredHeight() - (getPaddingTop() + getPaddingBottom())));
        mWaveView.measure(widthMeasureSpec, heightMeasureSpec);

        mCircleView.measure();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            return;
        }

        ensureTarget();

        final int thisWidth = getMeasuredWidth();
        final int thisHeight = getMeasuredHeight();

        final int childRight = thisWidth - getPaddingRight();
        final int childBottom = thisHeight - getPaddingBottom();
        mTarget.layout(getPaddingLeft(), getPaddingTop(), childRight, childBottom);

        layoutWaveView();
    }

    private void layoutWaveView() {
        if (mWaveView == null) {
            return;
        }
        final int thisWidth = getMeasuredWidth();
        final int thisHeight = getMeasuredHeight();

        final int circleWidth = mCircleView.getMeasuredWidth();
        final int circleHeight = mCircleView.getMeasuredHeight();
        mCircleView.layout((thisWidth - circleWidth) / 2, -circleHeight + mTopOffset,
                (thisWidth + circleWidth) / 2, mTopOffset);
        final int childRight = thisWidth - getPaddingRight();
        final int childBottom = thisHeight - getPaddingBottom();
        mWaveView.layout(getPaddingLeft(), mTopOffset + getPaddingTop(), childRight, childBottom);
    }

    public void setTopOffsetOfWave(int topOffset) {
        if (topOffset < 0) {
            return;
        }
        mTopOffset = topOffset;
        layoutWaveView();
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        mWaveView.bringToFront();
        mCircleView.bringToFront();
        if (mIsManualRefresh) {
            mIsManualRefresh = false;
            mWaveView.manualRefresh();
            reInitCircleView();
            mCircleView.setBackgroundColor(Color.TRANSPARENT);
            mCircleView.setTranslationY(
                    mWaveView.getCurrentCircleCenterY() + mCircleView.getHeight() / 2);
            animateOffsetToCorrectPosition();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        ensureTarget();

        if (!isEnabled() || canChildScrollUp() || isRefreshing()) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                mFirstTouchDownPointY = getMotionEventY(event, mActivePointerId);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                final float currentY = getMotionEventY(event, mActivePointerId);

                if (currentY == -1) {
                    return false;
                }

                if (mFirstTouchDownPointY == -1) {
                    mFirstTouchDownPointY = currentY;
                }

                final float yDiff = currentY - mFirstTouchDownPointY;

                // State is changed to drag if over slop
                if (yDiff > ViewConfiguration.get(getContext()).getScaledTouchSlop() && !isRefreshing()) {
                    mCircleView.makeProgressTransparent();
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return false;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        //orz
    }

    /**
     * Make circle view be visible and even scale.
     */
    private void reInitCircleView() {
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }

        mCircleView.scaleWithKeepingAspectRatio(1f);
        mCircleView.makeProgressTransparent();
    }

    private boolean onMoveTouchEvent(@NonNull MotionEvent event, int pointerIndex) {
        if (mIsBeingDropped) {
            return false;
        }

        final float y = MotionEventCompat.getY(event, pointerIndex);
        final float diffY = y - mFirstTouchDownPointY;
        final float overScrollTop = diffY * DRAGGING_WEIGHT;

        if (overScrollTop < 0) {
            mCircleView.showArrow(false);
            return false;
        }

        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        float originalDragPercent = overScrollTop / (DEFAULT_CIRCLE_TARGET * metrics.density);
        float dragPercent = Math.min(1f, originalDragPercent);
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;

        // 0f...2f
        float tensionSlingshotPercent =
                (originalDragPercent > 3f) ? 2f : (originalDragPercent > 1f) ? originalDragPercent - 1f : 0;
        float tensionPercent = (4f - tensionSlingshotPercent) * tensionSlingshotPercent / 8f;

        mCircleView.showArrow(true);
        reInitCircleView();

        if (originalDragPercent < 1f) {
            float strokeStart = adjustedPercent * .8f;
            mCircleView.setProgressStartEndTrim(0f, Math.min(MAX_PROGRESS_ROTATION_RATE, strokeStart));
            mCircleView.setArrowScale(Math.min(1f, adjustedPercent));
        }

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mCircleView.setProgressRotation(rotation);
        mCircleView.setTranslationY(mWaveView.getCurrentCircleCenterY());

        float seed = diffY / Math.min(getMeasuredWidth(), getMeasuredHeight());
        float firstBounds = seed * (5f - 2 * seed) / 3.5f;
        float secondBounds = firstBounds - VERTICAL_DRAG_THRESHOLD.FIRST.val;
        float finalBounds = (firstBounds - VERTICAL_DRAG_THRESHOLD.SECOND.val) / 5;

        if (firstBounds < VERTICAL_DRAG_THRESHOLD.FIRST.val) {
            // draw a wave and not draw a circle
            onBeginPhase(firstBounds);
        } else if (firstBounds < VERTICAL_DRAG_THRESHOLD.SECOND.val) {
            // draw a circle with a wave
            onAppearPhase(firstBounds, secondBounds);
        } else if (firstBounds < VERTICAL_DRAG_THRESHOLD.THIRD.val) {
            // draw a circle with expanding a wave
            onExpandPhase(firstBounds, secondBounds, finalBounds);
        } else {
            // stop to draw a wave and drop a circle
            onDropPhase();
        }

        return !mIsBeingDropped;
    }

    private void onBeginPhase(float move1) {
        //最初の小波の描画
        mWaveView.beginPhase(move1);

        setEventPhase(EVENT_PHASE.BEGINNING);
    }

    private void onAppearPhase(float move1, float move2) {
        //すでに描画されている波に対して追加で円を描画する
        mWaveView.appearPhase(move1, move2);

        setEventPhase(EVENT_PHASE.APPEARING);
    }

    private void onExpandPhase(float move1, float move2, float move3) {
        mWaveView.expandPhase(move1, move2, move3);

        setEventPhase(EVENT_PHASE.EXPANDING);
    }

    private void onDropPhase() {
        mWaveView.animationDropCircle();

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
        setRefreshing(true, true);
        mIsBeingDropped = true;
        setEventPhase(EVENT_PHASE.DROPPING);
        setEnabled(false);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        if (!isEnabled() || canChildScrollUp()) {
            return false;
        }
        mIsBeingDropped = mWaveView.isDisappearCircleAnimatorRunning();

        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Here is not called from anywhere
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                return pointerIndex >= 0 && onMoveTouchEvent(event, pointerIndex);

            case MotionEvent.ACTION_UP:
                if (mIsBeingDropped) {
                    mIsBeingDropped = false;
                    return false;
                }

                final float diffY = event.getY() - mFirstTouchDownPointY;
                final float waveHeightThreshold =
                        diffY * (5f - 2 * diffY / Math.min(getMeasuredWidth(), getMeasuredHeight())) / 1000f;
                mWaveView.startWaveAnimation(waveHeightThreshold);

            case MotionEvent.ACTION_CANCEL:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                if (!isRefreshing()) {
                    mCircleView.setProgressStartEndTrim(0f, 0f);
                    mCircleView.showArrow(false);
                    mCircleView.setVisibility(GONE);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
        }
        return true;
    }

    private float getMotionEventY(@NonNull MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void animateOffsetToCorrectPosition() {
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mCircleView.setAnimationListener(mRefreshListener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
        }
    };

    /**
     * @param dropHeight 高さ
     */
    public void setMaxDropHeight(int dropHeight) {
        mWaveView.setMaxDropHeight(dropHeight);
    }

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isRefreshing()) {
                mCircleView.makeProgressTransparent();
                mCircleView.startProgress();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            } else {
                mCircleView.stopProgress();
                mCircleView.setVisibility(View.GONE);
                mCircleView.makeProgressTransparent();
                mWaveView.startDisappearCircleAnimation();
            }
        }
    };

    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView) && !child.equals(mWaveView)) {
                    mTarget = child;
                    break;
                }
            }
        }

        if (mTarget == null) {
            throw new IllegalStateException("This view must have at least one AbsListView");
        }
    }

    /**
     * @param refreshing Refreshの状態
     * @param notify     Listenerに通知するかどうか
     */
    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (isRefreshing() != refreshing) {
            mNotify = notify;
            ensureTarget();
            setState(refreshing);
            if (isRefreshing()) {
                animateOffsetToCorrectPosition();
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    private void setEventPhase(EVENT_PHASE eventPhase) {
        mEventPhase = eventPhase;
    }

    private void setState(STATE state) {
        mState = state;
        setEnabled(true);
        if (!isRefreshing()) {
            setEventPhase(EVENT_PHASE.WAITING);
        }
    }

    private void setState(boolean doRefresh) {
        setState((doRefresh) ? STATE.REFRESHING : STATE.PENDING);
    }

    /**
     * @param listener {@link android.view.animation.Animation.AnimationListener}
     */
    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        Animation scaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mCircleView.scaleWithKeepingAspectRatio(1 - interpolatedTime);
            }
        };

        scaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(scaleDownAnimation);
    }

    /**
     * @param colorResIds ColorのId達
     */
    public void setColorSchemeResources(@IdRes int... colorResIds) {
        mCircleView.setProgressColorSchemeColorsFromResource(colorResIds);
    }

    /**
     * @param colors セットするColor達
     */
    public void setColorSchemeColors(int... colors) {
        // FIXME Add @NonNull to the argument
        ensureTarget();
        mCircleView.setProgressColorSchemeColors(colors);
    }

    /**
     * @return {@link WaveSwipeRefreshLayout#mState} == REFRESHING of {@link
     * WaveSwipeRefreshLayout.STATE}
     */
    public boolean isRefreshing() {
        return mState == STATE.REFRESHING;
    }

    private boolean isBeginning() {
        return mEventPhase == EVENT_PHASE.BEGINNING;
    }

    private boolean isExpanding() {
        return mEventPhase == EVENT_PHASE.EXPANDING;
    }

    private boolean isDropping() {
        return mEventPhase == EVENT_PHASE.DROPPING;
    }

    private boolean isAppearing() {
        return mEventPhase == EVENT_PHASE.APPEARING;
    }

    private boolean isWaiting() {
        return mEventPhase == EVENT_PHASE.WAITING;
    }

    /**
     * @param refreshing {@link WaveSwipeRefreshLayout#mState} のセット
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && !isRefreshing()) {
            // scale and show
            setState(true);
            mNotify = false;

            mIsManualRefresh = true;
            if (mWaveView.getCurrentCircleCenterY() == 0) {
                return;
            }
            mWaveView.manualRefresh();
            reInitCircleView();
            mCircleView.setTranslationY(
                    mWaveView.getCurrentCircleCenterY() + mCircleView.getHeight() / 2);
            animateOffsetToCorrectPosition();
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    /**
     * @return ScrollUp出来るかどうか
     */
    public boolean canChildScrollUp() {
        if (mTarget == null) {
            return false;
        }

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
                        || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /**
     * @param radius 波の影の深さ
     */
    public void setShadowRadius(int radius) {
        radius = Math.max(0, radius); // set zero if negative
        mWaveView.setShadowRadius(radius);
    }

    /**
     * This is an alias to WaveView#setWaveColor(int)
     *
     * @see WaveView#setWaveColor(int)
     */
    public void setWaveColor(int argbColor) {
        int alpha = 0xFF & (argbColor >> 24);
        int red = 0xFF & (argbColor >> 16);
        int blue = 0xFF & (argbColor >> 0);
        int green = 0xFF & (argbColor >> 8);
        setWaveARGBColor(alpha, red, green, blue);
    }

    /**
     * WaveView is colored by given rgb color + 0xFF000000
     *
     * @param r int [0, 0xFF]
     * @param g int [0, 0xFF]
     * @param b int [0, 0xFF]
     */
    public void setWaveRGBColor(int r, int g, int b) {
        mWaveView.setWaveColor(Color.argb(0xFF, r, g, b));
    }

    /**
     * This is an alias to WaveView#setWaveARGBColor(int)
     *
     * @param a int [0, 0xFF]
     * @param r int [0, 0xFF]
     * @param g int [0, 0xFF]
     * @param b int [0, 0xFF]
     * @see WaveView#setWaveARGBColor(int, int, int, int)
     */
    public void setWaveARGBColor(int a, int r, int g, int b) {
        setWaveRGBColor(r, g, b);
        if (a == 0xFF) {
            return;
        }
        mWaveView.setAlpha((float) a / 255f);
    }

    private static int makeMeasureSpecExactly(int length) {
        return MeasureSpec.makeMeasureSpec(length, MeasureSpec.EXACTLY);
    }

    /**
     * @param listener {@link WaveSwipeRefreshLayout.OnRefreshListener}
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

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
            mProgress = new MaterialProgressDrawable(context, WaveSwipeRefreshLayout.this);

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
}