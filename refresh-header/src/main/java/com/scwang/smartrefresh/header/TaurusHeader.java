package com.scwang.smartrefresh.header;

import android.support.annotation.RequiresApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.pathview.PathsDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Taurus
 * Created by SCWANG on 2017/5/31.
 */

public class TaurusHeader extends View implements RefreshHeader/*, SizeDefinition*/ {

    //<editor-fold desc="Field">
    private static final float SCALE_START_PERCENT = 0.5f;
    private static final int ANIMATION_DURATION = 1000;

    private static final float SIDE_CLOUDS_INITIAL_SCALE = 0.6f;//1.05f;
    private static final float SIDE_CLOUDS_FINAL_SCALE = 1f;//1.55f;

    private static final float CENTER_CLOUDS_INITIAL_SCALE = 0.8f;//0.8f;
    private static final float CENTER_CLOUDS_FINAL_SCALE = 1f;//1.30f;

    private static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    // Multiply with this animation interpolator time
    private static final int LOADING_ANIMATION_COEFFICIENT = 80;
    private static final int SLOW_DOWN_ANIMATION_COEFFICIENT = 6;
    // Amount of lines when is going lading animation
    private static final int WIND_SET_AMOUNT = 10;
    private static final int Y_SIDE_CLOUDS_SLOW_DOWN_COF = 4;
    private static final int X_SIDE_CLOUDS_SLOW_DOWN_COF = 2;
    private static final int MIN_WIND_LINE_WIDTH = 50;
    private static final int MAX_WIND_LINE_WIDTH = 300;
    private static final int MIN_WIND_X_OFFSET = 1000;
    private static final int MAX_WIND_X_OFFSET = 2000;
    private static final int RANDOM_Y_COEFFICIENT = 5;

    private PathsDrawable mAirplane;
    private PathsDrawable mCloudCenter;
    private PathsDrawable mCloudLeft;
    private PathsDrawable mCloudRight;
    private Matrix mMatrix;
    private float mPercent;
    private int mHeaderHeight;
    private Animation mAnimation;

    private boolean isRefreshing = false;
    private float mLoadingAnimationTime;
    private float mLastAnimationTime;

    private Random mRandom;
    private boolean mEndOfRefreshing;

    //KEY: Y position, Value: X offset of wind
    private Map<Float, Float> mWinds;
    private Paint mWindPaint;
    private float mWindLineWidth;
    private boolean mNewWindSet;
    private boolean mInverseDirection;

    private enum AnimationPart {
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    }
    //</editor-fold>

    //<editor-fold desc="View">
    public TaurusHeader(Context context) {
        super(context);
        initView(context, null);
    }

    public TaurusHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TaurusHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public TaurusHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mMatrix = new Matrix();
        mWinds = new HashMap<>();
        mRandom = new Random();

        mWindPaint = new Paint();
        mWindPaint.setColor(0xffffffff);
        mWindPaint.setStrokeWidth(DensityUtil.dp2px(3));
        mWindPaint.setAlpha(50);

        setupAnimations();
        setupPathDrawable();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TaurusHeader);

        int primaryColor = ta.getColor(R.styleable.TaurusHeader_thPrimaryColor, 0);
        if (primaryColor != 0) {
            setBackgroundColor(primaryColor);
        } else {
            setBackgroundColor(0xff11bbff);
        }

        ta.recycle();
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
        mEndOfRefreshing = false;
        mPercent = 1f * offset / headHeight;
        mHeaderHeight = headHeight;
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        mPercent = 1f * offset / headHeight;
        mHeaderHeight = headHeight;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        isRefreshing = true;
        startAnimation(mAnimation);
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        isRefreshing = false;
        mEndOfRefreshing = true;
        clearAnimation();
        return 0;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        setBackgroundColor(colors[0]);
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

    //<editor-fold desc="draw">
    @Override
    public void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (isRefreshing) {
            // Set up new set of wind
            while (mWinds.size() < WIND_SET_AMOUNT) {
                float y = (float) (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT));
                float x = random(MIN_WIND_X_OFFSET, MAX_WIND_X_OFFSET);

                // Magic with checking interval between winds
                if (mWinds.size() > 1) {
                    y = 0;
                    while (y == 0) {
                        float tmp = (float) (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT));

                        for (Map.Entry<Float, Float> wind : mWinds.entrySet()) {
                            // We want that interval will be greater than fifth part of draggable distance
                            if (Math.abs(wind.getKey() - tmp) > mHeaderHeight / RANDOM_Y_COEFFICIENT) {
                                y = tmp;
                            } else {
                                y = 0;
                                break;
                            }
                        }
                    }
                }

                mWinds.put(y, x);
                drawWind(canvas, y, x, width);
            }

            // Draw current set of wind
            if (mWinds.size() >= WIND_SET_AMOUNT) {
                for (Map.Entry<Float, Float> wind : mWinds.entrySet()) {
                    drawWind(canvas, wind.getKey(), wind.getValue(), width);
                }
            }

            // We should to create new set of winds
            if (mInverseDirection && mNewWindSet) {
                mWinds.clear();
                mNewWindSet = false;
                mWindLineWidth = random(MIN_WIND_LINE_WIDTH, MAX_WIND_LINE_WIDTH);
            }

            // needed for checking direction
            mLastAnimationTime = mLoadingAnimationTime;
        }
        drawAirplane(canvas, width, height);
        drawSideClouds(canvas, width, height);
        drawCenterClouds(canvas, width, height);
    }

    /**
     * Draw wind on loading animation
     *
     * @param canvas  - area where we will draw
     * @param y       - y position fot one of lines
     * @param xOffset - x offset for on of lines
     */
    private void drawWind(Canvas canvas, float y, float xOffset, int width) {
        /* We should multiply current animation time with this coefficient for taking all screen width in time
        Removing slowing of animation with dividing on {@LINK #SLOW_DOWN_ANIMATION_COEFFICIENT}
        And we should don't forget about distance that should "fly" line that depend on screen of device and x offset
        */
        float cof = (width + xOffset) / (LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT);
        float time = mLoadingAnimationTime;

        // HORRIBLE HACK FOR REVERS ANIMATION THAT SHOULD WORK LIKE RESTART ANIMATION
        if (mLastAnimationTime - mLoadingAnimationTime > 0) {
            mInverseDirection = true;
            // take time from 0 to end of animation time
            time = (LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT) - mLoadingAnimationTime;
        } else {
            mNewWindSet = true;
            mInverseDirection = false;
        }

        // Taking current x position of drawing wind
        // For fully disappearing of line we should subtract wind line width
        float x = (width - (time * cof)) + xOffset - mWindLineWidth;
        float xEnd = x + mWindLineWidth;

        canvas.drawLine(x, y, xEnd, y, mWindPaint);
    }

    private void drawSideClouds(Canvas canvas, int width, int height) {
        Matrix matrix = mMatrix;
        matrix.reset();

        mCloudLeft = mCloudCenter;
        mCloudRight = mCloudCenter;

        // Drag percent will newer get more then 1 here
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        if (isInEditMode()) {
            dragPercent = 1;
            mHeaderHeight = height;
        }

        float scale;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            scale = SIDE_CLOUDS_INITIAL_SCALE + (SIDE_CLOUDS_FINAL_SCALE - SIDE_CLOUDS_INITIAL_SCALE) * scalePercent;
        } else {
            scale = SIDE_CLOUDS_INITIAL_SCALE;
        }

        // Current y position of clouds
        float dragYOffset = mHeaderHeight * (1.0f - dragPercent);

        // Position where clouds fully visible on screen and we should drag them with content of listView
//        int cloudsVisiblePosition = mHeaderHeight / 2 - mCloudCenter.height() / 2;

//        boolean needMoveCloudsWithContent = false;
//        if (dragYOffset < cloudsVisiblePosition) {
//            needMoveCloudsWithContent = true;
//        }

        float offsetLeftX = 0 - mCloudLeft.width() / 2;
        float offsetLeftY = (//needMoveCloudsWithContent
                //? mHeaderHeight * dragPercent - mCloudLeft.height() :
                dragYOffset);

        float offsetRightX = width - mCloudRight.width() / 2;
        float offsetRightY = (//needMoveCloudsWithContent
                //? mHeaderHeight * dragPercent - mCloudRight.height() :
                dragYOffset);

        // Magic with animation on loading process
        if (isRefreshing) {
            if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                offsetLeftX -= 2*getAnimationPartValue(AnimationPart.FIRST) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += getAnimationPartValue(AnimationPart.FIRST) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
            } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                offsetLeftX -= 2*getAnimationPartValue(AnimationPart.SECOND) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += getAnimationPartValue(AnimationPart.SECOND) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
            } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                offsetLeftX -= getAnimationPartValue(AnimationPart.THIRD) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += 2*getAnimationPartValue(AnimationPart.THIRD) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
            } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                offsetLeftX -= getAnimationPartValue(AnimationPart.FOURTH) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += 2*getAnimationPartValue(AnimationPart.FOURTH) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
            }
        }

        if (offsetLeftY + scale * mCloudLeft.height() < height + 2) {
            offsetLeftY = height + 2 - scale * mCloudLeft.height();
        }
        if (offsetRightY + scale * mCloudRight.height() < height + 2) {
            offsetRightY = height + 2 - scale * mCloudRight.height();
        }

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(offsetLeftX, offsetLeftY);
        matrix.postScale(scale, scale, mCloudLeft.width() * 3 / 4, mCloudLeft.height());
        canvas.concat(matrix);
        mCloudLeft.setAlpha(100);
        mCloudLeft.draw(canvas);
        mCloudLeft.setAlpha(255);
        canvas.restoreToCount(saveCount);
        canvas.save();
        canvas.translate(offsetRightX, offsetRightY);
        matrix.postScale(scale, scale, 0, mCloudRight.height());
        canvas.concat(matrix);
        mCloudRight.setAlpha(100);
        mCloudRight.draw(canvas);
        mCloudRight.setAlpha(255);
        canvas.restoreToCount(saveCount);
    }

    private void drawCenterClouds(Canvas canvas, int width, int height) {
        Matrix matrix = mMatrix;
        matrix.reset();
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        if (isInEditMode()) {
            dragPercent = 1;
            mHeaderHeight = height;
        }

        float scale;
        float overdragPercent = 0;
        boolean overdrag = false;

        if (mPercent > 1.0f) {
            overdrag = true;
            // Here we want know about how mach percent of over drag we done
            overdragPercent = Math.abs(1.0f - mPercent);
        }

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            scale = CENTER_CLOUDS_INITIAL_SCALE + (CENTER_CLOUDS_FINAL_SCALE - CENTER_CLOUDS_INITIAL_SCALE) * scalePercent;
        } else {
            scale = CENTER_CLOUDS_INITIAL_SCALE;
        }

        float parallaxPercent = 0;
        boolean parallax = false;
        // Current y position of clouds
        float dragYOffset = mHeaderHeight * dragPercent;
        // Position when should start parallax scrolling
        int startParallaxHeight = mHeaderHeight - mCloudCenter.height()/2;

        if (dragYOffset > startParallaxHeight) {
            parallax = true;
            parallaxPercent = dragYOffset - startParallaxHeight;
        }

        float offsetX = (width / 2) - mCloudCenter.width() / 2;
        float offsetY = dragYOffset
                - (parallax ? mCloudCenter.height()/2 + parallaxPercent : mCloudCenter.height()/2);

        float sx = overdrag ? scale + overdragPercent / 4 : scale;
        float sy = overdrag ? scale + overdragPercent / 2 : scale;

        if (isRefreshing && !overdrag) {
            if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                sx = scale - (getAnimationPartValue(AnimationPart.FIRST) / LOADING_ANIMATION_COEFFICIENT) / 8;
            } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                sx = scale - (getAnimationPartValue(AnimationPart.SECOND) / LOADING_ANIMATION_COEFFICIENT) / 8;
            } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                sx = scale + (getAnimationPartValue(AnimationPart.THIRD) / LOADING_ANIMATION_COEFFICIENT) / 6;
            } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                sx = scale + (getAnimationPartValue(AnimationPart.FOURTH) / LOADING_ANIMATION_COEFFICIENT) / 6;
            }
            sy = sx;
        }


        matrix.postScale(sx, sy, mCloudCenter.width() / 2, 0);

        if (offsetY + sy * mCloudCenter.height() < height + 2) {
            offsetY = height + 2 - sy * mCloudCenter.height();
        }

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.concat(matrix);
        mCloudCenter.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void drawAirplane(Canvas canvas, int width, int height) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mPercent;
        float rotateAngle = 0;

        if (isInEditMode()) {
            dragPercent = 1;
            mHeaderHeight = height;
        }

        // Check overdrag
        if (dragPercent > 1.0f /*&& !mEndOfRefreshing*/) {
            rotateAngle = (dragPercent % 1) * 20;
            dragPercent = 1.0f;
        }

        float offsetX = ((width * dragPercent) / 2) - mAirplane.width() / 2;
        float offsetY = mHeaderHeight * (1 - dragPercent/2) - mAirplane.height() / 2;

        if (mEndOfRefreshing) {
            offsetX = width/2 + width * (1-dragPercent) / 2 - mAirplane.width() / 2;
            offsetY = (dragPercent) * (mHeaderHeight / 2 + mAirplane.height() * 3 / 2) - 2 * mAirplane.height();
        }

        if (isRefreshing) {
            if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                offsetY -= getAnimationPartValue(AnimationPart.FIRST);
            } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                offsetY -= getAnimationPartValue(AnimationPart.SECOND);
            } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                offsetY += getAnimationPartValue(AnimationPart.THIRD);
            } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                offsetY += getAnimationPartValue(AnimationPart.FOURTH);
            }
        }

        if (rotateAngle > 0) {
            matrix.postRotate(rotateAngle,
                    mAirplane.width() / 2,
                    mAirplane.height() / 2);
        }

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.concat(matrix);
        mAirplane.draw(canvas);
        canvas.restoreToCount(saveCount);
    }
    //</editor-fold>

//    @Override
//    public int defineHeight() {
//        return (int)(Resources.getSystem().getDisplayMetrics().widthPixels * 0.3);
//    }
//
//    @Override
//    public int defineExtendHeight() {
//        return (int) (defineHeight() * 0.3f);
//    }

    //<editor-fold desc="private">

    private void setupPathDrawable() {
        DensityUtil density = new DensityUtil();

        mAirplane = new PathsDrawable();
        //mAirplane.parserPaths("M60.68,16.15l-0.13,-0.11c-1.09,-0.76 -2.63,-1.16 -4.47,-1.16c-2.92,0 -5.95,0.99 -7.32,1.92l-10.76,7.35l-20.97,4.45c-0.18,0.04 -0.35,0.11 -0.51,0.22c-0.41,0.28 -0.64,0.76 -0.62,1.25c0.04,0.71 0.58,1.27 1.28,1.34l8.87,0.89l-8.65,5.9c-2.57,-1.18 -5.02,-2.33 -7.27,-3.4c-3.48,-1.67 -5.76,-1.96 -6.83,-0.89c-1.11,1.11 -0.39,3.02 0.01,3.6l8.33,10.8c0.28,0.41 0.6,0.64 0.99,0.71c0.64,0.11 1.2,-0.27 1.78,-0.68l2.11,-1.45l11.72,-5.69l-1.71,6.12c-0.19,0.68 0.14,1.38 0.78,1.68c0.18,0.08 0.39,0.13 0.59,0.13c0.29,0 0.57,-0.09 0.81,-0.25c0.16,-0.1 0.28,-0.23 0.38,-0.39l6.7,-10.19l4.1,-4.8L58.08,21.08c0.28,-0.19 0.55,-0.36 0.82,-0.54c0.63,-0.4 1.22,-0.78 1.65,-1.21C61.47,18.41 61.52,17.39 60.68,16.15z");
        mAirplane.parserPaths("m23.01,81.48c-0.21,-0.3 -0.38,-0.83 -0.38,-1.19 0,-0.55 0.24,-0.78 1.5,-1.48 1.78,-0.97 2.62,-1.94 2.24,-2.57 -0.57,-0.93 -1.97,-1.24 -11.64,-2.59 -5.35,-0.74 -10.21,-1.44 -10.82,-1.54l-1.09,-0.18 1.19,-0.91c0.99,-0.76 1.38,-0.91 2.35,-0.91 0.64,0 6.39,0.33 12.79,0.74 6.39,0.41 12.09,0.71 12.65,0.67l1.03,-0.07 -1.24,-2.19C30.18,66.77 15.91,42 15.13,40.68l-0.51,-0.87 4.19,-1.26c2.3,-0.69 4.27,-1.26 4.37,-1.26 0.1,0 5.95,3.85 13,8.55 14.69,9.81 17.1,11.31 19.7,12.31 4.63,1.78 6.45,1.69 12.94,-0.64 13.18,-4.73 25.22,-9.13 25.75,-9.4 0.69,-0.36 3.6,1.33 -24.38,-14.22L50.73,23.07 46.74,16.42 42.75,9.77 43.63,8.89c0.83,-0.83 0.91,-0.86 1.46,-0.52 0.32,0.2 3.72,3.09 7.55,6.44 3.83,3.34 7.21,6.16 7.5,6.27 0.29,0.11 13.6,2.82 29.58,6.03 15.98,3.21 31.86,6.4 35.3,7.1l6.26,1.26 3.22,-1.13c41.63,-14.63 67.88,-23.23 85.38,-28 14.83,-4.04 23.75,-4.75 32.07,-2.57 7.04,1.84 9.87,4.88 7.71,8.27 -1.6,2.5 -4.6,4.63 -10.61,7.54 -5.94,2.88 -10.22,4.46 -25.4,9.41 -8.15,2.66 -16.66,5.72 -39.01,14.02 -66.79,24.82 -88.49,31.25 -121.66,36.07 -14.56,2.11 -24.17,2.95 -34.08,2.95 -5.43,0 -5.52,-0.01 -5.89,-0.54z");
        mAirplane.setBounds(0, 0, density.dip2px(65), density.dip2px(20));
        mAirplane.parserColors(0xffffffff);

        mCloudCenter = new PathsDrawable();
        mCloudCenter.parserPaths(
                "M551.81,1.01A65.42,65.42 0,0 0,504.38 21.5A50.65,50.65 0,0 0,492.4 20A50.65,50.65 0,0 0,441.75 70.65A50.65,50.65 0,0 0,492.4 121.3A50.65,50.65 0,0 0,511.22 117.64A65.42,65.42 0,0 0,517.45 122L586.25,122A65.42,65.42 0,0 0,599.79 110.78A59.79,59.79 0,0 0,607.81 122L696.34,122A59.79,59.79 0,0 0,711.87 81.9A59.79,59.79 0,0 0,652.07 22.11A59.79,59.79 0,0 0,610.93 38.57A65.42,65.42 0,0 0,551.81 1.01zM246.2,1.71A54.87,54.87 0,0 0,195.14 36.64A46.78,46.78 0,0 0,167.77 27.74A46.78,46.78 0,0 0,120.99 74.52A46.78,46.78 0,0 0,167.77 121.3A46.78,46.78 0,0 0,208.92 96.74A54.87,54.87 0,0 0,246.2 111.45A54.87,54.87 0,0 0,268.71 106.54A39.04,39.04 0,0 0,281.09 122L327.6,122A39.04,39.04 0,0 0,343.38 90.7A39.04,39.04 0,0 0,304.34 51.66A39.04,39.04 0,0 0,300.82 51.85A54.87,54.87 0,0 0,246.2 1.71z",
                "m506.71,31.37a53.11,53.11 0,0 0,-53.11 53.11,53.11 53.11,0 0,0 15.55,37.5h75.12a53.11,53.11 0,0 0,1.88 -2.01,28.49 28.49,0 0,0 0.81,2.01h212.96a96.72,96.72 0,0 0,-87.09 -54.85,96.72 96.72,0 0,0 -73.14,33.52 28.49,28.49 0,0 0,-26.74 -18.74,28.49 28.49,0 0,0 -13.16,3.23 53.11,53.11 0,0 0,0.03 -0.66,53.11 53.11,0 0,0 -53.11,-53.11zM206.23,31.81a53.81,53.81 0,0 0,-49.99 34.03,74.91 74.91,0 0,0 -47.45,-17 74.91,74.91 0,0 0,-73.54 60.82,31.3 31.3,0 0,0 -10.17,-1.73 31.3,31.3 0,0 0,-26.09 14.05L300.86,121.98a37.63,37.63 0,0 0,0.2 -3.85,37.63 37.63,0 0,0 -37.63,-37.63 37.63,37.63 0,0 0,-3.65 0.21,53.81 53.81,0 0,0 -53.54,-48.9z",
                "m424.05,36.88a53.46,53.46 0,0 0,-40.89 19.02,53.46 53.46,0 0,0 -1.34,1.76 62.6,62.6 0,0 0,-5.39 -0.27,62.6 62.6,0 0,0 -61.36,50.17 62.6,62.6 0,0 0,-0.53 3.51,15.83 15.83,0 0,0 -10.33,-3.84 15.83,15.83 0,0 0,-8.06 2.23,21.1 21.1,0 0,0 -18.31,-10.67 21.1,21.1 0,0 0,-19.47 12.97,21.81 21.81,0 0,0 -6.56,-1.01 21.81,21.81 0,0 0,-19.09 11.32L522.84,122.07a43.61,43.61 0,0 0,-43.11 -37.35,43.61 43.61,0 0,0 -2.57,0.09 53.46,53.46 0,0 0,-53.11 -47.93zM129.08,38.4a50.29,50.29 0,0 0,-50.29 50.29,50.29 50.29,0 0,0 2.37,15.06 15.48,15.83 0,0 0,-5.87 1.68,15.48 15.83,0 0,0 -0.98,0.58 16.53,16.18 0,0 0,-0.19 -0.21,16.53 16.18,0 0,0 -11.86,-4.91 16.53,16.18 0,0 0,-16.38 14.13,20.05 16.18,0 0,0 -14.97,7.04L223.95,122.07a42.56,42.56 0,0 0,1.14 -9.56,42.56 42.56,0 0,0 -42.56,-42.56 42.56,42.56 0,0 0,-6.58 0.54,50.29 50.29,0 0,0 -0,-0.01 50.29,50.29 0,0 0,-46.88 -32.07zM631.67,82.61a64.01,64.01 0,0 0,-44.9 18.42,26.73 26.73,0 0,0 -10.67,-2.24 26.73,26.73 0,0 0,-22.72 12.71,16.88 16.88,0 0,0 -0.25,-0.12 16.88,16.88 0,0 0,-6.57 -1.33,16.88 16.88,0 0,0 -16.15,12.03h160.36a64.01,64.01 0,0 0,-59.1 -39.46z"
        );
//        mCloudCenter.parserColors(0xfffdfdfd,0xffe8f3fd,0xffc7dcf1);
        mCloudCenter.parserColors(0xaac7dcf1,0xdde8f3fd,0xfffdfdfd);
        mCloudCenter.setBounds(0, 0, density.dip2px(260), density.dip2px(45));

    }

    private float random(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return mRandom.nextInt((max - min) + 1) + min;
    }

    /**
     * We need a special value for different part of animation
     *
     * @param part - needed part
     * @return - value for needed part
     */
    private float getAnimationPartValue(AnimationPart part) {
        switch (part) {
            case FIRST: {
                return mLoadingAnimationTime;
            }
            case SECOND: {
                return getAnimationTimePart(AnimationPart.FOURTH) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH));
            }
            case THIRD: {
                return mLoadingAnimationTime - getAnimationTimePart(AnimationPart.SECOND);
            }
            case FOURTH: {
                return getAnimationTimePart(AnimationPart.THIRD) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH));
            }
            default:
                return 0;
        }
    }

    /**
     * On drawing we should check current part of animation
     *
     * @param part - needed part of animation
     * @return - return true if current part
     */
    private boolean checkCurrentAnimationPart(AnimationPart part) {
        switch (part) {
            case FIRST: {
                return mLoadingAnimationTime < getAnimationTimePart(AnimationPart.FOURTH);
            }
            case SECOND:
            case THIRD: {
                return mLoadingAnimationTime < getAnimationTimePart(part);
            }
            case FOURTH: {
                return mLoadingAnimationTime > getAnimationTimePart(AnimationPart.THIRD);
            }
            default:
                return false;
        }
    }

    /**
     * Get part of animation duration
     *
     * @param part - needed part of time
     * @return - interval of time
     */
    private int getAnimationTimePart(AnimationPart part) {
        switch (part) {
            case SECOND: {
                return LOADING_ANIMATION_COEFFICIENT / 2;
            }
            case THIRD: {
                return getAnimationTimePart(AnimationPart.FOURTH) * 3;
            }
            case FOURTH: {
                return LOADING_ANIMATION_COEFFICIENT / 4;
            }
            default:
                return 0;
        }
    }

    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
                setLoadingAnimationTime(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    }

    private void setLoadingAnimationTime(float loadingAnimationTime) {
        /**SLOW DOWN ANIMATION IN {@link #SLOW_DOWN_ANIMATION_COEFFICIENT} time */
        mLoadingAnimationTime = LOADING_ANIMATION_COEFFICIENT * (loadingAnimationTime / SLOW_DOWN_ANIMATION_COEFFICIENT);
        invalidate();
    }
    //</editor-fold>
}
